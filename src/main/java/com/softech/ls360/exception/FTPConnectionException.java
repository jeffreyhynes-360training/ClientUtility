package com.softech.ls360.exception;

/**
 * Copyright (c) 9/30/15, CreditCards.com. All Rights Reserved.
 * User: jeffreyhynes
 * Date: 9/30/15
 * com.softech.ls360.exception
 * <p/>
 * Exception thrown to indicate that there was a failure while attepting to connect to the ftp server.
 */
public class FTPConnectionException extends FTPClientException{
    private static final long serialVersionUID = - 6411746124323054747L;

    public FTPConnectionException(){
        super();
    }

    public FTPConnectionException(Throwable cause){
        super(cause);
    }

    public FTPConnectionException(String message){
        super(message);
    }

    public FTPConnectionException(String message,Throwable cause){
        super(message,cause);
    }

    protected FTPConnectionException(String message,Throwable cause,boolean enableSuppression,
            boolean writableStackTrace){
        super(message,cause,enableSuppression,writableStackTrace);
    }
}
