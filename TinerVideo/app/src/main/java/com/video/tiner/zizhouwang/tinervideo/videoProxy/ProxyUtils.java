package com.video.tiner.zizhouwang.tinervideo.videoProxy;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.os.Environment;
import android.util.Log;

/**
 * 工具类
 * @author hellogv
 *
 */
public class ProxyUtils {	
	/**
	 * 获取重定向后的URL，即真正有效的链接
	 * @param urlString
	 * @return
	 */
    public static String getRedirectUrl(String urlString){
    	URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setInstanceFollowRedirects(false);
			if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_MOVED_PERM)
				return urlConnection.getHeaderField("Location");
			
			if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_MOVED_TEMP)
				return urlConnection.getHeaderField("Location");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return urlString;
    }
    
	public static String getExceptionMessage(Exception ex){
		String result="";
		StackTraceElement[] stes = ex.getStackTrace();
		for(int i=0;i<stes.length;i++){
			result=result+stes[i].getClassName() 
			+ "." + stes[i].getMethodName() 
			+ "  " + stes[i].getLineNumber() +"line"
			+"\r\n";
		}
		return result;
	}
	
	
	/**
	 * 异步清除过多的缓存文件
	 * @param dir 缓存文件的路径
	 * @param
	 */
	public static void clearCacheFile(final String dir) {
		File cacheDir = new File(dir);
		if (cacheDir.exists() == false) {
			return;
		}

		// 防止listFiles()导致ANR
		File[] files = cacheDir.listFiles();
		Log.e(dir,"共有"+files.length+"个缓存文件");
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		
	}
	
	static public String urlToFileName(String fileName)
    {
		String str=fileName;
        str=str.replace("\\","");
        str=str.replace("/","");
        str=str.replace(":","");
        str=str.replace("*","");
        str=str.replace("?","");
        str=str.replace("\"","");
        str=str.replace("<","");
        str=str.replace(">","");
        str=str.replace("|","");
        str=str.replace(" ","");    //前面的替换会产生空格,最后将其一并替换掉
        return str;
    }

    public static String urlToFilePath(String urlString) {
		try {
			URI tmpURI = new URI(urlString);
			String fileName = ProxyUtils.urlToFileName(tmpURI.getPath());
			String filePath = C.getBufferDir() + "/" + fileName;
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
