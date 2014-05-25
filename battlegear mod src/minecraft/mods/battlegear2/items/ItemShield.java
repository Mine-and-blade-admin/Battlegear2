package mods.battlegear2.items;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.shield.IArrowCatcher;
import mods.battlegear2.api.IDyable;
import mods.battlegear2.api.IEnchantable;
import mods.battlegear2.api.ISheathed;
import mods.battlegear2.api.shield.IArrowDisplay;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.shield.ShieldType;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemShield extends Item implements IShield, IDyable, IEnchantable, ISheathed, IArrowCatcher, IArrowDisplay{

    public ShieldType enumShield;

    private IIcon backIcon;
    private IIcon trimIcon;

    public ItemShield(ShieldType enumShield) {
        super();
        this.setCreativeTab(BattlegearConfig.customTab);

        this.enumShield = enumShield;

        this.setUnlocalizedName("battlegear2:shield."+enumShield.getName());
        this.setTextureName("battlegear2:shield/shield."+enumShield.getName());

        this.setMaxDamage(enumShield.getMaxDamage());
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        GameRegistry.registerItem(this, this.getUnlocalizedName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        backIcon = par1IconRegister.registerIcon("battlegear2:shield/shield."+enumShield.getName()+".back");
        trimIcon = par1IconRegister.registerIcon("battlegear2:shield/shield."+enumShield.getName()+".trim");
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

    public IIcon getBackIcon() {
        return backIcon;
    }

    public IIcon getTrimIcon() {
        return trimIcon;
    }

    @Override
    public float getDecayRate(ItemStack shield) {
    	int use = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.shieldUsage.effectId, shield);
        return enumShield.getDecayRate()*(1-use*0.1F);
    }
    
    @Override
    public float getRecoveryRate(ItemStack shield){
    	int recover = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.shieldRecover.effectId, shield);
    	return 0.01F*(1+recover*0.2F);//should take 5 seconds to fully recover without enchantment
    }

    @Override
    public boolean canBlock(ItemStack shield, DamageSource source) {
        return !source.isUnblockable();
    }

    @Override
    public void blockAnimation(EntityPlayer player, float dmg){
        player.worldObj.playSoundAtEntity(player, "battlegear2:shield", 1, 1);
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
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        par3List.add("");

        par3List.add(EnumChatFormatting.DARK_GREEN+
                ItemStack.field_111284_a.format( 1F / (enumShield.getDecayRate()) / 20F)+
                StatCollector.translateToLocal("attribute.shield.block.time"));

        int arrowCount = getArrowCount(par1ItemStack);
        if(arrowCount > 0){
            par3List.add(String.format("%s%s %s", EnumChatFormatting.GOLD, arrowCount, StatCollector.translateToLocal("attribute.shield.arrow.count")));
        }

    }

    /**
     * Return whether the specified armor ItemStack has a color.
     */
    @Override
    public boolean hasColor(ItemStack par1ItemStack)
    {
        return par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("display") && par1ItemStack.getTagCompound().getCompoundTag("display").hasKey("color");
    }

    /**
     * Return the color for the specified armor ItemStack.
     */
    @Override
    public int getColor(ItemStack par1ItemStack)
    {
        {
            NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

            if (nbttagcompound == null)
            {
                return getDefaultColor(par1ItemStack);
            }
            else
            {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
                return nbttagcompound1 == null ? getDefaultColor(par1ItemStack): (nbttagcompound1.hasKey("color") ? nbttagcompound1.getInteger("color") : getDefaultColor(par1ItemStack));
            }
        }
    }

    /**
     * Remove the color from the specified armor ItemStack.
     */
    @Override
    public void removeColor(ItemStack par1ItemStack)
    {
        NBTTagCompound nbttagcompound = par1ItemStack.getTagCompound();

        if (nbttagcompound != null)
        {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

            if (nbttagcompound1.hasKey("color"))
            {
                nbttagcompound1.removeTag("color");
            }
        }
    }

    @Override
    public int getDefaultColor(ItemStack par1ItemStack) {
        return enumShield.getDefaultRGB();
    }

    @Override
    public void setColor(ItemStack par1ItemStack, int par2)
    {

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
		return true;
	}
	
	@Override
	public int getItemEnchantability(){
        return enumShield.getEnchantability();
    }

	@Override
	public boolean sheatheOnBack(ItemStack item) {
		return true;
	}
}
