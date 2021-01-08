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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;

public class downloader{
	
	private File downloadDirectory;
	
	private JSONArray bvArray;
	private JSONObject avJson;
	private JSONObject videoJson;
	private ArrayList<quality> qualityList;
	
	private String bv;
	private int up;
	private int quality;
	private int aid;

	public downloader() {
		avJson = new JSONObject("{\n"
				+ "  \"code\": 0,\n"
				+ "  \"message\": \"0\",\n"
				+ "  \"ttl\": 1,\n"
				+ "  \"data\": {\n"
				+ "    \"bvid\": \"spaceholder\",\n"
				+ "    \"aid\": 81,\n"
				+ "    \"videos\": 1,\n"
				+ "    \"tid\": 1,\n"
				+ "    \"tname\": \"spaceholder\",\n"
				+ "    \"copyright\": 1,\n"
				+ "    \"pic\": \"sapceholder\",\n"
				+ "    \"title\": \"spaceholder\",\n"
				+ "    \"pubdate\": 0,\n"
				+ "    \"ctime\": 0,\n"
				+ "    \"desc\": \"sapceholder\",\n"
				+ "    \"state\": 0,\n"
				+ "    \"duration\": 486,\n"
				+ "    \"rights\": {\n"
				+ "      \"bp\": 0,\n"
				+ "      \"elec\": 0,\n"
				+ "      \"download\": 1,\n"
				+ "      \"movie\": 0,\n"
				+ "      \"pay\": 0,\n"
				+ "      \"hd5\": 1,\n"
				+ "      \"no_reprint\": 1,\n"
				+ "      \"autoplay\": 1,\n"
				+ "      \"ugc_pay\": 0,\n"
				+ "      \"is_cooperation\": 0,\n"
				+ "      \"ugc_pay_preview\": 0,\n"
				+ "      \"no_background\": 0,\n"
				+ "      \"clean_mode\": 0,\n"
				+ "      \"is_stein_gate\": 0\n"
				+ "    },\n"
				+ "    \"owner\": {\n"
				+ "      \"mid\": 0,\n"
				+ "      \"name\": \"spaceholder\",\n"
				+ "      \"face\": \"spaceholder\"\n"
				+ "    },\n"
				+ "    \"stat\": {\n"
				+ "      \"aid\": 0,\n"
				+ "      \"view\": 0,\n"
				+ "      \"danmaku\": 0,\n"
				+ "      \"reply\": 0,\n"
				+ "      \"favorite\": 0,\n"
				+ "      \"coin\": 0,\n"
				+ "      \"share\": 0,\n"
				+ "      \"now_rank\": 0,\n"
				+ "      \"his_rank\": 0,\n"
				+ "      \"like\": 0,\n"
				+ "      \"dislike\": 0,\n"
				+ "      \"evaluation\": \"\",\n"
				+ "      \"argue_msg\": \"\"\n"
				+ "    },\n"
				+ "    \"dynamic\": \"spaceholder\",\n"
				+ "    \"cid\": 0,\n"
				+ "    \"dimension\": {\n"
				+ "      \"width\": 3840,\n"
				+ "      \"height\": 2160,\n"
				+ "      \"rotate\": 0\n"
				+ "    },\n"
				+ "    \"no_cache\": false,\n"
				+ "    \"pages\": [\n"
				+ "      {\n"
				+ "        \"cid\": 0,\n"
				+ "        \"page\": 1,\n"
				+ "        \"from\": \"vupload\",\n"
				+ "        \"part\": \"sapceholder\",\n"
				+ "        \"duration\": 0,\n"
				+ "        \"vid\": \"\",\n"
				+ "        \"weblink\": \"\",\n"
				+ "        \"dimension\": {\n"
				+ "          \"width\": 3840,\n"
				+ "          \"height\": 2160,\n"
				+ "          \"rotate\": 0\n"
				+ "        }\n"
				+ "      }\n"
				+ "    ],\n"
				+ "    \"subtitle\": {\n"
				+ "      \"allow_submit\": false,\n"
				+ "      \"list\": []\n"
				+ "    },\n"
				+ "    \"user_garb\": {\n"
				+ "      \"url_image_ani_cut\": \"\"\n"
				+ "    }\n"
				+ "  }\n"
				+ "}");
	}
		
	private class quality{
		int num;
		String nme;
		String vid;
		String aud;
		public quality(int num, String nme, String vid, String aud) {
			this.num = num;
			this.nme = nme;
			this.vid = vid;
			this.aud = aud;
		}
		
		public int getNum() {
			return this.num;
		}
		
		public String getNme() {
			return this.nme;
		}
		
		public String getVid() {
			return this.vid;
		}
		
		public String getAud() {
			return this.aud;
		}
	}
	
	protected void download(String bv,int quality, File downloadDirectory) throws IOException {
		this.bv = bv;
		this.quality = quality;
		this.downloadDirectory = downloadDirectory;
		getCidJson(bv);
		getAidJson(getCid(0));
		for (int i=0;i<getEpisode();i++) {
			downloadVideo(i);
		}
	}
	

	
	private void getCidJson(String bv) {
		HttpURLConnection con = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;
		try {
			URL url = new URL("https://api.bilibili.com/x/player/pagelist?bvid=" + bv);
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
		JSONObject resultJson = new JSONObject(result);
		
		this.bvArray = resultJson.getJSONArray("data");
	}
	
	private int getEpisode() {
		//dataArray.
		return bvArray.length();
	}
	
	private int getCid(int epi) {
		return bvArray.getJSONObject(epi).getInt("cid");
	}
	
	private String getSubTitle(int epi) {
		return bvArray.getJSONObject(epi).getString("part");
	}
	
	private void getAidJson(int cid) {
		HttpURLConnection con = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;
		try {
			URL url = new URL("https://api.bilibili.com/x/web-interface/view?cid="+cid+"&bvid="+bv);
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
		avJson = new JSONObject(result);
	}
	
	private int getAid() {
		return avJson.getJSONObject("data").getInt("aid");
	}
	
	public String getTitle() {
		return avJson.getJSONObject("data").getString("title");
	}
	
	private String getAuthor() {
		return avJson.getJSONObject("data").getJSONObject("owner").getString("name");
	}
	
	private void getDownloadLink(int cid, int aid) {
		qualityList = new ArrayList<quality>();
		HttpURLConnection con = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;
		try {
//			URL url = new URL("https://api.bilibili.com/x/player/playurl?cid="+cid+"&bvid=BV"+bv+"&qn=16&type=&otype=json&fourk=1&fnver=0&fnval=80");
			URL url = new URL("https://api.bilibili.com/x/player/playurl?cid="+cid+"&bvid=BV"+bv+"&qn=16&type=&otype=json&fourk=1&fnver=0&fnval=80");
			con = (HttpURLConnection) url.openConnection();
			//TODO copy your cookie below.
			//con.setRequestProperty("cookie",);
			con.setRequestProperty("cookie", "PVID=2;sid=hwa2aziw;bsource=seach_google;CURRENT_QUALITY=120;SESSDATA=56cea56a%2C1624634622%2Cc9598*c1;bili_jct=acf9662b41993df346deb3e20d9c87d7;DedeUserID=14118102;finger=158939783;rpdid=	|(uum~u|kk)k0J'uY|m|RR|)m;blackside_state=1;DedeUserID__ckMd5=cee4814c9ded7ad0");
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
		
		videoJson = new JSONObject(result);
		
		for (int i=0; i<videoJson.getJSONObject("data").getJSONArray("accept_description").length();i++) {
			qualityList.add(new quality(videoJson.getJSONObject("data").getJSONArray("accept_quality").getInt(i),videoJson.getJSONObject("data").getJSONArray("accept_description").getString(i),videoJson.getJSONObject("data").getJSONObject("dash").getJSONArray("video").getJSONObject(i).getString("base_url"),videoJson.getJSONObject("data").getJSONObject("dash").getJSONArray("audio").getJSONObject(0).getString("base_url")));
			System.out.println(i);
			System.out.println(videoJson.getJSONObject("data").getJSONObject("dash").getJSONArray("video").getJSONObject(i).getString("base_url"));
		}
	}
	
	private void downloadVideo(int epi) throws MalformedURLException{
		URL vidUrl = null;
		URL audUrl = null;
		getDownloadLink(getCid(epi),getAid());
		System.out.println(downloadDirectory);
		for (int i=0; i<qualityList.size();i++) {
			if (qualityList.get(i).getNum() == quality) {
				vidUrl = new URL(qualityList.get(i).getVid());
			}
		}
		if (vidUrl == null) vidUrl = new URL(qualityList.get(0).getVid());
		audUrl = new URL(qualityList.get(0).getAud());

		int byteSum = 0;
		int byteRead;
	
		try {
            HttpURLConnection con = (HttpURLConnection) vidUrl.openConnection();
            con.setRequestProperty("Host","upos-hz-mirrorakam.akamaized.net:443");
            con.setRequestProperty("Connection","keep-alive");
            con.setRequestProperty("User-Agent", new RandomUserAgent().getRandomUserAgent());
            con.setRequestProperty("Referer", "https://www.bilibili.com/video/BV"+bv);
            InputStream inStream = con.getInputStream();
            FileOutputStream fs = new FileOutputStream(new File(downloadDirectory,"/bilidown.m4s"));
            
            System.out.println("Video Download Started");
            
            byte[] buffer = new byte[1024];
            int length;
            while ((byteRead = inStream.read(buffer)) != -1) {
                byteSum += byteRead;
                fs.write(buffer, 0, byteRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		System.out.println("Video Download Successful");
		
		byteSum = 0;
	
		try {
            HttpURLConnection con = (HttpURLConnection) audUrl.openConnection();
            con.setRequestProperty("Host","upos-hz-mirrorakam.akamaized.net:443");
            con.setRequestProperty("Connection","keep-alive");
            con.setRequestProperty("User-Agent", new RandomUserAgent().getRandomUserAgent());
            con.setRequestProperty("Referer", "https://www.bilibili.com/video/BV"+bv);
            InputStream inStream = con.getInputStream();
            FileOutputStream fs = new FileOutputStream(new File(downloadDirectory,"/bilidown.mp3"));
            
            System.out.println("Audio Download Started");
            
            byte[] buffer = new byte[1024];
            int length;
            while ((byteRead = inStream.read(buffer)) != -1) {
                byteSum += byteRead;
                fs.write(buffer, 0, byteRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		System.out.println("Audio Download Successful");

		try {
			merge(epi,downloadDirectory);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void merge(int epi, File downloadDirectory) throws InterruptedException{
		this.downloadDirectory = downloadDirectory;
		Runtime rt = Runtime.getRuntime();
		try {
			System.out.println("ffmpeg -i "+downloadDirectory.toString()+ File.separator +"bilidown.m4s -i "+downloadDirectory.toString()+
					File.separator + "bilidown.mp3 -c:v copy -c:a aac "+downloadDirectory.toString() + File.separator + getChinese(getTitle().trim())
					+epi+getSubTitle(epi)+".mp4");
			List<String> comm = new LinkedList<String>();
			comm.add("ffmpeg");
			comm.add("-i");
			comm.add(downloadDirectory.toString()+ File.separator + "bilidown.m4s");
			comm.add("-i");
			comm.add(downloadDirectory.toString()+ File.separator + "bilidown.mp3");
			comm.add("-c:v");
			comm.add("copy");
			comm.add("-c:a");
			comm.add("aac");
			comm.add(downloadDirectory.toString()+File.separator+"["+getAuthor()+"]"+getChinese(getTitle().trim())+"_"+getChinese(getSubTitle(epi))+".mp4");
			ProcessBuilder builder = new ProcessBuilder(comm);
			builder.directory(downloadDirectory);
			builder.redirectErrorStream(true);
			builder.inheritIO();
			System.out.println("1");
			Process pr = builder.start();
			pr.waitFor();
			System.out.println("File Merge Successful");
			pr.destroy();
		} catch (IOException e) {
			System.out.println("File Merge Failed");
			e.printStackTrace();
		}
	}
	
	private static String getChinese(String orgStr) {
		String regex = "([\\u4e00-\\u9fa5_a-zA-Z0-9一-龠ぁ-ゔァ-ヴー]+)";
		String str = "";
		Matcher matcher = Pattern.compile(regex).matcher(orgStr);
		while (matcher.find()) {
		str+= matcher.group(0);
		}
		return str;
		}
}
