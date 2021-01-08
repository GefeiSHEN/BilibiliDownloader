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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JFileChooser;


public class guiMain extends JFrame implements ActionListener {
	//GUI components
	private JPanel bvPanel;
	private JPanel upPanel;
	private JPanel inputPanel;
	private JPanel buttonPanel;
	private JLabel bvLabel;
	private JLabel upLabel;
	private JLabel statusLabel;
	private JTextField bvTextField;
	private JTextField upTextField;
	private JButton downloadButton;
	private JButton directoryButton;
	private JButton batchButton;
	private JFileChooser downloadChooser;
	
	//user's download directory
	private File downloadDirectory;
	
	//downlaoder and batchdownloader objects
	private downloader download = new downloader();
	private batchDownloader batch = new batchDownloader();

	private guiMain() {
		//inherite from mother class 
		super();
		
		//GUI conponents initialization
		bvPanel = new JPanel();
		upPanel = new JPanel();
		inputPanel = new JPanel();
		buttonPanel = new JPanel();
		bvLabel = new JLabel("BV号: ");
		upLabel = new JLabel("UP号: ");
		statusLabel = new JLabel("    ", SwingConstants.CENTER);
		bvTextField = new JTextField(7);
		upTextField = new JTextField(7);
		downloadButton = new JButton("下载");
		directoryButton = new JButton("下载位置");
		batchButton = new JButton("批量下载");

		//add listeners for button events
		downloadButton.addActionListener(this);
		directoryButton.addActionListener(this);
		batchButton.addActionListener(this);

		//GUI layout
		bvPanel.add(bvLabel);
		bvPanel.add(bvTextField);
		upPanel.add(upLabel);
		upPanel.add(upTextField);
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(upPanel,BorderLayout.NORTH);
		inputPanel.add(bvPanel,BorderLayout.CENTER);
		inputPanel.add(directoryButton, BorderLayout.SOUTH);
		buttonPanel.add(batchButton);
		buttonPanel.add(downloadButton);

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.add(statusLabel);
		this.add(statusLabel, BorderLayout.NORTH);
		this.add(inputPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.setBounds(100, 100, 500, 300);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String buttonCommand = e.getActionCommand();
		if (buttonCommand.equals("下载")) {
			try {
				download.download(bvTextField.getText(),120,downloadDirectory);
				bvTextField.setText("");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (buttonCommand.equals("下载位置")) {
			//dialog box for directory selection
			downloadChooser = new JFileChooser();
			downloadChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int option = downloadChooser.showOpenDialog(this);
			//if directory is valid, update to download Directory
			if (option == JFileChooser.APPROVE_OPTION) 
				downloadDirectory = downloadChooser.getSelectedFile();
				System.out.println(downloadDirectory);
		} else if (buttonCommand.equals("批量下载")) {
			try {
				batch.batchDownload(Integer.parseInt(upTextField.getText()), 120, downloadDirectory);
				upTextField.setText("");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		guiMain objMain = new guiMain();
	}
	
}
