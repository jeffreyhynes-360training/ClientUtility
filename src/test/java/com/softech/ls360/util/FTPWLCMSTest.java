package com.softech.ls360.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Copyright (c) 10/1/15, CreditCards.com. All Rights Reserved.
 * User: jeffreyhynes
 * Date: 10/1/15
 * com.softech.ls360.util
 * <p/>
 * Test class to test the Current WLCMS Code for ftp upload.
 */
public class FTPWLCMSTest{
    private static final Logger logger = LoggerFactory.getLogger(FTPWLCMSTest.class);

    @Test
    public void verifyWLCMSUploadUtility(){
        FTPClientWLCMS ftpWLCMSClient = new FTPClientWLCMS();
        FTPClientUtil ftpUtil = new FTPClientUtil();
        String requestID = "99";
        int currentChunk = - 1;
        int chunks = - 1;

        String filename = "eight-demo.flv";
        String finalPath = "TemporaryAssets/99";
        String fullRemotePath = "TemporaryAssets/99/eight-demo.flv";

        String imageFilePath =
                "/Users/jeffreyhynes/Development/Projects/360training/ClientUtility/src/test/resources/Movie/eight-demo.flv";
        byte[] fileArray = FileUtils.load(imageFilePath);
        try{
            ftpWLCMSClient.uploadFileChunk(requestID,filename,currentChunk,chunks,fileArray);
        }catch( Exception e ){
            logger.error("FTP Exception",e);
        }
        FTPClient client = ftpUtil.connect();
        client = ftpUtil.login(client);
        client = ftpUtil.initializeClient(client);

        assertTrue(ftpUtil.fileExists(client,filename,finalPath));

        ftpUtil.resetToHome(client);
        assertTrue(ftpUtil.deleteFile(client,finalPath,filename));

        ftpUtil.resetToHome(client);
        ftpUtil.changeDirectory(client,"TemporaryAssets");

        assertTrue(ftpUtil.removeDirectory(client,"99"));
        assertTrue(ftpUtil.fileNotExists(client,filename,fullRemotePath));

        ftpUtil.disconnect(client);
    }

    @Test
    public void verifyWLCMSCreateDirectoryUtility(){
        FTPClientWLCMS ftpWLCMSClient = new FTPClientWLCMS();
        FTPClientUtil ftpUtil = new FTPClientUtil();

        String filepath = "demo";
        String requestID = "R99";
        String serverPath = "TemporaryAssets/R99/" + filepath;
        try{
            ftpWLCMSClient.createDirectory(requestID,filepath);
        }catch( Exception e ){
            logger.error("FTP Exception",e);
        }

        FTPClient client = ftpUtil.connect();
        client = ftpUtil.login(client);
        client = ftpUtil.initializeClient(client);

        assertFalse(ftpUtil.directoryExists(client,serverPath));

        assertTrue(ftpUtil.removeDirectory(client,"TemporaryAssets/R99"));
        assertFalse(ftpUtil.directoryExists(client,"TemporaryAssets/R99"));
        ftpUtil.disconnect(client);
    }
}
