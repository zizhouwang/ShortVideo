//
//  Util.m
//  PopTiner
//
//  Created by zizhou wang on 2018/9/7.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "Util.h"
#import "MD5Encrypt.h"

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

+ (BOOL)jp_safeWriteData:(NSFileHandle*)fileHandle data:(NSData *)data {
    NSInteger retry = 3;
    size_t bytesLeft = data.length;
    const void *bytes = [data bytes];
    int fileDescriptor = [fileHandle fileDescriptor];
    while (bytesLeft > 0 && retry > 0) {
        ssize_t amountSent = write(fileDescriptor, bytes + data.length - bytesLeft, bytesLeft);
        if (amountSent < 0) {
            // write failed.
            NSLog(@"%@", @"Write file failed");
            break;
        }
        else {
            bytesLeft = bytesLeft - amountSent;
            if (bytesLeft > 0) {
                // not finished continue write after sleep 1 second.
                NSLog(@"%@", @"Write file retry");
                sleep(1);  //probably too long, but this is quite rare.
                retry--;
            }
        }
    }
    return bytesLeft == 0;
}

+ (void)saveVideoInfo {
    [[NSUserDefaults standardUserDefaults] setObject:[Util shareInstance].savedVideoDic forKey:@"savedVideoDic"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

+ (NSString*)generateLocalVideoPath:(NSString *)originURL {
    NSString * videoURLMd5 = [MD5Encrypt MD5ForLower32Bate:originURL];
    return [NSHomeDirectory() stringByAppendingFormat:@"/Documents/%@.mp4", videoURLMd5];
}

+ (BOOL)isSavedVideoURL:(NSString *)originURLStr {
    NSDictionary * videoInfo = [[Util shareInstance].savedVideoDic objectForKey:originURLStr];
    if (videoInfo != nil) {
        return true;
    } else {
        return false;
    }
}

+ (NSString*)numberToKStr:(NSInteger)number {
    CGFloat numberFloat = number;
    if (numberFloat > 10000) {
        numberFloat = numberFloat / 1000.0f;
        return [NSString stringWithFormat:@"%.1fw", numberFloat];
    } else {
        return [NSString stringWithFormat:@"%.0f", numberFloat];
    }
}

+ (CGFloat)screenWidth {
    return [[UIScreen mainScreen] bounds].size.width;
}

+ (CGFloat)screenHeight {
    return [[UIScreen mainScreen] bounds].size.height;
}

+ (UIWindow*)mainWindow {
    UIApplication *app = [UIApplication sharedApplication];
    if ([app.delegate respondsToSelector:@selector(window)]) {
        return [app.delegate window];
    } else {
        return [app keyWindow];
    }
}

@end
