package guiToolkit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GUIAltButton extends GuiButton {

	private static final ResourceLocation toolkit = new ResourceLocation("/guiToolkit/Toolkit.png");
	/**
	 * Creates a new button
	 * @param id The button ID
	 * @param xPos The X position
	 * @param yPos The y Position
	 * @param width The width
	 * @param height The height
	 * @param text the dispay text
	 */
	public GUIAltButton(int id, int xPos, int yPos, int width, int height,
			String text) {
		super(id, xPos, yPos, width, height, text);
	}
	
	/**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY)
    {
        if (this.drawButton)
        {
            FontRenderer var4 = minecraft.fontRenderer;
            minecraft.renderEngine.func_110577_a(toolkit);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean mouseOver = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(mouseOver);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0+42, hoverState * 18, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2 +42, hoverState * 18, this.width / 2, this.height);
            this.mouseDragged(minecraft, mouseX, mouseY);
            
            int colour = 14737632;
            if (!this.enabled)
            {
                colour = -6250336;
            }
            else if (mouseOver)
            {
                colour = 16777120;
            }

            
            this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, colour);
        }
    }

}


