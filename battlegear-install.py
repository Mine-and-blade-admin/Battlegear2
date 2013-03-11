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
	cmd = 'patch -iR'
    
	if os.name == 'nt':
		applydiff = os.path.abspath(os.path.join(mcp_dir, 'runtime', 'bin', 'applydiff.exe'))
		cmd = '"'+applydiff+'" -uf -iR'
					
	for root, _, filelist in os.walk(battlegearCode_dir, followlinks=True):
		for cur_file in filelist:
				
			bg_file = os.path.join(root, cur_file)
			target_file = os.path.join(root, cur_file).replace(battlegearCode_dir, src_dir)
			
			if cur_file.endswith('.java.patch'):
				
				target_file = target_file[:len(target_file)-6]
				print 'Patching ' + cur_file[:len(cur_file)-6]
				
				
				thiscmd = cmd +' "'+target_file+'"'
				thiscmd = thiscmd +' "'+bg_file+'"'
				
				args = shlex.split(thiscmd)
				process = subprocess.Popen(args, bufsize=-1)
				process.communicate()
			else:
				#not a patch file 
				print 'Copying '+cur_file
				target_parent = os.path.abspath(os.path.join(target_file, os.pardir))
				
				if not os.path.exists(target_parent):
					os.makedirs(target_parent)
				
				shutil.copy(bg_file, target_parent)
				
				
	
	if os.path.isfile(temp):
		os.remove(temp)

	
	print '================ Copy Battlegear Files Done==================='
	
	print '================ Battlegear src Instalation Done==================='
	
	
def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)

if  __name__ =='__main__':
	if not os.path.exists('forge.py'):
		print 'Invalid Instalation, battlegear-installer must be in the forge directory'
	elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
		print 'Invalid Instalation, Minecraft Forge must not be inside the mcp directory'
	elif not os.path.isdir('battlegear mod src'):
		print "Invalid Instalation, can't find battlegear pathchs"
	else:
		main(os.path.abspath('mcp'), os.path.abspath('battlegear mod src'),os.path.abspath(os.path.join('mcp', 'runtime')))
