package com.hino.dev.dashboardupdater;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Session {
    private SharedPreferences prefs;
    private Gson gson;

    public Session(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
    }

    public void set(String key, String value){
        prefs.edit().putString(key,value).commit();
    }

    public String get(String key){
        String value = prefs.getString(key,"");
        return value;
    }

    public void setInSection(ArrayList inList){
        String jsonValue = gson.toJson(inList);
        prefs.edit().putString("inList",jsonValue).commit();
    }

    public ArrayList<WipChassisNumber> getInSection(){
        String jsonvalue = prefs.getString("inList","");
        Type type = new TypeToken<ArrayList<WipChassisNumber>>() {}.getType();
        return gson.fromJson(jsonvalue, type);
    }
}
