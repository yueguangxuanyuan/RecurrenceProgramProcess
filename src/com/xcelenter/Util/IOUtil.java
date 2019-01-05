package com.xcelenter.Util;

import java.io.FileWriter;
import java.io.IOException;

public class IOUtil {

    public static void elegantlyCloseFileWriter(FileWriter fileWriter){
        if(fileWriter != null){
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
