//
//  BasePageViewController.m
//  PopTiner
//
//  Created by zizhou wang on 2018/9/5.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "BasePageViewController.h"
#import "HomeVC.h"
#import <AFNetworking.h>
#import "VideoModel.h"
#import <YYModel.h>

@interface BasePageViewController () <UIPageViewControllerDataSource, UIPageViewControllerDelegate>

@property (nonatomic, strong) NSMutableArray<VideoModel*> * videoModels;

@end

@implementation BasePageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.dataSource = self;
    self.delegate = self;
    
    _videoModels = [NSMutableArray array];
    
    HomeVC * homeVC = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HomeVC"];
    homeVC.index = 0;
    __weak typeof(self) weakSelf = self;
    [self setViewControllers:@[homeVC] direction:UIPageViewControllerNavigationDirectionReverse animated:YES completion:^(BOOL finished) {
        AFHTTPSessionManager * manager =[AFHTTPSessionManager manager];
        manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json", @"text/json", @"text/javascript",@"text/html", nil];
        [manager GET:@"http://172.96.240.118/index.php?r=site/index&channel=12" parameters:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            NSDictionary * responseDic = responseObject;
            NSArray * results = responseDic[@"result"];
            for (NSDictionary * videoDic in results) {
                VideoModel * videoModel = [VideoModel yy_modelWithDictionary:videoDic];
                [weakSelf.videoModels addObject:videoModel];
            }
            [homeVC startInitVideo:weakSelf.videoModels.firstObject.video_cdn_url];
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            NSLog(@"%@", error);
        }];
    }];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.hidden = YES;
    self.tabBarController.tabBar.hidden = YES;
}

- (BOOL)prefersStatusBarHidden {
    return YES;
}

#pragma mark - UIPageViewControllerDataSource
- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)viewController {
    HomeVC * beforeHomeVC = (HomeVC*)viewController;
    NSInteger currentIndex = beforeHomeVC.index;
    currentIndex--;
    if (currentIndex < 0) {
        return nil;
    }
    HomeVC * homeVC = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HomeVC"];
    homeVC.index = currentIndex;
    VideoModel * videoModel = _videoModels[currentIndex];
    [homeVC startInitVideo:videoModel.video_cdn_url];
    return homeVC;
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)viewController {
    HomeVC * beforeHomeVC = (HomeVC*)viewController;
    NSInteger currentIndex = beforeHomeVC.index;
    currentIndex++;
    if (currentIndex >= _videoModels.count) {
        return nil;
    }
    HomeVC * homeVC = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HomeVC"];
    homeVC.index = currentIndex;
    VideoModel * videoModel = _videoModels[currentIndex];
    [homeVC startInitVideo:videoModel.video_cdn_url];
    return homeVC;
}

#pragma mark - UIPageViewControllerDelegate
- (void)pageViewController:(UIPageViewController *)pageViewController didFinishAnimating:(BOOL)finished previousViewControllers:(NSArray<UIViewController *> *)previousViewControllers transitionCompleted:(BOOL)completed {
    
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
