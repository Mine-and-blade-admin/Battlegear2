package mods.battlegear2.items;

import mods.battlegear2.api.weapons.IPenetrateWeapon;
import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;


//Should we make this also use the heraldry? It actually doesn't look as good as the sword (and makes the sword a little more special)
public class ItemWaraxe extends OneHandedWeapon implements IPenetrateWeapon{ // implements IHeraldyItem{ Don't know if we want to do this or not
	
	private int ignoreDamageAmount;
	
	/*
	private Icon baseIcon;
	private Icon trimIcon;
	private Icon postRenderIcon;
	 */
	public ItemWaraxe(int par1, EnumToolMaterial material, String name, int ignoreDamageAmount) {
		super(par1,material,name);
		this.ignoreDamageAmount = ignoreDamageAmount;
		//set the base damage to that of lower than usual (balance)
		this.baseDamage -= 1 + ignoreDamageAmount;
		this.setMaxDamage(material.getMaxUses()*2);
    }
	
	@Override
	public boolean canHarvestBlock(Block par1Block)//Waraxe can harvest logs
    {
        return par1Block.blockID == Block.wood.blockID;
    }


    @Override
    public int getPenetratingPower(ItemStack stack) {
        return ignoreDamageAmount;
    }
}
