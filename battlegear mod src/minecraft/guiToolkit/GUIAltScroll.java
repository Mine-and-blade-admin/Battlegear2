package guiToolkit;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.MathHelper;

public class GUIAltScroll extends GuiButton {
	
	 /** The sizein pixels */
    protected int size;

    /** The x position of this control. */
    public int xPos;

    /** The y position of this control. */
    public int yPos;
    
    /** Weather the control is horizontal or vertical*/
    protected boolean horizonatal;
    
    /** The current position of the slider */
    public int current;
    
    /** The slider value 0<=x=<1 */
    public float sliderValue;
    
    /** Is this slider control being dragged. */
    public boolean dragging = false;
    
    protected int min;
    protected int max;
	
	public GUIAltScroll(int id, int xPos, int yPos, int size, boolean horizontal, int min, int max){
		super(id, xPos, yPos, size, size, "");
		
		if(max > min){
			this.max = max;
			this.min = min;
		}else{
			this.min = max;
			this.max = min;
		}
		
		this.max++;
		
		this.horizonatal = horizontal;
		
		if(!horizontal){
			width = 14;
			height = size;
		}else{
			height = 14;
			width = size;
		}
		
		this.current = min;
	}
	
	
	
	@Override
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		 if (this.drawButton)
	     {
			 FontRenderer var4 = minecraft.fontRenderer;
			 minecraft.renderEngine.bindTexture("/guiToolkit/Toolkit.png");
			 GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			 
			 if(horizonatal){
				 this.drawTexturedModalRect(this.xPosition, this.yPosition, 42, 52, this.width / 2, this.height);
		         this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2 + 42, 52, this.width / 2, this.height);
		         this.drawTexturedModalRect(this.xPosition + (int)(sliderValue * (float)(this.width - 15)), this.yPosition+1, 39, 0, 24, 12);
			 }else{
				 drawTexturedModalRect(xPosition, yPosition, 242, 0, this.width, this.height/2);
				 drawTexturedModalRect(xPosition, yPosition + height/2, 242, 200 - this.height/ 2, this.width , this.height/ 2);
				 this.drawTexturedModalRect(this.xPosition+1, this.yPosition + (int)(this.sliderValue * (float)(this.height- 15)), 0, 36, 12, 15);
			 }
			 
			 if(Mouse.isButtonDown(0))
				 this.mouseDragged(minecraft, mouseX, mouseY);
			 else{
				 mouseReleased(mouseX, mouseY);
			 }
			 
			 if(this.displayString != null && this.displayString.length()>0){
				 
			 }
	     }
	}



	 
    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
	 
    @Override
    protected void mouseDragged(Minecraft minecraft, int mouseX, int mouseY)
    {
        if (this.drawButton)
        {
            if (this.dragging)
            {
            	if(horizonatal)
            		this.sliderValue = (float)(mouseX - (this.xPosition + 8)) / (float)(this.width - 15);
            	else
            		this.sliderValue = (float)(mouseY -(this.yPosition + 8)) / (float)(this.height - 15);

            	
                if (this.sliderValue < 0.0F)
                {
                    this.sliderValue = 0.0F;
                }

                if (this.sliderValue > 0.9999F)
                {
                    this.sliderValue = 0.9999F;
                }
                
                current = MathHelper.floor_float(sliderValue*(max-min)+min);                
            }            
        }
    }
    
    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft par1Minecraft, int mouseX, int mouseY)
    {
        if (super.mousePressed(par1Minecraft,mouseX, mouseY))
        {
        	if(horizonatal)
        		this.sliderValue = (float)(mouseX - (this.xPosition + 15)) / (float)(this.width - 15);
        	else
        		this.sliderValue = (float)(mouseY -(this.yPosition + 15)) / (float)(this.height - 15);

            if (this.sliderValue < 0.0F){
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 0.9999F){
                this.sliderValue = 0.9999F;
            }
           
            this.dragging = !dragging;
            return true;
        }
        else
        {
            return false;
        }
        
    }
	
	/**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int par1, int par2)
    {
        this.dragging = false;
    }
	
    
    

}
