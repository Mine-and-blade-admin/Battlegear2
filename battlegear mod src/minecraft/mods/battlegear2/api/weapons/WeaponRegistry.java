package mods.battlegear2.api.weapons;

import java.util.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.javaws.exceptions.InvalidArgumentException;
import cpw.mods.fml.common.event.FMLInterModComms;
import mods.battlegear2.api.ISensible;
import mods.battlegear2.api.StackHolder;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Registry for stacks which will be allowed in battle inventory,
 * accessible through {@link FMLInterModComms} messages.
 * Use only if your item is not recognized by default.
 * Use of {@link IBattlegearWeapon} is preferred over this method.
 * {@link NBTTagCompound} are supported.
 * @author GotoLink
 *
 */
public class WeaponRegistry {
    private static Map<StackHolder, Wield> wielding = new HashMap<StackHolder, Wield>();
    private static Set sensitivities = Sets.newHashSet(Sensitivity.ID, Sensitivity.DAMAGE, Sensitivity.NBT);

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
                return Wield.valueOf(type.toUpperCase()).setWeapon(stack);
            }catch (IllegalArgumentException ignored){
                return false;
            }
        }
    }

	/**
	 * Helper method to set an {@link ItemStack} as dual-wieldable, bypassing right-click method check
	 */
	public static void addDualWeapon(ItemStack stack) {
        wielding.put(new StackHolder(stack), Wield.BOTH);
	}
	
	/**
	 * Helper method to set an {@link ItemStack} as wieldable only in mainhand
	 */
	public static void addTwoHanded(ItemStack stack) {
        wielding.put(new StackHolder(stack), Wield.RIGHT);
	}

	/**
	 * Helper method to set an {@link ItemStack} as wieldable only in offhand, bypassing right-click method check
	 */
	public static void addOffhandWeapon(ItemStack stack) {
        wielding.put(new StackHolder(stack), Wield.LEFT);
	}

    public static boolean addSensitivity(ISensible sensitivity){
        return sensitivities.add(sensitivity);
    }

    public static boolean removeSensitivity(ISensible sensitivity){
        return sensitivities.remove(sensitivity);
    }
	
	public static boolean isWeapon(ItemStack stack) {
        StackHolder holder = new StackHolder(stack);
        if(sensitivities==null)
            return wielding.containsKey(holder);
        else
            return isWeapon(holder, sensitivities.iterator());
	}

    public static boolean isWeapon(StackHolder holder, Iterator<ISensible> itr){
        final Predicate<StackHolder> filter = new StackFilter(holder, itr);
        Map<StackHolder,Wield> tempMap = Maps.filterKeys(wielding, filter);
        return !tempMap.isEmpty();
    }
	
	public static boolean isMainHand(ItemStack stack) {
        StackHolder holder = new StackHolder(stack);
        if(sensitivities==null) {
            Wield w = wielding.get(holder);
            return w != null && w.isMainhand();
        }else
            return isMainHand(holder, sensitivities.iterator());
	}

    public static boolean isMainHand(StackHolder holder, Iterator<ISensible> itr){
        final Predicate<StackHolder> filter = new StackFilter(holder, itr);
        Map<StackHolder,Wield> tempMap = Maps.filterEntries(wielding, new Predicate<Map.Entry<StackHolder,Wield>>() {
            @Override
            public boolean apply(Map.Entry<StackHolder,Wield> input) {
                return input.getValue().isMainhand() && filter.apply(input.getKey());
            }
        });
        return !tempMap.isEmpty();
    }
	
	public static boolean isOffHand(ItemStack stack) {
        StackHolder holder = new StackHolder(stack);
        if(sensitivities==null) {
            Wield w = wielding.get(holder);
            return w != null && w.isOffhand();
        }else
            return isOffHand(holder, sensitivities.iterator());
	}

    public static boolean isOffHand(StackHolder holder, Iterator<ISensible> itr){
        final Predicate<StackHolder> filter = new StackFilter(holder, itr);
        Map<StackHolder,Wield> tempMap = Maps.filterEntries(wielding, new Predicate<Map.Entry<StackHolder,Wield>>() {
            @Override
            public boolean apply(Map.Entry<StackHolder,Wield> input) {
                return input.getValue().isOffhand() && filter.apply(input.getKey());
            }
        });
        return !tempMap.isEmpty();
    }

    public enum Sensitivity implements ISensible{
        ORE{
            @Override
            public boolean diffWith(StackHolder holder1, StackHolder holder2) {
                return !Objects.deepEquals(OreDictionary.getOreIDs(holder1.stack), OreDictionary.getOreIDs(holder2.stack));
            }
        },
        TYPE{
            @Override
            public boolean diffWith(StackHolder holder1, StackHolder holder2) {
                return !holder1.stack.getItem().getClass().equals(holder2.stack.getItem().getClass());
            }
        },
        ID{
            @Override
            public boolean diffWith(StackHolder holder1, StackHolder holder2) {
                return holder1.stack.getItem() != holder2.stack.getItem();
            }
        },
        DAMAGE {
            @Override
            public boolean diffWith(StackHolder holder1, StackHolder holder2) {
                return holder1.stack.getItemDamage() != holder2.stack.getItemDamage();
            }
        },
        NBT {
            @Override
            public boolean diffWith(StackHolder holder1, StackHolder holder2) {
                if(holder1.stack.hasTagCompound())
                    return !holder1.stack.getTagCompound().equals(holder2.stack.getTagCompound());
                else if(holder1.stack.hasTagCompound())
                    return true;
                return false;
            }
        };
    }

    public enum Wield{
        BOTH,
        RIGHT{
            @Override
            public boolean setWeapon(ItemStack stack){
                wielding.put(new StackHolder(stack), this);
                return true;
            }
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
            if(!BattlegearUtils.checkForRightClickFunction(stack)){
                wielding.put(new StackHolder(stack), this);
                return true;
            }
            return false;
        }
    }

    static class StackFilter implements Predicate<StackHolder>{
        private final Iterator<ISensible> senses;
        private final StackHolder toCompare;
        public StackFilter(StackHolder compare, Iterator<ISensible> sensitivities){
            this.toCompare = compare;
            this.senses = sensitivities;
        }

        @Override
        public boolean apply(StackHolder input) {
            while(senses.hasNext()){
                if(senses.next().diffWith(input, toCompare)){
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean equals(Object object){
            if(this == object){
                return true;
            }
            return object!=null && object instanceof StackFilter && this.toCompare.equals(((StackFilter) object).toCompare) && this.senses.equals(((StackFilter) object).senses);
        }
    }
}
