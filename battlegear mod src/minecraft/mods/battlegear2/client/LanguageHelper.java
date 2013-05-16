package mods.battlegear2.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Helper class to assist M&B:B2 in loading all the required language files
 * @author nerd-boy
 */
public class LanguageHelper {

	/**
	 * Loads all the languages from the lang/Mine & Blade Folder
	 */
	public static void loadAllLanguages(){

		loadDefault();

		File folder = getOrGenerateLangFolder();

		readAllLanguages(folder);
	}

	/**
	 * Loads the default language file
	 */
	private static void loadDefault() {
		System.out.println("Mine & Blade: Loading Default Language");
		String line = null;
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(LanguageHelper.class.getResourceAsStream("en_US.lang")));
			while ((line = reader.readLine()) != null) {
		    	String[] split = line.split("=");
		    	if(split.length == 2){
		    		if(split[0].startsWith("item.")){
		    			split[0] = split[0].replace("item.", "item.battlegear2:");
		    		}
		    		LanguageRegistry.instance().addStringLocalization(split[0], split[1]);
		    	}

		    }
		}catch(Exception e){
				e.printStackTrace();
		}
	}

	/**
	 * Reads all the .lang files from the given folder
	 * @param folder the folder to read the lang files from
	 */
	private static void readAllLanguages(File folder) {
		BufferedReader reader = null;

		if(folder.exists()){
			for (File langFile : folder.listFiles()) {
				if(langFile.getName().endsWith(".lang")){
					try{
					reader = new BufferedReader(new FileReader(langFile));
					String l = langFile.getName().substring(0, langFile.getName().length()-5);

					String line = null;
				    while ((line = reader.readLine()) != null) {
				    	String[] split = line.split("=");
				    	if(split.length == 2){
				    		if(split[0].startsWith("item.")){
				    			split[0] = split[0].replace("item.", "item.battlegear2:");
				    		}
				    		LanguageRegistry.instance().addStringLocalization(split[0], l, split[1]);
				    	}
				    }

				    System.out.println("Mine & Blade: Loaded Language "+l);

					}catch(Exception e){}
					finally{
						if(reader!=null){
							try{
								reader.close();
							}catch(Exception e){}
						}
					}
				}
			}
		}

	}

	/**
	 * Retrieves the folder containing the .lang files or generates it if it does not exist
	 * @return
	 */
	private static File getOrGenerateLangFolder() {
		File dir = FMLClientHandler.instance().getClient().getMinecraftDir();
		File MBLang = new File(dir.getAbsolutePath()+File.separator+"lang"+File.separator+"Mine & Blade");
		if(!MBLang.exists()){
			System.out.println("Mine & Blade: Generating Language Folder");
			MBLang.mkdirs();
		}
		return MBLang;
	}

}
