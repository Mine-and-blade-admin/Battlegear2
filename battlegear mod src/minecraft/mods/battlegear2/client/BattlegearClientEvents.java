package mods.battlegear2.client;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.weapons.Attributes;
import mods.battlegear2.api.weapons.IBackStabbable;
import mods.battlegear2.client.gui.BattlegearInGameGUI;
import mods.battlegear2.client.gui.controls.GuiBGInventoryButton;
import mods.battlegear2.client.gui.controls.GuiPlaceableButton;
import mods.battlegear2.client.gui.controls.GuiSigilButton;
import mods.battlegear2.client.model.QuiverModel;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import mods.battlegear2.enchantments.BaseEnchantment;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BattlegearClientEvents implements IResourceManagerReloadListener {

	private final BattlegearInGameGUI inGameGUI;
    public final QuiverModel quiverModel;
    public final ResourceLocation quiverDetails;
    public final ResourceLocation quiverBase;
    //public static final ResourceLocation patterns = new ResourceLocation("battlegear2", "textures/heraldry/patterns-small.png");
    //public static int storageIndex;

	public static final GuiPlaceableButton[] tabsList = { new GuiBGInventoryButton(0), new GuiSigilButton(1)};
    public static final BattlegearClientEvents INSTANCE = new BattlegearClientEvents();
    private String[] attributeNames;
    private ItemStack cache;

    private BattlegearClientEvents(){
        inGameGUI = new BattlegearInGameGUI();
        quiverModel = new QuiverModel();
        quiverDetails = new ResourceLocation("battlegear2", "textures/armours/quiver/quiver_details.png");
        quiverBase = new ResourceLocation("battlegear2", "textures/armours/quiver/quiver_base.png");
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    /**
     * Render all the Battlegear HUD elements
     * @param event Fired on each type of HUD element
     */
	@SubscribeEvent(receiveCanceled = true, priority = EventPriority.LOWEST)
	public void preRenderToolbar(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && (BattlegearConfig.forceHUD || !event.isCanceled())) {
            inGameGUI.renderGameOverlay(event.getPartialTicks(), event.getResolution());
            if(!event.isCanceled()){
                final EntityPlayer player = Minecraft.getMinecraft().player;
                if(!player.isSpectator() && BattlegearUtils.isPlayerInBattlemode(player)) {
                    cache = player.getHeldItemOffhand();
                    BattlegearUtils.setPlayerOffhandItem(player, ItemStack.EMPTY);
                }
            }
        }
	}

    /**
     * Revert soft hack of the offhand item slot rendering
     * @param event Fired if Pre event wasn't cancelled, right after all HUD render is done
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void postRenderToolbar(RenderGameOverlayEvent.Post event){
	    if(event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR){
            final EntityPlayer player = Minecraft.getMinecraft().player;
            if(!player.isSpectator() && BattlegearUtils.isPlayerInBattlemode(player)) {
                BattlegearUtils.setPlayerOffhandItem(player, cache);
                cache = ItemStack.EMPTY;
            }
        }
    }

    /**
     * Render the offhand arm and shield swing in first person
     * @param event Fired right before player arm are rendered in first person
     */
	@SubscribeEvent
    public void renderPlayerOffhand(RenderSpecificHandEvent event){
        final AbstractClientPlayer player = Minecraft.getMinecraft().player;
	    if(event.getHand() == EnumHand.OFF_HAND){
	        if(event.getItemStack().isEmpty()){
                if(!player.isInvisible() && BattlegearUtils.isPlayerInBattlemode(player)) {
                    GlStateManager.pushMatrix();
                    Minecraft.getMinecraft().getItemRenderer().renderArmFirstPerson(event.getEquipProgress(), event.getSwingProgress(), player.getPrimaryHand().opposite());
                    GlStateManager.popMatrix();
                }
                return;
	        }else if(event.getItemStack().getItem() instanceof IShield){
                BattlegearRenderHelper.renderItemInFirstPerson(event.getEquipProgress(), player, Minecraft.getMinecraft().getItemRenderer());
                event.setCanceled(true);
                return;
            }
        }
        if(event.getItemStack().getItem() instanceof IArrowContainer2 && ((IArrowContainer2) event.getItemStack().getItem()).renderDefaultQuiverModel(event.getItemStack())) {
            if (BattlegearConfig.hasRender("quiver")) {
                ItemStack quiverStack = QuiverArrowRegistry.getArrowContainer(player);
                if (event.getItemStack() == quiverStack) {
                    int slot = ((IArrowContainer2) event.getItemStack().getItem()).getSelectedSlot(event.getItemStack());
                    ItemStack arrowStack = ((IArrowContainer2) event.getItemStack().getItem()).getStackInSlot(event.getItemStack(), slot);
                    Minecraft.getMinecraft().getItemRenderer().renderItemInFirstPerson(player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), arrowStack, event.getEquipProgress());
                    event.setCanceled(true);
                    return;
                }
            }
        }
        ItemStack inUse = player.getActiveItemStack();
        if(event.getHand() != player.getActiveHand() && event.getItemStack() != inUse && !inUse.isEmpty() && BattlegearUtils.isBow(inUse.getItem())) {
            event.setCanceled(true);
        }
    }

    private static final int SKELETON_ARROW = 5;
    /**
     * Render quiver on skeletons if possible
     */
	@SubscribeEvent
	public void renderLiving(RenderLivingEvent.Post<AbstractSkeleton> event) {

		if (BattlegearConfig.enableSkeletonQuiver && event.getEntity() instanceof EntitySkeleton && event.getRenderer() instanceof RenderSkeleton) {

            GlStateManager.pushMatrix();
            GlStateManager.disableCull();

            GlStateManager.color(1, 1, 1);
			Minecraft.getMinecraft().getTextureManager().bindTexture(quiverDetails);

            GlStateManager.translate((float) event.getX(), (float) event.getY(), (float) event.getZ());

            GlStateManager.scale(1, -1, 1);

			float f2 = interpolateRotation(event.getEntity().prevRenderYawOffset, event.getEntity().renderYawOffset, BattlegearClientTickHandeler.getPartialTick());

            GlStateManager.rotate(180.0F - f2, 0.0F, 1.0F, 0.0F);

			if (event.getEntity().deathTime > 0) {
				float f3 = ((float) event.getEntity().deathTime
						+ BattlegearClientTickHandeler.getPartialTick() - 1.0F) / 20.0F * 1.6F;
				f3 = MathHelper.sqrt(f3);

				if (f3 > 1.0F) {
					f3 = 1.0F;
				}

                GlStateManager.rotate(-f3 * 90, 0.0F, 0.0F, 1.0F);
			}

            GlStateManager.translate(0, -1.5F, 0);

            GlStateManager.rotate(event.getEntity().rotationPitch, 0, 1, 0);

            if(event.getEntity().hasItemInSlot(EntityEquipmentSlot.CHEST)){
                GlStateManager.translate(0, 0, BattlegearRenderHelper.RENDER_UNIT);
            }
            ((ModelBiped)event.getRenderer().mainModel).bipedBody.postRender(BattlegearRenderHelper.RENDER_UNIT);
            GlStateManager.scale(1.05F, 1.05F, 1.05F);
			quiverModel.render(SKELETON_ARROW, BattlegearRenderHelper.RENDER_UNIT);

			Minecraft.getMinecraft().getTextureManager().bindTexture(quiverBase);
            GlStateManager.color(0.10F, 0.10F, 0.10F);
			quiverModel.render(0, BattlegearRenderHelper.RENDER_UNIT);
            GlStateManager.color(1, 1, 1);

            GlStateManager.enableCull();
            GlStateManager.popMatrix();
		}
	}

    /**
     * Counter the bow use fov jerkyness with the draw enchantment
     */
    @SubscribeEvent
    public void onBowFOV(FOVUpdateEvent event){
        ItemStack stack = event.getEntity().getActiveItemStack();
        if (EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowCharge, stack) > 0) {
            int i = event.getEntity().getItemInUseCount();
            float f1 = (float) i / 20.0F;
            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 *= f1;
            }
            event.setNewfov(event.getFov()/ (1.0F - f1 * 0.15F));
        }
    }

	/**
	 * Returns a rotation angle that is in between two other rotation angles.
	 * Copy of {@link RenderLivingBase#interpolateRotation(float, float, float)}
	 */
	public float interpolateRotation(float start, float end, float factor) {
		float f3 = end - start;
		while (f3 < -180.0F) {
            f3 += 360.0F;
		}

		while (f3 >= 180.0F) {
			f3 -= 360.0F;
		}
		return start + factor * f3;
	}

    /**
     * Register a few "item" icons
     */
	@SubscribeEvent
	public void preStitch(TextureStitchEvent.Pre event) {
        ClientProxy.backgroundIcon = new TextureAtlasSprite[]{
                    event.getMap().registerSprite(new ResourceLocation("battlegear2:items/slots/mainhand")),
                    event.getMap().registerSprite(new ResourceLocation("battlegear2:items/slots/offhand"))
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
        for (String txt : event.getToolTip()) {
            if (txt.startsWith(TextFormatting.BLUE.toString())) {
                if (txt.contains(attributeNames[0]) || txt.contains(attributeNames[2]))
                    event.getToolTip().set(event.getToolTip().indexOf(txt), TextFormatting.DARK_GREEN + TextFormatting.getTextWithoutFormattingCodes(txt));
                else if (txt.contains(attributeNames[3]))
                    event.getToolTip().set(event.getToolTip().indexOf(txt), TextFormatting.DARK_GREEN + reformat(txt, 3));
                else if (txt.contains(attributeNames[1]))
                    event.getToolTip().set(event.getToolTip().indexOf(txt), TextFormatting.GOLD + reformat(txt, 1));
            }
        }
        if(event.getItemStack().getItem() instanceof IBackStabbable){
            event.getToolTip().add(TextFormatting.GOLD + I18n.format("attribute.name.weapon.backstab"));
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
        String result = TextFormatting.getTextWithoutFormattingCodes(txt);
        if(result == null)
            return txt;
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
                return I18n.format("attribute.modifier" + temp, ItemStack.DECIMALFORMAT.format(value), attributeNames[type]);
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
        return I18n.format("attribute.name." + attribute.getName());
    }

    /**
     * Reload translation caches
     */
    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        attributeNames = new String[]{
                toLocal(Attributes.armourPenetrate),
                toLocal(Attributes.daze),
                toLocal(Attributes.extendedReach),
                toLocal(Attributes.attackSpeed)
        };
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void postInitGui(GuiScreenEvent.InitGuiEvent.Post event){
        if (Battlegear.battlegearEnabled && event.getGui() instanceof InventoryEffectRenderer) {
            if(!ClientProxy.tconstructEnabled || Minecraft.getMinecraft().player.capabilities.isCreativeMode) {
                onOpenGui(event.getButtonList(), ((InventoryEffectRenderer) event.getGui()).getGuiLeft()-30, ((InventoryEffectRenderer) event.getGui()).getGuiTop());
            }
        }
    }

    /**
     * Helper method to add buttons to a gui when opened
     * @param buttons the List<GuiButton> of the opened gui
     * @param guiLeft horizontal placement parameter
     * @param guiTop vertical placement parameter
     */
	public static void onOpenGui(List<GuiButton> buttons, int guiLeft, int guiTop) {
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
