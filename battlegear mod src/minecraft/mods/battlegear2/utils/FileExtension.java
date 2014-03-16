package mods.battlegear2.utils;

import java.io.File;

public class FileExtension extends File{
    public FileExtension(File file){
        super(file.getAbsolutePath());
    }

    public FileExtension(String path){
        super(path);
    }

    public String get(){
        int index = this.getPath().lastIndexOf('.');
        if(index >= 0){
            return this.getPath().substring(index + 1);
        }else{
            return null;
        }
    }

    public boolean isImage(){
        return get()!=null && (get().equalsIgnoreCase("jpg")||get().equalsIgnoreCase("jpeg")||get().equalsIgnoreCase("gif")||get().equalsIgnoreCase("png"));
    }
}
