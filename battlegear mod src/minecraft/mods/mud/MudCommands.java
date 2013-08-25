package mods.mud;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mods.mud.gui.GuiChangelogDownload;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class MudCommands extends CommandBase{

    @Override
    public String getCommandName() {
        return "mud";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return super.addTabCompletionOptions(par1ICommandSender, par2ArrayOfStr);
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
        if(icommandsender instanceof EntityPlayer){
            if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
                GuiChangelogDownload gui = new GuiChangelogDownload();
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

        }
    }
}
