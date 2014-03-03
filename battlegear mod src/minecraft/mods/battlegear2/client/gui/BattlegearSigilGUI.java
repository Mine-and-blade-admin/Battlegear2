package mods.battlegear2.client.gui;

import mods.battlegear2.api.heraldry.RefreshableTexture;
import mods.battlegear2.gui.ContainerHeraldry;
import mods.battlegear2.packet.BattlegearGUIPacket;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.opengl.GL11;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.client.BattlegearClientEvents;
import mods.battlegear2.client.gui.controls.GUICrestElementList;
import mods.battlegear2.client.gui.controls.GuiColourPicker;
import mods.battlegear2.client.gui.controls.GuiColourToggleButton;
import mods.battlegear2.client.gui.controls.GuiPatternScrollList;
import mods.battlegear2.client.gui.controls.GuiToggleButton;
import mods.battlegear2.client.gui.controls.IControlListener;
import mods.battlegear2.client.renderer.HeraldryCrestItemRenderer;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class BattlegearSigilGUI extends GuiContainer {

    private static final int ADD = 0;
    private static final int REMOVE = 1;
    private static final int PATTERN = 2;
    private static final int PAT_COL_1 = 3;
    private static final int PAT_COL_2 = 4;
    private static final int PAT_COL_3 = 5;
    private static final int COL_SELECT_PAT = 6;
    
    
    private static final int[] PAT_BUTTONS = new int[]{PATTERN,PAT_COL_1,PAT_COL_2,PAT_COL_3};
	
    private static int RES = 32;

    private GUICrestElementList elementList;
    private int selectedIndex;

    private HeraldryData currentData = HeraldryData.getDefault();

    private GuiButton addButton;
    private GuiButton removeButton;
    private GuiToggleButton[] patternToggleButtons;
    private GuiColourPicker colourPickerPattern;
    private GuiPatternScrollList scrollListPattern;

    private final RefreshableTexture currentCrest = new RefreshableTexture(RES, RES);
    private boolean crestDirty = true;
    
    public BattlegearSigilGUI(EntityPlayer entityPlayer, boolean isRemote){
        super(new ContainerHeraldry(entityPlayer.inventory, !isRemote, entityPlayer));
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        this.elementList=new GUICrestElementList(this, 125, (this.width-125) / 2);

        addButton = new GuiButton(ADD, (this.width-125) / 2+2, 5, 60, 20, StatCollector.translateToLocal("gui.sigil.add"));
        removeButton = new GuiButton(REMOVE, (this.width-125)/2 + 63, 5, 60, 20, StatCollector.translateToLocal("gui.sigil.remove"));

        buttonList.add(addButton);
        buttonList.add(removeButton);
        addButton.enabled = elementList.getSize() < HeraldryData.MAX_CRESTS+1;
        removeButton.enabled = selectedIndex != 0;
        
        patternToggleButtons = new GuiToggleButton[PAT_BUTTONS.length];
        
        int prevIndexSelected = 0;
        if(patternToggleButtons != null){
        	for(int i = 0; i < patternToggleButtons.length; i++){
        		if(patternToggleButtons[i] != null && patternToggleButtons[i].getSelected()){
        			prevIndexSelected = i;
        		}
        	}
        }
        
        patternToggleButtons[0] = new GuiToggleButton(PAT_BUTTONS[0], (width+160)/2, 15, 65, 20, StatCollector.translateToLocal("gui.sigil.pattern"));
    	patternToggleButtons[0].enabled = selectedIndex == 0;
    	patternToggleButtons[0].visible = selectedIndex == 0;
    	buttonList.add(patternToggleButtons[0]);
   
        for(int i = 1; i < patternToggleButtons.length; i++){
        	patternToggleButtons[i] = new GuiColourToggleButton(PAT_BUTTONS[i], (width+160)/2+66 + 16*(i-1), 18, currentData.getColour(i-1));
        	patternToggleButtons[i].enabled = selectedIndex == 0;
        	patternToggleButtons[i].visible = selectedIndex == 0;
        	buttonList.add(patternToggleButtons[i]);
        }
        patternToggleButtons[prevIndexSelected].setSelected(true);
        colourPickerPattern = new GuiColourPicker(COL_SELECT_PAT, (width+200)/2, 45, 0xFF000000, GuiColourPicker.COLOUR_DISPLAY|GuiColourPicker.DEFAULT_COLOURS);
        
        colourPickerPattern.visible = colourPickerPattern.enabled = selectedIndex == 0 && !patternToggleButtons[0].getSelected();
        colourPickerPattern.addListener(new IControlListener() {
			@Override
			public void actionPreformed(GuiButton button) {
				int selectedToggle = 1;
            	for(int i = 1; i < patternToggleButtons.length; i++){
            		if(patternToggleButtons[i].getSelected()){
            			selectedToggle = i;
            		}
            	}
            	((GuiColourToggleButton)patternToggleButtons[selectedToggle]).setColour(colourPickerPattern.getRGB());
            	currentData.setColour(selectedToggle-1, colourPickerPattern.getRGB());
            	elementList.markDirty(0);
            	crestDirty = true;
			}
		});
        buttonList.add(colourPickerPattern);
        
        scrollListPattern = new GuiPatternScrollList(this, 125, 45, height-32, (width+160)/2);
        BattlegearClientEvents.onOpenGui(buttonList, guiLeft-100, 5);
    }

    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }
    
    private void enableButtons(){
    	removeButton.enabled = selectedIndex!=0;
    	
    	for(int i = 0; i < patternToggleButtons.length; i++){
        	patternToggleButtons[i].enabled = selectedIndex==0;
        	patternToggleButtons[i].visible = selectedIndex==0;
        }
        
        colourPickerPattern.enabled = colourPickerPattern.visible = 
        		selectedIndex ==0 & !patternToggleButtons[0].getSelected();
    	
        addButton.enabled = elementList.getSize() < HeraldryData.MAX_CRESTS+1;
    	
        scrollListPattern.drawList = selectedIndex == 0 & patternToggleButtons[0].getSelected();
    }

    public void select(int index){
        selectedIndex = index;
        enableButtons();
        crestDirty = true;
    }

    public int getSelectedIndex(){
        return selectedIndex;
    }


    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();


        if(elementList != null)
            this.elementList.drawScreen(par1, par2, par3);
        
        if(scrollListPattern != null){
        	scrollListPattern.drawScreen(par1, par2, par3);
        }
        
        this.drawButtonControlPanel();

        super.drawScreen(par1, par2, par3);
        
        this.drawCrest();

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

    }


    private void drawButtonControlPanel() {
    	drawRect((width+150)/2, 0, (width+400)/2, height, 0x44000000);
	}

	public HeraldryData getCurrentData() {
        return currentData;
    }


    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
                
        crestDirty = true;
        switch (button.id){
            case ADD:
                elementList.addNewCrest();
                break;
            case REMOVE:
                elementList.removeCrest(selectedIndex);
                select(selectedIndex-1);
                break;
            case PAT_COL_1:
            case PAT_COL_2:
            case PAT_COL_3:
            case PATTERN:
            	for(int i = 0; i < patternToggleButtons.length; i++){
            		patternToggleButtons[i].setSelected((button.id == patternToggleButtons[i].id));
            	}
            	colourPickerPattern.visible = button.id != PATTERN;
            	colourPickerPattern.enabled = button.id != PATTERN;
            	if(button.id!=PATTERN)
            		colourPickerPattern.selectColour(((GuiColourToggleButton)button).getColour());
            	break;
            case COL_SELECT_PAT:
            	
            	break;
        }
        
        enableButtons();

    }

	public void markAllDirty() {
		for(int i = 0; i < HeraldryData.MAX_CRESTS+1; i++)
			elementList.markDirty(i);
		
		scrollListPattern.markAllDirty();
		
		crestDirty = true;
	}

	private void drawCrest(){
		GL11.glColor4f(1, 1, 1, 1);
		Minecraft.getMinecraft().renderEngine.bindTexture(HeraldryCrestItemRenderer.map_overlay);
		

        drawTexturedModalRect((width-400)/2, 50, 128, 128, 0, 0, 1, 1);

        if(crestDirty){
            currentCrest.refreshWith(currentData, true);
        	crestDirty = false;
        }
        
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);


        currentCrest.updateDynamicTexture();
        ResourceLocation rl = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("gui_crest", currentCrest);
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        drawTexturedModalRect((width-400)/2 + 16, 50+16, 96, 96, 0, 0, 1, 1);


        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);

	}
	
	
    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect(int x, int y, int width, int height, int tex_x, int tex_y, int tex_width, int tex_height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)this.zLevel, (double)((float)(tex_x + 0)), (double)((float)(tex_y + tex_height)));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((float)(tex_x + tex_width)), (double)((float)(tex_y + tex_height)));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel, (double)((float)(tex_x + tex_width) ), (double)((float)(tex_y + 0)));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(tex_x + 0) ), (double)((float)(tex_y + 0)));
        tessellator.draw();
    }

	public static void open(EntityPlayer player){
		//send packet to open container on server
        Battlegear.packetHandler.sendPacketToServer(new BattlegearGUIPacket(BattlegearGUIHandeler.sigilEditor).generatePacket());
	}


}
