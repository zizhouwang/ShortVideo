//
//  HomeVC.m
//  PopTiner
//
//  Created by zizhou wang on 2018/9/5.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "HomeVC.h"
#import <AFNetworking.h>
#import <IJKMediaFramework/IJKMediaFramework.h>
#import "ZZVideoPlayer.h"

@interface HomeVC () <NSURLSessionDelegate, ZZVideoPlayerProtocol>

//@property (nonatomic, strong) id<IJKMediaPlayback> player;
@property (nonatomic, strong) ZZVideoPlayer * player;
@property (nonatomic, assign) Boolean isNeedPlay;

@end

@implementation HomeVC

- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        _isNeedPlay = false;
        _player = [[ZZVideoPlayer alloc] init];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor colorWithRed:22.0f / 255.0f green:24.0f / 255.0f blue:35.0f / 255.0f alpha:1.0f];
    _player.delegate = self;
    [self.view addSubview:_player];
}

- (void)startInitVideo:(NSString*)urlStr {
//    [_player play];
//    AFHTTPSessionManager * manager =[AFHTTPSessionManager manager];
//    [manager setTaskWillPerformHTTPRedirectionBlock:^NSURLRequest * _Nonnull(NSURLSession * _Nonnull session, NSURLSessionTask * _Nonnull task, NSURLResponse * _Nonnull response, NSURLRequest * _Nonnull request) {
//        return nil;
//    }];
    [self installMovieNotificationObservers];
    _player.urlStr = urlStr;
    __weak typeof(self) weakSelf = self;
    NSURL *url = [NSURL URLWithString:urlStr];
    NSURLRequest *request = [[NSURLRequest alloc] initWithURL:url];
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    config.requestCachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
    NSURLSession *urlSession = [NSURLSession sessionWithConfiguration:config delegate:self delegateQueue:[NSOperationQueue currentQueue]];
    NSURLSessionDataTask *task = [urlSession dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        NSHTTPURLResponse *res = (NSHTTPURLResponse *)response;
        NSDictionary * headerFields = res.allHeaderFields;
        [weakSelf.player startLoadVideo:headerFields[@"Location"]];
//        [self.view addSubview:weakSelf.player];
        weakSelf.isNeedPlay = true;
    }];
    [task resume];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
//    [self removeMovieNotificationObservers];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
//    if (_isNeedPlay == true && _player.isPreparedToPlay == false) {
//        [_player prepareToPlay];
//    }
    [_player setIsNeedPlay:true];
    _isNeedPlay = true;
    [_player.avPlayer play];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [self removeMovieNotificationObservers];
    [_player.avPlayer pause];
}

- (void)installMovieNotificationObservers {
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(loadStateDidChange:)
//                                                 name:IJKMPMoviePlayerLoadStateDidChangeNotification
//                                               object:_player];
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(moviePlayBackFinish:)
//                                                 name:IJKMPMoviePlayerPlaybackDidFinishNotification
//                                               object:_player];
//
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(mediaIsPreparedToPlayDidChange:)
//                                                 name:IJKMPMediaPlaybackIsPreparedToPlayDidChangeNotification
//                                               object:_player];
//
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(moviePlayBackStateDidChange:)
//                                                 name:IJKMPMoviePlayerPlaybackStateDidChangeNotification
//                                               object:_player];
}

- (void)removeMovieNotificationObservers {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

//- (void)loadStateDidChange:(NSNotification*)notification {
//    IJKMPMovieLoadState loadState = _player.loadState;
//
//    if ((loadState & IJKMPMovieLoadStatePlaythroughOK) != 0) {
//        NSLog(@"LoadStateDidChange: IJKMovieLoadStatePlayThroughOK: %d\n",(int)loadState);
//    }else if ((loadState & IJKMPMovieLoadStateStalled) != 0) {
//        NSLog(@"loadStateDidChange: IJKMPMovieLoadStateStalled: %d\n", (int)loadState);
//    } else {
//        NSLog(@"loadStateDidChange: ???: %d\n", (int)loadState);
//    }
//}
//
//- (void)moviePlayBackFinish:(NSNotification*)notification {
//    int reason =[[[notification userInfo] valueForKey:IJKMPMoviePlayerPlaybackDidFinishReasonUserInfoKey] intValue];
//    switch (reason) {
//        case IJKMPMovieFinishReasonPlaybackEnded:
//            NSLog(@"playbackStateDidChange: IJKMPMovieFinishReasonPlaybackEnded: %d\n", reason);
//            [_player play];
//            break;
//
//        case IJKMPMovieFinishReasonUserExited:
//            NSLog(@"playbackStateDidChange: IJKMPMovieFinishReasonUserExited: %d\n", reason);
//            break;
//
//        case IJKMPMovieFinishReasonPlaybackError:
//            NSLog(@"playbackStateDidChange: IJKMPMovieFinishReasonPlaybackError: %d\n", reason);
//            break;
//
//        default:
//            NSLog(@"playbackPlayBackDidFinish: ???: %d\n", reason);
//            break;
//    }
//}
//
//- (void)mediaIsPreparedToPlayDidChange:(NSNotification*)notification {
//    NSLog(@"mediaIsPrepareToPlayDidChange\n");
//}
//
//- (void)moviePlayBackStateDidChange:(NSNotification*)notification {
//    switch (_player.playbackState) {
//        case IJKMPMoviePlaybackStateStopped:
//            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: stoped", (int)_player.playbackState);
//            break;
//
//        case IJKMPMoviePlaybackStatePlaying:
//            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: playing", (int)_player.playbackState);
//            break;
//
//        case IJKMPMoviePlaybackStatePaused:
//            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: paused", (int)_player.playbackState);
////            [_player play];
//            break;
//
//        case IJKMPMoviePlaybackStateInterrupted:
//            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: interrupted", (int)_player.playbackState);
//            break;
//
//        case IJKMPMoviePlaybackStateSeekingForward:
//        case IJKMPMoviePlaybackStateSeekingBackward: {
//            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: seeking", (int)_player.playbackState);
//            break;
//        }
//
//        default: {
//            NSLog(@"IJKMPMoviePlayBackStateDidChange %d: unknown", (int)_player.playbackState);
//            break;
//        }
//    }
//}

- (BOOL)prefersStatusBarHidden {
    return YES;
}

#pragma mark - NSURLSessionDelegate
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task willPerformHTTPRedirection:(NSHTTPURLResponse *)response newRequest:(NSURLRequest *)request completionHandler:(void (^)(NSURLRequest * __nullable))completionHandler{
    completionHandler(nil);
}

#pragma mark - ZZVideoPlayerProtocol
- (void)playbackFinish:(NSNotification *)notifi {
    NSLog(@"播放完成%li", (long)_index);
}

- (void)dealloc {
    [_player p_currentItemRemoveObserver];
    NSLog(@"dealloc%li" ,(long)_index);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
