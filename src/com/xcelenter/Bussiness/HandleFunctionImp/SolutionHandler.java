package com.xcelenter.Bussiness.HandleFunctionImp;

import com.xcelenter.Common.CommonAttributes;
import com.xcelenter.Common.ExceptionInfo;
import com.xcelenter.Model.Project;
import com.xcelenter.Model.Solution;
import com.xcelenter.Util.DBUtil;
import jdk.nashorn.internal.parser.JSONParser;
import netscape.javascript.JSObject;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SolutionHandler{

    public static void solutionOpenHandler(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            CommonAttributes commonAttributes = CommonAttributes.getInstance();
            connection = DBUtil.getDBConnection(commonAttributes.getDbFilePath());
            statement = connection.createStatement();

            /*
            检索出solution记录
             */
            String sql = "select * from solution_open_event where id = " +id;
            resultSet = statement.executeQuery(sql);

            if(resultSet == null || !resultSet.next()){
                throw new Exception(ExceptionInfo.SOLUTION_RECORD_NOT_FOUND);
            }
            String solutionName = resultSet.getString("solutionname");
            String fullPath = resultSet.getString("fullpath");
            String rootFolder = resultSet.getString("targetfolder");

            //解析文件位置
            String markString = "Roaming\\CppMonitor\\File\\start_files\\";
            int markLocation = rootFolder.indexOf(markString);
            String fileSuffix = rootFolder.substring( markLocation+ markString.length());

            /*
            将文件拷贝到指定位置
             */
            String sourceDirPath = commonAttributes.getFileRootPath() + File.separator +"File\\start_files\\"+ fileSuffix;
            String targetDirPath = commonAttributes.getOutPutRootPath() + File.separator + fileSuffix;

            FileUtils.copyDirectory(new File(sourceDirPath),new File(targetDirPath));

            /*
            维护上下文
             */
            Solution solution = new Solution(targetDirPath);
            solution.setSolutionName(solutionName);
            solution.setSolutionPath(fullPath.substring(0,fullPath.lastIndexOf("\\")));

            solution.loadSolution();

            commonAttributes.setCurrentSolution(solution);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.elegantlyClose(connection,statement,resultSet);
        }
    }

    public static void solutionCloseHandler(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            CommonAttributes commonAttributes = CommonAttributes.getInstance();
            connection = DBUtil.getDBConnection(commonAttributes.getDbFilePath());
            statement = connection.createStatement();

            /*
            检索出solution记录
             */
            String sql = "select * from solution_open_event where id = " +id;
            resultSet = statement.executeQuery(sql);

            if(resultSet == null || !resultSet.next()){
                throw new Exception(ExceptionInfo.SOLUTION_RECORD_NOT_FOUND);
            }
            String solutionName = resultSet.getString("solutionname");

            Solution currentSolution = commonAttributes.getCurrentSolution();
            if(currentSolution == null){
                throw new Exception(ExceptionInfo.CURRENT_SOLUTION_NOT_EXIST);
            }

            if(! solutionName .equals(currentSolution.getSolutionName())){
                throw new Exception(ExceptionInfo.SOLUTION_NOT_MATCH);
            }

            commonAttributes.setCurrentSolution(null);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.elegantlyClose(connection,statement,resultSet);
        }
    }


    public static void solutionRenameHandler(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            CommonAttributes commonAttributes = CommonAttributes.getInstance();
            connection = DBUtil.getDBConnection(commonAttributes.getDbFilePath());
            statement = connection.createStatement();

            /*
            检索出solution记录
             */
            String sql = "select * from solution_open_event where id = " +id;
            resultSet = statement.executeQuery(sql);

            if(resultSet == null || !resultSet.next()){
                throw new Exception(ExceptionInfo.SOLUTION_RECORD_NOT_FOUND);
            }
            String newSolutionName = resultSet.getString("solutionname");
            String oldName = resultSet.getString("info");
            oldName = oldName.substring(oldName.lastIndexOf("\\")+1,oldName.lastIndexOf(".sln"));

            Solution currentSolution = commonAttributes.getCurrentSolution();
            if(currentSolution == null){
                throw new Exception(ExceptionInfo.CURRENT_SOLUTION_NOT_EXIST);
            }

            if(! oldName .equals(currentSolution.getSolutionName())){
                throw new Exception(ExceptionInfo.SOLUTION_NOT_MATCH);
            }

            currentSolution.setSolutionName(newSolutionName);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.elegantlyClose(connection,statement,resultSet);
        }
    }

    public static void solAddProjectHandler(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            CommonAttributes commonAttributes = CommonAttributes.getInstance();
            connection = DBUtil.getDBConnection(commonAttributes.getDbFilePath());
            statement = connection.createStatement();

            /*
            检索出solution记录
             */
            String sql = "select * from solution_open_event where id = " +id;
            resultSet = statement.executeQuery(sql);

            if(resultSet == null || !resultSet.next()){
                throw new Exception(ExceptionInfo.SOLUTION_RECORD_NOT_FOUND);
            }

            String projectFullPath = resultSet.getString("info");
            String projectSourcePath = resultSet.getString("targetfolder");

            Solution currentSolution = commonAttributes.getCurrentSolution();
            if(currentSolution == null){
                throw new Exception(ExceptionInfo.CURRENT_SOLUTION_NOT_EXIST);
            }

            //将项目文件拷贝至目标目录
            String projectName = projectFullPath.substring(projectFullPath.lastIndexOf("\\")+1,projectFullPath.indexOf(".vcxproj"));
            String projectRealPath = projectFullPath.substring(0,projectFullPath.lastIndexOf("\\"));

            String mark = "Roaming\\CppMonitor\\File\\middle_files\\";
            String fileSuffix = projectSourcePath.substring(projectSourcePath.indexOf(mark)+ mark.length());

            String projectFileOriginPath = commonAttributes.getFileRootPath() + "\\File\\middle_files\\" + fileSuffix;
            String targetDir = currentSolution.getSolutionPhysicPath() + File.separator + projectName;

            FileUtils.copyDirectory(new File(projectFileOriginPath),new File(targetDir));

            //加载项目内容，并配置好环境
            Project project = new Project(targetDir);
            project.setProjectPath(projectRealPath);
            project.setProjectName(projectName);
            project.loadProject();

            currentSolution.addProject(project);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.elegantlyClose(connection,statement,resultSet);
        }
    }

    public static void solDelProjectHandler(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            CommonAttributes commonAttributes = CommonAttributes.getInstance();
            connection = DBUtil.getDBConnection(commonAttributes.getDbFilePath());
            statement = connection.createStatement();

            /*
            检索出solution记录
             */
            String sql = "select * from solution_open_event where id = " +id;
            resultSet = statement.executeQuery(sql);

            if(resultSet == null || !resultSet.next()){
                throw new Exception(ExceptionInfo.SOLUTION_RECORD_NOT_FOUND);
            }

            String projectName = resultSet.getString("info");

            Solution currentSolution = commonAttributes.getCurrentSolution();
            if(currentSolution == null){
                throw new Exception(ExceptionInfo.CURRENT_SOLUTION_NOT_EXIST);
            }

            currentSolution.removeProject(projectName);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.elegantlyClose(connection,statement,resultSet);
        }
    }

    public static void solRenameProjectHandler(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            CommonAttributes commonAttributes = CommonAttributes.getInstance();
            connection = DBUtil.getDBConnection(commonAttributes.getDbFilePath());
            statement = connection.createStatement();

            /*
            检索出solution记录
             */
            String sql = "select * from solution_open_event where id = " +id;
            resultSet = statement.executeQuery(sql);

            if(resultSet == null || !resultSet.next()){
                throw new Exception(ExceptionInfo.SOLUTION_RECORD_NOT_FOUND);
            }

            String info = resultSet.getString("info");

            JSONObject jsonObject = new JSONObject(info);
            String oldName = jsonObject.getString("OldName");
            String newName = jsonObject.getString("NewName");

            Solution currentSolution = commonAttributes.getCurrentSolution();
            if(currentSolution == null){
                throw new Exception(ExceptionInfo.CURRENT_SOLUTION_NOT_EXIST);
            }

            currentSolution.renameProject(oldName,newName);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.elegantlyClose(connection,statement,resultSet);
        }
    }
}
