package mods.battlegear2.client.utils;

import mods.battlegear2.utils.FileExtension;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aaron on 3/08/13.
 */
public class ImageFilter extends FileFilter {
    public static final List<String> DEFAULT = Arrays.asList("png", "tiff", "tif", "gif", "bmp", "jpeg", "jpg");
    private final List<String> valid;
    public ImageFilter(List<String> validExt){
        super();
        valid = validExt;
    }

    @Override
    public boolean accept(File pathname) {
        String extention = new FileExtension(pathname).get();
        return extention == null || valid.contains(extention.toLowerCase());
    }

    @Override
    public String getDescription() {
        return "Images";
    }
}
