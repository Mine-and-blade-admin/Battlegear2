package assets.battlegear2.common.items;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import assets.battlegear2.api.IBackStabbable;
import assets.battlegear2.api.IBattlegearWeapon;
import assets.battlegear2.api.ILowHitTime;
import assets.battlegear2.api.IPenetrateWeapon;
import assets.battlegear2.api.ISpecialEffect;
import assets.battlegear2.common.utils.BattlegearConfig;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Made this extend the sword class (allows them to be enchanted)
public abstract class ItemWeapon extends ItemSword implements IBattlegearWeapon{

	protected final EnumToolMaterial material;
	protected String name;
	protected float baseDamage;
	
	public ItemWeapon(int par1, EnumToolMaterial material, String named) {
		super(par1, material);
		//May be unsafe, but will allow others to add weapons using custom materials (also more efficent)
		this.material = material;
		
		this.setCreativeTab(BattlegearConfig.customTab);
		this.maxStackSize = 1;
		
		if(material == EnumToolMaterial.EMERALD){
			this.name = named+".diamond";
		}else{
			this.name= named+"."+material.name().toLowerCase();
		}
		
		
		this.setUnlocalizedName("battlegear2:"+name);
		
		
		this.baseDamage = 4 + material.getDamageVsEntity();
	}
	
	
	public EnumToolMaterial getMaterial() {
		return this.material;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
		//TODO Change/Remove this when 1.6 is released
		par3List.add(String.format("%s +%d %s", EnumChatFormatting.BLUE, (baseDamage-1), 
				StatCollector.translateToLocal("tooltip.attack")));
	}
	
	@Override
	public Multimap func_111205_h() {
		Multimap map = HashMultimap.create();
        map.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.baseDamage, 0));
        return map;
    }
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase entityHit, EntityLivingBase entityHitting)
    {
		//Record the hurt times
		int hurtTimeTemp = entityHit.hurtTime;
		int hurtResistanceTimeTemp = entityHit.hurtResistantTime;
		if(stack.getItem() instanceof IPenetrateWeapon)
		{
			//Attack using the "generic" damage type (ignores armour)
			entityHit.attackEntityFrom(DamageSource.generic, ((IPenetrateWeapon)stack.getItem()).getPenetratingPower(stack, entityHit, entityHitting));
		}
		if(stack.getItem() instanceof IBackStabbable)
		{
			performBackStab(stack.getItem(), entityHit, entityHitting);
		}
		if(stack.getItem() instanceof ISpecialEffect)
		{
			performEffects((ISpecialEffect)stack.getItem(), entityHit, entityHitting);
		}
		if(stack.getItem() instanceof ILowHitTime)
		{
			//The usual is less than half the max hurt resistance time
			if(entityHit.hurtResistantTime < (float)(entityHit.maxHurtResistantTime) * 0.75F)
			{
				entityHit.hurtResistantTime = ((ILowHitTime)stack.getItem()).getHitTime(stack, entityHit);		
			}
		}
		else
		{
			//Re-apply the saved values
			entityHit.hurtTime = hurtTimeTemp;
			entityHit.hurtResistantTime = hurtResistanceTimeTemp;
		}
        return super.hitEntity(stack, entityHit, entityHitting);
    }
	
	protected void performBackStab(Item item, EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		//Get victim and murderer vector views at hit time
		double[] victimView = new double[]{entityHit.getLookVec().xCoord,entityHit.getLookVec().zCoord};
		double[] murdererView = new double[]{entityHitting.getLookVec().xCoord,entityHitting.getLookVec().zCoord};
		//back-stab conditions: vectors are closely enough aligned, (fuzzy parameter might need testing)
		//but not in opposite directions (face to face or sideways)
		if(Math.abs(victimView[0]*murdererView[1]-victimView[1]*murdererView[0])<0.01 && Math.signum(victimView[0])==Math.signum(murdererView[0]) && Math.signum(victimView[1])==Math.signum(murdererView[1]))
		{
			((IBackStabbable)item).onBackStab(entityHit, entityHitting);//Perform back stab effect
		}
	}

	protected void performEffects(ISpecialEffect item, EntityLivingBase entityHit, EntityLivingBase entityHitting) {
		PotionEffect[] effects= item.getEffectsOnHit(entityHit, entityHit);
		for(PotionEffect effect:effects){
			//add effects if they aren't already applied, with a 10% chance
			if(!entityHit.isPotionActive(effect.getPotionID()) && new Random().nextFloat() * 10>9)
				entityHit.addPotionEffect(effect);
		}
	}
}
