package mods.battlegear2.coremod;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import mods.battlegear2.api.core.BattlegearTranslator;

import java.io.File;
import java.util.Map;

@TransformerExclusions({"mods.battlegear2.coremod"})
@Name("Mine and Blade: Battlegear2")
@SortingIndex(1500)
public final class BattlegearLoadingPlugin implements IFMLLoadingPlugin {

    public static final String EntityPlayerTransformer = "mods.battlegear2.coremod.transformers.EntityPlayerTransformer";
    public static final String ModelBipedTransformer = "mods.battlegear2.coremod.transformers.ModelBipedTransformer";
    public static final String NetClientHandlerTransformer = "mods.battlegear2.coremod.transformers.NetClientHandlerTransformer";
    public static final String NetServerHandlerTransformer = "mods.battlegear2.coremod.transformers.NetServerHandlerTransformer";
    public static final String PlayerControllerMPTransformer = "mods.battlegear2.coremod.transformers.PlayerControllerMPTransformer";
    public static final String ItemRendererTransformer = "mods.battlegear2.coremod.transformers.ItemRendererTransformer";
    public static final String MinecraftTransformer = "mods.battlegear2.coremod.transformers.MinecraftTransformer";
    public static final String ItemStackTransformer = "mods.battlegear2.coremod.transformers.ItemStackTransformer";
    public static final String ItemInWorldTransformer = "mods.battlegear2.coremod.transformers.ItemInWorldTransformer";
    public static final String EntityAIControlledTransformer = "mods.battlegear2.coremod.transformers.EntityAIControlledByPlayerTransformer";
    public static final String EntityOtherPlayerMPTransformer = "mods.battlegear2.coremod.transformers.EntityOtherPlayerMPTransformer";
    public static final String AccessTransformer = "mods.battlegear2.coremod.transformers.BattlegearAccessTransformer";
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
		        ItemStackTransformer,
		        ItemInWorldTransformer,
		        EntityAIControlledTransformer,
		        EntityOtherPlayerMPTransformer,
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
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    	debugOutputLocation = new File(data.get("mcLocation").toString(), "bg edited classes");
        BattlegearTranslator.obfuscatedEnv = Boolean.class.cast(data.get("runtimeDeobfuscationEnabled"));
    }

}
