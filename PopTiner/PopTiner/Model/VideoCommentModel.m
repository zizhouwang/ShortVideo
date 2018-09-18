//
//  VideoCommentModel.m
//  PopTiner
//
//  Created by Mac on 2018/9/14.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "VideoCommentModel.h"

@implementation VideoCommentModel

- (id)init {
    self = [super init];
    if (self) {
        _imageName = [NSString stringWithFormat:@"annoymous_header_big%i.png", arc4random() % 3 + 1];
    }
    return self;
}

@end
