//
//  Util.h
//  PopTiner
//
//  Created by zizhou wang on 2018/9/7.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZZVideoPlayer.h"
#import "BasePageViewController.h"
#import "BaseTabbarController.h"

@interface Util : NSObject

@property (nonatomic, strong) ZZVideoPlayer * currentVideoPlayer;
@property (nonatomic, strong) NSMutableDictionary * savedVideoDic;
@property (nonatomic, strong) BasePageViewController * homePageViewController;
@property (nonatomic, strong) BaseTabbarController * homeTabbarController;
@property (nonatomic, assign) NSInteger userId;

+ (Util *)shareInstance;
+ (long long)fileSizeAtPath:(NSString*)filePath;
+ (BOOL)jp_safeWriteData:(NSFileHandle*)fileHandle data:(NSData *)data;
+ (void)saveVideoInfo;
+ (NSString*)generateLocalVideoPath:(NSString*)originURL;
+ (BOOL)isSavedVideoURL:(NSString*)originURLStr;
+ (NSString*)numberToKStr:(NSInteger)number;

+ (CGFloat)screenWidth;
+ (CGFloat)screenHeight;
+ (UIWindow*)mainWindow;

@end
