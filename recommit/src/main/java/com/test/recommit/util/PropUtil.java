package com.test.recommit.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropUtil {

    private static PropUtil util = null;
    private static Properties pro = null;
    private static final String PROP_LOCATION = "/errors.properties";


    private PropUtil() {

    }

    public static final PropUtil getInstance() {
        if (util == null) {
            try {
                load(PROP_LOCATION);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new PropUtil();
        }
        return util;
    }

    private static void load(String path) throws IOException{
        pro = new Properties();
        InputStream inStream = PropUtil.class.getResourceAsStream(path);
        pro.load(inStream);
    }

    public static String getValue(String key){
        if(pro == null && pro.isEmpty())
            return "";
        return pro.getProperty(key);
    }

    public static void main(String[] args) {
        System.out.println(PropUtil.getInstance());
    }

}
