package mods.mud.utils;

import mods.mud.ModUpdateDetector;

import java.io.File;

public class FileDeleter {
    public static void main(String[] args){
        if(args.length > 0){
            File f = new File(args[0]);
            if(f.exists()){
                while(f.exists()){
                    f.delete();
                    ModUpdateDetector.logger.trace("File in use, try again in 1 second");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
