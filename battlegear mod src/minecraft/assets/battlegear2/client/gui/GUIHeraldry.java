package assets.battlegear2.client.gui;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import java.awt.Color;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import assets.battlegear2.api.IHeraldyItem;
import assets.battlegear2.client.blocks.BannerBlockRenderer;
import assets.battlegear2.client.heraldry.HeraldryArmourModel;
import assets.battlegear2.client.heraldry.HeraldryIcon;
import assets.battlegear2.client.heraldry.HeraldryPositions;
import assets.battlegear2.client.heraldry.HeraldyPattern;
import assets.battlegear2.common.BattleGear;
import assets.battlegear2.common.BattlegearPacketHandeler;
import assets.battlegear2.common.blocks.TileEntityBanner;
import assets.battlegear2.common.gui.ContainerHeraldry;
import assets.battlegear2.common.heraldry.KnightArmourRecipie;
import assets.battlegear2.common.heraldry.SigilHelper;
import assets.battlegear2.common.items.ItemKnightArmour;
import assets.battlegear2.common.utils.BattlegearConfig;

import cpw.mods.fml.client.FMLClientHandler;
import guiToolkit.GUIAltButton;
import guiToolkit.GUIAltScroll;
import guiToolkit.GuiHSBColourPicker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringTranslate;

public class GUIHeraldry extends GuiContainer{
	
	private Minecraft mc = FMLClientHandler.instance().getClient();

	/** The X size of the window in pixels. */
	protected int xSize = 176;

	/** The Y size of the window in pixels. */
	protected int ySize = 190;
	
	private int sigilLeft;
	private int sigilTop;
	
	/**
     * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiLeft;
    
    BannerBlockRenderer bannerRenderer;

    /**
     * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiTop;
	
	private String[] displayString = new String[]{
			"sigil.pattern.title",
			"sigil.icon.title",
			"sigil.icon_pos.title",
			"sigil.banner.title",
			"sigil.helm.title",
			"sigil.colour1.title",
			"sigil.colour2.title",
			"sigil.icon_colour1.title",
			"sigil.icon_colour2.title",
	};
	
	private byte[][] bannerCodes;
	private ItemStack[] helmCodes;
	
	private static int [] preDefinedColours = new int[16];
	private int preDefinedIndex = -1;
	
	
	private String patternTitle = "sigil.pattern.title";
	private String iconTitle = "sigil.icon.title";
	
	private GUIAltScroll panelScroll;
	
	private BasicColourButton[] colourButtons;

	private EntityPlayer player;
	private boolean personal;
	private int panelId = -1;
	
	private GuiHSBColourPicker colourPicker;
	private GuiTextField colourTextField;
	private GUIAltButton selectColourButton;
	
	private HeraldryArmourModel armourModel;
	private static StringTranslate translator = new StringTranslate();
	public static final ResourceLocation panels = new ResourceLocation(BattleGear.imageFolder+"gui/Sigil GUI Panels.png");
	
	static{
		for(int i = 0; i < 16; i++){
			preDefinedColours[i] = (ItemDye.dyeColors[i] & 0xF0F0F0F0) | 0xFF0F0F0F;
		}
	}

	
	int rotation2 = 0;
	
	/**
	 * Creates a new GUI for creating and editing sigils.
	 * @param player
	 * @param personal
	 */
	public GUIHeraldry(EntityPlayer player, boolean personal, boolean remote){
		super(new ContainerHeraldry(player.inventory, !remote, player));
		this.player = player;
		this.personal = personal;
		this.allowUserInput = true;
		bannerRenderer = (BannerBlockRenderer) TileEntityRenderer.instance.getSpecialRendererForClass(TileEntityBanner.class);
		computeBannerAndHelmCodes();
	}
	
	private void computeBannerAndHelmCodes(){
		bannerCodes = new byte[4][];
		helmCodes = new ItemStack[4];
		byte[] code = getCode();
		for(byte i = 0; i < 4; i++){
			bannerCodes[i] = SigilHelper.updateBanner(code.clone(), i);
			helmCodes[i] = new ItemStack(BattlegearConfig.knightArmor[0]);
			((IHeraldyItem)helmCodes[i].getItem()).setHeraldryCode(helmCodes[i], SigilHelper.updateHelm(code.clone(), i));
		}
	}
	
	public void initGui()
    {
        super.initGui();

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        
        sigilLeft = guiLeft+10;
        sigilTop = guiTop+13;
                
        int controlsX = guiLeft+130;
        int controlsY = guiTop+27;
        //colourTextField = new GuiTextField(this.fontRenderer, guiLeft+xSize+12, guiTop+36+55, 14, 48);
        addDefaultButtons();
    }
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
	    panelScroll.enabled = panelId < 3 && panelId >-1;
	    panelScroll.drawButton = panelId < 3 && panelId >-1;
	    colourPicker.drawButton = colourPicker.enabled = panelId < 10 && panelId >=5;
	    
	    super.drawScreen(par1, par2, par3);
	    
	    if(par1 > sigilLeft && par1 < sigilLeft+64 && par2 > sigilTop && par2 < 7+sigilTop+64){
		    GL11.glPushMatrix();
		    GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glDisable(GL11.GL_DEPTH_TEST);
	        this.drawGradientRect(sigilLeft-1, sigilTop-1, sigilLeft + 66, sigilTop + 66, -2130706433, -2130706433);
	        GL11.glEnable(GL11.GL_LIGHTING);
	        GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glPopMatrix();
		}
	}
	
	
	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    mc.renderEngine.func_110577_a(BasicColourButton.sigilGui);//.bindTexture(BattleGear.imageFolder+"gui/Sigil GUI.png");
	    this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
	    
	    drawPanel();
	    
	    drawSigil();
	    
	   
	    
	    GL11.glPushMatrix();
	    float scale = 0.70F;
	    GL11.glScalef(scale, scale, scale);
	    this.drawString(fontRenderer, SigilHelper.bytesToHex(getCode()), (int)((10+guiLeft)*(1/scale)), (int)((93+guiTop)*(1/scale)), 0xFFFFFFFF);
	    GL11.glScalef(1F/scale, 1F/scale, 1F/scale);
	    GL11.glPopMatrix();
	}
	
	public byte[] getCode(){
		return ((ContainerHeraldry)inventorySlots).code;
	}
	
	
	private void drawSigil(){
		
		this.zLevel += 1000;
		int startX = sigilLeft;
		int startY = sigilTop;
		
		byte[] code = getCode();
		Color primaryColour = SigilHelper.getPrimaryColour(code);
		Color secondaryColour = SigilHelper.getSecondaryColour(code);
		HeraldyPattern pattern = SigilHelper.getPattern(code);
		HeraldryPositions position = SigilHelper.getSigilPosition(code);
		HeraldryIcon icon = SigilHelper.getSigil(code);
		Color sigilPrimaryColour = SigilHelper.getSigilPrimaryColour(code);
		Color sigilSecondaryColour = SigilHelper.getSigilSecondaryColour(code);
		
		this.drawRect(startX, startY, 64+startX, 64+startY, primaryColour.getRGB(), zLevel);
		GL11.glColor3f((float)secondaryColour.getRed()  / 256F,
				(float)secondaryColour.getGreen() / 256F,
				(float)secondaryColour.getBlue() / 256F);
	    //mc.renderEngine.bindTexture("/gui/items.png");
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
	    this.drawTexturedModelRectFromIcon(startX, startY, pattern.getIcon(), 64, 64);
	    
	    if(! HeraldryIcon.Blank.equals(icon)){
		    mc.renderEngine.func_110577_a(icon.getForegroundImage());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(flipColours){
		    		GL11.glColor3f((float)sigilSecondaryColour.getRed() / 256F, 
		    				(float)sigilSecondaryColour.getGreen()  / 256F, 
		    				(float)sigilSecondaryColour.getBlue()  / 256F);
		    	}else{
		    		GL11.glColor3f(
		    				(float)sigilPrimaryColour.getRed() / 256F,
		    				(float)sigilPrimaryColour.getGreen()  / 256F,
		    				(float)sigilPrimaryColour.getBlue()  / 256F);
		    	}
		    	
		    	this.drawTexturedModelRect(startX, startY, 64, 64, x,y, width, flip);
		    }
		    
		    mc.renderEngine.func_110577_a(icon.getBackgroundImage());
		    
		    for(int i = 0; i < position.getPassess(); i++){
		    	float x = position.getSourceX(i);
		    	float y = position.getSourceY(i);
		    	float width = position.getWidth();
		    	boolean flip = position.getPatternFlip(i);
		    	boolean flipColours = position.getAltColours(i);
		    	
		    	if(!flipColours){
		    		GL11.glColor3f((float)sigilSecondaryColour.getRed() / 256F, 
		    				(float)sigilSecondaryColour.getGreen()  / 256F, 
		    				(float)sigilSecondaryColour.getBlue()  / 256F);
		    	}else{
		    		GL11.glColor3f(
		    				(float)sigilPrimaryColour.getRed() / 256F,
		    				(float)sigilPrimaryColour.getGreen()  / 256F,
		    				(float)sigilPrimaryColour.getBlue()  / 256F);
		    	}
		    	
		    	this.drawTexturedModelRect(startX, startY, 64, 64, x,y,width, flip);
		    }
	    }
	    
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    mc.renderEngine.func_110577_a(BasicColourButton.sigilGui);//.bindTexture(BattleGear.imageFolder+"gui/Sigil GUI.png");
	    this.drawTexturedModalRect(startX-1, startY-1, 0, 190, 66, 66);
	    
	    GL11.glDisable(GL11.GL_BLEND);
	    
	    this.zLevel -= 1000;
	    
	}
	
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
			
			
			if( (panelId == 0  || panelId == 1  ||panelId == 2) && 
					xOff >= 8 && xOff <= 68 &&
					yOff >= 19 && yOff <= 19+166){
				panelScroll.current = Math.max(0, Math.min(99, panelScroll.current+(diff*10)));
				panelScroll.sliderValue = Math.max(0F, Math.min(.99999F, panelScroll.sliderValue + (float)(diff)/10F));
			}
			
		}
	}
	

	private void drawPanel() {
		 selectColourButton.enabled = selectColourButton.drawButton = false;
		    
		if(panelId > -1){
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		    mc.renderEngine.func_110577_a(panels);
		    
		   
			if(panelId < 3 && panelId >-1){
			    this.drawTexturedModalRect(guiLeft+5+xSize, guiTop, 0, 0, 80, 190);
			    
			    if(panelId == 0){
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					int start = (int)(panelScroll.sliderValue * 11);
					
					Color primaryColour = SigilHelper.getColour(getCode(), 0);
					Color secondaryColor = SigilHelper.getColour(getCode(), 1);
					int selectedPattern = SigilHelper.getPattern(getCode()).ordinal();
					for(int y = 0; y < 6; y++){
						int xStart = guiLeft+25+xSize;
						int yStart = y*27+guiTop+19;
						
						this.drawRect(guiLeft+25+xSize, y*27+guiTop+19, guiLeft+25+26+xSize, (y+1)*27+guiTop+18, primaryColour.getRGB());
					    GL11.glColor4f((float)secondaryColor.getRed() /255F,
					    			(float)secondaryColor.getGreen() /255F,
					    			(float)secondaryColor.getBlue() /255F, 1);
					    //mc.renderEngine.bindTexture("/gui/items.png");
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
				}
			    else if(panelId == 1){
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					Color primaryColour = SigilHelper.getColour(getCode(), 2);
					Color secondaryColor = SigilHelper.getColour(getCode(), 3);
					int selectedSigil = SigilHelper.getSigil(getCode()).ordinal();
					int start = (int)(panelScroll.sliderValue * (HeraldryIcon.values().length-5));
					for(int y = 0; y < 6; y++){
						
						
						int xStart = guiLeft+25+xSize;
						int yStart = y*27+guiTop+19;
											
						if(y+start < HeraldryIcon.values().length){
							HeraldryIcon selected = HeraldryIcon.values()[y+start];
							if(! HeraldryIcon.Blank.equals(selected)){
								GL11.glColor4f((float)primaryColour.getRed() /255F,
						    			(float)primaryColour.getGreen() /255F,
						    			(float)primaryColour.getBlue() /255F, 1);
								mc.renderEngine.func_110577_a(selected.getForegroundImage());
								GL11.glEnable(GL11.GL_BLEND);
							    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
							    this.drawTexturedModelRect(guiLeft+25+xSize, y*27+guiTop+19, 26, 26, 0, 0, 1, false);
							    
							    GL11.glColor4f((float)secondaryColor.getRed() /255F,
						    			(float)secondaryColor.getGreen() /255F,
						    			(float)secondaryColor.getBlue() /255F, 1);
								mc.renderEngine.func_110577_a(selected.getBackgroundImage());
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
					Color primaryColour = SigilHelper.getColour(getCode(), 2);
					Color secondaryColor = SigilHelper.getColour(getCode(), 3);
					int selectedPos = SigilHelper.getSigilPosition(getCode()).ordinal();
					HeraldryIcon selectedIcon = SigilHelper.getSigil(getCode()); 
					int start = (int)(panelScroll.sliderValue * 3);
					for(int y = 0; y < 6; y++){
						
						int xStart = guiLeft+25+xSize;
						int yStart = y*27+guiTop+19;
								
						if(y+start < HeraldryPositions.values().length){
							HeraldryPositions position = HeraldryPositions.values()[y+start];
							
							for(int pass = 0; pass < position.getPassess(); pass++){
								
								float xPos = position.getSourceX(pass);
						    	float yPos = position.getSourceY(pass);
						    	float width = position.getWidth();
						    	boolean flip = position.getPatternFlip(pass);
						    	boolean flipColours = position.getAltColours(pass);
						    	
						    	if(flipColours){
						    		 GL11.glColor4f((float)secondaryColor.getRed() /255F,
								    			(float)secondaryColor.getGreen() /255F,
								    			(float)secondaryColor.getBlue() /255F, 1);
						    	}else{
						    		GL11.glColor4f((float)primaryColour.getRed() /255F,
							    			(float)primaryColour.getGreen() /255F,
							    			(float)primaryColour.getBlue() /255F, 1);
						    	}
						    	
								mc.renderEngine.func_110577_a(selectedIcon.getForegroundImage());
								GL11.glEnable(GL11.GL_BLEND);
								GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
								this.drawTexturedModelRect(guiLeft+25+xSize, y*27+guiTop+19, 26, 26, xPos, yPos, width, flip);
								
								if(!flipColours){
									 GL11.glColor4f((float)secondaryColor.getRed() /255F,
								    			(float)secondaryColor.getGreen() /255F,
								    			(float)secondaryColor.getBlue() /255F, 1);
						    	}else{
						    		GL11.glColor4f((float)primaryColour.getRed() /255F,
							    			(float)primaryColour.getGreen() /255F,
							    			(float)primaryColour.getBlue() /255F, 1);
						    	}
								
								
								mc.renderEngine.func_110577_a(selectedIcon.getBackgroundImage());
								this.drawTexturedModelRect(guiLeft+25+xSize, y*27+guiTop+19, 26, 26, xPos, yPos, width, flip);
								
							}

							if(y+start == selectedPos){					
								
								this.drawRect(xStart-1, yStart-1, xStart+1, yStart+1+27, 0xFFFFFF00);
								this.drawRect(xStart-1, yStart-1+27, xStart+1+27, yStart+1+27, 0xFFFFFF00);
								
								this.drawRect(xStart-1, yStart-1, xStart+1+27, yStart+1, 0xFFFFFF00);
								this.drawRect(xStart-1+27, yStart-1, xStart+1+27, yStart+1+27, 0xFFFFFF00);
							
							}
						
						}
					}

				}
			    
			}else if (panelId != -1 && panelId < 5){
				this.drawTexturedModalRect(guiLeft+5+xSize, guiTop, 160, 0, 80, 190);
				
				if(panelId == 3){
					for(int i = 0; i < 4; i++){
						drawBannerOnGui(guiLeft+xSize+5+33, guiTop+46 + i*42, 15, 0, 0, bannerCodes[i]);
					}
					
					int banner = SigilHelper.getBanner(getCode());
					this.drawRect(23+guiLeft+xSize+5, 18+guiTop + (banner * 42), 23+guiLeft+xSize+5+36, 18+guiTop+2+(banner * 42), Color.YELLOW.getRGB());
					this.drawRect(23+guiLeft+xSize+5, 18+guiTop + (banner * 42)+34, 23+guiLeft+xSize+5+36, 18+guiTop+2+(banner * 42)+34, Color.YELLOW.getRGB());
					
					this.drawRect(23+guiLeft+xSize+5, 18+guiTop + (banner * 42), 23+guiLeft+xSize+5+2, 18+guiTop+2+(banner * 42)+34, Color.YELLOW.getRGB());
					this.drawRect(23+guiLeft+xSize+5+34, 18+guiTop + (banner * 42), 23+guiLeft+xSize+5+36, 18+guiTop+2+(banner * 42)+34, Color.YELLOW.getRGB());
					
					
				}else if (panelId == 4){
					for(int i = 0; i < 4; i++){
						
							drawHelmOnGui(mc, guiLeft+xSize+5+33+6, guiTop+27 + i*42, 2, 0, 0, helmCodes[i]);
					}
				}
				
			}else if(panelId < 10 && panelId >-1){
				this.drawTexturedModalRect(guiLeft+5+xSize, guiTop, 80, 0, 80, 190);
				//colourTextField.drawTextBox();
				//this.drawGradientRect(par1, par2, par3, par4, par5, par6)
				int rgb = colourPicker.getRGB();
				String colour = String.format("%x", SigilHelper.get12bitRGB(rgb)).toUpperCase();
				while(colour.length() < 3){
					colour = "0"+colour;
				}
				selectColourButton.enabled = selectColourButton.drawButton = true;
				this.drawString(fontRenderer, "#"+colour,  guiLeft+xSize+45, guiTop+36+55+25, 0xFFFFFFFF);
				this.drawRect(guiLeft+xSize+15, guiTop+36+52+25, guiLeft+xSize+40, guiTop+36+65+25, rgb);
				
				for(int i = 0; i < 8; i++){
					this.drawRect(9+guiLeft+xSize+5+(i*8), 30+guiTop, 9+guiLeft+5+xSize+(i*8)+8, 30+guiTop+8, preDefinedColours[i]);
					this.drawRect(9+guiLeft+xSize+5+(i*8), 30+guiTop+8, 9+guiLeft+5+xSize+(i*8)+8, 30+guiTop+16, preDefinedColours[i+8]);
				}
				
				if(preDefinedIndex >= 0){
					this.drawRect(
							9+guiLeft+xSize+4+((preDefinedIndex%8)*8),
							30+guiTop-1 + (preDefinedIndex/8)*8, 
							9+guiLeft+xSize+((preDefinedIndex%8)*8)+14, 
							30+guiTop+9 + (preDefinedIndex/8)*8,
							preDefinedColours[preDefinedIndex]);
				}
			}
			
			
			String[] split = translator.translateKey(displayString[panelId]).split("%n");
			for(int i = 0; i < split.length; i++){
				this.drawCenteredString(this.fontRenderer, split[i], guiLeft+43+xSize, guiTop+4+i*10, 0xFFFF40);
			}
			
			
		}
	}
	
	
	
	@Override
	protected void mouseClicked(int x, int y, int par3) {
		
		super.mouseClicked(x, y, par3);
		
		if(par3 == 0 && panelId != -1 && panelId < 3){
			int xOff = x - guiLeft - 5 - 8 - xSize;
			int yOff = y - guiTop - 19;
			
			if(panelId == 0){
				if(xOff >= 0 && xOff <=50 && yOff >= 0 && yOff <= 162){
					int selectedPattern = ((yOff/27) + (int)(panelScroll.sliderValue * 11));
					
					selectedPattern = Math.max(0, selectedPattern);
					selectedPattern = Math.min(selectedPattern, HeraldyPattern.values().length-1);
					setCode(SigilHelper.updatePattern(getCode(), HeraldyPattern.values()[selectedPattern]));
				}
			}else if(panelId == 1){
				if(xOff >= 0 && xOff <=50 && yOff >= 0 && yOff <= 162){
					int selectedSigil = (yOff/27) + (int)(panelScroll.sliderValue * (HeraldryIcon.values().length-5));
				
					selectedSigil = Math.max(0, selectedSigil);
					selectedSigil = Math.min(selectedSigil, HeraldryIcon.values().length-1);
					
					setCode(SigilHelper.updateSigil(getCode(), HeraldryIcon.values()[selectedSigil]));
				}
			}else if(panelId == 2){
				if(xOff >= 0 && xOff <=50 && yOff >= 0 && yOff <= 162){
					int selectedPosition = (yOff/27) + (int)(panelScroll.sliderValue * 3);
					
					selectedPosition = Math.max(0, selectedPosition);
					selectedPosition = Math.min(selectedPosition, HeraldryPositions.values().length-1);
					
					setCode(SigilHelper.updateSigilPos(getCode(), HeraldryPositions.values()[selectedPosition]));
				}
			}
		}else if (par3 == 0 && panelId < 5){
			int xOff = x - guiLeft - 5 - 22 - xSize;
			int yOff = y - guiTop - 17;
			
			if(xOff >= 0 && xOff <= 38 && yOff >=0 && yOff <= 4*42){
				if (yOff % 42 <= 38){
					setCode(SigilHelper.updateBanner(getCode(), (byte)(yOff/42)));
				}
			}
			
			
		}else if (par3 == 0 && panelId >= 5 && panelId < 10){
			int xOff = x - guiLeft - 5 - 7 - xSize;
			int yOff = y - guiTop - 30;
			
			
			if(xOff / 8 < 8 && xOff >= 0 && yOff < 17 && yOff >= 0){
				preDefinedIndex = (xOff / 8);
				if(yOff > 8)
					preDefinedIndex = preDefinedIndex+8;
				//setCode(SigilHelper.updateColour(getCode(), preDefinedColours[preDefinedIndex], panelId-5));
				colourPicker.setColour(preDefinedColours[preDefinedIndex]);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		
	}
	
	public void addDefaultButtons(){
		buttonList.clear();
		byte[] code = getCode();
		
		colourButtons = new BasicColourButton[4];
		
		int y = guiTop+6;
		buttonList.add(new GUIAltButton(0, guiLeft+80, y, 72, 18, translator.translateKey("sigil.pattern.title")));
		colourButtons[0] = new BasicColourButton(5, guiLeft+80+76, y, 9, 9, SigilHelper.getPrimaryColour(code));
		colourButtons[1] = new BasicColourButton(6, guiLeft+80+76, y+10, 9, 9, SigilHelper.getSecondaryColour(code));
		y+=20;
		buttonList.add(new GUIAltButton(1, guiLeft+80, y, 72, 18, translator.translateKey("sigil.icon.title")));
		colourButtons[2] = new BasicColourButton(7, guiLeft+80+76, y, 9, 9,  SigilHelper.getSigilPrimaryColour(code));
		colourButtons[3] = new BasicColourButton(8, guiLeft+80+76, y+10, 9, 9, SigilHelper.getSigilSecondaryColour(code));
		y+=20;
		buttonList.add(new GUIAltButton(2, guiLeft+80, y, 72+16, 18, translator.translateKey("sigil.icon_pos.title")));
		y+=20;
		buttonList.add(new GUIAltButton(3, guiLeft+80, y, 72+16, 18, translator.translateKey("sigil.banner.title")));
		y+=20;
		buttonList.add(new GUIAltButton(4, guiLeft+80, y, 72+16, 18, translator.translateKey("sigil.helm.title")));
		
		buttonList.add(colourButtons[0]);
		buttonList.add(colourButtons[1]);
		buttonList.add(colourButtons[2]);
		buttonList.add(colourButtons[3]);
		
		panelScroll = new GUIAltScroll(20, guiLeft+5+61+xSize, guiTop+17, 166, false, 0, 99);
		buttonList.add(panelScroll);
		
		
		colourPicker = new GuiHSBColourPicker(21, guiLeft+xSize+12, guiTop+36+25, Color.GREEN);
		
		selectColourButton = new GUIAltButton(22, guiLeft+xSize+12, guiTop+36+68+25, 64, 18, translator.translateKey("sigil.colour.select"));
		buttonList.add(selectColourButton);
		buttonList.add(colourPicker);
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		
		if(par1GuiButton.id < 10){
			if(panelId == par1GuiButton.id){
				panelId = -1;
			}else{
				panelId = par1GuiButton.id;
			}
			
			if(panelId >=5 && panelId<10){
				colourPicker.setColour(SigilHelper.getColour(getCode(), panelId-5));
			}
			
			preDefinedIndex = -1;
		}else if (par1GuiButton.id == 22 && panelId >=5 && panelId<10){ //select colour
			int newColour = colourPicker.getRGB();
			
			preDefinedIndex = -1;
			colourButtons[panelId-5].colour = new Color(newColour);
			setCode(SigilHelper.updateColour(getCode(), newColour, panelId-5));
		}
		super.actionPerformed(par1GuiButton);
	}
	
	
	
	private void setCode(byte[] newCode) {
		if(player instanceof EntityClientPlayerMP){
			((EntityClientPlayerMP)player).sendQueue.addToSendQueue(
					BattlegearPacketHandeler.generateHeraldryChangeGUIPacket(
							newCode, player));
			
			((ContainerHeraldry)inventorySlots).setCode(newCode);
		}
		
		this.computeBannerAndHelmCodes();
		
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
	
	
	/**
     * Draws a solid color rectangle with the specified coordinates and color. Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int par0, int par1, int par2, int par3, int par4, float zLevel)
    {
        int j1;

        if (par0 < par2)
        {
            j1 = par0;
            par0 = par2;
            par2 = j1;
        }

        if (par1 < par3)
        {
            j1 = par1;
            par1 = par3;
            par3 = j1;
        }

        float f = (float)(par4 >> 24 & 255) / 255.0F;
        float f1 = (float)(par4 >> 16 & 255) / 255.0F;
        float f2 = (float)(par4 >> 8 & 255) / 255.0F;
        float f3 = (float)(par4 & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)par0, (double)par3, zLevel);
        tessellator.addVertex((double)par2, (double)par3, zLevel);
        tessellator.addVertex((double)par2, (double)par1, zLevel);
        tessellator.addVertex((double)par0, (double)par1, zLevel);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    
    public void drawBannerOnGui(int x, int y, int scale, float rotation, float pitch, byte[] code)
    {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, 50.0F);
        GL11.glScalef((float)(-scale), (float)scale, (float)scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        
        
        
        
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float)Math.atan((double)(pitch / 40.0F))) * 20.0F + 20, 1.0F, 0.0F, 0.0F);
        
        
        bannerRenderer.renderBanner(code, 0, 0, 0, 0, true, true, -105);
        
        
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    
    public void drawHelmOnGui(Minecraft par0Minecraft, int x, int y, float scale, float rotation, float pitch, ItemStack stack)
    {
    	scale = 25;
    	 GL11.glEnable(GL11.GL_COLOR_MATERIAL);
         GL11.glPushMatrix();
         GL11.glTranslatef((float)x, (float)y, 50F);
         
         
         GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
         RenderHelper.enableStandardItemLighting();
         
         GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-((float)Math.atan((double)(pitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
         
         mc.renderEngine.func_110577_a(new ResourceLocation("battlegear2",((ItemKnightArmour)stack.getItem()).getArmorTexture(stack,null, 0, 0)));//.bindTexture(((ItemKnightArmour)stack.getItem()).getArmorTexture(stack,null, 0, 0));
         armourModel = new HeraldryArmourModel(0);
         //armourModel = (HeraldryArmourModel) ((ItemKnightArmour)stack.getItem()).getArmorModel(null, stack, 0);
         armourModel.setItemStack(stack);
         
         armourModel.bipedHead.showModel = true;

         armourModel.bipedHeadwear.showModel = false;
         armourModel.bipedBody.showModel = false;
         armourModel.bipedRightArm.showModel = false;
         armourModel.bipedLeftArm.showModel = false;
         armourModel.bipedRightLeg.showModel = false;
         armourModel.bipedLeftLeg.showModel = false;
         
         armourModel.helmOffset = 0.95F;
         GL11.glScalef((float)(-scale), (float)scale, (float)scale);
         
         armourModel.render(null, 0, 0, 0, (float)-140, 10, 0.0625F);
         
         GL11.glScalef(1F/(float)(-scale), 1F/(float)scale, 1F/(float)scale);
         armourModel.helmOffset = 0;
         
         GL11.glPopMatrix();
         RenderHelper.disableStandardItemLighting();
         GL11.glDisable(GL12.GL_RESCALE_NORMAL);
         OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GL11.glDisable(GL11.GL_TEXTURE_2D);
         OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    	
    }
    
    
    /**
     * Args: left, top, width, height, pointX, pointY. Note: left, top are local to Gui, pointX, pointY are local to
     * screen
     */
    protected boolean isPointInRegion(int par1, int par2, int par3, int par4, int par5, int par6)
    {
    	
    	if(par1 <= -1000){
    		
    		return par5 > sigilLeft && par5 < sigilLeft+64 &&
    				par6 > sigilTop && par6 < sigilTop+64;
    		
    	}else{
    		return super.isPointInRegion(par1, par2, par3, par4, par5, par6);
    	}
    	
    	
    }
}