package mods.battlegear2.items;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import mods.battlegear2.api.weapons.*;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

//Made this extend the sword class (allows them to be enchanted)
public abstract class ItemWeapon extends ItemSword implements IBattlegearWeapon {

    public static final DecimalFormat decimal_format = new DecimalFormat("#.###");

    protected static final UUID penetrateArmourUUID = UUID.fromString("DB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final RangedAttribute armourPenetrate = new RangedAttribute("weapon.penetrateArmor", 0.0D, 0.0D, Double.MAX_VALUE);

    protected static final UUID dazeUUID = UUID.fromString("927f0df6-946e-4e78-a479-c2c13034edb5");
    protected static final RangedAttribute daze = new RangedAttribute("weapon.daze", 3.0D, 0.0D, Double.MAX_VALUE);

    protected static final UUID extendReachUUID = UUID.fromString("fb557a05-866e-4017-990b-aab8450bf41b");
    protected static final RangedAttribute extendedReach = new RangedAttribute("weapon.extendedReach", 2.0D, 0.0D, Double.MAX_VALUE);


    protected final ToolMaterial material;
	protected String name;
	protected float baseDamage;
	
	public ItemWeapon(ToolMaterial material, String named) {
		super(material);
		//May be unsafe, but will allow others to add weapons using custom materials (also more efficent)
		this.material = material;
        this.setCreativeTab(BattlegearConfig.customTab);
		
		if(material == ToolMaterial.EMERALD){
			this.name = named+".diamond";
		}else{
			this.name= named+"."+material.name().toLowerCase();
		}
		
		this.setUnlocalizedName("battlegear2:"+name);
		this.setTextureName("battlegear2:"+name);
		
		this.baseDamage = 4 + material.getDamageVsEntity();
	}
	
	
	public ToolMaterial getMaterial() {
		return this.material;
	}
	
	@Override
	public Multimap getItemAttributeModifiers() {
		Multimap map = HashMultimap.create();
		map.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.baseDamage, 0));

        return map;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);


        if(this instanceof IExtendedReachWeapon || this instanceof IPenetrateWeapon || this instanceof IHitTimeModifier || this instanceof ISpecialEffect){
            par3List.add("");

            if(this instanceof IPenetrateWeapon){
                par3List.add(EnumChatFormatting.DARK_GREEN+
                        StatCollector.translateToLocalFormatted("attribute.modifier.plus."+ 0,
                                decimal_format.format(((IPenetrateWeapon)this).getPenetratingPower(par1ItemStack)),
                                        StatCollector.translateToLocal("attribute.weapon.penetrateArmor")));
            }

            if(this instanceof IExtendedReachWeapon){
                float reach = ((IExtendedReachWeapon)this).getReachModifierInBlocks(par1ItemStack);

                if(reach > 0){
                    par3List.add(EnumChatFormatting.DARK_GREEN+
                            StatCollector.translateToLocalFormatted("attribute.modifier.plus."+ 0,
                                    decimal_format.format(reach),
                                            StatCollector.translateToLocal("attribute.weapon.extendedReach")));
                }else{
                    par3List.add(EnumChatFormatting.RED+
                            StatCollector.translateToLocalFormatted("attribute.modifier.take."+ 0,
                                    decimal_format.format(-1 * reach),
                                            StatCollector.translateToLocal("attribute.weapon.extendedReach")));
                }
            }

            if(this instanceof IHitTimeModifier){
                int hitMod = ((IHitTimeModifier)this).getHitTime(par1ItemStack, null);
                if(hitMod > 0){
                    par3List.add(EnumChatFormatting.RED+
                            StatCollector.translateToLocalFormatted("attribute.modifier.take."+ 1,
                                    decimal_format.format((float)hitMod / 10F * 100),
                                            StatCollector.translateToLocal("attribute.weapon.attackSpeed")));
                }else{
                    par3List.add(EnumChatFormatting.DARK_GREEN+
                            StatCollector.translateToLocalFormatted("attribute.modifier.plus."+ 1,
                                    decimal_format.format(-(float)hitMod / 10F * 100),
                                            StatCollector.translateToLocal("attribute.weapon.attackSpeed")));
                }
            }

            if(this instanceof IBackStabbable){
                par3List.add(EnumChatFormatting.GOLD+
                        StatCollector.translateToLocal("attribute.weapon.backstab"));

            }
        }

    }
}
