package com.xcelenter.Bussiness.HandleFunctionImp;

import com.xcelenter.Common.CommonAttributes;
import com.xcelenter.Common.ExceptionInfo;
import com.xcelenter.Model.Solution;
import com.xcelenter.Util.DBUtil;
import org.apache.commons.io.FileUtils;

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
}
