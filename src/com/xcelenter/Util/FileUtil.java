package com.xcelenter.Util;

import com.xcelenter.Common.CommonAttributes;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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


    public static void emptyTmpDir(){
        CommonAttributes commonAttributes = CommonAttributes.getInstance();

        String dirPath = commonAttributes.getTmpPath();
        File targetDir = new File(dirPath);
        if(targetDir.exists()){
            try {
                FileUtils.cleanDirectory(targetDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            targetDir.mkdirs();
        }
    }

    public static String checkNextUseableFileName(String fileName){
        boolean needCheck = true;
        int mark = 0 ;
        File file = null;
        while(needCheck){
            file = new File(fileName + (mark>0?("-"+mark):""));
            if(file.exists()){
                mark++;
            }else{
                needCheck = false;
            }
        }

        //确保父级路径存在
        file.getParentFile().mkdirs();

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }
}
