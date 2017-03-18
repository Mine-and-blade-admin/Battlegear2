package mods.battlegear2.client;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import mods.battlegear2.Battlegear;
import mods.battlegear2.CommonProxy;
import mods.battlegear2.MobHookContainerClass;
import mods.battlegear2.api.Colorable;
import mods.battlegear2.api.DefaultMesh;
import mods.battlegear2.api.IDyable;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.client.renderer.FlagPoleTileRenderer;
import mods.battlegear2.client.renderer.LayerQuiver;
import mods.battlegear2.client.renderer.LayerSheathedItem;
import mods.battlegear2.client.renderer.ShieldModelLoader;
import mods.battlegear2.client.utils.BattlegearClientUtils;
import mods.battlegear2.heraldry.BlockFlagPole;
import mods.battlegear2.heraldry.TileEntityFlagPole;
import mods.battlegear2.items.ItemMBArrow;
import mods.battlegear2.items.arrows.AbstractMBArrow;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import mods.battlegear2.packet.SpecialActionPacket;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderTippedArrow;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ClientProxy extends CommonProxy {

    public static boolean tconstructEnabled = false;
    public static Method updateTab, addTabs;
    public static TextureAtlasSprite[] backgroundIcon;

    @Override
    public void registerHandlers() {
        super.registerHandlers();
        MinecraftForge.EVENT_BUS.register(BattlegearClientEvents.INSTANCE);
        MinecraftForge.EVENT_BUS.register(BattlegearClientTickHandeler.INSTANCE);
        BattlegearUtils.RENDER_BUS.register(new BattlegearClientUtils());
    }

    @Override
    public void sendAnimationPacket(EnumBGAnimations animation, EntityPlayer entityPlayer) {
        if (entityPlayer instanceof EntityPlayerSP) {
            ((EntityPlayerSP) entityPlayer).connection.sendPacket(
                    new BattlegearAnimationPacket(animation, entityPlayer).generatePacket());
        }
    }

    @Override
    public void startFlash(EntityPlayer player, float damage) {
    	if(player.getName().equals(Minecraft.getMinecraft().player.getName())){
            BattlegearClientTickHandeler.resetFlash();
            ItemStack offhand = player.getHeldItemOffhand();
            if(offhand.getItem() instanceof IShield)
                BattlegearClientTickHandeler.reduceBlockTime(((IShield) offhand.getItem()).getDamageDecayRate(offhand, damage));
        }
    }

    @Override
    public void registerItemRenderers() {
        Map<String, RenderPlayer> map = Minecraft.getMinecraft().getRenderManager().getSkinMap();
        for (RenderPlayer render : map.values()) {
            render.addLayer(new LayerSheathedItem(render));
            if (BattlegearConfig.hasRender("quiver")) {
                render.addLayer(new LayerQuiver(render));
            }
        }
        if (BattlegearConfig.hasRender("shield"))
            MinecraftForge.EVENT_BUS.register(new ShieldModelLoader());
        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        if (BattlegearConfig.hasRender("bow")){
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
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new Colorable(), BattlegearConfig.shield);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                return tintIndex < 2 ? ((IDyable)stack.getItem()).getColor(stack) : -1;
            }
        }, BattlegearConfig.shield[0]);
        for (Item armor : BattlegearConfig.knightArmor) {
            if (armor != null)
                modelMesher.register(armor, DefaultMesh.INVENTORY);
        }

        if (BattlegearConfig.chain != null)
            modelMesher.register(BattlegearConfig.chain, DefaultMesh.INVENTORY);
        if (BattlegearConfig.quiver != null) {
            modelMesher.register(BattlegearConfig.quiver, DefaultMesh.INVENTORY);
            ModelBakery.registerItemVariants(BattlegearConfig.quiver, new ResourceLocation(BattlegearConfig.MODID + "quiver"), new ResourceLocation(BattlegearConfig.MODID + "quiver_full"));
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new Colorable(), BattlegearConfig.quiver);
        }
        if (BattlegearConfig.heradricItem != null)
            modelMesher.register(BattlegearConfig.heradricItem, DefaultMesh.INVENTORY);
        if (BattlegearConfig.MbArrows != null) {
            final ResourceLocation[] variants = new ResourceLocation[ItemMBArrow.names.length];
            for (int i = 0; i < variants.length; i++) {
                variants[i] = new ResourceLocation(BattlegearConfig.MODID + BattlegearConfig.itemNames[9] + "." + ItemMBArrow.names[i]);
            }
            ModelBakery.registerItemVariants(BattlegearConfig.MbArrows, variants);
            modelMesher.register(BattlegearConfig.MbArrows, new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                    return new ModelResourceLocation(variants[stack.getMetadata()], "inventory");
                }
            });
            Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(AbstractMBArrow.class, new RenderArrow<AbstractMBArrow>(Minecraft.getMinecraft().getRenderManager()) {
                @Nullable
                @Override
                protected ResourceLocation getEntityTexture(AbstractMBArrow entity) {
                    return RenderTippedArrow.RES_ARROW;
                }
            });
        }
        //TODO: Heraldry renderers
        if (BattlegearConfig.banner != null) {
            Collection<BlockFlagPole.Variants> collec = BlockFlagPole.VARIANT.getAllowedValues();
            final ResourceLocation[] variants = new ResourceLocation[collec.size()];
            int i = 0;
            for (BlockFlagPole.Variants comparable : collec) {
                variants[i] = new ResourceLocation(BattlegearConfig.MODID + BattlegearConfig.itemNames[10] + "/" + BlockFlagPole.VARIANT.getName(comparable));
                i++;
            }
            ModelBakery.registerItemVariants(Item.getItemFromBlock(BattlegearConfig.banner), variants);
            modelMesher.register(Item.getItemFromBlock(BattlegearConfig.banner), new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack) {
                    return new ModelResourceLocation(variants[stack.getMetadata()], "inventory");
                }
            });
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlagPole.class, new FlagPoleTileRenderer());
        }
    }

    private void registerArrows(){
        for (ItemStack itemStack : QuiverArrowRegistry.getKnownArrows()) {
            final String location = getArrowLocation(itemStack);
            final ResourceLocation rLocation = new ResourceLocation(location);
            final ResourceLocation key = new ResourceLocation(rLocation.getResourceDomain(), "use_" + rLocation.getResourcePath());
            Items.BOW.addPropertyOverride(key, new IItemPropertyGetter() {
                @Override
                public float apply(@Nonnull ItemStack itemStack, @Nullable World worldIn, @Nullable EntityLivingBase livingBase) {
                    if(livingBase instanceof EntityPlayer){
                        EntityPlayer entityPlayer = (EntityPlayer) livingBase;
                        if(entityPlayer.getActiveItemStack() == itemStack || BattlegearConfig.arrowForceRendered) {
                            ItemStack quiver = QuiverArrowRegistry.getArrowContainer(itemStack, (EntityPlayer) livingBase);
                            if(!quiver.isEmpty()){
                                ItemStack arrowStack = ((IArrowContainer2)quiver.getItem()).getStackInSlot(quiver, ((IArrowContainer2)quiver.getItem()).getSelectedSlot(quiver));
                                if(QuiverArrowRegistry.isKnownArrow(arrowStack)){
                                    String bow = getArrowLocation(arrowStack);
                                    if(bow.equals(location))
                                        return 1;
                                }
                            }
                            return 0;
                        }

                    }else if(livingBase instanceof EntitySkeleton){
                        String bow = getArrowLocation(MobHookContainerClass.INSTANCE.getArrowForMob((EntitySkeleton) livingBase));
                        return bow.equals(location) ? 1:0;
                    }
                    return 0;
                }
            });
        }
    }

    public static String getArrowLocation(ItemStack arrowStack){
        if (BattlegearConfig.hasRender("bow")) {
            return arrowStack.getUnlocalizedName().replace("item.", "");
        }
        return "arrow";
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
        FMLProxyPacket p;
        if(itemStack.getItem() instanceof IShield){
            RayTraceResult mop = getMouseOver(4);
            if(mop != null && mop.entityHit instanceof EntityLivingBase){
                p = new SpecialActionPacket(entityPlayer, mop.entityHit).generatePacket();
                if(mop.entityHit instanceof EntityPlayerMP){
                    Battlegear.packetHandler.sendPacketToPlayer(p, (EntityPlayerMP) mop.entityHit);
                }
                Battlegear.packetHandler.sendPacketToServer(p);
            }
        }
        else{
            p = new SpecialActionPacket(entityPlayer, null).generatePacket();
            Battlegear.packetHandler.sendPacketToServer(p);
        }
    }

    @Override
    public boolean handleAttack(EntityPlayer entityPlayer) {
        if(Minecraft.getMinecraft().player == entityPlayer && super.handleAttack(entityPlayer)){
            KeyBinding use = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
            KeyBinding.setKeyBindState(use.getKeyCode(), false);
            while(use.isPressed());
            return true;
        }
        return false;
    }

    /**
     * Finds what block or object the mouse is over at the specified distance. Args: distance
     */
    @Override
    public RayTraceResult getMouseOver(double d0)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getRenderViewEntity() != null)
        {
            if (mc.world != null)
            {
                float tickPart = BattlegearClientTickHandeler.getPartialTick();
                RayTraceResult objectMouseOver = mc.getRenderViewEntity().rayTrace(d0, tickPart);
                double d1 = d0;
                Vec3d vec3 = mc.getRenderViewEntity().getPositionEyes(tickPart);

                if (objectMouseOver != null)
                {
                    d1 = objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vec3d vec31 = mc.getRenderViewEntity().getLook(tickPart);
                Vec3d vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                Vec3d vec33 = null;
                Entity pointedEntity = null;
                List<Entity> list = mc.world.getEntitiesInAABBexcluding(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(1.0D, 1.0D, 1.0D), Predicates.and(new Predicate<Entity>() {
                    @Override
                    public boolean apply(@Nullable Entity input) {
                        return input!=null && input.canBeCollidedWith();
                    }
                }, EntitySelectors.NOT_SPECTATING));
                double d2 = d1;

                for (Entity entity : list)
                {
                    double f2 = entity.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expandXyz(f2);
                    RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

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
                            if (entity.getLowestRidingEntity() == mc.getRenderViewEntity().getLowestRidingEntity() && !mc.getRenderViewEntity().canRiderInteract()) {
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

                if (pointedEntity != null && (d2 < d1 || objectMouseOver == null))
                {
                    objectMouseOver = new RayTraceResult(pointedEntity, vec33);
                }

                return objectMouseOver;
            }
        }
        return null;
    }
    
    @Override
    public void tryUseTConstruct() {
    	try {
            Object tcManager = Class.forName("slimeknights.tconstruct.TConstruct").getField("pulseManager").get(null);
            if((Boolean)tcManager.getClass().getMethod("isPulseLoaded", String.class).invoke(tcManager, "Tinkers' Armory")) {//TODO Check TConstruct for their inv tabs
                Class<?> tabRegistry = Class.forName("tconstruct.client.tabs.TabRegistry");
                Class abstractTab = Class.forName("tconstruct.client.tabs.AbstractTab");
                Method registerTab = tabRegistry.getMethod("registerTab", abstractTab);
                updateTab = tabRegistry.getMethod("updateTabValues", int.class, int.class, Class.class);
                addTabs = tabRegistry.getMethod("addTabsToList", List.class);
                registerTab.invoke(null, Class.forName("mods.battlegear2.client.gui.controls.EquipGearTab").newInstance());
                if (Battlegear.debug) {
                    registerTab.invoke(null, Class.forName("mods.battlegear2.client.gui.controls.SigilTab").newInstance());
                }
                tconstructEnabled = true;
            }
		} catch (Throwable ignored) {
		}
	}

    @Override
    public EntityPlayer getClientPlayer(){
        return Minecraft.getMinecraft().player;
    }

    @Override
    public IThreadListener getThreadListener() {
        return Minecraft.getMinecraft();
    }
}
