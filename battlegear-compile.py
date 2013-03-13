import os, os.path, shutil
import zipfile

def main(distDir,  mcpDir):
    from sys import path
        
    defaultWD = os.getcwd()
    
    path.append(mcpDir)
    from runtime.commands import Commands, CLIENT, SIDE_NAME
    from runtime.mcp import reobfuscate_side, recompile_side
    
    print '================ Compiling Battlegear ==================='
    os.chdir(mcpDir)
    c = Commands()
    recompile_side(c, CLIENT)
    
    print '================ Creating coremod jar ==================='
    os.chdir(defaultWD)
    
    coremod_dir = os.path.join('battlegear2','coremod')
    
    coremod_bin = os.path.join(mcpDir, 'bin', 'minecraft', coremod_dir)
    
    core_jar = zipfile.ZipFile(os.path.join(distDir,'Battlegear-core.jar'), 'w')

    for root, _, filelist in os.walk(coremod_bin, followlinks=True):
		if(root.find(coremod_dir) > -1):
			for cur_file in filelist :
				jar_path = os.path.join(root.replace(coremod_bin, coremod_dir),cur_file)
				print('Packing '+jar_path)
				core_jar.write(os.path.join(root,cur_file), jar_path)
    
    write_core_manifest(core_jar, 'battlegear2.coremod.BattlegearLoadingPlugin')
    
    core_jar.close()
    
    
    print '================ Reobfuscate Battlegear ==================='
    os.chdir(mcpDir)
    reobfuscate_side(c, CLIENT)
    
    
    print '================ Creating Battlegear jar ==================='
    os.chdir(defaultWD)
    
    file_bin = os.path.join(mcpDir, 'src', 'minecraft')
    reob_bin = os.path.join(mcpDir, 'reobf')
    
    #I will change this to a jar later when it is base-clean
    bg_jar = zipfile.ZipFile(os.path.join(distDir,'Battlegear-mod.zip'), 'w')
    
    
    print 'Packing McMod Info'
    bg_jar.write(os.path.join(file_bin,'mcmod.info'), 'mcmod.info')
    
    print 'Packing Logo'
    bg_jar.write(os.path.join(file_bin,'bg-logo.png'), 'bg-logo.png')
    
    file_bin = os.path.join(mcpDir, 'src', 'minecraft', 'battlegear2')
    
    
    for root, _, filelist in os.walk(file_bin, followlinks=True):
		if root.find(coremod_dir) == -1:
			for cur_file in filelist :
				if not (cur_file.endswith('.java') or cur_file.endswith('.orig')):
					class_file = os.path.join(root.replace(file_bin, 'battlegear2'),cur_file)
					print 'Packing File : '+class_file
					bg_jar.write(os.path.join(root,cur_file), class_file)
    
    
    
    for root, _, filelist in os.walk(reob_bin, followlinks=True):
		if(root.find(coremod_dir) == -1):
			for cur_file in filelist :
				class_file = os.path.join(root.replace(os.path.join(reob_bin,'minecraft'), ''),cur_file)
				print 'Packing Class File : '+class_file
				bg_jar.write(os.path.join(root,cur_file), class_file)
	
	

def write_core_manifest(jar_file, loading_plugin):
	temp = open('temp', 'w')
	temp.write('Manifest-Version: 1.0\n')
	temp.write('FMLCorePlugin: '+loading_plugin+'\n')
	temp.close()
	
	jar_file.write('temp', os.path.join('META-INF','MANIFEST.MF'))
	
	os.remove('temp')
    
if  __name__ =='__main__':
    distDir = 'battlegear dist'
    mcpDir = 'mcp'
    
    if not os.path.isdir(mcpDir):
        print 'Invalid Instalation, battlegear-getDist must be in the forge directory'
    else:
        if not os.path.isdir(distDir):
            os.makedirs(distDir)
        
        main(os.path.abspath(distDir),  os.path.abspath(mcpDir))
