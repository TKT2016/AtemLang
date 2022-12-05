package atem.lang.rt;


import atem.compiler.tools.ClazzUtil;

import java.lang.reflect.Field;

public class FieldRef implements VarRef {
    private final TypeLiteral typeLiteral;
    private  final String memberName;

    public FieldRef( TypeLiteral typeLiteral,String memberName)
    {
        this.typeLiteral=typeLiteral;
        this.memberName =memberName;
    }

    public Object getValue()
    {
        initMember();
        try {
            return  field.get(null);
        }catch (Exception ex)
        {
            throw new InterpreterError(ex);
        }
    }

    public void setValue(Object value)
    {
        initMember();
        try {
            field.set(null,value);
        }catch (Exception ex)
        {
            throw new InterpreterError(ex);
        }
    }


    boolean isReaded;
    Class clazz;
    Field field;

    void initMember()
    {
        if(!isReaded)
        {
            clazz = RTUtil.getClass(typeLiteral.clazz);
            field = ClazzUtil.findField(clazz,memberName);
            isReaded = true;
        }
    }
}
