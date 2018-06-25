package com.video.tiner.zizhouwang.tinervideo.model;

/**
 * Created by Administrator on 2018/5/27.
 */

public class VideoModel {
    /**
     * video_id : 94294
     * video_cdn_url : http://mpaw-suse1.muscdn.com/reg02/2018/04/05/02/6540663835529843727_eckjTsuddh_o.mp4
     * video_image_url : http://mphw-suse1.muscdn.com/reg02/2018/04/05/02/4a0d5647-9959-409a-9a79-bec194e584ad_PMftALiFiS.jpg
     * share_url : http://www.musical.ly/v/6540663835529843727.html?uid=a0f097e3
     * share_text : #duet with @alexissses mention her as much as you can please❤️ #shuffle #dancearab #musicallyarab
     * width_height : 608x540
     * duration : 14834
     * video_created_time : 1522866962
     * video_details : {"share_number":4994,"comment_number":1980,"liked_number":322070}
     * channel : Musical.ly
     */

    private int video_id;
    private String video_cdn_url;
    private String video_image_url;
    private String share_url;
    private String share_text;
    private String width_height;
    private int duration;
    private int video_created_time;
    private VideoDetailsBean video_details;
    private String channel;

    public int getVideo_id() {
        return video_id;
    }

    public void setVideo_id(int video_id) {
        this.video_id = video_id;
    }

    public String getVideo_cdn_url() {
        return video_cdn_url;
    }

    public void setVideo_cdn_url(String video_cdn_url) {
        this.video_cdn_url = video_cdn_url;
    }

    public String getVideo_image_url() {
        return video_image_url;
    }

    public void setVideo_image_url(String video_image_url) {
        this.video_image_url = video_image_url;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getShare_text() {
        return share_text;
    }

    public void setShare_text(String share_text) {
        this.share_text = share_text;
    }

    public String getWidth_height() {
        return width_height;
    }

    public void setWidth_height(String width_height) {
        this.width_height = width_height;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getVideo_created_time() {
        return video_created_time;
    }

    public void setVideo_created_time(int video_created_time) {
        this.video_created_time = video_created_time;
    }

    public VideoDetailsBean getVideo_details() {
        return video_details;
    }

    public void setVideo_details(VideoDetailsBean video_details) {
        this.video_details = video_details;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public static class VideoDetailsBean {
        /**
         * share_number : 4994
         * comment_number : 1980
         * liked_number : 322070
         */

        private int share_number;
        private int comment_number;
        private int liked_number;

        public int getShare_number() {
            return share_number;
        }

        public void setShare_number(int share_number) {
            this.share_number = share_number;
        }

        public int getComment_number() {
            return comment_number;
        }

        public void setComment_number(int comment_number) {
            this.comment_number = comment_number;
        }

        public int getLiked_number() {
            return liked_number;
        }

        public void setLiked_number(int liked_number) {
            this.liked_number = liked_number;
        }
    }
}
