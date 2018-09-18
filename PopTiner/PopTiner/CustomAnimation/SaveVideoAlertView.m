//
//  SaveVideoAlertView.m
//  PopTiner
//
//  Created by Mac on 2018/9/13.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "SaveVideoAlertView.h"

@interface SaveVideoAlertView ()

@property (nonatomic, strong) UIView * centorBackgroundView;
@property (nonatomic, strong) UIActivityIndicatorView * indicatorView;

@property (nonatomic, strong) UIButton * backgroundButton;
@property (nonatomic, strong) UIView * alertBackgroundView;
@property (nonatomic, strong) UILabel * alertLabel;

@end

@implementation SaveVideoAlertView

- (id)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _centorBackgroundView = [[UIView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 80.0f, 80.0f)];
        _centorBackgroundView.center = CGPointMake(frame.size.width / 2.0f, frame.size.height / 2.0f);
        _centorBackgroundView.backgroundColor = [UIColor colorWithRed:0.0f green:0.0f blue:0.0f alpha:0.5f];
        _centorBackgroundView.userInteractionEnabled = false;
        _centorBackgroundView.layer.cornerRadius = 15.0f;
        [self addSubview:_centorBackgroundView];
        
        _indicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:(UIActivityIndicatorViewStyleWhite)];
        _indicatorView.frame = CGRectMake(0.0f, 0.0f, 80.0f, 80.0f);
//        _indicatorView.color = [UIColor whiteColor];
        [_centorBackgroundView addSubview:_indicatorView];
        
        _backgroundButton = [[UIButton alloc] initWithFrame:self.frame];
        [_backgroundButton addTarget:self action:@selector(removeSelf) forControlEvents:UIControlEventTouchUpInside];
        _backgroundButton.hidden = true;
        [self insertSubview:_backgroundButton atIndex:0];
        
        _alertBackgroundView = [[UIView alloc] init];
        _alertBackgroundView.backgroundColor = [UIColor colorWithRed:0.0f green:0.0f blue:0.0f alpha:0.5f];
        _alertBackgroundView.hidden = true;
        _alertBackgroundView.userInteractionEnabled = false;
        _alertBackgroundView.layer.cornerRadius = 5.0f;
        [self addSubview:_alertBackgroundView];
        
        _alertLabel = [[UILabel alloc] init];
        [_alertBackgroundView addSubview:_alertLabel];
    }
    return self;
}

- (void)startAnimation {
    [_indicatorView startAnimating];
}

- (void)showAlertLabel:(NSString*)alertStr {
    _centorBackgroundView.hidden = true;
    [_alertLabel setText:alertStr];
    [_alertLabel setTextColor:[UIColor whiteColor]];
    _alertLabel.numberOfLines = 0;
    _alertLabel.lineBreakMode = NSLineBreakByTruncatingTail;
    CGSize maximumLabelSize = CGSizeMake(180, 400);//labelsize的最大值
    CGSize expectSize = [_alertLabel sizeThatFits:maximumLabelSize];
    _alertLabel.frame = CGRectMake(0.0f, 0.0f, expectSize.width, expectSize.height);
    _alertBackgroundView.hidden = false;
    _alertBackgroundView.frame = CGRectMake(0.0f, 0.0f, expectSize.width + 10.0f, expectSize.height + 10.0f);
    _alertBackgroundView.center = CGPointMake(self.frame.size.width / 2.0f, self.frame.size.height / 2.0f);
    _alertLabel.center = CGPointMake(_alertBackgroundView.frame.size.width / 2.0f, _alertBackgroundView.frame.size.height / 2.0f);
    _backgroundButton.hidden = false;
    
    [NSTimer scheduledTimerWithTimeInterval:1.0f target:self selector:@selector(removeSelf) userInfo:nil repeats:false];
}

- (void)removeSelf {
    [self removeFromSuperview];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
