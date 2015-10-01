package com.softech.ls360.exception;

/**
 * Copyright (c) 9/30/15, 360training.com. All Rights Reserved.
 * User: jeffreyhynes
 * Date: 9/30/15
 * com.softech.ls360.util
 * <p/>
 * Exception thrown to indicate that there was a failure which attempting to upload a file to the server.
 */
public class FTPUploadException extends FTPClientException{
    private static final long serialVersionUID = 3023831093128405734L;

    public FTPUploadException(){
        super();
    }

    public FTPUploadException(Throwable cause){
        super(cause);
    }

    public FTPUploadException(String message){
        super(message);
    }

    public FTPUploadException(String message,Throwable cause){
        super(message,cause);
    }

    protected FTPUploadException(String message,Throwable cause,boolean enableSuppression,boolean writableStackTrace){
        super(message,cause,enableSuppression,writableStackTrace);
    }
}
