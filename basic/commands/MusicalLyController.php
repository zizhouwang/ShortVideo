<?php
namespace app\commands;
date_default_timezone_set('PRC');
use Yii;
use yii\console\Controller;
use app\models\ShortVideo;

class MusicalLyController extends Controller {
    public function actionIndex() {
        $musicalLyCurl = "curl -H 'X-Request-Info5: eyJvc3R5cGUiOiJpb3MiLCJvcyI6ImlPUyAxMS4zIiwiWC1SZXF1ZXN0LUlEIjoiMDM2QzBCQ0ItMTcwNC00RjczLTlGM0QtMjMxODM5ODc3MDI4Iiwic2xpZGVyLXNob3ctY29va2llIjoiVlVsRVh6RkRORVExUXpJNVJrSTNPVUl4TlVZNE5ETkVOVUU1TlRNNVJrVTJSakF4WDNGeFgzVnVhVzl1T2tWU2NrMU1ZVVZ6ZURCdmJtNTNSM0Z3Y0V0c01VRTlQVHBqWkdVeU1HSTVNek5qWkdabVpURTBZekJrTXpsaFlUTmtPREptWTJNMU9UbzJOVFF6TVRVMk1UTTJNakUwTnprMU1qYzUiLCJtZXRob2QiOiJHRVQiLCJkZXZpY2VpZCI6ImkwMzlkM2JlYTIxNzEzNGE5OGIyMWIyOTg2ZDc0NDhiOGQ2NSIsInZlcnNpb24iOiI2LjguMiIsInRpbWVzdGFtcCI6IjE1MjM0NDc5NzkwMDAiLCJ1cmwiOiJodHRwczpcL1wvYXBpLm11c2ljYWwubHlcL3Jlc3RcL211c2ljYWxzXC9leHBsb3JlXC9saXN0P2xpbWl0PTgmX19fZD1leUpoWXlJNklreEpVMVFpTENKaWVpSTZJbVY0Y0d4dmNtVmZaR2x6WTI5MlpYSjVJaXdpWkcwaU9pSk5WVk5KUTBGTUlpd2lkbVZ5SWpvaVpHVm1ZWFZzZENKOSZob3RLZXk9bW9kZWwmZGlzcGxheU1vZGU9MSJ9' -H 'build: 20180322001' -H 'slider-show-cookie: VUlEXzFDNEQ1QzI5RkI3OUIxNUY4NDNENUE5NTM5RkU2RjAxX3FxX3VuaW9uOkVSck1MYUVzeDBvbm53R3FwcEtsMUE9PTpjZGUyMGI5MzNjZGZmZTE0YzBkMzlhYTNkODJmY2M1OTo2NTQzMTU2MTM2MjE0Nzk1Mjc5' -H 'X-Request-Sign5: 01i6fdda67ba8996880438054fb1dad7d6257f9298a6'  --compressed 'https://api.musical.ly/rest/musicals/explore/list?limit=10&___d=eyJhYyI6IkxJU1QiLCJieiI6ImV4cGxvcmVfZGlzY292ZXJ5IiwiZG0iOiJNVVNJQ0FMIiwidmVyIjoiZGVmYXVsdCJ9&hotKey=model&displayMode=1'";
        $musicalLyCurl = "curl 'https://api2.musical.ly/aweme/v1/feed/?account_region=&device_id=6543155919369356805&feed_style=0&is_my_cn=1&os_version=11.3&filter_warn=0&mas=0182d051f4357831d0dca915a238673cb6557a1ddd623f368eecf7&iid=6586846865898178310&app_name=musical_ly&pull_type=1&ac=WIFI&max_cursor=0&sys_region=US&ts=1535123214&type=0&as=a1455178fe004b6fb01844&volume=0.25&version_code=2.4.1&vid=B99D13C2-3C19-4DE6-8735-B9C727669C07&channel=App%20Store&min_cursor=0&count=6&os_api=18&idfa=25412B5C-31D4-41FE-854D-FF555AD9455E&device_platform=iphone&device_type=iPhone8%2C4&openudid=e79745e58469919780f1c97d14da96621be429f6&tz_name=Asia%2FShanghai&tz_offset=28800&app_language=en&carrier_region=US&build_number=80105&aid=1233&mcc_mnc=46011&screen_width=640&language=zh&app_version=2.4.1.'";
        $musicalLyCurl = "curl -H 'User-Agent: Musically/8.0.1 (iPhone; iOS 11.3; Scale/2.00)' -H 'Host: api2.musical.ly' 'https://api2.musical.ly/aweme/v1/feed/?version_code=2.4.1&language=zh&app_name=musical_ly&vid=B99D13C2-3C19-4DE6-8735-B9C727669C07&app_version=2.4.1.&carrier_region=US&is_my_cn=1&channel=App%20Store&mcc_mnc=46011&device_id=6543155919369356805&tz_offset=28800&account_region=&sys_region=US&aid=1233&screen_width=640&openudid=e79745e58469919780f1c97d14da96621be429f6&os_api=18&ac=WIFI&os_version=11.3&app_language=en&tz_name=Asia/Shanghai&device_platform=iphone&build_number=80105&device_type=iPhone8,4&iid=6586846865898178310&idfa=25412B5C-31D4-41FE-854D-FF555AD9455E&count=6&feed_style=0&filter_warn=0&max_cursor=0&min_cursor=0&pull_type=0&type=0&volume=0.00&mas=011080455bfb384222de6fc39e5286379400fdf4756c6f035d5eb1&as=a145ca28a0f73b19cc5309&ts=1535945072'";
        unset($output);
        exec($musicalLyCurl, $output);
        $execResult = implode("", $output);
        //$execResult = file_get_contents("tempResult.txt");
        $result = json_decode($execResult, true);
        $musicalList = $result['aweme_list'];
        foreach ($musicalList as $videoInfo) {
            $videoInfoJson = json_encode($videoInfo);
            $videoInfoJsonMd5 = md5($videoInfoJson);
            $jsonFilePath = "./runtime/{$videoInfoJsonMd5}.json";
            file_put_contents($jsonFilePath, $videoInfoJson);
            $execStr = "./yii musical-ly/dealwith-one-musical-ly '{$jsonFilePath}' > null &";
            exec($execStr);
        }
    }

    public function actionDealwithOneMusicalLy($jsonFilePath) {
        $videoInfoJson = file_get_contents($jsonFilePath);
        unlink($jsonFilePath);
        try {
            $videoInfo = json_decode($videoInfoJson, true);
            $statistics = $videoInfo['statistics'];
            if (intval($statistics['digg_count']) < 1000) {
                $addRandLikeCount = rand(1001, 2000);
                $statistics['digg_count'] = intval($statistics['digg_count']) + $addRandLikeCount;
            }
            $musicalId = $videoInfo['aweme_id'];
            $musicalLocalPath = "./runtime/{$musicalId}_musicalLy.mp4";
            $videoIdentifyMd5 = md5($musicalId);
            $shortVideo = ShortVideo::find()->where("video_channel_id=12 and video_identify_md5='{$videoIdentifyMd5}'")->one();
            if (empty($shortVideo)) {
                $shortVideo = new ShortVideo();
                $shortVideo->video_channel_id = 12;
                $shortVideo->video_identify_md5 = $videoIdentifyMd5;
                $shortVideo->os = 1;
            }
            $videoURLInfo = $videoInfo['video'];
            $videoUrl = $videoURLInfo['play_addr']['url_list'][0];
            $shortVideo->video_url = $videoUrl;
            $originCoverList = $videoURLInfo['origin_cover']['url_list'];
            if (count($originCoverList) > 0) {
                $shortVideo->video_image_url = $originCoverList[0];
            }
            $shortVideo->share_url = $videoInfo['share_url'];
            $shortVideo->share_text = json_encode($videoInfo['desc']);
            $shortVideo->width_height = $videoURLInfo['width'] . "x" . $videoURLInfo['height'];
            if ($videoURLInfo['width'] > $videoURLInfo['height']) {
                $shortVideo->screen_type = 2;
            } else {
                $shortVideo->screen_type = 1;
            }
            $shortVideo->duration = intval($videoURLInfo['duration']);
            $shortVideo->video_created_time = intval($videoInfo['create_time']);
            $videoDetail = [];
            $videoDetail['share_number'] = $statistics['share_count'];
            $videoDetail['comment_number'] = $statistics['comment_count'];
            $videoDetail['liked_number'] = $statistics['digg_count'];
            $shortVideo->video_details = json_encode($videoDetail);
            $author = $videoInfo['author'];
            $authorUid = $author['uid'];
            $authorNickname = $author['nickname'];
            $authorUniqueId = $author['unique_id'];
            $shortVideo->author_uid = $authorUid;
            $shortVideo->author_nickname = json_encode($authorNickname);
            $shortVideo->author_unique_id = json_encode($authorUniqueId);
            //$tempRating = intval($videoInfo['likedNum']) * 0.01 + intval($videoInfo['commentNum']) + intval($videoInfo['shareNum']);
            $tempRating = (intval($videoDetail['liked_number']) + intval($videoDetail['comment_number'])) * 10 + intval($videoDetail['share_number']) * 100 + ($shortVideo->video_created_time - strtotime("2018-01-01")) / 86400;
            $shortVideo->video_rating = intval($this->calculateTwoMonthRating($shortVideo->video_created_time, $tempRating));
            if ($shortVideo->video_rating < 10000) {
                //return false;
            }
        } catch (\Exception $e) {
            Yii::error($videoInfoJson, __METHOD__);
            Yii::error($e, __METHOD__);
            if (file_exists($musicalLocalPath)) {
                unlink($musicalLocalPath);
            }
            return false;
        }
        if (!$shortVideo->save()) {
            Yii::error($shortVideo->errors, __METHOD__);
        }
    }

    public function calculateTwoMonthRating($createdTime, $rating) {
        return $rating;
        $createdTime /= 1000;
        $twoMonthTime = 86400 * 60;
        $diffTime = intval(time()) - intval($createdTime);
        if ($diffTime < $twoMonthTime) {
            $rating = $rating * $twoMonthTime / $diffTime;
        }
        return intval($rating);
    }

    public function actionRepairZeroSizeVideo() {
        $shortVideos = ShortVideo::find()->where("width_height='0x0'")->all();
        foreach ($shortVideos as $shortVideo) {
            $videoImageUrl = $shortVideo->video_image_url;
            $curl = "curl -o temp.jpg '{$videoImageUrl}'";
            exec($curl);
            $imgInfo = getimagesize('temp.jpg');
            $shortVideo->width_height = $imgInfo[0] . "x" . $imgInfo[1];
            $shortVideo->save();
        }
    }

    public function actionRepairScreenType() {
        $shortVideos = ShortVideo::find()->where("screen_type=0 and video_channel_id=12")->each(500);
        foreach ($shortVideos as $shortVideo) {
            var_dump($shortVideo->id);
            $widthHeight = $shortVideo->width_height;
            $sizeInfo = explode("x", $widthHeight);
            $width = intval($sizeInfo[0]);
            $height = intval($sizeInfo[1]);
            if ($width > $height) {
                $shortVideo->screen_type = 2;
            } else if ($width < $height) {
                $shortVideo->screen_type = 1;
            } else {
                $shortVideo->screen_type = 3;
            }
            $shortVideo->save();
        }
    }

    public function actionDeleteLessLikeVideo() {
        $id = 0;
        while (true) {
            $shortVideos = ShortVideo::find()->where("id>{$id}")->limit(100)->all();
            foreach ($shortVideos as $shortVideo) {
                var_dump($shortVideo->id);
                $id = $shortVideo->id;
                $videoDetail = json_decode($shortVideo->video_details, true);
                if (!isset($videoDetail['liked_number'])) {
                    $shortVideo->delete();
                    var_dump("delete" . $shortVideo->id);
                    continue;
                } else {
                    $likeCount = intval($videoDetail['liked_number']);
                    if ($likeCount === 0 || $likeCount === 1000) {
                        $shortVideo->delete();
                        var_dump("delete" . $shortVideo->id);
                        continue;
                    }
                    if ($likeCount < 1000) {
                        $likeCount += 1000;
                        $videoDetail['liked_number'] = $likeCount;
                        $shortVideo->video_details = json_encode($videoDetail);
                        $shortVideo->save();
                        var_dump("add 1000" . $shortVideo->id);
                    }
                }
            }
        }
    }

    public function actionTest() {
        /*$channelsJson = '{"0":"","100":"","200":"","300":"","310":"","311":"","312":"","404":"","2017":"","10000":"","10005":"","10006":"","10007":"","10008":"","10012":"","10013":"","10014":"","10016":"","10020":"","10021":"","10022":"","10023":"","10024":"","10025":"","10026":"","10027":"","10028":"","10029":"","10030":"","10031":"","10032":"","10033":"","10034":"","10035":"","10036":"","10037":"","10038":"","10039":"","10040":"","10041":"","10042":"","10043":"","10046":"","10048":"","10050":"","10051":"","10073":"","10081":"","10082":"","10083":"","10084":"","10085":"","10086":"","10087":"","10091":"","10092":"","10093":"","10094":"","10100":"","10101":"","10102":"","10103":"","10104":"","10105":"","10106":"","10107":"","10108":"","10110":"","10112":"","10118":"","10120":"","10122":"","10123":"","10124":"","10133":"","10134":"","10135":"","10136":"","10138":"","10139":"","10140":"","10149":"","10150":"","10151":"","10153":"","10154":"","10155":"","10156":"","10157":"","10158":"","10159":"","10161":"","10190":"","10191":"","10198":"","10200":"","10232":"","10233":"","10235":"","10236":"","10237":"","10238":"","10239":"","10245":"","10246":"","10247":"","10257":"","10258":"","10259":"","10261":"","10262":"","10263":"","10264":"","10265":"","10267":"","10281":"","10282":"","10286":"","10287":"","10288":"","10289":"","10291":"","10292":"","10293":"","10294":"","10295":"","10296":"","10297":"","10299":"","10303":"","10306":"","10307":"","10308":"","10309":"","10310":"","10314":"","10317":"","10318":"","10319":"","10320":"","10321":"","10322":"","10323":"","10324":"","10325":"","10326":"","10327":"","10328":"","10329":"","10330":"","10331":"","10332":"","10333":"","10334":"","10335":"","10336":"","10337":"","10338":"","10339":"","10341":"","10342":"","10343":"","16166":"","17427":"","20000":"","29307":"","34041":"","49518":"","59532":"","64422":"","66498":"","74706":"","89010":"","90001":"","90002":"","90003":"","90004":"","90005":"","91888":"","100000":"","100090":""}';
        $channels = json_decode($channelsJson, true);
        foreach ($channels as $channel => $value) {
            $scrapyNewsfeedAdTasks =  ScrapyNewsfeedAdTask::findBySql("select * from scrapy_newsfeed_ad_task where channel=12 and is_available=1 and request_details like '%/{$channel}?%'")->one();
            if (empty($scrapyNewsfeedAdTasks)) {
                var_dump($channels);
                $url = "http://iflow.uczzd.cn/iflow/api/v1/channel/{$channel}?app=uc-iflow&sp_gz=4&recoid=16084568911326171908&ftime=1531904586459&method=new&count=20&no_op=0&auto=0&content_ratio=0&_tm=1531904600445&scene=0&earphone=0&moving=0&puser=1&enable_ad=1&uicid=AAMqE7GbQbYmvqqbeEwLd5Ce%2Bf4QV8KVn6%2BXaJypXDJr3qN1O91Sx4waA13k6T90m%2F8%3D&ressc=12&uc_param_str=dnnivebichfrmintcpgimewidsudsvliss&dn=35765747151-f11c3c44&nn=AAStxSpt1neyHlvSJQ9fg7Cz0LXhfMzYqKd4%2BBASshUmJA%3D%3D&ve=12.0.5.1082&bi=997&fr=iphone&mi=iPhone8%2C4&nt=2&pc=AARdsNNdqjm0lF5k%2FXNfqltVEOaAfUULPO01ZRvXGr5GFX7RMWU1nvJdLbdME%2FKeE7%2FN3aXDWdMxW2E4gsJI3OJT&gp=AAQMJllI0GYH%2Ftl%2F74zqP%2FnB63lbl4VidLZ5kS7zDusgkA%3D%3D&me=AASxEIljFlkkrsq%2Br%2Bj0iOEqRoJsL%2F2al%2F1miZ%2BgCcXb4ew7bP%2BeRbYk467K04iiOwo%3D&ut=AATql1xzHMrJ2pSC0GiTmr0vxuWddFxVq4S0CVE%2BkXVYWQ%3D%3D&ai=AAQ%3D&sv=appa&lb=AASrz%2FMEWj7ku0N1TmCcLAWscGTrGGo%2Bp5bT3bRfEXZoig%3D%3D&ss=320x568";
                $requestDetail = '{"url":"' . $url . '","header":{"Host":"iflow.uczzd.cn","User-Agent":"Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_1 like Mac OS X; zh-CN) AppleWebKit/537.51.1 (KHTML, like Gecko) Mobile/15C153 UCBrowser/11.8.0.1042 Mobile  AliApp(TUnionSDK/0.1.20)","Accept-Language":"zh-cn"},"body":null,"formdata":null,"os":1}';
                $newScrapyNewsfeedAdTask = new ScrapyNewsfeedAdTask();
                $newScrapyNewsfeedAdTask->channel = 12;
                $newScrapyNewsfeedAdTask->request_details = $requestDetail;
                $newScrapyNewsfeedAdTask->is_available = 1;
                $newScrapyNewsfeedAdTask->source_app = 'uc浏览器';
                $newScrapyNewsfeedAdTask->save();
            }
        }

        return;*/
        $scrapyNewsfeedAdTasks =  ScrapyNewsfeedAdTask::find()->where("id=407 and channel=12 and is_available=4")->all();
        $citysJson = '["沈阳","长春","哈尔滨","南京","武汉","广州","成都","西安","石家庄","唐山","太原","大连","鞍山","抚顺","吉林","齐齐哈尔","徐州","杭州","福州","南昌","济南","青岛","淄博","郑州","长沙","贵阳","昆明","兰州","邯郸","保定","张家口","大同","本溪","丹东","锦州","阜新","辽阳","鸡西","鹤岗","大庆","伊春","佳木斯","牡丹江","无锡","常州","苏州","宁波","合肥","淮南","淮北","厦门","枣庄","烟台","潍坊","泰安","临沂","开封","洛阳","平顶山","安阳","新乡","焦作","黄石","襄樊","荆州","株洲","湘潭","衡阳","深圳","汕头","湛江","西宁","秦皇岛","邢台","承德","沧州","廊坊","衡水","阳泉","长治","营口","盘锦","铁岭","朝阳","葫芦岛","四平","辽源","通化","白山","松原","白城","双鸭山","七台河","南通","连云港","淮阴","盐城","扬州","镇江","泰州","温州","嘉兴","湖州","绍兴","台州","芜湖","蚌埠","马鞍山","铜陵","安庆","阜阳","泉州","漳州","南平","龙岩","景德镇","萍乡","九江","新余","东营","济宁","威海","日照","莱芜","德州","鹤壁","濮阳","许昌","漯河","南阳","商丘","十堰","宜昌","鄂州","荆门","孝感","黄冈","邵阳","岳阳","常德","益阳","郴州","永州","怀化","韶关","珠海","佛山","江门","茂名","肇庆","惠州","梅州","阳江","东莞","中山","潮州","海口","自贡","攀枝花","泸州","德阳","绵阳","广元","遂宁","内江","乐山","南充","宜宾","六盘水","遵义","曲靖","铜川","宝鸡","咸阳","汉中","白银","天水","晋城","朔州","黑河","宿迁","金华","衢州","舟山","黄山","滁州","宿州","巢湖","六安","莆田","三明","鹰潭","赣州","聊城","三门峡","信阳","咸宁","张家界","娄底","汕尾","河源","清远","揭阳","云浮","三亚","达州","玉溪","渭南","延安","榆林","嘉峪关","金昌","辛集","藁城","晋州","新乐","鹿泉","遵化","丰南","迁安","武安","南宫","沙河","涿州","定州","安国","高碑店","泊头","任丘","黄骅","河间","霸州","三河","冀州","深州","古交","潞城","高平","忻州","原平","孝义","离石","汾阳","榆次","介休","临汾","侯马","霍州","运城","永济","河津","新民","瓦房店","普兰店","庄河","海城","东港","凤城","凌海","北宁","盖州","大石桥","灯塔","铁法","开原","北票","凌源","兴城","九台","榆树","德惠","蛟河","桦甸","舒兰","磐石","公主岭","双辽","梅河口","集安"';
        $citysJson .= ',"临江","洮南","大安","延吉","图们","敦化","珲春","龙井","和龙","阿城","双城","尚志","五常","讷河","虎林","密山","铁力","同江","富锦","绥芬河","海林","宁安","穆棱","北安","五大连池","绥化","安达","肇东","海伦","江阴","宜兴","锡山","新沂","邳州","溧阳","金坛","武进","常熟","张家港","昆山","吴江","太仓","吴县","启东","如皋","通州","海门","淮安","东台","大丰","仪征","高邮","江都","丹阳","扬中","句容","兴化","靖江","泰兴","姜堰","萧山","建德","富阳","余杭","临安","余姚","慈溪","奉化","瑞安","乐清","海宁","平湖","桐乡","诸暨","上虞","嵊州","兰溪","义乌","东阳","永康","江山","温岭","临海","丽水","龙泉","桐城","天长","明光","亳州","界首","宣州","宁国","贵池","福清","长乐","永安","石>狮","晋江","南安","龙海","邵武","武夷山","建瓯","建阳","漳平","宁德","福安","福鼎","乐平","瑞昌","贵溪","瑞金","南康","宜春","丰城","樟树","高安","上饶","德兴","吉安","井冈山","临川","章丘","胶州","即墨","平度","胶南","莱西","滕州","龙口","莱阳","莱州","蓬莱","招远","栖霞","海阳","青州","诸城","寿光","安丘","高密","昌邑","曲阜","兖州","邹城","新泰","肥城","文登","荣成","乳山","乐陵","禹城","临清","滨州","菏泽","巩义","荥阳","新密","新郑","登封","偃师","舞钢","汝州","林州","卫辉","辉县","济源","沁阳","孟州","禹州","长葛","义马","灵宝","邓>州","永城","周口","项城","驻马店","大冶","丹江口","枝城","当阳","枝江","老河口","枣阳","宜城","钟祥","应城","安陆","广水","汉川","石首","洪湖","松滋","麻城","武穴","恩施","利川"';
        $citysJson .= ',"随州","仙桃","潜江","天门","浏阳","醴陵","湘乡","韶山","耒阳","常宁","武冈","汩罗","临湘","津市","沅江","资兴","洪江","冷水江","涟源","吉首","番禺","花都","增城","从化","乐昌","南雄","潮阳","澄海","顺德","南海","三水","高明","台山","新会","开平","鹤山","恩平","廉江","雷州","吴川","高州","化州","信宜","高要","四会","惠阳","兴宁","陆丰","阳春","英德","连州","普宁","罗定","通什","琼海","儋州","琼山","文昌","万宁","东方","都江堰","彭州","邛崃","崇州","广汉","什邡","绵竹","江油","峨眉山","阆中","华蓥","万源","雅安","西昌","巴中","资阳","简阳","清镇","赤水","仁怀","铜仁","兴义","毕节","安顺","凯里","都匀","福泉","安宁","宣威","昭通",">楚雄","个旧","开远","思茅","景洪","大理","保山","瑞丽","潞西","兴平","韩城","华阴","安康","商州","玉门","酒泉","敦煌","张掖","武威","平凉","西峰","临夏","合作","格尔木","德令哈","北京","天津","上海","重庆"]';
        $citys = json_decode($citysJson, true);
        $errorCityEncode = urlencode("北京123");
        foreach ($scrapyNewsfeedAdTasks as $scrapyNewsfeedAdTask) {
            $requestDetails = json_decode($scrapyNewsfeedAdTask->request_details, true);
            $url = $requestDetails['url'];
            $url = $url . "&city_name={$errorCityEncode}";
            unset($output);
            $curlStr = "curl -H 'Host: iflow.uczzd.cn' -H 'User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X; zh-CN) AppleWebKit/537.51.1 (KHTML, like Gecko) Mobile/15E216 UCBrowser/12.0.5.1082 Mobile  AliApp(TUnionSDK/0.1.20.3)' -H 'Accept-Language: zh-cn' --compressed '{$url}'";
            exec($curlStr, $output);
            $result = json_decode(implode("", $output), true);
            if ($result['status'] == 0) {
                continue;
            }
            foreach ($citys as $city) {
                $requestDetails = json_decode($scrapyNewsfeedAdTask->request_details, true);
                $cityEncode = urlencode($city);
                $url = $requestDetails['url'];
                $url = $url . "&city_name={$cityEncode}";
                unset($output);
                $curlStr = "curl -H 'Host: iflow.uczzd.cn' -H 'User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X; zh-CN) AppleWebKit/537.51.1 (KHTML, like Gecko) Mobile/15E216 UCBrowser/12.0.5.1082 Mobile  AliApp(TUnionSDK/0.1.20.3)' -H 'Accept-Language: zh-cn' --compressed '{$url}'";
                exec($curlStr, $output);
                $result = json_decode(implode("", $output), true);
                if ($result['status'] == 0) {
                    $requestDetails['url'] = $url;
                    $newScrapyNewsfeedAdTask = new ScrapyNewsfeedAdTask();
                    $newScrapyNewsfeedAdTask->channel = 12;
                    $newScrapyNewsfeedAdTask->request_details = json_encode($requestDetails, JSON_UNESCAPED_SLASHES);
                    $newScrapyNewsfeedAdTask->is_available = 1;
                    $newScrapyNewsfeedAdTask->source_app = 'uc浏览器';
                    $newScrapyNewsfeedAdTask->save();
                }
            }
        }
        return;
        $allTime = 0;
        $time1 = intval(time());
        $minId = 1;
        $maxId = 214565;
        $partDataCount = ($maxId - $minId) / 2;
        $randTime1 = intval(time());
        $id1 = rand($minId, $maxId);
        $randTime2 = intval(time());
        $randTime = $randTime2 - $randTime1;
        $ids = [$id1];
        $resultShortVideos = [];
        $channelInfo = Yii::$app->params['channel_info'];
        $findTimes = 0;
        $currentVideoCount = 0;
        $sqlTime = 0;
        while (true) {
            foreach ($ids as $index => $id) {
                $sqlTime1 = intval(time());
                $oldShortVideos = ShortVideo::findBySql("SELECT id, video_channel_id, video_url, share_url, share_text, width_height, video_details FROM short_video WHERE id>{$id} and video_channel_id=10 LIMIT 100")->all();
                $sqlTime2 = intval(time());
                $sqlTime += $sqlTime2 - $sqlTime1;
                if (count($oldShortVideos) === 0) {
                    $id1 = rand($minId, $partDataCount);
                    $id2 = rand($partDataCount, $maxId);
                    $ids = [$id1, $id2];
                    continue;
                }
                $oldShortVideos = array_merge($oldShortVideos);
                $shortVideoCount = count($oldShortVideos);
                file_put_contents("../noContent.txt", $shortVideoCount);
                if (count($oldShortVideos) === 0) {
                    continue;
                }
                $shortVideos = [];
                $shortVideoCount = count($oldShortVideos);
                if ($shortVideoCount < 20) {
                    $shortVideos = $oldShortVideos;
                } else {
                    $incre = intval($shortVideoCount / 20);
                    file_put_contents("../incre.txt", $incre);
                    $shortVideoIndex = 0;
                    for ($i = 0; $i < 20; $i++) {
                        if ($shortVideoIndex == 0) {
                            $shortVideos[] = $oldShortVideos[0];
                        } else {
                            $shortVideos[] = $oldShortVideos[$shortVideoIndex-1];
                        }
                        $shortVideoIndex += $incre;
                        if ($incre === 1) {
                            $shortVideoIndex++;
                            $i++;
                        }
                    }
                }
                var_dump(count($shortVideos));
                foreach ($shortVideos as $shortVideo) {
                    $resultShortVideo = [];
                    $resultShortVideo['video_id'] = $shortVideo->id;
                    $ids[$index] = $resultShortVideo['video_id'];
                    $resultShortVideo['video_cdn_url'] = $shortVideo->video_url;
                    $resultShortVideo['share_url'] = $shortVideo->share_url;
                    $resultShortVideo['share_text'] = json_decode($shortVideo->share_text, true);
                    if ($shortVideo->share_text === "") {
                        $resultShortVideo['share_text'] = "";
                    }
                    $resultShortVideo['width_height'] = $shortVideo->width_height;
                    $resultShortVideo['video_details'] = json_decode($shortVideo->video_details, true);
                    $resultShortVideo['channel'] = $channelInfo[$shortVideo->video_channel_id];
                    $resultShortVideos[] = $resultShortVideo;
                }
                var_dump(count($resultShortVideos));
            }
            break;
            if (count($resultShortVideos) === $currentVideoCount) {
                continue;
            }
            $currentVideoCount = count($resultShortVideos);
            $findTimes++;
            if ($findTimes > 3) {
                break;
            }
            if (count($resultShortVideos) >= 10) {
                break;
            }
        }
        $time2 = intval(time());
        $allTime += $time2 - $time1;
        $response = [];
        $response['resultCode'] = 1;
        $response['result'] = $resultShortVideos;
        var_dump($response);
    }
}
/* vim: set ts=4 sw=4 sts=4 tw=100 */
?>
