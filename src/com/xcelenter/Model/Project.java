package com.xcelenter.Model;

import com.xcelenter.Common.CommonAttributes;
import com.xcelenter.Common.ConstantAttributes;
import com.xcelenter.Util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;

public class Project {
    private String projectName;
    private String projectPath;
    private String projectPhysicPath;
    private Map<String,CodeFile> fileMap;

    public Project(String projectPhysicPath){
        this.projectPhysicPath = projectPhysicPath;
        projectName = ConstantAttributes.UNKNOWN_PROJECTNAME;
        projectPath = ConstantAttributes.UNKNOWN_PATH;

        fileMap = new HashMap<>();
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void addFile(String fileRealPath,String filePhysicPath){
        String pathSuffix = null;
        if(fileRealPath.startsWith(projectPath)){
            pathSuffix = fileRealPath.substring(projectPath.length() + 1);
        }else{
            pathSuffix = ConstantAttributes.PROJECT_OUT_FILE_DIRNAME + File.separator +
                    fileRealPath.substring(fileRealPath.lastIndexOf("\\")+1);
        }

        String targetFilePath = projectPhysicPath + File.separator + pathSuffix;
        targetFilePath = FileUtil.checkNextUseableFileName(targetFilePath);

        try {
            FileUtils.copyFile(new File(filePhysicPath),new File(targetFilePath));

            //判断是否是代码文件然后选择是否添加进项目
            if(!FileUtil.checkIsCppFile(targetFilePath)){
                return;
            }
            CodeFile codeFile = new CodeFile(targetFilePath);
            codeFile.setFilePath(fileRealPath);
            codeFile.setFileName(fileRealPath.substring(fileRealPath.lastIndexOf("\\")+1));
            fileMap.put(fileRealPath,codeFile);
        } catch (IOException e) {
            fileMap.put(fileRealPath,null);
        }

    }

    public CodeFile getCodeFile(String fileRealPath){
        return fileMap.get(fileRealPath);
    }

    public void removeFile(String fileRealPath){
        fileMap.remove(fileRealPath);
    }

    public void renameFile(String newFilePath,String oldName){
        String pathPrefix = newFilePath.substring(0,newFilePath.lastIndexOf("\\"));
        String newFileName = newFilePath.substring(newFilePath.lastIndexOf("\\")+1);

        String originFileRealPath = pathPrefix + File.separator + oldName;

        CodeFile theCodeFile = fileMap.get(originFileRealPath);
        if(theCodeFile != null){
            theCodeFile.setFileName(newFileName);
            theCodeFile.setFilePath(newFilePath);
            fileMap.remove(originFileRealPath);
            fileMap.put(newFilePath,theCodeFile);
        }
    }

    public void loadProject(){

        String filterFilePath = projectPhysicPath + File.separator + projectName + ".vcxproj.filters";
        File filterFile = new File(filterFilePath);
        if(!filterFile.exists()){
            return;
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(filterFilePath);

            Element root = document.getDocumentElement();
            NodeList itemGroupList = root.getElementsByTagName("ItemGroup");

            Map<String,String> fileRelativePathMap = new HashMap<>();

            Consumer<NodeList> parseFileFunction = (nodeList)->{
                for(int i = 0 ; i < nodeList.getLength() ; i++){
                    Element targetElement = (Element) nodeList.item(i);
                    String relativePath = targetElement.getAttribute("Include");
                    String fileRealPath = FileUtil.convertRelativePathToRealPath(projectPath + File.separator + relativePath);

                    String filePhysicPath = "";
                    NodeList filterList = targetElement.getElementsByTagName("Filter");
                    if(filterList.getLength() > 0){
                        filePhysicPath += File.separator + filterList.item(0).getTextContent();
                    }
                    int indexOfLastBackSplash = relativePath.lastIndexOf("\\");
                    String fileName = null;
                    if(indexOfLastBackSplash >= 0){
                        fileName = relativePath.substring(indexOfLastBackSplash+1);
                    }else{
                        fileName = relativePath;
                    }
                    filePhysicPath += File.separator + fileName;

                    fileRelativePathMap.put(fileRealPath,filePhysicPath);
                }
            };
            for(int i = 0 ; i < itemGroupList.getLength(); i++){
                Element itemGroup = (Element) itemGroupList.item(i);
                NodeList clCompileList = itemGroup.getElementsByTagName("ClCompile");
                parseFileFunction.accept(clCompileList);

                NodeList clIncludeList = itemGroup.getElementsByTagName("ClInclude");
                parseFileFunction.accept(clIncludeList);
            }

            /*
            还原物理路径
             */
            String tmpDirPath = CommonAttributes.getInstance().getTmpPath();

            File projectDir = new File(projectPhysicPath);
            File tmpDir = new File(tmpDirPath);
            File outFilesDir = new File(projectPhysicPath + File.separator + ConstantAttributes.PROJECT_OUT_FILE_DIRNAME);

            //初始化环境
            FileUtil.emptyTmpDir();
            FileUtils.copyDirectory(projectDir,tmpDir);
            FileUtils.cleanDirectory(projectDir);
            outFilesDir.mkdirs();

            //将vcxproj拷贝过来
            File[] sourceVCXFiles = tmpDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().contains("vcxproj");
                }
            });
            for(File vcxFile : sourceVCXFiles){
                FileUtils.copyFile(vcxFile,new File(projectPhysicPath+File.separator + vcxFile.getName()));
            }

            //将代码文件拷贝进来
            for(String fileRealPath : fileRelativePathMap.keySet()){
                String sourceFilePath = tmpDirPath + File.separator + fileRelativePathMap.get(fileRealPath);
                addFile(fileRealPath,sourceFilePath);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void flushProjectChange(){
        for(CodeFile codeFile : fileMap.values()){
            if(codeFile != null){
                codeFile.flushContent();
            }
        }
    }

    public String getProjectStructure(){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(projectName);
        stringBuilder.append("---");
        stringBuilder.append(projectPath);
        stringBuilder.append("\r\n");

        TreeSet<String> codeFileRelativePathSet = new TreeSet<>();
        for(String codeFilePath : fileMap.keySet()){
            StringBuilder fileRecord = new StringBuilder();
            CodeFile theCodeFile = fileMap.get(codeFilePath);
            if(theCodeFile == null){
                fileRecord.append("null");
            }else{
                fileRecord.append(theCodeFile.getFilePhysicPath().substring(projectPhysicPath.length()+1));
            }
            fileRecord.append("---");
            fileRecord.append(theCodeFile.getFileName());
            fileRecord.append("---");
            fileRecord.append(codeFilePath);

            codeFileRelativePathSet.add(fileRecord.toString());
        }

        for(String codeFileRecord : codeFileRelativePathSet){
            stringBuilder.append("\t");
            stringBuilder.append(codeFileRecord);
            stringBuilder.append("\r\n");
        }

        return stringBuilder.toString();
    }
}
