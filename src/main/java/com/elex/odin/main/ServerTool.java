package com.elex.odin.main;
import com.elex.odin.service.ConfigurationManager;
import com.elex.odin.service.FeatureModelService;

/**
 * Author: liqiang
 * Date: 14-11-8
 * Time: 上午10:56
 */
public class ServerTool {

    public static void main(String[] args) throws Exception {
        if(args.length == 0){
            System.out.println("day");
            System.exit(-1);
        }
        String day = args[0];
        ConfigurationManager.updateFeatureAttribute();
        new FeatureModelService().updateModel(day);
    }

}
