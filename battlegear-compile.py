import os, os.path, shutil
import zipfile

authors = '["nerd-boy", "GoToLink", "apan95"]'

def main(distDir,  mcpDir):
    from sys import path
        
    defaultWD = os.getcwd()
    
    path.append(mcpDir)
    from runtime.commands import Commands, CLIENT, SIDE_NAME
    from runtime.mcp import reobfuscate_side, recompile_side
    
    print '================ Removing Old versions ================'
    for the_file in os.listdir(distDir):
        file_path = os.path.join(distDir, the_file)
        try:
            if os.path.isfile(file_path):
                os.unlink(file_path)
        except Exception, e:
            print e
    
    print '================ Generating Details ==================='
    
    file_bin = os.path.join(mcpDir, 'src', 'minecraft')
    reob_bin = os.path.join(mcpDir, 'reobf')
    
    with open(os.path.join(file_bin, 'net', 'minecraft', 'client', 'Minecraft.java'), 'rb') as mcClass:
		for line in mcClass:
			startIndex = line.find('"Minecraft Minecraft ')
			endIndex = line.rfind('"')
			if startIndex > -1 and endIndex > -1 and  endIndex > startIndex:
				mcVersion = line[startIndex:endIndex]
				mcVersion = mcVersion.replace('"', '')
				mcVersion = mcVersion.replace('Minecraft', '')
				mcVersion = mcVersion.replace(' ', '')
    
    modid = ''
    version = ''
    modName = ''
    
    with open(os.path.join(file_bin, 'assets', 'battlegear2', 'common', 'BattleGear.java'), 'rb') as mainClass:
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
						
    print '================ Compiling Battlegear ==================='
    os.chdir(mcpDir)
    c = Commands()
    recompile_side(c, CLIENT)
    
    print '================ Creating coremod jar ==================='
    os.chdir(defaultWD)
    
    coremod_dir = os.path.join('assets','battlegear2','coremod')
    
    coremod_bin = os.path.join(mcpDir, 'bin', 'minecraft', coremod_dir)
    
    core_jar = zipfile.ZipFile(os.path.join(distDir,generateJarName('M&B Battlegear 2 - Core', mcVersion, version)), 'w')

    for root, _, filelist in os.walk(coremod_bin, followlinks=True):
		if(root.find(coremod_dir) > -1):
			for cur_file in filelist :
				jar_path = os.path.join(root.replace(coremod_bin, coremod_dir),cur_file)
				print('Packing '+jar_path)
				core_jar.write(os.path.join(root,cur_file), jar_path)
    
    write_core_manifest(core_jar, 'assets.battlegear2.coremod.BattlegearLoadingPlugin')
    
    core_jar.close()
    
    
    print '================ Reobfuscate Battlegear ==================='
    os.chdir(mcpDir)
    
    print(os.getcwd())
    
    cmd = './reobfuscate_srg.sh'
    
    if (os.name == 'nt'): 
		cmd = 'reobfuscate_srg.bat'

    
    os.system(cmd)
    
    print '================ Creating Battlegear jar ==================='
    os.chdir(defaultWD)
    
    bg_jar = zipfile.ZipFile(os.path.join(distDir,generateJarName('M&B Battlegear 2 - Mod', mcVersion, version)), 'w')
    
    print 'Packing McMod Info'
	
    packMcModInfo(bg_jar, modid, version, modname)
    
    print 'Packing Logo'
    bg_jar.write(os.path.join(file_bin,'bg-logo.png'), 'bg-logo.png')
    
    file_bin = os.path.join(mcpDir, 'src', 'minecraft')
    
    
    for root, _, filelist in os.walk(file_bin, followlinks=True):
		if root.find(coremod_dir) == -1:
			if (not root.find('battlegear2') == -1) or (not root.find('extendedGUI') == -1):
				for cur_file in filelist :
					if not (cur_file.endswith('.java') or cur_file.endswith('.orig')):
						class_file = os.path.join(root.replace(file_bin, ''),cur_file)
						print 'Packing File : '+class_file
						bg_jar.write(os.path.join(root,cur_file), class_file)
    
    for root, _, filelist in os.walk(reob_bin, followlinks=True):
            if(root.find(coremod_dir) == -1 and root.find(os.path.join('net','minecraft')) == -1):
			for cur_file in filelist :
				class_file = os.path.join(root.replace(os.path.join(reob_bin,'minecraft'), ''),cur_file)
				print 'Packing Class File : '+class_file
				bg_jar.write(os.path.join(root,cur_file), class_file)
    os.chdir(mcpDir)
    cmd = './reobfuscate.sh'
    
    if (os.name == 'nt'): 
		cmd = 'reobfuscate.bat'

    
    os.system(cmd)    
    os.chdir(defaultWD)
    
    print '================ Creating Base class edit zip ==================='	
    os.chdir(defaultWD)
    baseclass_zip = zipfile.ZipFile(os.path.join(distDir,generateZipName('M&B Battlegear 2 - Base Class', mcVersion, version)), 'w')
    
    for files in os.listdir(os.path.join(reob_bin, 'minecraft')):
        if files.endswith(".class"):
            print files
            baseclass_zip.write(os.path.join(reob_bin, 'minecraft', files), files)
    
    baseclass_zip.write(os.path.join(reob_bin,'minecraft','net', 'minecraft','client', 'Minecraft.class'), os.path.join('net','minecraft','client', 'Minecraft.class'))
    
    print '================ Creating Language Packs ==================='
    os.chdir(defaultWD)
    lang_zip = zipfile.ZipFile(os.path.join(distDir,'M&B Battlegear2 - Language Packs'), 'w')
    
    for files in os.listdir(os.path.join(defaultWD, 'battlegear lang files')):
        if files.endswith(".lang"):
            if not files.startswith("en_US"):
                print 'Moving '+files
                lang_zip.write(os.path.join(defaultWD, 'battlegear lang files', files), os.path.join('MB-Battlegear 2', files))
                
    print '================ Creating Texture Packs ==================='
    tex_folder = os.path.join(defaultWD, 'battlegear gimp files')
    tex_dist = os.path.join(distDir, "Texture Packs")
    if not os.path.exists(tex_dist):
        os.makedirs(tex_dist)
        
    for the_file in os.listdir(tex_dist):
        file_path = os.path.join(tex_dist, the_file)
        try:
            if os.path.isfile(file_path):
                os.unlink(file_path)
        except Exception, e:
            print e
        
    for files in os.listdir(tex_folder):
       if os.path.isdir(os.path.join(tex_folder,files)):
           print "Creating Pack: "+files
           texture_zip = zipfile.ZipFile(os.path.join(tex_dist,'M&B Battlegear2 - Texture -'+files+".zip"), 'w')
           for root, _, filelist in os.walk(os.path.join(tex_folder,files), followlinks=True):
                for cur_file in filelist:
                    dest_path = root.replace(os.path.join(tex_folder, files), '')
                    dest_path = os.sep+'assets'+os.sep+'battlegear2'+os.sep+'textures'+os.sep+dest_path
                    dest_path = os.path.join(dest_path,cur_file)
                    texture_zip.write(os.path.join(root,cur_file), dest_path)


def generateJarName(name, mcVersion, version):
	return '['+mcVersion+'] '+name+' ('+version.replace('"','')+').jar'

def generateZipName(name, mcVersion, version):
     return '['+mcVersion+'] '+name+' ('+version.replace('"','')+').zip'
	
def packMcModInfo(bg_jar, modid, version, modname):
	temp = open('temp', 'w')
	temp.write('[\n{\n')
	temp.write('  "modid": '+modid+',\n')
	temp.write('  "name": '+modname+',\n')
	temp.write('  "description": "Minecraft Combat Evolved",\n')
	temp.write('  "version": '+version+',\n')
	temp.write('  "logoFile": "/bg-logo.png",\n')
	temp.write('  "url": "http://www.minecraftforum.net/topic/1722960-wip-mine-blade-battlegear-2",\n')
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
