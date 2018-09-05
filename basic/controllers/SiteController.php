<?php

namespace app\controllers;

use Yii;
use yii\filters\AccessControl;
use yii\web\Controller;
use yii\web\Response;
use yii\filters\VerbFilter;
use app\models\LoginForm;
use app\models\ContactForm;
use app\models\ShortVideo;

class SiteController extends Controller
{
    public $enableCsrfValidation = false;

    public function init(){
        $this->enableCsrfValidation = false;
    }

    /**
     * {@inheritdoc}
     */
    /*public function behaviors()
    {
        return [
            'access' => [
                'class' => AccessControl::className(),
                'only' => ['logout'],
                'rules' => [
                    [
                        'actions' => ['logout'],
                        'allow' => true,
                        'roles' => ['@'],
                    ],
                ],
            ],
            'verbs' => [
                'class' => VerbFilter::className(),
                'actions' => [
                    'logout' => ['post'],
                ],
            ],
        ];
    }*/

    /**
     * {@inheritdoc}
     */
    /*public function actions()
    {
        return [
            'error' => [
                'class' => 'yii\web\ErrorAction',
            ],
            'captcha' => [
                'class' => 'yii\captcha\CaptchaAction',
                'fixedVerifyCode' => YII_ENV_TEST ? 'testme' : null,
            ],
        ];
    }*/

    public function actionEmailClicked($isToGooglePlay, $gmail = "") {
        $toGooglePlayCountFilePath = "../runtime/toGooglePlayCount.txt";
        $insCountFilePath = "../runtime/insCount.txt";
        $unsubscribeCountFilePath = "../runtime/unsubscribeCount.txt";
        $unsubscribeGmailsFilePath = "../runtime/unsubscribeGmails.json";
        if ($isToGooglePlay == 1) {
            $this->redirect('https://play.google.com/store/apps/details?id=com.poptiner.zizhouwang', 302);
            if (!file_exists($toGooglePlayCountFilePath)) {
                file_put_contents($toGooglePlayCountFilePath, 0);
            }
            $toGooglePlayCount = intval(file_get_contents($toGooglePlayCountFilePath));
            $toGooglePlayCount++;
            file_put_contents($toGooglePlayCountFilePath, $toGooglePlayCount);
        } else if ($isToGooglePlay == 0) {
            if (!file_exists($unsubscribeCountFilePath)) {
                file_put_contents($unsubscribeCountFilePath, 0);
            }
            $unsubscribeCount = intval(file_get_contents($unsubscribeCountFilePath));
            $unsubscribeCount++;
            file_put_contents($unsubscribeCountFilePath, $unsubscribeCount);

            if ($gmail !== "") {
                if (!file_exists($unsubscribeGmailsFilePath)) {
                    file_put_contents($unsubscribeGmailsFilePath, "[]");
                }
                $unsubscribeInfo = json_decode(file_get_contents($unsubscribeGmailsFilePath), true);
                $unsubscribeInfo[] = $gmail;
                file_put_contents($unsubscribeGmailsFilePath, json_encode($unsubscribeInfo));
            }

            return "unsubscribe success! :)";
        } else if ($isToGooglePlay == 2) {
            $this->redirect('https://play.google.com/store/apps/details?id=com.poptiner.zizhouwang', 302);
            if (!file_exists($insCountFilePath)) {
                file_put_contents($insCountFilePath, 0);
            }
            $insCount = intval(file_get_contents($insCountFilePath));
            $insCount++;
            file_put_contents($insCountFilePath, $insCount);
        }
    }

    public function actionReceiveError() {
        $request = Yii::$app->request;
        $errStr = $request->post("errStr");
        $currentTime = time();
        $random = rand(10000, 99999);
        file_put_contents("../runtime/logs/popTinerError/{$currentTime}_{$random}.txt", $errStr);
        return "";
    }

    /**
     * Displays homepage.
     *
     * @return string
     */
    public function actionIndex($screenType = 0, $channel = 0)
    {
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
                $sqlStr = "SELECT id, video_channel_id, video_url, video_image_url, share_url, share_text, width_height, video_details FROM short_video WHERE id>{$id}";
                if ($channel != 0) {
                    $sqlStr .= " AND video_channel_id={$channel}";
                }
                if ($screenType != 0) {
                    $sqlStr .= " AND screen_type={$screenType}";
                }
                $sqlStr .= " LIMIT 100";
                $oldShortVideos = ShortVideo::findBySql($sqlStr)->all();
                /*if ($channel == 0) {
                    $oldShortVideos = ShortVideo::findBySql("SELECT id, video_channel_id, video_url, video_image_url, share_url, share_text, width_height, video_details FROM short_video WHERE id>{$id} AND screen_type={$screenType} LIMIT 100")->all();
                } else {
                    $oldShortVideos = ShortVideo::findBySql("SELECT id, video_channel_id, video_url, video_image_url, share_url, share_text, width_height, video_details FROM short_video WHERE id>{$id} AND screen_type={$screenType} AND video_channel_id={$channel} LIMIT 100")->all();
                }*/
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
                foreach ($shortVideos as $shortVideo) {
                    $resultShortVideo = [];
                    $resultShortVideo['video_id'] = $shortVideo->id;
                    $ids[$index] = $resultShortVideo['video_id'];
                    $resultShortVideo['video_cdn_url'] = $shortVideo->video_url;
                    $resultShortVideo['share_url'] = $shortVideo->share_url;
		    $resultShortVideo['video_image_url'] = $shortVideo->video_image_url;
                    $resultShortVideo['share_text'] = json_decode($shortVideo->share_text, true);
                    if ($shortVideo->share_text === "") {
                        $resultShortVideo['share_text'] = "";
                    }
                    $resultShortVideo['width_height'] = $shortVideo->width_height;
                    $resultShortVideo['video_details'] = json_decode($shortVideo->video_details, true);
                    $videoChannelId = $shortVideo->video_channel_id;
                    //settype($videoChannelId, 'string');
                    $resultShortVideo['channel'] = $channelInfo[$videoChannelId];
                    $resultShortVideos[] = $resultShortVideo;
                }
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
	    $response['adWaitCount'] = 10;
        $response['resultCode'] = 1;
        $response['result'] = $resultShortVideos;
        return json_encode($response);
    }

    /**
     * Displays about page.
     *
     * @return string
     */
    public function actionAbout()
    {
        return $this->render('about');
    }
}
