package com.example.ftpserverdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.http.conn.util.InetAddressUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.annotation.SuppressLint;
import android.app.Activity;

public class MainActivity extends Activity {

	private static final String TAG = "FtpServerService";
	private static String hostip = "191.167.10.53"; // ����IP
	private static final int PORT = 2222;
	// sd��Ŀ¼
	@SuppressLint("SdCardPath")
	private static final String dirname = "/mnt/sdcard/ftp";
	// ftp�����������ļ�·��
	private static final String filename = dirname + "/users.properties";
	private FtpServer mFtpServer = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//���������������ļ�
		try {
			creatDirsFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ���������������ļ�
	 */
	private void creatDirsFiles() throws IOException {
		File dir = new File(dirname);
		if (!dir.exists()) {
			dir.mkdir();
		}
		FileOutputStream fos = null;
		String tmp = getString(R.string.users);
		File sourceFile = new File(dirname + "/users.properties");
		fos = new FileOutputStream(sourceFile);
		fos.write(tmp.getBytes());
		if (fos != null) {
			fos.close();
		}
	}
	
	/**
	 * ����FTP������
	 * @param hostip ����ip
	 */
	private void startFtpServer(String hostip) {
		FtpServerFactory serverFactory = new FtpServerFactory();

		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		File files = new File(filename);
		//���������ļ�
		userManagerFactory.setFile(files);
		serverFactory.setUserManager(userManagerFactory.createUserManager());
		// ���ü���IP�Ͷ˿ں�
		ListenerFactory factory = new ListenerFactory();
		factory.setPort(PORT);
		factory.setServerAddress(hostip);

		// replace the default listener
		serverFactory.addListener("default", factory.createListener());

		// start the server
		mFtpServer = serverFactory.createServer();
		try {
			mFtpServer.start();
			Log.d(TAG, "������FTP������  ip = " + hostip);
		} catch (FtpException e) {
			System.out.println(e);
		}
	}

	/**
	 * �ر�FTP������
	 */
	private void stopFtpServer() {
		if (mFtpServer != null) {
			mFtpServer.stop();
			mFtpServer = null;
			Log.d(TAG, "�ر���FTP������ ip = " + hostip);
		}
	}
	
	/**
	 * ��ȡ����ip
	 */
	private String getLocalIpAddress() {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (isIPv4) {
                        	return sAddr;
                        }
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	public void onStartServer(View view){
		hostip = getLocalIpAddress();
		Log.d(TAG, "��ȡ����IP = " + hostip);
		startFtpServer(hostip);
	}
}
