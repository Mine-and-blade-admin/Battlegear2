package mods.battlegear2.client.gui;


import mods.battlegear2.api.heraldry.HeraldryData;
import mods.mud.gui.GuiSlotModList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

public class BattlegearSigilGUI extends GuiScreen{


    private static final int ADD = 0;
    private static final int REMOVE = 1;

    private GUICrestElementList elementList;
    private int selectedIndex;

    private HeraldryData currentData = HeraldryData.defaultData;

    private GuiButton addButton;
    private GuiButton removeButton;


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
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public void select(int index){
        selectedIndex = index;

        removeButton.enabled = index!=0;
    }

    public int getSelectedIndex(){
        return selectedIndex;
    }


    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();


        if(elementList != null)
            this.elementList.drawScreen(par1, par2, par3);

        super.drawScreen(par1, par2, par3);

        //this.drawPanel();

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

                addButton.enabled = elementList.getSize() < HeraldryData.MAX_CRESTS+1;

                break;
            case REMOVE:
                elementList.removeCrest(selectedIndex);
                select(selectedIndex-1);
                addButton.enabled = elementList.getSize() < HeraldryData.MAX_CRESTS+1;
                break;

        }

    }


}
