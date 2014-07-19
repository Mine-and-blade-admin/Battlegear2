package mods.mud;

import cpw.mods.fml.common.ModContainer;
import mods.mud.exceptions.UnknownVersionFormatException;

import java.net.URL;

public class UpdateEntry{

    private final ModContainer mc;
    private final URL updateXML;
    private final URL changelogURL;
    private Release latest = null;

    public UpdateEntry(ModContainer mc, URL updateXML, URL changelogURL) {
        this.mc = mc;
        this.updateXML = updateXML;
        this.changelogURL = changelogURL;
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

    public boolean isUpToDate() throws UnknownVersionFormatException, NullPointerException {

        String[] version_split = mc.getVersion().split("\\.");
        int[] version = new int[version_split.length];

        try{
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
