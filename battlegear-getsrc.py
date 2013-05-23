import os, os.path
import shutil, fnmatch
import difflib
import hashlib, sys


def generate_patches(src_dir, src_backup_dir, battlegear_dir):

	print '==================== Moving Battlegear source ===================='
	for root, _, filelist in os.walk(src_dir, followlinks=True):
		for cur_file in filelist :

			new_path = os.path.join(root, cur_file)
			original_path = new_path.replace(src_dir, src_backup_dir)

			if os.path.exists(original_path) and os.path.exists(new_path) and new_path.endswith('.java'):
				
				#generate and compare the md5s, ignoring the filepath
				if not ('', hashfile(open(original_path, 'r'), hashlib.md5())) == ('', hashfile(open(new_path, 'r'), hashlib.md5())):
					
					print "Generating .patch for "+cur_file
					
					patch_path = original_path.replace(src_backup_dir, battlegear_dir)+".patch"
					patch_parent = os.path.abspath(os.path.join(patch_path, os.pardir))
					
					if not os.path.exists(patch_parent):
						os.makedirs(patch_parent)
						
					patch_File = open(patch_path, 'w')
					
					diff = difflib.unified_diff(open(original_path, 'r').readlines(), open(new_path, 'r').readlines())
					
					patch_File.writelines(diff)
				
			elif not os.path.exists(original_path) and os.path.exists(new_path):
				print "Copying "+cur_file
				copied_path = original_path.replace(src_backup_dir, battlegear_dir)
				copied_parent = os.path.abspath(os.path.join(copied_path, os.pardir))
				
				if not os.path.exists(copied_parent):
						os.makedirs(copied_parent)
				shutil.copy(new_path, copied_parent)
	print '==================== Moving Battlegear lang files ===================='
	langPath = os.path.join(os.path.abspath(os.path.join(battlegear_dir, os.pardir)),"battlegear lang files")
	if not os.path.exists(langPath):
		os.makedirs(langPath)
	enUsPath = os.path.join(src_dir, "minecraft", "mods", "battlegear2", "client", "en_US.lang")
	shutil.copy(enUsPath, langPath)
 
	modlangpath = os.path.join(os.path.abspath(os.path.join(src_dir, os.pardir)),"jars", "lang", "MB-Battlegear 2")
	for cur_file in os.listdir(modlangpath):
           if cur_file.endswith(".lang"):
               print 'Moving '+cur_file
               shutil.copy(os.path.join(modlangpath, cur_file), langPath)
	


def hashfile(afile, hasher, blocksize=65536):
    buf = afile.read(blocksize)
    while len(buf) > 0:
        hasher.update(buf)
        buf = afile.read(blocksize)
    return hasher.digest()
    	

if  __name__ =='__main__':
	src_dir = os.path.abspath(os.path.join('mcp', 'src'))
	src_backup_dir = os.path.abspath(os.path.join('mcp', 'src_backup'))
	battlegear_dir = os.path.abspath('battlegear mod src')
	
	if not os.path.exists('forge.py'):
		print 'Invalid Instalation, battlegear-getsrc must be in the forge directory'
	elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
		print 'Invalid Instalation, Minecraft Forge must not be inside the mcp directory'
	elif not os.path.isdir(src_backup_dir):
		print 'battlegear-install must be run before battlegear-getsrc'
	else:
		if(os.path.isdir(battlegear_dir)):
			shutil.rmtree(battlegear_dir)
			
		generate_patches(src_dir, src_backup_dir, battlegear_dir)

