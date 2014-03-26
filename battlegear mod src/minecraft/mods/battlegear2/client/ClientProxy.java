package mods.battlegear2.client;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.registry.GameData;
import mods.battlegear2.Battlegear;
import mods.battlegear2.CommonProxy;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.client.gui.BattlegearGuiKeyHandler;
import mods.battlegear2.client.renderer.*;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.heraldry.TileEntityFlagPole;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.packet.SpecialActionPacket;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy {

    public static boolean tconstructEnabled = false;
    public static Method updateTab, addTabs;
    public static IIcon[] backgroundIcon;
    public static IIcon[] bowIcons;

    @Override
    public void registerKeyHandelers() {
        if(BattlegearConfig.enableGUIKeys){
            FMLCommonHandler.instance().bus().register(new BattlegearGuiKeyHandler());
        }
    }

    @Override
    public void registerTickHandelers() {
        super.registerTickHandelers();
        MinecraftForge.EVENT_BUS.register(new BattlegearClientEvents());
        FMLCommonHandler.instance().bus().register(new BattlegearClientTickHandeler());
    }

    @Override
    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {
        if (entityPlayer instanceof EntityClientPlayerMP) {
            ((EntityClientPlayerMP) entityPlayer).sendQueue.addToSendQueue(
                    new BattlegearAnimationPacket(animation, entityPlayer).generatePacket());
        }
    }

    @Override
    public void sendPlaceBlockPacket(EntityPlayer entityPlayer, int x, int y, int z, int face, Vec3 par8Vec3) {
        if (entityPlayer instanceof EntityClientPlayerMP) {
            ((EntityClientPlayerMP) entityPlayer).sendQueue.addToSendQueue(
                    new C08PacketPlayerBlockPlacement(x, y, z, face,
                            ((InventoryPlayerBattle)entityPlayer.inventory).getCurrentOffhandWeapon(),
                            (float)par8Vec3.xCoord - (float)x,
                            (float)par8Vec3.yCoord - (float)y,
                            (float)par8Vec3.zCoord - (float)z)
            );
        }
    }

    @Override
    public void startFlash(EntityPlayer player, float damage) {
    	if(player.getCommandSenderName().equals(Minecraft.getMinecraft().thePlayer.getCommandSenderName())){
            BattlegearClientTickHandeler.resetFlash();
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
	        		MinecraftForgeClient.registerItemRenderer(spear, spearRenderer);
	        }
    	}

        if(Arrays.binarySearch(BattlegearConfig.disabledRenderers, "shield")  < 0){
        	ShieldRenderer shieldRenderer = new ShieldRenderer();
	        for(Item shield : BattlegearConfig.shield){
	        	if(shield!=null)
	        		MinecraftForgeClient.registerItemRenderer(shield, shieldRenderer);
	        }
        }

        if(Arrays.binarySearch(BattlegearConfig.disabledRenderers, "bow")  < 0)
        	MinecraftForgeClient.registerItemRenderer(Items.bow, new BowRenderer());
        if(BattlegearConfig.quiver!=null && Arrays.binarySearch(BattlegearConfig.disabledRenderers, "quiver")  < 0)
        	MinecraftForgeClient.registerItemRenderer(BattlegearConfig.quiver, new QuiverItremRenderer());
        if(BattlegearConfig.banner!=null && Arrays.binarySearch(BattlegearConfig.disabledRenderers, "flagpole")  < 0){
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BattlegearConfig.banner), new FlagPoleItemRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlagPole.class, new FlagPoleTileRenderer());
        }
        if(Battlegear.debug){
            Item it = null;
            for(Iterator itr = GameData.itemRegistry.iterator(); itr.hasNext(); it = (Item) itr.next()){
            	if(it instanceof IHeraldryItem && ((IHeraldryItem)it).useDefaultRenderer()){
            		MinecraftForgeClient.registerItemRenderer(it, new HeraldryItemRenderer());
            	}
            }
            MinecraftForgeClient.registerItemRenderer(BattlegearConfig.heradricItem, new HeraldryCrestItemRenderer());
        }
    }

    @Override
    public IIcon getSlotIcon(int index){
        if(backgroundIcon != null){
            return backgroundIcon[index];
        }else{
            return null;
        }
    }

    @Override
    public void doSpecialAction(EntityPlayer entityPlayer) {

        if(entityPlayer.getCommandSenderName().equals(Minecraft.getMinecraft().thePlayer.getCommandSenderName())){
            ItemStack offhand = ((InventoryPlayerBattle)entityPlayer.inventory).getCurrentOffhandWeapon();

            MovingObjectPosition mop = null;

            if(offhand != null && offhand.getItem() instanceof IShield){
                mop = getMouseOver(1, 4);
            }

            FMLProxyPacket p;
            if(mop != null && mop.entityHit != null && mop.entityHit instanceof EntityLivingBase){
                p = new SpecialActionPacket(entityPlayer, mop.entityHit).generatePacket();
                Battlegear.packetHandler.sendPacketToServer(p);

                if(mop.entityHit instanceof EntityPlayerMP){
                    Battlegear.packetHandler.sendPacketToPlayer(p, (EntityPlayerMP) mop.entityHit);
                }

            }else{
                p = new SpecialActionPacket(entityPlayer, null).generatePacket();
            }
            Battlegear.packetHandler.sendPacketToServer(p);
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
                mc.pointedEntity = null;
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
            updateTab = tabRegistry.getMethod("updateTabValues", int.class, int.class, Class.class);
            addTabs = tabRegistry.getMethod("addTabsToList", List.class);
            registerTab.invoke(null, Class.forName("mods.battlegear2.client.gui.controls.EquipGearTab").newInstance());
            if(Battlegear.debug){
                registerTab.invoke(null, Class.forName("mods.battlegear2.client.gui.controls.SigilTab").newInstance());
            }
		} catch (Exception e) {
			return;
		}
    	tconstructEnabled = true;
	}

    @Override
    public EntityPlayer getClientPlayer(){
        return Minecraft.getMinecraft().thePlayer;
    }
}
