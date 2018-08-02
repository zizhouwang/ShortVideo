// let changeColor = document.getElementById('changeColor');
var showVideoWidth = 300;
var imageWidthNumber = 200;
let headerDiv = document.getElementById('headerDiv');
//headerDiv.style.width = showVideoWidth * 2 + 8 + 'px';
let videosDiv = document.getElementById('videosDiv');
let refreshImg = document.getElementById('refreshGif');
refreshImg.style.width = imageWidthNumber + 'px';
refreshImg.style.height = imageWidthNumber + 'px';
var refreshImgMarginLeftNumber = (showVideoWidth * 2 + 8 - imageWidthNumber) / 2;
refreshImg.style.marginLeft = refreshImgMarginLeftNumber + 'px';
let bottomRefreshImg = document.getElementById('bottomRefreshGif');
bottomRefreshImg.style.width = imageWidthNumber + 'px';
bottomRefreshImg.style.height = imageWidthNumber + 'px';
bottomRefreshImg.style.marginLeft = refreshImgMarginLeftNumber + 'px';
let videosDiv1 = document.getElementById('videosDiv1');
let videosDiv2 = document.getElementById('videosDiv2');
videosDiv.style.width = showVideoWidth * 2 + 8 + 'px';
videosDiv1.style.width = showVideoWidth + 'px';
videosDiv2.style.width = showVideoWidth + 'px';
videosDiv1.style.float = "left";
videosDiv2.style.float = "left";
var isLoadingMore = false;
var currentVideo = undefined;

function generateUUID() {
  var d = new Date().getTime();
  var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = (d + Math.random()*16)%16 | 0;
    d = Math.floor(d/16);
    return (c=='x' ? r : (r&0x3|0x8)).toString(16);
  });
  return uuid;
};
if (localStorage.getItem("get_video_network_identify") === null) {
  var uuid = generateUUID();
  localStorage.setItem("get_video_network_identify", uuid);
};
$(function(){

  /*初始化*/
  var counter = 0; /*计数器*/
  var pageStart = 0; /*offset*/
  var pageSize = 10; /*size*/
  var isEnd = false;/*结束标志*/

  /*首次加载*/
  loadShortVideos(0, 0);

  /*监听加载更多*/
  $(window).scroll(function(){
   if (isLoadingMore === true) {
    return;
  }
  if(isEnd == true){
    return;
  }

    // 当滚动到最底部以上100像素时， 加载新内容
    // 核心代码
    if ($(document).height() - $(this).scrollTop() - $(this).height()<100){
      counter ++;
      pageStart = counter * pageSize;
      isLoadingMore = true;
      loadShortVideos(2, pageStart);
    }
  });
});

function parseNum(src){
  if (src === undefined) {
    src = 0;
  };
  var src = parseInt(src);
  if (src === 0) {
    src = "";
  };
  if (src === undefined || src === NaN) {
    src = "";
  } else {
    if (src > 1000000) {
      src = parseInt(src / 1000000);
      src = src + "M";
    } else if (src > 1000) {
      src = parseInt(src / 1000);
      src = src + "k";
    };
  };
  return src;
}

function loadShortVideos(status, index) {
  if (bottomRefreshImg.hidden === false) {
    return;
  };
  if (status === 2) {
    bottomRefreshImg.hidden = false;
  };
  var xhr = new XMLHttpRequest();
  xhr.open("GET", "http://47.88.9.138:6555/short-video/get-short-videos?index=" + index, true);
  xhr.onreadystatechange = function() {
    bottomRefreshImg.hidden = true;
    if (xhr.readyState == 4) {
      var currentVideoCount = $("video[videotag='video']").length;
      var resultObj = JSON.parse(xhr.responseText);
      resultObj = resultObj.result;
      var videoInfos = resultObj;
      var videoIds = new Array();
      for (let videoInfo of videoInfos) {
        var videoIndex = videoInfos.indexOf(videoInfo);
        var videoId = videoInfo.video_id;
        var relativeTag = videoId + '' + (currentVideoCount + videoIndex);
        videoIds[videoIds.length] = "video" + relativeTag;
        var videoUrl = videoInfo.video_cdn_url;
        if (videoUrl === undefined) {
          continue;
        };
        var shareUrl = videoInfo.share_url;
        var shareText = decodeURI(videoInfo.share_text);
        if (shareText.length > 200) {
          shareText = shareText.substr(0, 197) + "...";
        };
        var widthXHeight = videoInfo.width_height;
        widthXHeight = widthXHeight.split("x");
        var width = widthXHeight[0];
        var height = widthXHeight[1];
        var showHeight = height * showVideoWidth / width;
        var channel = videoInfo.channel;
        var videoDetail = videoInfo.video_details;
        var shareNumber = parseNum(videoDetail.share_number);
        
        var likedNumber = parseNum(videoDetail.liked_number);
        var playHeight = "50px";
        if(showHeight >= 400)
          playHeight = "90px";
        
        let div = document.createElement('div');
        div.style.display = "inline-block";
        var divInnerHTML = "";
        divInnerHTML += '<div><div style="margin-bottom:10px; font-size:16px; color:#555; word-wrap:break-word; width:'+ showVideoWidth + 'px;">' + shareText + '</div><div><button style="background:transparent;width:' + showVideoWidth + 'px;height:' + (showHeight - 54) + 'px;position: absolute;z-index: 1;border-width: 0px;" id="videoPlayButton' + relativeTag + '" buttonIndex="' + relativeTag + '"></button><button style="background:transparent;width:' + showVideoWidth + 'px;height:' + (showHeight - 54) + 'px;position: absolute;border-width: 0px;"><img src="images/refresh.gif" id="videoPlayImg' + relativeTag + '" style="width:'+playHeight+'"></button><video id="video' + relativeTag + '" videoIndex="' + relativeTag + '" style="z-index:-1;" controls="" width="' + showVideoWidth + '" height="' + showHeight + '" videotag="video"><source src="' + videoUrl + '" type="video/mp4"></video></div>';
        divInnerHTML += '<div style="text-align:center;margin-top:10px;color:#919191">';
        divInnerHTML += '<a style="color:#919191;font-size:13px;display:inline-block;text-align: center;width:32%;position:relative;">' + channel + '</a>|';
        divInnerHTML += '<a style="color:#919191;font-size:13px;display:inline-block;text-align: center;width:32%;position:relative;"><img style="position:  absolute;" src="images/like.jpg" width="20px" height="20px"><p style="margin-left: 20px;display:  inline;">' + likedNumber + '</p></a>|';
        divInnerHTML += '<a href="https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(shareUrl) + '&t=' + shareText + '" style="color:#919191;font-size:13px;display:inline-block;text-align: center;width:32%;position:relative;" target="_blank"><img style="position:  absolute;" src="images/share.jpg" width="20px" height="20px"><p style="margin-left: 20px;display:  inline;">' + shareNumber + '</p></a>';
        divInnerHTML += '</div>';
        divInnerHTML += '<div style="margin-top:10px;margin-bottom:10px;border-bottom:1px solid #ddd;"></div></div>';
        refreshImg.hidden = true;
        div.innerHTML += divInnerHTML;
        if (videosDiv2.offsetHeight > videosDiv1.offsetHeight) {
          videosDiv1.appendChild(div);
        } else {
          videosDiv2.appendChild(div);
        };
        $('#videoPlayButton' + relativeTag).unbind("click");
        $('#videoPlayButton' + relativeTag).click(playPause);

        var video = document.getElementById("video" + relativeTag);
        
        video.oncanplay = function(){
          var reloadImg = document.getElementById("videoPlayImg" + this.getAttribute("videoindex"));
          // reloadImg.src = "images/play.png";
          reloadImg.hidden = true;
        };
        video.onplaying = function(){
          var reloadImg = document.getElementById("videoPlayImg" + this.getAttribute("videoindex"));
          //reloadImg.src = "images/play.png";
          reloadImg.hidden = true;
        }
        // video.onloadedmetadata = function() {
        //   var reloadImg = document.getElementById("videoPlayImg" + this.getAttribute("videoindex"));
        //   reloadImg.src = "images/play.png";
        // };
        video.onloadstart = function() {
          var video = this;
          var reloadImg = document.getElementById("videoPlayImg" + video.getAttribute("videoindex"));
          reloadImg.hidden = false;
        };
        // video.onwaiting = function(){
        //   var reloadImg = document.getElementById("videoPlayImg" + this.getAttribute("videoindex"));
        //   reloadImg.src = "images/refresh.gif";
        //   reloadImg.hidden = false;
        // }
        function playPause() {
          var newButtonIndex = this.getAttribute("buttonIndex");
          var myVideo = document.getElementById('video' + newButtonIndex);
          var videoPlayImg = document.getElementById('videoPlayImg' + newButtonIndex);
          if(myVideo.paused) {
            if (currentVideo !== undefined) {
              var currentVideoIndex = currentVideo.getAttribute("videoIndex");
              var currentVideoPlayImg = document.getElementById('videoPlayImg' + currentVideoIndex);
              currentVideo.pause();
              currentVideoPlayImg.hidden = true;
            };
            videoPlayImg.hidden = true;
            myVideo.play();
            currentVideo = myVideo;
          }
          else {
            currentVideo = undefined;
            myVideo.pause();
            videoPlayImg.hidden = true;
          }
        }
      }
    }
    isLoadingMore = false;
  };
  xhr.send();
}