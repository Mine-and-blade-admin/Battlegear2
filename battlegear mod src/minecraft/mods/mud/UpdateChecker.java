package mods.mud;

import cpw.mods.fml.common.Loader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.*;

public class UpdateChecker implements Runnable{
    private Collection<UpdateEntry> updateEntries;

    public UpdateChecker(Collection<UpdateEntry> entries){
        updateEntries = entries;
    }


    @Override
    public void run() {
        //Map<String, Release> latestReleases = new HashMap<String, Release>();

        for(UpdateEntry entry:updateEntries){
            Release latest = getUpToDateRelease(
                    entry.getMc().getModId(),
                    Loader.instance().getMCVersionString().replaceAll("Minecraft ", ""),
                    Release.EnumReleaseType.Normal,
                    entry.getUpdateXML()
            );

            entry.setLatest(latest);
        }

        ModUpdateDetector.notifyUpdateDone();

    }

    public Release getUpToDateRelease(String modId, String targetMcVersion, Release.EnumReleaseType versionLevel, URL updateURL){
        try{

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(updateURL.openStream());

            NodeList mods = doc.getElementsByTagName("mod");

            TreeSet<Release> releases = new TreeSet<Release>();

            for(int i = 0; i < mods.getLength(); i++){
                Node mod = mods.item(i);
                if(mod.hasAttributes() &&
                        mod.getAttributes().getNamedItem("modid") != null &&
                        mod.getAttributes().getNamedItem("modid").getNodeValue().equals(modId)){

                    NodeList mcVersions = mod.getChildNodes();
                    for(int j = 0; j < mcVersions.getLength(); j++){
                        Node mcVersion = mcVersions.item(j);

                        if(mcVersion.hasAttributes() &&
                                mcVersion.getAttributes().getNamedItem("version") != null &&
                                mcVersion.getAttributes().getNamedItem("version").getNodeValue().equals(targetMcVersion)){

                            NodeList releasNodes = mcVersion.getChildNodes();
                            for(int k = 0; k < releasNodes.getLength(); k++){
                                Release release = parseNode(releasNodes.item(k), versionLevel);
                                if(release != null){
                                    releases.add(release);
                                }
                            }

                        }

                    }

                }
            }

            if(!releases.isEmpty()){
                return releases.last();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }



    private Release parseNode(Node item, Release.EnumReleaseType versionLevel) {
        if(item.hasAttributes()){

            Node versionNode = item.getAttributes().getNamedItem("version");
            Node typeNode = item.getAttributes().getNamedItem("type");
            String url = null;
            String download = null;
            String md5 = null;
            if(item.getAttributes().getNamedItem("url") != null){
                url = item.getAttributes().getNamedItem("url").getNodeValue();
            }
            if(item.getAttributes().getNamedItem("md5") != null){
                md5 = item.getAttributes().getNamedItem("md5").getNodeValue();
            }
            if(item.getAttributes().getNamedItem("download") != null){
                download = item.getAttributes().getNamedItem("download").getNodeValue();
            }
            Release.EnumReleaseType releaseType = Release.EnumReleaseType.Normal;
            if(typeNode != null){
                if(typeNode.getNodeValue().equalsIgnoreCase("beta")){
                    releaseType = Release.EnumReleaseType.Beta;
                }
                if(typeNode.getNodeValue().equalsIgnoreCase("dev")){
                    releaseType = Release.EnumReleaseType.Dev;
                }
            }

            if(versionNode != null && releaseType.level <= versionLevel.level){
                String[] split = versionNode.getNodeValue().toLowerCase(Locale.ENGLISH).split("\\.");
                int[] version = new int[split.length];
                for(int i = 0; i < split.length; i++){
                    try{
                        version[i] = Integer.parseInt(split[i]);
                    }catch (NumberFormatException e){
                        return null;
                    }
                }

                return new Release(releaseType, url, version, download, md5);
            }else{
                return null;
            }


        }else{
            return null;
        }
    }
}
