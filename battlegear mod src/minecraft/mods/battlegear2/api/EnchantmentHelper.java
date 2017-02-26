package mods.battlegear2.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameData;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
    private final Set<Enchantment> enchants;
    public static final int MIN_FREE_SLOT = 11, INVALID = -1;
    private static int MAX_ENCHANTS = INVALID;

    public EnchantmentHelper(){
        enchants = Sets.newIdentityHashSet();
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
            list.add(Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
        }
        return list;
    }

    public static int getMaxEnchants() {
        if(MAX_ENCHANTS == INVALID){
            try {
                Field field = GameData.class.getDeclaredField("MAX_ENCHANTMENT_ID");
                field.setAccessible(true);
                MAX_ENCHANTS = (Integer) field.get(null);
            }catch (Exception printed){
                printed.printStackTrace();
            }
        }
        return MAX_ENCHANTS;
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
     * Will always return the same result till a new enchantment is added to {@link Enchantment#REGISTRY}
     * Recommended when registering lone enchantments
     *
     * @param startingId to start search from (included)
     * @return {@code #INVALID} if no id above the given one is available, or the next available id
     */
    public static int getNextAvailableId(int startingId){
        int result = startingId < MIN_FREE_SLOT ? MIN_FREE_SLOT : startingId;
        while(result < getMaxEnchants()) {
            if(Enchantment.getEnchantmentByID(result)==null)
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
     * @return {@code #INVALID} if no id above the given one is available, or the next available id
     */
    public static int takeNextAvailableId(int startingId){
        if(reservedId==null){
            reservedId = new BitSet(getMaxEnchants());
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
        }while(result < getMaxEnchants());
        return INVALID;
    }

    /**
     * Get the next available id for a new enchantment, makes an internal reservation.
     * Can be used when searching multiple available ids
     * The given argument is untouched if {@code #INVALID} is returned
     *
     * @param property read as an int {@link Property#getInt()} and refreshed with the new available id
     * @return {@code #INVALID} if no id above the given one is available, or the next available id
     */
    public static int takeNextAvailableId(Property property){
        if(property!=null && property.isIntValue()){
            int start = property.getInt();
            if(start>=0 && start<getMaxEnchants()) {
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
    public static Optional<Enchantment> build(Property property, String name, Class<? extends Enchantment> type, String registry, Object... args){
        int id = takeNextAvailableId(property);
        if(id!=INVALID && type!=null){
            Enchantment enchantment = null;
            try {
                Constructor<? extends Enchantment> constructor;
                if (args != null) {
                    Class<?>[] ctorArgClasses = new Class<?>[args.length];
                    for (int idx = 0; idx < args.length; idx++) {
                        ctorArgClasses[idx] = Primitives.unwrap(args[idx].getClass());
                    }
                    constructor = type.getConstructor(ctorArgClasses);
                    enchantment = constructor.newInstance(args);
                } else {
                    constructor = type.getConstructor();
                    enchantment = constructor.newInstance();
                }
            }catch (Exception logged){
                FMLLog.log(Level.ERROR, logged, "Caught an exception during enchantment registration");
            }
            if(enchantment != null && name != null && !name.isEmpty()) {
                enchantment.setName(name);
                Enchantment.REGISTRY.register(id, new ResourceLocation(registry), enchantment);
            }
            return Optional.fromNullable(enchantment);
        }
        return Optional.absent();
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
            return net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(enchantmentOptional.get(), itemStack);
        else
            return 0;
    }
}
