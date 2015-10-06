package mods.battlegear2.items;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
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
        GameRegistry.registerItem(this, this.name);
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put(armourPenetrate.getAttributeUnlocalizedName(), new AttributeModifier(penetrateArmourUUID, "Attack Modifier", this.ignoreDamageAmount, 0));
        return map;
    }
	
	@Override
	public boolean canHarvestBlock(Block par1Block)//Waraxe can harvest logs
    {
        return par1Block instanceof BlockLog;
    }
}
