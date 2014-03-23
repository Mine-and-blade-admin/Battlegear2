package mods.battlegear2.client.gui;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.RenderItemBarEvent;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.client.BattlegearClientEvents;
import mods.battlegear2.client.BattlegearClientTickHandeler;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;

public class BattlegearInGameGUI extends Gui {

    public static final int SLOT_H = 22;
    public static final RenderItem itemRenderer = new RenderItem();
    public static final ResourceLocation resourceLocation = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation resourceLocationShield = new ResourceLocation("battlegear2", "textures/gui/Shield Bar.png");
    private Class<?> previousGui;
    private Minecraft mc;

    public BattlegearInGameGUI() {
        super();
        mc = FMLClientHandler.instance().getClient();
    }

    public void renderGameOverlay(float frame, int mouseX, int mouseY) {

        if(Battlegear.battlegearEnabled){
            ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int width = scaledresolution.getScaledWidth();
            int height = scaledresolution.getScaledHeight();
            RenderGameOverlayEvent renderEvent = new RenderGameOverlayEvent(frame, scaledresolution, mouseX, mouseY);

            if (!this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                zLevel = -90.0F;
                if(!ClientProxy.tconstructEnabled || mc.thePlayer.capabilities.isCreativeMode){
	                if(mc.currentScreen==null) {
                        if(previousGui!=null)
	                	    previousGui=null;
	                }else{
                        Class<?> currentGui = mc.currentScreen.getClass();
                        if(currentGui!=previousGui && (currentGui.equals(GuiContainerCreative.class) || currentGui.equals(GuiInventory.class))){
                            BattlegearClientEvents.onOpenGui(mc.currentScreen.buttonList, ((GuiContainer) mc.currentScreen).guiLeft-30, ((GuiContainer)mc.currentScreen).guiTop);
                            previousGui = currentGui;
                        }
                    }
                }

                RenderItemBarEvent event = new RenderItemBarEvent.BattleSlots(renderEvent, true);
                if(!MinecraftForge.EVENT_BUS.post(event)){
                    renderBattleSlots(width / 2 + 121 + event.xOffset, height - 22 + event.yOffset, frame, true);
                }
                event = new RenderItemBarEvent.BattleSlots(renderEvent, false);
                if(!MinecraftForge.EVENT_BUS.post(event)){
                    renderBattleSlots(width / 2 - 184 + event.xOffset, height - 22 + event.yOffset, frame, false);
                }

                ItemStack offhand = ((InventoryPlayerBattle) mc.thePlayer.inventory).getCurrentOffhandWeapon();
                if(offhand!= null && offhand.getItem() instanceof IShield){
                    event = new RenderItemBarEvent.ShieldBar(renderEvent, offhand);
                    if(!MinecraftForge.EVENT_BUS.post(event))
                        renderBlockBar(width / 2 - 91 + event.xOffset, height - 35 + event.yOffset);
                }

                ItemStack mainhand = mc.thePlayer.getCurrentEquippedItem();
                if(mainhand != null){
                    ItemStack quiver = QuiverArrowRegistry.getArrowContainer(mainhand, mc.thePlayer);
                    if(quiver != null){
                        event = new RenderItemBarEvent.QuiverSlots(renderEvent, mainhand, quiver);
                        if(!MinecraftForge.EVENT_BUS.post(event))
                            renderQuiverBar(quiver, frame, event.xOffset+width/2, event.yOffset);
                    }
                }

            }
        }
    }

    public void renderBattleSlots(int x, int y, float frame, boolean isMainHand) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(resourceLocation);

        drawTexturedModalRect(x, y, 0, 0, 31, SLOT_H);
        drawTexturedModalRect(x + 31, y, 151, 0, 31, SLOT_H);

        if (mc.thePlayer!=null && ((IBattlePlayer) mc.thePlayer).isBattlemode()) {
            this.drawTexturedModalRect(x + (mc.thePlayer.inventory.currentItem - InventoryPlayerBattle.OFFSET) * 20,
                    y - 1, 0, 22, 24, SLOT_H);
        }

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        if(mc.thePlayer!=null){
            for (int i = 0; i < InventoryPlayerBattle.WEAPON_SETS; ++i) {
                int varx = x + i * 20 + 3;
                this.renderInventorySlot(i + InventoryPlayerBattle.OFFSET+(isMainHand?0:InventoryPlayerBattle.WEAPON_SETS), varx, y+3, frame);
            }
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void renderQuiverBar(ItemStack quiver, float frame, int xOffset, int yOffset) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(resourceLocation);

        int maxSlots = ((IArrowContainer2)quiver.getItem()).getSlotCount(quiver)*10;

        drawTexturedModalRect(xOffset -(1 + maxSlots), yOffset, 0, 0, 1+maxSlots, SLOT_H);
        drawTexturedModalRect(xOffset , yOffset, 182-(1+maxSlots), 0, 1+maxSlots, SLOT_H);

        int selectedSlot =  ((IArrowContainer2)quiver.getItem()).getSelectedSlot(quiver);

        drawTexturedModalRect(xOffset -(2 + maxSlots) + 20*selectedSlot, -1+yOffset, 0, 22, 24, SLOT_H);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < maxSlots/10; ++i) {
            int x = xOffset -(maxSlots-1) + i * 20;
            renderStackAt(x, yOffset+2, ((IArrowContainer2)quiver.getItem()).getStackInSlot(quiver, i), frame);
        }

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void renderBlockBar(int x, int y) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(resourceLocationShield);

        if(mc.thePlayer!=null){
            if(mc.thePlayer.capabilities.isCreativeMode){
                if(mc.thePlayer.isRidingHorse()){
                    y-=5;
                }
            }else{
                y-= 16;
                if(ForgeHooks.getTotalArmorValue(mc.thePlayer) > 0 || mc.thePlayer.isRidingHorse() || mc.thePlayer.getAir() < 300){
                    y-=10;
                }
            }
        }

        this.drawTexturedModalRect(x, y, 0, 0, 182, 9);

        float[] colour = BattlegearClientTickHandeler.COLOUR_DEFAULT;
        if(BattlegearClientTickHandeler.blockBar < 0.33F){
            colour = BattlegearClientTickHandeler.COLOUR_RED;
        }
        if(BattlegearClientTickHandeler.getFlashTimer() > 0 && (System.currentTimeMillis() / 250) % 2 == 0){
            colour = BattlegearClientTickHandeler.COLOUR_YELLOW;
        }
        GL11.glColor3f(colour[0], colour[1], colour[2]);
        this.drawTexturedModalRect(x, y, 0, 9, (int) (182 * BattlegearClientTickHandeler.blockBar), 9);

        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderInventorySlot(int par1, int par2, int par3, float par4) {
        ItemStack itemstack = this.mc.thePlayer.inventory.getStackInSlot(par1);
        renderStackAt(par2, par3, itemstack, par4);
    }

    private void renderStackAt(int x, int y, ItemStack itemstack, float frame){
        if (itemstack != null) {
            float f1 = (float) itemstack.animationsToGo - frame;

            if (f1 > 0.0F) {
                GL11.glPushMatrix();
                float f2 = 1.0F + f1 / 5.0F;
                GL11.glTranslatef((float) (x + 8), (float) (y + 12), 0.0F);
                GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, itemstack, x, y);
            if (f1 > 0.0F) {
                GL11.glPopMatrix();
            }
            itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, itemstack, x, y);
        }
    }

}
