package atem.compiler.tools;

import atem.compiler.ast.JCExpression;
import atem.compiler.ast.JCTree;
import atem.compiler.ast.callables.proc.ProcItemParameter;

import java.util.*;

public abstract class ListUtil {
    public static <T> ArrayList<T> removeDuplicateWithOrder(ArrayList<T> list) {
        Set set = new HashSet();
        ArrayList<T> newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            T element = (T)iter.next();
            if (set.add(element))
                newList.add(element);
        }
        return newList;
    }

    public static JCExpression[] toExprArray(ArrayList<JCExpression> elements)
    {
        JCExpression[] array = new JCExpression[elements.size()];
        for (int i=0;i<elements.size();i++)
        {
            array[i] =elements.get(i);
        }
        return array;
    }

    public static ProcItemParameter[] toProcItemParameter(ArrayList<ProcItemParameter> elements)
    {
        ProcItemParameter[] array = new ProcItemParameter[elements.size()];
        for (int i=0;i<elements.size();i++)
        {
            array[i] =elements.get(i);
        }
        return array;
    }

    public static JCTree[] toAstArray(ArrayList<JCTree> elements)
    {
        JCTree[] array = new JCTree[elements.size()];
        for (int i=0;i<elements.size();i++)
        {
            array[i] =elements.get(i);
        }
        return array;
    }

    public static String join(ArrayList<String> list,String splitor)
    {
        if(list==null) return  "";
        if(list.size()==0) return "";
        if(list.size()==1) return list.get(0);
        StringBuilder builder = new StringBuilder();
        builder.append(list.get(0));
        for(int i=0;i<list.size();i++)
        {
            builder.append(splitor);
            builder.append(list.get(i));
        }
        return builder.toString();
    }

    public static String join(String[] array,String splitor)
    {
        if(array==null) return  "";
        int size = array.length;
        if(size==0) return "";
        if(size==1) return array[0];
        StringBuilder builder = new StringBuilder();
        builder.append( array[0]);
        for(int i=0;i<size;i++)
        {
            builder.append(splitor);
            builder.append(array[i]);
        }
        return builder.toString();
    }
}
