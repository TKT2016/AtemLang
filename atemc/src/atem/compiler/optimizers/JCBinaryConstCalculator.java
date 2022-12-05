package atem.compiler.optimizers;

import atem.compiler.ast.JCBinary;
import atem.compiler.ast.JCExpression;
import atem.compiler.utils.CompileError;

/** 二元运算表达式常量运算器 */
class JCBinaryConstCalculator {

    public JCExpression translate(JCBinary tree)
    {
        switch (tree.opcode) {
            case AND:
                return foldAnd(tree);
            case OR:
                return foldOr(tree);
            case ADD:
                return foldAdd(tree);
            case SUB:
                return foldSub(tree);
            case MUL:
                return foldMul(tree);
            case DIV:
                return foldDiv(tree);
            case GT:
            case GTEQ:
            case EQEQ:
            case LTEQ:
            case NOTEQ:
            case LT:
                return foldCompare(tree);
            default:
                throw new CompileError();
        }
    }

    /** 优化与逻辑运算 */
    JCExpression foldAnd(JCBinary tree) {
        Object leftValue = OptimizerUtil.getLiteralValue(tree.left);
        Object rightValue = OptimizerUtil.getLiteralValue(tree.right);
        /* 左右两个值，只有一个是false,则与运算必定是false,返回一个值为false的JCLiteral */
        if (OptimizerUtil.isFalse(leftValue)||OptimizerUtil.isFalse(rightValue) )
            return OptimizerUtil.newBooleanLiteral(tree, false);
        /* 两个都是常量值，进行与计算，对结果生成一个JCLiteral返回 */
         if (OptimizerUtil.isBoolean(leftValue) && OptimizerUtil.isBoolean(rightValue))
            return OptimizerUtil.newBooleanLiteral(tree, (Boolean) leftValue && (Boolean) rightValue);
        /* 其它情况,不进行优化，返回原来的tree */
        return tree;
    }

    /** 优化或逻辑运算 */
    JCExpression foldOr(JCBinary tree) {
        Object leftValue = OptimizerUtil.getLiteralValue(tree.left);
        Object rightValue = OptimizerUtil.getLiteralValue(tree.right);
        /* 左右两个值，只有一个是true,则与运算必定是true,返回一个值为true的JCLiteral */
        if (OptimizerUtil.isTrue(leftValue)||OptimizerUtil.isTrue(rightValue) )
            return OptimizerUtil.newBooleanLiteral(tree, false);
        /* 两个都是常量值，进行或计算，对结果生成一个JCLiteral返回 */
        if (OptimizerUtil.isBoolean(leftValue) && OptimizerUtil.isBoolean(rightValue))
            return OptimizerUtil.newBooleanLiteral(tree, (Boolean) leftValue || (Boolean) rightValue);
        /* 其它情况,不进行优化，返回原来的tree */
        return tree;
    }

    /* 优化加法运算 */
    JCExpression foldAdd(JCBinary tree)
    {
        Object leftValue = OptimizerUtil.getLiteralValue(tree.left);
        Object rightValue = OptimizerUtil.getLiteralValue(tree.right);
        if (tree.isStringContact) {
            if(leftValue !=null && rightValue!=null)
            {
                String newStringValue = leftValue.toString()+rightValue.toString();
                return OptimizerUtil.newStringLiteral(tree, newStringValue);
            }
        }
        else {
            if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
                return OptimizerUtil.newIntLiteral(tree, (Integer) leftValue + (Integer) rightValue);
            else if (OptimizerUtil.isIntZero(leftValue))
                return tree.right;
            else if (OptimizerUtil.isIntZero(rightValue))
                return tree.left;
        }
        return tree;
    }

    /* 优化减法运算 */
    JCExpression foldSub(JCBinary tree) {
        Object leftValue = OptimizerUtil.getLiteralValue(tree.left);
        Object rightValue = OptimizerUtil.getLiteralValue(tree.right);
        if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
            return OptimizerUtil.newIntLiteral(tree, (Integer) leftValue - (Integer) rightValue);
        else if (OptimizerUtil.isIntZero(rightValue))
            return tree.left;
        return tree;
    }

    /* 优化乘法运算 */
    JCExpression foldMul(JCBinary tree)
    {
        Object leftValue = OptimizerUtil.getLiteralValue(tree.left);
        Object rightValue = OptimizerUtil.getLiteralValue(tree.right);

        if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue))
            return OptimizerUtil.newIntLiteral(tree, (Integer) leftValue * (Integer) rightValue);
        else if(OptimizerUtil.isIntOne(leftValue))
            return tree.right;
        else if(OptimizerUtil.isIntOne(rightValue))
            return tree.left;
        else if(OptimizerUtil.isIntZero(leftValue)||OptimizerUtil.isIntZero(rightValue))
            return OptimizerUtil.newIntLiteral(tree, 0);
        return tree;
    }

    /* 优化除法运算 */
    JCExpression foldDiv(JCBinary tree)
    {
        Object leftValue = OptimizerUtil.getLiteralValue(tree.left);
        Object rightValue = OptimizerUtil.getLiteralValue(tree.right);

        if(OptimizerUtil.isInt(leftValue)&&OptimizerUtil.isInt(rightValue))
            return OptimizerUtil.newIntLiteral(tree, (Integer) leftValue / (Integer) rightValue);
        else if(OptimizerUtil.isIntOne(rightValue))
            return tree.left;
        return tree;
    }

    /** 优化比较运算 */
    JCExpression foldCompare(JCBinary tree) {
        Object leftValue = OptimizerUtil.getLiteralValue(tree.left);
        Object rightValue = OptimizerUtil.getLiteralValue(tree.right);
        if (OptimizerUtil.isInt(leftValue) && OptimizerUtil.isInt(rightValue)) {
            switch (tree.opcode) {
                case GT:
                    return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue > (Integer) rightValue);
                case GTEQ:
                    return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue >= (Integer) rightValue);
                case EQEQ:
                    return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue == (Integer) rightValue);
                case LTEQ:
                        return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue <= (Integer) rightValue);
                case NOTEQ:
                        return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue != (Integer) rightValue);
                case LT:
                        return OptimizerUtil.newBooleanLiteral(tree, (Integer) leftValue < (Integer) rightValue);
                default:
                    throw new CompileError();
            }
        }
        return tree;
    }
}
