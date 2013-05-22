package mods.battlegear2.client.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import extendedGUI.BasicColourButton;
import extendedGUI.GUIAltButton;
import extendedGUI.GUIAltScroll;
import mods.battlegear2.client.heraldry.HeraldryIcon;
import mods.battlegear2.client.heraldry.HeraldryItemRenderer;
import mods.battlegear2.client.heraldry.HeraldyPattern;
import mods.battlegear2.client.heraldry.HeraldryPositions;
import mods.battlegear2.common.BattleGear;
import mods.battlegear2.common.BattlegearPacketHandeler;
import mods.battlegear2.common.gui.ContainerHeraldry;
import mods.battlegear2.common.heraldry.SigilHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetServerHandler;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringTranslate;

public class GuiHeraldry2 extends GuiContainer{
	
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
	
	private String[] displayString = new String[]{
			"sigil.pattern.title",
			"sigil.icon.title",
			"sigil.icon_pos.title",
			"",
			"",
			"sigil.colour1.title",
			"sigil.colour2.title",
			"sigil.icon_colour1.title",
			"sigil.icon_colour2.title",
	};
	
	private String patternTitle = "sigil.pattern.title";
	private String iconTitle = "sigil.icon.title";
	
	private GUIAltScroll panelScroll;
	
	private BasicColourButton[] colourButtons;
	
	private int selectedPattern = SigilHelper.getPattern(SigilHelper.defaultSigil);
	private int selectedSigil = SigilHelper.getIcon(SigilHelper.defaultSigil);
	private int selectedPosition = SigilHelper.getIconPos(SigilHelper.defaultSigil);
	
	private int[] selectedColours = new int[]{
			SigilHelper.getColour1(SigilHelper.defaultSigil),
			SigilHelper.getColour2(SigilHelper.defaultSigil),
			SigilHelper.getIconColour1(SigilHelper.defaultSigil),
			SigilHelper.getIconColour2(SigilHelper.defaultSigil)};
	
	private GUIAltScroll patternScroll;
	private BasicColourButton[] patternColours;
	private GUIAltScroll iconScroll;
	private GUIAltScroll positionScroll;
	private BasicColourButton[] iconColours;
	
	
	private EntityPlayer player;
	private boolean personal;
	
	private int panelId = -1;
	
	/**
	 * Creates a new GUI for creating and editing sigils.
	 * @param player
	 * @param personal
	 */
	public GuiHeraldry2(EntityPlayer player, boolean personal, boolean remote){
		super(new ContainerHeraldry(player.inventory, !remote, player));
		this.player = player;
		this.personal = personal;
		this.allowUserInput = true;
	}
	
	public void initGui()
    {
        super.initGui();

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
                
        int controlsX = guiLeft+130;
        int controlsY = guiTop+27;
        
        addDefaultButtons();
    }

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
	    
	    //this.drawCenteredString(fontRenderer, patternTitle, this.guiLeft+130+45, 12+guiTop, 0xFFFFFF);
	    //this.drawCenteredString(fontRenderer, iconTitle, this.guiLeft+130+45, 12+guiTop+16*4, 0xFFFFFF);

	    mc.renderEngine.bindTexture(BattleGear.imageFolder+"/gui/Sigil GUI 2.png");
	    	    
	    panelScroll.enabled = panelId < 5 && panelId >-1;
	    panelScroll.drawButton = panelId < 5 && panelId >-1;
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
	    */
	    super.drawScreen(par1, par2, par3);
	}
	
	private void drawPanel() {
		
		if(panelId > -1){
			if(panelId < 5 && panelId >-1){
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			    mc.renderEngine.bindTexture(BattleGear.imageFolder+"gui/Sigil GUI 2.png");
			    this.drawTexturedModalRect(guiLeft+5+xSize, guiTop, 176, 0, 80, 190);
			}else if(panelId < 10 && panelId >-1){
				
				
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			    mc.renderEngine.bindTexture(BattleGear.imageFolder+"gui/Sigil GUI 2.png");
			    this.drawTexturedModalRect(guiLeft+5+xSize, guiTop, 176, 190, 80, 66);
			    
			    for(int y = 0; y < 4; y++){
			    	for(int x = 0; x < 8; x++){
			    		
			    		if(x+y*8 < SigilHelper.colours.length){
				    		this.drawRect(guiLeft+5+8+xSize+x*8, guiTop+26+y*8,
				    				guiLeft+5+16+xSize+x*8, guiTop+26+8+y*8, 
				    				0xFF000000 | SigilHelper.colours[x+y*8]);
			    		}
			    	}
			    }
			    
			    int index = selectedColours[panelId-5];
			    
			    int indexX = index % 8;
			    int indexY = index / 8;
			    
			    this.drawRect(guiLeft+5+8+xSize+indexX*8-2, guiTop+26+indexY*8 - 2,
				    				guiLeft+5+16+xSize+indexX*8+2, guiTop+26+8+indexY*8+2, 
				    				0xFF000000 | SigilHelper.colours[index]);
			    
			    
			}
			
			if(panelId == 0){
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				int start = (int)(panelScroll.sliderValue * 11);
				for(int y = 0; y < 6; y++){
					
					int primaryColour = 0xFF000000 | SigilHelper.colours[selectedColours[0]];
					int secondaryColour = 0xFF000000 | SigilHelper.colours[selectedColours[1]];
					
					int xStart = guiLeft+25+xSize;
					int yStart = y*27+guiTop+19;
					
					this.drawRect(guiLeft+25+xSize, y*27+guiTop+19, guiLeft+25+26+xSize, (y+1)*27+guiTop+18, primaryColour);
					
				    float[] colour = SigilHelper.convertColourToARGBArray(secondaryColour);
				    GL11.glColor4f(colour[2], colour[1], colour[0], 1);
				    mc.renderEngine.bindTexture("/gui/items.png");
				    GL11.glEnable(GL11.GL_BLEND);
				    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				    this.drawTexturedModelRectFromIcon(guiLeft+25+xSize, y*27+guiTop+19, HeraldyPattern.values()[y+start].getIcon(), 26, 26);
				    GL11.glDisable(GL11.GL_BLEND);
				    
				    if(y+start == selectedPattern){					
						
						this.drawRect(xStart-1, yStart-1, xStart+1, yStart+1+27, 0xFFFFFF00);
						this.drawRect(xStart-1, yStart-1+27, xStart+1+27, yStart+1+27, 0xFFFFFF00);
						
						this.drawRect(xStart-1, yStart-1, xStart+1+27, yStart+1, 0xFFFFFF00);
						this.drawRect(xStart-1+27, yStart-1, xStart+1+27, yStart+1+27, 0xFFFFFF00);
					
					}
				}
			}else if(panelId == 1){
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				int start = (int)(panelScroll.sliderValue * (HeraldryIcon.values().length-5));
				for(int y = 0; y < 6; y++){
					
					int primaryColour = 0xFF000000 | SigilHelper.colours[selectedColours[2]];
					int secondaryColour = 0xFF000000 | SigilHelper.colours[selectedColours[3]];
					
					int xStart = guiLeft+25+xSize;
					int yStart = y*27+guiTop+19;
										
					if(y+start < HeraldryIcon.values().length){
						HeraldryIcon selected = HeraldryIcon.values()[y+start];
						if(! HeraldryIcon.Blank.equals(selected)){
							float[] colour = SigilHelper.convertColourToARGBArray(primaryColour);
							GL11.glColor4f(colour[2], colour[1], colour[0], 1);
							mc.renderEngine.bindTexture(selected.getForegroundImagePath());
							GL11.glEnable(GL11.GL_BLEND);
						    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
						    this.drawTexturedModelRect(guiLeft+25+xSize, y*27+guiTop+19, 26, 26, 0, 0, 1, false);
						    
						    colour = SigilHelper.convertColourToARGBArray(secondaryColour);
							GL11.glColor4f(colour[2], colour[1], colour[0], 1);
							mc.renderEngine.bindTexture(selected.getBackgroundImagePath());
							this.drawTexturedModelRect(guiLeft+25+xSize, y*27+guiTop+19, 26, 26, 0, 0, 1, false);
						}
					}
					
					if(y+start == selectedSigil){					
						
						this.drawRect(xStart-1, yStart-1, xStart+1, yStart+1+27, 0xFFFFFF00);
						this.drawRect(xStart-1, yStart-1+27, xStart+1+27, yStart+1+27, 0xFFFFFF00);
						
						this.drawRect(xStart-1, yStart-1, xStart+1+27, yStart+1, 0xFFFFFF00);
						this.drawRect(xStart-1+27, yStart-1, xStart+1+27, yStart+1+27, 0xFFFFFF00);
					
					}
					
				}
			}else if(panelId == 2){
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				int start = (int)(panelScroll.sliderValue * 3);
				for(int y = 0; y < 6; y++){
					
					int primaryColour = 0xFF000000 | SigilHelper.colours[selectedColours[2]];
					int secondaryColour = 0xFF000000 | SigilHelper.colours[selectedColours[3]];
					
					int xStart = guiLeft+25+xSize;
					int yStart = y*27+guiTop+19;
					
					float[] colourIconPrimary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[selectedColours[2]]);
				    float[] colourIconSconondary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[selectedColours[3]]);
										
					if(y+start < HeraldryPositions.values().length){

						HeraldryIcon selectedIcon = HeraldryIcon.values()[selectedSigil];
						HeraldryPositions position = HeraldryPositions.values()[y+start];
						
						for(int pass = 0; pass < position.getPassess(); pass++){
							
							float xPos = position.getSourceX(pass);
					    	float yPos = position.getSourceY(pass);
					    	float width = position.getWidth();
					    	boolean flip = position.getPatternFlip(pass);
					    	boolean flipColours = position.getAltColours(pass);
					    	
					    	if(flipColours){
					    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
					    	}else{
					    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
					    	}
					    	
							mc.renderEngine.bindTexture(selectedIcon.getForegroundImagePath());
							GL11.glEnable(GL11.GL_BLEND);
							GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
							this.drawTexturedModelRect(guiLeft+25+xSize, y*27+guiTop+19, 26, 26, xPos, yPos, width, flip);
							
							if(!flipColours){
					    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
					    	}else{
					    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
					    	}
							
							
							mc.renderEngine.bindTexture(selectedIcon.getBackgroundImagePath());
							this.drawTexturedModelRect(guiLeft+25+xSize, y*27+guiTop+19, 26, 26, xPos, yPos, width, flip);
							
						}

						if(y+start == selectedPosition){					
							
							this.drawRect(xStart-1, yStart-1, xStart+1, yStart+1+27, 0xFFFFFF00);
							this.drawRect(xStart-1, yStart-1+27, xStart+1+27, yStart+1+27, 0xFFFFFF00);
							
							this.drawRect(xStart-1, yStart-1, xStart+1+27, yStart+1, 0xFFFFFF00);
							this.drawRect(xStart-1+27, yStart-1, xStart+1+27, yStart+1+27, 0xFFFFFF00);
						
						}
					
					}
				}

			}
			//GL11.glColor4f(1.0F, 1.0F, 0.0F, 1.0F);
			
			
			String[] split = StringTranslate.getInstance().translateKey(displayString[panelId]).split("%n");
			for(int i = 0; i < split.length; i++){
				this.drawCenteredString(this.fontRenderer, split[i], guiLeft+43+xSize, guiTop+4+i*10, 0xFFFF40);
			}
		}
			
	}
	
	

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		
		int diff = Mouse.getDWheel();
		if(Mouse.hasWheel() && diff != 0){
			
			int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
	        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
	        
			int xOff = i - guiLeft - 5 - xSize;
			int yOff =j - guiTop;
			
			
			if(diff > 1)
				diff = 1;
			if(diff < -1)
				diff = -1;
			
			diff = -diff;
			
			if(panelId == 0 && 
					xOff >= 8 && xOff <= 68 &&
					yOff >= 19 && yOff <= 19+166){
				panelScroll.current = Math.max(0, Math.min(99, panelScroll.current+(diff*10)));
				panelScroll.sliderValue = Math.max(0F, Math.min(.99999F, panelScroll.sliderValue + (float)(diff)/10F));
			}
			
		}
	}

	public void addDefaultButtons(){
		buttonList.clear();
		
		colourButtons = new BasicColourButton[4];
		
		int y = guiTop+9;
		buttonList.add(new GUIAltButton(0, guiLeft+78, y, 90, 18, "Pattern"));
		y+=20;
		colourButtons[0] = new BasicColourButton(5, guiLeft+78, y, 45, 16, SigilHelper.colours[selectedColours[0]]);
		colourButtons[1] = new BasicColourButton(6, guiLeft+78+45, y, 45, 16, SigilHelper.colours[selectedColours[1]]);
		y+=20;
		buttonList.add(new GUIAltButton(1, guiLeft+78, y, 90, 18, "Sigil"));
		y+=20;
		buttonList.add(new GUIAltButton(2, guiLeft+78, y, 90, 18, "Sigil Position"));
		y+=20;
		colourButtons[2] = new BasicColourButton(7, guiLeft+78, y, 45, 16, SigilHelper.colours[selectedColours[2]]);
		colourButtons[3] = new BasicColourButton(8, guiLeft+78+45, y, 45, 16, SigilHelper.colours[selectedColours[3]]);
		
		buttonList.add(colourButtons[0]);
		buttonList.add(colourButtons[1]);
		buttonList.add(colourButtons[2]);
		buttonList.add(colourButtons[3]);
		
		panelScroll = new GUIAltScroll(20, guiLeft+5+61+xSize, guiTop+17, 166, false, 0, 99);
		buttonList.add(panelScroll);
	}
	
	private void drawSigil(){
		int startX = 7 + guiLeft;
		int startY = 11 + guiTop;
		
		int primaryPattern = 0xFF000000 | SigilHelper.colours[selectedColours[0]];
		float[] secondaryPattern = SigilHelper.convertColourToARGBArray(SigilHelper.colours[selectedColours[1]]);
		
		this.drawRect(startX, startY, 64+startX, 64+startY, primaryPattern);
		
		GL11.glColor4f(secondaryPattern[2], secondaryPattern[1], secondaryPattern[0], 1);
	    mc.renderEngine.bindTexture("/gui/items.png");
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    this.drawTexturedModelRectFromIcon(startX, startY, HeraldyPattern.values()[selectedPattern].getIcon(), 64, 64);

	    HeraldryPositions position = HeraldryPositions.values()[selectedPosition];
	    
	    float[] colourIconPrimary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[selectedColours[2]]);
	    float[] colourIconSconondary = SigilHelper.convertColourToARGBArray(SigilHelper.colours[selectedColours[3]]);
	    
	    if(! HeraldryIcon.Blank.equals(HeraldryIcon.values()[selectedSigil])){
		    mc.renderEngine.bindTexture(HeraldryIcon.values()[selectedSigil].getForegroundImagePath());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(flipColours){
		    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
		    	}else{
		    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
		    	}
		    	
		    	this.drawTexturedModelRect(startX, startY, 64, 64, x,y, width, flip);
		    }
		    
		    mc.renderEngine.bindTexture(HeraldryIcon.values()[selectedSigil].getBackgroundImagePath());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(! flipColours){
		    		GL11.glColor4f(colourIconSconondary[2], colourIconSconondary[1], colourIconSconondary[0], 1);
		    	}else{
		    		GL11.glColor4f(colourIconPrimary[2], colourIconPrimary[1], colourIconPrimary[0], 1);
		    	}
		    	
		    	this.drawTexturedModelRect(startX, startY, 64, 64, x,y,width, flip);
		    }
	    }
	    
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    mc.renderEngine.bindTexture(BattleGear.imageFolder+"gui/Sigil GUI 2.png");
	    this.drawTexturedModalRect(startX-1, startY-1, 0, 190, 66, 66);
	    
	    GL11.glDisable(GL11.GL_BLEND);
	    
	}

	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    mc.renderEngine.bindTexture(BattleGear.imageFolder+"gui/Sigil GUI 2.png");
	    this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
	    
	    drawPanel();
	    
	    drawSigil();
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

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		
		if(par1GuiButton.id < 10){
			if(panelId == par1GuiButton.id){
				panelId = -1;
			}else{
				panelId = par1GuiButton.id;
			}
		}
		
		super.actionPerformed(par1GuiButton);
	}
	
	

	@Override
	protected void mouseClicked(int x, int y, int par3) {
		super.mouseClicked(x, y, par3);

		
		if(par3 == 0 && panelId != -1 && panelId < 5){
			int xOff = x - guiLeft - 5 - 8 - xSize;
			int yOff = y - guiTop - 19;
			
			if(panelId == 0){
				if(xOff >= 0 && xOff <=50 && yOff >= 0 && yOff <= 162){
					selectedPattern = (yOff/27) + (int)(panelScroll.sliderValue * 11);
					
					selectedPattern = Math.max(0, selectedPattern);
					selectedPattern = Math.min(selectedPattern, HeraldyPattern.values().length-1);
				}
			}else if(panelId == 1){
				if(xOff >= 0 && xOff <=50 && yOff >= 0 && yOff <= 162){
					selectedSigil = (yOff/27) + (int)(panelScroll.sliderValue * (HeraldryIcon.values().length-5));
				}
				
				selectedSigil = Math.max(0, selectedSigil);
				selectedSigil = Math.min(selectedSigil, HeraldryIcon.values().length-1);
			}else if(panelId == 2){
				if(xOff >= 0 && xOff <=50 && yOff >= 0 && yOff <= 162){
					selectedPosition = (yOff/27) + (int)(panelScroll.sliderValue * 3);
				}
				
				selectedPosition = Math.max(0, selectedPosition);
				selectedPosition = Math.min(selectedPosition, HeraldryPositions.values().length-1);
			}
			
			
			if(player instanceof EntityClientPlayerMP){
				System.out.println("Add to send queue");
				((EntityClientPlayerMP)player).sendQueue.addToSendQueue(
						BattlegearPacketHandeler.generateHeraldryChangeGUIPacket(
								SigilHelper.packSigil(
										selectedPattern, selectedColours[0], selectedColours[1],
										selectedSigil, selectedColours[2],selectedColours[3], selectedPosition),
										player));
			}
		}
		
		if(panelId >= 5 && panelId < 10){
			int xOff = x - guiLeft - 5 - 8 - xSize;
			int yOff = y - guiTop - 28;
			
			if(xOff >= 0 && xOff <=64 && yOff >= 0 && yOff <= 40){
				int xVal = xOff / 8;
				int yVal = yOff / 8;
								
				if(xVal+yVal*8 < SigilHelper.colours.length){
					selectedColours[panelId - 5] = xVal+yVal*8;
					colourButtons[panelId - 5].colour = SigilHelper.colours[selectedColours[panelId - 5]];
				}

				if(player instanceof EntityClientPlayerMP){
					((EntityClientPlayerMP)player).sendQueue.addToSendQueue(
							BattlegearPacketHandeler.generateHeraldryChangeGUIPacket(
									SigilHelper.packSigil(
											selectedPattern, selectedColours[0], selectedColours[1],
											selectedSigil, selectedColours[2],selectedColours[3], selectedPosition),
											player));
				}
			}
		}
	}

	@Override
	protected void mouseMovedOrUp(int par1, int par2, int par3) {
		super.mouseMovedOrUp(par1, par2, par3);

		if(!Mouse.isButtonDown(0)){
			panelScroll.dragging = false;
		}
	}

	
	
	
	
		
}
