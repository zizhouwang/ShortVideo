//
//  ZZVideoResourceLoader.m
//  PopTiner
//
//  Created by zizhou wang on 2018/9/6.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import "ZZVideoResourceLoader.h"
#import "BaseURLSessionDataTask.h"
#import "NSURLSessionDataTask+CustomContent.h"
#import <MobileCoreServices/MobileCoreServices.h>
#import <AssetsLibrary/AssetsLibrary.h>
#import "Util.h"

@interface ZZVideoResourceLoader()<NSURLConnectionDataDelegate, NSURLSessionDataDelegate>

@property (nonatomic, assign) Boolean cancel;

@property (nonatomic, strong) NSMutableArray<AVAssetResourceLoadingRequest *> *loadingRequests;
@property (nonatomic, strong) NSMutableDictionary<NSURLSessionDataTask*, AVAssetResourceLoadingRequest *> *loadingRequestsDic;
@property (nonatomic, strong) AVAssetResourceLoadingRequest *runningLoadingRequest;

@end

@implementation ZZVideoResourceLoader

- (id)initWithURLStr:(NSString*)urlStr originURLStr:(NSString*)originURLStr {
    self = [super init];
    if (self) {
        _loadingRequests = [NSMutableArray array];
        _loadingRequestsDic = [NSMutableDictionary dictionary];
        _urlStr = urlStr;
        _originURLStr = originURLStr;
        NSString *savingPath = [NSHomeDirectory() stringByAppendingFormat:@"/Documents/%@",@"abc.mp4"];
        [[NSFileManager defaultManager] createFileAtPath:savingPath contents:nil attributes:nil];
    }
    return self;
}

- (NSRange)fetchRequestRangeWithRequest:(AVAssetResourceLoadingRequest *)loadingRequest {
    NSUInteger location, length;
    // data range.
    if ([loadingRequest.dataRequest respondsToSelector:@selector(requestsAllDataToEndOfResource)] && loadingRequest.dataRequest.requestsAllDataToEndOfResource) {
        location = (NSUInteger)loadingRequest.dataRequest.requestedOffset;
        length = NSUIntegerMax;
    }
    else {
        location = (NSUInteger)loadingRequest.dataRequest.requestedOffset;
        length = loadingRequest.dataRequest.requestedLength;
    }
    if(loadingRequest.dataRequest.currentOffset > 0){
        location = (NSUInteger)loadingRequest.dataRequest.currentOffset;
    }
    return NSMakeRange(location, length);
}

- (NSString*)jPRangeToHTTPRangeHeader:(NSRange)range {
    if ([self jPValidByteRange:range]) {
        if (range.location == NSNotFound) {
            return [NSString stringWithFormat:@"bytes=-%tu",range.length];
        }
        else if (range.length == NSUIntegerMax) {
            return [NSString stringWithFormat:@"bytes=%tu-",range.location];
        }
        else {
            return [NSString stringWithFormat:@"bytes=%tu-%tu",range.location, NSMaxRange(range) - 1];
        }
    }
    else {
        return nil;
    }
}

- (BOOL)jPValidByteRange:(NSRange)range {
    return ((range.location != NSNotFound) || (range.length > 0));
}

- (long long)getVideoFileLength:(NSURLResponse *)response {
    NSHTTPURLResponse *res = (NSHTTPURLResponse *)response;
    NSString *range = [res allHeaderFields][@"Content-Range"];
    if (range) {
        NSArray *ranges = [range componentsSeparatedByString:@"/"];
        if (ranges.count > 0) {
            NSString *lengthString = [[ranges lastObject] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
            return [lengthString longLongValue];
        }
    }
    else {
        return [response expectedContentLength];
    }
    return 0;
}

#pragma mark - AVAssetResourceLoaderDelegate
- (BOOL)resourceLoader:(AVAssetResourceLoader *)resourceLoader shouldWaitForLoadingOfRequestedResource:(AVAssetResourceLoadingRequest *)loadingRequest {
    NSLog(@"%@", @"收到新的请求");
    [_loadingRequests addObject:loadingRequest];
    NSLog(@"开始下载 目前有%lu个请求", (unsigned long)_loadingRequests.count);
    NSRange range = [self fetchRequestRangeWithRequest:loadingRequest];
    
    NSMutableURLRequest * request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:_urlStr] cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:20.0f];
    if (loadingRequest.dataRequest.requestedOffset > 0) {
    }
    NSString * rangeValue = [self jPRangeToHTTPRangeHeader:range];
    [request addValue:rangeValue forHTTPHeaderField:@"Range"];
//    [[NSURLSession sharedSession] dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
//        [loadingRequest.dataRequest respondWithData:data];
//        NSLog(@"%@", @"视频数据返回成功");
//    }];
    NSURLSession * session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration] delegate:self delegateQueue:[NSOperationQueue mainQueue]];
    NSURLSessionDataTask * task = [session dataTaskWithRequest:request];
    [_loadingRequestsDic setObject:loadingRequest forKey:task];
    [task resume];
    
    return YES;
}

- (void)resourceLoader:(AVAssetResourceLoader *)resourceLoader didCancelLoadingRequest:(AVAssetResourceLoadingRequest *)loadingRequest {
    NSLog(@"%@", @"取消下载");
//    if ([_loadingRequests containsObject:loadingRequest]) {
//        [_loadingRequests removeObject:loadingRequest];
//        NSURLSessionDataTask * theKey = nil;
//        for (NSURLSessionDataTask * key in _loadingRequestsDic) {
//            AVAssetResourceLoadingRequest * tempLoadingRequest = _loadingRequestsDic[key];
//            if (tempLoadingRequest == loadingRequest) {
//                theKey = key;
//            }
//        }
//        [_loadingRequestsDic removeObjectForKey:theKey];
//        NSLog(@"取消下载 目前有%lu个请求", (unsigned long)_loadingRequests.count);
//    }
}

#pragma mark - NSURLSessionDataDelegate
//服务器响应
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveResponse:(NSURLResponse *)response completionHandler:(void (^)(NSURLSessionResponseDisposition))completionHandler {
    if (self.cancel) return;
    AVAssetResourceLoadingRequest * loadingRequest = _loadingRequestsDic[dataTask];
    NSLog(@"%@", @"视频数据返回成功");
    NSString *mimeType = [response MIMEType];
    CFStringRef contentType = UTTypeCreatePreferredIdentifierForTag(kUTTagClassMIMEType, (__bridge CFStringRef)(mimeType), NULL);
    NSHTTPURLResponse *res = (NSHTTPURLResponse *)response;
    bool byteRangeAccessSupported = [res allHeaderFields][@"Content-Range"] != nil;
    loadingRequest.contentInformationRequest.byteRangeAccessSupported = byteRangeAccessSupported;
    loadingRequest.contentInformationRequest.contentType = CFBridgingRelease(contentType);
    long long contentLength = [self getVideoFileLength:response];
    loadingRequest.contentInformationRequest.contentLength = contentLength;
    NSLog(@"%lli", loadingRequest.contentInformationRequest.contentLength);
    if (completionHandler) {
        completionHandler(NSURLSessionResponseAllow);
    }
}

//服务器返回数据 可能会调用多次
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data {
    if (self.cancel) return;
    AVAssetResourceLoadingRequest * loadingRequest = _loadingRequestsDic[dataTask];
    [loadingRequest.dataRequest respondWithData:data];
    
    if (data.length > 64) {
        NSString *savingPath = [NSHomeDirectory() stringByAppendingFormat:@"/Documents/%@",@"abc.mp4"];
        NSFileHandle *writingHandle = [NSFileHandle fileHandleForWritingAtPath:savingPath];
        [writingHandle seekToEndOfFile];
        //    [Util jp_safeWriteData:writingHandle data:data];
        [writingHandle writeData:data];
        [writingHandle closeFile];
        long long size = [Util fileSizeAtPath:savingPath];
        NSLog(@"文件大小为%lli", size);
    }
    NSLog(@"网络数据为%lu", (unsigned long)data.length);
    
//    [SUFileHandle writeTempFileData:data];
//    self.cacheLength += data.length;
//    if (self.delegate && [self.delegate respondsToSelector:@selector(requestTaskDidUpdateCache)]) {
//        [self.delegate requestTaskDidUpdateCache];
//    }
}

//请求完成会调用该方法，请求失败则error有值
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    NSLog(@"下载完成");
    if (self.cancel) {
        NSLog(@"下载取消");
    }else {
        if (error) {
            NSLog(@"%@", error);
//            if (self.delegate && [self.delegate respondsToSelector:@selector(requestTaskDidFailWithError:)]) {
//                [self.delegate requestTaskDidFailWithError:error];
//            }
        }else {
            AVAssetResourceLoadingRequest * loadingRequest = _loadingRequestsDic[(NSURLSessionDataTask*)task];
            [_loadingRequests removeObject:loadingRequest];
            NSLog(@"下载完成 目前有%lu个请求", (unsigned long)_loadingRequests.count);
            [_loadingRequestsDic removeObjectForKey:(NSURLSessionDataTask*)task];
            if(_loadingRequests.count == 0){ // 全部完成.
                [loadingRequest finishLoading];
//                NSString *savingPath = [NSHomeDirectory() stringByAppendingFormat:@"/Documents/%@",@"abc"];
//                long long size = [Util fileSizeAtPath:savingPath];
//                NSLog(@"文件大小为%lli", size);
//                [loadingRequest finishLoading];
            }
            else { // 完成了一部分, 继续请求.
//                [self startNextTaskIfNeed];
            }
            //可以缓存则保存文件
//            if (self.cache) {
//                [SUFileHandle cacheTempFileWithFileName:[NSString fileNameWithURL:self.requestURL]];
//            }
//            if (self.delegate && [self.delegate respondsToSelector:@selector(requestTaskDidFinishLoadingWithCache:)]) {
//                [self.delegate requestTaskDidFinishLoadingWithCache:self.cache];
//            }
        }
    }
}

@end
