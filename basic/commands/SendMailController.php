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
class SendMailController extends Controller
{
    /**
     * This command echoes what you have entered as the message.
     * @param string $message the message to be echoed.
     * @return int Exit code
     */
    public function actionIndex($username, $password)
    {
        $isSentEmailFilePath = "./runtime/sentEmails.json";
        //$sentCountFileDir = "./runtime/sentCount/{$username}";
        //$sentCountFileFile = $sentCountFileDir . "";
        if (!file_exists($isSentEmailFilePath)) {
            file_put_contents($isSentEmailFilePath, "[]");
        }
        $isSentEmailInfo = json_decode(file_get_contents($isSentEmailFilePath), true);
        $emailListStr = file_get_contents("./runtime/emailList1.txt");
        $emailList = explode("\r\n", $emailListStr);
        /*foreach ($emailList as $email) {
            if (in_array($email, $isSentEmailInfo)) {
                continue;
            }
            var_dump("\n" . $email . "\n");
            exec("./yii send-mail/send-mail '{$username}' '{$password}' '{$email}'", $output);
            $result = implode("", $output);
            if (stripos($result, "Message has been sent") === false) {
                return;
            }
            var_dump($result);
            $isSentEmailInfo[] = $email;
            file_put_contents($isSentEmailFilePath, json_encode($isSentEmailInfo));
            sleep(5);
        }*/
        $this->actionSendMail($username, $password, 'zxzasa1001@gmail.com');
    }

    public function actionSendMail($username, $password, $gmail) {

        //Load Composer's autoloader
        require('./commands/class.phpmailer.php');
        $mail = new \PHPMailer(true);                              // Passing `true` enables exceptions

        try {
            //Server settings
            $mail->SMTPDebug = 2;                                 // Enable verbose debug output
            $mail->isSMTP();  
            $mail->CharSet = 'UTF-8';                                    // Set mailer to use SMTP
            $mail->Host = 'smtp.office365.com';
            $mail->Host = 'smtp-mail.outlook.com';
            $mail->SMTPAuth = true;     
            $mail->Username = $username;
            $mail->Password = $password;                          // SMTP password
            //$mail->SMTPSecure = 'starttls';
            $mail->SMTPSecure = 'tls';
            //$mail->SMTPSecure = 'ssl';
            $mail->Port = 465;                                    // TCP port to connect to
            $mail->Port = 587;
            //$mail->Port = 25;

            //Recipients
            $mail->setFrom($username, 'PopTiner AD');
            //$mail->setFrom('zxzasa1212@163.com', 'wzz');
            $mail->addAddress($gmail);

            //Attachments
            // $mail->addAttachment('/var/tmp/file.tar.gz');         // Add attachments
            // $mail->addAttachment('/tmp/image.jpg', 'new.jpg');    // Optional name

            //Content
            $mail->isHTML(true);                                  // Set email format to HTML
            $mail->Subject = 'Funny videos: most popular short videos from Musical.ly, IFunny, Coup, …';
            $mail->Subject = 'Funny v1232353453i5d345e354o453s53:34 most 463p6o3463pula6r346 43short videos from Musical.ly, IFunny, Coup, …';
            //$mail->Subject = 'HI!';
            $mail->Body    = '<i>There are funny, interesting or shocking videos on various platforms here.</i><br>
            <i>Brushing one video after another to pass the time.</i><br>
            <i>Download an2y12412 42o355f 546t457h567e865 88abo5v23e52 videos.</i><br>
            <i>Good life!</i><br>
            <i>play google store: <a href="http://172.96.240.118/index.php?r=site/email-clicked&isToGooglePlay=1">https://play.google.com/store/apps/details?id=com.poptiner.zizhouwang</a></i><br>
            <i>Don\'t want to receive such emails anymore? <a href="http://172.96.240.118/index.php?r=site/email-clicked&isToGooglePlay=0&gmail=' . $gmail . '}">click here</a></i><br>
            <img src="http://172.96.240.118/poptinerImg/img1.png" />
            <img src="http://172.96.240.118/poptinerImg/img2.png" />
            <img src="http://172.96.240.118/poptinerImg/img3.png" />
            <img src="http://172.96.240.118/poptinerImg/img4.png" />
            <img src="http://172.96.240.118/poptinerImg/img5.png" />';
            //$mail->Body    = 'Good life!';
            // $mail->Body = ProcessImg($mail, $mail->Body);
            // var_dump($mail->Body);
            $mail->AltBody = "OK";

            $mail->send();
            echo 'Message has been sent';
        } catch (Exception $e) {
            echo 'Message could not be sent. Mailer Error: ', $mail->ErrorInfo;
        }
    }

    public function processImg($mail, $html){
        $resutl = preg_match_all('@<img src=.+? />@', $html, $matches);
        if(!$resutl) return $html;
        $trans = array();
        foreach ($matches[0] as $key => $img) {
            $id = 'img' . $key;
            preg_match('/src="(.*?)"/', $html, $path);
            if ($path[1]){
                $mail->addEmbeddedImage($path[1], $id);
                $trans[$img] = '<img src="cid:' . $id . '" />';
            }
        }
        $html = strtr($html, $trans);
        return $html;
    }
}
