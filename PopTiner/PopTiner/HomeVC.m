//
//  HomeVC.m
//  PopTiner
//
//  Created by zizhou wang on 2018/9/5.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "HomeVC.h"
#import <AFNetworking.h>
#import "ZZVideoPlayer.h"
#import "SaveVideoAlertView.h"
#import "CommentListView.h"
#import "VideoCommentModel.h"

@interface HomeVC () <NSURLSessionDelegate, ZZVideoPlayerProtocol, NSURLSessionDataDelegate, CommentListProtocol>

@property (nonatomic, strong) VideoModel * videoModel;
@property (nonatomic, strong) ZZVideoPlayer * player;
@property (nonatomic, assign) Boolean isNeedPlay;
@property (nonatomic, strong) NSTimer * updateProgressTimer;
@property (weak, nonatomic) IBOutlet UIButton *nicknameButton;
@property (weak, nonatomic) IBOutlet UILabel *videoContentLabel;
@property (weak, nonatomic) IBOutlet UIButton *likeCountButton;
@property (weak, nonatomic) IBOutlet UIButton *commentCountButton;
@property (weak, nonatomic) IBOutlet UIProgressView *playProgressView;
@property (nonatomic, strong) NSMutableArray<VideoCommentModel*> * videoCommentModels;

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
    [self.view insertSubview:_player atIndex:0];
    [self loadDataWithModel];
}

- (void)updateProgressView {
    if (_playProgressView.progress >= 1.0f) {
        _playProgressView.progress = 0.0f;
        _playProgressView.alpha = 1.0f;
    }
    _playProgressView.progress += 0.02;
    _playProgressView.alpha -= 0.01;
}

- (void)setIndex:(NSInteger)index {
    _index = index;
    [_player setIndex:_index];
}

- (void)setVideoModel:(VideoModel*)videoModel {
    NSArray * commentArray = @[
                               @"这个软件真的好用动动手指就轻松挣钱省时省力我很好奇为什么开个视频就有金币收也算是无意之间看到这款软件的抱着试试的心态使用用后感觉还是很不错的也不用管别人怎么用轻松的就挣到钱了反正不管是.",
                               @"上班还是下班还是在家闲着都可以做天力推荐如果有喜欢或者需要的朋友们可以多下载支持这个软件这个软件也适合各个年龄阶段只要会玩手机的人随时随地不分场合我也是才刚刚接触了解的不是特别说以后用熟悉了还会来评论的",
                               @"看上了之后感觉就想找到了老婆一样，  每天都会抱着她看那些发明，虽然都是小发明，但是有的确实又好用又好玩，我自己本来也特别热爱这些东西没事都会看着手机上里面的东西学着发明一些小玩意",
                               @"有的发表的言论我觉得好像跟真的似得虽然是猜想但是也不否认确实会有那些东西存在呀，还有那些小发明其实都是用一下生活中的用品就能做出来一些东西比如用报废的铁加上两根线用物理的原理就可以做出一个小电风扇，  在这个炎热的夏日可以随身携带别人问我哪里学来的我都会告诉他们是跟学的。",
                               @"里面我觉得最值得推荐的就是关于生活类的，里面都会有很多世界各地的网民，都会告诉你一些平时遗漏掉的小知识和我们常人不会在意的东西，有时候我都觉得里面的人实在是太有才了",
                               @"这些都让我们提高了很多生活品质，常年生活在城市里已经忘了大自然有很多东西都是有规律的，很多都是一一个循环，所以我每天都会去翻看喜欢。",
                               @"我是一个特别喜欢那些经典的电视的人，只要一出现什么好看的电视我就会第一之间追上去，看到生活不能自己，里面每天都会更新好多好看的电视电影片段，如果你觉得好看的话还可以直接点击观看完整视频",
                               @"特别喜欢这个功能，就好像是特定为了我出来的功能一样，他会根据你喜欢或者经常看的种类时间久了给你自己规划出来让你每次刷新出来都是你喜欢电影的类型。",
                               @"这一点我觉得特别好，不会让你观看的时候跳出来一些你不需要或者不感兴趣的东西，简单简洁但是又不失有趣，贴近生活知道观众需要的东西是什么。真心好。",
                               @"有热门视频，看完之后往上一划，就可以更新一批热门视频，更新速度挺快的，现在已经把软件推荐给我的家人和朋友们了，家人和朋友们都很喜欢这个软件，他们经常都是一直在刷不停地看这个",
                               @"有时候大家还会聚在一起分享各自收藏的有趣短视频内容，有时候一起互相调侃和吐槽，尤其在没有具体的话题情况下，总有那么几个搞笑视频成为我们茶余饭后的主题，说真的，还真的是一个很好的软件，就这样拉近了朋友之间的距离。",
                               @"以前觉得就都是网友们自己拍的，  现在看起来好像不止这些了，除了网友们的创意感觉还有专业的制作团队在上面发布视频了，比如会有一些专业的时装新品发布会的走秀，或者是专业的团队分享一些天南海北的新奇的事情，这些很多事情我都是闻所未闻见所未见的，很大程度上丰富了我的知识",
                               @"我觉得这个非常好，非常给力，让我足不出户就了解到了很多事情，  对于我这种宅男来说简直是天大的福利，赞一个给，希望可以运营的越来越好。",
                               @"我们平常分享视频都是自己看过之后，感觉很好适合，才会发给家里的群或是朋友的群里，除了搞笑的视频外，还有很多新闻类的或者是生活窍门类的视频都比较受到大家的欢迎，这就需要视频软件内容齐全而且还要新，有原创的内容",
                               @"就很好的满足了我的需要，好多独家视频比较适合分享，大家也都是喜欢看没看过的东西，所以每个人都能给我一个积极的反馈，现在我已经是身边人的小道消息聚集处了，被需要的感觉真好，谢谢!",
                               @"在朋友的再三推荐下我才下载了这款软件，我开始就是好奇它到底有什么魔力能让我朋友天天抱着手机在沙发上笑个不停我才下载的它，结果下载之后真的给了我很大的惊喜，里面的内容确实不错",
                               @"感觉用的网友都非常有才，真不知道那些点子他们是怎么想出来的。我是一个旅游爱好者，  现在也会把自己所到之处拍摄下来，发到网上做个记录",
                               @"首页有热门视频，看完之后往上一一划，就可以更新一批热门视频，更新速度挺快的，现在已经把软件推荐给我的家人和朋友们了，家人和朋友们都很喜欢这个软件",
                               @"非常喜欢这个平台，他有很多非常丰富的短视频内容，可以看到许多和自己不同的人的生活状态，也能看到和自己不一样的生活方式，还可以和他们进行交流，无论是平凡的普通人，还是小有名气的明星网红，还是商业成功人士。",
                               @"感谢这个平台让我更加深入的了解了这个时代和社会，这真是一个包罗万象的平台，除了交流，我还可以从中学到很多东西，比如做饭等等一些技 能什么的，还有和家人朋友相处的关系处理方式等等，真的是非常有生活意义和社会价值。",
                               @"我新买了个小金毛，很多东西都不懂该怎么弄，哪天小奶狗拉稀了我就非常害怕，我就忽然想到了这个平台，我记得平时也有一些网友把自己的宠物视频拍下来发到网上分享，于是我就也把我家狗狗生病的视频拍摄下来发到了网上，并且在标题询问该怎么办，结果真的得到很多热心网友的帮助"
                               ];
    _videoModel = videoModel;
    NSArray * videoComments = [[Util shareInstance].homePageViewController.videoCommentDic objectForKey:[NSNumber numberWithInteger:videoModel.video_id]];
    if (videoComments == nil) {
        _videoCommentModels = [NSMutableArray array];
        NSInteger commentCount = videoModel.video_details.comment_number;
        for (NSInteger i = 0; i < commentCount; i++) {
            VideoCommentModel * videoCommentModel = [[VideoCommentModel alloc] init];
            videoCommentModel.nickname = [NSString stringWithFormat:@"用户%li", arc4random() % 3294967296 + 99999999];
            videoCommentModel.commentContent = commentArray[arc4random() % commentArray.count];
            [_videoCommentModels addObject:videoCommentModel];
        }
    } else {
        _videoCommentModels = [NSMutableArray arrayWithArray:videoComments];
    }
    [[Util shareInstance].homePageViewController.videoCommentDic setObject:[NSArray arrayWithArray:_videoCommentModels] forKey:[NSNumber numberWithInteger:videoModel.video_id]];
}

- (void)loadDataWithModel {
    VideoDetails * videoDetails = _videoModel.video_details;
    [_likeCountButton setTitle:[Util numberToKStr:videoDetails.liked_number] forState:UIControlStateNormal];
    [_commentCountButton setTitle:[Util numberToKStr:videoDetails.comment_number] forState:UIControlStateNormal];
    [_nicknameButton setTitle:_videoModel.author_nickname forState:UIControlStateNormal];
    if ([_videoModel.desc isEqual:@""]) {
        [_videoContentLabel setText:_videoModel.share_text];
    } else {
        [_videoContentLabel setText:_videoModel.desc];
    }
}

- (void)setVideoInfo:(NSString*)urlStr {
    [self installMovieNotificationObservers];
    _originURLStr = urlStr;
    _player.urlStr = urlStr;
    _player.originURLStr = urlStr;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [_player p_currentItemAddObserver];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [Util shareInstance].currentVideoPlayer = nil;
    [self removeMovieNotificationObservers];
    [_player.avPlayer pause];
    [_player p_currentItemRemoveObserver];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [Util shareInstance].homePageViewController.currentIndex = _index;
    [Util shareInstance].currentVideoPlayer = _player;
    _playProgressView.progress = 0.0f;
    _playProgressView.alpha = 0.75f;
    if (_updateProgressTimer) {
        [_updateProgressTimer invalidate];
    }
    _updateProgressTimer = [NSTimer scheduledTimerWithTimeInterval:0.02 target:self selector:@selector(updateProgressView) userInfo:nil repeats:true];
    if (_originURLStr != nil) {
        if (_urlStr == nil) {
            NSURL *url = [NSURL URLWithString:_originURLStr];
            NSURLRequest *request = [[NSURLRequest alloc] initWithURL:url];
            NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
            config.requestCachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
            NSURLSession *urlSession = [NSURLSession sessionWithConfiguration:config delegate:self delegateQueue:[NSOperationQueue currentQueue]];
            NSURLSessionDataTask *task = [urlSession dataTaskWithRequest:request];
            [task resume];
        } else {
            [_player startLoadVideo:_urlStr];
            [_player.avPlayer play];
            if ([_player.avPlayerLayer isReadyForDisplay]) {
                [_updateProgressTimer invalidate];
                _updateProgressTimer = nil;
                _playProgressView.progress = 0.0f;
                _playProgressView.alpha = 0.25f;
            }
        }
    }
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [self removeMovieNotificationObservers];
    [_player.avPlayer pause];
    if (_updateProgressTimer) {
        [_updateProgressTimer invalidate];
        _updateProgressTimer = nil;
    }
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

- (IBAction)reportButtonClicked:(UIButton *)sender {
    SaveVideoAlertView * saveVideoAlertView = [[SaveVideoAlertView alloc] initWithFrame:self.view.frame];
    [self.view addSubview:saveVideoAlertView];
    [saveVideoAlertView showAlertLabel:@"举报成功"];
}

- (IBAction)likeButtonClicked:(UIButton *)sender {
    [sender setImage:[UIImage imageNamed:@"liked_heart.png"] forState:UIControlStateNormal];
}

- (IBAction)commentButtonClicked:(UIButton *)sender {
    CommentListView * commentListView = [[CommentListView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, self.view.frame.size.width, self.view.frame.size.height) videoCommentModels:[NSArray arrayWithArray:_videoCommentModels]];
    commentListView.commentDelegate = self;
    [self.view addSubview:commentListView];
    [Util shareInstance].homePageViewController.scrollView.scrollEnabled = false;
}

- (IBAction)saveVideoToPhotosAlbum:(UIButton *)sender {
    SaveVideoAlertView * saveVideoAlertView = [[SaveVideoAlertView alloc] initWithFrame:self.view.frame];
    saveVideoAlertView.tag = 100;
    [self.view addSubview:saveVideoAlertView];
    [saveVideoAlertView startAnimation];
    if ([Util isSavedVideoURL:_originURLStr]) {
        NSString * localFilePath = [Util generateLocalVideoPath:_originURLStr];
        UISaveVideoAtPathToSavedPhotosAlbum(localFilePath, self, @selector(video:didFinishSavingWithError:contextInfo:), nil);
    } else {
        [saveVideoAlertView showAlertLabel:@"视频缓冲中"];
    }
}

#pragma mark - NSURLSessionDataDelegate
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveResponse:(NSURLResponse *)response completionHandler:(void (^)(NSURLSessionResponseDisposition))completionHandler {
    if ([[Util shareInstance].currentVideoPlayer isEqual:_player]) {
        NSHTTPURLResponse *res = (NSHTTPURLResponse *)response;
        NSDictionary * headerFields = res.allHeaderFields;
        _urlStr = headerFields[@"Location"];
        [_player startLoadVideo:_urlStr];
        [_player.avPlayer play];
    }
    completionHandler(NSURLSessionResponseAllow);
}

- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data {
    
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    @try {
        [session finishTasksAndInvalidate];
    } @catch(NSException *exception) {
        
    }
}

#pragma mark - NSURLSessionDelegate
//不要注释下面这个方法 它用来否定自动重定向
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task willPerformHTTPRedirection:(NSHTTPURLResponse *)response newRequest:(NSURLRequest *)request completionHandler:(void (^)(NSURLRequest * __nullable))completionHandler {
    completionHandler(nil);
}

#pragma mark - ZZVideoPlayerProtocol
- (void)playbackFinish:(NSNotification *)notifi {
    [_player.avPlayerItem seekToTime:kCMTimeZero];
    [_player.avPlayer play];
}

- (void)readyToPlay:(id)videoPlayer {
    if (_updateProgressTimer) {
        [_updateProgressTimer invalidate];
        _updateProgressTimer = nil;
    }
    _playProgressView.progress = 0.0f;
    _playProgressView.alpha = 0.25f;
}

#pragma mark 视频保存完毕的回调
- (void)video:(NSString *)videoPath didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInf{
    SaveVideoAlertView * saveVideoAlertView = (SaveVideoAlertView*)[[Util mainWindow] viewWithTag:100];
    if (error) {
        [saveVideoAlertView showAlertLabel:@"保存视频失败"];
        NSLog(@"保存视频过程中发生错误，错误信息:%@",error.localizedDescription);
    }else{
        [saveVideoAlertView showAlertLabel:@"保存视频成功"];
        NSLog(@"视频保存成功.");
    }
}

#pragma mark - CommentListProtocol
- (void)sendComment:(NSString *)commentStr commentListView:(CommentListView *)commentListView {
    VideoCommentModel * videoCommentModel = [[VideoCommentModel alloc] init];
    videoCommentModel.nickname = [NSString stringWithFormat:@"用户%li", (long)[Util shareInstance].userId];
    videoCommentModel.commentContent = commentStr;
    [_videoCommentModels insertObject:videoCommentModel atIndex:0];
    [[Util shareInstance].homePageViewController.videoCommentDic setObject:_videoCommentModels forKey:[NSNumber numberWithInteger:_videoModel.video_id]];
    commentListView.videoComments = [NSArray arrayWithArray:_videoCommentModels];
    [_commentCountButton setTitle:[NSString stringWithFormat:@"%lu", (unsigned long)_videoCommentModels.count] forState:UIControlStateNormal];
    [commentListView.commentTableView reloadData];
}

- (void)removeSelf {
    [Util shareInstance].homePageViewController.scrollView.scrollEnabled = true;
}

- (void)dealloc {
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
