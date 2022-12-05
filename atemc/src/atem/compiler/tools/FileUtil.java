package atem.compiler.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static boolean exists(String path)
    {
        File file = new File(path);
        return  file.exists();
    }

    public static String getNameNoExt(String path)
    {
        String path2 = path.replace('/','\\');
        return path2.substring(path2.lastIndexOf("\\")+1,path2.lastIndexOf("."));
    }

    public static String getCurrentPath()
    {
        File directory = new File("");//设定为当前文件夹
        try{
            String str=   directory.getAbsolutePath();//获取绝对路径
            return str;
        }catch(Exception e){}
        return "/";
    }

    public static byte[] readAllBytes(String filePath) throws IOException {
        Path path= Paths.get(filePath) ;
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    public static String readText(String filePath) throws IOException {
        byte[] data = readAllBytes(filePath);
        String result = new String(data, "utf-8");
        return result;
    }

    public static void saveText(String filePath, String content)  throws IOException
    {
        FileWriter fw = new FileWriter(filePath);
        BufferedWriter bfw = new BufferedWriter(fw);
        bfw.write(content, 0, content.length());
        bfw.flush();
        fw.close();
    }

    public static void saveText(File file, String content)  throws IOException
    {
        FileWriter fw = new FileWriter(file);
        BufferedWriter bfw = new BufferedWriter(fw);
        bfw.write(content, 0, content.length());
        bfw.flush();
        fw.close();
    }
}
