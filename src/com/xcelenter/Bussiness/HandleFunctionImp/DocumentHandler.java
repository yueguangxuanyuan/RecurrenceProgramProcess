package com.xcelenter.Bussiness.HandleFunctionImp;

import com.xcelenter.Common.CommonAttributes;
import com.xcelenter.Common.ExceptionInfo;
import com.xcelenter.Model.CodeFile;
import com.xcelenter.Model.Project;
import com.xcelenter.Model.Solution;
import com.xcelenter.Util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DocumentHandler {

    public static void documentSaveHandler(int id) {
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
            String sql = "select * from document where id = " +id;
            resultSet = statement.executeQuery(sql);

            if(resultSet == null || !resultSet.next()){
                throw new Exception(ExceptionInfo.DOCUMENT_RECORD_NOT_FOUND);
            }
            String fullPath = resultSet.getString("fullpath");
            String proejctName = resultSet.getString("project");

            Solution currentSolution = commonAttributes.getCurrentSolution();
            if(currentSolution != null){
                Project targetProject = currentSolution.getProject(proejctName);

                if(targetProject != null){
                    CodeFile codeFile = targetProject.getCodeFile(fullPath);
                    if(codeFile != null){
                        codeFile.flushContent();
                    }
                }
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
