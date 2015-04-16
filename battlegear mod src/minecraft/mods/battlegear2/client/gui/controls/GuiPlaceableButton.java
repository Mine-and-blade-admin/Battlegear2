package mods.battlegear2.client.gui.controls;

import cpw.mods.fml.client.config.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.util.ResourceLocation;

public abstract class GuiPlaceableButton extends GuiButton {
    public static final ResourceLocation CREATIVE_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	public static final int HEIGHT = 20, TAB_DIM = 28, TAB_BORDER = 3;
    protected int deltaY = 0;
    private final String oldName;
	public GuiPlaceableButton(int par1, String name) {
		super(par1, 0, 0, HEIGHT+4, HEIGHT, name);
        oldName = name;
	}

    /**
     * Change the button position.
     * Default is a 2-row pattern
     *
     * @param count the order of the button in the drawn list
     * @param guiLeft starting horizontal position
     * @param guiTop starting vertical position
     */
	public void place(int count, int guiLeft, int guiTop) {
		this.xPosition = guiLeft + (count / 2) * width;
		this.yPosition = guiTop + (count % 2) * height;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean inWindow = super.mousePressed(mc, mouseX, mouseY);
		if (inWindow){
            if(!isInGui(mc.currentScreen))
                this.openGui(mc);
            else
                mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
        }
		return inWindow;
	}
	
	public final boolean isInGui(GuiScreen currentScreen) {
		return currentScreen.getClass()==getGUIClass();
	}

	@Override
	public int getHoverState(boolean isMouseOver){
		if(!isInGui(Minecraft.getMinecraft().currentScreen)){
            this.displayString = oldName;
			return super.getHoverState(isMouseOver);
		}
        this.displayString = "Main";
		return 0;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY){
        if (this.visible)
        {
            if(mc.currentScreen instanceof InventoryEffectRenderer) {
                if (deltaY != 0) {
                    this.yPosition -= deltaY;
                    deltaY = 0;
                }
                int size = mc.thePlayer.getActivePotionEffects().size();
                if (size > 0) {
                    int off = 33;
                    if (size > 4) {
                        off = -2 * height;
                        size = 1;
                    }
                    deltaY = off * size;
                    this.yPosition += deltaY;
                }
            }
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            drawTextureBox(k);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = 14737632;

            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            else if (!this.enabled)
            {
                color = 10526880;
            }
            else if (this.field_146123_n)
            {
                color = 16777120;
            }
            this.drawCenteredString(mc.fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);
        }
    }

    protected void drawTextureBox(int hoverState){
        GuiUtils.drawContinuousTexturedBox(CREATIVE_TABS, this.xPosition, this.yPosition, 0, 2 + (hoverState > 0 ? 30 : 0), this.width, this.height, TAB_DIM, TAB_DIM, TAB_BORDER, TAB_BORDER, TAB_BORDER, TAB_BORDER, this.zLevel);
    }

    /**
     * The gui type that will be opened by #openGui(Minecraft)
     */
	protected abstract Class<? extends GuiScreen> getGUIClass();

	protected abstract void openGui(Minecraft mc);

    /**
     * @return a new instance of this button
     */
    public abstract GuiPlaceableButton copy();
}
