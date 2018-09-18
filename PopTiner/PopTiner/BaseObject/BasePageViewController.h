//
//  BasePageViewController.h
//  PopTiner
//
//  Created by zizhou wang on 2018/9/5.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "VideoCommentModel.h"

@interface BasePageViewController : UIPageViewController

@property (nonatomic, assign) NSInteger currentIndex;
@property (nonatomic, strong) UIScrollView * scrollView;
@property (nonatomic, strong) NSMutableDictionary<NSNumber*, NSArray<VideoCommentModel*>*> * videoCommentDic;

@end
