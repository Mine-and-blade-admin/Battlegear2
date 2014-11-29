package mods.battlegear2.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created by GotoLink on 11/10/2014.
 * Help to manage collections of enchantments, build new optional instances, find available ids...
 */
public class EnchantmentHelper {
    /**
     * Reservation system for new enchantments id, requested using {@link #takeNextAvailableId}
     */
    private static BitSet reservedId;
    private final TreeSet<Enchantment> enchants;
    public static final int MIN_FREE_SLOT = 8, INVALID = -1;

    public EnchantmentHelper(){
        enchants = Sets.newTreeSet(new Comparator<Enchantment>() {
            @Override
            public int compare(Enchantment o1, Enchantment o2) {
                return o1.effectId - o2.effectId;
            }
        });
    }

    public EnchantmentHelper(Comparator<Enchantment> comparator){
        enchants = Sets.newTreeSet(comparator);
    }

    /**
     * @return an immutable copy of the enchantments added to this helper
     */
    public List<Enchantment> getEnchants() {
        return ImmutableList.copyOf(enchants);
    }

    /**
     * Add an enchantment to this helper.
     *
     * @param enchantment to add to the list
     * @return true if the enchantment could be added
     */
    public boolean addEnchantment(Enchantment enchantment){
        return enchantment!=null && enchants.add(enchantment);
    }

    /**
     * Give maxed-out enchantments books from the enchantments list.
     * Used by battlegear creative tab for its display list
     *
     * @return list containing the enchantment books
     */
    public Collection<ItemStack> getEnchantmentBooks() {
        Collection<ItemStack> list = new ArrayList<ItemStack>(enchants.size());
        for(Enchantment enchantment:enchants){
            list.add(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
        }
        return list;
    }

    /**
     * Get the lowest available id for a new enchantment, makes no reservation.
     *
     * @see #getNextAvailableId(int)
     */
    public static int getLowestAvailableId(){
        return getNextAvailableId(MIN_FREE_SLOT);
    }

    /**
     * Get the next available id for a new enchantment, makes no reservation.
     * Will always return the same result till a new enchantment is added to {@link Enchantment#enchantmentsList}
     * Recommended when registering lone enchantments
     *
     * @param startingId to start search from (included)
     * @return {@link #INVALID} if no id above the given one is available, or the next available id
     */
    public static int getNextAvailableId(int startingId){
        int result = startingId < MIN_FREE_SLOT ? MIN_FREE_SLOT : startingId;
        while(result < Enchantment.enchantmentsList.length) {
            if(Enchantment.enchantmentsList[result]==null)
                return result;
            result++;
        }
        return INVALID;
    }

    /**
     * Get the next available id for a new enchantment, makes an internal reservation.
     * Can be used when searching multiple available ids
     *
     * @param startingId to start search from (included)
     * @return {@link #INVALID} if no id above the given one is available, or the next available id
     */
    public static int takeNextAvailableId(int startingId){
        if(reservedId==null){
            reservedId = new BitSet(Enchantment.enchantmentsList.length);
        }
        int result = startingId < MIN_FREE_SLOT ? MIN_FREE_SLOT : startingId;
        int temp;
        do{
            temp = getNextAvailableId(result);
            if(temp>=result) {
                result = temp;
                if(!reservedId.get(result)) {
                    reservedId.set(result);
                    return result;
                }
                result++;
            }else
                break;
        }while(result < Enchantment.enchantmentsList.length);
        return INVALID;
    }

    /**
     * Get the next available id for a new enchantment, makes an internal reservation.
     * Can be used when searching multiple available ids
     * The given argument is untouched if {@link #INVALID} is returned
     *
     * @param property read as an int {@link Property#getInt()} and refreshed with the new available id
     * @return {@link #INVALID} if no id above the given one is available, or the next available id
     */
    public static int takeNextAvailableId(Property property){
        if(property!=null && property.isIntValue()){
            int start = property.getInt();
            if(start>=0 && start<Enchantment.enchantmentsList.length) {
                int result = takeNextAvailableId(start);
                property.set(result);
                return result;
            }
        }
        return INVALID;
    }

    /**
     * Build an optional enchantment.
     * When instantiating, the first argument will always be the enchantment id,
     * while the others have unwrapped types based on the {@code args} array
     *
     * @param property to take the integer value as default enchantment id
     * @param name to name the enchantment, null or empty to not call {@link Enchantment#setName(String)}
     * @param type class to instantiate once an available id is found
     * @param args additional arguments used on the constructor when instantiating
     * @return the optional enchantment
     */
    public static Optional<Enchantment> build(Property property, String name, Class<? extends Enchantment> type, Object... args){
        int id = takeNextAvailableId(property);
        if(id!=INVALID && type!=null){
            Enchantment enchantment = null;
            try {
                Constructor<? extends Enchantment> constructor;
                if (args != null) {
                    Class<?>[] ctorArgClasses = new Class<?>[args.length+1];
                    ctorArgClasses[0] = int.class;
                    for (int idx = 0; idx < args.length; idx++) {
                        ctorArgClasses[idx+1] = Primitives.unwrap(args[idx].getClass());
                    }
                    constructor = type.getConstructor(ctorArgClasses);
                    enchantment = constructor.newInstance(ObjectArrays.concat(id, args));
                } else {
                    constructor = type.getConstructor(int.class);
                    enchantment = constructor.newInstance(id);
                }
            }catch (Exception logged){
                FMLLog.log(Level.ERROR, logged, "Caught an exception during enchantment registration");
            }
            if(enchantment != null && name != null && !name.isEmpty())
                enchantment.setName(name);
            return Optional.fromNullable(enchantment);
        }
        return Optional.absent();
    }

    /**
     * Get the enchantment id from an optional enchantment.
     *
     * @param enchantmentOptional to get the id from
     * @return {@link #INVALID} if the covered enchantment doesn't exist
     */
    public static int getId(Optional<Enchantment> enchantmentOptional){
        if(enchantmentOptional.isPresent())
            return enchantmentOptional.get().effectId;
        else
            return INVALID;
    }

    /**
     * Get the enchantment level on the given stack.
     *
     * @param enchantmentOptional the optional enchantment
     * @param itemStack to search enchantment on, can be null
     * @return 0 if the enchantment doesn't exist, or stack is null, the level of the enchantment otherwise
     */
    public static int getEnchantmentLevel(Optional<Enchantment> enchantmentOptional, ItemStack itemStack){
        if(enchantmentOptional.isPresent())
            return net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(enchantmentOptional.get().effectId, itemStack);
        else
            return 0;
    }
}
