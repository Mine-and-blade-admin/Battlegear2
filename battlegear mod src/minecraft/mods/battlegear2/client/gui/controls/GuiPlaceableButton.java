package mods.battlegear2.client.gui.controls;

import cpw.mods.fml.client.config.GuiUtils;
import mods.battlegear2.Battlegear;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.BattlegearGUIPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public abstract class GuiPlaceableButton extends GuiButton {
    public static final ResourceLocation CREATIVE_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	public static final int HEIGHT = 20, TAB_DIM = 28, TAB_BORDER = 3;
    private int deltaX = 0;
    private final String oldName;
	public GuiPlaceableButton(int par1, int par2, int par3, String name) {
		super(par1, par2, par3, HEIGHT+4, HEIGHT, name);
        oldName = name;
	}

	public void place(int count, int guiLeft, int guiTop) {
		this.xPosition = guiLeft;
		this.yPosition = guiTop + count * HEIGHT;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        this.xPosition -= deltaX;
		boolean inWindow = super.mousePressed(mc, mouseX, mouseY);
        this.xPosition += deltaX;
		if (inWindow){
            if(!isInGui(mc.currentScreen))
                this.openGui(mc);
            else
                Battlegear.packetHandler.sendPacketToServer(new BattlegearGUIPacket(BattlegearGUIHandeler.mainID).generatePacket());
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
        deltaX = mc.thePlayer.getActivePotionEffects().isEmpty() ? 0 : 130;
        this.xPosition -= deltaX;
        if (this.visible)
        {
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
        this.xPosition += deltaX;
    }

    protected void drawTextureBox(int hoverState){
        GuiUtils.drawContinuousTexturedBox(CREATIVE_TABS, this.xPosition, this.yPosition, 0, 2 + (hoverState > 0 ? 30 : 0), this.width, this.height, TAB_DIM, TAB_DIM, TAB_BORDER, TAB_BORDER, TAB_BORDER, TAB_BORDER, this.zLevel);
    }

	protected abstract Class<? extends GuiScreen> getGUIClass();

	protected abstract void openGui(Minecraft mc);
}
