package com.xcelenter.Common;

import com.xcelenter.Model.Solution;

public class CommonAttributes {

    private static CommonAttributes commonAttributes;
    private String dbFilePath;
    private String fileRootPath;

    private String outPutRootPath;
    private String tmpPath;

    private Solution currentSolution;

    private  CommonAttributes(){
        dbFilePath = null;
        fileRootPath = null;
    }

    public static CommonAttributes getInstance(){
        if(commonAttributes == null){
            commonAttributes = new CommonAttributes();
        }

        return commonAttributes;
    }

    public String getDbFilePath() {
        return dbFilePath;
    }

    public void setDbFilePath(String dbFilePath) {
        this.dbFilePath = dbFilePath;
    }

    public String getFileRootPath() {
        return fileRootPath;
    }

    public void setFileRootPath(String fileRootPath) {
        this.fileRootPath = fileRootPath;
    }

    public String getOutPutRootPath() {
        return outPutRootPath;
    }

    public void setOutPutRootPath(String outPutRootPath) {
        this.outPutRootPath = outPutRootPath;
    }

    public Solution getCurrentSolution() {
        return currentSolution;
    }

    public void setCurrentSolution(Solution currentSolution) {
        if(this.currentSolution != null){
            this.currentSolution.flushSolutionChange();
        }
        this.currentSolution = currentSolution;
    }

    public String getTmpPath() {
        return tmpPath;
    }

    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }
}
