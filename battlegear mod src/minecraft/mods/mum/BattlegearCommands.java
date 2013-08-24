package mods.mum;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.Battlegear;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.BattlegearGUIPacket;
import mods.mum.gui.ModListGui;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet100OpenWindow;

import java.util.List;

public class BattlegearCommands extends CommandBase{

    @Override
    public String getCommandName() {
        return "mum";
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
                ModListGui gui = new ModListGui();
                Minecraft.getMinecraft().displayGuiScreen(gui);
            }

        }
    }
}
