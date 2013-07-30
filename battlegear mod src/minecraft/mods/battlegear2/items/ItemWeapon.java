package mods.battlegear2.items;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import mods.battlegear2.api.*;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

//Made this extend the sword class (allows them to be enchanted)
public abstract class ItemWeapon extends ItemSword implements IBattlegearWeapon {

    public static final DecimalFormat field_111284_a = new DecimalFormat("#.###");

    protected static final UUID penetrateArmourUUID = UUID.fromString("DB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final RangedAttribute armourPenatrate = new RangedAttribute("weapon.penetrateArmor", 0.0D, 0.0D, Double.MAX_VALUE);

    protected static final UUID dazeUUID = UUID.fromString("927f0df6-946e-4e78-a479-c2c13034edb5");
    protected static final RangedAttribute daze = new RangedAttribute("weapon.daze", 3.0D, 0.0D, Double.MAX_VALUE);

    protected static final UUID extendReachUUID = UUID.fromString("fb557a05-866e-4017-990b-aab8450bf41b");
    protected static final RangedAttribute extendedReach = new RangedAttribute("weapon.extendedReach", 2.0D, 0.0D, Double.MAX_VALUE);




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
		this.func_111206_d("battlegear2:"+name);
		
		this.baseDamage = 4 + material.getDamageVsEntity();
	}
	
	
	public EnumToolMaterial getMaterial() {
		return this.material;
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
       /*
        if(stack.getItem() instanceof IExtendedReachWeapon){
            float reachMod = ((IExtendedReachWeapon) stack.getItem()).getReachModifierInBlocks(stack);
            if(reachMod < 0){
                System.out.println(entityHitting.getDistanceToEntity(entityHit));
                System.out.println(reachMod + 2 < entityHitting.getDistanceToEntity(entityHit));
                if(reachMod + 2 < entityHitting.getDistanceToEntity(entityHit)){
                    return false;
                }
            }
        }
        */

		if(stack.getItem() instanceof IPenetrateWeapon)
		{
			//Attack using the "generic" damage type (ignores armour)
			entityHit.attackEntityFrom(DamageSource.generic, ((IPenetrateWeapon)stack.getItem()).getPenetratingPower(stack));
		}
		if(stack.getItem() instanceof IBackStabbable)
		{
			performBackStab(stack.getItem(), entityHit, entityHitting);
		}
		if(stack.getItem() instanceof ISpecialEffect)
		{
			performEffects((ISpecialEffect)stack.getItem(), entityHit, entityHitting);
		}
		if(stack.getItem() instanceof IHitTimeModifier)
		{
            //int hurtTimeTemp = entityHit.hurtTime;
            //int hurtResistanceTimeTemp = entityHit.hurtResistantTime;

			//The usual is less than half the max hurt resistance time
			if(entityHit.hurtResistantTime < (float)(entityHit.maxHurtResistantTime) * (0.5) + ((IHitTimeModifier)this).getHitTime(stack, entityHit))
			{
				entityHit.hurtResistantTime = ((IHitTimeModifier)stack.getItem()).getHitTime(stack, entityHit);
			}else{
                entityHit.hurtResistantTime = entityHit.maxHurtResistantTime + ((IHitTimeModifier)this).getHitTime(stack, entityHit);
            }
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

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);


        if(this instanceof IExtendedReachWeapon || this instanceof IPenetrateWeapon || this instanceof IHitTimeModifier || this instanceof ISpecialEffect){
            par3List.add("");

            if(this instanceof IPenetrateWeapon){
                par3List.add(EnumChatFormatting.DARK_GREEN+
                        StatCollector.translateToLocalFormatted("attribute.modifier.plus."+ 0,
                                new Object[] {field_111284_a.format(((IPenetrateWeapon)this).getPenetratingPower(par1ItemStack)),
                                        StatCollector.translateToLocal("attribute.name.weapon.penetrateArmor")}));
            }

            if(this instanceof IExtendedReachWeapon){
                float reach = ((IExtendedReachWeapon)this).getReachModifierInBlocks(par1ItemStack);

                if(reach > 0){
                    par3List.add(EnumChatFormatting.DARK_GREEN+
                            StatCollector.translateToLocalFormatted("attribute.modifier.plus."+ 0,
                                    new Object[] {field_111284_a.format(reach),
                                            StatCollector.translateToLocal("attribute.name.weapon.extendedReach")}));
                }else{

                    par3List.add(EnumChatFormatting.RED+
                            StatCollector.translateToLocalFormatted("attribute.modifier.take."+ 0,
                                    new Object[] {field_111284_a.format(-1 * reach),
                                            StatCollector.translateToLocal("attribute.name.weapon.extendedReach")}));

                }
            }

            if(this instanceof IHitTimeModifier){
                int hitMod = ((IHitTimeModifier)this).getHitTime(par1ItemStack, null);
                if(hitMod > 0){
                    par3List.add(EnumChatFormatting.RED+
                            StatCollector.translateToLocalFormatted("attribute.modifier.plus."+ 1,
                                    new Object[] {field_111284_a.format((float)hitMod / 10F * 100),
                                            StatCollector.translateToLocal("attribute.name.weapon.attackSpeed")}));
                }else{
                    par3List.add(EnumChatFormatting.DARK_GREEN+
                            StatCollector.translateToLocalFormatted("attribute.modifier.take."+ 1,
                                    new Object[] {field_111284_a.format(-(float)hitMod / 10F * 100),
                                            StatCollector.translateToLocal("attribute.name.weapon.attackSpeed")}));
                }
            }

            if(this instanceof IBackStabbable){
                par3List.add(EnumChatFormatting.GOLD+
                        StatCollector.translateToLocal("attribute.name.weapon.backstab"));

            }
        }

    }
}
