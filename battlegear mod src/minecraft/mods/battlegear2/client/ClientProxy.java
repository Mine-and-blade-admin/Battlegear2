package mods.battlegear2.client;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import mods.battlegear2.Battlegear;
import mods.battlegear2.BattlegearTickHandeler;
import mods.battlegear2.CommonProxy;
import mods.battlegear2.api.IShield;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.client.gui.BattlegearGuiKeyHandler;
import mods.battlegear2.client.renderer.*;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.packet.SpecialActionPacket;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.util.*;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

    public static Icon[] backgroundIcon;
    public static Icon[] bowIcons;

    @Override
    public void registerKeyHandelers() {
        KeyBindingRegistry.registerKeyBinding(new BattlegearKeyHandeler());
        if(BattlegearConfig.enableGUIKeys){
        	KeyBindingRegistry.registerKeyBinding(new BattlegearGuiKeyHandler());
        }
    }

    @Override
    public void registerTickHandelers() {
        super.registerTickHandelers();
        
        MinecraftForge.EVENT_BUS.register(new BattlegearClientEvents());
        TickRegistry.registerTickHandler(new BattlegearTickHandeler(), Side.CLIENT);
        TickRegistry.registerTickHandler(new BattlegearClientTickHandeler(), Side.CLIENT);
    }

    @Override
    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {
        if (entityPlayer instanceof EntityClientPlayerMP) {
            ((EntityClientPlayerMP) entityPlayer).sendQueue.addToSendQueue(
                    new BattlegearAnimationPacket(animation, entityPlayer.username).generatePacket());
        }
    }

    @Override
    public void sendPlaceBlockPacket(EntityPlayer entityPlayer, int x, int y, int z, int face, Vec3 par8Vec3) {
        if (entityPlayer instanceof EntityClientPlayerMP) {
            ((EntityClientPlayerMP) entityPlayer).sendQueue.addToSendQueue(
                    new Packet15Place(x, y, z, face,
                            ((InventoryPlayerBattle)entityPlayer.inventory).getCurrentOffhandWeapon(),
                            (float)par8Vec3.xCoord - (float)x,
                            (float)par8Vec3.yCoord - (float)y,
                            (float)par8Vec3.zCoord - (float)z)
            );
        }
    }

    @Override
    public void startFlash(EntityPlayer player, float damage) {
    	if(player.username.equals(Minecraft.getMinecraft().thePlayer.username)){
            BattlegearClientTickHandeler.flashTimer = 30;
            ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();

            if(offhand != null && offhand.getItem() instanceof IShield)
                BattlegearClientTickHandeler.blockBar -= ((IShield) offhand.getItem()).getDamageDecayRate(offhand, damage);
        }
    }

    @Override
    public void registerItemRenderers() {
    	if(Arrays.binarySearch(BattlegearConfig.disabledRenderers, "spear")  < 0){
	        SpearRenderer spearRenderer =  new SpearRenderer();
	        for(Item spear: BattlegearConfig.spear){
	        	if(spear!=null)
	        		MinecraftForgeClient.registerItemRenderer(spear.itemID, spearRenderer);
	        }
    	}

        if(Arrays.binarySearch(BattlegearConfig.disabledRenderers, "shield")  < 0){
        	ShieldRenderer shieldRenderer = new ShieldRenderer();
	        for(Item shield : BattlegearConfig.shield){
	        	if(shield!=null)
	        		MinecraftForgeClient.registerItemRenderer(shield.itemID, shieldRenderer);
	        }
        }

        if(Arrays.binarySearch(BattlegearConfig.disabledRenderers, "bow")  < 0)
        	MinecraftForgeClient.registerItemRenderer(Item.bow.itemID, new BowRenderer());
        if(BattlegearConfig.quiver!=null && Arrays.binarySearch(BattlegearConfig.disabledRenderers, "quiver")  < 0)
        	MinecraftForgeClient.registerItemRenderer(BattlegearConfig.quiver.itemID, new QuiverItremRenderer());

        if(Battlegear.debug){
            MinecraftForgeClient.registerItemRenderer(BattlegearConfig.heradricItem.itemID, new HeraldryCrestItemRenderer());
            
            for(int i = 0; i < Item.itemsList.length; i++){
            	if(Item.itemsList[i] instanceof IHeraldryItem &&
            			((IHeraldryItem)Item.itemsList[i]).useDefaultRenderer()){
            		MinecraftForgeClient.registerItemRenderer(i, new HeraldryItemRenderer());
            	}
            }
        }
    }

    @Override
    public Icon getSlotIcon(int index){
        if(backgroundIcon != null){
            return backgroundIcon[index];
        }else{
            return null;
        }
    }

    @Override
    public void doSpecialAction(EntityPlayer entityPlayer) {

        if(entityPlayer.username.equals(Minecraft.getMinecraft().thePlayer.username)){
            ItemStack offhand = ((InventoryPlayerBattle)entityPlayer.inventory) .getCurrentOffhandWeapon();
            ItemStack mainhand = entityPlayer.getCurrentEquippedItem();

            MovingObjectPosition mop = null;

            if(offhand != null && offhand.getItem() instanceof IShield){
                mop = getMouseOver(1, 4);
            }

            Packet p;
            if(mop != null && mop.entityHit != null && mop.entityHit instanceof EntityLivingBase){
                p = new SpecialActionPacket(entityPlayer, mainhand, offhand, mop.entityHit).generatePacket();
                PacketDispatcher.sendPacketToServer(p);

                if(mop.entityHit instanceof EntityPlayer){
                    PacketDispatcher.sendPacketToPlayer(p, (Player)mop.entityHit);
                }

            }else{
                p = new SpecialActionPacket(entityPlayer, mainhand, offhand, null).generatePacket();
            }
            PacketDispatcher.sendPacketToServer(p);
        }

    }

    /**
     * Finds what block or object the mouse is over at the specified partial tick time. Args: partialTickTime
     */
    @Override
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
    
    @Override
    public void tryUseTConstruct() {
    	try {
            Class tabRegistry = Class.forName("tconstruct.client.tabs.TabRegistry");
            Class abstractTab = Class.forName("tconstruct.client.tabs.AbstractTab");
            Method registerTab = tabRegistry.getMethod("registerTab", abstractTab);
            registerTab.invoke(null, Class.forName("mods.battlegear2.client.gui.controls.EquipGearTab").newInstance());
            if(Battlegear.debug){
                registerTab.invoke(null, Class.forName("mods.battlegear2.client.gui.controls.SigilTab").newInstance());
            }
		} catch (Exception e) {
			return;
		}
    	tconstructEnabled = true;
	}
}
