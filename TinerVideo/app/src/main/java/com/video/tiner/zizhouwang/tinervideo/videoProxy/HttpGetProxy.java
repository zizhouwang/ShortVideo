package com.video.tiner.zizhouwang.tinervideo.videoProxy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

/**
 * 代理服务器类
 *
 * @author hellogv
 */
public class HttpGetProxy {
    final static public String TAG = "HttpGetProxy";
    /**
     * 链接带的端口
     */
    private int remotePort = -1;
    /**
     * 远程服务器地址
     */
    private String remoteHost;
    /**
     * 代理服务器使用的端口
     */
    private int localPort;
    /**
     * 本地服务器地址
     */
    private String localHost;
    private ServerSocket localServer = null;
    /**
     * 收发Media Player请求的Socket
     */
    private Socket sckPlayer = null;
    /**
     * 收发Media Server请求的Socket
     */
    private Socket sckServer = null;

    private SocketAddress address = null;

    /**
     * 下载线程
     */
    private DownloadThread download = null;
    public String[] targetAndLocalURL;
    public boolean isInitSuccess = false;
    private String url = null;
    private boolean isReadComp = false;
    private String needReadFilePath = null;

    /**
     * 初始化代理服务器
     *
     * @param localport 代理服务器监听的端口
     */
    public HttpGetProxy(int localport) {
        try {
            localPort = localport;
            localHost = C.LOCAL_IP_ADDRESS;
            localServer = new ServerSocket(localport, 1, InetAddress.getByName(localHost));
            isInitSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把URL提前下载在SD卡，实现预加载
     *
     * @param urlString
     * @return 返回预加载文件名
     * @throws Exception
     */
    public String prebuffer(String urlString, int size) throws Exception {
        if (download != null && download.isDownloading())
            download.stopThread(true);

        URI tmpURI = new URI(urlString);
        String filePath = ProxyUtils.urlToFilePath(tmpURI.getPath());

        download = new DownloadThread(urlString, filePath, size);
        download.startThread();

        return filePath;
    }

    /**
     * 把网络URL转为本地URL，127.0.0.1替换网络域名
     *
     * @param
     * @return [0]:重定向后MP4真正URL，[1]:本地URL
     */
    public String[] configLocalURL(String urlString) {

        // ----排除HTTP特殊----//
        String targetUrl = ProxyUtils.getRedirectUrl(urlString);
        url = targetUrl;
        // ----获取对应本地代理服务器的链接----//
        String localUrl = null;
        URI originalURI = URI.create(targetUrl);
        remoteHost = originalURI.getHost();
        if (originalURI.getPort() != -1) {// URL带Port
//            address = new InetSocketAddress(remoteHost, originalURI.getPort());// 使用默认端口
            remotePort = originalURI.getPort();// 保存端口，中转时替换
            localUrl = targetUrl.replace(
                    remoteHost + ":" + originalURI.getPort(), localHost + ":"
                            + localPort);
        } else {// URL不带Port
//            address = new InetSocketAddress(remoteHost, C.HTTP_PORT);// 使用80端口
            remotePort = -1;
            localUrl = targetUrl.replace(remoteHost, localHost + ":"
                    + localPort);
        }

        String[] result = new String[]{targetUrl, localUrl};
        targetAndLocalURL = result;
        return result;
    }

    /**
     * 异步启动代理服务器
     *
     * @throws IOException
     */
    public void asynStartProxy() {
        new Thread() {
            public void run() {
                startProxy();
            }
        }.start();
    }

    private void startProxy() {
        if (address == null) {
            String targetUrl = targetAndLocalURL[0];
            URI originalURI = URI.create(targetUrl);
            if (originalURI.getPort() != -1) {
                address = new InetSocketAddress(remoteHost, originalURI.getPort());// 使用默认端口
            } else {
                address = new InetSocketAddress(remoteHost, C.HTTP_PORT);// 使用80端口
            }
        }
        HttpParser httpParser = null;
        int bytes_read;
        boolean enablePrebuffer = false;//必须放在这里

        byte[] local_request = new byte[1024];
        byte[] remote_reply = new byte[1024];
        InputStream is = null;
        BufferedInputStream bin = null;
        HttpURLConnection con = null;

        while (true) {
            boolean hasResponseHeader = false;
            try {// 开始新的request之前关闭过去的Socket
                if (sckPlayer != null)
                    sckPlayer.close();
                if (sckServer != null)
                    sckServer.close();
            } catch (IOException e1) {
            }
            try {
                // --------------------------------------
                // 监听MediaPlayer的请求，MediaPlayer->代理服务器
                // --------------------------------------
                Log.v("start wait accept", "start wait accept");
                sckPlayer = localServer.accept();
                Log.e("TAG", "------------------------------------------------------------------");
//                if (download != null && download.isDownloading())
//                    download.stopThread(false);

                httpParser = new HttpParser(remoteHost, remotePort, localHost, localPort);

                HttpParser.ProxyRequest request = null;
                while ((bytes_read = sckPlayer.getInputStream().read(local_request)) != -1) {
                    String requestStr = new String(local_request);
                    byte[] buffer = httpParser.getRequestBody(local_request, bytes_read);
                    if (buffer != null) {
                        request = httpParser.getProxyRequest(buffer);
                    }
                    break;
                }

                String filePath = ProxyUtils.urlToFilePath(url);
                boolean isExists = new File(filePath).exists();
                if (request != null) {
                    enablePrebuffer = isExists && request._isReqRange0;//两者具备才能使用预加载
                } else {
                    enablePrebuffer = isExists;
                }
                Log.e(TAG, "enablePrebuffer:" + enablePrebuffer);
                boolean enableSendHeader = true;
                int mTotalSize = 0;
                URL url = null;
                int fileBufferSize = 0;
                if (enablePrebuffer) {//send prebuffer to mediaplayer
                    fileBufferSize = sendPrebufferToMP(filePath);
                    if (fileBufferSize > 0) {//重新发送请求到服务器
//                        request._body = httpParser.modifyRequestRange(request._body, fileBufferSize);
//                        Log.e(TAG + "-pre->", request._body);
                        enablePrebuffer = false;

                        // 下次不处理response的http header
//                        sentToServer(request._body);

                        enableSendHeader = false;
                        hasResponseHeader = false;
//                        continue;
                    }
                } else {
//                    Log.e(TAG + "-pre->", request._body);
//                    sentToServer(request._body);
                }
                // ------------------------------------------------------
                // 把网络服务器的反馈发到MediaPlayer，网络服务器->代理服务器->MediaPlayer
                // ------------------------------------------------------


                url = new URL(this.url);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("HEAD");
                mTotalSize = con.getContentLength();
                con.disconnect();
                Log.e(TAG, "mTotalSize:" + mTotalSize);
                int fileIndex = 1;
                while (true) {
                    String tempFilePath = filePath + "_" + fileIndex;
                    int endBufferSize = fileBufferSize + 1024 * 40;
                    if (endBufferSize > mTotalSize) {
                        break;
                    }
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Host", "img.ifcdn.com");
                    con.setRequestProperty("Connection", "keep-alive");
                    con.setRequestProperty("User-Agent", "stagefright/1.2 (Linux;Android 4.4.4)");
                    con.setRequestProperty("Accept-Encoding", "gzip,deflate");
                    con.setRequestProperty("Range", "bytes=" + fileBufferSize + "-" + endBufferSize);
                    con.connect();
                    int responseCode = con.getResponseCode();
                    Log.v("responseCode", "responseCode:" + responseCode);
                    if (responseCode == 206) {
                        bin = new BufferedInputStream(con.getInputStream());
                        fileBufferSize = streamSaveToFile(tempFilePath, bin, fileBufferSize);
                        Log.v("startOfflet", "startOfflet" + fileBufferSize);
//                        int len = 0;
//                        byte[] bs = new byte[2048];
//                        while (((len = bin.read(bs)) != -1)) {//未全部读取
//                            fileBufferSize += len;
//                            Log.v("startOfflet", "startOfflet" + fileBufferSize);
//                            sendToMP(bs);
//                        }
                        if (bin != null) {
                            bin.close();
                            bin = null;
                        }
                    }
                    con.disconnect();
                    con = null;
                    Log.v("start send", "start send");
                    sendPrebufferToMP(tempFilePath);
                    fileIndex++;
                }
//                BufferedInputStream bin = new BufferedInputStream(sckServer.getInputStream());
//                while (true) {
////                    Log.v("start", "start");
//                    bytes_read = bin.read(remote_reply);
////                    Log.v("read and read", "read and read");
//                    if (bytes_read == -1) {
////                        Log.v("没数据了", "没数据了");
//                        break;
//                    }
////                    Log.v("read success", "read success");
//                    byte[] tmpBuffer = new byte[bytes_read];
////                    Log.v("tmpBuffer init", "tmpBuffer init");
//                    System.arraycopy(remote_reply, 0, tmpBuffer, 0, tmpBuffer.length);
////                    Log.v("依然在读数据", "依然在读数据");
//                    if (hasResponseHeader) {
////                        Log.v("start sendToMP", "start sendToMP");
//                        sendToMP(tmpBuffer);
////                        Log.v("end sendToMP", "end sendToMP");
//                    } else {
//                        List<byte[]> httpResponse = httpParser.getResponseBody(remote_reply, bytes_read);
//                        if (httpResponse.size() > 0) {
//                            hasResponseHeader = true;
//                            String responseStr = new String(httpResponse.get(0));
//                            Log.e(TAG + "<---", responseStr);
//                            if (enableSendHeader) {
//                                // send http header to mediaplayer
//                                sendToMP(httpResponse.get(0));
//                            }
//
//                            //发送剩余数据
//                            if (httpResponse.size() == 2) {
//                                sendToMP(httpResponse.get(1));
//                            }
//                        }
//                    }
//                }
                Log.e(TAG, ".........over..........");

            } catch (Exception e) {
                Log.e(TAG, ProxyUtils.getExceptionMessage(e));
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bin != null) {
                    try {
                        bin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (con != null) {
                    con.disconnect();
                }
                // 关闭 2个SOCKET
                try {
                    if (sckPlayer != null) {
                        sckPlayer.close();
                        sckPlayer = null;
                    }
                    if (sckServer != null) {
                        sckServer.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int streamSaveToFile(String filePath, BufferedInputStream bin, int startOff) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            int len = 0;
            byte[] bs = new byte[2048];
            while (((len = bin.read(bs)) != -1)) {//未全部读取
                startOff += len;
                os.write(bs, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return startOff;
    }

    private int sendPrebufferToMP(String fileName) throws IOException {
        int fileBufferSize = 0;
        byte[] file_buffer = new byte[1 * 2048];
        int bytes_read = 0;
        FileInputStream fInputStream = new FileInputStream(fileName);
        while ((bytes_read = fInputStream.read(file_buffer)) != -1) {
            fileBufferSize += bytes_read;
            sendToMP(file_buffer);
        }
        fInputStream.close();

        Log.e(TAG, "读取完毕...下载:" + download.getDownloadedSize() + ",读取:" + fileBufferSize);
        return fileBufferSize;
    }

    private void sendToMP(byte[] bytes) throws IOException {
        try {
            sckPlayer.getOutputStream().write(bytes);
            sckPlayer.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sentToServer(String requestStr) throws IOException {
        try {
            if (sckServer != null)
                sckServer.close();
        } catch (Exception ex) {
        }
        sckServer = new Socket();
        sckServer.connect(address);
        sckServer.getOutputStream().write(requestStr.getBytes());// 发送MediaPlayer的请求
        sckServer.getOutputStream().flush();
    }
}