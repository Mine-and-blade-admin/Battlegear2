package mods.mud.gui;

import cpw.mods.fml.client.GuiScrollingList;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import mods.mud.UpdateEntry;
import mods.mud.exceptions.UnknownVersionFormatException;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuiSlotModList extends GuiScrollingList
{
    private GuiChangelogDownload parent;
    private List<UpdateEntry> entries;

    public GuiSlotModList(GuiChangelogDownload parent, Collection<UpdateEntry> entries, int listWidth)
    {
        super(parent.getMinecraftInstance(), listWidth, parent.height, 32, parent.height - 65 + 4, 10, 25);
        this.parent=parent;
        this.entries = new ArrayList<UpdateEntry>(entries);
    }

    @Override
    protected int getSize()
    {
        return entries.size();
    }

    @Override
    protected void elementClicked(int var1, boolean var2)
    {
        this.parent.selectModIndex(var1);
    }

    @Override
    protected boolean isSelected(int var1)
    {
        return this.parent.modIndexSelected(var1);
    }

    @Override
    protected void drawBackground()
    {
        this.parent.drawDefaultBackground();
    }

    @Override
    protected int getContentHeight()
    {
        return (this.getSize()) * 25 + 1;
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
    {
        ModContainer mc=entries.get(listIndex).getMc();
        if (Loader.instance().getModState(mc)== LoaderState.ModState.DISABLED)
        {
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(mc.getName(), listWidth - 10), this.left + 3 , var3 + 2, 0xFF2222);
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth("DISABLED", listWidth - 10), this.left + 3 , var3 + 12, 0xFF2222);
        }
        else
        {
            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(mc.getName(), listWidth - 10), this.left + 3 , var3 + 2, 0xFFFFFF);
            try{
                if(entries.get(listIndex).isUpToDate()){
                    this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(I18n.format("mud.version.latest"), listWidth - 10), this.left + 3 , var3 + 12, 0xFF00FF00);
                }else{
                    this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(I18n.format("mud.version.out"), listWidth - 10), this.left + 3 , var3 + 12, 0xFFFF0000);
                }
            }catch (UnknownVersionFormatException e){

            }catch (NullPointerException e){

            }
        }
    }

}