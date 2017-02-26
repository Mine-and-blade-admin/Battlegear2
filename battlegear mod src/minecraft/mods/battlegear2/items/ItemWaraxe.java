package mods.battlegear2.items;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.registry.GameRegistry;

//Should we make this also use the heraldry? It actually doesn't look as good as the sword (and makes the sword a little more special)
public class ItemWaraxe extends OneHandedWeapon { // implements IHeraldyItem{ Don't know if we want to do this or not
	
	private final int ignoreDamageAmount;

	public ItemWaraxe(ToolMaterial material, String name, int ignoreDamageAmount) {
		super(material,name);
		this.ignoreDamageAmount = ignoreDamageAmount;
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 1 + ignoreDamageAmount;
		this.setMaxDamage(material.getMaxUses()*2);
        GameRegistry.register(this);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(slot);
        if(slot.getSlotType() == EntityEquipmentSlot.Type.HAND)
            map.put(armourPenetrate.getName(), new AttributeModifier(penetrateArmourUUID, "Attack Modifier", this.ignoreDamageAmount, 0));
        return map;
    }
	
	@Override
	public boolean canHarvestBlock(IBlockState par1Block)//Waraxe can harvest logs
    {
        return par1Block.getBlock() instanceof BlockLog;
    }
}
