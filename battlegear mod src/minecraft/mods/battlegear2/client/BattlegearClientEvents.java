package mods.battlegear2.client;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.RenderItemBarEvent;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.weapons.Attributes;
import mods.battlegear2.api.weapons.IBackStabbable;
import mods.battlegear2.client.gui.BattlegearInGameGUI;
import mods.battlegear2.client.gui.controls.GuiBGInventoryButton;
import mods.battlegear2.client.gui.controls.GuiPlaceableButton;
import mods.battlegear2.client.gui.controls.GuiSigilButton;
import mods.battlegear2.client.model.QuiverModel;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.packet.PickBlockPacket;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BattlegearClientEvents implements IResourceManagerReloadListener {

	private final BattlegearInGameGUI inGameGUI;
    public final QuiverModel quiverModel;
    public final ResourceLocation quiverDetails;
    public final ResourceLocation quiverBase;
    //public static final ResourceLocation patterns = new ResourceLocation("battlegear2", "textures/heraldry/Patterns-small.png");
    //public static int storageIndex;

    private static final int MAIN_INV = InventoryPlayer.getHotbarSize();
	public static final GuiPlaceableButton[] tabsList = { new GuiBGInventoryButton(0), new GuiSigilButton(1)};
    public static final BattlegearClientEvents INSTANCE = new BattlegearClientEvents();
    private String[] attributeNames;

    private BattlegearClientEvents(){
        inGameGUI = new BattlegearInGameGUI();
        quiverModel = new QuiverModel();
        quiverDetails = new ResourceLocation("battlegear2", "textures/armours/quiver/QuiverDetails.png");
        quiverBase = new ResourceLocation("battlegear2", "textures/armours/quiver/QuiverBase.png");
        ((IReloadableResourceManager) FMLClientHandler.instance().getClient().getResourceManager()).registerReloadListener(this);
    }

    /**
     * Offset battle slots rendering according to config values
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void postRenderBar(RenderItemBarEvent.BattleSlots event) {
        if(!event.isMainHand){
            event.xOffset += BattlegearConfig.battleBarOffset[0];
            event.yOffset += BattlegearConfig.battleBarOffset[1];
        }else{
            event.xOffset += BattlegearConfig.battleBarOffset[2];
            event.yOffset += BattlegearConfig.battleBarOffset[3];
        }
    }

    /**
     * Offset quiver slots rendering according to config values
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void postRenderQuiver(RenderItemBarEvent.QuiverSlots event) {
        event.xOffset += BattlegearConfig.quiverBarOffset[0];
        event.yOffset += BattlegearConfig.quiverBarOffset[1];
    }

    /**
     * Offset shield stamina rendering according to config values
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void postRenderShield(RenderItemBarEvent.ShieldBar event) {
        event.xOffset += BattlegearConfig.shieldBarOffset[0];
        event.yOffset += BattlegearConfig.shieldBarOffset[1];
    }

    /**
     * Render all the Battlegear HUD elements
     */
	@SubscribeEvent(receiveCanceled = true)
	public void postRenderOverlay(RenderGameOverlayEvent.Post event) {
		if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR && (BattlegearConfig.forceHUD || !event.isCanceled())) {
            inGameGUI.renderGameOverlay(event.partialTicks);
        }
	}

    /**
     * Bend the models when the item in left hand is used
     * And stop the right hand inappropriate bending
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderPlayerLeftItemUsage(RenderLivingEvent.Pre event){
        if(event.entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) event.entity;
            if(event.renderer instanceof RenderPlayer && entityPlayer.inventory instanceof InventoryPlayerBattle) {
                ItemStack offhand = ((InventoryPlayerBattle) entityPlayer.inventory).getCurrentOffhandWeapon();
                if (offhand != null) {
                    ModelPlayer renderer = ((RenderPlayer) event.renderer).getPlayerModel();
                    renderer.heldItemLeft = 1;
                    if (entityPlayer.getItemInUseCount() > 0 && entityPlayer.getItemInUse() == offhand) {
                        EnumAction enumaction = offhand.getItemUseAction();
                        if (enumaction == EnumAction.BLOCK) {
                            renderer.heldItemLeft = 3;
                        } else if (enumaction == EnumAction.BOW) {
                            renderer.aimedBow = true;
                        }
                        ItemStack mainhand = entityPlayer.inventory.getCurrentItem();
                        renderer.heldItemRight = mainhand != null ? 1 : 0;
                    } else if (((IBattlePlayer) entityPlayer).isBlockingWithShield()) {
                        renderer.heldItemLeft = 3;
                    }
                }
            }
        }
    }

    /**
     * Reset models to default values
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void resetPlayerLeftHand(RenderPlayerEvent.Post event){
        event.renderer.getPlayerModel().heldItemLeft = 0;
    }

    private static final int SKELETON_ARROW = 5;
    /**
     * Render quiver on skeletons if possible
     */
	@SubscribeEvent
	public void renderLiving(RenderLivingEvent.Post event) {

		if (BattlegearConfig.enableSkeletonQuiver && event.entity instanceof EntitySkeleton && event.renderer instanceof RenderSkeleton) {

			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_CULL_FACE);

			GL11.glColor3f(1, 1, 1);
			Minecraft.getMinecraft().getTextureManager().bindTexture(quiverDetails);

            GL11.glTranslatef((float) event.x, (float) event.y, (float) event.z);

			GL11.glScalef(1, -1, 1);

			float f2 = interpolateRotation(event.entity.prevRenderYawOffset, event.entity.renderYawOffset, 0);

			GL11.glRotatef(180.0F - f2, 0.0F, 1.0F, 0.0F);

			if (event.entity.deathTime > 0) {
				float f3 = ((float) event.entity.deathTime
						+ BattlegearClientTickHandeler.getPartialTick() - 1.0F) / 20.0F * 1.6F;
				f3 = MathHelper.sqrt_float(f3);

				if (f3 > 1.0F) {
					f3 = 1.0F;
				}

				GL11.glRotatef(-f3 * 90, 0.0F, 0.0F, 1.0F);
			}

			GL11.glTranslatef(0, -1.5F, 0);

			GL11.glRotatef(event.entity.rotationPitch, 0, 1, 0);

            if(event.entity.getEquipmentInSlot(3)!=null){//chest armor
                GL11.glTranslatef(0, 0, BattlegearRenderHelper.RENDER_UNIT);
            }
            ((ModelBiped)event.renderer.mainModel).bipedBody.postRender(BattlegearRenderHelper.RENDER_UNIT);
			GL11.glScalef(1.05F, 1.05F, 1.05F);
			quiverModel.render(SKELETON_ARROW, BattlegearRenderHelper.RENDER_UNIT);

			Minecraft.getMinecraft().getTextureManager().bindTexture(quiverBase);
			GL11.glColor3f(0.10F, 0.10F, 0.10F);
			quiverModel.render(0, BattlegearRenderHelper.RENDER_UNIT);
			GL11.glColor3f(1, 1, 1);

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glPopMatrix();
		}
	}

    /**
     * Counter the bow use fov jerkyness with the draw enchantment
     */
    @SubscribeEvent
    public void onBowFOV(FOVUpdateEvent event){
        ItemStack stack = event.entity.getItemInUse();
        if (EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowCharge, stack) > 0) {
            int i = event.entity.getItemInUseDuration();
            float f1 = (float) i / 20.0F;
            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 *= f1;
            }
            event.newfov /= 1.0F - f1 * 0.15F;
        }
    }

    /**
     * Fixes pick block
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void replacePickBlock(MouseEvent event){
        if(event.buttonstate){
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.thePlayer != null) {
                if(event.button-100 == mc.gameSettings.keyBindPickBlock.getKeyCode()){
                    event.setCanceled(true);
                    if (!((IBattlePlayer) mc.thePlayer).isBattlemode()) {
                        boolean isCreative = mc.thePlayer.capabilities.isCreativeMode;
                        ItemStack stack = getItemFromPointedAt(mc.objectMouseOver, mc.thePlayer);
                        if (stack != null) {
                            int k = -1;
                            ItemStack temp;
                            for (int slot = 0; slot < MAIN_INV; slot++) {
                                temp = mc.thePlayer.inventory.getStackInSlot(slot);
                                if (temp != null && stack.isItemEqual(temp) && ItemStack.areItemStackTagsEqual(stack, temp)) {
                                    k = slot;
                                    break;
                                }
                            }
                            if (isCreative && k == -1) {
                                k = mc.thePlayer.inventory.getFirstEmptyStack();
                                if (k < 0 || k >= MAIN_INV) {
                                    k = mc.thePlayer.inventory.currentItem;
                                }
                            }
                            if (k >= 0 && k < MAIN_INV) {
                                mc.thePlayer.inventory.currentItem = k;
                                Battlegear.packetHandler.sendPacketToServer(new PickBlockPacket(stack, k).generatePacket());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Equivalent code to the creative pick block
     * @param target The client target vector
     * @param player The player trying to pick
     * @return the stack expected for the creative pick button
     */
    private static ItemStack getItemFromPointedAt(MovingObjectPosition target, EntityPlayer player) {
        if(target!=null){
            if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                BlockPos pos = target.getBlockPos();
                World world = player.getEntityWorld();
                Block block = world.getBlockState(pos).getBlock();
                if (block.isAir(world, pos))
                {
                    return null;
                }
                return block.getPickBlock(target, world, pos);
            }
            else
            {
                if (target.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || target.entityHit == null || !player.capabilities.isCreativeMode)
                {
                    return null;
                }
                return target.entityHit.getPickedResult(target);
            }
        }
        return null;
    }

	/**
	 * Returns a rotation angle that is inbetween two other rotation angles.
	 * par1 and par2 are the angles between which to interpolate, par3 is
	 * probably a float between 0.0 and 1.0 that tells us where "between" the
	 * two angles we are. Example: par1 = 30, par2 = 50, par3 = 0.5, then return
	 * = 40
	 */
	public float interpolateRotation(float par1, float par2, float par3) {
		float f3 = par2 - par1;

		while (f3 < -180.0F) {
            f3 += 360.0F;
		}

		while (f3 >= 180.0F) {
			f3 -= 360.0F;
		}

		return par1 + par3 * f3;
	}

    /**
     * Register a few "item" icons
     */
	@SubscribeEvent
	public void preStitch(TextureStitchEvent.Pre event) {
        ClientProxy.backgroundIcon = new TextureAtlasSprite[]{
                    event.map.registerSprite(new ResourceLocation("battlegear2:items/slots/mainhand")),
                    event.map.registerSprite(new ResourceLocation("battlegear2:items/slots/offhand"))
            };

            //storageIndex = PatternStore.DEFAULT.buildPatternAndStore(patterns);
            /*CrestImages.initialise(Minecraft.getMinecraft().getResourceManager());
            for (HeraldryPattern pattern : HeraldryPattern.patterns) {
                pattern.registerIcon(event.map);
            }*/
	}

    /**
     * Change attribute format when displayed on item tooltip
     *
     * @param event
     */
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        for (String txt : event.toolTip) {
            if (txt.startsWith(EnumChatFormatting.BLUE.toString())) {
                if (txt.contains(attributeNames[0]) || txt.contains(attributeNames[2]))
                    event.toolTip.set(event.toolTip.indexOf(txt), EnumChatFormatting.DARK_GREEN + EnumChatFormatting.getTextWithoutFormattingCodes(txt));
                else if (txt.contains(attributeNames[3]))
                    event.toolTip.set(event.toolTip.indexOf(txt), EnumChatFormatting.DARK_GREEN + reformat(txt, 3));
                else if (txt.contains(attributeNames[1]))
                    event.toolTip.set(event.toolTip.indexOf(txt), EnumChatFormatting.GOLD + reformat(txt, 1));
            }
        }
        if(event.itemStack.getItem() instanceof IBackStabbable){
            event.toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("attribute.name.weapon.backstab"));
        }
    }

    //Equivalent of the ItemStack decimal formatter used in attribute tooltip display
    private final static Pattern FLOAT = Pattern.compile("\\d.\\d+");

    /**
     * Format into "ratio" attribute localization
     *
     * @param txt  current attribute local
     * @param type the attribute index
     * @return the new localization
     */
    private String reformat(String txt, int type) {
        String result = EnumChatFormatting.getTextWithoutFormattingCodes(txt);
        Matcher matcher = FLOAT.matcher(result);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String temp = result.substring(start, end).replace(",", ".");
            try {
                float value = Float.valueOf(temp) * 100;
                temp = ".plus.1";
                if (start > 0 && result.charAt(start - 1) == '-') {
                    temp = ".take.1";
                }
                return StatCollector.translateToLocalFormatted("attribute.modifier" + temp, ItemStack.DECIMALFORMAT.format(value), attributeNames[type]);
            } catch (NumberFormatException notNumber) {
                notNumber.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Help translating attributes
     *
     * @param attribute
     * @return the attribute name into the current language
     */
    private String toLocal(BaseAttribute attribute) {
        return StatCollector.translateToLocal("attribute.name." + attribute.getAttributeUnlocalizedName());
    }

    /**
     * Reload translation caches
     */
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        attributeNames = new String[]{
                toLocal(Attributes.armourPenetrate),
                toLocal(Attributes.daze),
                toLocal(Attributes.extendedReach),
                toLocal(Attributes.attackSpeed)
        };
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void postInitGui(GuiScreenEvent.InitGuiEvent.Post event){
        if (Battlegear.battlegearEnabled && event.gui instanceof InventoryEffectRenderer) {
            if(!ClientProxy.tconstructEnabled || FMLClientHandler.instance().getClientPlayerEntity().capabilities.isCreativeMode) {
                onOpenGui(event.buttonList, ((InventoryEffectRenderer) event.gui).guiLeft-30, ((InventoryEffectRenderer) event.gui).guiTop);
            }
        }
    }

    /**
     * Helper method to add buttons to a gui when opened
     * @param buttons the List<GuiButton> of the opened gui
     * @param guiLeft horizontal placement parameter
     * @param guiTop vertical placement parameter
     */
	public static void onOpenGui(List buttons, int guiLeft, int guiTop) {
        if(BattlegearConfig.enableGuiButtons){
			int count = 0;
			for (GuiPlaceableButton tab : tabsList) {
                GuiPlaceableButton button = tab.copy();
				button.place(count, guiLeft, guiTop);
				button.id = buttons.size()+2;//Due to GuiInventory and GuiContainerCreative button performed actions, without them having buttons...
				count++;
				buttons.add(button);
			}
        }
	}
}
