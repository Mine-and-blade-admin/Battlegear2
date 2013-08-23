package mods.battlegear2.utils;


import mods.battlegear2.Battlegear;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.Arrays;
import java.util.TreeSet;

public class BattlegearUpdateChecker {
    private final String updateURL;

    public static void main(String[] arge){
        BattlegearUpdateChecker buc = new BattlegearUpdateChecker("https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/battlegear_update.xml");

        System.out.println(buc.getUpToDateRelease("battlegear2", "1.5.2", Battlegear.debug?Release.EnumReleaseType.Dev:Release.EnumReleaseType.Normal));
    }

    public BattlegearUpdateChecker(String updateUrl){
        this.updateURL = updateUrl;
    }


    public Release getUpToDateRelease(String modId, String targetVersion, Release.EnumReleaseType versionLevel){
        try{

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(updateURL).openStream());

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
                                mcVersion.getAttributes().getNamedItem("version").getNodeValue().equals(targetVersion)){

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

            if(releases.isEmpty()){

            }else{
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
            if(item.getAttributes().getNamedItem("url") != null){
                url = item.getAttributes().getNamedItem("url").getNodeValue();
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
                String[] split = versionNode.getNodeValue().toLowerCase().split("\\.");
                int[] version = new int[split.length];
                for(int i = 0; i < split.length; i++){
                    try{
                        version[i] = Integer.parseInt(split[i]);
                    }catch (NumberFormatException e){
                        return null;
                    }
                }

                return new Release(releaseType, url, version, download);
            }else{
                return null;
            }


        }else{
            return null;
        }
    }
}
