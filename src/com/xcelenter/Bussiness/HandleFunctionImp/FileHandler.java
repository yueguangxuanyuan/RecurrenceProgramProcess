package com.xcelenter.Bussiness.HandleFunctionImp;

import com.xcelenter.Common.CommonAttributes;
import com.xcelenter.Common.ExceptionInfo;
import com.xcelenter.Model.Project;
import com.xcelenter.Model.Solution;
import com.xcelenter.Util.DBUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FileHandler {

    public static void fileAddHandler(int id) {
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
            String sql = "select * from file_event where id = " +id;
            resultSet = statement.executeQuery(sql);

            if(resultSet == null || !resultSet.next()){
                throw new Exception(ExceptionInfo.FILE_RECORD_NOT_FOUND);
            }
            String fullPath = resultSet.getString("filename");
            String targetFile = resultSet.getString("targetFile");
            String projectName = resultSet.getString("projectname");

            //解析文件位置
            String markString = "Roaming\\CppMonitor\\File\\middle_files\\";
            int markLocation = targetFile.indexOf(markString);
            String fileSuffix = targetFile.substring( markLocation+ markString.length());

            /*
            将文件拷贝到指定位置
             */
            String sourceFilePath = commonAttributes.getFileRootPath() + File.separator +"File\\middle_files\\"+ fileSuffix;
            Solution currentSolution = commonAttributes.getCurrentSolution();
            Project targetProject = currentSolution.getProject(projectName);

            if(targetProject != null){
                targetProject.addFile(fullPath,sourceFilePath);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.elegantlyClose(connection,statement,resultSet);
        }
    }
}
