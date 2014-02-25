package mods.mud;

import java.util.List;

import mods.mud.gui.GuiChangelogDownload;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class MudCommands extends CommandBase{

    @Override
    public String getCommandName() {
        return "mud";
    }
    
    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return getListOfStringsMatchingLastWord(par2ArrayOfStr, getCommandName());
    }
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender){
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiChangelogDownload(Minecraft.getMinecraft().currentScreen));
    }

    @Override
    public int compareTo(Object o) {
        return this.compareTo((ICommand)o);
    }
}
