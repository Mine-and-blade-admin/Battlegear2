package mods.battlegear2.coremod;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import cpw.mods.fml.common.asm.transformers.deobf.LZMAInputSupplier;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.IFMLCallHook;
import net.minecraft.src.BaseMod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BattlegearTranslator implements IFMLCallHook {

    private String deobFile;
    private String mcLocation;
    
    private static HashMap<String, String> classNameMap = new HashMap<String, String>();
    private static HashMap<String, String> fieldNameMap = new HashMap<String, String>();
    private static HashMap<String, String> methodNameMap = new HashMap<String, String>();
    private static HashMap<String, String> methodDescMap = new HashMap<String, String>();

    public static String getMapedFieldName(String className, String fieldName) {
        return fieldNameMap.get(className + "." + fieldName);
    }

    public static String getMapedClassName(String className) {
        return classNameMap.get(className);
    }

    public static String getMapedMethodName(String className, String methodName) {
        return methodNameMap.get(className + "." + methodName);
    }

    public static String getMapedMethodDesc(String className, String methodName) {
        return methodDescMap.get(className + "." + methodName);
    }

    public static void setup(String deobFileName){
        try{
            LZMAInputSupplier zis = new LZMAInputSupplier(FMLInjectionData.class.getResourceAsStream(deobFileName));
            InputSupplier<InputStreamReader> srgSupplier = CharStreams.newReaderSupplier(zis, Charsets.UTF_8);
            List<String> srgList = CharStreams.readLines(srgSupplier);

            for (String line : srgList) {

                line.replaceAll(" #C", "");
                line.replaceAll(" #S", "");

                if (line.startsWith("CL")) {
                    parseClass(line);
                } else if (line.startsWith("FD")) {
                    parseField(line);
                } else if (line.startsWith("MD")) {
                    parseMethod(line);
                }

            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Void call() throws Exception {
        setup(deobFile);
        //parse the config file
        File config = new File(mcLocation+File.separator+"config"+File.separator+"battlegear2.cfg");
        config.getParentFile().mkdirs();
        config.createNewFile();
        if(config.exists()){
        	readConfig(config);
        }else{
        	
        }
        
        
        return null;
    }

    private void readConfig(File config) {
    	BufferedReader br = null;
    	try{
    		br = new BufferedReader(new FileReader(config));
    		String line = br.readLine();

    		while(line != null){
    			if(line.toLowerCase().contains("asm debug mode")){
    				BattlegearLoadingPlugin.debug = line.toLowerCase().contains("true");
    			}
    			line = br.readLine();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	} finally{
    		if(br != null){
    			try{
    				br.close();
    			}catch (Exception e2){
    				e2.printStackTrace();
    			}
    		}
    	}
	}

	private static void parseMethod(String line) {
        String[] splitLine = line.split(" ");

        String[] splitObName = splitLine[1].split("/");
        String[] splitTranslatedName = splitLine[3].split("/");

        String key = splitTranslatedName[splitTranslatedName.length - 2] + "." + splitTranslatedName[splitTranslatedName.length - 1];

        methodNameMap.put(key, splitObName[splitObName.length - 1]);

        methodDescMap.put(key, splitLine[2]);
    }

    private static void parseField(String line) {
        String[] splitLine = line.split(" ");

        String[] splitObName = splitLine[1].split("/");
        String[] splitTranslatedName = splitLine[2].split("/");

        String key = splitTranslatedName[splitTranslatedName.length - 2] + "." + splitTranslatedName[splitTranslatedName.length - 1];


        fieldNameMap.put(key, splitObName[splitObName.length - 1]);
    }

    private static void parseClass(String line) {
        String[] splitLine = line.split(" ");

        String[] splitClassPath = splitLine[2].split("/");

        classNameMap.put(splitClassPath[splitClassPath.length - 1], splitLine[1]);
    }



    @Override
    public void injectData(Map<String, Object> data) {
        deobFile = data.get("deobfuscationFileName").toString();
        mcLocation = data.get("mcLocation").toString();
    }

}