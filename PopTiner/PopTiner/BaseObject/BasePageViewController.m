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
#import "UIImage+Rotate.h"

@interface BasePageViewController () <UIPageViewControllerDataSource, UIPageViewControllerDelegate, UIScrollViewDelegate>

@property (nonatomic, assign) Boolean isLoading;

@property (nonatomic, strong) NSMutableArray<VideoModel*> * videoModels;
@property (nonatomic, strong) UIButton * refreshButton;
@property (nonatomic, strong) NSMutableArray * itemButtons;

@property (nonatomic, strong) CABasicAnimation * animation;

@end

@implementation BasePageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    _itemButtons = [NSMutableArray array];
    for (UITabBarItem * tabbarItem in self.tabBarController.tabBar.items) {
        [tabbarItem setTitle:nil];
        [tabbarItem setImage:nil];
        [tabbarItem setSelectedImage:nil];
    }
    NSLog(@"%f", self.tabBarController.tabBar.frame.size.height);
    NSInteger itemButtonCount = 2;
    NSArray * itemTitles = @[@"首页", @"我"];
    for (NSInteger i = 0; i < itemButtonCount; i++) {
        UIButton * itemButton = [[UIButton alloc] initWithFrame:CGRectMake([Util screenWidth] / (itemButtonCount * 1.0f) * (i * 1.0f), 0.0f, [Util screenWidth] / (itemButtonCount * 1.0f), self.tabBarController.tabBar.frame.size.height)];
        itemButton.tag = i;
        [itemButton setTitle:itemTitles[i] forState:UIControlStateNormal];
        [itemButton setTitle:itemTitles[i] forState:UIControlStateSelected];
        [itemButton setTitleColor:[UIColor colorWithWhite:0.7f alpha:1.0f] forState:UIControlStateNormal];
        [itemButton setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
        [itemButton addTarget:self action:@selector(itemButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        [itemButton setBackgroundColor:[UIColor clearColor]];
        [self.tabBarController.tabBar addSubview:itemButton];
        [_itemButtons addObject:itemButton];
        if (i == 0) {
            itemButton.selected = true;
        }
    }
    _videoCommentDic = [NSMutableDictionary dictionary];
    _animation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    _animation.fromValue = [NSNumber numberWithFloat:0.f];
    _animation.toValue = [NSNumber numberWithFloat: M_PI *2];
    _animation.duration = 3;
    _animation.autoreverses = NO;
    _animation.fillMode = kCAFillModeForwards;
    _animation.repeatCount = MAXFLOAT; //如果这里想设置成一直自旋转，可以设置为MAXFLOAT，否则设置具体的数值则代表执行多少次
    [Util shareInstance].homePageViewController = self;
    _currentIndex = 0;
    _scrollView = [self findScrollView];
    _scrollView.delegate = self;
    
    self.dataSource = self;
    self.delegate = self;
    
    _videoModels = [NSMutableArray array];
    
    CGFloat refreshButtonSize = 50.0f;
    _refreshButton = [[UIButton alloc] initWithFrame:CGRectMake([Util screenWidth] - refreshButtonSize - 10.0f, 20.0f, refreshButtonSize, refreshButtonSize)];
    [_refreshButton setImage:[UIImage imageNamed:@"refresh.png"] forState:UIControlStateNormal];
    [_refreshButton setAlpha:0.0f];
    [self.view addSubview:_refreshButton];
    
    [self refreshButtonStartLoad:false];
}

- (void)itemButtonClicked:(UIButton*)button {
    self.tabBarController.selectedIndex = button.tag;
    for (UIButton * itemButton in _itemButtons) {
        itemButton.selected = false;
    }
    button.selected = true;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.hidden = YES;
    //    self.tabBarController.tabBar.hidden = YES;
    self.tabBarController.tabBar.backgroundImage = [UIImage imageNamed:@"clear.png"];
    self.tabBarController.tabBar.shadowImage = [[UIImage alloc] init];
}

- (void)loadDataWithMore:(BOOL)isMore {
    __weak typeof(self) weakSelf = self;
    if (isMore == true) {
        AFHTTPSessionManager * manager =[AFHTTPSessionManager manager];
        manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json", @"text/json", @"text/javascript",@"text/html", nil];
        [manager GET:@"http://172.96.240.118/index.php?r=site/index&channel=12" parameters:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            NSDictionary * responseDic = responseObject;
            NSArray * results = responseDic[@"result"];
            for (NSDictionary * videoDic in results) {
                VideoModel * videoModel = [VideoModel yy_modelWithDictionary:videoDic];
                [weakSelf.videoModels addObject:videoModel];
            }
            [self refreshButtonStopAnimation];
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            NSLog(@"%@", error);
        }];
    } else {
        HomeVC * homeVC = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HomeVC"];
        homeVC.index = 0;
        AFHTTPSessionManager * manager =[AFHTTPSessionManager manager];
        manager.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json", @"text/json", @"text/javascript",@"text/html", nil];
        [manager GET:@"http://172.96.240.118/index.php?r=site/index&channel=12" parameters:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            NSDictionary * responseDic = responseObject;
            NSArray * results = responseDic[@"result"];
            weakSelf.videoModels = [NSMutableArray array];
            for (NSDictionary * videoDic in results) {
                VideoModel * videoModel = [VideoModel yy_modelWithDictionary:videoDic];
                [weakSelf.videoModels addObject:videoModel];
            }
            [homeVC setVideoModel:weakSelf.videoModels.firstObject];
            [homeVC loadDataWithModel];
            [homeVC setVideoInfo:weakSelf.videoModels.firstObject.video_cdn_url];
            [self setViewControllers:@[homeVC] direction:UIPageViewControllerNavigationDirectionReverse animated:false completion:^(BOOL finished) {
            }];
            [self refreshButtonStopAnimation];
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            NSLog(@"%@", error);
        }];
    }
}

- (void)refreshButtonStartLoad:(Boolean)isMore {
    [_refreshButton.layer addAnimation:_animation forKey:nil];
    _refreshButton.alpha = 1.0f;
    _isLoading = true;
    [self loadDataWithMore:isMore];
}

- (void)refreshButtonStopAnimation {
    [_refreshButton.layer removeAllAnimations];
    _refreshButton.alpha = 0.0f;
    _isLoading = false;
}

- (BOOL)prefersStatusBarHidden {
    return true;
}

- (UIScrollView*)findScrollView {
    UIScrollView* scrollView;
    for(id subview in self.view.subviews){
        if([subview isKindOfClass:UIScrollView.class]){
            scrollView = subview;
            break;
        }
    }
    return scrollView;
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
    [homeVC setVideoModel:videoModel];
    [homeVC setVideoInfo:videoModel.video_cdn_url];
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
    [homeVC setVideoModel:videoModel];
    [homeVC setVideoInfo:videoModel.video_cdn_url];
    return homeVC;
}

#pragma mark - UIPageViewControllerDelegate
- (void)pageViewController:(UIPageViewController *)pageViewController willTransitionToViewControllers:(NSArray<UIViewController *> *)pendingViewControllers {
    HomeVC * homeVC = (HomeVC*)pendingViewControllers[0];
    if (homeVC.index > _videoModels.count - 9 && _isLoading == false) {
        [self refreshButtonStartLoad:true];
    }
}

- (void)pageViewController:(UIPageViewController *)pageViewController didFinishAnimating:(BOOL)finished previousViewControllers:(NSArray<UIViewController *> *)previousViewControllers transitionCompleted:(BOOL)completed {
    
}

#pragma mark - UIScrollViewDelegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    if (_currentIndex == 0 && _isLoading == false) {
        CGFloat alpha = ([Util screenHeight] - scrollView.contentOffset.y) / 50.0f;
        if (alpha < 0.0f) {
            alpha = 0.0f;
        }
        if (alpha > 1.0f) {
            alpha = 1.0f;
        }
        CGFloat angle = alpha * 360.0f;
        [_refreshButton setAlpha:alpha];
        UIImage * refreshImage = [UIImage imageNamed:@"refresh.png"];
        [_refreshButton setImage:[refreshImage imageRotatedByDegrees:angle] forState:UIControlStateNormal];
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    CGFloat scrollDistance = [Util screenHeight] - scrollView.contentOffset.y;
    if (_currentIndex == 0 && scrollDistance > 50.0f) {
        [self refreshButtonStartLoad:false];
    }
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
