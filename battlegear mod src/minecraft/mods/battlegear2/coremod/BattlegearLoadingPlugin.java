package mods.battlegear2.coremod;

import mods.battlegear2.api.core.BattlegearTranslator;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

import java.io.File;
import java.util.Map;

@TransformerExclusions({"mods.battlegear2.coremod"})
@Name("Mine and Blade: Battlegear2")
@SortingIndex(1500)
public final class BattlegearLoadingPlugin implements IFMLLoadingPlugin {

    private static final String AccessTransformer = "mods.battlegear2.coremod.transformers.BattlegearAccessTransformer";
    private static final String[] transformers =
    		new String[]{
                    "mods.battlegear2.coremod.transformers.EntityPlayerTransformer",
                    "mods.battlegear2.coremod.transformers.ModelBipedTransformer",
                    "mods.battlegear2.coremod.transformers.ItemRendererTransformer",
                    "mods.battlegear2.coremod.transformers.NetClientHandlerTransformer",
                    "mods.battlegear2.coremod.transformers.NetServerHandlerTransformer"
   			};

    public static File debugOutputLocation;

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
