package mods.mud;

import cpw.mods.fml.common.Loader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;
import java.util.TreeSet;

public class UpdateChecker implements Runnable{
    private Collection<UpdateEntry> updateEntries;

    public UpdateChecker(Collection<UpdateEntry> entries){
        updateEntries = entries;
    }

    @Override
    public void run() {
        for(UpdateEntry entry:updateEntries){
            Release latest = getUpToDateRelease(
                    entry.getMc().getModId(),
                    Loader.MC_VERSION,
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
            NamedNodeMap nodeMap = item.getAttributes();
            String versionNode = getNodeValue(nodeMap, "version");
            if(versionNode == null)
                return null;
            String type = getNodeValue(nodeMap, "type");
            Release.EnumReleaseType releaseType = Release.EnumReleaseType.Normal;
            if(type != null){
                if(type.equalsIgnoreCase("beta")){
                    releaseType = Release.EnumReleaseType.Beta;
                }
                else if(type.equalsIgnoreCase("dev")){
                    releaseType = Release.EnumReleaseType.Dev;
                }
            }

            if(releaseType.level <= versionLevel.level){
                String[] split = versionNode.toLowerCase(Locale.ENGLISH).split("\\.");
                int[] version = new int[split.length];
                for(int i = 0; i < split.length; i++){
                    try{
                        version[i] = Integer.parseInt(split[i]);
                    }catch (NumberFormatException e){
                        return null;
                    }
                }
                String url = getNodeValue(nodeMap, "url");
                String download = getNodeValue(nodeMap, "download");
                String md5 = getNodeValue(nodeMap, "md5");

                return new Release(releaseType, url, version, download, md5);
            }
        }
        return null;
    }

    private String getNodeValue(NamedNodeMap map, String nodeName){
        return map.getNamedItem(nodeName) != null ? map.getNamedItem(nodeName).getNodeValue() : null;
    }
}
