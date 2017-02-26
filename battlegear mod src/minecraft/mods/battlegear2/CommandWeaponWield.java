package mods.battlegear2;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.weapons.WeaponRegistry;
import mods.battlegear2.packet.WieldSetPacket;
import net.minecraft.command.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Command that can be used by op to set held/named items in the WeaponRegistry and tinker its sensitivity
 * @author GotoLink
 */
public final class CommandWeaponWield extends CommandBase{

    public static final CommandWeaponWield INSTANCE = new CommandWeaponWield();

    private CommandWeaponWield(){}

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
    public String getName() {
        return "weaponwield";
    }

    /**
     * <current> <handWielding:both|left|right> [player] [use:true]
     * OR
     * <name> <handWielding:both|left|right> <item name> [use:true]
     * OR
     * <sensitivity> <operation:add|remove|get> <ore|type|id|damage|nbt>
     */
    @Override
    public String getUsage(@Nonnull ICommandSender var1) {
        return "commands.weaponwield.usage";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server,@Nonnull ICommandSender var1,@Nonnull String[] var2) throws CommandException {
            ItemStack itemStack = ItemStack.EMPTY;
            if (var2.length == 2) {
                if(var2[0].equals(searchModes[0])) {//current
                    itemStack = getCommandSenderAsPlayer(var1).getHeldItemMainhand();
                }else if(var2[0].equals(searchModes[2]) && var2[1].equals(operations[2])){//sensitivity get
                    notifyCommandListener(var1, this, "commands.weaponwield.sensitivity", sensitivities);
                    return;
                }
            }else if(var2.length < 5){
                if(var2[0].equals(searchModes[0]))//current
                    itemStack = getPlayer(server, var1, var2[2]).getHeldItemMainhand();
                else if(var2[0].equals(searchModes[1])) {//name
                    Item item = getItemByText(var1, var2[2]);
                    itemStack = new ItemStack(item);
                }else if(var2[0].equals(searchModes[2])){//sensitivity
                    if(var2[1].equals(operations[0])){//add
                        try {
                            WeaponRegistry.Sensitivity sens = WeaponRegistry.Sensitivity.valueOf(var2[2].toUpperCase(Locale.ENGLISH));
                            if (sensitivities.add(sens.name()) && WeaponRegistry.addSensitivity(sens)) {
                                notifyCommandListener(var1, this, "commands.weaponwield.sensitivity.added", sens);
                                var1.sendMessage(new TextComponentString(sensitivities.toString()));
                                return;
                            }
                        } catch (IllegalArgumentException ignored) {
                        }
                    }else if(var2[1].equals(operations[1])){//remove
                        try {
                            WeaponRegistry.Sensitivity sens = WeaponRegistry.Sensitivity.valueOf(var2[2].toUpperCase(Locale.ENGLISH));
                            if (sensitivities.remove(sens.name()) && WeaponRegistry.removeSensitivity(sens)) {
                                notifyCommandListener(var1, this, "commands.weaponwield.sensitivity.removed", sens);
                                var1.sendMessage(new TextComponentString(sensitivities.toString()));
                                return;
                            }
                        }catch (IllegalArgumentException ignored){}
                    }
                }
            }
            boolean result = false;
            if(!itemStack.isEmpty()) {
                String temp = var2[1].toUpperCase(Locale.ENGLISH);
                if (var2.length == 4 && parseBoolean(var2[3])) {
                    result = setUsable(itemStack, temp);
                } else {
                    if (BattlegearUtils.checkForRightClickFunction(itemStack))
                        result = setUsable(itemStack, temp);
                    else if (setWeapon(itemStack, temp))
                        result = true;
                }
            }
            if(result) {
                var1.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, itemStack.getCount());
                notifyCommandListener(var1, this, "commands.weaponwield.set", itemStack);
            }else
                throw new WrongUsageException(getUsage(var1), itemStack);
    }

    public boolean setWeapon(ItemStack stack, String type) {
        return WeaponRegistry.Wield.valueOf(type).setWeapon(stack);
    }

    public boolean setUsable(ItemStack stack, String type) {
        if(WeaponRegistry.Wield.valueOf(type).setUsable(stack)){
            Battlegear.packetHandler.sendPacketToAll(new WieldSetPacket(stack, type).generatePacket());
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender par1ICommandSender, String[] par2ArrayOfStr, BlockPos pos) {
        if(par2ArrayOfStr.length == 1)
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, searchModes);
        else if(par2ArrayOfStr.length == 2) {
            if(par2ArrayOfStr[0].equals(searchModes[0])||par2ArrayOfStr[0].equals(searchModes[1]))
                return getListOfStringsMatchingLastWord(par2ArrayOfStr, getNames(WeaponRegistry.Wield.values(), true));
            else if(par2ArrayOfStr[0].equals(searchModes[2]))//sensitivity
                return getListOfStringsMatchingLastWord(par2ArrayOfStr, operations);
        }
        else if(par2ArrayOfStr.length == 3) {
            if (par2ArrayOfStr[0].equals(searchModes[0]))//current
                return getListOfStringsMatchingLastWord(par2ArrayOfStr, server.getOnlinePlayerNames());
            else if(par2ArrayOfStr[0].equals(searchModes[1]))//name
                return getListOfStringsMatchingLastWord(par2ArrayOfStr, GameData.getItemRegistry().getKeys());
            else if (par2ArrayOfStr[0].equals(searchModes[2])) {//sensitivity
                if(par2ArrayOfStr[1].equals(operations[0]))//add
                    return getListOfStringsMatchingLastWord(par2ArrayOfStr, Sets.difference(ImmutableSet.copyOf(getNames(WeaponRegistry.Sensitivity.values(), false)), sensitivities));
                else if(par2ArrayOfStr[1].equals(operations[1])) {//remove
                    return getListOfStringsMatchingLastWord(par2ArrayOfStr, sensitivities);
                }
            }
        }
        else if(par2ArrayOfStr.length == 4) {
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, "true", "1", "false", "0");
        }
        return super.getTabCompletions(server, par1ICommandSender, par2ArrayOfStr, pos);
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
}
