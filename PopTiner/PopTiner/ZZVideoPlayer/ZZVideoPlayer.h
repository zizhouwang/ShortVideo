//
//  ZZVideoPlayer.h
//  PopTiner
//
//  Created by zizhou wang on 2018/9/6.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import "ZZVideoResourceLoader.h"

@protocol ZZVideoPlayerProtocol<NSObject>

@required
- (void)playbackFinish:(NSNotification*)notifi;

@end

@interface ZZVideoPlayer : UIView

@property (nonatomic, assign) Boolean isNeedPlay;
@property (nonatomic, assign) Boolean isCouldPlay;
//@property (nonatomic, assign) Boolean isPlayed;
//@property (nonatomic, assign) Boolean isPause;

@property (nonatomic, weak) id<ZZVideoPlayerProtocol> delegate;

@property (nonatomic, strong) NSString * urlStr;
@property (nonatomic, strong) AVURLAsset * asset;
@property (nonatomic, strong) AVPlayerItem * avPlayerItem;
@property (nonatomic, strong) AVPlayer * avPlayer;
@property (nonatomic, strong) AVPlayerLayer * avPlayerLayer;
@property (nonatomic, strong) ZZVideoResourceLoader * resourceLoader;

- (void)startLoadVideo:(NSString*)urlStr;
- (void)p_currentItemRemoveObserver;

@end
