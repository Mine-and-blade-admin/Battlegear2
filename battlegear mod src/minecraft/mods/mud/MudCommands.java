package mods.mud;

import mods.mud.gui.GuiChangelogDownload;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.List;

public class MudCommands extends CommandBase{

    @Override
    public String getCommandName() {
        return "mud";
    }
    
    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr, BlockPos pos) {
        return getListOfStringsMatchingLastWord(par2ArrayOfStr, getCommandName());
    }
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender){
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/mud";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) throws CommandException {
        new Thread() {
            @Override
            public void run()
            {
                while(Minecraft.getMinecraft().currentScreen!=null)
                    try {
                        Thread.sleep(100L);
                    }catch (Exception ignored){
                    }
                Minecraft.getMinecraft().displayGuiScreen(new GuiChangelogDownload());
            }
        }.start();
    }
}
