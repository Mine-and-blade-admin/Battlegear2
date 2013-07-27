import os, os.path, shutil
import shutil, fnmatch
import difflib
import hashlib, sys
import shlex
import subprocess
import install

def main(mcp_dir, battlegearCode_dir, runtime_dir):
	
	print '================ Battlegear src Instalation ==================='
	print 'Installing Minceraft Forge'
	install.main(mcp_dir)
	print '================ Clone src ==================='
	src_dir = os.path.join(mcp_dir, 'src')
	src_backup_dir = os.path.join(mcp_dir, 'src_backup')
	
	if os.path.exists(src_backup_dir):
		shutil.rmtree(src_backup_dir)
		
	if os.path.exists(src_dir):
		shutil.copytree(src_dir, src_backup_dir)
	
	print '================ Clone src Done ==================='
	
	print '================ Copy Battlegear Files ==================='
	temp = os.path.abspath('temp.patch')
	cmd = 'patch -u -i'
	cmd = 'patch -p2 -i "%s" ' % temp
    
	if os.name == 'nt':
		applydiff = os.path.abspath(os.path.join(mcp_dir, 'runtime', 'bin', 'applydiff.exe'))
		cmd = '"'+applydiff+'" -u -i'
		cmd = '"%s" -uf -p2 -i "%s"' % (applydiff, temp)
					
	for root, _, filelist in os.walk(battlegearCode_dir, followlinks=True):
		for cur_file in filelist:
				
			bg_file = os.path.join(root, cur_file)
			target_file = os.path.join(root, cur_file).replace(battlegearCode_dir, src_dir)
			
			if cur_file.endswith('.java.patch'):
				
				target_file = target_file[:len(target_file)-6]
				
				target_parent = os.path.abspath(os.path.join(target_file, os.pardir))
				
				next_cmd = cmd + ' "'+target_file+'"'
				
				print 'Patching ' + cur_file[:len(cur_file)-6]
				
				fix_patch(bg_file, temp)
				
				process = subprocess.Popen(cmdsplit(next_cmd), bufsize=-1)
				process.communicate()
			else:
				#not a patch file 
				print 'Copying '+cur_file
				target_parent = os.path.abspath(os.path.join(target_file, os.pardir))
				
				if not os.path.exists(target_parent):
					os.makedirs(target_parent)
				
				shutil.copy(bg_file, target_parent)

	#if os.path.isfile(temp):
	#	os.remove(temp)

	print '================ Copy Battlegear Lang Files ==================='
	langPath = os.path.join(os.path.abspath(os.path.join(battlegearCode_dir, os.pardir)),"battlegear lang files")
	if not os.path.exists(langPath):
		os.makedirs(langPath)
	#enUsPath = os.path.join(langPath, "en_US.lang")
	#shutil.copy(enUsPath, os.path.join(mcp_dir, "src", "minecraft", "assets", "battlegear2", "client"))
	for files in os.listdir(langPath):
         if files.endswith(".lang"):
             if not files.startswith("en_US.lang"):
                 shutil.copy(os.path.join(langPath, files), os.path.join(mcp_dir, "jars", "lang", "MB-Battlegear 2", files))
	
	print '================ Copy Battlegear Files Done==================='
	
	print '================ Battlegear src Instalation Done==================='

#Copied from fml.py
def fix_patch(in_file, out_file, find=None, rep=None):
    in_file = os.path.normpath(in_file)
    if out_file is None:
        tmp_filename = in_file + '.tmp'
    else:
        out_file = os.path.normpath(out_file)
        tmp_file = out_file
        dir_name = os.path.dirname(out_file)
        if dir_name:
            if not os.path.exists(dir_name):
                os.makedirs(dir_name)
    file = 'not found'
    with open(in_file, 'rb') as inpatch:
        with open(tmp_file, 'wb') as outpatch:
            for line in inpatch:
                line = line.rstrip('\r\n')
                if line[:3] in ['+++', '---', 'Onl', 'dif']:
                    if not find == None and not rep == None:
                        line = line.replace('\\', '/').replace(find, rep).replace('/', os.sep)
                    else:
                        line = line.replace('\\', '/').replace('/', os.sep)
                    outpatch.write(line + os.linesep)
                else:
                    outpatch.write(line + os.linesep)
                if line[:3] == '---':
                    file = line[line.find(os.sep, line.find(os.sep)+1)+1:]
                    
    if out_file is None:
        shutil.move(tmp_file, in_file)
    return file	
	
#Copied from fml.py
def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)

if  __name__ =='__main__':
	if not os.path.exists('forge.py'):
		print 'Invalid Installation, battlegear-installer must be in the forge directory'
	elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
		print 'Invalid Installation, Minecraft Forge must not be inside the mcp directory'
	elif not os.path.isdir('battlegear mod src'):
		print "Invalid Installation, can't find battlegear patchs"
	else:
		main(os.path.abspath('mcp'), os.path.abspath('battlegear mod src'),os.path.abspath(os.path.join('mcp', 'runtime')))
