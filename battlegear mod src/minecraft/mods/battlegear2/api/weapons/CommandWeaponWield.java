package mods.battlegear2.api.weapons;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.*;

/**
 * Command that can be used by op to set held/named items in the WeaponRegistry and tinker its sensitivity
 * @author GotoLink
 */
public class CommandWeaponWield extends CommandBase{
    /**
     * Item searching modes: current for getting the held {@link ItemStack}, name for getting an itemstack from the {@link GameRegistry} by name
     * Available operations: on the selected sensitivities
     */
    public final String[] searchModes = {"current", "name", "sensitivity"};
    /**
     * Sensitivity operations
     */
    public final String[] operations = {"add", "remove", "get"};
    /**
     * Selected sensitivities for the WeaponRegistry comparison algorithm
     */
    private Set<String> sensitivities = Sets.newHashSet("ID", "DAMAGE", "NBT");
    @Override
    public String getCommandName() {
        return "weaponwield";
    }

    /**
     * <current> <handWielding:both|left|right> [player]
     * OR
     * <name> <handWielding:both|left|right> <item name>
     * OR
     * <sensitivity> <operation:add|remove|get> <ore|type|id|damage|nbt>
     */
    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "commands.weaponwield.usage";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        if(var2!=null) {
            ItemStack itemStack = null;
            if (var2.length == 2) {
                if(var2[0].equals(searchModes[0])) {//current
                    if (var1 instanceof EntityPlayer)
                        itemStack = ((EntityPlayer) var1).getCurrentEquippedItem();
                    else
                        throw new PlayerNotFoundException();
                }else if(var2[0].equals(searchModes[2]) && var2[1].equals(operations[2])){//sensitivity get
                    func_152373_a(var1, this, "commands.weaponwield.sensitivity", sensitivities);
                    return;
                }
            }else if(var2.length == 3){
                if(var2[0].equals(searchModes[0]))//current
                    itemStack = getPlayer(var1, var2[2]).getCurrentEquippedItem();
                else if(var2[0].equals(searchModes[1])) {//name
                    String[] splits = var2[2].split(":", 2);
                    if(splits.length == 2)
                        itemStack = GameRegistry.findItemStack(splits[0], splits[1], 1);
                }else if(var2[0].equals(searchModes[2])){//sensitivity
                    if(var2[1].equals(operations[0])){//add
                        try {
                            WeaponRegistry.Sensitivity sens = WeaponRegistry.Sensitivity.valueOf(var2[2].toUpperCase(Locale.ENGLISH));
                            if (sensitivities.add(sens.name()) && WeaponRegistry.addSensitivity(sens)) {
                                func_152373_a(var1, this, "commands.weaponwield.sensitivity.added", sens);
                                var1.addChatMessage(new ChatComponentText(sensitivities.toString()));
                                return;
                            }
                        } catch (IllegalArgumentException ignored) {
                        }
                    }else if(var2[1].equals(operations[1])){//remove
                        try {
                            WeaponRegistry.Sensitivity sens = WeaponRegistry.Sensitivity.valueOf(var2[2].toUpperCase(Locale.ENGLISH));
                            if (sensitivities.remove(sens.name()) && WeaponRegistry.removeSensitivity(sens)) {
                                func_152373_a(var1, this, "commands.weaponwield.sensitivity.removed", sens);
                                var1.addChatMessage(new ChatComponentText(sensitivities.toString()));
                                return;
                            }
                        }catch (IllegalArgumentException ignored){}
                    }
                }
            }
            if(var2.length>1 && setWeapon(itemStack, var2[1].toUpperCase(Locale.ENGLISH)))
                func_152373_a(var1, this, "commands.weaponwield.set", itemStack);
            else
                throw new WrongUsageException(getCommandUsage(var1), itemStack);
        }
    }

    public boolean setWeapon(ItemStack stack, String type) {
        return stack != null && WeaponRegistry.Wield.valueOf(type).setWeapon(stack);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        if(par2ArrayOfStr.length == 1)
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, searchModes);
        else if(par2ArrayOfStr.length == 2) {
            if(par2ArrayOfStr[0].equals(searchModes[0])||par2ArrayOfStr[0].equals(searchModes[1]))
                return getListOfStringsMatchingLastWord(par2ArrayOfStr, getNames(WeaponRegistry.Wield.values(), true));
            else if(par2ArrayOfStr[0].equals(searchModes[2]))//sensitivity
                return getListOfStringsMatchingLastWord(par2ArrayOfStr, operations);
        }
        else if(par2ArrayOfStr.length == 3) {
            if (par2ArrayOfStr[0].equals(searchModes[0]))
                return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
            else if (par2ArrayOfStr[0].equals(searchModes[2])) {//sensitivity
                if(par2ArrayOfStr[1].equals(operations[0]))//add
                    return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, Sets.difference(ImmutableSet.copyOf(getNames(WeaponRegistry.Sensitivity.values(), false)), sensitivities));
                else if(par2ArrayOfStr[1].equals(operations[1])) {//remove
                    return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, sensitivities);
                }
            }
        }
        return null;
    }

    private String[] getNames(Object[] values, boolean lowerCase) {
        String[] names = new String[values.length];
        for(int i = 0; i<values.length; i++) {
            names[i] = values[i].toString();
            if(lowerCase)
                names[i] = names[i].toLowerCase(Locale.ENGLISH);
        }
        return names;
    }

    @Override
    public int compareTo(Object object){
        return compareTo((ICommand) object);
    }
}
