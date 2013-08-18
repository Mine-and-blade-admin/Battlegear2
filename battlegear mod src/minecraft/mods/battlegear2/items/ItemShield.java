package mods.battlegear2.items;

import cpw.mods.fml.common.FMLCommonHandler;
import mods.battlegear2.api.IShield;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.EnumShield;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemShield extends Item implements IShield{

    EnumShield enumShield;

    private Icon backIcon;
    private Icon trimIcon;

    public static final float[] arrowX = new float[64];
    public static final float[] arrowY = new float[64];
    public static final float[] arrowDepth = new float[64];
    public static final float[] pitch = new float[64];
    public static final float[] yaw = new float[64];

    static{
        for(int i = 0; i < 64; i++){
            double r = Math.random()*5;
            double theta = Math.random()*Math.PI*2;

            arrowX[i] = (float)(r * Math.cos(theta));
            arrowY[i] = (float)(r * Math.sin(theta));
            arrowDepth[i] = (float)(Math.random()* 0.5 + 0.5F);

            pitch[i] = (float)(Math.random()*50 - 25);
            yaw[i] = (float)(Math.random()*50 - 25);
        }


    }

    public ItemShield(int id, EnumShield enumShield) {
        super(id);
        this.setCreativeTab(BattlegearConfig.customTab);

        this.enumShield = enumShield;

        this.setUnlocalizedName("battlegear2:shield."+enumShield.getName());
        this.func_111206_d("battlegear2:shield/shield."+enumShield.getName());

        this.getShareTag();

        this.setMaxDamage(enumShield.getMaxDamage());
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);

        backIcon = par1IconRegister.registerIcon("battlegear2:shield/shield."+enumShield.getName()+".back");
        trimIcon = par1IconRegister.registerIcon("battlegear2:shield/shield."+enumShield.getName()+".trim");

    }

    public int getArrowCount(ItemStack stack){
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("arrows")){
            return stack.getTagCompound().getShort("arrows");
        }else
            return 0;
    }

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

    public Icon getBackIcon() {
        return backIcon;
    }

    public Icon getTrimIcon() {
        return trimIcon;
    }

    @Override
    public float getDecayRate(ItemStack shield) {
        return enumShield.getDecayRate();
    }

    @Override
    public boolean canBlock(ItemStack shield, DamageSource source) {
        return !source.isUnblockable();
    }

    @Override
    public float getDamageDecayRate(ItemStack shield, float amount) {
        return enumShield.getDamageDecay();
    }

    @Override
    public float getBlockAngle(ItemStack shield) {
        return 60;
    }
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        par3List.add("");

        par3List.add(EnumChatFormatting.DARK_GREEN+
                ItemWeapon.decimal_format.format((float) 1F / (enumShield.getDecayRate()) / 20F)+
                StatCollector.translateToLocal("attribute.name.shield.block.time"));

        int arrowCount = getArrowCount(par1ItemStack);
        System.out.println(arrowCount);
        if(arrowCount > 0){
            par3List.add(String.format("%s%s %s", EnumChatFormatting.GOLD, arrowCount, StatCollector.translateToLocal("attribute.name.shield.arrow.count")));
        }

    } 
}
