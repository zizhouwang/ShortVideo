//
//  PersonVC.m
//  PopTiner
//
//  Created by Mac on 2018/9/14.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "PersonVC.h"
#import "SaveVideoAlertView.h"

@interface PersonVC ()
@property (weak, nonatomic) IBOutlet UIButton *headerButton;

@end

@implementation PersonVC

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _headerButton.imageView.layer.cornerRadius = 40.0f;
}

- (IBAction)clearCacheButtonClicked:(UIButton *)sender {
    SaveVideoAlertView * saveVideoAlertView = [[SaveVideoAlertView alloc] initWithFrame:self.view.frame];
    [self.view addSubview:saveVideoAlertView];
    [saveVideoAlertView startAnimation];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString * documentDir = [NSHomeDirectory() stringByAppendingFormat:@"/Documents/"];
    NSArray * fileList = [fileManager contentsOfDirectoryAtPath:documentDir error:nil];
    BOOL isDir = false;
    for (NSString *file in fileList) {
        NSString *path = [documentDir stringByAppendingPathComponent:file];
        [fileManager fileExistsAtPath:path isDirectory:(&isDir)];
        if (isDir == false) {
            [fileManager removeItemAtPath:path error:nil];
        }
        isDir = false;
    }
    [saveVideoAlertView showAlertLabel:@"清理成功"];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [UIApplication sharedApplication].statusBarStyle = UIStatusBarStyleLightContent;
    [[UINavigationBar appearance] setBarTintColor:[UIColor orangeColor]];
    [[UINavigationBar appearance] setTintColor:[UIColor orangeColor]];
}

- (UIStatusBarStyle)preferredStatusBarStyle {
    return UIStatusBarStyleLightContent;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
