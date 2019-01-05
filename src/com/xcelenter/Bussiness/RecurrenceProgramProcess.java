package com.xcelenter.Bussiness;

import com.xcelenter.Bussiness.HandleFunctionImp.ContentHandler;
import com.xcelenter.Bussiness.HandleFunctionImp.DocumentHandler;
import com.xcelenter.Bussiness.HandleFunctionImp.FileHandler;
import com.xcelenter.Bussiness.HandleFunctionImp.SolutionHandler;
import com.xcelenter.Common.ActionEnumClass;
import com.xcelenter.Common.CommonAttributes;
import com.xcelenter.Model.Solution;
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
        FileUtil.ensureThenEmptyDir(targetDir);
        commonAttributes.setOutPutRootPath(targetDir);
        FileUtil.ensureThenEmptyDir(tmpDir);
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

        Solution currentSolution = commonAttributes.getCurrentSolution();
        if(currentSolution != null){
            commonAttributes.setCurrentSolution(null);
        }
    }

    private void loadHandler(){
        handlerMap.put(ActionEnumClass.solutionOpen , SolutionHandler::solutionOpenHandler);
        handlerMap.put(ActionEnumClass.solutionClose,SolutionHandler::solutionCloseHandler);
        handlerMap.put(ActionEnumClass.solutionRename,SolutionHandler::solutionRenameHandler);

        handlerMap.put(ActionEnumClass.solAddProject,SolutionHandler::solAddProjectHandler);
        handlerMap.put(ActionEnumClass.solDelProject,SolutionHandler::solDelProjectHandler);
        handlerMap.put(ActionEnumClass.solRenameProject,SolutionHandler::solRenameProjectHandler);

        handlerMap.put(ActionEnumClass.fileAddFile, FileHandler::fileAddHandler);
        handlerMap.put(ActionEnumClass.fileDelFile, FileHandler::fileDelHandler);
        handlerMap.put(ActionEnumClass.fileRenameFile, FileHandler::fileRenameHandler);

        handlerMap.put(ActionEnumClass.contentInsert, ContentHandler::contentChangeHandler);
        handlerMap.put(ActionEnumClass.contentDelete, ContentHandler::contentChangeHandler);
        handlerMap.put(ActionEnumClass.contentReplace, ContentHandler::contentChangeHandler);

        handlerMap.put(ActionEnumClass.documentSave, DocumentHandler::documentSaveHandler);

    }
}
