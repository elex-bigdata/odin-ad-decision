package com.elex.odin.expression;

import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-10-30
 * Time: 下午4:36
 */
public class ObjectExpression implements RuleExpression {

    private Object left;
    private Object right;
    private ExpressionOperator operater;

    public void init() throws Exception {
        if(left instanceof String){
            this.right = Double.parseDouble(right.toString());
        }else if( !(left instanceof RuleExpression) || !(right instanceof RuleExpression)){
            throw new Exception("The left object must be either string or expression");
        }

    }

    @Override
    public boolean match(Map<String,String> source) {
        if(left instanceof RuleExpression){
            operater.compare(((RuleExpression) left).match(source),right);
        }else if(left instanceof String){
            return operater.compare(left,right);
        }
        return false;
    }

    public Object getLeft() {
        return left;
    }

    public void setLeft(Object left) {
        this.left = left;
    }

    public Object getRight() {
        return right;
    }

    public void setRight(Object right) {
        this.right = right;
    }

    public ExpressionOperator getOperater() {
        return operater;
    }

    public void setOperater(String operater) throws Exception {
        this.operater = ExpressionOperator.getOperator(operater);
        if(this.operater == null){
            throw new Exception("Not support operator " + operater);
        }
    }
}
