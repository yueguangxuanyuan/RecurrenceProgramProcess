package com.xcelenter.Model;

import java.util.Map;

public class Solution {
    private String solutionName;
    private String solutionPath;
    private Map<String,Project> projectMap;


    public String getSolutionName() {
        return solutionName;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public void renameSolution(String solutionName){
        setSolutionName(solutionName);
    }

    public String getSolutionPath() {
        return solutionPath;
    }

    public void setSolutionPath(String solutionPath) {
        this.solutionPath = solutionPath;
    }

    public void addProject(Project project){
        projectMap.put(project.getProjectName(),project);
    }

    public void removeProject(String projectName){
        projectMap.remove(projectName);
    }

    public void renameProject(String oldName,String newName){
        Project project = projectMap.get(oldName);
        project.setProjectName(newName);
        projectMap.remove(oldName);
        projectMap.put(newName,project);
    }
}
