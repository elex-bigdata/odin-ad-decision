package com.elex.odin.expression;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.io.StringReader;
import java.lang.reflect.Method;

/**
 * Author: liqiang
 * Date: 14-10-30
 * Time: 下午5:16
 */
public class RuleParser {

    public ObjectExpression parse(String rule) throws Exception {
        String sql = "select * from t " + rule;
        CCJSqlParserManager pm = new CCJSqlParserManager();
        PlainSelect xx = (PlainSelect)((Select) pm.parse(new StringReader(sql))).getSelectBody();
        Expression e = xx.getWhere();
        ObjectExpression objExpression = new ObjectExpression();
        generateList(e, objExpression);
        return objExpression;
    }

    private ObjectExpression generateList(Expression ex, ObjectExpression objExpression) throws Exception {
        if(ex==null){
            return objExpression;
        }
        if(ex instanceof OrExpression ||ex instanceof AndExpression){
            String operator = (ex instanceof OrExpression)?"OR":"AND";
            ObjectExpression left = new ObjectExpression();
            ObjectExpression right = new ObjectExpression();

            objExpression.setLeft(left);
            objExpression.setRight(right);
            objExpression.setOperater(operator);
            objExpression.init();

            BinaryExpression be = (BinaryExpression)ex;
            generateList(be.getLeftExpression(), left);
            generateList(be.getRightExpression(), right);

        }else if(ex instanceof Parenthesis){
            Expression exp = getExpressionWithoutParenthesis(ex);
            if(exp instanceof OrExpression||exp instanceof AndExpression){
                generateList(exp,objExpression);
            }else{
                generateList(exp,objExpression);
            }

        }else{
            processExpression(ex, objExpression);
            objExpression.init();
        }
        return objExpression;
    }

    private Expression getExpressionWithoutParenthesis(Expression ex){
        if(ex instanceof Parenthesis){
            Expression child = ((Parenthesis)ex).getExpression();
            return getExpressionWithoutParenthesis(child);
        }else{
            return ex;
        }

    }

    private Object invokeMethod(Object obj, String methodFunc){
        try {
            Method method = obj.getClass().getMethod(methodFunc, null);
            return method.invoke(obj, null);
        } catch (Exception e) {
            return null;
        }
    }

    private void processExpression(Expression e,ObjectExpression objExpression) throws Exception {
        ObjectExpression oe = new ObjectExpression();

        Column column = (Column)invokeMethod(e, "getLeftExpression");
        objExpression.setLeft(column.getColumnName());

        if (e instanceof BinaryExpression) {
            BinaryExpression be = (BinaryExpression) e;
            objExpression.setOperater(be.getStringExpression());
            if(be.getRightExpression() instanceof Function){
                objExpression.setRight(invokeMethod(be.getRightExpression(), "toString"));
            }else{
                objExpression.setRight(invokeMethod(be.getRightExpression(), "getValue"));
            }
        }else{
            oe.setOperater((String) invokeMethod(e, "toString"));
        }
    }
}
