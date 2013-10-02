package mods.battlegear2.client.gui;


import mods.battlegear2.api.heraldry.HeraldryData;
import mods.battlegear2.client.gui.controls.GUICrestElementList;
import mods.battlegear2.client.gui.controls.GUIScrollList;
import mods.battlegear2.client.gui.controls.GuiColourPicker;
import mods.battlegear2.client.gui.controls.GuiColourToggleButton;
import mods.battlegear2.client.gui.controls.GuiPatternScrollList;
import mods.battlegear2.client.gui.controls.GuiToggleButton;
import mods.battlegear2.client.gui.controls.IControlListener;
import mods.mud.gui.GuiSlotModList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public class BattlegearSigilGUI extends GuiScreen{


    private static final int ADD = 0;
    private static final int REMOVE = 1;
    private static final int PATTERN = 2;
    private static final int PAT_COL_1 = 3;
    private static final int PAT_COL_2 = 4;
    private static final int PAT_COL_3 = 5;
    private static final int COL_SELECT_PAT = 6;
    
    
    private static final int[] PAT_BUTTONS = new int[]{PATTERN,PAT_COL_1,PAT_COL_2,PAT_COL_3};

    private GUICrestElementList elementList;
    private int selectedIndex;

    private HeraldryData currentData = HeraldryData.defaultData;

    private GuiButton addButton;
    private GuiButton removeButton;
    private GuiToggleButton[] patternToggleButtons;
    private GuiColourPicker colourPickerPattern;
    private GuiPatternScrollList scrollListPattern;


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
    	patternToggleButtons[0].drawButton = selectedIndex == 0;
    	buttonList.add(patternToggleButtons[0]);
   
        for(int i = 1; i < patternToggleButtons.length; i++){
        	patternToggleButtons[i] = new GuiColourToggleButton(PAT_BUTTONS[i], (width+160)/2+66 + 16*(i-1), 18, currentData.getColour(i-1));
        	patternToggleButtons[i].enabled = selectedIndex == 0;
        	patternToggleButtons[i].drawButton = selectedIndex == 0;
        	buttonList.add(patternToggleButtons[i]);
        }
        patternToggleButtons[prevIndexSelected].setSelected(true);
        colourPickerPattern = new GuiColourPicker(COL_SELECT_PAT, (width+200)/2, 45, 0xFF000000, GuiColourPicker.COLOUR_DISPLAY|GuiColourPicker.DEFAULT_COLOURS);
        
        colourPickerPattern.drawButton = colourPickerPattern.enabled = selectedIndex == 0 && !patternToggleButtons[0].getSelected();
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
			}
		});
        buttonList.add(colourPickerPattern);
        
        scrollListPattern = new GuiPatternScrollList(this, 125, 45, height-32, (width+160)/2);
        
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }
    
    private void enableButtons(){
    	removeButton.enabled = selectedIndex!=0;
    	
    	for(int i = 0; i < patternToggleButtons.length; i++){
        	patternToggleButtons[i].enabled = selectedIndex==0;
        	patternToggleButtons[i].drawButton = selectedIndex==0;
        }
        
        colourPickerPattern.enabled = colourPickerPattern.drawButton = 
        		selectedIndex ==0 & !patternToggleButtons[0].getSelected();
    	
        addButton.enabled = elementList.getSize() < HeraldryData.MAX_CRESTS+1;
    	
        scrollListPattern.drawList = selectedIndex == 0 & patternToggleButtons[0].getSelected();
    }

    public void select(int index){
        selectedIndex = index;
        enableButtons();
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
        //this.drawPanel();

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
            	colourPickerPattern.drawButton = button.id != PATTERN;
            	colourPickerPattern.enabled = button.id != PATTERN;
            	if(button.id!=PATTERN)
            		colourPickerPattern.selectColour(((GuiColourToggleButton)button).getColour());
            	break;
            case COL_SELECT_PAT:
            	
            	break;
        }
        
        enableButtons();

    }


}
