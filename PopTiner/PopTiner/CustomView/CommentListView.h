//
//  CommentListView.h
//  PopTiner
//
//  Created by Mac on 2018/9/13.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "VideoCommentModel.h"

@class CommentListView;

@protocol CommentListProtocol<NSObject>

- (void)sendComment:(NSString*)commentStr commentListView:(CommentListView*)commentListView;
- (void)removeSelf;

@end

@interface CommentListView : UIView

@property (nonatomic, strong) NSArray<VideoCommentModel*> * videoComments;
@property (nonatomic, weak) id<CommentListProtocol> commentDelegate;
@property (nonatomic, strong) UITableView * commentTableView;

- (id)initWithFrame:(CGRect)frame videoCommentModels:(NSArray<VideoCommentModel*>*)videoCommentModels;

@end
