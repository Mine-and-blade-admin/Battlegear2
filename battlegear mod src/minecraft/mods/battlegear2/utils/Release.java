package mods.battlegear2.utils;

import java.util.Arrays;

public  class Release implements Comparable<Release>{
    public int[] version;
    public EnumReleaseType type;
    public String url;

    public Release(EnumReleaseType type, String url, int[] version) {
        this.type = type;
        this.url = url;
        this.version = version;
    }

    @Override
    public int compareTo(Release other) {

        for(int i = 0; i < version.length && i < other.version.length; i++){
            if(version[i] > other.version[i]){
                return 1;
            }else if (version[i] < other.version[i]){
                return -1;
            }
        }

        return version.length - other.version.length;
    }

    @Override
    public String toString() {
        return "Release{" +
                "type=" + type +
                ", version=" + Arrays.toString(version) +
                ", url='" + url + '\'' +
                '}';
    }


    public static enum EnumReleaseType{
        Normal(1),
        Beta(2),
        Dev(3);

        public int level;

        private EnumReleaseType(int level){
            this.level = level;
        }
    }
}
