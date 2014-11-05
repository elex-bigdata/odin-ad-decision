package com.elex.odin.expression;

/**
 * Author: liqiang
 * Date: 14-10-30
 * Time: 下午4:49
 */
public enum ExpressionOperator {

    AND("and"){
        public boolean compare(Object source, Object dest){
            return (Boolean)source && (Boolean) dest;
        }
    },OR("or"){
        public boolean compare(Object source, Object dest){
            return (Boolean)source || (Boolean) dest;
        }
    },LARGE(">"){
        public boolean compare(Object source, Object dest){
            return (Double)source > (Double) dest;
        }
    },LESS("<"){
        public boolean compare(Object source, Object dest){
            return (Double)source < (Double) dest;
        }
    },LARGE_EQ(">="){
        public boolean compare(Object source, Object dest){
            return (Double)source >= (Double) dest;
        }
    },LESS_EQ("<="){
        public boolean compare(Object source, Object dest){
            return (Double)source <= (Double) dest;
        }
    },EQU("="){
        public boolean compare(Object source, Object dest){
            return (Double)source == (Double) dest;
        }
    },NOT_EQU("<>"){
        public boolean compare(Object source, Object dest){
            return (Double)source != (Double) dest;
        }
    };

    private ExpressionOperator(String op){
        this.op = op;
    }
    private final String op;
    public abstract boolean compare(Object source, Object dest);
    public String getType(){
        return this.op;
    }

    public static ExpressionOperator getOperator(String op){
        op = op.toLowerCase();
        for(ExpressionOperator eo : ExpressionOperator.values()){
            if(eo.op.equals(op)){
                return eo;
            }
        }
        return null;
    }
}
