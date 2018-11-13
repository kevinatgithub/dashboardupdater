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

    public void setUser(User user){
        String jsonValue = gson.toJson(user);
        prefs.edit().putString("user",jsonValue).commit();
    }

    public User getUser(){
        String jsonValue = prefs.getString("user","");
        if(jsonValue.equals("")){
            return null;
        }
        return gson.fromJson(jsonValue,User.class);
    }

    public void removeUser(){
        prefs.edit().remove("user").commit();
    }

    public void setSection(User.Section section){
        String jsonValue = gson.toJson(section);
        prefs.edit().putString("section",jsonValue).commit();
    }

    public User.Section getSection(){
        String jsonValue = prefs.getString("section","");
        if(jsonValue.equals("")){
            return null;
        }
        return gson.fromJson(jsonValue,User.Section.class);
    }

    public void removeSection(){
        prefs.edit().remove("section").commit();
    }

}
