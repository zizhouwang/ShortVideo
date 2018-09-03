<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "short_video".
 *
 * @property integer $id
 * @property integer $video_channel_id
 * @property string $video_url
 * @property string $video_cdn_url
 * @property string $video_identify_md5
 * @property string $video_image_url
 * @property string $share_url
 * @property string $share_text
 * @property string $width_height
 * @property integer $screen_type
 * @property integer $duration
 * @property integer $video_created_time
 * @property string $video_details
 * @property integer $os
 * @property integer $video_rating
 * @property string $created_at
 * @property string $updated_at
 */
class ShortVideo extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'short_video';
    }

    /**
     * @return \yii\db\Connection the database connection used by this AR class.
     */
    /*public static function getDb()
    {
        //return Yii::$app->get('mysql');
    }*/

    /**
     * @inheritdoc
     */
    /*public function rules()
    {
        return [
            [['video_channel_id', 'screen_type', 'duration', 'video_created_time', 'os', 'video_rating'], 'integer'],
            [['created_at', 'updated_at'], 'safe'],
            [['video_url', 'video_cdn_url', 'video_image_url', 'share_url', 'share_text', 'video_details'], 'string', 'max' => 1024],
            [['video_identify_md5', 'width_height'], 'string', 'max' => 32],
        ];
    }*/

    /**
     * @inheritdoc
     */
    /*public function attributeLabels()
    {
        return [
            'id' => Yii::t('api.models.viralpeta', 'ID'),
            'video_channel_id' => Yii::t('api.models.viralpeta', 'Video Channel ID'),
            'video_url' => Yii::t('api.models.viralpeta', 'Video Url'),
            'video_cdn_url' => Yii::t('api.models.viralpeta', 'Video Cdn Url'),
            'video_identify_md5' => Yii::t('api.models.viralpeta', 'Video Identify Md5'),
            'video_image_url' => Yii::t('api.models.viralpeta', 'Video Image Url'),
            'share_url' => Yii::t('api.models.viralpeta', 'Share Url'),
            'share_text' => Yii::t('api.models.viralpeta', 'Share Text'),
            'width_height' => Yii::t('api.models.viralpeta', 'Width Height'),
            'screen_type' => Yii::t('api.models.viralpeta', 'Screen Type'),
            'duration' => Yii::t('api.models.viralpeta', 'Duration'),
            'video_created_time' => Yii::t('api.models.viralpeta', 'Video Created Time'),
            'video_details' => Yii::t('api.models.viralpeta', 'Video Details'),
            'os' => Yii::t('api.models.viralpeta', 'Os'),
            'video_rating' => Yii::t('api.models.viralpeta', 'Video Rating'),
            'created_at' => Yii::t('api.models.viralpeta', 'Created At'),
            'updated_at' => Yii::t('api.models.viralpeta', 'Updated At'),
        ];
    }*/
}
