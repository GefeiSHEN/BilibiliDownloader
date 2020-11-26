package bilibiliDownloader;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.*;

public class guiMain extends JFrame implements ActionListener {
	private JPanel inputPanel;
	private JLabel bvLabel;
	private JLabel statusLabel;
	private JTextField bvTextField;
	private JButton downloadButton;
	
	private JSONArray dataArray;
	private String bv;

	private guiMain() {
		super();

		inputPanel = new JPanel();
		bvLabel = new JLabel("BV号: ");
		statusLabel = new JLabel("    ", SwingConstants.CENTER);
		bvTextField = new JTextField(7);
		downloadButton = new JButton("下载");

		downloadButton.addActionListener(this);

		inputPanel.add(bvLabel);
		inputPanel.add(bvTextField);

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.add(statusLabel);
		this.add(statusLabel, BorderLayout.NORTH);
		this.add(inputPanel, BorderLayout.CENTER);
		this.add(downloadButton, BorderLayout.SOUTH);
		this.setBounds(100, 100, 300, 150);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String buttonCommand = e.getActionCommand();
		if (buttonCommand.equals("下载"))
			try {
				download(bvTextField.getText());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

	public static void main(String args[]) {
		guiMain objMain = new guiMain();
	}

	private void download(String bv) throws IOException {
		this.bv = bv;
		getCidJson(bv);
		for (int i=0; i<getEpisode();i++) {
			System.out.println(getAid(getCid(i)));
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
		
		this.dataArray = resultJson.getJSONArray("data");
	}
	
	private int getEpisode() {
		//dataArray.
		return dataArray.length();
	}
	
	private int getCid(int epi) {
		return dataArray.getJSONObject(epi).getInt("cid");
	}
	
	private int getAid(int cid) {
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
		JSONObject resultJson = new JSONObject(result);
		return resultJson.getJSONObject("data").getInt("aid");
	}
}
