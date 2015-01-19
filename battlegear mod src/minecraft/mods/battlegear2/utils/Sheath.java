package mods.battlegear2.utils;

import net.minecraft.client.resources.I18n;

import java.util.Locale;

/**
 * Created by GotoLink on 19/01/2015.
 */
public enum Sheath {
    BACK,HIP,NONE;

    public Sheath next(){
        if(this==BACK)
            return HIP;
        else if(this==HIP)
            return NONE;
        return BACK;
    }

    public String format(){
        return I18n.format("render.sheathed", toString().toLowerCase(Locale.ENGLISH));
    }

    public static String[] names(){
        return new String[]{"BACK", "HIP", "NONE"};
    }

    public static Sheath from(String text){
        try{
            return valueOf(text);
        }catch (Exception silent){
        }
        return NONE;
    }
}
