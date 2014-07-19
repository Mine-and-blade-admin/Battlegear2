package mods.battlegear2.api.weapons;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Locale;

/**
 * Command that can be used by op to set held items in the WeaponRegistry
 * @author GotoLink
 */
public class CommandWeaponWield extends CommandBase{
    /**
     * Item searching modes: current for getting the held {@link #ItemStack}, name for getting an itemstack from the {@link GameRegistry} by name
     */
    public final String[] searchModes = {"current", "name"};
    /**
     * Supported wielding styles, by hand
     */
    public final String[] types = {"both", "right", "left"};
    @Override
    public String getCommandName() {
        return "weaponwield";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "commands.weaponwield.usage";//<itemSearch:current/name> <handWielding:both/right/left> <player or item name>
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        if(var2!=null) {
            ItemStack itemStack = null;
            if (var2.length == 2) {
                if(var2[0].equals(searchModes[0])) {
                    if (var1 instanceof EntityPlayer)
                        itemStack = ((EntityPlayer) var1).getCurrentEquippedItem();
                    else
                        throw new PlayerNotFoundException();
                }
            }else if(var2.length == 3){
                if(var2[0].equals(searchModes[0]))
                    itemStack = getPlayer(var1, var2[2]).getCurrentEquippedItem();
                else if(var2[0].equals(searchModes[1])) {
                    String[] splits = var2[2].split(":", 2);
                    if(splits.length == 2)
                        itemStack = GameRegistry.findItemStack(splits[0], splits[1], 1);
                }
            }
            if(var2.length>1 && setWeapon(itemStack, var2[1].toLowerCase(Locale.ENGLISH)))
                func_152373_a(var1, this, "commands.weaponwield.set", itemStack);
            else
                throw new WrongUsageException(getCommandUsage(var1), itemStack);
        }
    }

    public boolean setWeapon(ItemStack stack, String type) {
        if(stack!=null){
            if(type.equals(types[0])){
                if(!BattlegearUtils.checkForRightClickFunction(stack.getItem(), stack)){
                    WeaponRegistry.addDualWeapon(stack);
                    return true;
                }
            }else if(type.equals(types[1])){
                WeaponRegistry.addTwoHanded(stack);
                return true;
            }else if(type.equals(types[2])){
                if(!BattlegearUtils.checkForRightClickFunction(stack.getItem(), stack)){
                    WeaponRegistry.addOffhandWeapon(stack);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        if(par2ArrayOfStr.length == 1)
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, searchModes);
        else if(par2ArrayOfStr.length == 2)
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, types);
        else if(par2ArrayOfStr.length == 3 && par2ArrayOfStr[0].equals(searchModes[0]))
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
        else
            return null;
    }

    @Override
    public int compareTo(Object object){
        return compareTo((ICommand) object);
    }
}
