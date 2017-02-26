package mods.battlegear2.items;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.IDyable;
import mods.battlegear2.api.IEnchantable;
import mods.battlegear2.api.ISheathed;
import mods.battlegear2.api.shield.IArrowCatcher;
import mods.battlegear2.api.shield.IArrowDisplay;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.shield.ShieldType;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemShield extends Item implements IShield, IDyable, IEnchantable, ISheathed, IArrowCatcher, IArrowDisplay, IFuelHandler{

    public ShieldType enumShield;

    public ItemShield(ShieldType enumShield) {
        super();
        this.setCreativeTab(BattlegearConfig.customTab);

        this.enumShield = enumShield;

        this.setUnlocalizedName("battlegear2:shield."+enumShield.getName());
        this.setRegistryName("battlegear2:shield."+enumShield.getName());
        this.setMaxDamage(enumShield.getMaxDamage());
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        GameRegistry.register(this);
    }

    @Override
    public boolean catchArrow(ItemStack shield, EntityPlayer player, IProjectile arrow){
        if(arrow instanceof EntityArrow){
            setArrowCount(shield, getArrowCount(shield)+1);
            player.setArrowCountInEntity(player.getArrowCountInEntity() - 1);
            ((EntityArrow)arrow).setDead();
            return true;
        }
        return false;
    }

    @Override
    public int getArrowCount(ItemStack stack){
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("arrows")){
            return stack.getTagCompound().getShort("arrows");
        }else
            return 0;
    }

    @Override
    public void setArrowCount(ItemStack stack, int count){

        if(!stack.hasTagCompound()){
            stack.setTagCompound(new NBTTagCompound());
        }

        //Should never happen, you would need A LOT of arrows for this to happen
        if(count > Short.MAX_VALUE){
            count = Short.MAX_VALUE;
        }

        stack.getTagCompound().setShort("arrows", (short)count);
    }

    @Override
    public float getDecayRate(ItemStack shield) {
    	int use = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.shieldUsage, shield);
        return enumShield.getDecayRate()*(1-use*0.1F);
    }
    
    @Override
    public float getRecoveryRate(ItemStack shield){
    	int recover = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.shieldRecover, shield);
    	return 0.01F*(1+recover*0.2F);//should take 5 seconds to fully recover without enchantment
    }

    @Override
    public boolean canBlock(ItemStack shield, DamageSource source) {
        return !source.isUnblockable();
    }

    @Override
    public void blockAnimation(EntityPlayer player, float dmg){
        player.world.playSound(null, player.getPosition(), Battlegear.shieldSound, SoundCategory.PLAYERS, 1, 1);
    }

    @Override
    public float getDamageReduction(ItemStack shield, DamageSource source){
        return 0;
    }

    @Override
    public float getDamageDecayRate(ItemStack shield, float amount) {
        return enumShield.getDamageDecay() * amount;
    }

    @Override
    public float getBlockAngle(ItemStack shield) {
        return 60;
    }

    @Override
    public int getBashTimer(ItemStack shield) {
        return 10;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        par3List.add("");

        par3List.add(TextFormatting.DARK_GREEN+
                I18n.format("attribute.shield.block.time", ItemStack.DECIMALFORMAT.format(1F / (enumShield.getDecayRate()) / 20F)));

        int arrowCount = getArrowCount(par1ItemStack);
        if(arrowCount > 0){
            par3List.add(TextFormatting.GOLD+
                    I18n.format("attribute.shield.arrow.count", arrowCount));
        }

    }

    @SideOnly(Side.CLIENT)
    public int getColor(ItemStack stack, int renderPass) {
        if (renderPass == 0 || (this.enumShield == ShieldType.WOOD && renderPass < 2))
            return getColor(stack);
        return -1;
    }

    /**
     * Return whether the specified armor ItemStack has a color.
     */
    @Override
    public boolean hasColor(ItemStack par1ItemStack){
        return par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("display") && par1ItemStack.getTagCompound().getCompoundTag("display").hasKey("color");
    }

    /**
     * Return the color for the specified ItemStack.
     */
    @Override
    public int getColor(ItemStack par1ItemStack){
        NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();
        if (nbttagcompound == null){
            return getDefaultColor(par1ItemStack);
        }
        else{
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
            return nbttagcompound1 == null ? getDefaultColor(par1ItemStack): (nbttagcompound1.hasKey("color") ? nbttagcompound1.getInteger("color") : getDefaultColor(par1ItemStack));
        }
    }

    /**
     * Remove the color from the specified ItemStack.
     */
    @Override
    public void removeColor(ItemStack par1ItemStack){
        NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

        if (nbttagcompound != null) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

            if (nbttagcompound1.hasKey("color")) {
                nbttagcompound1.removeTag("color");
            }
        }
    }

    @Override
    public int getDefaultColor(ItemStack par1ItemStack) {
        return enumShield.getDefaultRGB();
    }

    @Override
    public void setColor(ItemStack par1ItemStack, int par2) {
        NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();
        if (nbttagcompound == null)
        {
            nbttagcompound = new NBTTagCompound();
            par1ItemStack.setTagCompound(nbttagcompound);
        }

        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

        if (!nbttagcompound.hasKey("display"))
        {
            nbttagcompound.setTag("display", nbttagcompound1);
        }

        nbttagcompound1.setInteger("color", par2);
    }

	@Override
	public boolean isEnchantable(Enchantment baseEnchantment, ItemStack stack) {
		return baseEnchantment.type == EnumEnchantmentType.ALL;
	}
	
	@Override
	public int getItemEnchantability(){
        return enumShield.getEnchantability();
    }

	@Override
	public boolean sheatheOnBack(ItemStack item) {
		return true;
	}

    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repair){
        return this.enumShield.canBeRepairedWith(repair);
    }

    @Override
    public int getBurnTime(ItemStack fuel) {
        if(fuel.getItem() == this)
            return 300;
        return 0;
    }
}
