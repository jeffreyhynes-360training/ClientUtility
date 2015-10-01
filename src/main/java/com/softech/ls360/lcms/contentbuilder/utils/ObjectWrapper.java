package com.softech.ls360.lcms.contentbuilder.utils;

/**
 * @author abdul.wahid
 *         This class is for alternate, for getting multiple values from a method
 *         Alternate of developing a new class for enclosing domain specific multiple values to be return from a method.
 */
public class ObjectWrapper<T extends Object>{
    private T value;

    public T getValue(){
        return value;
    }

    public void setValue(T value){
        this.value = value;
    }
}
