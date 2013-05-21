package mods.battlegear2.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;


import mods.battlegear2.api.IHeraldyItem;
import mods.battlegear2.client.gui.BattlegearGUITickHandeler;
import mods.battlegear2.client.heraldry.HeradrySwordRenderer;
import mods.battlegear2.client.heraldry.HeraldryItemRenderer;
import mods.battlegear2.client.heraldry.HeraldyPattern;
import mods.battlegear2.client.keybinding.BattlegearKeyHandeler;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.BattlegearPacketHandeler;
import mods.battlegear2.common.BattlegearTickHandeler;
import mods.battlegear2.common.BattlemodeHookContainerClass;
import mods.battlegear2.common.CommonProxy;
import mods.battlegear2.common.utils.BattlegearConfig;
import mods.battlegear2.common.utils.EnumBGAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.StitchSlot;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy{

	public static RenderEngine renderEngine = FMLClientHandler.instance().getClient().renderEngine;
	public static Icon[] backgroundIcon;
	public static Icon[][] swordIcons;
	
	@Override
	public void registerTextures(Object iconRegister){

		IconRegister register = (IconRegister)iconRegister;
		HeraldyPattern.setAllIcon(register);
		
		System.out.println("register");
		this.backgroundIcon=new Icon[2];
        for (int i=0;i<2;i++){
        	this.backgroundIcon[i]=
        			register.registerIcon(
        					"battlegear2:slots/".concat(i==0?"mainhand":"offhand"));
        }
        
        this.swordIcons = new Icon[5][3];
        for(int i = 0; i < swordIcons.length; i++){
        	this.swordIcons[i][0]=
        			register.registerIcon(
        					"battlegear2:sword-heraldry/sword-blade-"+i);
        	this.swordIcons[i][1]=
        			register.registerIcon(
        					"battlegear2:sword-heraldry/sword-hilt-"+i);
        	this.swordIcons[i][2]=
        			register.registerIcon(
        					"battlegear2:sword-heraldry/sword-gem-"+i);
        }
       
	}
	
	@Override
	public Icon getBackgroundIcon(int i){
		return backgroundIcon[i];
	}
	

	@Override
	public void registerKeyHandelers() {
		KeyBindingRegistry.registerKeyBinding(new BattlegearKeyHandeler());
		
		
		LanguageHelper.loadAllLanguages();
		for(int i = 0; i < Item.itemsList.length; i++){
			
			if(Item.itemsList[i] != null){
				//if(Item.itemsList[i] instanceof ItemShield){
				
				Item item = Item.itemsList[i];
				
				if (item instanceof IHeraldyItem){
					
					if(((IHeraldyItem) item).useDefaultRenderer()){
						MinecraftForgeClient.registerItemRenderer(i, new HeraldryItemRenderer());
					}else if(i == BattlegearConfig.heradricItem.itemID){
						MinecraftForgeClient.registerItemRenderer(i, new HeraldryItemRenderer(1.5F));
					}
					
				}
				
				
				if(i == Item.swordWood.itemID){
					MinecraftForgeClient.registerItemRenderer(i, 
							new HeradrySwordRenderer(0));
				}else if(i == Item.swordStone.itemID){
					MinecraftForgeClient.registerItemRenderer(i, 
							new HeradrySwordRenderer(1));
				}else if(i == Item.swordIron.itemID){
					MinecraftForgeClient.registerItemRenderer(i, 
							new HeradrySwordRenderer(2));
				}else if(i == Item.swordDiamond.itemID){
					MinecraftForgeClient.registerItemRenderer(i, 
							new HeradrySwordRenderer(3));
				}else if(i == Item.swordGold.itemID){
					MinecraftForgeClient.registerItemRenderer(i, 
							new HeradrySwordRenderer(4));
				}
			}
			
		}
	}
		
	
	@Override
	public void registerTickHandelers(){
		super.registerTickHandelers();
		MinecraftForge.EVENT_BUS.register(new BattlegearClientHookContainer());
		//TickRegistry.registerTickHandler(new BattlegearGUITickHandeler(), Side.CLIENT);
		TickRegistry.registerTickHandler(new BattlegearTickHandeler(), Side.CLIENT);
	}

	@Override
	public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {

		if(entityPlayer instanceof EntityClientPlayerMP){
			((EntityClientPlayerMP)entityPlayer).sendQueue.addToSendQueue(
					BattlegearPacketHandeler.generateBgAnimationPacket(animation, entityPlayer.username));
		}
		
	}
	
	
	@Override
	public void attackCreatureWithItem(EntityPlayer entityPlayer, Entity target) {
		System.out.println("Attacking creature");
		FMLClientHandler.instance().getClient().playerController.attackEntity(entityPlayer, target);
	}


	@Override
	/**
     * Finds what block or object the mouse is over at the specified partial tick time. Args: partialTickTime
     */
    public MovingObjectPosition getMouseOver(float tickPart, float maxDist)
    {
		Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc.renderViewEntity != null)
        {
            if (mc.theWorld != null)
            {
                mc.pointedEntityLiving = null;
                double d0 = (double)maxDist;
                MovingObjectPosition objectMouseOver = mc.renderViewEntity.rayTrace(d0, tickPart);
                double d1 = d0;
                Vec3 vec3 = mc.renderViewEntity.getPosition(tickPart);

                if (objectMouseOver != null)
                {
                    d1 = objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3 vec31 = mc.renderViewEntity.getLook(tickPart);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                Entity pointedEntity = null;
                float f1 = 1.0F;
                List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
                double d2 = d1;

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity = (Entity)list.get(i);

                    if (entity.canBeCollidedWith())
                    {
                        float f2 = entity.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                        if (axisalignedbb.isVecInside(vec3))
                        {
                            if (0.0D < d2 || d2 == 0.0D)
                            {
                                pointedEntity = entity;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null)
                        {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                pointedEntity = entity;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (pointedEntity != null && (d2 < d1 || objectMouseOver == null))
                {
                    objectMouseOver = new MovingObjectPosition(pointedEntity);
                }
                
                return objectMouseOver;
            }
        }
        return null;
    }
	
		
	
}
