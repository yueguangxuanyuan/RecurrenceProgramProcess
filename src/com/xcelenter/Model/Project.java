package com.xcelenter.Model;

import com.xcelenter.Common.ConstantAttributes;

import java.util.HashMap;
import java.util.Map;

public class Project {
    private String projectName;
    private String projectPath;
    private String projectPhysicPath;
    private Map<String,String> fileMap;

    public Project(String projectPhysicPath){
        this.projectPhysicPath = projectPhysicPath;
        projectName = ConstantAttributes.UNKNOWN_PROJECTNAME;
        projectPath = ConstantAttributes.UNKNOWN_PATH;

        fileMap = new HashMap<>();
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void loadProject(){

    }
}
