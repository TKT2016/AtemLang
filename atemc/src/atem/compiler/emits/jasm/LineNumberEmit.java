package atem.compiler.emits.jasm;
import org.objectweb.asm.*;
import java.util.HashMap;

/** 行号生成器 */
public class LineNumberEmit {
    /**
     * 行号对应表,保存生成过的行号,防止重复生成同一行
     */
    HashMap<Integer, Object> lineMaps = new HashMap<>();

    /** 根据行号和Label生成行号信息 */
    public void emitLineNumber(EmitContext arg, int line, Label lineLabel){
        /* 检查这个行号是否生成过，已经生成则返回 */
        if (lineMaps.containsKey(line))
            return;
        arg.mv.visitLineNumber(line, lineLabel);
        lineMaps.put(line, line);
    }

    /** 根据行号生成行号信息 */
    public void emitLineNumber(EmitContext arg, int line) {
        if (lineMaps.containsKey(line))
            return;
        /* 创建一个Label实例 */
        Label lineLabel = new Label();
        arg.mv.visitLabel(lineLabel);
        arg.mv.visitLineNumber(line, lineLabel);
        lineMaps.put(line, line);
    }
}
