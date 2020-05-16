package com.andrew.secondkill.util;

import org.apache.commons.codec.digest.DigestUtils;
import sun.applet.Main;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/22
 */
public class MD5Util {

    private static final String SALT = "pooh";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }



    public static String inputPassToFormPass(String inputPass) {
        String str = ""+SALT.charAt(0) + inputPass +SALT.charAt(3) + SALT.charAt(2);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String str = ""+SALT.charAt(0) + formPass +SALT.charAt(3) + SALT.charAt(2);
        return md5(str);
    }

    public static String inputPassToDbPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }


}
