package com.xcelenter.Model;

import com.xcelenter.Util.LogUtil;

import java.io.*;

public class CodeFile {
    private String fileName;
    private String filePath;
    private String filePhysicPath;
    private StringBuilder content;
    private boolean isContentChanged;

    public CodeFile(String filePhysicPath){
        this.filePhysicPath = filePhysicPath;

        fileName = null;
        filePath = null;
        content = null;
        isContentChanged = false;

        loadContent();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePhysicPath() {
        return filePhysicPath;
    }

    public void setFilePhysicPath(String filePhysicPath) {
        this.filePhysicPath = filePhysicPath;
    }

    public void handleContentChange(String textFrom,String textTo,int absoluteoffset){
        if(absoluteoffset < 0 || absoluteoffset > content.length()){
            LogUtil.getInstance().logInfo(filePath + "-content change-absoluteoffset out of border");
            return;
        }
        //存在内容删除
        if(textFrom.length() > 0){
            if(absoluteoffset + textFrom.length() > content.length()){
                LogUtil.getInstance().logInfo(filePath + "-content change-textfrom out of border");
                return;
            }

            if(content.substring(absoluteoffset,absoluteoffset+textFrom.length()).equals(textFrom)){
                content = content.delete(absoluteoffset,absoluteoffset+textFrom.length());
                isContentChanged |= true;
            }else{
                LogUtil.getInstance().logInfo(filePath + "-content change-textfrom cannot pair");
                return;
            }
        }

        //存在增加内容
        if(textTo.length() > 0){
            content.insert(absoluteoffset,textTo);
            isContentChanged |= true;
        }
    }

    public void flushContent(){
        File theFile = new File(filePhysicPath);
        if(!theFile.exists()){
            theFile.getParentFile().mkdirs();
            try {
                theFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(isContentChanged){
            FileWriter fw =null;
            try {
                fw = new FileWriter(theFile,false);
                fw.write(content.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fw != null){
                    try {
                        fw.flush();
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    }

    private void loadContent(){
        File theFile = new File(filePhysicPath);

        content = new StringBuilder();

        if(theFile.exists()){
            try{
                BufferedReader br = new BufferedReader(new FileReader(theFile));
                String lineContent = null;
                while((lineContent = br.readLine()) != null){
                    content.append(lineContent);
                    content.append("\r\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
