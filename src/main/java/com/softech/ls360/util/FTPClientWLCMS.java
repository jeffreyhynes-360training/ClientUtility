package com.softech.ls360.util;

import com.softech.ls360.lcms.contentbuilder.utils.Delegate;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(FTPClientWLCMS.class);

    private final String userName = "freemium";
    private final String password = "Fr!!mi@m#&amp;";
    private final String ftpServer = "qa-flash-3.360training.com";
    private final String tempLocation = "/TemporaryAssets/";
    private final String permanentLocation = "/PermanentAssets/";
    private final int maxRetries = 3;
    private final int port = 21;

    public String uploadFileChunk(String requestId,String fileName,int currentChunk,int chunks,byte[] data)
            throws Exception{
        FTPClient ftpClient = new FTPClient();
        String filePath = getTempLocation() + requestId + "/" + fileName;

        try{
            ftpClient.connect(getFtpServer());
            if( ! ftpClient.login(getUserName(),getPassword()) ){
                throw new Exception("Unable to login\nFTP Error Msg:" + ftpClient.getReplyString());
            }

            filePath = ftpClient.printWorkingDirectory() + filePath;
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            OutputStream out = null;

            //Application from linux application server doesn't work on Active mode.
            ftpClient.enterLocalPassiveMode();

            //if it is a first chunk
            if( currentChunk <= 0 ){
                //assuming, file already exists.
                ftpClient.deleteFile(filePath);

                //assuming, directory structure is not defined
                createDirectory(ftpClient,filePath,true);
            }

            try{
                //some time "ftpClient.appendFileStream" returns null stream. so we will try configured number of times.
                int tryLeft = getMaxRetries();
                while( true ){
                    out = ftpClient.appendFileStream(filePath);
                    tryLeft--;
                    if( out == null && tryLeft > 0 ){
                        logger.info("Unable to append file" + "\n File Path:" + filePath + "\nFTP Error Msg:" +
                                            ftpClient.getReplyString());
                        logger.info("Retrying..");
                        Thread.sleep(200);
                    }else{
                        break;
                    }
                }

                if( out != null ){
                    out.write(data);
                }else{
                    //last try with input stream
                    InputStream is = new ByteArrayInputStream(data);

                    //appendFileStream is observed much faster that appendFile, So we kept appendFileStream as first priority
                    if( ! ftpClient.appendFile(filePath,is) ){
                        throw new IOException(
                                "Unable to copy file to server because of unkonwn issue.\nFTP Error Msg:" +
                                        ftpClient.getReplyString());
                    }
                }
            }finally{
                if( out != null ){
                    out.close();
                }
            }
        }finally{
            try{
                ftpClient.disconnect();
            }catch( IOException e ){
            }
        }

        return requestId + "/" + fileName;
    }

    public String confirmFile(String requestId,String fileRelativePath,Delegate permanentPathCalculater)
            throws SocketException, IOException{
        FTPClient ftpClient = new FTPClient();
        String permanetPath = ( String )permanentPathCalculater.invoke(getPermanentLocation());
        String permanetRelativePath = permanetPath;
        String tempPath = getTempLocation() + fileRelativePath;

        try{
            ftpClient.connect(getFtpServer());
            ftpClient.login(getUserName(),getPassword());

            //append working directory.
            permanetPath = ftpClient.printWorkingDirectory() + permanetPath;
            tempPath = ftpClient.printWorkingDirectory() + tempPath;

            createDirectory(ftpClient,permanetPath,true);

            //move file location.
            if( ! ftpClient.rename(tempPath,permanetPath) ){
                throw new IOException(
                        "Unable to move file to permanent location because of unkonwn issue." + "\nTemp Path:" +
                                tempPath + "\nPermanent Path:" + permanetPath + "\nFTP Error Msg:" +
                                ftpClient.getReplyString());
            }
        }finally{
            try{
                ftpClient.disconnect();
            }catch( IOException e ){
            }
        }

        return permanetRelativePath;
    }

    private void createDirectory(FTPClient ftpClient,String filePath,boolean hasFileName) throws IOException{
        String[] pathParts = filePath.split("[/\\\\]");

        String constructedPath = "";

        //for avoiding last part if it has a file name.
        int loopSize = pathParts.length - ( ( hasFileName ) ? 1 : 0 );

        for( int i = 0;i < loopSize;i++ ){
            String pathPart = pathParts[i];
            if( pathPart.trim() == "" ){
                continue;
            }
            constructedPath += "/" + pathPart;
            if( ftpClient.makeDirectory(constructedPath) ){
                ;
            }
        }
    }

    public String getTempLocation(){
        return tempLocation;
    }

    public String getFtpServer(){
        return ftpServer;
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

    public int getMaxRetries(){
        return maxRetries;
    }

    public String getPermanentLocation(){
        return permanentLocation;
    }

    public void createDirectory(String requestId,String filepath) throws Exception{
        FTPClient ftpClient = new FTPClient();
        String filePath = getTempLocation() + requestId + "/" + filepath;
        try{
            ftpClient.connect(getFtpServer());
            if( ! ftpClient.login(getUserName(),getPassword()) ){
                throw new Exception("Unable to login\nFTP Error Msg:" + ftpClient.getReplyString());
            }

            filePath = ftpClient.printWorkingDirectory() + filePath;
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            createDirectory(ftpClient,filePath,true);
        }finally{
            try{
                ftpClient.disconnect();
            }catch( IOException e ){
            }
        }
    }
}
