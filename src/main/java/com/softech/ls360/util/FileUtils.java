package com.softech.ls360.util;

import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Copyright (c) 9/30/15, 360training.com. All Rights Reserved.
 * User: jeffreyhynes
 * Date: 9/30/15
 * com.softech.ls360.util
 * <p/>
 * Generic utility that reads files and creates there byte array data.
 */
public class FileUtils{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FTPClientUtil.class);

    public static final byte[] LINE_BREAK_BYTES = { ( byte )13,( byte )10 };

    public static String loadAsText(InputStream in,String encoding) throws IOException{
        return loadAsText(in,encoding,4096);
    }

    public static String loadAsText(InputStream in,String encoding,int bufferSize) throws IOException{
        InputStreamReader reader = new InputStreamReader(in,encoding);
        char[] buffer = new char[bufferSize];
        int offset = 0;
        for(;;){
            int remain = buffer.length - offset;
            if( remain <= 0 ){
                char[] newBuffer = new char[buffer.length * 2];
                System.arraycopy(buffer,0,newBuffer,0,offset);
                buffer = newBuffer;
                remain = buffer.length - offset;
            }
            int numRead = reader.read(buffer,offset,remain);
            if( numRead == - 1 ){
                break;
            }
            offset += numRead;
        }
        return new String(buffer,0,offset);
    }

    public static byte[] readFileAsBytes(String path,Integer start,Integer length){

        byte[] byteData = null;

        try{

            File file = new File(path);

            DataInputStream dis;
            dis = new DataInputStream(new FileInputStream(file));

            if( dis.available() > Integer.MAX_VALUE ){
                System.out.println("dis.available() > Integer.MAX_VALUE");
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream(length);
            byte[] bytes = new byte[length];

            dis.skipBytes(start);
            int readBytes = dis.read(bytes,0,length);
            os.write(bytes,0,readBytes);

            byteData = os.toByteArray();

            dis.close();
            os.close();
        }catch( Exception e ){
            System.out.println(e);
        }

        return byteData;
    }

    public static byte[] readFileAsBytes(File file,Integer start,Integer length){

        byte[] byteData = null;

        try{
            DataInputStream dis;
            dis = new DataInputStream(new FileInputStream(file));

            if( dis.available() > Integer.MAX_VALUE ){
                System.out.println("dis.available() > Integer.MAX_VALUE");
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream(length);
            byte[] bytes = new byte[length];

            dis.skipBytes(start);
            int readBytes = dis.read(bytes,0,length);
            os.write(bytes,0,readBytes);

            byteData = os.toByteArray();

            dis.close();
            os.close();
        }catch( Exception e ){
            System.out.println(e);
        }

        return byteData;
    }

    public final static byte[] load(String fileName){
        try{
            FileInputStream fin = new FileInputStream(fileName);
            return load(fin);
        }catch( Exception e ){

            return new byte[0];
        }
    }

    public final static byte[] load(File file){
        try{
            long fileLength = file.length();
            if( fileLength > Integer.MAX_VALUE ){
                throw new IOException("File '" + file.getName() + "' too big");
            }

            FileInputStream fin = new FileInputStream(file);
            return load(fin);
        }catch( Exception e ){

            return new byte[0];
        }
    }

    public static byte[] load(InputStream in) throws IOException{
        return load(in,4096);
    }

    public final static byte[] load(FileInputStream fin){
        byte readBuf[] = new byte[512 * 1024];

        try{
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            int readCnt = fin.read(readBuf);
            while( 0 < readCnt ){
                bout.write(readBuf,0,readCnt);
                readCnt = fin.read(readBuf);
            }

            fin.close();

            return bout.toByteArray();
        }catch( Exception e ){

            return new byte[0];
        }
    }

    public static byte[] load(InputStream in,byte[] buffer,int offset,int initialBufferSize) throws IOException{
        if( initialBufferSize == 0 ){
            initialBufferSize = 1;
        }
        if( buffer.length < offset + initialBufferSize ){
            initialBufferSize = buffer.length - offset;
        }
        for(;;){
            int numRead = in.read(buffer,offset,initialBufferSize);
            if( numRead == - 1 || numRead == initialBufferSize ){
                break;
            }
            offset += numRead;
        }
        if( offset < buffer.length ){
            byte[] newBuffer = new byte[buffer.length];
            System.arraycopy(buffer,offset,newBuffer,offset,initialBufferSize);
            buffer = newBuffer;
        }
        return buffer;
    }

    public static byte[] load(InputStream in,int initialBufferSize) throws IOException{
        if( initialBufferSize == 0 ){
            initialBufferSize = 1;
        }
        byte[] buffer = new byte[initialBufferSize];
        int offset = 0;
        for(;;){
            int remain = buffer.length - offset;
            if( remain <= 0 ){
                int newSize = buffer.length * 2;
                byte[] newBuffer = new byte[newSize];
                System.arraycopy(buffer,0,newBuffer,0,offset);
                buffer = newBuffer;
                remain = buffer.length - offset;
            }
            int numRead = in.read(buffer,offset,remain);
            if( numRead == - 1 ){
                break;
            }
            offset += numRead;
        }
        if( offset < buffer.length ){
            byte[] newBuffer = new byte[offset];
            System.arraycopy(buffer,0,newBuffer,0,offset);
            buffer = newBuffer;
        }
        return buffer;
    }

    public static byte[] loadExact(InputStream in,int length) throws IOException{
        byte[] buffer = new byte[length];
        int offset = 0;
        for(;;){
            int remain = length - offset;
            if( remain <= 0 ){
                break;
            }
            int numRead = in.read(buffer,offset,remain);
            if( numRead == - 1 ){
                throw new IOException("Reached EOF, read " + offset + " expecting " + length);
            }
            offset += numRead;
        }
        return buffer;
    }

    public static void save(File file,byte[] content) throws IOException{
        FileOutputStream out = new FileOutputStream(file);
        try{
            out.write(content);
        }finally{
            out.close();
        }
    }

    public static void save(String fileName,byte[] content) throws IOException{
        FileOutputStream out = new FileOutputStream(fileName);
        try{
            out.write(content);
        }finally{
            out.close();
        }
    }

    public static boolean fileExists(String fileName){
        File file = new File(fileName);
        return file.exists();
    }
}

