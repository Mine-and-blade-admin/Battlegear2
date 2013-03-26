import os, os.path, shutil
import zipfile

authors = '["nerd-boy", "GoToLink"]'

def main(distDir,  mcpDir):
    from sys import path
        
    defaultWD = os.getcwd()
    
    path.append(mcpDir)
    from runtime.commands import Commands, CLIENT, SIDE_NAME
    from runtime.mcp import reobfuscate_side, recompile_side
    
    print '================ Compiling Battlegear ==================='
    os.chdir(mcpDir)
    c = Commands()
    #recompile_side(c, CLIENT)
    
    print '================ Creating coremod jar ==================='
    os.chdir(defaultWD)
    
    coremod_dir = os.path.join('mods','battlegear2','coremod')
    
    coremod_bin = os.path.join(mcpDir, 'bin', 'minecraft', coremod_dir)
    
    core_jar = zipfile.ZipFile(os.path.join(distDir,'Battlegear-core.jar'), 'w')

    for root, _, filelist in os.walk(coremod_bin, followlinks=True):
		if(root.find(coremod_dir) > -1):
			for cur_file in filelist :
				jar_path = os.path.join(root.replace(coremod_bin, coremod_dir),cur_file)
				print('Packing '+jar_path)
				core_jar.write(os.path.join(root,cur_file), jar_path)
    
    write_core_manifest(core_jar, 'mods.battlegear2.coremod.BattlegearLoadingPlugin')
    
    core_jar.close()
    
    
    print '================ Reobfuscate Battlegear ==================='
    os.chdir(mcpDir)
    
    print(os.getcwd())
    
    cmd = './reobfuscate_srg.sh'
    
    if (os.name == 'nt'): 
		cmd = 'reobfuscate_srg.bat'

    
    #os.system(cmd)
    
    print '================ Creating Battlegear jar ==================='
    os.chdir(defaultWD)
    
    file_bin = os.path.join(mcpDir, 'src', 'minecraft')
    reob_bin = os.path.join(mcpDir, 'reobf')
    
    bg_jar = zipfile.ZipFile(os.path.join(distDir,'Battlegear-mod.jar'), 'w')
    
    
    print 'Packing McMod Info'
    modid = ''
    version = ''
    modName = ''
    
    with open(os.path.join(file_bin, 'mods', 'battlegear2', 'common', 'BattleGear.java'), 'rb') as mainClass:
		for line in mainClass:
			if(line.startswith('@Mod(')):
				line = line.replace('@Mod(','')
				line = line.replace(')','')
				line = line.strip()
				fields = line.split(', ')
				for field in fields:
					pair = field.split('=')
					if(pair[0] == 'modid'):
						modid = pair[1]
					elif (pair[0] == 'name'):
						modname = pair[1]
					elif (pair[0] == 'version'):
						version = pair[1]
	
    packMcModInfo(bg_jar, modid, version, modname)
    
    print 'Packing Logo'
    bg_jar.write(os.path.join(file_bin,'bg-logo.png'), 'bg-logo.png')
    
    file_bin = os.path.join(mcpDir, 'src', 'minecraft', 'mods')
    
    
    for root, _, filelist in os.walk(file_bin, followlinks=True):
		if root.find(coremod_dir) == -1:
			if not root.find('battlegear2') == -1:
				for cur_file in filelist :
					if not (cur_file.endswith('.java') or cur_file.endswith('.orig')):
						class_file = os.path.join(root.replace(file_bin, 'mods'),cur_file)
						print 'Packing File : '+class_file
						bg_jar.write(os.path.join(root,cur_file), class_file)
    
    
    
    for root, _, filelist in os.walk(reob_bin, followlinks=True):
		if(root.find(coremod_dir) == -1):
			for cur_file in filelist :
				class_file = os.path.join(root.replace(os.path.join(reob_bin,'minecraft'), ''),cur_file)
				print 'Packing Class File : '+class_file
				bg_jar.write(os.path.join(root,cur_file), class_file)
				
	
	
def packMcModInfo(bg_jar, modid, version, modname):
	temp = open('temp', 'w')
	temp.write('[\n{\n')
	temp.write('  "modid": '+modid+',\n')
	temp.write('  "name": '+modname+',\n')
	temp.write('  "description": "Minecraft Combat Evolved",\n')
	temp.write('  "version": '+version+',\n')
	temp.write('  "logoFile": "/bg-logo.png",\n')
	temp.write('  "url": "http://www.minecraftforum.net/topic/1722960-wip-mine-blade-battlegear-2-modders-discussions/",\n')
	temp.write('  "authors": '+authors+'\n')
	temp.write('}\n]')
	
	temp.close()
	
	bg_jar.write('temp', os.path.join('mcmod.info'))
	
	os.remove('temp')

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
