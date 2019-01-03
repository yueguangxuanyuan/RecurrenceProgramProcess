package com.xcelenter.Util;

import java.io.File;
import java.util.Stack;

public class FileUtil {

    public static String convertRelativePathToRealPath(String relativePath){
        String[] fileArray = relativePath.split("\\\\");

        Stack<String> pathStack = new Stack<>();

        for(String fileName : fileArray){
            switch (fileName){
                case  ".":
                    continue;
                case "..":
                    pathStack.pop();
                    break;
                default:
                    pathStack.push(fileName);
                    break;
            }
        }

        String realPath = null;
        for(String fileName : pathStack){
            if(realPath == null){
                realPath = fileName;
            }else{
                realPath += File.separator + fileName;
            }
        }
        return realPath;
    }
}
