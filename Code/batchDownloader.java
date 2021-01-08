/*
 * 作者：沈格非
 * Author: Gefei Shen
 * 
 * 上传日期：2021/01/08
 * Release Date: Jan/08/2021
 * 
 * 一个以最高分辨率下载b站视频的软件
 * A program downloads video from Bilibili with maximum quality.
 * 
 * 本程序遵守GPL-2.0协议
 * This program is licensed under GPL-2.0.
 */

package bilibiliDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import org.json.JSONObject;

public class batchDownloader {
	private int up;
	private int quality;
	private File downloadDirectory;
	private downloader batch;
	
	public batchDownloader() {
		this.up = 114514;
		this.quality = 120;
		this.batch = new downloader();
	}
	
	protected void batchDownload(int up, int quality, File downloadDirectory) throws IOException {
		this.up = up;
		this.quality = quality;
		this.downloadDirectory = downloadDirectory;
		
		LinkedList<String> videoList = getVideolist(up,true);
		
		for (int i=0; i<videoList.size();i++) {
			batch.download(videoList.get(i), quality, downloadDirectory);
		}
	}
	
	/*
	 * all: true if needs all videos, false if latest page of videos only 
	 */
	private LinkedList<String> getVideolist(int up,boolean all){
		LinkedList<String> videoList = new LinkedList<String>();
		int page = 0;
		
		//JSON object that receives the video list.
		JSONObject resultJson;
		
		do {
			HttpURLConnection con = null;
			InputStream is = null;
			BufferedReader br = null;
			String result = null;
			page++;
			try {
				URL url = new URL("https://api.bilibili.com/x/space/arc/search?mid="+up+"&ps=30&tid=0&pn="+page+"&keyword=&order=pubdate&jsonp=jsonp");
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setConnectTimeout(5000);
				con.setReadTimeout(5000);
				con.connect();
				if (con.getResponseCode() == 200) {
					is = con.getInputStream();
					br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					StringBuffer sbf = new StringBuffer();
					String temp = null;
					while ((temp = br.readLine()) != null) {
						sbf.append(temp);
					}
					result = sbf.toString();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != br) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (null != is) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				con.disconnect();
			}
			resultJson = new JSONObject(result);
			
			System.out.println(resultJson.getJSONObject("data").getJSONObject("list").getJSONArray("vlist").length());
			for (int i = 0; i < resultJson.getJSONObject("data").getJSONObject("list").getJSONArray("vlist").length();i++) {
				videoList.add(resultJson.getJSONObject("data").getJSONObject("list").getJSONArray("vlist").getJSONObject(i).getString("bvid").substring(2));
			}
			
		} while (all == true && hasInfo(resultJson)); //quit the program if only fetch the latest information, or the reaches the end of video list.
		return videoList;
	}
	
	private boolean hasInfo(JSONObject test) {
		if (test.getJSONObject("data").getJSONObject("list").getJSONArray("vlist").length()>0) return true;
		return false;
	}
}
