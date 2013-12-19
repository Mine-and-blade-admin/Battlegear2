package mods.mud;

import java.util.List;

import mods.mud.gui.GuiChangelogDownload;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class MudCommands extends CommandBase{

    @Override
    public String getCommandName() {
        return "mud";
    }
    
    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return getListOfStringsMatchingLastWord(par2ArrayOfStr, getCommandName());
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiChangelogDownload(Minecraft.getMinecraft().currentScreen));
    }
}
