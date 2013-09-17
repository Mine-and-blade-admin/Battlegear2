package mods.battlegear2.client.gui;

import mods.battlegear2.gui.ContainerBattle;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BattleEquipGUI extends InventoryEffectRenderer {


    public static final ResourceLocation resource = new ResourceLocation("battlegear2", "textures/gui/Equip GUI.png");

    /**
     * x size of the inventory window in pixels. Defined as float, passed as int
     */
    private float xSize_lo;

    /**
     * y size of the inventory window in pixels. Defined as float, passed as int.
     */
    private float ySize_lo;

    public BattleEquipGUI(EntityPlayer entityPlayer, boolean isRemote) {
        //super(new ContainerPlayer(entityPlayer.inventory, !isRemote, entityPlayer));
        super(new ContainerBattle(entityPlayer.inventory, !isRemote, entityPlayer));
        this.allowUserInput = true;

        //Don't need this, however maybe we can add a stat later on. I will keep it comented out for now
        //entityPlayer.addStat(AchievementList.openInventory, 1);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3){
        super.drawScreen(par1, par2, par3);
        this.xSize_lo = (float) par1;
        this.ySize_lo = (float) par2;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        super.initGui();
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(resource);
        int var5 = this.guiLeft;
        int var6 = this.guiTop;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        //GuiInventory.drawPlayerOnGui(this.mc, var5 + 31, var6 + 75, 30, (float) (var5 + 51) - this.xSize_lo, (float) (var6 + 75 - 50) - this.ySize_lo);
        GuiInventory.func_110423_a(var5 + 31, var6 + 75, 30, (float) (var5 + 51) - this.xSize_lo, (float) (var6 + 75 - 50) - this.ySize_lo, mc.thePlayer);
    }



}
