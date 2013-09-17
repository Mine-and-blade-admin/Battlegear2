package mods.battlegear2.coremod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;

public class BattlegearLoadingPlugin implements IFMLLoadingPlugin {

    public static final String EntityPlayerTransformer = "mods.battlegear2.coremod.transformers.EntityPlayerTransformer";
    public static final String ModelBipedTransformer = "mods.battlegear2.coremod.transformers.ModelBipedTransformer";
    public static final String NetClientHandlerTransformer = "mods.battlegear2.coremod.transformers.NetClientHandlerTransformer";
    public static final String NetServerHandlerTransformer = "mods.battlegear2.coremod.transformers.NetServerHandlerTransformer";
    public static final String PlayerControllerMPTransformer = "mods.battlegear2.coremod.transformers.PlayerControllerMPTransformer";
    public static final String ItemRendererTransformer = "mods.battlegear2.coremod.transformers.ItemRendererTransformer";
    public static final String MinecraftTransformer = "mods.battlegear2.coremod.transformers.MinecraftTransformer";
    //public static final String RenderPlayerTransformer = "mods.battlegear2.coremod.transformers.RenderPlayerTransformer";
    public static final String ItemInWorldTransformer = "mods.battlegear2.coremod.transformers.ItemInWorldTransformer";
    public static final String EntityAIControlledTransformer = "mods.battlegear2.coremod.transformers.EntityAIControlledByPlayerTransformer";
    public static final String EntityOtherPlayerMPTransformer = "mods.battlegear2.coremod.transformers.EntityOtherPlayerMPTransformer";
    //public static final String EntityArrowTransformer = "mods.battlegear2.coremod.transformers.EntityArrowTransformer";
    public static final String EntityTrackerTransformer = "mods.battlegear2.coremod.transformers.EntityTrackerTransformer";
    //Setting this to true will enable the output of all edited classess as .class files
    //I will probably expose this via some sort of config to allow debugging of potential issues in the future
    public static boolean debug = false;
    public static File debugOutputLocation;
    
    public static final String[] transformers = 
    		new String[]{
		        EntityPlayerTransformer,
		        ModelBipedTransformer,
		        NetClientHandlerTransformer,
		        NetServerHandlerTransformer,
		        PlayerControllerMPTransformer,
		        ItemRendererTransformer,
		        MinecraftTransformer,
		        //RenderPlayerTransformer,
		        ItemInWorldTransformer,
		        EntityAIControlledTransformer,
		        EntityOtherPlayerMPTransformer,
		        //EntityArrowTransformer,
		        EntityTrackerTransformer
   			};

    @Override
    public String[] getLibraryRequestClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return transformers;
    }

    @Override
    public String getModContainerClass() {
        //return "mods.battlegear2.coremod.BattlegearCoremodContainer";
        return null;
    }

    @Override
    public String getSetupClass() {
        return "mods.battlegear2.coremod.BattlegearTranslator";
    }

    @Override
    public void injectData(Map<String, Object> data) {
    	debugOutputLocation = new File(data.get("mcLocation").toString(), "bg edited classess");
    }

}
