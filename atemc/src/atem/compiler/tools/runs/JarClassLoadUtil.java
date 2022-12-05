package atem.compiler.tools.runs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class JarClassLoadUtil {

    public static URLClassLoader loadJarFile(  File fileJar) //throws IOException
    {
        URLClassLoader urlClassLoader = null;
        //String substring = null;
        try {
            //通过URLClassLoader加载外部jar
            urlClassLoader = new URLClassLoader(new URL[]{new URL("file:" + fileJar.getAbsolutePath())});
        }
        catch (MalformedURLException e)
        {

        }
        return urlClassLoader;
    }

    public static ArrayList<URLClassLoader> loadFolder(  File folder )
    {
       // System.out.println("loadFolder 43 path :"+path);
       // File folder = new File(path);//File类型可以是文件也可以是文件夹
        //System.out.println("loadFolder 45 folder :"+folder);
        //System.out.println("loadFolder 45 folder.exists:"+folder.exists());

        File[] fileList = folder.listFiles();//将该目录下的所有文件放置在一个File类型的数组中
       // System.out.println("loadFolder 47 fileList :"+fileList);

        ArrayList<URLClassLoader> loaders = new ArrayList<>();
        if(fileList==null)
            return loaders;

        for(File file : fileList)
        {
            if(file.getName().toLowerCase().endsWith(".jar"))
            {
                URLClassLoader urlClassLoader = loadJarFile(file);
                if(urlClassLoader!=null)
                {
                    loaders.add(urlClassLoader);
                }
            }
        }
        return loaders;
    }

    /** 备份,暂时不用 */
    private void searchJarClass(String jarFileName,ClassLoader classLoader) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration<JarEntry> en = jarFile.entries();
        while (en.hasMoreElements()) {
            JarEntry je = en.nextElement();
            String name = je.getName();
            String s5 = name.replace('/', '.');
            if (s5.lastIndexOf(".class") > 0) {
                String className = je.getName().substring(0,
                        je.getName().length() - ".class".length()).replace('/',
                        '.');
                Class c = null;
                try {
                    c = classLoader.loadClass(className);
                    System.out.println(className);
                } catch (ClassNotFoundException e) {
                    System.err.println("NO CLASS: " + className);
                    // continue;
                } catch (NoClassDefFoundError e) {
                    System.err.println("NO CLASS: " + className);
                    // continue;
                }
//     callBack.operate(c);
            }
        }
    }

}
