module com.atemscript.atemide {
    exports atem.compiler;
    exports atem.lang.rt;
    exports atem.lang;
    exports atem.compiler.emits.jasm;
    exports atem.compiler.tools;

    requires org.objectweb.asm;
    requires org.objectweb.asm.util;
    requires java.desktop;
    requires ant;
    requires commons.cli;
    //requires javassist;
}