package com.softech.ls360.util;

import com.softech.ls360.exception.FTPClientException;
import com.softech.ls360.exception.FTPCompletionException;
import com.softech.ls360.exception.FTPConnectionException;
import com.softech.ls360.exception.FTPLoginException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Copyright (c) 9/30/15, 360training.com. All Rights Reserved.
 * User: jeffreyhynes
 * Date: 9/30/15
 * com.softech.ls360.util
 * <p/>
 * <p> Utility class used to verify functional behaviour of connecting to and interacting with a ftp server.
 * </p>
 */
public class FTPClientUtil{
    private static final Logger logger = LoggerFactory.getLogger(FTPClientUtil.class);
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    public static final int DEFAULT_TIMEOUT = 10000;
    public static final String BASE_DIRECTORY = "media";

    private final String user = "freemium";
    private final String password = "Fr!!mi@m#&amp;";
    private final String server = "qa-flash-3.360training.com";
    private final int port = 21;

    /**
     * Create a new FTPClient class which then connects to the
     * specified ftp server.
     *
     * @return FTPClient newly created ftpClient class.
     * @throws FTPConnectionException is thrown if there was a failure connecting to the server
     * @throws FTPCompletionException is thrown if a positive response is not returned by the server.
     */
    public FTPClient connect() throws FTPConnectionException, FTPCompletionException{
        logger.info("Creating a new instance of the ftpClient and connecting to the ftp server");
        FTPClient client = new FTPClient();
        try{
            client.connect(server,port);
            client.setSoTimeout(30000);

            logger.info("Connected to " + server + ":" + port);
            checkReplyPositiveCompletion(client);
        }catch( IOException exc ){
            throw new FTPConnectionException("Could not connect to server '" + server + "'",exc);
        }
        return client;
    }

    /**
     * Executes the logout & disconnect functions against the ftpClient
     *
     * @param client an instance of org.apache.commons.net.ftp.FTPClient class.
     */
    public void disconnect(FTPClient client){
        if( client != null ){
            try{
                boolean logoutSuccess = client.logout();
                client.disconnect();
                if( ! logoutSuccess ){
                    logger.warn("Logout failed while disconnecting, error code - " + client.getReplyCode());
                }
            }catch( IOException ioException ){
                throw new FTPConnectionException(
                        "Logout failed while disconnecting, error code - " + client.getReplyCode(),ioException);
            }
        }
    }

    /**
     * Executes the login function to the ftp server using the username
     * and password provided.
     *
     * @param client an instance of org.apache.commons.net.ftp.FTPClient class.
     * @return the active ftp client successfully connected to the server
     * @throws FTPLoginException      is throw if there is an exception thrown while attempting to log into the server
     * @throws FTPCompletionException is thrown if a positive response is not returned by the server.
     */
    public FTPClient login(FTPClient client) throws FTPLoginException, FTPCompletionException{
        logger.info("logging into the server");
        logger.info("User:" + user + " Password:" + password);
        try{
            client.login(user,password);
            checkReplyPositiveCompletion(client);
        }catch( IOException ioException ){
            throw new FTPConnectionException("Could not connect to server '" + server + "'",ioException);
        }
        return client;
    }

    /**
     * Initializes the ftp connection with default settings and values.
     *
     * @param client an instance of org.apache.commons.net.ftp.FTPClient class.
     * @return the active ftp client
     * @throws FTPClientException is thrown if there is any exceptions thrown while attempting to configure the ftpClient.
     */
    public FTPClient initializeClient(FTPClient client) throws FTPClientException{
        try{
            logger.info("Initializing baseline settings for the FTPClient");
            client.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.setSoTimeout(DEFAULT_TIMEOUT);
            client.enterLocalPassiveMode();
        }catch( IOException ioException ){
            throw new FTPClientException("Failure while attempting to configure the ftpClient",ioException);
        }
        client.setBufferSize(DEFAULT_BUFFER_SIZE);
        return client;
    }

    /**
     * Verifies if the ftp server response was valid positive completion response.
     * All codes beginning with a 2 are positive completion responses.
     * The FTP server will send a positive completion response on the final successful completion of a command.
     *
     * @param client an instance of org.apache.commons.net.ftp.FTPClient class.
     * @throws FTPCompletionException is thrown if the response code is not a positive response.
     */
    public void checkReplyPositiveCompletion(FTPClient client) throws FTPCompletionException{
        logger.info("verify the response code from the ftp server is a positive completion response.");
        if( ! FTPReply.isPositiveCompletion(client.getReplyCode()) ){
            StrBuilder builder = new StrBuilder();
            builder.append("FTP server '").append(server).append("' ").append("sent negative completion. Reply: ")
                   .append(client.getReplyString());
            throw new FTPCompletionException(builder.toString());
        }
    }

    /**
     * Helper method that logs the last known server response details
     *
     * @param client an instance of org.apache.commons.net.ftp.FTPClient class.
     */
    public void logServerReply(FTPClient client){
        String[] replies = client.getReplyStrings();
        logger.info("Server response settings");
        if( replies != null && replies.length > 0 ){
            for( String aReply : replies ){
                logger.info(aReply);
            }
        }
    }

    /**
     * change the directory location of the ftp server
     *
     * @param client          an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param remoteDirectory Path of the directory, i.e /projects/java/ftp/demo
     * @throws FTPClientException if any error occurred during client-server communication
     */
    public void changeDirectory(FTPClient client,String remoteDirectory) throws FTPClientException{
        try{
            client.changeWorkingDirectory(remoteDirectory);
        }catch( IOException ioException ){
            throw new FTPClientException("Unable to locate the ftp directory " + remoteDirectory,ioException);
        }
    }

    /**
     * Creates a nested directory structure on a FTP server
     *
     * @param client          an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param remoteDirectory Path of the directory, i.e /projects/java/ftp/demo
     * @throws FTPClientException if any error occurred during client-server communication
     */
    public void makeDirectories(FTPClient client,String remoteDirectory) throws FTPClientException{
        String[] pathElements = remoteDirectory.split("/");
        for( String directoryPath : pathElements ){
            if( directoryMissing(client,directoryPath) ){
                makeDirectory(client,directoryPath);
                try{
                    client.changeWorkingDirectory(directoryPath);
                }catch( IOException ioException ){
                    throw new FTPClientException("Unable to locate the ftp directory " + directoryPath,ioException);
                }
            }
        }
    }

    /**
     * Verifies if a specified directory does not exist in the parent path
     *
     * @param client        an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param directoryPath the directory to check for
     * @return true if the directory exists
     * @throws FTPConnectionException is thrown if an exception is encountered while attempting to locate the directory.
     */
    public boolean directoryMissing(FTPClient client,String directoryPath) throws FTPClientException{
        try{
            client.changeWorkingDirectory(directoryPath);
        }catch( IOException ioException ){
            throw new FTPClientException("Unable to locate the ftp directory " + directoryPath,ioException);
        }

        return FTPReply.FILE_UNAVAILABLE == client.getReplyCode();
    }

    /**
     * Verifies if a specified directory exists in the parent path
     *
     * @param client        an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param directoryPath the directory to check for
     * @return true if the directory exists
     * @throws FTPConnectionException is thrown if an exception is encountered while attempting to locate the directory.
     */
    public boolean directoryExists(FTPClient client,String directoryPath) throws FTPClientException{
        try{
            logger.info("Verifying if the directory :'" + directoryPath + "' exists on the ftp server");
            boolean exists = client.changeWorkingDirectory(directoryPath);
            logger.info("Directory exists status:" + exists);
        }catch( IOException ioException ){
            throw new FTPClientException("Unable to locate the ftp directory " + directoryPath,ioException);
        }

        return FTPReply.isPositiveCompletion(client.getReplyCode());
    }

    /**
     * Creates a new directory on the FTP server in the current directory (if a relative pathname is given) or where specified (if an absolute pathname is given).
     *
     * @param client        an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param directoryPath the directory to create
     * @return True if successfully completed, false if not.
     * @throws FTPClientException
     */
    public boolean makeDirectory(FTPClient client,String directoryPath) throws FTPClientException{
        boolean success;
        try{
            logger.info("Creating the new directory:" + directoryPath);
            success = client.makeDirectory(directoryPath);
        }catch( IOException ioException ){
            throw new FTPClientException(
                    "An error was encountered while attempting to create the new directory" + directoryPath,
                    ioException);
        }
        return success;
    }

    /**
     * Verifies if a specified file exists in the specified directory.
     *
     * @param client   an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param filename the file name to search for
     * @param filePath the directory path to search in.
     * @return true if the directory exists, false if not
     */
    public boolean fileExists(FTPClient client,final String filename,String filePath){
        boolean success;
        try{
            FTPFileFilter filter = new FTPFileFilter(){
                @Override
                public boolean accept(FTPFile ftpFile){
                    return ( ftpFile.isFile() && StringUtils.equalsIgnoreCase(ftpFile.getName(),filename) );
                }
            };
            FTPFile[] result = client.listFiles(filePath,filter);
            success = result != null && result.length == 1;
        }catch( IOException ioException ){
            throw new FTPClientException("An error was encountered while attempting to locate the file " + filePath,
                                         ioException);
        }

        return success;
    }

    /**
     * Verifies if a specified file does not exists in the specified directory.
     *
     * @param client   an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param filename the file name to search for
     * @param filePath the directory path to search in.
     * @return true if the directory exists, false if not
     */
    public boolean fileNotExists(FTPClient client,final String filename,String filePath){
        boolean success;
        try{
            FTPFileFilter filter = new FTPFileFilter(){
                @Override
                public boolean accept(FTPFile ftpFile){
                    return ( ftpFile.isFile() && StringUtils.equalsIgnoreCase(ftpFile.getName(),filename) );
                }
            };
            FTPFile[] result = client.listFiles(filePath,filter);
            success = result == null || result.length == 0;
        }catch( IOException ioException ){
            throw new FTPClientException("An error was encountered while attempting to locate the file " + filePath,
                                         ioException);
        }

        return success;
    }

    /**
     * Deletes a file on the FTP server.
     *
     * @param client   an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param fileName the file to be removed.
     * @return true if the file was successfully deleted.
     * @throws FTPClientException is thrown if an exception is encountered while attempting to remove the file.
     */
    public boolean deleteFile(FTPClient client,String fileName) throws FTPClientException{
        boolean success = false;
        try{
            logger.info("Attempting to delete the file:" + fileName);
            success = client.deleteFile(fileName);
        }catch( IOException ioException ){
            throw new FTPClientException("Unable to delete the file " + fileName,ioException);
        }
        return success;
    }

    /**
     * @param client     an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param fileName   the file to be removed.
     * @param remotePath the directory where the file is located
     * @return true if the file was successfully deleted.
     * @throws FTPClientException is thrown if an exception is encountered while attempting to remove the file.
     */
    public boolean deleteFile(FTPClient client,String remotePath,String fileName) throws FTPClientException{
        boolean success = false;
        try{
            logger.info("Attempting to delete the file:" + fileName);
            client.changeWorkingDirectory(remotePath);
            success = client.deleteFile(fileName);
        }catch( IOException ioException ){
            throw new FTPClientException("Unable to delete the file " + fileName,ioException);
        }
        return success;
    }

    /**
     * Removes a directory on the FTP server (if empty).
     *
     * @param client    an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param directory the directory to remove
     * @return true if the file was successfully deleted.
     * @throws FTPClientException is thrown if an exception is encountered while attempting to remove the directory.
     */
    public boolean removeDirectory(FTPClient client,String directory) throws FTPClientException{
        boolean success;
        try{

            logger.info("Attempting to remove the directory:" + directory);
            success = client.removeDirectory(directory);
        }catch( IOException ioException ){
            throw new FTPClientException("Unable to remove the directory " + directory,ioException);
        }
        return success;
    }

    public boolean resetToHome(FTPClient client){
        boolean success;
        try{
            success = client.changeToParentDirectory();
            client.changeWorkingDirectory(BASE_DIRECTORY);
        }catch( IOException ioException ){
            throw new FTPClientException("An error was encountered while attempting to return to the parent directory",
                                         ioException);
        }
        return success;
    }

    /**
     * Upload a file to the ftp server by Storing a file on the server using the given name and taking input from the given InputStream.
     *
     * @param client          an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param filePath        the local path for the file to upload
     * @param remoteDirectory the given remote path location
     * @return true if the file was successfully stored.
     * @throws FTPClientException is thrown if the local file was not found or if an exception was encountered while attempting to store the file on the server.
     */
    public boolean uploadFile(FTPClient client,String filePath,String remoteDirectory) throws FTPClientException{
        File file = new File(filePath);
        DataInputStream dataInputStream = null;
        try{
            dataInputStream = new DataInputStream(new FileInputStream(file));
            client.storeFile(remoteDirectory,dataInputStream);
        }catch( FileNotFoundException fileNotFoundException ){
            throw new FTPClientException("Exception while locating the local file",fileNotFoundException);
        }catch( IOException ioException ){
            throw new FTPClientException("An error was encountered while attempting to upload a file" + remoteDirectory,
                                         ioException);
        }finally{
            try{
                if( null != dataInputStream ){
                    dataInputStream.close();
                }
            }catch( IOException ioException ){
                logger.error("Severe exception attempting to close the image file",ioException);
            }
        }
        return FTPReply.isPositiveCompletion(client.getReplyCode());
    }

    /**
     * Uploads a file to the ftp server by using an OutputStream through which data can be written to store a file on the server using the given name.
     *
     * @param client          an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param filePath        the local path for the file to upload
     * @param remoteDirectory the given remote path location
     * @return true if the file was successfully stored.
     * @throws FTPClientException is thrown if the local file was not found or if an exception was encountered while attempting to store the file on the server.
     */
    public boolean uploadFileStream(FTPClient client,String filePath,String remoteDirectory) throws FTPClientException{
        BufferedInputStream clientFileStream = null;
        BufferedOutputStream outputStream = null;
        byte readBuf[] = new byte[512 * 1024];
        try{
            clientFileStream = new BufferedInputStream(new FileInputStream(filePath));
            outputStream = new BufferedOutputStream(client.storeFileStream(remoteDirectory));
            int readCnt = clientFileStream.read(readBuf);
            while( 0 < readCnt ){
                outputStream.write(readBuf,0,readCnt);
                readCnt = clientFileStream.read(readBuf);
            }
            outputStream.flush();
            outputStream.close();
            client.completePendingCommand();
        }catch( FileNotFoundException fileNotFoundException ){
            throw new FTPClientException("Exception while locating the local file",fileNotFoundException);
        }catch( IOException ioException ){
            throw new FTPClientException("An error was encountered while attempting to upload a file" + filePath,
                                         ioException);
        }finally{
            try{
                if( null != clientFileStream ){
                    clientFileStream.close();
                }
                if( null != outputStream ){
                    outputStream.close();
                }
            }catch( IOException ioException ){
                logger.error("Severe exception attempting to close the image file",ioException);
            }
        }

        return FTPReply.isPositiveCompletion(client.getReplyCode());
    }

    /**
     * Uploads a file to the ftp server by using an OutputStream through which data is appended to a file on the server with the given name.
     *
     * @param client          an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param filePath        the local path for the file to upload
     * @param remoteDirectory the given remote path location
     * @return true if the file was successfully stored.
     * @throws FTPClientException is thrown if the local file was not found or if an exception was encountered while attempting to store the file on the server.
     */
    public boolean uploadAppendFileStream(FTPClient client,String filePath,String remoteDirectory)
            throws FTPClientException{
        OutputStream outputStream = null;
        try{
            byte[] fileData = FileUtils.load(filePath);
            outputStream = client.appendFileStream(remoteDirectory);
            outputStream.write(fileData);
            outputStream.close();
            client.completePendingCommand();
        }catch( FileNotFoundException fileNotFoundException ){
            throw new FTPClientException("Exception while locating the local file",fileNotFoundException);
        }catch( IOException ioException ){
            throw new FTPClientException("An error was encountered while attempting to upload a file" + filePath,
                                         ioException);
        }finally{
            try{
                if( null != outputStream ){
                    outputStream.close();
                }
            }catch( IOException ioException ){
                logger.error("Severe exception attempting to close the image file",ioException);
            }
        }
        logger.info("FTP Client response: " + client.getReplyCode() + " - " + client.getReplyString());
        return FTPReply.isPositiveCompletion(client.getReplyCode());
    }

    /**
     * Helper method to log the current server and directory details.
     *
     * @param client    an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param directory the given remote path location
     * @throws FTPClientException is thrown if an exception was encountered while attempting to get the server directory details.
     */
    public void logDirectoryStatus(FTPClient client,String directory) throws FTPClientException{
        try{
            logger.info("SYSTEM TYPE:\n " + client.getSystemType());
            logger.info("SYSTEM STATUS:\n " + client.getStatus());
            logger.info("DIRECTORY STATUS:\n " + client.getStatus(directory));
        }catch( IOException ioException ){
            throw new FTPClientException("Unable to get server status " + directory,ioException);
        }
    }
}