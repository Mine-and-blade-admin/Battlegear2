package mods.mud;

import mods.mud.exceptions.UnknownVersionFormatException;
import net.minecraftforge.fml.common.ModContainer;

import java.net.URL;

public class UpdateEntry{

    private final ModContainer mc;
    private final URL updateXML;
    private final URL changelogURL;
    private Release latest;
    private String fileName;

    public UpdateEntry(ModContainer mc, URL updateXML, URL changelogURL) {
        if(mc == null)
            throw new IllegalArgumentException("Mod wrapper can't be null");
        this.mc = mc;
        this.updateXML = updateXML;
        this.changelogURL = changelogURL;
    }

    public UpdateEntry(ModContainer mc, URL updateXML, URL changelogURL, String fileName){
        this(mc, updateXML, changelogURL);
        this.fileName = fileName;
    }

    public ModContainer getMc() {
        return mc;
    }

    public URL getUpdateXML() {
        return updateXML;
    }

    public URL getChangelogURL() {
        return changelogURL;
    }

    public Release getLatest() {
        return latest;
    }

    public void setLatest(Release latest) {
        this.latest = latest;
    }

    public String getFileName(String mcVersion){
        if(fileName==null)
            return String.format("[%s] %s - %s.jar",
                    mcVersion,
                    getMc().getName(),
                    getLatest()!=null ? getLatest().getVersionString() : "unknown_version");
        else
            return fileName;
    }

    public void setFileName(String newName){
        this.fileName = newName;
    }

    public boolean isUpToDate() throws UnknownVersionFormatException {

        try{
            String[] version_split = mc.getVersion().split("\\.");
            int[] version = new int[version_split.length];
            for(int i = 0; i < version.length; i++){
                version[i] = Integer.parseInt(version_split[i]);
            }
            Release thisVersion = new Release(Release.EnumReleaseType.Normal, null, version, null);

            return (thisVersion.compareTo(latest) >= 0);
        }
        catch(NumberFormatException e){
            throw new UnknownVersionFormatException();
        }
    }
}
