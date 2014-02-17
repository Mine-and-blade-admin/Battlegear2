package mods.battlegear2.items.arrows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
/**
 * An arrow which deals wither effect on living entities
 * @author GotoLink
 *
 */
public class EntityPoisonArrow extends AbstractMBArrow{

	public EntityPoisonArrow(World par1World) {
		super(par1World);
	}

	public EntityPoisonArrow(World par1World, EntityLivingBase par2EntityLivingBase, float par3) {
        super(par1World, par2EntityLivingBase, par3);
    }

    public EntityPoisonArrow(World par1World, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase, float par4, float par5) {
        super(par1World, par2EntityLivingBase, par3EntityLivingBase, par4, par5);
    }

	@Override
	public boolean onHitEntity(Entity entityHit, DamageSource source, float ammount) {
		boolean flag = false;
		if(entityHit instanceof EntityLivingBase){
			((EntityLivingBase) entityHit).addPotionEffect(new PotionEffect(Potion.wither.id, 200));
            ((EntityLivingBase) entityHit).setArrowCountInEntity(((EntityLivingBase) entityHit).getArrowCountInEntity()+1);
			flag = true;
		}
		setDead();
		return flag;
	}

	@Override
	public void onHitGround(int x, int y, int z) {
        setDead();
	}
}
