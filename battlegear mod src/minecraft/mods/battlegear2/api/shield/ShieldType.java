package mods.battlegear2.api.shield;

import mods.battlegear2.api.ISensible;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

/**
 * Defines a shield "material", since not necessarily a tool
 * Roughly covers the data asked for by {@link IShield}
 */
public class ShieldType {

    public static final ShieldType WOOD = new ShieldType("wood", 1F/1F/20F, 1F/20F, 40, 15, 0xFFbc9862).setRepair(new ItemStack(Blocks.planks), IDSensible.INSTANCE); // 1 second block
    public static final ShieldType HIDE = new ShieldType("hide", 1F/1.5F/20F, 1F/20F, 40, 12, 0xFF9b482b).setRepair(new ItemStack(Items.leather), IDSensible.INSTANCE); //1.5 second block
    public static final ShieldType IRON = new ShieldType("iron", 1F/3F/20F, 1F/20F, 120,  9, 0xFFacacac).setRepair(new ItemStack(Items.iron_ingot), IDSensible.INSTANCE); //3 second block
    public static final ShieldType DIAMOND = new ShieldType("diamond", 1F/5F/20F, 1F/20F, 263, 10, 0xFF23bfbf).setRepair(new ItemStack(Items.diamond), IDSensible.INSTANCE); //5 second block
    public static final ShieldType GOLD = new ShieldType("gold", 1F/2F/20F, 1F/20F, 56, 25, 0xFFa8a400).setRepair(new ItemStack(Items.gold_ingot), IDSensible.INSTANCE); //2 second block

    private final float decayRate;
    private final float damageDecay;
    private final String name;
    private final int enchantability;
    private final int maxDamage;
    private final int defaultRGB;
    private ItemStack repairingMaterial;
    private ISensible<ItemStack> comparator;

    public ShieldType(String name, float decayRate, float damageDecay, int maxDamage, int enchantability, int defaultColour){
        this.name = name;
        this.decayRate = decayRate;
        this.damageDecay = damageDecay;
        this.enchantability = enchantability;
        this.maxDamage = maxDamage;
        defaultRGB = defaultColour;
    }

    private ShieldType(String name, NBTTagCompound compound){
        this.name = name;
        this.decayRate = compound.getFloat("DecayRate");
        this.damageDecay = compound.getFloat("DamageDecay");
        this.maxDamage = compound.getInteger("MaxDamage");
        this.enchantability = compound.getInteger("Enchantability");
        this.defaultRGB = compound.getInteger("RGB");
    }

    /**
     * See {@link IShield#getDecayRate(net.minecraft.item.ItemStack)}
     */
    public float getDecayRate() {
        return decayRate;
    }

    /**
     * See {@link IShield#getDamageDecayRate(net.minecraft.item.ItemStack, float)}
     */
    public float getDamageDecay() {
        return damageDecay;
    }

    public String getName() {
        return name;
    }

    /**
     * See {@link Item#getItemEnchantability(net.minecraft.item.ItemStack)}
     */
    public int getEnchantability() {
        return enchantability;
    }

    /**
     * See {@link Item#getMaxDamage(net.minecraft.item.ItemStack)}
     */
    public int getMaxDamage() {
        return maxDamage;
    }

    public int getDefaultRGB() {
        return defaultRGB;
    }

    /**
     * Sets repairing mechanism.
     * If both arguments are null, results in no repairing behavior
     *
     * @param repairingMaterial the valid ItemStack that can repair this
     * @param comparator the optional comparison, in case only partial ItemStack recognition is needed
     * @return the modified instance
     */
    public ShieldType setRepair(ItemStack repairingMaterial, ISensible<ItemStack> comparator){
        this.repairingMaterial = repairingMaterial;
        this.comparator = comparator;
        return this;
    }

    public boolean canBeRepairedWith(ItemStack stack){
        if(comparator!=null)
            return repairingMaterial != null && !comparator.differenciate(stack, repairingMaterial);
        else
            return ItemStack.areItemStacksEqual(stack, repairingMaterial);
    }

    public ShieldType setRepair(NBTTagCompound compound){
        if(compound.hasKey("Repair", Constants.NBT.TAG_COMPOUND)){
            setRepair(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Repair")), IDSensible.INSTANCE);
        }
        return this;
    }

    /**
     * Make a new instance based on the compressed data.
     * Note: Only a valid name is required
     *
     * @param compound data to read from
     * @return the new type, or null if name is not readable
     */
    public static ShieldType fromNBT(NBTTagCompound compound){
        String name = compound.getString("Name");
        if(name.isEmpty())
            return null;
        else
            return new ShieldType(name, compound).setRepair(compound);
    }

    /**
     * Defines a comparator that only differenciate ItemStack's by their Item instances
     */
    public static class IDSensible implements ISensible<ItemStack>{
        public static final ISensible<ItemStack> INSTANCE = new IDSensible();
        private IDSensible(){}
        @Override
        public boolean differenciate(ItemStack holder1, ItemStack holder2) {
            return holder1.getItem() != holder2.getItem();
        }
    }
}
