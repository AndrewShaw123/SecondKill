package com.andrew.secondkill.util;

import java.util.UUID;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/23
 */
public class UUIDUtil {

    public static String uuid(){

        return UUID.randomUUID().toString().replace("-","");
    }

}
