package mods.battlegear2.items;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.heraldry.IHeraldyArmour;
import mods.battlegear2.api.heraldry.PatternStore;
import mods.battlegear2.client.heraldry.HeraldryArmourModel;
import mods.battlegear2.heraldry.SigilHelper;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemKnightArmour extends ItemArmor implements IHeraldyArmour, ISpecialArmor{

	public static final String[] armourTypes = {"boots", "legs", "plate", "helmet"};
	private Object modelObject;
    private final float motionFactor;

	public ItemKnightArmour(int armourType) {
		super(Battlegear.knightArmourMaterial, 1, EntityEquipmentSlot.values()[armourType+2]);
		setCreativeTab(BattlegearConfig.customTab);
		setUnlocalizedName("battlegear2:knights_armour." + armourTypes[armourType]);
		if(armourType == 2){//Chest
            motionFactor = -0.20F;
        }else if(armourType == 1){//Legs
            motionFactor = -0.15F;
        }else{
            motionFactor = -0.05F;
        }
        setRegistryName("battlegear2", "knights_armour." + armourTypes[armourType]);
		GameRegistry.register(this);
	}

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(@Nonnull Item par1, CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List){
        ItemStack armor = new ItemStack(par1);
        setHeraldry(armor,SigilHelper.getDefault());
        par3List.add(armor);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack armor, EntityPlayer player, List<String> par3List, boolean par4) {
		super.addInformation(armor, player, par3List, par4);
		par3List.add(String.format("%s +%d %s",
				TextFormatting.BLUE, this.damageReduceAmount, I18n.format("tooltip.armour.points")));
        par3List.add(TextFormatting.RED + I18n.format("attribute.modifier.take." + 2, ItemStack.DECIMALFORMAT.format(-motionFactor*100.0D), I18n.format("attribute.name.generic.movementSpeed")));
	}

	@Override
	public boolean hasHeraldry(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(heraldryTag);
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
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String layer) {
		String model = (3 - slot.getIndex()) + ".png";
		if (layer != null) {
			model = "base-" + model;
		}
		return Battlegear.imageFolder + "armours/knights/knights-" + model;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped defaul) {
		
		if(modelObject == null){
			modelObject = new HeraldryArmourModel(armorType);
		}
		
		HeraldryArmourModel model = (HeraldryArmourModel)modelObject;
		model.setItemStack(itemStack);
		
		return model;
	}

	@Override
	public String getBaseArmourPath(EntityEquipmentSlot armourSlot) {
		return Battlegear.imageFolder+"armours/knights/knights-base-"+(armourSlot==EntityEquipmentSlot.LEGS?1:0)+".png";
	}

	@Override
	public String getPatternArmourPath(PatternStore pattern, int index, EntityEquipmentSlot armourSlot) {
		return Battlegear.imageFolder+"armours/knights/patterns/knights-pattern-"+(armourSlot==EntityEquipmentSlot.LEGS?1:0)+"-"+index+".png";
	}

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, int slot) {
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
    public int getArmorDisplay(EntityPlayer player,@Nonnull ItemStack armor, int slot) {
        if(slot==2){//Chest
            if(Iterators.all(player.getArmorInventoryList().iterator(), new Predicate<ItemStack>() {
				@Override
				public boolean apply(@Nullable ItemStack input) {
					return input.getItem() instanceof ItemKnightArmour;
				}
			}))
			return 9;
        }
        return damageReduceAmount;
    }

    @Override
    public void damageArmor(EntityLivingBase entity,@Nonnull ItemStack stack, DamageSource source, int damage, int slot) {
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
