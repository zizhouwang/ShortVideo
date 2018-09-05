//
//  HomeVC.h
//  PopTiner
//
//  Created by zizhou wang on 2018/9/5.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "BaseViewController.h"

@interface HomeVC : BaseViewController

@property (nonatomic, assign) NSInteger index;
@property (nonatomic, strong) NSString * urlStr;

- (void)startInitVideo:(NSString*)urlStr;

@end
