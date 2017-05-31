package com.mibo.fishtank.FishTankmManage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Monty on 2017/6/1.
 */

public class SharedPreferencesUtil {
    private static String database = "FishTank";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    /**
     * 保存数据的方法，拿到数据保存数据的基本类型，然后根据类型调用不同的保存方法
     *
     * @param key
     */
    public static boolean putString(Context context,String key, String value) {
        sharedPreferences = context.getSharedPreferences(database, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }


    public static String getString(Context context,String key){
        sharedPreferences = context.getSharedPreferences(database, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

}
