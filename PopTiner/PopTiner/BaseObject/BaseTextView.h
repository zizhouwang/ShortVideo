//
//  BaseTextView.h
//  PopTiner
//
//  Created by Mac on 2018/9/13.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@protocol BaseTextViewProtocol<NSObject>

- (void)zzKeyboardShow:(NSNotification *)note;
- (void)sendButtonClicked;

@end

@interface BaseTextView : UITextView

@property (nonatomic, assign) CGFloat originY;
@property (nonatomic, weak) id<BaseTextViewProtocol> baseTVDelegate;

@end
