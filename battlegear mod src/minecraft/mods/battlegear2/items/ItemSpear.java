package mods.battlegear2.items;

import com.google.common.collect.Multimap;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.battlegear2.api.IBackSheathedRender;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

public class ItemSpear extends TwoHandedWeapon implements IExtendedReachWeapon,IBackSheathedRender {

    //Will make it one more than a sword
    private final int mounted_extra_damage;
    private final float reach;
    public IIcon bigIcon;

    public ItemSpear(ToolMaterial material, String name, int mount, float reach) {
		super(material,name);
        this.mounted_extra_damage = mount;
        this.reach = reach;
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 2;
        GameRegistry.registerItem(this, this.name);
	}
	
	@Override
	public float getReachModifierInBlocks(ItemStack stack) {
		return getModifiedAmount(stack, extendedReach.getAttributeUnlocalizedName());
	}

	@Override
	public boolean allowOffhand(ItemStack mainhand, ItemStack offhand) {
		return super.allowOffhand(mainhand, offhand) || offhand.getItem() instanceof IShield;
	}

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put(extendedReach.getAttributeUnlocalizedName(), new AttributeModifier(extendReachUUID, "Reach Modifier", this.reach, 0));
        map.put(mountedBonus.getAttributeUnlocalizedName(), new AttributeModifier(mountedBonusUUID, "Attack Modifier", this.mounted_extra_damage, 0));
        return map;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
        return par1ItemStack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack){
        return EnumAction.none;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack){
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        bigIcon = par1IconRegister.registerIcon(this.getIconString()+".big");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void preRenderBackSheathed(ItemStack itemStack, int amountOnBack, RenderPlayerEvent event, boolean inMainHand) {
        if(inMainHand) {
            GL11.glScalef(0.6F, -0.6F, 0.6F);
            GL11.glTranslatef(0, -1, 0);
        }else{//Case never reached anyway
            GL11.glScalef(-0.6F, 0.6F, 0.6F);
        }
    }
}
