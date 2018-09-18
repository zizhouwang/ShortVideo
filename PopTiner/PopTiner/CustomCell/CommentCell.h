//
//  CommentCell.h
//  PopTiner
//
//  Created by Mac on 2018/9/14.
//  Copyright © 2018年 zizhou wang. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CommentCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIButton *headerButton;
@property (weak, nonatomic) IBOutlet UILabel *nicknameLabel;
@property (weak, nonatomic) IBOutlet UILabel *commentLabel;

@end
