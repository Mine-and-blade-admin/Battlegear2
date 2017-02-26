package mods.battlegear2.client.gui;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.RenderItemBarEvent;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.client.BattlegearClientTickHandeler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

public class BattlegearInGameGUI extends Gui {

    private static final float[] COLOUR_DEFAULT = new float[]{0, 0.75F, 1};
    private static final float[] COLOUR_RED = new float[]{1, 0.1F, 0.1F};
    private static final float[] COLOUR_YELLOW = new float[]{1, 1F, 0.1F};
    public static final int SLOT_H = 22;
    private static final ResourceLocation resourceLocation = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation resourceLocationShield = new ResourceLocation("battlegear2", "textures/gui/shield_bar.png");
    private final Minecraft mc;

    public BattlegearInGameGUI() {
        super();
        mc = FMLClientHandler.instance().getClient();
        zLevel = -90.0F;
    }

    public void renderGameOverlay(float frame, ScaledResolution scaledResolution) {

        if (Battlegear.battlegearEnabled && !this.mc.playerController.isSpectator() && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            int width = scaledResolution.getScaledWidth();
            int height = scaledResolution.getScaledHeight();
            RenderGameOverlayEvent renderEvent = new RenderGameOverlayEvent(frame, scaledResolution);

            RenderItemBarEvent event = new RenderItemBarEvent.BattleSlots(renderEvent, true);
            if(!BattlegearUtils.RENDER_BUS.post(event)){
                renderBattleSlots(width / 2 + 121 + event.xOffset, height - 22 + event.yOffset, frame, true);
            }
            event = new RenderItemBarEvent.BattleSlots(renderEvent, false);
            if(!BattlegearUtils.RENDER_BUS.post(event)){
                renderBattleSlots(width / 2 - 184 + event.xOffset, height - 22 + event.yOffset, frame, false);
            }

            ItemStack offhand = ((InventoryPlayerBattle) mc.player.inventory).getCurrentOffhandWeapon();
            if(offhand.getItem() instanceof IShield){
                event = new RenderItemBarEvent.ShieldBar(renderEvent, offhand);
                if(!BattlegearUtils.RENDER_BUS.post(event))
                    renderBlockBar(width / 2 - 91 + event.xOffset, height - 35 + event.yOffset);
            }

            ItemStack mainhand = mc.player.getHeldItemMainhand();
            boolean quiverFound = false;
            if(!mainhand.isEmpty()){
                ItemStack quiver = QuiverArrowRegistry.getArrowContainer(mainhand, mc.player);
                if(!quiver.isEmpty()){
                    event = new RenderItemBarEvent.QuiverSlots(renderEvent, mainhand, quiver);
                    if(!BattlegearUtils.RENDER_BUS.post(event))
                        renderQuiverBar(quiver, frame, event.xOffset + width / 2, event.yOffset);
                    quiverFound = true;
                }
            }
            if(!quiverFound) {
                mainhand = ((InventoryPlayerBattle) mc.player.inventory).getCurrentOffhandWeapon();
                if (!mainhand.isEmpty()) {
                    ItemStack quiver = QuiverArrowRegistry.getArrowContainer(mainhand, mc.player);
                    if (!quiver.isEmpty()) {
                        event = new RenderItemBarEvent.QuiverSlots(renderEvent, mainhand, quiver);
                        if (!BattlegearUtils.RENDER_BUS.post(event))
                            renderQuiverBar(quiver, frame, event.xOffset + width / 2, event.yOffset);
                    }
                }
            }
        }
    }

    public void renderBattleSlots(int x, int y, float frame, boolean isMainHand) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(resourceLocation);
        drawTexturedModalRect(x, y, 0, 0, 31, SLOT_H);
        drawTexturedModalRect(x + 31, y, 151, 0, 31, SLOT_H);
        if (mc.player!=null){
            if(((IBattlePlayer) mc.player).isBattlemode())
                this.drawTexturedModalRect(x + (mc.player.inventory.currentItem - InventoryPlayerBattle.OFFSET) * 20-1,
                        y - 1, 0, 22, 24, SLOT_H);
            push();
            for (int i = 0; i < InventoryPlayerBattle.WEAPON_SETS; ++i) {
                int varx = x + i * 20 + 3;
                this.renderInventorySlot(i + InventoryPlayerBattle.OFFSET+(isMainHand?0:InventoryPlayerBattle.WEAPON_SETS),
                        varx, y+3, frame);
            }
            pop();
        }
    }

    public void renderQuiverBar(ItemStack quiver, float frame, int xOffset, int yOffset) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(resourceLocation);
        int maxSlots = ((IArrowContainer2)quiver.getItem()).getSlotCount(quiver)*10;

        drawTexturedModalRect(xOffset -(1 + maxSlots), yOffset, 0, 0, 1+maxSlots, SLOT_H);
        drawTexturedModalRect(xOffset , yOffset, 182-(1+maxSlots), 0, 1+maxSlots, SLOT_H);

        int selectedSlot =  ((IArrowContainer2)quiver.getItem()).getSelectedSlot(quiver);
        drawTexturedModalRect(xOffset -(2 + maxSlots) + 20*selectedSlot, yOffset-1, 0, 22, 24, SLOT_H);

        push();
        for (int i = 0; i < maxSlots/10; ++i) {
            int x = xOffset -(1+maxSlots) + i * 20 + 3;
            renderStackAt(x, yOffset+3, ((IArrowContainer2)quiver.getItem()).getStackInSlot(quiver, i), frame);
        }
        pop();
    }

    public void renderBlockBar(int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(resourceLocationShield);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if(mc.player!=null){
            if(mc.player.capabilities.isCreativeMode){
                if(mc.player.isRidingHorse()){
                    y-=5;
                }
            }else{
                y-= 16;
                if(mc.player.isRidingHorse() || mc.player.getAir() < 300 || ForgeHooks.getTotalArmorValue(mc.player) > 0){
                    y-=10;
                }
            }
        }

        this.drawTexturedModalRect(x, y, 0, 0, 182, 9);

        float[] colour = COLOUR_DEFAULT;
        if(BattlegearClientTickHandeler.getBlockTime() < 0.33F){
            colour = COLOUR_RED;
        }
        if(BattlegearClientTickHandeler.getFlashTimer() > 0 && (System.currentTimeMillis() / 250) % 2 == 0){
            colour = COLOUR_YELLOW;
        }
        GlStateManager.color(colour[0], colour[1], colour[2]);
        this.drawTexturedModalRect(x, y, 0, 9, (int) (182 * BattlegearClientTickHandeler.getBlockTime()), 9);

        GlStateManager.disableBlend();
    }

    private void renderInventorySlot(int par1, int par2, int par3, float par4) {
        ItemStack itemstack = this.mc.player.inventory.getStackInSlot(par1);
        renderStackAt(par2, par3, itemstack, par4);
    }

    private void renderStackAt(int x, int y, ItemStack itemstack, float frame){
        if (!itemstack.isEmpty()) {
            float f1 = (float) itemstack.getAnimationsToGo() - frame;

            if (f1 > 0.0F) {
                GlStateManager.pushMatrix();
                float f2 = 1.0F + f1 / 5.0F;
                GlStateManager.translate((float) (x + 8), (float) (y + 12), 0.0F);
                GlStateManager.scale(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
            }

            mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, x, y);
            if (f1 > 0.0F) {
                GlStateManager.popMatrix();
            }
            mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, itemstack, x, y);
        }
    }

    private void push(){
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.enableGUIStandardItemLighting();
    }

    private void pop(){
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
}
