//
//  ZZVideoResourceLoader.h
//  PopTiner
//
//  Created by zizhou wang on 2018/9/6.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>

@interface ZZVideoResourceLoader : NSObject<AVAssetResourceLoaderDelegate>

@property (nonatomic, assign) NSInteger index;

@property (nonatomic, strong) NSString * tag;
@property (nonatomic, strong) NSString * urlStr;
@property (nonatomic, strong) NSString * originURLStr;

- (id)initWithURLStr:(NSString*)urlStr originURLStr:(NSString*)originURLStr;

@end
