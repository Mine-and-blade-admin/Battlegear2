package mods.battlegear2.coremod;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class BattlegearLoadingPlugin implements IFMLLoadingPlugin{
	
	public static final String EntityPlayerTransformer = "mods.battlegear2.coremod.transformers.EntityPlayerTransformer";
	public static final String ModelBipedTransformer = "mods.battlegear2.coremod.transformers.ModelBipedTransformer";
	public static final String NetClientHandlerTransformer = "mods.battlegear2.coremod.transformers.NetClientHandlerTransformer";
	public static final String NetServerHandlerTransformer = "mods.battlegear2.coremod.transformers.NetServerHandlerTransformer";
	public static final String PlayerControllerMPTransformer = "mods.battlegear2.coremod.transformers.PlayerControllerMPTransformer";
	public static final String ItemRendererTransformer = "mods.battlegear2.coremod.transformers.ItemRendererTransformer";

	
	//Setting this to true will enable the output of all edited classess as .class files
	//I will probably expose this via some sort of config to allow debugging of potential issues in the future
	public static boolean debug = false;
	
	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{
				EntityPlayerTransformer,
				ModelBipedTransformer,
				NetClientHandlerTransformer,
				NetServerHandlerTransformer,
				ItemRendererTransformer
				};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return "mods.battlegear2.coremod.BattleGearTranslator";
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

}
