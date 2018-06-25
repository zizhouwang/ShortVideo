package com.video.tiner.zizhouwang.tinervideo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

/**
 * Created by zizhouwang on 2018/6/8.
 */

public class TwitterResultReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
            // success
            final Long tweetId = intent.getLongExtra(TweetUploadService.EXTRA_TWEET_ID, 0);
        } else {
            // failure
            final Intent retryIntent = intent.getParcelableExtra(TweetUploadService.EXTRA_RETRY_INTENT);
        }
    }
}
