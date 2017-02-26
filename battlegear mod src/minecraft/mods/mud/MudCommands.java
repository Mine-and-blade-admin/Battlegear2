package mods.mud;

import mods.mud.gui.GuiChangelogDownload;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.List;

public class MudCommands extends CommandBase{

    @Nonnull
    @Override
    public String getName() {
        return "mud";
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender par1ICommandSender, String[] par2ArrayOfStr, BlockPos pos) {
        return getListOfStringsMatchingLastWord(par2ArrayOfStr, getName());
    }
    
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender par1ICommandSender){
        return true;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender icommandsender) {
        return "/mud";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server,@Nonnull ICommandSender icommandsender,@Nonnull String[] string) throws CommandException {
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
