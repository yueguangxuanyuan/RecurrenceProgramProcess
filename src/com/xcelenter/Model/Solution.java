package com.xcelenter.Model;

import com.xcelenter.Common.ConstantAttributes;
import com.xcelenter.Util.FileUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {
    private String solutionName;
    private String solutionPath;//在用户系统上的位置
    private String solutionPhysicPath;//在复原用户操作时，解决方案在磁盘上的位置
    private Map<String,Project> projectMap;

    public Solution(String solutionPhysicPath){
        this.solutionPhysicPath = solutionPhysicPath;

        solutionName = ConstantAttributes.UNKNOWN_SOLUTIONNAME;
        solutionPath = ConstantAttributes.UNKNOWN_PATH;

        projectMap = new HashMap<>();
    }


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

    public String getSolutionPhysicPath() {
        return solutionPhysicPath;
    }

    public void renameProject(String oldName, String newName){
        Project project = projectMap.get(oldName);
        project.setProjectName(newName);
        projectMap.remove(oldName);
        projectMap.put(newName,project);
    }


    /*
    初始化行为，
    根据sln，加载项目
     */
    public void loadSolution(){
        String slnFileFullPath = solutionPhysicPath + File.separator + solutionName +".sln";

        //Project("{8BC9CEB8-8B4A-11D0-8D11-00A0C91BC942}") = "TestAdd3", "TestAdd3\TestAdd3.vcxproj", "{B3243AD9-A09A-459F-9BC7-E03397766FAF}"

        try {
            BufferedReader br = new BufferedReader(new FileReader(slnFileFullPath));

            Pattern projectPattern = Pattern.compile("^Project\\(\".+\"\\) = \"(.+)\", \"(.+)\", \".+\"$");

            String line = null;
            while((line = br.readLine()) != null){
                Matcher matcher = projectPattern.matcher(line);
                if(matcher.find()){
                    String projectName = matcher.group(1);
                    String projectRelativePath = matcher.group(2);
                    projectRelativePath = solutionPath + File.separator + projectRelativePath;

                    String projectRealPath = FileUtil.convertRelativePathToRealPath(projectRelativePath);
                    String projectPhysicPath = solutionPhysicPath + File.separator + projectName;

                    //加载项目内容
                    Project project = new Project(projectPhysicPath);
                    project.setProjectName(projectName);
                    project.setProjectPath(projectRealPath.substring(0,projectRealPath.lastIndexOf("\\")));
                    project.loadProject();

                    projectMap.put(projectName,project);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
