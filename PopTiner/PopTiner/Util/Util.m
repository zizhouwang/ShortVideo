//
//  Util.m
//  PopTiner
//
//  Created by zizhou wang on 2018/9/7.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "Util.h"

@implementation Util

+ (Util *)shareInstance {
    static Util * util = nil ;
    if (util == nil) {
        util = [[Util alloc] init];
    }
    return (Util *)util;
}

+ (long long)fileSizeAtPath:(NSString*)filePath {
    NSFileManager* manager = [NSFileManager defaultManager];
    if ([manager fileExistsAtPath:filePath]){
        return [[manager attributesOfItemAtPath:filePath error:nil] fileSize];
    }
    return 0;
}

+ (void)saveVideoToAlbum:(NSString*)filePath {
    UISaveVideoAtPathToSavedPhotosAlbum(filePath, self, nil, nil);
}

@end
