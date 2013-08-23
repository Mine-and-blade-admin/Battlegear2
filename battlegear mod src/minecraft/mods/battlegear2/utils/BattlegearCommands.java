package mods.battlegear2.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import mods.battlegear2.Battlegear;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.packet.BattlegearGUIPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet100OpenWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BattlegearCommands extends CommandBase{


    @Override
    public String getCommandName() {
        return "mb";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        return Arrays.asList("download");
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

        if(astring.length > 0){
            if(astring[0].equalsIgnoreCase("download")){

                if(icommandsender instanceof EntityPlayer){


                    PacketDispatcher.sendPacketToPlayer(
                            BattlegearGUIPacket.generatePacket(
                                    BattlegearGUIHandeler.downloader), (Player)icommandsender);

                }

                if(Battlegear.latestRelease != null && Battlegear.latestRelease.url != null){
                    //openURL(Battlegear.latestRelease.url);
                }
            }

        }

    }
}
