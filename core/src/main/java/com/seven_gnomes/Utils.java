package com.seven_gnomes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Utils {

    public static FileHandle getClassPath(String filePath){
        return Gdx.files.classpath(filePath);
    }

    public static FileHandle getInternalPath(String filePath){
        return Gdx.files.internal(filePath);
    }

    public static FileHandle getLocalPath(String filePath){
        return Gdx.files.local(filePath);
    }

    public static FileHandle getExternalPath(String filePath){
        return Gdx.files.external(filePath);
    }
}
