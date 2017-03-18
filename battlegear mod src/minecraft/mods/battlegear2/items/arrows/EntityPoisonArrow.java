package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * An arrow which deals wither effect on living entities
 * @author GotoLink
 *
 */
public class EntityPoisonArrow extends AbstractMBArrow{
	@GameRegistry.ObjectHolder("minecraft:wither")
	public static Potion WITHER;
	public EntityPoisonArrow(World par1World) {
		super(par1World);
	}

	public EntityPoisonArrow(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityPoisonArrow(World par1World, double x, double y, double z) {
		super(par1World, x, y, z);
    }

	@Override
	public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
		return false;
	}

	@Override
	protected void arrowHit(EntityLivingBase entityHit) {
		entityHit.addPotionEffect(new PotionEffect(WITHER, 200));
	}

	@Override
	public void onHitGround(BlockPos pos) {
        setDead();
	}
}
