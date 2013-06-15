package mods.battlegear2.common.blocks;

import java.awt.Color;
import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.client.EntityFXBreakBanner;
import mods.battlegear2.common.heraldry.SigilHelper;
import mods.battlegear2.common.utils.BattlegearConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBanner extends BlockContainer{

	public BlockBanner(int id) {
		super(id, Material.cloth);
	    this.setCreativeTab(BattlegearConfig.customTab);
	    this.setUnlocalizedName("battlegear2:banner");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityBanner();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	 public int getRenderType(){
	        return -1;
	 }

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	/**
	  * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
	  */
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		if (y >= 255){
			 return false;
		 }

		 return (world.getBlockId(x, y-1, z) != this.blockID && world.getBlockId(x, y+1, z) != this.blockID) 
				 && (world.getBlockId(x, y+1, z) == 0 || world.getBlockId(x, y-1, z) == 0);
	}
	
	/**
	  * Prevents creatures from spawning on top of banners
	  */
	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, World world, int x, int y, int z) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		// TODO Auto-generated method stub
		//super.registerIcons(par1IconRegister);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addBlockDestroyEffects(World world, int x, int y, int z,
			int meta, EffectRenderer effectRenderer) {
		
		byte b0 = 4;

		byte[] code = getHeraldryCode(world, x, y, z);
		float[] colour1 =  SigilHelper.getPrimaryColourArray(code);
		float[] colour2 =  SigilHelper.getSecondaryColourArray(code);
		
        for (int j1 = 0; j1 < b0; ++j1)
        {
            for (int k1 = 0; k1 < b0; ++k1)
            {
                for (int l1 = 0; l1 < b0; ++l1)
                {
                    double d0 = (double)x + ((double)j1 + 0.5D) / (double)b0;
                    double d1 = (double)y + ((double)k1 + 0.5D) / (double)b0;
                    double d2 = (double)z + ((double)l1 + 0.5D) / (double)b0;
                    int i2 = world.rand.nextInt(6);
                    
                    float[] colour = world.rand.nextFloat()>0.33F?colour1:colour2;
                   
                    EntityFX fx = new EntityFXBreakBanner(
                    		world, d0, d1, d2, d0 - (double)x - 0.5D, d1 - (double)y - 0.5D, d2 - (double)z - 0.5D,
                    		blockParticleGravity, colour);
                   
                    
                    
                    effectRenderer.addEffect(fx);
                    
                   
                    //this.addEffect(()).func_70596_a(par1, par2, par3));
                }
            }
        }
		
		
		return true;
	}
	
	
	public byte[] getHeraldryCode(IBlockAccess par1iBlockAccess, int i,
			int j, int k) {
		
		TileEntity e = par1iBlockAccess.getBlockTileEntity(i, j, k);
		
		if(e != null && e instanceof TileEntityBanner){
			return ((TileEntityBanner)e).getHeraldry();
		}
		
		return SigilHelper.getDefault();
	}


	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y,
			int z, int metadata, int fortune) {
		
		
		TileEntity te = world.getBlockTileEntity(x, y, z);
		ArrayList<ItemStack> stack = new ArrayList<ItemStack>();
		
		if(te instanceof TileEntityBanner){
			byte[] code = ((TileEntityBanner)te).getHeraldry();
			
			
			ItemStack bannerStack = new ItemStack(Item.itemsList[idDropped(metadata, world.rand, fortune)]);
			if(bannerStack.getItem() instanceof IHeraldyItem){
				((IHeraldyItem)bannerStack.getItem()).setHeraldryCode(bannerStack, code);
				
				stack.add(bannerStack);
			}
			
			
		}
		
		return stack;
	}

	/**
	  * Will remove the other section of the banner when it is destroyed
	  */
	 @Override
	 public void breakBlock(World world, int x, int y, int z, int p, int a){

		 TileEntity tile = world.getBlockTileEntity(x, y, z);

		 if(tile != null && tile instanceof TileEntityBanner){
			TileEntityBanner banner = (TileEntityBanner)tile;

			//if it is a base, remove the top
			if(banner.isBase()){
				world.setBlockToAir(x, y + 1, z);
				world.setBlockTileEntity(x, y + 1, z, null);
			}else{ // if not remove the base
				world.setBlockToAir(x, y - 1, z);
				world.setBlockTileEntity(x, y - 1, z, null);
			}

		 }

		//remove self
		world.setBlockTileEntity(x, y, z, null);
		super.breakBlock(world, x, y, z, p, a);
	 }

	@Override
	public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
		return false;
	}
	
	
	
	 
	 
	
		
}
