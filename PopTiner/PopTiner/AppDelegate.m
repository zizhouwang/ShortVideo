//
//  AppDelegate.m
//  PopTiner
//
//  Created by zizhou wang on 2018/9/3.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "AppDelegate.h"
#import "Util.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    NSDictionary *savedVideoDic = [[NSUserDefaults standardUserDefaults] objectForKey:@"savedVideoDic"];
    if (savedVideoDic == nil) {
        [Util shareInstance].savedVideoDic = [NSMutableDictionary dictionary];
        [Util saveVideoInfo];
    } else {
        [Util shareInstance].savedVideoDic = [NSMutableDictionary dictionaryWithDictionary:savedVideoDic];
    }
    NSInteger userId = [[NSUserDefaults standardUserDefaults] integerForKey:@"userId"];
    if (userId == 0) {
        userId = arc4random() % 3294967296 + 99999999;
        [[NSUserDefaults standardUserDefaults] setInteger:userId forKey:@"userId"];
    }
    [Util shareInstance].userId = userId;
    return YES;
}


- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
}


- (void)applicationDidEnterBackground:(UIApplication *)application {
//    [[Util shareInstance].currentVideoPlayer.avPlayer pause];
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
//    [[Util shareInstance].currentVideoPlayer.avPlayer play];
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}


@end
