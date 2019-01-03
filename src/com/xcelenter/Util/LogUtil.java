package com.xcelenter.Util;

import java.time.LocalDateTime;

/*
自定义的日志，
将解析过程的关键事件进行输出
 */
public class LogUtil {

    private static  LogUtil logUtil;

    private LogUtil(){

    }

    public static LogUtil getInstance(){
        if(logUtil == null){
            logUtil = new LogUtil();
        }

        return logUtil;
    }

    public void logInfo(String msg){
        //暂时输出到Console
        String formattedMsg = LocalDateTime.now().toLocalTime().toString() +"-"+msg;
        System.out.println(formattedMsg);
    }
}
