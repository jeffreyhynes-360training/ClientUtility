package com.softech.ls360.exception;

/**
 * Copyright (c) 9/30/15, 360training.com. All Rights Reserved.
 * User: jeffreyhynes
 * Date: 9/30/15
 * com.softech.ls360.util
 * <p/>
 * Exception thrown to indicate that there was a failure with connecting and or logging into the ftp server.
 */
public class FTPLoginException extends FTPClientException{
    private static final long serialVersionUID = 7495054789221772512L;

    public FTPLoginException(){
        super();
    }

    public FTPLoginException(Throwable cause){
        super(cause);
    }

    public FTPLoginException(String message){
        super(message);
    }

    public FTPLoginException(String message,Throwable cause){
        super(message,cause);
    }

    protected FTPLoginException(String message,Throwable cause,boolean enableSuppression,boolean writableStackTrace){
        super(message,cause,enableSuppression,writableStackTrace);
    }
}
