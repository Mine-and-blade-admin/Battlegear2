package mods.battlegear2.client.gui;

import java.util.List;

import mods.battlegear2.Battlegear;
import mods.battlegear2.BowHookContainerClass2;
import mods.battlegear2.api.IShield;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.client.BattlegearClientEvents;
import mods.battlegear2.client.BattlegearClientTickHandeler;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class BattlegearInGameGUI extends Gui {

    private Minecraft mc;

    public static final RenderItem itemRenderer = new RenderItem();
    protected static final ResourceLocation resourceLocation = new ResourceLocation("textures/gui/widgets.png");
    protected static final ResourceLocation resourceLocationShield = new ResourceLocation("battlegear2", "textures/gui/Shield Bar.png");
    private Class<?extends InventoryEffectRenderer> previousGui;

    public BattlegearInGameGUI() {
        super();
        mc = FMLClientHandler.instance().getClient();
    }

    public void renderGameOverlay(float frame) {

        if(Battlegear.battlegearEnabled){
            ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int width = scaledresolution.getScaledWidth();
            int height = scaledresolution.getScaledHeight();

            if (!this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                if(!Battlegear.tconstructEnabled || mc.thePlayer.capabilities.isCreativeMode){
	                if(previousGui!=null && mc.currentScreen==null) {
	                	previousGui=null;
	                }
	                if(mc.currentScreen instanceof InventoryEffectRenderer && mc.currentScreen.getClass()!=previousGui){
                		BattlegearClientEvents.onOpenGui((List) ObfuscationReflectionHelper.getPrivateValue(GuiScreen.class, mc.currentScreen, 3),(int)ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class,(GuiContainer)mc.currentScreen,5)-40,(int)ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class,(GuiContainer)mc.currentScreen,6));
						previousGui = (Class<? extends InventoryEffectRenderer>) mc.currentScreen.getClass();
	                }
                }
                this.mc.renderEngine.bindTexture(resourceLocation);
                InventoryPlayerBattle inventoryplayer = (InventoryPlayerBattle) this.mc.thePlayer.inventory;
                this.zLevel = -90.0F;

                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                drawTexturedModalRect(width / 2 + 91 + 15 + 15, height - 22, 0, 0, 31, 22);
                drawTexturedModalRect(width / 2 + 91 + 15 + 31 + 15, height - 22, 151, 0, 31, 22);

                drawTexturedModalRect(width / 2 - 91 - 15 - 62 - 15, height - 22, 0, 0, 31, 22);
                drawTexturedModalRect(width / 2 - 91 - 15 - 31 - 15, height - 22, 151, 0, 31, 22);


                if (mc.thePlayer.isBattlemode()) {

                    this.drawTexturedModalRect(width / 2 - 169 + (inventoryplayer.currentItem - InventoryPlayerBattle.OFFSET) * 20 - 15,
                            height - 22 - 1, 0, 22, 24, 22);

                    this.drawTexturedModalRect(width / 2 + 105 + (inventoryplayer.currentItem - InventoryPlayerBattle.OFFSET) * 20 + 15,
                            height - 22 - 1, 0, 22, 24, 22);
                }

                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                RenderHelper.enableGUIStandardItemLighting();

                for (int i = 0; i < InventoryPlayerBattle.WEAPON_SETS; ++i) {
                    int x = width / 2 - 91 - 16 - 58 + (i) * 20 - 15;
                    int y = height - 19;

                    this.renderInventorySlot(i + InventoryPlayerBattle.OFFSET, x + 105 + 169+ 30, y, frame);
                    this.renderInventorySlot(i + InventoryPlayerBattle.OFFSET + InventoryPlayerBattle.WEAPON_SETS, x, y, frame);

                }

                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);

            }

            if(mc.thePlayer.isBattlemode() &&
                    mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem + 3) != null &&
                    mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem + 3).getItem() instanceof IShield){

                renderBlockBar(width, height);
            }

            ItemStack mainhand = mc.thePlayer.getCurrentEquippedItem();
            if(mainhand != null){
                ItemStack quiver = BowHookContainerClass2.getArrowContainer(mainhand, mc.thePlayer);
                if(quiver != null){
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                    this.mc.renderEngine.bindTexture(resourceLocation);
                    InventoryPlayerBattle inventoryplayer = (InventoryPlayerBattle) this.mc.thePlayer.inventory;
                    this.zLevel = -90.0F;

                    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    int maxSlots = ((IArrowContainer2)quiver.getItem()).getSlotCount(quiver);

                    drawTexturedModalRect(width / 2 -(1 + (maxSlots*10)), 0, 0, 0, 1+(maxSlots*10), 22);
                    drawTexturedModalRect(width / 2 , 0, 182-(1+(maxSlots*10)), 0, (1+(maxSlots*10)), 22);

                    int selectedSlot =  ((IArrowContainer2)quiver.getItem()).getSelectedSlot(quiver);

                    this.drawTexturedModalRect(width / 2 -(2 + (maxSlots*10)) + 20*selectedSlot, -1, 0, 22, 24, 22);


                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                    RenderHelper.enableGUIStandardItemLighting();

                    for (int i = 0; i < maxSlots; ++i) {
                        int x = width / 2 -((maxSlots*10)-1) + i * 20;
                        int y = 2;

                        renderStackAt(x, y, ((IArrowContainer2)quiver.getItem()).getStackInSlot(quiver, i), frame);
                    }

                    RenderHelper.disableStandardItemLighting();
                    GL11.glDisable(GL12.GL_RESCALE_NORMAL);




                }
            }
        }
    }

    private void renderBlockBar(int width, int height) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.mc.renderEngine.bindTexture(resourceLocationShield);
        int x = width / 2 - 91;
        int y = height - 35;

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if(player.capabilities.isCreativeMode){
            if(player.isRidingHorse()){
                y-=5;
            }
        }else{
            y-= 16;
            if(ForgeHooks.getTotalArmorValue(player) > 0 || player.isRidingHorse() || player.getAir() < 300){
                y-=10;
            }
        }

        this.drawTexturedModalRect(x, y, 0, 0, 182, 9);

        float[] colour = BattlegearClientTickHandeler.COLOUR_DEFAULT;
        if(BattlegearClientTickHandeler.blockBar < 0.33F){
            colour = BattlegearClientTickHandeler.COLOUR_RED;
        }
        if(BattlegearClientTickHandeler.flashTimer > 0 && (System.currentTimeMillis() / 250) % 2 == 0){
            colour = BattlegearClientTickHandeler.COLOUR_YELLOW;
        }
        GL11.glColor3f(colour[0], colour[1], colour[2]);
        this.drawTexturedModalRect(x, y, 0, 9, (int)(182 * BattlegearClientTickHandeler.blockBar), 9);

        GL11.glDisable(GL11.GL_BLEND);

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

    private void renderInventorySlot(int par1, int par2, int par3, float par4) {
        ItemStack itemstack = this.mc.thePlayer.inventory.getStackInSlot(par1);
        renderStackAt(par2, par3, itemstack, par4);
    }

}
