package com.softech.ls360.exception;

/**
 * Copyright (c) 9/30/15, 360training.com. All Rights Reserved.
 * User: jeffreyhynes
 * Date: 9/30/15
 * com.softech.ls360.util
 * <p/>
 * Exception thrown to indicate a failure with teh ftpClient.
 */
public class FTPClientException extends RuntimeException{
    private static final long serialVersionUID = 1513339946550642258L;

    public FTPClientException(){
        super();
    }

    public FTPClientException(Throwable cause){
        super(cause);
    }

    public FTPClientException(String message){
        super(message);
    }

    public FTPClientException(String message,Throwable cause){
        super(message,cause);
    }

    protected FTPClientException(String message,Throwable cause,boolean enableSuppression,boolean writableStackTrace){
        super(message,cause,enableSuppression,writableStackTrace);
    }
}
