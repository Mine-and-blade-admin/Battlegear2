package mods.battlegear2.api.weapons;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.event.FMLInterModComms;
import mods.battlegear2.api.ISensible;
import mods.battlegear2.api.StackHolder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Registry for stacks which will be allowed in battle inventory, accessible through {@link FMLInterModComms} messages.
 * Use only if your item is not recognized by default.
 * Use of {@link IBattlegearWeapon} or {@link IUsableItem} are preferred over this method.
 * {@link NBTTagCompound} are supported by default, though can be bypassed through /weaponwield sensitivity command by server op
 * @author GotoLink
 *
 */
public class WeaponRegistry {
    private static Map<StackHolder, Pair<Wield, Boolean>> wielding = new HashMap<StackHolder, Pair<Wield, Boolean>>();
    private static Set<ISensible<StackHolder>> sensitivities = Sets.newHashSetWithExpectedSize(3);
    static{
        Collections.addAll(sensitivities, Sensitivity.ID, Sensitivity.DAMAGE, Sensitivity.NBT);
    }

    /**
     * Called by a {@link FMLInterModComms.IMCMessage} with key as type, and the {@link ItemStack} as value
     * @param type the key from the message, accepted values (case don't matter) are: "dual", "mainhand", "offhand", "both", "right", "left"
     * @param stack registered as either dual-wieldable, wieldable only in mainhand or in offhand
     */
    public static boolean setWeapon(String type, ItemStack stack){
        if(type.equalsIgnoreCase("Dual")){
            return Wield.BOTH.setWeapon(stack);
        }else if(type.equalsIgnoreCase("MainHand")){
            return Wield.RIGHT.setWeapon(stack);
        }else if(type.equalsIgnoreCase("OffHand")) {
            return Wield.LEFT.setWeapon(stack);
        }else{
            try{
                return Wield.valueOf(type.toUpperCase(Locale.ENGLISH)).setWeapon(stack);
            }catch (IllegalArgumentException ignored){
                return false;
            }
        }
    }

	/**
	 * Helper method to set an {@link ItemStack} as dual-wieldable
	 */
	public static void addDualWeapon(ItemStack stack) {
        wielding.put(new StackHolder(stack), Pair.of(Wield.BOTH, false));
	}
	
	/**
	 * Helper method to set an {@link ItemStack} as wieldable only in mainhand
	 */
	public static void addTwoHanded(ItemStack stack) {
        wielding.put(new StackHolder(stack), Pair.of(Wield.RIGHT, false));
	}

	/**
	 * Helper method to set an {@link ItemStack} as wieldable only in offhand
	 */
	public static void addOffhandWeapon(ItemStack stack) {
        wielding.put(new StackHolder(stack), Pair.of(Wield.LEFT, false));
	}

    /**
     * Adds a way to compare two {@link StackHolder} in this registry
     * @param sensitivity the comparison to add
     * @return true if this new comparison could be added
     */
    public static boolean addSensitivity(ISensible<StackHolder> sensitivity){
        return sensitivities.add(sensitivity);
    }

    /**
     * Removes a way to compare two {@link StackHolder} in this registry
     * @param sensitivity the comparison to remove
     * @return true if this comparison has been removed
     */
    public static boolean removeSensitivity(ISensible<StackHolder> sensitivity){
        return sensitivities.remove(sensitivity);
    }

    /**
     * Check if given {@link ItemStack} has been registered as any type of weapon
     * @param stack the stack to check
     * @return true if an equivalent stack as been found in this registry
     */
	public static boolean isWeapon(ItemStack stack) {
        StackHolder holder = new StackHolder(stack);
        if(sensitivities==null)
            return wielding.containsKey(holder);
        else
            return isWeapon(holder, sensitivities.iterator());
	}

    /**
     * Check if given {@link StackHolder} has been registered as any type of weapon,
     * depending on given {@link Iterator} of comparisons
     * @param holder the stack wrapper to check
     * @return true if a comparable stack wrapper as been found in this registry
     */
    public static boolean isWeapon(StackHolder holder, Iterator<ISensible<StackHolder>> itr){
        final Predicate<StackHolder> filter = new ISensible.Filter<StackHolder>(holder, itr);
        return Iterators.any(wielding.keySet().iterator(), filter);
    }

    /**
     * Check if given {@link ItemStack} has been registered as a mainhand weapon
     * @param stack the stack to check
     * @return true if an equivalent mainhand-wieldable stack as been found in this registry
     */
	public static boolean isMainHand(ItemStack stack) {
        StackHolder holder = new StackHolder(stack);
        if(sensitivities==null) {
            Pair<Wield,Boolean> w = wielding.get(holder);
            return w != null && w.getLeft().isMainhand();
        }else
            return isMainHand(holder, sensitivities.iterator());
	}

    /**
     * Check if given {@link StackHolder} has been registered as a mainhand weapon,
     * depending on given {@link Iterator} of comparisons
     * @param holder the stack wrapper to check
     * @return true if a comparable mainhand-wieldable stack wrapper as been found in this registry
     */
    public static boolean isMainHand(StackHolder holder, Iterator<ISensible<StackHolder>> itr){
        final Predicate<StackHolder> filter = new ISensible.Filter<StackHolder>(holder, itr);
        return Iterators.any(wielding.entrySet().iterator(), new Predicate<Map.Entry<StackHolder,Pair<Wield, Boolean>>>() {
                    @Override
                    public boolean apply(Map.Entry<StackHolder,Pair<Wield, Boolean>> input) {
                        return input.getValue().getLeft().isMainhand() && filter.apply(input.getKey());
                    }
                });
    }

    /**
     * Check if given {@link ItemStack} has been registered as an offhand weapon
     * @param stack the stack to check
     * @return true if an equivalent offhand-wieldable stack as been found in this registry
     */
	public static boolean isOffHand(ItemStack stack) {
        StackHolder holder = new StackHolder(stack);
        if(sensitivities==null) {
            Pair<Wield,Boolean> w = wielding.get(holder);
            return w != null && w.getLeft().isOffhand();
        }else
            return isOffHand(holder, sensitivities.iterator());
	}

    /**
     * Check if given {@link StackHolder} has been registered as an offhand weapon
     * depending on given {@link Iterator} of comparisons
     * @param holder the stack wrapper to check
     * @return true if a comparable offhand-wieldable stack wrapper as been found in this registry
     */
    public static boolean isOffHand(StackHolder holder, Iterator<ISensible<StackHolder>> itr){
        final Predicate<StackHolder> filter = new ISensible.Filter<StackHolder>(holder, itr);
        return Iterators.any(wielding.entrySet().iterator(), new Predicate<Map.Entry<StackHolder,Pair<Wield, Boolean>>>() {
            @Override
            public boolean apply(Map.Entry<StackHolder,Pair<Wield, Boolean>> input) {
                return input.getValue().getLeft().isOffhand() && filter.apply(input.getKey());
            }
        });
    }

    public static boolean useOverAttack(ItemStack stack, boolean inOffhand){
        StackHolder holder = new StackHolder(stack);
        if(sensitivities==null) {
            Pair<Wield,Boolean> w = wielding.get(holder);
            return w != null && w.getRight() && (inOffhand ? w.getLeft().isOffhand() : w.getLeft().isMainhand());
        }else
            return useOverAttack(holder, sensitivities.iterator(), inOffhand);
    }

    public static boolean useOverAttack(StackHolder holder, Iterator<ISensible<StackHolder>> itr, final boolean inOffhand){
        final Predicate<StackHolder> filter = new ISensible.Filter<StackHolder>(holder, itr);
        return Iterators.any(wielding.entrySet().iterator(), new Predicate<Map.Entry<StackHolder,Pair<Wield, Boolean>>>() {
            @Override
            public boolean apply(Map.Entry<StackHolder,Pair<Wield, Boolean>> input) {
                return input.getValue().getRight() && (inOffhand ? input.getValue().getLeft().isOffhand():input.getValue().getLeft().isMainhand()) && filter.apply(input.getKey());
            }
        });
    }

    /**
     * Commonly used comparisons
     */
    public enum Sensitivity implements ISensible<StackHolder>{
        ORE{
            @Override
            public boolean differenciate(StackHolder holder1, StackHolder holder2) {
                return !Objects.deepEquals(OreDictionary.getOreIDs(holder1.stack), OreDictionary.getOreIDs(holder2.stack));
            }
        },
        TYPE{
            @Override
            public boolean differenciate(StackHolder holder1, StackHolder holder2) {
                return !holder1.stack.getItem().getClass().equals(holder2.stack.getItem().getClass());
            }
        },
        ID{
            @Override
            public boolean differenciate(StackHolder holder1, StackHolder holder2) {
                return holder1.stack.getItem() != holder2.stack.getItem();
            }
        },
        DAMAGE {
            @Override
            public boolean differenciate(StackHolder holder1, StackHolder holder2) {
                return holder1.stack.getItemDamage() != holder2.stack.getItemDamage();
            }
        },
        NBT {
            @Override
            public boolean differenciate(StackHolder holder1, StackHolder holder2) {
                if(holder1.stack.hasTagCompound())
                    return !holder1.stack.getTagCompound().equals(holder2.stack.getTagCompound());
                else
                    return holder2.stack.hasTagCompound();
            }
        }
    }

    /**
     * The way an item is wield
     */
    public enum Wield{
        BOTH,
        RIGHT{
            @Override
            public boolean isOffhand(){
                return false;
            }
        },
        LEFT{
            @Override
            public boolean isMainhand(){
                return false;
            }
        };

        public boolean isOffhand(){
            return true;
        }

        public boolean isMainhand(){
            return true;
        }

        public boolean setWeapon(ItemStack stack){
            wielding.put(new StackHolder(stack), Pair.of(this, false));
            return true;
        }

        public boolean setUsable(ItemStack stack){
            wielding.put(new StackHolder(stack), Pair.of(this, true));
            return true;
        }
    }
}
