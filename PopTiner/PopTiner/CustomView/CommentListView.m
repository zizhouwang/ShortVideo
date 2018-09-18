//
//  CommentListView.m
//  PopTiner
//
//  Created by Mac on 2018/9/13.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "CommentListView.h"
#import "UITextView+ZWPlaceHolder.h"
#import "BaseTextView.h"
#import "CommentCell.h"
#import "Util.h"

@interface CommentListView () <UITableViewDelegate, UITableViewDataSource, BaseTextViewProtocol>

@property (nonatomic, assign) Boolean isEditing;

@property (nonatomic, strong) UIView * commentBackgroundView;
@property (nonatomic, strong) BaseTextView * commentTextView;

@end

@implementation CommentListView

- (id)initWithFrame:(CGRect)frame videoCommentModels:(NSArray<VideoCommentModel*>*)videoCommentModels {
    self = [super initWithFrame:frame];
    if (self) {
        _videoComments = videoCommentModels;
        UITapGestureRecognizer *tapGesturRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(backgroundClicked)];
        [self addGestureRecognizer:tapGesturRecognizer];
        CGFloat commentTextViewHeight = 60.0f;
        [self setBackgroundColor:[UIColor clearColor]];
        _commentBackgroundView = [[UIView alloc] initWithFrame:CGRectMake(0.0f, frame.size.height / 3.0f, frame.size.width, frame.size.height / 3.0f * 2.0f)];
        _commentBackgroundView.layer.cornerRadius = 10.0f;
        [_commentBackgroundView setBackgroundColor:[UIColor colorWithWhite:1.0f alpha:0.97f]];
        [self addSubview:_commentBackgroundView];
        _commentTableView = [[UITableView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, frame.size.width, frame.size.height / 3.0f * 2.0f - commentTextViewHeight)];
        _commentTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        [_commentTableView setBackgroundColor:[UIColor clearColor]];
        _commentTableView.delegate = self;
        _commentTableView.dataSource = self;
        [_commentBackgroundView addSubview:_commentTableView];
        _commentTextView = [[BaseTextView alloc] initWithFrame:CGRectMake(0.0f, frame.size.height - commentTextViewHeight, frame.size.width, commentTextViewHeight)];
        _commentTextView.originY = frame.size.height - commentTextViewHeight;
        _commentTextView.layer.masksToBounds = true;
        _commentTextView.baseTVDelegate = self;
        _commentTextView.zw_placeHolder = @"说点什么吧";
        [self addSubview:_commentTextView];
        UIView * textViewTopLine = [[UIView alloc] initWithFrame:CGRectMake(0.0f, -1.0f, frame.size.width, 1.0f)];
        [textViewTopLine setBackgroundColor:[UIColor grayColor]];
        [_commentTextView addSubview:textViewTopLine];
    }
    return self;
}

- (void)backgroundClicked {
    if (_isEditing == true) {
        _isEditing = false;
        [self endEditing:true];
    } else {
        [self removeFromSuperview];
        if (_commentDelegate != nil) {
            if ([_commentDelegate respondsToSelector:@selector(removeSelf)]) {
                [_commentDelegate removeSelf];
            }
        }
    }
}

#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _videoComments.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UIViewController * customCells = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"CustomCells"];
    UITableView * cells = [customCells.view viewWithTag:100];
    VideoCommentModel * videoCommentModel = _videoComments[indexPath.row];
    NSString *reuseIdentifier = NSStringFromClass([CommentCell class]);
    CommentCell *cell = [cells dequeueReusableCellWithIdentifier:reuseIdentifier];
    if (cell == nil) {
        cell = [[CommentCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:reuseIdentifier];
//        cell = [[[NSBundle mainBundle] loadNibNamed:reuseIdentifier owner:nil options:nil] firstObject];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    [cell setBackgroundColor:[UIColor clearColor]];
    [cell.contentView setBackgroundColor:[UIColor clearColor]];
    [cell.nicknameLabel setText:videoCommentModel.nickname];
    [cell.commentLabel setText:videoCommentModel.commentContent];
    cell.headerButton.imageView.layer.cornerRadius = 5.0f;
    [cell.headerButton setImage:[UIImage imageNamed:videoCommentModel.imageName] forState:UIControlStateNormal];
    return cell;
}

#pragma mark - UITableViewDelegate
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    VideoCommentModel * videoCommentModel = _videoComments[indexPath.row];
    UILabel * textView = [[UILabel alloc] init];
    [textView setText:videoCommentModel.commentContent];
    textView.numberOfLines = 0;
    CGSize expectSize = [textView sizeThatFits:CGSizeMake([Util screenWidth] - 62.5f - 8.0f - 8.0f - 8.0f, 1000.0f)];
    return expectSize.height + 21.0f + 8.0f + 8.0f + 8.0f;
}

#pragma mark - BaseTextViewProtocol
- (void)zzKeyboardShow:(NSNotification *)note {
    _isEditing = true;
}

- (void)sendButtonClicked {
    if (_commentDelegate != nil) {
        if ([_commentDelegate respondsToSelector:@selector(sendComment:commentListView:)]) {
            [_commentDelegate sendComment:_commentTextView.text commentListView:self];
        }
    }
    [_commentTextView setText:@""];
    [self endEditing:true];
    _isEditing = false;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
