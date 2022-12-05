package atem.compiler.tools.runs;

//import jdk.internal.org.objectweb.asm.ClassReader;
//import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassRuner
{
    public Object runBytes(ClassRunArg runArg ) throws Exception
    {
        if(runArg.checkBytes)
          checkJVMBytes(runArg.bytes);

        DynamicClassLoader cl = new DynamicClassLoader(runArg.classPaths,runArg);
        Class<?> clazz = cl.defineClass(runArg.className, runArg.bytes);
        Method main = null;
        try {
            main = clazz.getMethod(runArg.method, runArg.argTypes);
        }catch (NoSuchMethodException exception)
        {
            System.err.println("类"+runArg.className+"的"+runArg.method+"方法不存在或者参数错误");
            return null;
        }
        /* 调用默认构造函数反射生成实例，并调用其中的方法 */
        boolean isStatic = Modifier.isStatic(main.getModifiers());
        if(isStatic) {
            Object result =  main.invoke(null, new Object[]{new String[]{}});
            return result;
        }
        else
        {
            Object obj = clazz.newInstance();
            Object result =  main.invoke(obj,runArg.args);
            return result;
        }
    }

    /* 检查class文件正确性 */
    public static void checkJVMBytes(byte[] bytes)
    {
        PrintWriter pw = new PrintWriter(System.out);
        CheckClassAdapter.verify(new ClassReader(bytes), true, pw);
    }
}
