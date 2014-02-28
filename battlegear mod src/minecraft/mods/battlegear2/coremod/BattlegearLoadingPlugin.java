package mods.battlegear2.coremod;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import mods.battlegear2.api.core.BattlegearTranslator;

@TransformerExclusions({"mods.battlegear2.coremod"})
@Name("Mine and Blade: Battlegear2")
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
    public static final String AccessTransformer = "mods.battlegear2.coremod.transformers.BattlegearAccessTransformer";
    public static final String EntityTrackerTransformer = "mods.battlegear2.coremod.transformers.EntityTrackerTransformer";
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
		        EntityTrackerTransformer
   			};

    @Override
    public String[] getASMTransformerClass() {
        return transformers;
    }

    @Override
    public String getAccessTransformerClass() { return AccessTransformer; }

    @Override
    public String getModContainerClass() {
        //return "mods.battlegear2.coremod.BattlegearCoremodContainer";
        return null;
    }

    @Override
    public String getSetupClass() {
        return "mods.battlegear2.api.core.BattlegearTranslator";
    }

    @Override
    public void injectData(Map<String, Object> data) {
    	debugOutputLocation = new File(data.get("mcLocation").toString(), "bg edited classes");
        BattlegearTranslator.obfuscatedEnv = Boolean.class.cast(data.get("runtimeDeobfuscationEnabled"));
    }

}
