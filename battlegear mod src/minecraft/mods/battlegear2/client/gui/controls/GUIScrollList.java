package mods.battlegear2.client.gui.controls;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

public abstract class GUIScrollList {

    protected final int listWidth;
    protected final int top;
    protected final int bottom;
    private final int right;
    protected final int left;
    protected final int slotHeight;
    private int scrollUpActionId;
    private int scrollDownActionId;
    protected int mouseX;
    protected int mouseY;
    private float initialMouseClickY = -2.0F;
    private float scrollFactor;
    private float scrollDistance;
    private int selectedIndex = -1;
    private long lastClickTime = 0L;
    private boolean highlightSelected = true;
    private boolean hasHeader;
    private int headerHeight;
	public boolean drawList = true;

    public GUIScrollList(int width, int top, int bottom, int left, int entryHeight)
    {
        this.listWidth = width;
        this.top = top;
        this.bottom = bottom;
        this.slotHeight = entryHeight;
        this.left = left;
        this.right = width + this.left;
    }

    public void func_27258_a(boolean p_27258_1_)
    {
        this.highlightSelected = p_27258_1_;
    }

    protected void setHeaderInfo(boolean p_27259_1_, int p_27259_2_)
    {
        this.hasHeader = p_27259_1_;
        this.headerHeight = p_27259_2_;

        if (!p_27259_1_)
        {
            this.headerHeight = 0;
        }
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int index, boolean doubleClick);

    protected abstract boolean isSelected(int index);

    protected int getContentHeight(){
        return this.getSize() * this.slotHeight + this.headerHeight;
    }

    protected abstract void drawBackground();

    protected abstract void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5);

    protected void drawHeader(int entryRight, int relativeY, Tessellator tess) {}

    protected void drawScreen(int mouseX, int mouseY) {}

    public int func_27256_c(int p_27256_1_, int p_27256_2_){
        int var3 = this.left + 1;
        int var4 = this.left + this.listWidth - 7;
        int var5 = p_27256_2_ - this.top - this.headerHeight + (int)this.scrollDistance - 4;
        int var6 = var5 / this.slotHeight;
        return p_27256_1_ >= var3 && p_27256_1_ <= var4 && var6 >= 0 && var5 >= 0 && var6 < this.getSize() ? var6 : -1;
    }

    public void registerScrollButtons(List p_22240_1_, int p_22240_2_, int p_22240_3_)
    {
        this.scrollUpActionId = p_22240_2_;
        this.scrollDownActionId = p_22240_3_;
    }

    private void applyScrollLimits()
    {
        int var1 = this.getContentHeight() - (this.bottom - this.top - 4);

        if (var1 < 0)
        {
            var1 /= 2;
        }

        if (this.scrollDistance < 0.0F)
        {
            this.scrollDistance = 0.0F;
        }

        if (this.scrollDistance > (float)var1)
        {
            this.scrollDistance = (float)var1;
        }
    }

    public void actionPerformed(GuiButton button)
    {
        if (button.enabled && drawList)
        {
            if (button.id == this.scrollUpActionId)
            {
                this.scrollDistance -= (float)(this.slotHeight * 2 / 3);
                this.initialMouseClickY = -2.0F;
                this.applyScrollLimits();
            }
            else if (button.id == this.scrollDownActionId)
            {
                this.scrollDistance += (float)(this.slotHeight * 2 / 3);
                this.initialMouseClickY = -2.0F;
                this.applyScrollLimits();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float p_22243_3_)
    {
    	if(drawList){
	        this.mouseX = mouseX;
	        this.mouseY = mouseY;
	        this.drawBackground();
	        int listLength = this.getSize();
	        int scrollBarXStart = this.left + this.listWidth - 6;
	        int scrollBarXEnd = scrollBarXStart + 6;
	        int boxLeft = this.left;
	        int boxRight = scrollBarXStart-1;
	        int var10;
	        int var11;
	        int var13;
	        int var19;
	
	        if (Mouse.isButtonDown(0))
	        {
	            if (this.initialMouseClickY == -1.0F)
	            {
	                boolean var7 = true;
	
	                if (mouseY >= this.top && mouseY <= this.bottom)
	                {
	                    var10 = mouseY - this.top - this.headerHeight + (int)this.scrollDistance - 4;
	                    var11 = var10 / this.slotHeight;
	
	                    if (mouseX >= boxLeft && mouseX <= boxRight && var11 >= 0 && var10 >= 0 && var11 < listLength)
	                    {
	                        boolean var12 = var11 == this.selectedIndex && System.currentTimeMillis() - this.lastClickTime < 250L;
	                        this.elementClicked(var11, var12);
	                        this.selectedIndex = var11;
	                        this.lastClickTime = System.currentTimeMillis();
	                    }
	                    else if (mouseX >= boxLeft && mouseX <= boxRight && var10 < 0)
	                    {
	                        var7 = false;
	                    }
	
	                    if (mouseX >= scrollBarXStart && mouseX <= scrollBarXEnd)
	                    {
	                        this.scrollFactor = -1.0F;
	                        var19 = this.getContentHeight() - (this.bottom - this.top - 4);
	
	                        if (var19 < 1)
	                        {
	                            var19 = 1;
	                        }
	
	                        var13 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
	
	                        if (var13 < 32)
	                        {
	                            var13 = 32;
	                        }
	
	                        if (var13 > this.bottom - this.top - 8)
	                        {
	                            var13 = this.bottom - this.top - 8;
	                        }
	
	                        this.scrollFactor /= (float)(this.bottom - this.top - var13) / (float)var19;
	                    }
	                    else
	                    {
	                        this.scrollFactor = 1.0F;
	                    }
	
	                    if (var7)
	                    {
	                        this.initialMouseClickY = (float)mouseY;
	                    }
	                    else
	                    {
	                        this.initialMouseClickY = -2.0F;
	                    }
	                }
	                else
	                {
	                    this.initialMouseClickY = -2.0F;
	                }
	            }
	            else if (this.initialMouseClickY >= 0.0F)
	            {
	                this.scrollDistance -= ((float)mouseY - this.initialMouseClickY) * this.scrollFactor;
	                this.initialMouseClickY = (float)mouseY;
	            }
	        }
	        else
	        {
	            while (Mouse.next())
	            {
	                int var16 = Mouse.getEventDWheel();
	
	                if (var16 != 0)
	                {
	                    if (var16 > 0)
	                    {
	                        var16 = -1;
	                    }
	                    else if (var16 < 0)
	                    {
	                        var16 = 1;
	                    }
	
	                    this.scrollDistance += (float)(var16 * this.slotHeight / 2);
	                }
	            }
	
	            this.initialMouseClickY = -1.0F;
	        }
	
	        this.applyScrollLimits();
	        GlStateManager.disableLighting();
	        GlStateManager.disableFog();
			Tessellator var18 = Tessellator.getInstance();
	

	        var10 = this.top + 4 - (int)this.scrollDistance;
	
	        if (this.hasHeader)
	        {
	            this.drawHeader(boxRight, var10, var18);
	        }
	
	        for (var11 = 0; var11 < listLength; ++var11)
	        {
	            var19 = var10 + var11 * this.slotHeight + this.headerHeight;
	            var13 = this.slotHeight - 4;
	
	            if (var19 <= this.bottom && var19 + var13 >= this.top)
	            {
	                if (this.highlightSelected && this.isSelected(var11))
	                {
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	                    GlStateManager.disableTexture2D();
						var18.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
						var18.getBuffer().pos((double) boxLeft, (double) (var19 + var13 + 2), 0).tex(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
						var18.getBuffer().pos((double) boxRight, (double) (var19 + var13 + 2), 0).tex(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
						var18.getBuffer().pos((double) boxRight, (double) (var19 - 2), 0).tex(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
						var18.getBuffer().pos((double) boxLeft, (double) (var19 - 2), 0).tex(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
						var18.getBuffer().pos((double) (boxLeft + 1), (double) (var19 + var13 + 1), 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
						var18.getBuffer().pos((double) (boxRight - 1), (double) (var19 + var13 + 1), 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
						var18.getBuffer().pos((double) (boxRight - 1), (double) (var19 - 1), 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
						var18.getBuffer().pos((double) (boxLeft + 1), (double) (var19 - 1), 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
						var18.draw();
	                    GlStateManager.enableTexture2D();
	                }
	
	                this.drawSlot(var11, boxRight, var19, var13, var18);
	            }
	        }
	
	        GlStateManager.disableDepth();
	        GlStateManager.enableBlend();
	        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GlStateManager.disableAlpha();
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
	        GlStateManager.disableTexture2D();
	
	        var19 = this.getContentHeight() - (this.bottom - this.top - 4);
	
	        if (var19 > 0)
	        {
	            var13 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
	
	            if (var13 < 32)
	            {
	                var13 = 32;
	            }
	
	            if (var13 > this.bottom - this.top - 8)
	            {
	                var13 = this.bottom - this.top - 8;
	            }
	
	            int var14 = (int)this.scrollDistance * (this.bottom - this.top - var13) / var19 + this.top;
	
	            if (var14 < this.top)
	            {
	                var14 = this.top;
	            }

				var18.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				var18.getBuffer().pos((double) scrollBarXStart, (double) this.bottom, 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				var18.getBuffer().pos((double) scrollBarXEnd, (double) this.bottom, 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				var18.getBuffer().pos((double) scrollBarXEnd, (double) this.top, 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				var18.getBuffer().pos((double) scrollBarXStart, (double) this.top, 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				var18.draw();
				var18.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				var18.getBuffer().pos((double) scrollBarXStart, (double) (var14 + var13), 0).tex(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				var18.getBuffer().pos((double) scrollBarXEnd, (double) (var14 + var13), 0).tex(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				var18.getBuffer().pos((double) scrollBarXEnd, (double) var14, 0).tex(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				var18.getBuffer().pos((double) scrollBarXStart, (double) var14, 0).tex(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				var18.draw();
				var18.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				var18.getBuffer().pos((double) scrollBarXStart, (double) (var14 + var13 - 1), 0).tex(0, 1).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
				var18.getBuffer().pos((double) (scrollBarXEnd - 1), (double) (var14 + var13 - 1), 0).tex(1, 1).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
				var18.getBuffer().pos((double) (scrollBarXEnd - 1), (double) var14, 0).tex(1, 0).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
				var18.getBuffer().pos((double) scrollBarXStart, (double) var14, 0).tex(0, 0).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
				var18.draw();
	        }
	
	        this.drawScreen(mouseX, mouseY);
	        GlStateManager.enableTexture2D();
			GlStateManager.shadeModel(GL11.GL_FLAT);
	        GlStateManager.enableAlpha();
	        GlStateManager.disableBlend();
	    }
    }
    
    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect(Tessellator tessellator, int x, int y, int width, int height, int zLevel)
    {
		tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		tessellator.getBuffer().pos((double) (x), (double) (y + height), (double) zLevel).tex(0F, 1F).endVertex();
		tessellator.getBuffer().pos((double) (x + width), (double) (y + height), (double) zLevel).tex(1F, 1F).endVertex();
		tessellator.getBuffer().pos((double) (x + width), (double) (y), (double) zLevel).tex(1F, 0F).endVertex();
		tessellator.getBuffer().pos((double) (x), (double) (y), (double) zLevel).tex(0F, 0F).endVertex();
		tessellator.draw();
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color. Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int x1, int y1, int x2, int y2, int col)
    {
		Gui.drawRect(x1, y1, x2, y2, col);
	}

}
