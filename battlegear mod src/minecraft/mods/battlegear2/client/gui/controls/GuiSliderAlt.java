package mods.battlegear2.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiSliderAlt extends GuiButton
{
    /** The value of this slider control. */
    public float sliderValue = 1.0F;

    /** Is this slider control being dragged. */
    public boolean dragging;

    private int min;
    private int max;
    private String label;

    public GuiSliderAlt(int par1, int par2, int par3, int width, String par5Str, float par6, int min, int max)
    {
        super(par1, par2, par3, width, 20, par5Str);
        this.min = min;
        this.max = max;
        this.label = par5Str;
        this.sliderValue = par6;
    }

    @Override
    protected int getHoverState(boolean par1)
    {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
                slideChange(par2);

            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    public void slideChange(int varX){
        this.sliderValue = (float)(varX - (this.xPosition + 4)) / (float)(this.width - 8);

        if (this.sliderValue < 0.0F)
        {
            this.sliderValue = 0.0F;
        }

        if (this.sliderValue > 1.0F)
        {
            this.sliderValue = 1.0F;
        }

        this.displayString = label +": " + ((int)((max - min) * sliderValue) + min);
    }

    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        if (super.mousePressed(par1Minecraft, par2, par3))
        {
            slideChange(par2);
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void mouseReleased(int par1, int par2)
    {
        this.dragging = false;
    }

    public int getValue() {
        return ((int)((max - min) * sliderValue) + min);
    }
}
