package com.elex.odin.expression;

import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-10-30
 * Time: 下午4:35
 */
public interface RuleExpression {

    public boolean match(Map<String,String> source);
}
