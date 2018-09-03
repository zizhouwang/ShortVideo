<?php
/**
 * @link http://www.yiiframework.com/
 * @copyright Copyright (c) 2008 Yii Software LLC
 * @license http://www.yiiframework.com/license/
 */

namespace app\commands;

use Yii;
use yii\console\Controller;
use yii\console\ExitCode;
use app\models\ShortVideo;

/**
 * This command echoes the first argument that you have entered.
 *
 * This command is provided as an example for you to learn how to create console commands.
 *
 * @author Qiang Xue <qiang.xue@gmail.com>
 * @since 2.0
 */
class HelloController extends Controller
{
    /**
     * This command echoes what you have entered as the message.
     * @param string $message the message to be echoed.
     * @return int Exit code
     */
    public function actionIndex($message = 'hello world')
    {
exec("./yii hello/test", $output);
var_dump($output);
return;
	Yii::error("sdfd", __METHOD__);
	$shortVideos = ShortVideo::find()->limit(100)->all();
	foreach ($shortVideos as $shortVideo) {
	    echo $shortVideo->id . "\n";
	}

        return ExitCode::OK;
    }

    public function actionTest() {
	var_dump(Yii::$app->db->createCommand("SELECT * FROM `short_video` WHERE id = 1")->queryOne());
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
                $oldShortVideos = ShortVideo::findBySql("SELECT id, video_channel_id, video_url, share_url, share_text, width_height, video_details FROM short_video WHERE id>{$id} LIMIT 100")->all();
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
                //file_put_contents("../noContent.txt", $shortVideoCount);
                if (count($oldShortVideos) === 0) {
                    continue;
                }
                $shortVideos = [];
                $shortVideoCount = count($oldShortVideos);
                if ($shortVideoCount < 20) {
                    $shortVideos = $oldShortVideos;
                } else {
                    $incre = intval($shortVideoCount / 20);
                    //file_put_contents("../incre.txt", $incre);
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
