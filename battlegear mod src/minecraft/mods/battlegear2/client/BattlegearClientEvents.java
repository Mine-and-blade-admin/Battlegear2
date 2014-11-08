package mods.battlegear2.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.battlegear2.Battlegear;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.IDyable;
import mods.battlegear2.api.RenderItemBarEvent;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.weapons.IBackStabbable;
import mods.battlegear2.api.weapons.IExtendedReachWeapon;
import mods.battlegear2.api.weapons.IHitTimeModifier;
import mods.battlegear2.api.weapons.IPenetrateWeapon;
import mods.battlegear2.client.gui.BattlegearInGameGUI;
import mods.battlegear2.client.gui.controls.GuiBGInventoryButton;
import mods.battlegear2.client.gui.controls.GuiPlaceableButton;
import mods.battlegear2.client.gui.controls.GuiSigilButton;
import mods.battlegear2.client.model.QuiverModel;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.items.ItemWeapon;
import mods.battlegear2.packet.PickBlockPacket;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class BattlegearClientEvents {

	private final BattlegearInGameGUI inGameGUI = new BattlegearInGameGUI();
	private final QuiverModel quiverModel = new QuiverModel();
	private final ResourceLocation quiverDetails = new ResourceLocation("battlegear2", "textures/armours/quiver/QuiverDetails.png");
	private final ResourceLocation quiverBase = new ResourceLocation("battlegear2", "textures/armours/quiver/QuiverBase.png");
    //public static final ResourceLocation patterns = new ResourceLocation("battlegear2", "textures/heraldry/Patterns-small.png");
    //public static int storageIndex;
	public static GuiPlaceableButton[] tabsList = { new GuiBGInventoryButton(0, 10, 10), new GuiSigilButton(1, 20, 20)};

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
			inGameGUI.renderGameOverlay(event.partialTicks, event.mouseX, event.mouseY);
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
            ItemStack offhand = ((InventoryPlayerBattle) entityPlayer.inventory).getCurrentOffhandWeapon();
            if (offhand != null && event.renderer instanceof RenderPlayer) {
                RenderPlayer renderer = ((RenderPlayer) event.renderer);
                renderer.modelArmorChestplate.heldItemLeft = renderer.modelArmor.heldItemLeft = renderer.modelBipedMain.heldItemLeft = 1;
                if (entityPlayer.getItemInUseCount() > 0 && entityPlayer.getItemInUse() == offhand) {
                    EnumAction enumaction = offhand.getItemUseAction();
                    if (enumaction == EnumAction.block) {
                        renderer.modelArmorChestplate.heldItemLeft = renderer.modelArmor.heldItemLeft = renderer.modelBipedMain.heldItemLeft = 3;
                    } else if (enumaction == EnumAction.bow) {
                        renderer.modelArmorChestplate.aimedBow = renderer.modelArmor.aimedBow = renderer.modelBipedMain.aimedBow = true;
                    }
                    ItemStack mainhand = entityPlayer.inventory.getCurrentItem();
                    renderer.modelArmorChestplate.heldItemRight = renderer.modelArmor.heldItemRight = renderer.modelBipedMain.heldItemRight = mainhand != null ? 1 : 0;
                }else if(((IBattlePlayer)entityPlayer).isBlockingWithShield()){
                    renderer.modelArmorChestplate.heldItemLeft = renderer.modelArmor.heldItemLeft = renderer.modelBipedMain.heldItemLeft = 3;
                }
            }
        }
    }

    /**
     * Reset models to default values
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void resetPlayerLeftHand(RenderPlayerEvent.Post event){
        event.renderer.modelArmorChestplate.heldItemLeft = event.renderer.modelArmor.heldItemLeft = event.renderer.modelBipedMain.heldItemLeft = 0;
    }

    /**
     * Render a player left hand item, or sheathed items, and quiver on player back
     */
	@SubscribeEvent
	public void render3rdPersonBattlemode(RenderPlayerEvent.Specials.Post event) {

		ModelBiped biped = (ModelBiped) event.renderer.mainModel;
		BattlegearRenderHelper.renderItemIn3rdPerson(event.entityPlayer, biped, event.partialRenderTick);

		ItemStack quiverStack = QuiverArrowRegistry.getArrowContainer(event.entityPlayer);
        if (quiverStack != null && ((IArrowContainer2) quiverStack.getItem()).renderDefaultQuiverModel(quiverStack)) {

            IArrowContainer2 quiver = (IArrowContainer2) quiverStack.getItem();
            int maxStack = quiver.getSlotCount(quiverStack);
            int arrowCount = 0;
            for (int i = 0; i < maxStack; i++) {
                arrowCount += quiver.getStackInSlot(quiverStack, i) == null ? 0 : 1;
            }
            GL11.glColor3f(1, 1, 1);
            Minecraft.getMinecraft().getTextureManager().bindTexture(quiverDetails);
            GL11.glPushMatrix();
            biped.bipedBody.postRender(0.0625F);
            GL11.glScalef(1.05F, 1.05F, 1.05F);
            quiverModel.render(arrowCount, 0.0625F);

            Minecraft.getMinecraft().getTextureManager().bindTexture(quiverBase);
            if(quiverStack.getItem() instanceof IDyable){
                int col = ((IDyable)quiver).getColor(quiverStack);
                float red = (float) (col >> 16 & 255) / 255.0F;
                float green = (float) (col >> 8 & 255) / 255.0F;
                float blue = (float) (col & 255) / 255.0F;
                GL11.glColor3f(red, green, blue);
            }
            quiverModel.render(0, 0.0625F);
            GL11.glColor3f(1, 1, 1);

            GL11.glPopMatrix();
        }
	}

    private static final int SKELETON_ARROW = 5;
    /**
     * Render quiver on skeletons if possible
     */
	@SubscribeEvent
	public void renderLiving(RenderLivingEvent.Post event) {

		if (BattlegearConfig.enableSkeletonQuiver && event.entity instanceof EntitySkeleton
				&& event.renderer instanceof RenderSkeleton) {

			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_CULL_FACE);

			GL11.glColor3f(1, 1, 1);
			Minecraft.getMinecraft().getTextureManager().bindTexture(quiverDetails);

			double d0 = (((EntitySkeleton) event.entity).lastTickPosX + ((((EntitySkeleton) event.entity).posX - ((EntitySkeleton) event.entity).lastTickPosX) * BattlegearClientTickHandeler.partialTick));
			double d1 = (((EntitySkeleton) event.entity).lastTickPosY + ((((EntitySkeleton) event.entity).posY - ((EntitySkeleton) event.entity).lastTickPosY) * BattlegearClientTickHandeler.partialTick));
			double d2 = (((EntitySkeleton) event.entity).lastTickPosZ + (((EntitySkeleton) event.entity).posZ - ((EntitySkeleton) event.entity).lastTickPosZ)
					* BattlegearClientTickHandeler.partialTick);

			GL11.glTranslatef((float) (d0 - RenderManager.renderPosX),
					(float) (d1 - RenderManager.renderPosY),
					(float) (d2 - RenderManager.renderPosZ));

			GL11.glScalef(1, -1, 1);

			float f2 = interpolateRotation(event.entity.prevRenderYawOffset, event.entity.renderYawOffset, 0);

			GL11.glRotatef(180.0F - f2, 0.0F, 1.0F, 0.0F);

			if (event.entity.deathTime > 0) {
				float f3 = ((float) event.entity.deathTime
						+ BattlegearClientTickHandeler.partialTick - 1.0F) / 20.0F * 1.6F;
				f3 = MathHelper.sqrt_float(f3);

				if (f3 > 1.0F) {
					f3 = 1.0F;
				}

				GL11.glRotatef(-f3 * 90, 0.0F, 0.0F, 1.0F);
			}

			GL11.glTranslatef(0, -1.5F, 0);

			GL11.glRotatef(event.entity.rotationPitch, 0, 1, 0);
            ((ModelBiped)event.renderer.mainModel).bipedBody.postRender(0.0625F);
			GL11.glScalef(1.05F, 1.05F, 1.05F);
			quiverModel.render(SKELETON_ARROW, 0.0625F);

			Minecraft.getMinecraft().getTextureManager().bindTexture(quiverBase);
			GL11.glColor3f(0.10F, 0.10F, 0.10F);
			quiverModel.render(0, 0.0625F);
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

    private static final int MAIN_INV = InventoryPlayer.getHotbarSize();

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
                        ItemStack stack = getItemFromPointedAt(mc.objectMouseOver, mc.theWorld, isCreative);
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
     * @param world The world of the player
     * @param creative If player is in creative mode
     * @return the stack expected for the creative pick button
     */
    private static ItemStack getItemFromPointedAt(MovingObjectPosition target, World world, boolean creative) {
        if(target!=null){
            if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int x = target.blockX;
                int y = target.blockY;
                int z = target.blockZ;
                Block block = world.getBlock(x, y, z);
                if (block.isAir(world, x, y, z))
                {
                    return null;
                }
                return block.getPickBlock(target, world, x, y, z);
            }
            else
            {
                if (target.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || target.entityHit == null || !creative)
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
	private float interpolateRotation(float par1, float par2, float par3) {
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
		if (event.map.getTextureType() == 1) {
			ClientProxy.backgroundIcon = new IIcon[]{event.map.registerIcon("battlegear2:slots/mainhand"),event.map.registerIcon("battlegear2:slots/offhand")};

			ClientProxy.bowIcons = new IIcon[3];
            for(int i = 0; i < ClientProxy.bowIcons.length; i++) {
                ClientProxy.bowIcons[i] = event.map.registerIcon("battlegear2:bow_pulling_"+i);
            }

            //storageIndex = PatternStore.DEFAULT.buildPatternAndStore(patterns);
            /*CrestImages.initialise(Minecraft.getMinecraft().getResourceManager());
            for (HeraldryPattern pattern : HeraldryPattern.patterns) {
                pattern.registerIcon(event.map);
            }*/
		}
	}

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event){
        if(event.itemStack.getItem() instanceof IPenetrateWeapon || event.itemStack.getItem() instanceof IHitTimeModifier || event.itemStack.getItem() instanceof IExtendedReachWeapon){
            for(String txt:event.toolTip){
                if(txt.startsWith(EnumChatFormatting.BLUE.toString())){
                    if(txt.contains(StatCollector.translateToLocal("attribute.name."+ ItemWeapon.armourPenetrate.getAttributeUnlocalizedName())) || txt.contains(StatCollector.translateToLocal("attribute.name."+ ItemWeapon.attackSpeed.getAttributeUnlocalizedName())) || txt.contains(StatCollector.translateToLocal("attribute.name."+ ItemWeapon.extendedReach.getAttributeUnlocalizedName())))
                        event.toolTip.set(event.toolTip.indexOf(txt), EnumChatFormatting.DARK_GREEN + EnumChatFormatting.getTextWithoutFormattingCodes(txt));
                }
            }
        }
        if(event.itemStack.getItem() instanceof IBackStabbable){
            event.toolTip.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("attribute.name.weapon.backstab"));
        }
    }

    @SubscribeEvent
    public void postInitGui(GuiScreenEvent.InitGuiEvent.Post event){
        if (Battlegear.battlegearEnabled && event.gui instanceof InventoryEffectRenderer) {
            if(!ClientProxy.tconstructEnabled || FMLClientHandler.instance().getClientPlayerEntity().capabilities.isCreativeMode) {
                onOpenGui(event.buttonList, guessGuiLeft((InventoryEffectRenderer) event.gui)-30, guessGuiTop(event.gui));
            }
        }
    }

    /**
     * Make a guess over the value of GuiContainer#guiTop (protected)
     * Use magic numbers !
     *
     * @param guiContainer the current screen whose value is desired
     * @return the guessed value
     */
    public static int guessGuiLeft(InventoryEffectRenderer guiContainer){
        int offset = FMLClientHandler.instance().getClientPlayerEntity().getActivePotionEffects().isEmpty() ? 0 : 60;
        if(guiContainer instanceof GuiContainerCreative){
            return offset + (guiContainer.width - 195)/2;
        }
        return offset + (guiContainer.width - 176)/2;
    }

    /**
     * Make a guess over the value of GuiContainer#guiLeft (protected)
     * Use magic numbers !
     *
     * @param gui the current screen whose value is desired
     * @return the guessed value
     */
    public static int guessGuiTop(GuiScreen gui){
        if(gui instanceof GuiContainerCreative){
            return (gui.height - 136)/2;
        }
        return (gui.height - 166)/2;
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
			for (GuiPlaceableButton button : tabsList) {
				button.place(count, guiLeft, guiTop);
				button.id = buttons.size()+2;//Due to GuiInventory and GuiContainerCreative button performed actions, without them having buttons...
				count++;
				buttons.add(button);
			}
        }
	}
}
