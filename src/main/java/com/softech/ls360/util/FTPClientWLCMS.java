package com.softech.ls360.util;

import com.softech.ls360.lcms.contentbuilder.utils.Delegate;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

/**
 * Copyright (c) 10/1/15, CreditCards.com. All Rights Reserved.
 * User: jeffreyhynes
 * Date: 10/1/15
 * com.softech.ls360.util
 */
public class FTPClientWLCMS{
    private String tempLocation = "TemporaryAssets/";
	private String permanentLocation = "PermanentAssets/";
	private String ftpServer = "qa-flash-3.360training.com";
	private String userName = "freemium";
	private String password = "Fr!!mi@m#&amp;";
	private Integer maxRetries = 3;
	private boolean enabled = true;
	private static Logger logger = LoggerFactory.getLogger(FTPClientWLCMS.class);
	
	public String uploadFileChunk(String requestId,String fileName, int currentChunk, int chunks, byte[] data) throws Exception {
		FTPClient ftpClient = new FTPClient();
    	String filePath = getTempLocation() + requestId +  "/" + fileName;
    	if(!enabled) {
    		return requestId +  "/" + fileName;
    	}
    	
    	try {
			ftpClient.connect(getFtpServer());
			if(!ftpClient.login(getUserName(), getPassword())){
				throw new Exception("Unable to login\nFTP Error Msg:" + ftpClient.getReplyString());
			}
			
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			OutputStream out = null;
			
			//Application from linux application server doesn't work on Active mode. 
			ftpClient.enterLocalPassiveMode();
			
			//if it is a first chunk
			if(currentChunk <= 0) {
				//assuming, file already exists. 
				ftpClient.deleteFile(filePath);
				
				//assuming, directory structure is not defined
				createDirectory(ftpClient,filePath, true);
			}	
			
			try {
				//some time "ftpClient.appendFileStream" returns null stream. so we will try configured number of times.
				int tryLeft = getMaxRetries();
				while(true) {
					out = ftpClient.appendFileStream(filePath);
					tryLeft--;
					if(out == null && tryLeft>0 ) {
						logger.error("Unable to append file" 
							+ "\n File Path:" + filePath
							+ "\nFTP Error Msg:" + ftpClient.getReplyString());
						Thread.sleep(200);
					} else {
						break;
					}
				} 
				
				if(out != null) {
					out = new BufferedOutputStream(out);
					out.write(data);
					out.flush();
				} else {
					//last try with input stream
					InputStream is = new ByteArrayInputStream(data);
					
					//appendFileStream is observed much faster that appendFile, So we kept appendFileStream as first priority 
					if(!ftpClient.appendFile(filePath,is)){
						throw new IOException("Unable to copy file to server because of unkonwn issue.\nFTP Error Msg:" + ftpClient.getReplyString());
					}
				}
			} finally {
				if(out != null) {
					out.close();
				}
			}
		
			
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {}
		}
    	
    	return requestId +  "/" + fileName;
	}

	public String confirmFile(String requestId,String fileRelativePath,Delegate permanentPathCalculater) throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();
		String permanetPath = (String) permanentPathCalculater.invoke(getPermanentLocation());
		String tempPath = getTempLocation() + fileRelativePath;
		
		if(!enabled) {
    		return permanetPath;
    	}
		
		try {
			ftpClient.connect(getFtpServer());
			ftpClient.login(getUserName(), getPassword());
			
			
			createDirectory(ftpClient,permanetPath, true);
			
			//move file location.
			if(!ftpClient.rename(tempPath, permanetPath)) {
				throw new IOException("Unable to move file to permanent location because of unkonwn issue." 
						+ "\nTemp Path:" + tempPath
						+ "\nPermanent Path:" + permanetPath
						+ "\nFTP Error Msg:" + ftpClient.getReplyString());
			}
			
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {}
		}
		
		return permanetPath;

	}
	
	private void createDirectory(FTPClient ftpClient,String filePath,boolean hasFileName) throws SocketException, IOException {
		String[] pathParts = filePath.split("[/\\\\]");
		
		StringBuilder constructedPath = new StringBuilder();
				
		//for avoiding last part if it has a file name.
		int loopSize = pathParts.length - ((hasFileName)? 1: 0);  
		String fwSlash = "";
		
		for(int i=0; i<loopSize; i++) {
			String pathPart = pathParts[i];
			if(pathPart.trim() == "") {
				continue;
			}
			constructedPath.append(fwSlash + pathPart);
			ftpClient.makeDirectory(constructedPath.toString());
			fwSlash = "/";
		}
	}
	
	public void createDirectory(String requestId,String filepath) throws Exception{
        FTPClient ftpClient = new FTPClient();
        String filePath = getTempLocation() + requestId + "/" + filepath;
        try{
            ftpClient.connect(getFtpServer());
            if( ! ftpClient.login(getUserName(),getPassword()) ){
                throw new Exception("Unable to login\nFTP Error Msg:" + ftpClient.getReplyString());
            }

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            createDirectory(ftpClient,filePath,false);
        }finally{
            try{
                ftpClient.disconnect();
            }catch( IOException e ){
            }
        }
    }

	public String getTempLocation() {
		return tempLocation;
	}

	public void setTempLocation(String tempLocation) {
		this.tempLocation = tempLocation;
	}

	public String getPermanentLocation() {
		return permanentLocation;
	}

	public void setPermanentLocation(String permanentLocation) {
		this.permanentLocation = permanentLocation;
	}

	public String getFtpServer() {
		return ftpServer;
	}

	public void setFtpServer(String ftpServer) {
		this.ftpServer = ftpServer;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(Integer maxRetries) {
		this.maxRetries = maxRetries;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
