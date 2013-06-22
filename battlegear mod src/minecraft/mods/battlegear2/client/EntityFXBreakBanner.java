package mods.battlegear2.client;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class EntityFXBreakBanner extends EntityFX{

	
	public EntityFXBreakBanner(World par1World, double par2, double par4,
			double par6, double par8, double par10, double par12, float gravity, float[]colour) {
		super(par1World, par2, par4, par6, par8, par10, par12);
		
		particleGravity = gravity;
		this.particleScale *= 2.0F;
		
		this.setRBGColorF(colour[0], colour[1], colour[2]);
	}

	

}
