package assets.battlegear2.coremod;

import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

import cpw.mods.fml.common.asm.transformers.deobf.LZMAInputSupplier;
import cpw.mods.fml.relauncher.IFMLCallHook;

public class BattleGearTranslator implements IFMLCallHook {
	
	private File deobFile;

	private static HashMap<String, String> classNameMap = new HashMap<String, String>();
	private static HashMap<String, String> fieldNameMap = new HashMap<String, String>();
	private static HashMap<String, String> methodNameMap = new HashMap<String, String>();
	private static HashMap<String, String> methodDescMap = new HashMap<String, String>();
	
	public static String getMapedFieldName(String className, String fieldName){
		return fieldNameMap.get(className+"."+fieldName);
	}
	
	public static String getMapedClassName(String className){
		return classNameMap.get(className);
	}
	
	public static String getMapedMethodName(String className, String methodName){
		return methodNameMap.get(className+"."+methodName);
	}
	
	public static String getMapedMethodDesc(String className, String methodName){
		return methodDescMap.get(className+"."+methodName);
	}

	@Override
	public Void call() throws Exception {

		ZipFile mapZip = new ZipFile(deobFile);
        ZipEntry classData = mapZip.getEntry("joined.srg");
        LZMAInputSupplier zis = new LZMAInputSupplier(mapZip.getInputStream(classData));
        InputSupplier<InputStreamReader> srgSupplier = CharStreams.newReaderSupplier(zis,Charsets.UTF_8);
        List<String> srgList = CharStreams.readLines(srgSupplier);
        
        for (String line : srgList) {
			
        	if(line.startsWith("CL")){
        		parseClass(line);
        	}else if(line.startsWith("FD")){
        		parseField(line);
        	}else if(line.startsWith("MD")){
        		parseMethod(line);
        	}
        	
		}
        mapZip.close();
        
		return null;
	}

	private void parseMethod(String line) {
		String[] splitLine = line.split(" ");
		
		String[] splitObName = splitLine[1].split("/");
		String[] splitTranslatedName = splitLine[3].split("/");
		
		String key = splitTranslatedName[splitTranslatedName.length-2]+"."+splitTranslatedName[splitTranslatedName.length-1];
		
		methodNameMap.put(key, splitObName[splitObName.length-1]);
		
		methodDescMap.put(key, splitLine[2]);
	}

	private void parseField(String line) {
		String[] splitLine = line.split(" ");
		
		String[] splitObName = splitLine[1].split("/");
		String[] splitTranslatedName = splitLine[2].split("/");
		
		String key = splitTranslatedName[splitTranslatedName.length-2]+"."+splitTranslatedName[splitTranslatedName.length-1];
		
		
		fieldNameMap.put(key, splitObName[splitObName.length-1]);
	}

	private void parseClass(String line) {
		String[] splitLine = line.split(" ");
		
		String[] splitClassPath = splitLine[2].split("/");
		
		classNameMap.put(splitClassPath[splitClassPath.length-1], splitLine[1]);		
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
		File lib = new File((File)data.get("mcLocation"), "lib");
		deobFile = new File(lib, (String)data.get("deobfuscationFileName"));
		
	}

}
