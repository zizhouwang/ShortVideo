//
//  BaseTextView.m
//  PopTiner
//
//  Created by Mac on 2018/9/13.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "BaseTextView.h"
#import "Util.h"

@interface BaseTextView () <UITextViewDelegate>

@property (nonatomic, assign) CGFloat deltaY;

@end

@implementation BaseTextView

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setReturnKeyType:UIReturnKeySend];
        [self setContentInset:UIEdgeInsetsMake(0.0f, 5.0f, 0.0f, 5.0f)];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardShow:) name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardHide:) name:UIKeyboardWillHideNotification object:nil];
        self.delegate = self;
    }
    return self;
}

#pragma mark - keyboardNotification
- (void)keyboardShow:(NSNotification *)note {
    CGRect keyBoardRect = [note.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGFloat deltaY = keyBoardRect.size.height;
    _deltaY = deltaY;
    [UIView animateWithDuration:[note.userInfo[UIKeyboardAnimationDurationUserInfoKey] floatValue] animations:^{
        [self setFrame:CGRectMake(self.frame.origin.x, self->_originY - deltaY, self.frame.size.width, self.frame.size.height)];
    }];
    if (_baseTVDelegate != nil) {
        if ([_baseTVDelegate respondsToSelector:@selector(zzKeyboardShow:)]) {
            [_baseTVDelegate zzKeyboardShow:note];
        }
    }
}

- (void)keyboardHide:(NSNotification *)note {
    [UIView animateWithDuration:[note.userInfo[UIKeyboardAnimationDurationUserInfoKey] floatValue] animations:^{
        [self setFrame:CGRectMake(self.frame.origin.x, self->_originY, self.frame.size.width, self.frame.size.height)];
    } completion:^(BOOL finished) {
        
    }];
}

#pragma mark - UITextViewDelegate
- (void)textViewDidChange:(UITextView *)textView {
    CGSize expectSize = [textView sizeThatFits:CGSizeMake(self.frame.size.width, 100.0f)];
    _originY = [Util screenHeight] - expectSize.height - 30.0f;
    [self setFrame:CGRectMake(self.frame.origin.x, _originY - _deltaY, self.frame.size.width, expectSize.height + 30.0f)];
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    if ([text isEqualToString:@"\n"]){
        if (_baseTVDelegate != nil) {
            if ([_baseTVDelegate respondsToSelector:@selector(sendButtonClicked)]) {
                [_baseTVDelegate sendButtonClicked];
            }
        }
        return false;
    }
    return true;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
