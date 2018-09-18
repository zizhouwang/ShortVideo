//
//  ZZVideoPlayer.m
//  PopTiner
//
//  Created by zizhou wang on 2018/9/6.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "ZZVideoPlayer.h"

@implementation ZZVideoPlayer

- (id)init {
    self = [super init];
    if (self) {
        _avPlayer = [[AVPlayer alloc] init];
        _avPlayer.volume = 1.0f;
        _asset = [AVURLAsset URLAssetWithURL:[NSURL URLWithString:@"systemCannotRecognition"] options:nil];
        _avPlayerItem = [AVPlayerItem playerItemWithAsset:_asset];
        _avPlayerLayer = [AVPlayerLayer playerLayerWithPlayer:_avPlayer];
        _avPlayerLayer.frame = CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height);
        _avPlayerLayer.backgroundColor = [[UIColor colorWithRed:22.0f / 255.0f green:24.0f / 255.0f blue:35.0f / 255.0f alpha:1.0f] CGColor];
        self.backgroundColor = [UIColor colorWithRed:22.0f / 255.0f green:24.0f / 255.0f blue:35.0f / 255.0f alpha:1.0f];
        _avPlayerLayer.videoGravity = AVLayerVideoGravityResizeAspect;
        [self.layer addSublayer:_avPlayerLayer];
    }
    return self;
}

- (void)startLoadVideo:(NSString*)urlStr {
    _resourceLoader = [[ZZVideoResourceLoader alloc] initWithURLStr:_urlStr originURLStr:_originURLStr];
    [_resourceLoader setIndex:_index];
    _resourceLoader.urlStr = _urlStr;
    _resourceLoader.originURLStr = _urlStr;
    [_asset.resourceLoader setDelegate:_resourceLoader queue:dispatch_get_main_queue()];
    [_avPlayer replaceCurrentItemWithPlayerItem:_avPlayerItem];
}

- (void)setIndex:(NSInteger)index {
    _index = index;
}

- (NSURL *)handleVideoURL:(NSURL *)url {
    NSURLComponents *components = [[NSURLComponents alloc] initWithURL:url resolvingAgainstBaseURL:NO];
    components.scheme = @"systemCannotRecognitionScheme";
    return [components URL];
}

- (NSURL *)getSchemeVideoURL:(NSURL *)url{
    NSURLComponents *components = [[NSURLComponents alloc] initWithURL:url resolvingAgainstBaseURL:NO];
    components.scheme = @"systemCannotRecognition";
    return [components URL];
}

- (void)p_currentItemRemoveObserver {
//    [self.avPlayer.currentItem removeObserver:self forKeyPath:@"status"];
//    [self.avPlayer.currentItem removeObserver:self forKeyPath:@"loadedTimeRanges"];
//    [[NSNotificationCenter defaultCenter] removeObserver:self name:AVPlayerItemDidPlayToEndTimeNotification object:nil];
    
    [_avPlayerItem removeObserver:self forKeyPath:@"status"];
    [_avPlayerItem removeObserver:self forKeyPath:@"loadedTimeRanges"];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:AVPlayerItemDidPlayToEndTimeNotification object:_avPlayerItem];
    //    [self.avPlayer removeTimeObserver:self.timeObserver];
}

- (void)p_currentItemAddObserver {
    //监控状态属性，注意AVPlayer也有一个status属性，通过监控它的status也可以获得播放状态
//    [_avPlayer.currentItem addObserver:self forKeyPath:@"status" options:(NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew) context:nil];
    [_avPlayerItem addObserver:self forKeyPath:@"status" options:(NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew) context:nil];
    
    //监控缓冲加载情况属性
//    [_avPlayer.currentItem addObserver:self forKeyPath:@"loadedTimeRanges" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    [_avPlayerItem addObserver:self forKeyPath:@"loadedTimeRanges" options:NSKeyValueObservingOptionOld|NSKeyValueObservingOptionNew context:nil];
    
    //监控播放完成通知
//    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(playbackFinished:) name:AVPlayerItemDidPlayToEndTimeNotification object:self.avPlayer.currentItem];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(playbackFinished:) name:AVPlayerItemDidPlayToEndTimeNotification object:_avPlayerItem];
    
    //监控时间进度
    //    @weakify(self);
    //    __weak typeof(self) weakSelf = self;
    //    self.timeObserver = [self.avPlayer addPeriodicTimeObserverForInterval:CMTimeMake(1, 1) queue:dispatch_get_main_queue() usingBlock:^(CMTime time) {
    //        @strongify(self);
    //        // 在这里将监听到的播放进度代理出去，对进度条进行设置
    //        if (self.delegate && [self.delegate respondsToSelector:@selector(updateProgressWithPlayer:)]) {
    //            [self.delegate updateProgressWithPlayer:self.player];
    //        }
    //    }];
}

- (void)setIsNeedPlay:(Boolean)isNeedPlay {
    _isNeedPlay = isNeedPlay;
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
//    AVPlayerItem *playerItem = object;
    if ([keyPath isEqualToString:@"status"]) {
        AVPlayerItemStatus status = [change[@"new"] integerValue];
        switch (status) {
            case AVPlayerItemStatusReadyToPlay:
            {
                // 开始播放
                _isCouldPlay = true;
                if ([self.delegate respondsToSelector:@selector(readyToPlay:)]) {
                    [self.delegate readyToPlay:self];                    
                }
                // 代理回调，开始初始化状态
//                if (self.delegate && [self.delegate respondsToSelector:@selector(startPlayWithplayer:)]) {
//                    [self.delegate startPlayWithplayer:self.player];
//                }
            }
                break;
            case AVPlayerItemStatusFailed:
            {
                NSLog(@"加载失败");
            }
                break;
            case AVPlayerItemStatusUnknown:
            {
                NSLog(@"未知资源");
            }
                break;
            default:
                break;
        }
    } else if([keyPath isEqualToString:@"loadedTimeRanges"]){
//        NSArray *array=playerItem.loadedTimeRanges;
//        //本次缓冲时间范围
//        CMTimeRange timeRange = [array.firstObject CMTimeRangeValue];
//        float startSeconds = CMTimeGetSeconds(timeRange.start);
//        float durationSeconds = CMTimeGetSeconds(timeRange.duration);
//        //缓冲总长度
//        NSTimeInterval totalBuffer = startSeconds + durationSeconds;
//        NSLog(@"共缓冲：%.2f",totalBuffer);
//        if (self.delegate && [self.delegate respondsToSelector:@selector(updateBufferProgress:)]) {
//            [self.delegate updateBufferProgress:totalBuffer];
//        }
        
    } else if ([keyPath isEqualToString:@"rate"]) {
        // rate=1:播放，rate!=1:非播放
//        float rate = self.player.rate;
//        if (self.delegate && [self.delegate respondsToSelector:@selector(player:changeRate:)]) {
//            [self.delegate player:self.player changeRate:rate];
//        }
    } else if ([keyPath isEqualToString:@"currentItem"]) {
//        NSLog(@"新的currentItem");
//        if (self.delegate && [self.delegate respondsToSelector:@selector(changeNewPlayItem:)]) {
//            [self.delegate changeNewPlayItem:self.player];
//        }
    }
}

- (void)playbackFinished:(NSNotification *)notifi {
    if ([self.delegate respondsToSelector:@selector(playbackFinish:)]) {
        [self.delegate playbackFinish:notifi];
    }
}

- (void)dealloc {
    NSLog(@"zz video player dealloc%li" ,(long)_index);
}

@end
