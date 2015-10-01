package com.softech.ls360.exception;

/**
 * Copyright (c) 9/30/15, 360training.com. All Rights Reserved.
 * User: jeffreyhynes
 * Date: 9/30/15
 * com.softech.ls360.util
 * <p/>
 * Exception thrown to indicate that the response code from the ftp server is different
 * from what we expected.
 */
public class FTPCompletionException extends FTPClientException{
    private static final long serialVersionUID = - 960332223439666935L;

    public FTPCompletionException(){
        super();
    }

    public FTPCompletionException(Throwable cause){
        super(cause);
    }

    public FTPCompletionException(String message){
        super(message);
    }

    public FTPCompletionException(String message,Throwable cause){
        super(message,cause);
    }

    protected FTPCompletionException(String message,Throwable cause,boolean enableSuppression,
            boolean writableStackTrace){
        super(message,cause,enableSuppression,writableStackTrace);
    }
}
