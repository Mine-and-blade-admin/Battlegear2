package assets.battlegear2.common.blocks;

import assets.battlegear2.api.IHeraldyItem;
import assets.battlegear2.api.IHeraldyItem.HeraldyRenderPassess;
import assets.battlegear2.common.heraldry.SigilHelper;
import assets.battlegear2.common.utils.BattlegearConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockItemBanner extends ItemBlock implements IHeraldyItem{
	
	private Icon[] baseIcons = new Icon[4];
	private Icon[] postIcons = new Icon[4];

	public BlockItemBanner(int par1) {
		super(par1);
		
		this.setMaxStackSize(1);
		this.setCreativeTab(BattlegearConfig.customTab);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("battlegear2:banner");
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		super.registerIcons(par1IconRegister);
		
		for(int i = 0; i < 4; i++){
			baseIcons[i] = par1IconRegister.registerIcon(String.format("battlegear2:banner/banner-base-%s",i));
			postIcons[i] = par1IconRegister.registerIcon(String.format("battlegear2:banner/banner-post-%s",i));
		}
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, 
			int side, float hitX, float hitY, float hitZ, int i) {

		if(side == 1){ // on top of a block
			if(world.isAirBlock(x, y+1, z))
			{

				boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ,  i);

				world.setBlock(x, y+1, z, BattlegearConfig.banner.blockID);
				

				float angle = player.rotationYaw + (45F/2)-90;
				while(angle < 0)
					angle =angle+360;
				while (angle>360)
					angle = angle-360;

				byte state = (byte) (angle / 45);
				byte[] code = getHeraldryCode(stack);
				
				
				world.setBlockTileEntity(x, y, z, new TileEntityBanner(state, code));
				world.setBlockTileEntity(x, y+1, z, new TileEntityBanner((byte) (state+8), code));

				return placed;
			}else
				return false;


		}else if (side != 0){ //If on a side
			
			if(world.isAirBlock(x, y-1, z)){
				boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ,  i);
				world.setBlock(x, y-1, z, BattlegearConfig.banner.blockID);
	
				byte state = 0;
				
				switch(side){
				case 2:state = 16;break;
				case 3:state = 18;break;
				case 4: state = 17; break;
				case 5: state = 19; break;
				}
				
				byte[] code = getHeraldryCode(stack);
				
				System.out.println(side);
				
				world.setBlockTileEntity(x, y, z, new TileEntityBanner((byte)(state+4), code));
				world.setBlockTileEntity(x, y-1, z, new TileEntityBanner(state, code));
			}
		}
		return false;

	}

	@Override
	public Icon getBaseIcon(ItemStack stack) {
		return baseIcons[SigilHelper.getBanner(getHeraldryCode(stack))];
	}

	@Override
	public Icon getTrimIcon(ItemStack stack) {
		return null;
	}

	@Override
	public Icon getPostRenderIcon(ItemStack stack) {
		return postIcons[SigilHelper.getBanner(getHeraldryCode(stack))];
	}

	@Override
	public boolean hasHeraldry(ItemStack stack) {
		return true;
	}
	
	@Override
	public byte[] getHeraldryCode(ItemStack stack) {
		if(!stack.hasTagCompound()){
			return SigilHelper.getDefault();
		}
		NBTTagCompound compound = stack.getTagCompound();
		if(compound.hasKey("hc2")){
			return compound.getByteArray("hc2");
		}else{
			return SigilHelper.getDefault();
		}
	}

	@Override
	public void removeHeraldry(ItemStack item) {
		if(item.hasTagCompound()){
			item.getTagCompound().removeTag("hc2");
		}
	}

	@Override
	public void setHeraldryCode(ItemStack stack, byte[] code) {
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		
		stack.getTagCompound().setByteArray("hc2", code);
	}

	@Override
	public boolean shouldDoPass(HeraldyRenderPassess pass) {
		return ! pass.equals(HeraldyRenderPassess.SecondaryColourTrim);
	}

	@Override
	public boolean useDefaultRenderer() {
		return true;
	}
}
