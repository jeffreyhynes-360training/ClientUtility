package com.softech.ls360.util;

import com.softech.ls360.exception.FTPClientException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrBuilder;
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
 * integration test used to verify the connection and usage of teh ftpClient.
 */
public class FTPClientTest{
    private static final Logger logger = LoggerFactory.getLogger(FTPClientTest.class);

    /**
     * Assert basic server connections as well successful reading of known directories.
     */
    @Test
    public void verifyClient(){
        FTPClientUtil ftpUtil = new FTPClientUtil();
        try{
            FTPClient client = ftpUtil.connect();
            logger.info("\n\n");
            ftpUtil.logServerReply(client);

            client = ftpUtil.login(client);
            ftpUtil.logServerReply(client);

            client = ftpUtil.initializeClient(client);
            ftpUtil.logServerReply(client);

            String persistentDirectory = "PersistentAssets";
            assertTrue(ftpUtil.directoryExists(client,persistentDirectory));
            ftpUtil.logServerReply(client);

            ftpUtil.resetToHome(client);

            String temporaryDirectory = "TemporaryAssets";
            assertTrue(ftpUtil.directoryExists(client,temporaryDirectory));
            ftpUtil.logServerReply(client);

            ftpUtil.disconnect(client);
        }catch( FTPClientException clientException ){
            logger.error("There was an exception thrown!!",clientException);
        }
    }

    /**
     * Verify the creation of a directory on the ftp server
     */
    @Test
    public void verifyDirectoryCreation() throws FTPClientException{
        FTPClient client = null;
        FTPClientUtil ftpUtil = new FTPClientUtil();

        client = ftpUtil.connect();
        client = ftpUtil.login(client);
        client = ftpUtil.initializeClient(client);
        String temporaryDirectory = "BasicTest";

        boolean success = ftpUtil.makeDirectory(client,temporaryDirectory);
        assertTrue(success);
        assertTrue(ftpUtil.directoryExists(client,temporaryDirectory));

        ftpUtil.resetToHome(client);
        assertTrue(ftpUtil.removeDirectory(client,temporaryDirectory));
        assertFalse(ftpUtil.directoryExists(client,temporaryDirectory));

        ftpUtil.disconnect(client);
    }

    /**
     * Verify the uploading of a file to the ftp server by directly storing the file on the server.
     */
    @Test
    public void verifyFileUpload() throws FTPClientException{
        FTPClientUtil ftpUtil = new FTPClientUtil();

        FTPClient client = ftpUtil.connect();
        client = ftpUtil.login(client);
        client = ftpUtil.initializeClient(client);

        String temporaryDirectory = "TemporaryAssets";
        String filename = "H264_test2_Talkinghead_mp4_480x320.mp4";
        String imageFilePath =
                "/Users/jeffreyhynes/Development/Projects/360training/LCMSSoapClient/src/test/resources/VSC/H264_test2_Talkinghead_mp4_480x320.mp4";

        StrBuilder builder = new StrBuilder();
        builder.append(temporaryDirectory).append(IOUtils.DIR_SEPARATOR).append(filename);
        String remoteFilePath = builder.toString();

        logger.info("Verify the FTP File upload");
        assertTrue(ftpUtil.uploadFile(client,imageFilePath,remoteFilePath));
        ftpUtil.resetToHome(client);

        assertTrue(ftpUtil.fileExists(client,filename,temporaryDirectory));

        assertTrue(ftpUtil.deleteFile(client,remoteFilePath));
        assertTrue(ftpUtil.fileNotExists(client,filename,temporaryDirectory));
        ftpUtil.disconnect(client);
    }

    /**
     * Verify the uploading of a file to the ftp server by opening a direct ouputstream.
     */
    @Test
    public void verifyFileUploadStream() throws FTPClientException{
    /*
        The proper steps to upload a file
        To properly write code to upload files to a FTP server using Apache Commons Net API, the following steps should be followed:

        Connect and login to the server.
        Enter local passive mode for data connection.
        Set file type to be transferred to binary.
        Create an InputStream for the local file.
        Construct path of the remote file on the server. The path can be absolute or relative to the current working directory.
        Call one of the storeXXX()methods to begin file transfer. There are two scenarios:
        Using an InputStream-based approach: this is the simplest way, since we let the system does the ins and outs.
        There is no additional code, just passing the InputStream object into the appropriate method, such as storeFile(String remote, InputStream local) method.
        Using an OutputStream-based approach: this is more complex way, but more control.
        Typically we have to write some code that reads bytes from the InputStream of the local file and writes those bytes
        into the OutputStream which is returned by the storeXXX() method, such as storeFileStream(String remote) method.
        Close the opened InputStream and OutputStream.
        Call completePendingCommand() method to complete transaction.
        Logout and disconnect from the server.
    */
        FTPClientUtil ftpUtil = new FTPClientUtil();
        FTPClient client = ftpUtil.connect();
        client = ftpUtil.login(client);
        client = ftpUtil.initializeClient(client);

        String temporaryDirectory = "TemporaryAssets";
        String filename = "eight-demo.flv";
        String imageFilePath =
                "/Users/jeffreyhynes/Development/Projects/360training/ClientUtility/src/test/resources/Movie/eight-demo.flv";

        StrBuilder builder = new StrBuilder();
        builder.append(temporaryDirectory).append(IOUtils.DIR_SEPARATOR).append(filename);
        String remoteFilePath = builder.toString();

        logger.info("Verify the FTP File upload");
        assertTrue(ftpUtil.uploadFileStream(client,imageFilePath,remoteFilePath));
        ftpUtil.resetToHome(client);

        assertTrue(ftpUtil.fileExists(client,filename,temporaryDirectory));

        assertTrue(ftpUtil.deleteFile(client,remoteFilePath));
        assertTrue(ftpUtil.fileNotExists(client,filename,temporaryDirectory));
        ftpUtil.disconnect(client);
    }

    /**
     * Verifies the uploading of a file to the ftp server by appending a file thru an outputstream
     */
    @Test
    public void verifyFileAppendStream() throws FTPClientException{
        FTPClientUtil ftpUtil = new FTPClientUtil();

        FTPClient client = ftpUtil.connect();
        client = ftpUtil.login(client);
        client = ftpUtil.initializeClient(client);

        String temporaryDirectory = "TemporaryAssets";
        String filename = "usa11.swf";
        String imageFilePath =
                "/Users/jeffreyhynes/Development/Projects/360training/ClientUtility/src/test/resources/Movie/usa11.swf";

        StrBuilder builder = new StrBuilder();
        builder.append(temporaryDirectory).append(IOUtils.DIR_SEPARATOR).append(filename);
        String remoteFilePath = builder.toString();

        logger.info("Verify the FTP File upload");
        assertTrue(ftpUtil.uploadAppendFileStream(client,imageFilePath,remoteFilePath));
        ftpUtil.resetToHome(client);

        assertTrue(ftpUtil.fileExists(client,filename,temporaryDirectory));

        assertTrue(ftpUtil.deleteFile(client,remoteFilePath));
        assertTrue(ftpUtil.fileNotExists(client,filename,temporaryDirectory));

        ftpUtil.disconnect(client);
    }
}
