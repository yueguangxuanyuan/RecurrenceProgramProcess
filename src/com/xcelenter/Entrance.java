package com.xcelenter;

import com.xcelenter.Bussiness.RecurrenceProgramProcess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Entrance {

    public static void main(String[] args) throws IOException {
        InputStream inputStream = new FileInputStream("resource/config.properties");

        Properties properties = new Properties();
        properties.load(inputStream);

        String sourceDir = properties.getProperty("sourceDir");
        String targetDir = properties.getProperty("targetDir");
        String tmpDir = properties.getProperty("tmpDir");

        RecurrenceProgramProcess recurrenceProgramProcess = new RecurrenceProgramProcess();

        recurrenceProgramProcess.reproduceProgramProcess(sourceDir,targetDir,tmpDir);
    }
}
