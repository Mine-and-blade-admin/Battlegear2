package mods.battlegear2.client;


import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.Battlegear;
import mods.battlegear2.BattlegearTickHandeler;
import mods.battlegear2.CommonProxy;
import mods.battlegear2.api.IShield;
import mods.battlegear2.client.model.QuiverModel;
import mods.battlegear2.client.renderer.BowRenderer;
import mods.battlegear2.client.renderer.QuiverItremRenderer;
import mods.battlegear2.client.renderer.ShieldRenderer;
import mods.battlegear2.client.renderer.SpearRenderer;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.items.ItemShield;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.packet.SpecialActionPacket;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.EnumBGAnimations;
import mods.battlegear2.utils.Release;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.*;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class ClientProxy extends CommonProxy {

    public static Icon[] backgroundIcon;
    public static Icon[] bowIcons;


    @Override
    public String getVersionCheckerMessage() {

            ModContainer mc = FMLCommonHandler.instance().findContainerFor(Battlegear.INSTANCE);
            if(Battlegear.latestRelease == null){
                return String.format("%s%s: %s%s%s - %s",
                        EnumChatFormatting.DARK_RED,  mc.getName(),
                        EnumChatFormatting.DARK_RED, "Version Check Failed",
                        EnumChatFormatting.WHITE, "Could not contact server or invalid response");
            }else{


                String[] version_split = mc.getVersion().split("\\.");
                int[] version = new int[version_split.length];
                try{
                    for(int i = 0; i < version.length; i++){
                        version[i] = Integer.parseInt(version_split[i]);
                    }
                    Release thisVersion = new Release(Release.EnumReleaseType.Normal, null, version);

                    if(thisVersion.compareTo(Battlegear.latestRelease) < 0){
                        StringBuffer newVersionString = new StringBuffer();
                        for(int i = 0; i < Battlegear.latestRelease.version.length; i++){
                            newVersionString.append(Battlegear.latestRelease.version[i]);
                            newVersionString.append(".");
                        }
                        newVersionString.deleteCharAt(newVersionString.lastIndexOf("."));

                        if(Battlegear.latestRelease.url != null){
                            return String.format("%s%s: %s%s (%s)",
                                    EnumChatFormatting.DARK_BLUE,  mc.getName(),
                                    EnumChatFormatting.DARK_BLUE, "New version found", newVersionString);
                        }else{
                            return String.format("%s%s: %s%s (%s)%s - %s",
                                    EnumChatFormatting.DARK_BLUE,  mc.getName(),
                                    EnumChatFormatting.DARK_BLUE, "New version found", newVersionString,
                                    EnumChatFormatting.WHITE, "type '\\mb latest' to open the url");
                        }
                    }else{


                        return String.format("%s%s: %s%s%s - %s %s",
                                EnumChatFormatting.DARK_GREEN,  mc.getName(),
                                EnumChatFormatting.DARK_GREEN, "Version Up to Date",
                                EnumChatFormatting.WHITE,
                                "You are running the latest version of",mc.getName());

                    }

                }catch (NumberFormatException e){

                    return String.format("%s%s: %s%s%s - %s",
                            EnumChatFormatting.DARK_RED,  mc.getName(),
                            EnumChatFormatting.DARK_RED, "Version Check Failed",
                            EnumChatFormatting.WHITE, "Could not determine running version");
                }

            }
    }

    @Override
    public void registerKeyHandelers() {
        KeyBindingRegistry.registerKeyBinding(new BattlegearKeyHandeler());
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
                    BattlegearAnimationPacket.generatePacket(animation, entityPlayer.username));
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

        Minecraft.getMinecraft().sndManager.playSound("battlegear2:shield", (float)player.posX, (float)player.posY, (float)player.posZ, 1, 1);

        if(player.username.equals(Minecraft.getMinecraft().thePlayer.username)){
            BattlegearClientTickHandeler.flashTimer = 30;
            ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();

            if(offhand != null && offhand.getItem() instanceof IShield)
                BattlegearClientTickHandeler.blockBar -= ((IShield) offhand.getItem()).getDamageDecayRate(offhand, damage);
        }
    }

    @Override
    public void registerItemRenderers() {

        SpearRenderer spearRenderer =  new SpearRenderer();
        for(Item spear: BattlegearConfig.spear){
            MinecraftForgeClient.registerItemRenderer(spear.itemID, spearRenderer);
        }

        ShieldRenderer shieldRenderer = new ShieldRenderer();
        for(Item shield : BattlegearConfig.shield){
            MinecraftForgeClient.registerItemRenderer(shield.itemID, shieldRenderer);
        }

        MinecraftForgeClient.registerItemRenderer(Item.bow.itemID, new BowRenderer());

        MinecraftForgeClient.registerItemRenderer(BattlegearConfig.quiver.itemID, new QuiverItremRenderer());


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

            Packet250CustomPayload p;
            if(mop != null && mop.entityHit != null && mop.entityHit instanceof EntityLivingBase){
                p = SpecialActionPacket.generatePacket(entityPlayer, mainhand, offhand, mop.entityHit);
                PacketDispatcher.sendPacketToServer(p);

                if(mop.entityHit instanceof EntityPlayer){
                    PacketDispatcher.sendPacketToPlayer(p, (Player)mop.entityHit);
                }

            }else{
                p = SpecialActionPacket.generatePacket(entityPlayer, mainhand, offhand, null);
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
}
