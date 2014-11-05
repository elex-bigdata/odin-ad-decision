package com.elex.odin.service;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.InputFeature;

/**
 * Author: liqiang
 * Date: 14-10-24
 * Time: 下午1:47
 */
public interface ADMatcher {

    public ADMatchMessage match(InputFeature userFeature) throws Exception;
}
