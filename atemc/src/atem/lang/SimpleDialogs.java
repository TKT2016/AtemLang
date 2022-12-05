package atem.lang;

import atem.lang.rt.AtemSourceGen;
import atem.lang.rt.VoidReturn;

import javax.swing.*;

public final class SimpleDialogs extends AtemSourceGen {
    public static Object alert(Object msg)
    {
        JOptionPane.showMessageDialog(null, msg, "Alert", JOptionPane.WARNING_MESSAGE);
       return   VoidReturn.ret;
    }

    public static Object confirm(Object msg)
    {
         JOptionPane.showConfirmDialog(null, msg, "Confirm", JOptionPane.DEFAULT_OPTION);
        return   VoidReturn.ret;
    }

    public static String prompt(Object msg)
    {
       var inputSTR = JOptionPane.showInputDialog(msg);//null, msg, "prompt", JOptionPane.INFORMATION_MESSAGE);
        return   inputSTR;
    }
}
