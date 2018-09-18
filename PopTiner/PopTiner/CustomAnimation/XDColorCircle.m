//
//  XDColorCircle.m
//  PopTiner
//
//  Created by Mac on 2018/9/13.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "XDColorCircle.h"

@implementation XDColorCircle

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor=[UIColor clearColor];
        UIView *circleView=[[UIView alloc]init];
        circleView.frame=CGRectMake(0, 0,frame.size.width,frame.size.height);
        circleView.backgroundColor=[UIColor whiteColor];
        [self addSubview: circleView];
        
        CAGradientLayer * gradientLayer = [CAGradientLayer layer];
        gradientLayer.colors = @[(__bridge id)[UIColor colorWithRed:0.0f green:0.0f blue:0.0f alpha:0.5f].CGColor,(__bridge id)[UIColor colorWithRed:1.0f green:1.0f blue:1.0f alpha:0.5f].CGColor];
        gradientLayer.locations = @[@0.2,@1.0];
        gradientLayer.startPoint = CGPointMake(0, 0);
        gradientLayer.endPoint = CGPointMake(1.0, 0);
        gradientLayer.frame =CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
        [circleView.layer insertSublayer:gradientLayer atIndex:0];
        
        CAShapeLayer *layer=[[CAShapeLayer alloc]init];
        CGMutablePathRef pathRef=CGPathCreateMutable();
        CGPathAddRelativeArc(pathRef, nil,frame.size.width/2.0,frame.size.height/2.0,frame.size.width<frame.size.height?frame.size.width/2.0-5:frame.size.height/2.0-5,0, 2*M_PI);
        layer.path=pathRef;
        layer.lineWidth=5;
        layer.fillColor=[UIColor clearColor].CGColor;
        layer.strokeColor=[UIColor blackColor].CGColor;
        CGPathRelease(pathRef);
        circleView.layer.mask=layer;
        
        CABasicAnimation *animation=[CABasicAnimation         animationWithKeyPath:@"transform.rotation.z"];  ;
        // 设定动画选项
        animation.duration = 1;
        animation.removedOnCompletion = NO;
        animation.fillMode = kCAFillModeForwards;
        animation.repeatCount =HUGE_VALF;
        // 设定旋转角度
        animation.fromValue = [NSNumber numberWithFloat:0.0]; // 起始角度
        animation.toValue = [NSNumber numberWithFloat:2 * M_PI]; // 终止角度
        [circleView.layer addAnimation:animation forKey:@"rotate-layer"];
        
        UILabel *label=[[UILabel alloc]init];
        label.text=@"保存中";
        label.font=[UIFont systemFontOfSize:32];
        label.textAlignment=NSTextAlignmentCenter;
        label.frame=CGRectMake(0, 0,frame.size.width,frame.size.height);
        label.backgroundColor=[UIColor clearColor];
        [self addSubview:label];
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
