package mods.battlegear2.items;

import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import mods.battlegear2.api.IPenetrateWeapon;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


//Should we make this also use the hearaldry? It actually doesn't look as good as the sword (and makes the sword a little more special)
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
		this.baseDamage = baseDamage-1- ignoreDamageAmount;
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
