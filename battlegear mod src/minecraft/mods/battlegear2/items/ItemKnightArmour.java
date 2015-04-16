package mods.battlegear2.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.api.heraldry.IHeraldyArmour;
import mods.battlegear2.api.heraldry.PatternStore;
import mods.battlegear2.client.heraldry.HeraldryArmourModel;
import mods.battlegear2.heraldry.SigilHelper;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

import java.util.List;

public class ItemKnightArmour extends ItemArmor implements IHeraldyArmour, ISpecialArmor{

	private IIcon baseIcon[];
	private IIcon postRenderIcon[];
	private IIcon trimRenderIcon;
	private Object modelObject;
    private final float motionFactor;

	public ItemKnightArmour(int armourType) {
		super(Battlegear.knightArmourMaterial, 1, armourType);
		setCreativeTab(BattlegearConfig.customTab);
		setUnlocalizedName("battlegear2:knights_armour."+ BattlegearConfig.armourTypes[armourType]);
        if(armourType==1){//Chest
            motionFactor = -0.20F;
        }else if(armourType==2){//Legs
            motionFactor = -0.15F;
        }else{
            motionFactor = -0.05F;
        }
        GameRegistry.registerItem(this, "knights_armour."+ BattlegearConfig.armourTypes[armourType]);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		//super.registerIcons(par1IconRegister);
		
		if(armorType == 0){//Helm
			baseIcon = new IIcon[4];
			postRenderIcon = new IIcon[4];
			for(int i = 0; i < 4; i ++){
				baseIcon[i] = par1IconRegister.registerIcon("battlegear2:armours/knight-base-"+BattlegearConfig.armourTypes[armorType]+"-"+i);
				postRenderIcon[i] =  par1IconRegister.registerIcon("battlegear2:armours/knight-post-"+BattlegearConfig.armourTypes[armorType]+"-"+i);
			}
		}else{
			baseIcon = new IIcon[1];
			postRenderIcon = new IIcon[1];
			baseIcon[0] = par1IconRegister.registerIcon("battlegear2:armours/knight-base-"+BattlegearConfig.armourTypes[armorType]);
			postRenderIcon[0] = par1IconRegister.registerIcon("battlegear2:armours/knight-post-"+BattlegearConfig.armourTypes[armorType]);
		}
	
		if(armorType == 2){
			trimRenderIcon = par1IconRegister.registerIcon("battlegear2:armours/knight-trim-"+BattlegearConfig.armourTypes[armorType]);
		}
	}

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack){
        return getPostRenderIcon(stack);
    }

	@Override
    @SideOnly(Side.CLIENT)
	public IIcon getBaseIcon(ItemStack stack) {
		if(armorType == 0){
			return baseIcon[SigilHelper.getHelm(((IHeraldryItem) stack.getItem()).getHeraldry(stack))];
		}else
			return baseIcon[0];
	}

	@Override
    @SideOnly(Side.CLIENT)
	public IIcon getPostRenderIcon(ItemStack stack) {
		if(armorType == 0){
			return postRenderIcon[SigilHelper.getHelm(((IHeraldryItem)stack.getItem()).getHeraldry(stack))];
		}else
			return postRenderIcon[0];
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public IIcon getTrimIcon(ItemStack stack) {
		return trimRenderIcon;
	}

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List){
        ItemStack armor = new ItemStack(par1);
        setHeraldry(armor,SigilHelper.getDefault());
        par3List.add(armor);
    }

	@Override
	public boolean hasHeraldry(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(heraldryTag);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack armor, EntityPlayer player, List par3List, boolean par4) {
		super.addInformation(armor, player, par3List, par4);
		par3List.add(String.format("%s +%d %s", 
				EnumChatFormatting.BLUE, this.damageReduceAmount, StatCollector.translateToLocal("tooltip.armour.points")));
        par3List.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + 2, ItemStack.field_111284_a.format(-motionFactor*100.0D), StatCollector.translateToLocal("attribute.name.generic.movementSpeed")));
	}

	@Override
	public byte[] getHeraldry(ItemStack stack) {
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		
		if(!stack.getTagCompound().hasKey(heraldryTag)){
			stack.getTagCompound().setByteArray(heraldryTag, SigilHelper.getDefault());
        }
        return stack.getTagCompound().getByteArray(heraldryTag);
	}
	
	@Override
	public void setHeraldry(ItemStack stack, byte[] code){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setByteArray(heraldryTag, code);
	}

	@Override
	public void removeHeraldry(ItemStack item) {
		if(item.hasTagCompound()){
			item.getTagCompound().setByteArray(heraldryTag, SigilHelper.getDefault());
		}
	}

	@Override
	public boolean shouldDoPass(HeraldyRenderPassess pass) {
		return true;
	}

	@Override
	public boolean useDefaultRenderer() {
		return true;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String layer) {
		return Battlegear.imageFolder+"armours/knights/knights-"+(slot==2?1:0)+".png";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		
		if(modelObject == null){
			modelObject = new HeraldryArmourModel(armorType);
		}
		
		HeraldryArmourModel model = (HeraldryArmourModel)modelObject;

		model.setItemStack(itemStack);
        if(armorSlot==0){
            model.bipedHead.showModel = true;
        }else if(armorSlot==1){
            model.bipedBody.showModel = true;
            model.bipedRightArm.showModel = true;
            model.bipedLeftArm.showModel = true;
        }else{
            model.bipedRightLeg.showModel = true;
            model.bipedLeftLeg.showModel = true;
            model.bipedBody.showModel = armorSlot == 2;
        }
		
		if(entityLiving != null){
            ItemStack heldRight = entityLiving.getHeldItem();
			model.heldItemRight = heldRight == null?0:1;
			if(entityLiving instanceof EntityPlayer)
			{
				Render renderer = RenderManager.instance.getEntityRenderObject(entityLiving);
				if(renderer instanceof RenderPlayer){
					ModelBiped modelArmor = armorSlot==2?((RenderPlayer) renderer).modelArmor:((RenderPlayer) renderer).modelArmorChestplate;
					model.heldItemLeft = modelArmor.heldItemLeft;
					model.heldItemRight = modelArmor.heldItemRight;
					model.aimedBow = modelArmor.aimedBow;
				}
			}
			model.isSneak = entityLiving.isSneaking();
		}
		
		return model;
	}

	@Override
	public String getBaseArmourPath(int armourSlot) {
		return Battlegear.imageFolder+"armours/knights/knights-base-"+(armourSlot==2?1:0)+".png";
	}

	@Override
	public String getPatternArmourPath(PatternStore pattern, int index, int armourSlot) {
		return Battlegear.imageFolder+"armours/knights/patterns/knights-pattern-"+(armourSlot==2?1:0)+"-"+index+".png";
	}

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        int max = getMaxAbsorption(armor);
        if(slot==1||slot==2){
            return new ArmorProperties(1,0.8D,max);
        }else if(slot==0){
            return new ArmorProperties(0,0.6D,max);
        }else if(slot==3){
            return new ArmorProperties(0,0.4D,max);
        }
        return null;
    }

    public int getMaxAbsorption(ItemStack armor) {
        return armor.getMaxDamage() + 1 - armor.getItemDamage();
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        if(slot==1){
            if(player.getEquipmentInSlot(1)!=null&&player.getEquipmentInSlot(1).getItem() instanceof ItemKnightArmour){
                if(player.getEquipmentInSlot(3)!=null&&player.getEquipmentInSlot(3).getItem() instanceof ItemKnightArmour){
                    if(player.getEquipmentInSlot(4)!=null&&player.getEquipmentInSlot(4).getItem() instanceof ItemKnightArmour){
                        return 9;
                    }
                }
            }
        }
        return damageReduceAmount;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
	    stack.damageItem(damage, entity);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
        player.motionX *= (1+motionFactor);
        player.motionZ *= (1+motionFactor);
        if(player.motionY>0.005D)//No need to change falling speed
            player.motionY *= (1+motionFactor);
    }
}
