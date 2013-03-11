package battlegear2.common.utils;

public class ReflectionMethod {
	private String name;
	private String obName;
	private Class[] parameterTypes;
	
	
	public ReflectionMethod(String name, String obName, Class[] parameterTypes) {
		this.name = name;
		this.obName = obName;
		this.parameterTypes = parameterTypes;
	}


	public String getNormalName() {
		return name;
	}
	
	public String getObName() {
		return obName;
	}
	
	public String getName(boolean obfuscation){
		if(obfuscation)
			return obName;
		else
			return name;
	}

	public Class[] getParameterTypes() {
		return parameterTypes;
	}
	
	
}
