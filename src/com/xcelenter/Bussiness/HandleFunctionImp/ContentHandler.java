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

public class ContentHandler {
    public static void contentChangeHandler(int id) {
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
            String sql = "select * from content_info where id = " +id;
            resultSet = statement.executeQuery(sql);

            if(resultSet == null || !resultSet.next()){
                throw new Exception(ExceptionInfo.CONTENT_RECORD_NOT_FOUND);
            }
            String fullPath = resultSet.getString("fullpath");
            String textfrom = resultSet.getString("textfrom");
            String textto = resultSet.getString("textto");
            int absoluteoffset = resultSet.getInt("absoluteoffset");
            String proejctName = resultSet.getString("project");

            handleFileEdit(fullPath,textfrom,textto,absoluteoffset,proejctName);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.elegantlyClose(connection,statement,resultSet);
        }
    }

    private static void handleFileEdit(String fullPath,String textfrom,String textto,int absoluteoffset,String proejctName){
        CommonAttributes commonAttributes = CommonAttributes.getInstance();
        Solution currentSolution = commonAttributes.getCurrentSolution();
        if(currentSolution != null){
            Project project = currentSolution.getProject(proejctName);

            if(project !=null){
                CodeFile codeFile = project.getCodeFile(fullPath);
                if(codeFile != null){
                    //在数据库中 对应记录  被单引号包裹
                    codeFile.handleContentChange(textfrom.substring(1,textfrom.length()-1),textto.substring(1,textto.length()-1),absoluteoffset);
                }
            }

        }
    }
}
