package mods.battlegear2.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class BattlegearLoadingPlugin implements IFMLLoadingPlugin{
	
	public static final String EntityPlayerTransformer = "mods.battlegear2.coremod.transformers.EntityPlayerTransformer";
	

	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{
				EntityPlayerTransformer
				};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

}
