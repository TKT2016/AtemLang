package atem.compiler.ast;


/** 语句父类 */
public abstract class JCStatement extends JCTree implements SourceFileSection
{
   public boolean isClinitStmt = false;
}
