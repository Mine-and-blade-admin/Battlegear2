package mods.battlegear2.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import mods.battlegear2.api.weapons.Attributes;
import mods.battlegear2.api.weapons.IBattlegearWeapon;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Locale;
//Made this extend the sword class (allows them to be enchanted)
public abstract class ItemWeapon extends ItemSword implements IBattlegearWeapon, Attributes {

    protected final ToolMaterial material;
	protected String name;
	protected float baseDamage;
	
	public ItemWeapon(ToolMaterial material, String named) {
		super(material);
		//May be unsafe, but will allow others to add weapons using custom materials (also more efficient)
		this.material = material;
        this.setCreativeTab(BattlegearConfig.customTab);

		this.name = named+"."+material.name().toLowerCase(Locale.ENGLISH);
		
		this.setUnlocalizedName("battlegear2:"+name);
		this.setRegistryName("battlegear2", name);
		this.baseDamage = 4 + material.getDamageVsEntity();
	}

	public ToolMaterial getMaterial() {
		return this.material;
	}

	@Nonnull
	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
		Multimap<String, AttributeModifier> map = HashMultimap.<String, AttributeModifier>create();
		if(slot.getSlotType()== EntityEquipmentSlot.Type.HAND) {
			map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.baseDamage, 0));
			map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4D, 0));
		}
        return map;
    }

	@Override
	public boolean canHarvestBlock(IBlockState par1Block)
	{
		return false;
	}

	@Override
	public final ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		return super.onItemRightClick(world, player, hand);
	}

	@Override
	@Nonnull
	public final EnumAction getItemUseAction(ItemStack par1ItemStack){
		return EnumAction.NONE;
	}

	@Override
	public final int getMaxItemUseDuration(ItemStack itemStack){
		return 0;
	}
}
