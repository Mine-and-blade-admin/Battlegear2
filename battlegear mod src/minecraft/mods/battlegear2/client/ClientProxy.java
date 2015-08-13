package mods.battlegear2.client;

import mods.battlegear2.Battlegear;
import mods.battlegear2.CommonProxy;
import mods.battlegear2.api.DefaultMesh;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.quiver.QuiverMesh;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.client.gui.BattlegearGuiKeyHandler;
import mods.battlegear2.client.renderer.ShieldModelLoader;
import mods.battlegear2.client.utils.BattlegearClientUtils;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import mods.battlegear2.heraldry.BlockFlagPole;
import mods.battlegear2.items.ItemMBArrow;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.packet.SpecialActionPacket;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ClientProxy extends CommonProxy {

    public static boolean tconstructEnabled = false;
    public static Method updateTab, addTabs;
    private static Object dynLightPlayerMod;
    private static Method dynLightFromItemStack, refresh;
    public static ItemStack heldCache;
    public static TextureAtlasSprite[] backgroundIcon;

    @Override
    public void registerKeyHandelers() {
        if(BattlegearConfig.enableGUIKeys){
            FMLCommonHandler.instance().bus().register(BattlegearGuiKeyHandler.INSTANCE);
        }
    }

    @Override
    public void registerTickHandelers() {
        super.registerTickHandelers();
        MinecraftForge.EVENT_BUS.register(BattlegearClientEvents.INSTANCE);
        FMLCommonHandler.instance().bus().register(BattlegearClientTickHandeler.INSTANCE);
        BattlegearUtils.RENDER_BUS.register(new BattlegearClientUtils());
    }

    @Override
    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {
        if (entityPlayer instanceof EntityClientPlayerMP) {
            ((EntityClientPlayerMP) entityPlayer).sendQueue.addToSendQueue(
                    new BattlegearAnimationPacket(animation, entityPlayer).generatePacket());
        }
    }

    @Override
    public void startFlash(EntityPlayer player, float damage) {
    	if(player.getCommandSenderName().equals(Minecraft.getMinecraft().thePlayer.getCommandSenderName())){
            BattlegearClientTickHandeler.resetFlash();
            ItemStack offhand = ((InventoryPlayerBattle)player.inventory).getCurrentOffhandWeapon();

            if(offhand != null && offhand.getItem() instanceof IShield)
                BattlegearClientTickHandeler.reduceBlockTime(((IShield) offhand.getItem()).getDamageDecayRate(offhand, damage));
        }
    }

    @Override
    public void registerItemRenderers() {
        if (Arrays.binarySearch(BattlegearConfig.disabledRenderers, "shield") < 0)
            MinecraftForge.EVENT_BUS.register(new ShieldModelLoader());
        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        if (Arrays.binarySearch(BattlegearConfig.disabledRenderers, "bow") < 0){
            registerArrows();
        }
        for (Item item : BattlegearConfig.dagger) {
            if (item != null)
                modelMesher.register(item, DefaultMesh.INVENTORY);
        }
        for (Item item : BattlegearConfig.warAxe) {
            if (item != null)
                modelMesher.register(item, DefaultMesh.INVENTORY);
        }
        for (Item item : BattlegearConfig.mace) {
            if (item != null)
                modelMesher.register(item, DefaultMesh.INVENTORY);
        }
        for (Item item : BattlegearConfig.spear) {
            if (item != null)
                modelMesher.register(item, DefaultMesh.INVENTORY);
        }
        for (Item shield : BattlegearConfig.shield) {
            if (shield != null)
                modelMesher.register(shield, DefaultMesh.INVENTORY);
        }
        for (Item armor : BattlegearConfig.knightArmor) {
            if (armor != null)
                modelMesher.register(armor, DefaultMesh.INVENTORY);
        }

        if (BattlegearConfig.chain != null)
            modelMesher.register(BattlegearConfig.chain, DefaultMesh.INVENTORY);
        if (BattlegearConfig.quiver != null) {
            modelMesher.register(BattlegearConfig.quiver, new QuiverMesh("_full", DefaultMesh.INVENTORY));
            ModelLoader.addVariantName(BattlegearConfig.quiver, BattlegearConfig.MODID + "quiver", BattlegearConfig.MODID + "quiver_full");
        }
        if (BattlegearConfig.heradricItem != null)
            modelMesher.register(BattlegearConfig.heradricItem, DefaultMesh.INVENTORY);
        if (BattlegearConfig.MbArrows != null) {
            String[] variants = new String[ItemMBArrow.names.length];
            for (int i = 0; i < variants.length; i++) {
                variants[i] = BattlegearConfig.MODID + BattlegearConfig.itemNames[9] + "." + ItemMBArrow.names[i];
            }
            ModelLoader.addVariantName(BattlegearConfig.MbArrows, variants);
            modelMesher.register(BattlegearConfig.MbArrows, DefaultMesh.INVENTORY);
        }
        //TODO: Flagpole, Heraldry renderers
        if (BattlegearConfig.banner != null) {
            Collection<Comparable> collec = BlockFlagPole.VARIANT.getAllowedValues();
            final String[] variants = new String[collec.size()];
            int i = 0;
            for (Comparable comparable : collec) {
                variants[i] = BattlegearConfig.MODID + BattlegearConfig.itemNames[10] + "/" + BlockFlagPole.VARIANT.getName(comparable);
                i++;
            }
            ModelLoader.addVariantName(Item.getItemFromBlock(BattlegearConfig.banner), variants);
            modelMesher.register(Item.getItemFromBlock(BattlegearConfig.banner), new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                    return new ModelResourceLocation(variants[stack.getMetadata()], "inventory");
                }
            });
            //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlagPole.class, new FlagPoleTileRenderer());
        }
    }

    private void registerArrows(){
        ModelLoader.addVariantName(Items.bow, "bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2");
        for (ItemStack itemStack : QuiverArrowRegistry.getKnownArrows()) {
            String location = BattlegearRenderHelper.getArrowLocation(itemStack);
            if(BattlegearConfig.arrowForceRendered){
                ModelLoader.addVariantName(Items.bow, location);
            }
            location += "_pulling_";
            ModelLoader.addVariantName(Items.bow, location + "0", location + "1", location + "2");
        }
    }

    public TextureAtlasSprite getSlotIcon(int index) {
        if(backgroundIcon != null){
            return backgroundIcon[index];
        }else{
            return null;
        }
    }

    @Override
    public void doSpecialAction(EntityPlayer entityPlayer, ItemStack itemStack) {
        MovingObjectPosition mop = null;
        if(itemStack != null && itemStack.getItem() instanceof IShield){
            mop = getMouseOver(4);
        }

        FMLProxyPacket p;
        if(mop != null && mop.entityHit instanceof EntityLivingBase){
            p = new SpecialActionPacket(entityPlayer, mop.entityHit).generatePacket();
            if(mop.entityHit instanceof EntityPlayerMP){
                Battlegear.packetHandler.sendPacketToPlayer(p, (EntityPlayerMP) mop.entityHit);
            }
        }else{
            p = new SpecialActionPacket(entityPlayer, null).generatePacket();
        }
        Battlegear.packetHandler.sendPacketToServer(p);
    }

    /**
     * Finds what block or object the mouse is over at the specified partial tick time. Args: partialTickTime
     */
    @Override
    public MovingObjectPosition getMouseOver(double d0)
    {
        Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc.getRenderViewEntity() != null)
        {
            if (mc.theWorld != null)
            {
                float tickPart = BattlegearClientTickHandeler.getPartialTick();
                MovingObjectPosition objectMouseOver = mc.getRenderViewEntity().rayTrace(d0, tickPart);
                double d1 = d0;
                Vec3 vec3 = mc.getRenderViewEntity().getPositionEyes(tickPart);

                if (objectMouseOver != null)
                {
                    d1 = objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3 vec31 = mc.getRenderViewEntity().getLook(tickPart);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                Vec3 vec33 = null;
                Entity pointedEntity = null;
                List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(1.0D, 1.0D, 1.0D));
                double d2 = d1;

                for (Object o : list)
                {
                    Entity entity = (Entity) o;

                    if (entity.canBeCollidedWith())
                    {
                        double f2 = entity.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(f2, f2, f2);
                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                        if (axisalignedbb.isVecInside(vec3))
                        {
                            if (d2 >= 0.0D)
                            {
                                pointedEntity = entity;
                                vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null)
                        {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                if (entity == entity.ridingEntity && !entity.canRiderInteract()) {
                                    if (d2 == 0.0D) {
                                        pointedEntity = entity;
                                        vec33 = movingobjectposition.hitVec;
                                    }
                                } else {
                                    pointedEntity = entity;
                                    vec33 = movingobjectposition.hitVec;
                                    d2 = d3;
                                }
                            }
                        }
                    }
                }

                if (pointedEntity != null && (d2 < d1 || objectMouseOver == null))
                {
                    objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
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
    public void tryUseDynamicLight(EntityPlayer player, ItemStack stack){
        if(player==null && stack==null){
            dynLightPlayerMod = Loader.instance().getIndexedModList().get("DynamicLights_thePlayer").getMod();
            try {
                if(dynLightPlayerMod!=null) {
                    dynLightFromItemStack = dynLightPlayerMod.getClass().getDeclaredMethod("getLightFromItemStack", ItemStack.class);
                    dynLightFromItemStack.setAccessible(true);
                    refresh = Class.forName("mods.battlegear2.client.utils.DualHeldLight").getMethod("refresh", EntityPlayer.class, int.class, int.class);
                }
            }catch (Exception e){
                return;
            }
        }
        if(dynLightFromItemStack!=null && refresh!=null){
            if(!ItemStack.areItemStacksEqual(stack, heldCache)) {
                try {
                    int lightNew = Integer.class.cast(dynLightFromItemStack.invoke(dynLightPlayerMod, stack));
                    int lightOld = Integer.class.cast(dynLightFromItemStack.invoke(dynLightPlayerMod, heldCache));
                    if (lightNew != lightOld) {
                        refresh.invoke(null, player, lightNew, lightOld);
                    }
                }catch (Exception e){
                    return;
                }
                heldCache = stack;
            }
        }
    }

    @Override
    public EntityPlayer getClientPlayer(){
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public void scheduleTask(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }
}
