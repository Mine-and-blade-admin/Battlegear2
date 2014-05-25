package mods.battlegear2.items;

import java.util.Iterator;
import java.util.UUID;

import mods.battlegear2.api.weapons.*;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
//Made this extend the sword class (allows them to be enchanted)
public abstract class ItemWeapon extends ItemSword implements IBattlegearWeapon {

    public static final UUID penetrateArmourUUID = UUID.fromString("DB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final RangedAttribute armourPenetrate = new RangedAttribute("weapon.penetrateArmor", 0.0D, 0.0D, Double.MAX_VALUE);

    public static final UUID dazeUUID = UUID.fromString("927f0df6-946e-4e78-a479-c2c13034edb5");
    public static final RangedAttribute daze = new RangedAttribute("weapon.daze", 3.0D, 0.0D, Double.MAX_VALUE);

    public static final UUID extendReachUUID = UUID.fromString("fb557a05-866e-4017-990b-aab8450bf41b");
    public static final RangedAttribute extendedReach = new RangedAttribute("weapon.extendedReach", 2.0D, 0.0D, Double.MAX_VALUE);

    public static final UUID attackSpeedUUID = UUID.fromString("4833af8b-40f2-44c5-8405-735f7003b1be");
    public static final RangedAttribute attackSpeed = new RangedAttribute("weapon.attackSpeed", 0.0D, -10.0D, 10.0D);

    public static final UUID mountedBonusUUID = UUID.fromString("fd234540-d099-4a05-a4ac-0ad7c11a8b65");
    public static final RangedAttribute mountedBonus = new RangedAttribute("weapon.mountedBonus", 0.0D, 0.0D, Double.MAX_VALUE);

    protected final ToolMaterial material;
	protected String name;
	protected float baseDamage;
	
	public ItemWeapon(ToolMaterial material, String named) {
		super(material);
		//May be unsafe, but will allow others to add weapons using custom materials (also more efficient)
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
	public Multimap getAttributeModifiers(ItemStack stack) {
		Multimap map = HashMultimap.create();
		map.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.baseDamage, 0));
        return map;
    }

    public float getModifiedAmount(ItemStack stack, String modifierName){
        Iterator itr = stack.getAttributeModifiers().get(modifierName).iterator();
        float f = 0;
        while(itr.hasNext()){
            f+= ((AttributeModifier)itr.next()).getAmount();
        }
        return f;
    }
}
