package mods.battlegear2.api.core;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

import cpw.mods.fml.common.asm.transformers.deobf.LZMAInputSupplier;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.IFMLCallHook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BattlegearTranslator implements IFMLCallHook {
    //Setting this to true will enable the output of all edited classes as .class files
    public static boolean debug = false;
    public static boolean obfuscatedEnv;

    private String deobFile;
    private String mcLocation;
    
    private static HashMap<String, String> classNameMap = new HashMap<String, String>();
    private static HashMap<String, String> fieldNameMap = new HashMap<String, String>();
    private static HashMap<String, String> methodNameMap = new HashMap<String, String>();
    private static HashMap<String, String> methodDescMap = new HashMap<String, String>();

    public static String getMapedFieldName(String className, String fieldName, String devName) {
        return obfuscatedEnv?fieldNameMap.get(className + "." + fieldName):devName;
    }

    public static String getMapedClassName(String className) {
    	if(obfuscatedEnv)
    		return classNameMap.get(className.substring(className.lastIndexOf(".")+1));
    	else{
    		StringBuilder clas = new StringBuilder("net/minecraft/");
    		clas.append(className.replace(".", "/"));
    		return clas.toString();
    	}
    }

    public static String getMapedMethodName(String className, String methodName, String devName) {
        return obfuscatedEnv?methodNameMap.get(className + "." + methodName):devName;
    }

    public static String getMapedMethodDesc(String className, String methodName, String devDesc) {
        return obfuscatedEnv?methodDescMap.get(className + "." + methodName):devDesc;
    }

    public static void setup(String deobFileName){
        try{
            LZMAInputSupplier zis = new LZMAInputSupplier(FMLInjectionData.class.getResourceAsStream(deobFileName));
            InputSupplier<InputStreamReader> srgSupplier = CharStreams.newReaderSupplier(zis, Charsets.UTF_8);
            List<String> srgList = CharStreams.readLines(srgSupplier);

            for (String line : srgList) {

                line = line.replace(" #C", "").replace(" #S", "");

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
        if(config.createNewFile() || config.exists()){
        	readConfig(config);
        }
        
        return null;
    }

    private void readConfig(File config) {
    	BufferedReader br = null;
    	try{
    		br = new BufferedReader(new FileReader(config));
    		String line = br.readLine();

    		while(line != null){
    			if(line.toLowerCase(Locale.ENGLISH).contains("asm debug mode")){
    				debug = line.toLowerCase(Locale.ENGLISH).contains("true");
                    break;
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