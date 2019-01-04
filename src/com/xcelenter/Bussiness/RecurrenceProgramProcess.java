package com.xcelenter.Bussiness;

import com.xcelenter.Bussiness.HandleFunctionImp.FileHandler;
import com.xcelenter.Bussiness.HandleFunctionImp.SolutionHandler;
import com.xcelenter.Common.ActionEnumClass;
import com.xcelenter.Common.CommonAttributes;
import com.xcelenter.Util.DBUtil;
import com.xcelenter.Util.FileUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class RecurrenceProgramProcess {

    Map<String,HandleFunctionInterface> handlerMap;

    public RecurrenceProgramProcess(){
        handlerMap = new HashMap<>();

        loadHandler();
    }

    public void reproduceProgramProcess(String sourceDir,String targetDir,String tmpDir){
        CommonAttributes commonAttributes = CommonAttributes.getInstance();
        commonAttributes.setDbFilePath(sourceDir+ File.separator +"\\Dao\\log.db");
        commonAttributes.setFileRootPath(sourceDir);
        commonAttributes.setOutPutRootPath(targetDir);
        commonAttributes.setTmpPath(tmpDir);

        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;

        int cursor = 0;
        int range = 100;

        try {
            con = DBUtil.getDBConnection(commonAttributes.getDbFilePath());
            statement = con.createStatement();
            String moduleSql = "select * from summary_info";

            boolean notEnd = true;
            while(notEnd){
                String sql = moduleSql + " limit "+cursor +","+range;
                resultSet = statement.executeQuery(sql);
                if(resultSet.isBeforeFirst()){
                    while(resultSet.next()){
                        int id = resultSet.getInt("id");
                        String action = resultSet.getString("action");

                        if(handlerMap.containsKey(action)){
                            handlerMap.get(action).handleEvent(id);
                        }
                    }
                    cursor += range;
                }else{
                    notEnd = false;
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.elegantlyClose(con,statement,resultSet);
        }
    }

    private void loadHandler(){
        handlerMap.put(ActionEnumClass.solutionOpen , SolutionHandler::solutionOpenHandler);

        handlerMap.put(ActionEnumClass.fileAddFile, FileHandler::fileAddHandler);
    }
}
