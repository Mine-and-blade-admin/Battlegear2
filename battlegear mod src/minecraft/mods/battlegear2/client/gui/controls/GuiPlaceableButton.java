package mods.battlegear2.client.gui.controls;

import mods.battlegear2.Battlegear;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.BattlegearGUIPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;

public abstract class GuiPlaceableButton extends GuiButton {
	public static final int HEIGHT = 20;
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
        deltaX = mc.thePlayer.getActivePotionEffects().isEmpty() ? 0 : 120;
        this.xPosition -= deltaX;
        super.drawButton(mc, mouseX, mouseY);
        this.xPosition += deltaX;
    }

	protected abstract Class<? extends GuiScreen> getGUIClass();

	protected abstract void openGui(Minecraft mc);
}
