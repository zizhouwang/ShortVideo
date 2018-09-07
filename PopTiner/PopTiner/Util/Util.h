//
//  Util.h
//  PopTiner
//
//  Created by zizhou wang on 2018/9/7.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZZVideoPlayer.h"

@interface Util : NSObject

@property (nonatomic, strong) ZZVideoPlayer * currentVideoPlayer;

+ (Util *)shareInstance;
+ (long long)fileSizeAtPath:(NSString*)filePath;

@end
