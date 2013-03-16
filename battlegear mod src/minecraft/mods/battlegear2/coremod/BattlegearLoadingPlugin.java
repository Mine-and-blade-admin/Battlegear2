package mods.battlegear2.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class BattlegearLoadingPlugin implements IFMLLoadingPlugin{

	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		System.out.println("Get Transformers");
		return null;
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
