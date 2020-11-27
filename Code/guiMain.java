package bilibiliDownloader;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;

public class guiMain extends JFrame implements ActionListener {
	private JPanel bvPanel;
	private JPanel inputPanel;
	private JLabel bvLabel;
	private JLabel statusLabel;
	private JTextField bvTextField;
	private JButton downloadButton;
	private JButton directoryButton;
	private JFileChooser downloadChooser;
	
	private File downloadDirectory;
	
	private JSONArray bvArray;
	private JSONObject avJson;
	private String bv;
	private int aid;
	private String author;
	private String title;

	private guiMain() {
		super();
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

		bvPanel = new JPanel();
		inputPanel = new JPanel();
		bvLabel = new JLabel("BV号: ");
		statusLabel = new JLabel("    ", SwingConstants.CENTER);
		bvTextField = new JTextField(7);
		downloadButton = new JButton("下载");
		directoryButton = new JButton("下载位置");

		downloadButton.addActionListener(this);
		directoryButton.addActionListener(this);

		bvPanel.add(bvLabel);
		bvPanel.add(bvTextField);
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(bvPanel,BorderLayout.CENTER);
		inputPanel.add(directoryButton, BorderLayout.SOUTH);

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.add(statusLabel);
		this.add(statusLabel, BorderLayout.NORTH);
		this.add(inputPanel, BorderLayout.CENTER);
		this.add(downloadButton, BorderLayout.SOUTH);
		this.setBounds(100, 100, 500, 300);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String buttonCommand = e.getActionCommand();
		if (buttonCommand.equals("下载")) {
			try {
				download(bvTextField.getText());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (buttonCommand.equals("下载位置")) {
			downloadChooser = new JFileChooser();
			downloadChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int option = downloadChooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) 
				downloadDirectory = downloadChooser.getSelectedFile();
		}
	}

	public static void main(String args[]) {
		guiMain objMain = new guiMain();
	}

	private void download(String bv) throws IOException {
		this.bv = bv;
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
		return avJson.getJSONObject("owner").getString("name");
	}
	
	private String getDownloadLink(int cid, int aid) {
		HttpURLConnection con = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;
		try {
			URL url = new URL("https://api.bilibili.com/x/player/playurl?avid="+aid+"&cid="+cid+"&otype=json");
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
		JSONArray resultArray = resultJson.getJSONObject("data").getJSONArray("durl");
		return resultArray.getJSONObject(0).getString("url");
	}
	
	private void downloadVideo(int epi) throws MalformedURLException {
		
		URL url = new URL(getDownloadLink(getCid(epi),getAid()));
		int byteSum = 0;
		int byteRead;
		
		try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Host","upos-hz-mirrorakam.akamaized.net:443");
            con.setRequestProperty("Connection","keep-alive");
            con.setRequestProperty("User-Agent", new RandomUserAgent().getRandomUserAgent());
            InputStream inStream = con.getInputStream();
            //FileOutputStream fs = new FileOutputStream(new File(downloadDirectory,"/"+getTitle().trim()+"_"+getSubTitle(epi)+".flv"));
            FileOutputStream fs = new FileOutputStream(new File(downloadDirectory,"/bilidown.flv"));
            
            System.out.println("Download Started");
            
            byte[] buffer = new byte[1204];
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
		
		System.out.println("Downloaded");
		
		File oldFile = new File(downloadDirectory,"/bilidown.flv");
		System.out.println(oldFile.renameTo(new File(downloadDirectory,"/"+getTitle().trim()+getSubTitle(epi).trim()+".flv")));
	}
	
	private static String replaceSpecStr(String orgStr){
		if (null!=orgStr&&!"".equals(orgStr.trim())) {
			String regEx="[\\s~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(orgStr);
			return m.replaceAll("");
		}
		return null;
	}
}
