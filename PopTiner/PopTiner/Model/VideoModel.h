//
//Created by ESJsonFormatForMac on 18/09/05.
//

#import <Foundation/Foundation.h>

@class VideoDetails;
@interface VideoModel : NSObject

@property (nonatomic, copy) NSString *share_url;

@property (nonatomic, copy) NSString *channel;

@property (nonatomic, copy) NSString *share_text;

@property (nonatomic, copy) NSString *desc;

@property (nonatomic, copy) NSString *author_nickname;

@property (nonatomic, copy) NSString *author_uid;

@property (nonatomic, assign) NSInteger video_id;

@property (nonatomic, copy) NSString *video_cdn_url;

@property (nonatomic, copy) NSString *width_height;

@property (nonatomic, strong) VideoDetails *video_details;

@property (nonatomic, copy) NSString *video_image_url;

@end
@interface VideoDetails : NSObject

@property (nonatomic, assign) NSInteger liked_number;

@property (nonatomic, assign) NSInteger comment_number;

@property (nonatomic, assign) NSInteger share_number;

@end

