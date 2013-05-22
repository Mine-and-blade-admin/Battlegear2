package mods.battlegear2.client.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import extendedGUI.BasicColourButton;
import extendedGUI.GUIAltButton;
import extendedGUI.GUIAltScroll;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.heraldry.SigilHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetServerHandler;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;

public class GuiHeraldry extends GuiScreen{
	
	private Minecraft mc = FMLClientHandler.instance().getClient();

	/** The X size of the window in pixels. */
	protected int xSize = 176;

	/** The Y size of the window in pixels. */
	protected int ySize = 190;
	
	/**
     * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiLeft;

    /**
     * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiTop;
	
	private byte pattern; //only a nibble
	private byte colour1; //only a nibble
	private byte colour2; //only a nibble
	private byte sigil; //only value that is actually a byte
	private byte sigilPos; //only a nibble
	private byte sigilColour1; //only a nibble
	private byte sigilColour2; //only a nibble
	
	private String[] displayString = new String[]{
			"sigil.pattern",
			"sigil.patternColour1",
			"sigil.patternColour2",
			"sigil.icon",
			"sigil.pos",
			"sigil.iconColour1",
			"sigil.iconColour2"
	};
	
	private String patternTitle = "sigil.pattern.title";
	private String iconTitle = "sigil.icon.title";
	
	
	private EntityPlayer player;
	private boolean personal;
	
	/**
	 * Creates a new GUI for creating and editing sigils.
	 * @param player
	 * @param personal
	 */
	public GuiHeraldry(EntityPlayer player, boolean personal){
		this.player = player;
		this.personal = personal;
	}
	
	public void initGui()
    {
        super.initGui();

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
                
        int controlsX = guiLeft+130;
        int controlsY = guiTop+27;
        
       
    }

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
	    
	    //this.drawCenteredString(fontRenderer, patternTitle, this.guiLeft+130+45, 12+guiTop, 0xFFFFFF);
	    //this.drawCenteredString(fontRenderer, iconTitle, this.guiLeft+130+45, 12+guiTop+16*4, 0xFFFFFF);

	    mc.renderEngine.bindTexture(BattleGear.imageFolder+"/gui/Sigil GUI.png");
	    
	    /*
	    for(int i = 1; i < 3; i++){
		    this.drawRect(guiLeft+130+72, guiTop+22+16*i, guiLeft+130+72+16, guiTop+22+16*i+16,
		    		0xFF000000 | SigilHelper.colours[scrolls[i].current]);
		    GL11.glColor3f(1, 1, 1);
		    this.drawTexturedModalRect(guiLeft+130+72, guiTop+22+16*i, 66, 240, 16, 16);	
	    }
	    
	    
	    for(int i = 5; i < 7; i++){
	    	 this.drawRect(guiLeft+130+72, guiTop+22+16*i+16, guiLeft+130+72+16, guiTop+22+16*i+32, 
	    			 0xFF000000 | SigilHelper.colours[scrolls[i].current]);
			 GL11.glColor3f(1, 1, 1);
			 this.drawTexturedModalRect(guiLeft+130+72, guiTop+22+16*i+16, 66, 240, 16, 16);	
	    }
	    
	    drawSigil();
	    */
	    
	    
	    super.drawScreen(par1, par2, par3);
	    	    
	}
	
	/*
	private void drawSigil() {
		
		int offset = 15;
		this.drawRect(guiLeft+12+offset, guiTop+31, 64+guiLeft+12+offset, 64+guiTop+31,
				0xFF000000 | SigilHelper.colours[scrolls[1].current]);
	    
		mc.renderEngine.bindTexture(BattleGear.imageFolder+"/sigil/patterns/pattern-"+scrolls[0].current+".png");
	    float[] colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[scrolls[2].current]);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glColor4f(colour[2], colour[1], colour[0], 1);
	    this.drawTexturedModelRect(guiLeft+12+offset, guiTop+31, 64, 64);
	    
	    mc.renderEngine.bindTexture(BattleGear.imageFolder+"/sigil/icons/icon-"+"1"+"-0.png");
	    float[] colourIconPrimary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[scrolls[5].current]);
	    float[] colourIconSconondary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[scrolls[6].current]);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    int pattern = scrolls[4].current;
	    
	    
	    for(int i = 0; i < SigilHelper.patternPassess[pattern]; i++){
	    	float x = SigilHelper.patternSourceX[pattern][i];
	    	float y = SigilHelper.patternSourceY[pattern][i];
	    	float width = SigilHelper.patternWidth[pattern];
	    	boolean flip = SigilHelper.patternFlip[pattern][i];
	    	boolean flipColours = SigilHelper.patternAltColours[pattern][i];
	    	
	    	if(flipColours){
	    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
	    	}else{
	    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
	    	}
	    	
	    	this.drawTexturedModelRect(guiLeft+12+offset, guiTop+31, 64, 64, x,y,width, flip);
	    }
	    
	    
	    /*
	    colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[scrolls[6].current]);
	    GL11.glColor4f(colour[2], colour[1], colour[0], 1);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    this.drawTexturedModalRect(guiLeft+19, guiTop+38, 32, 96, 32, 32);
	    */
	//}

	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    mc.renderEngine.bindTexture(BattleGear.imageFolder+"gui/Sigil GUI.png");
	    this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
	}
	
	public void drawTexturedModelRect(int par1, int par2, int par4, int par5)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), (double)this.zLevel, 0, 1);
        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), (double)this.zLevel, 1, 1);
        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), (double)this.zLevel, 1, 0);
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, 0, 0);
        tessellator.draw();
    }
	
	public void drawTexturedModelRect(int par1, int par2, int par4, int par5, float sourceX, float sourceY, float width, boolean flip)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        if(flip){
        	tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), (double)this.zLevel, sourceX+width, sourceY+width);
            tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), (double)this.zLevel, sourceX, sourceY+width);
            tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), (double)this.zLevel, sourceX, sourceY);
            tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, sourceX+width, sourceY);
        }else{
	        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), (double)this.zLevel, sourceX, sourceY+width);
	        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), (double)this.zLevel, sourceX+width, sourceY+width);
	        tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), (double)this.zLevel, sourceX+width, sourceY);
	        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, sourceX, sourceY);
        }
        tessellator.draw();
    }
}
