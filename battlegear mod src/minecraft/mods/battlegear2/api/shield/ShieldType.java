package mods.battlegear2.api.shield;

public class ShieldType {

    public static final ShieldType WOOD = new ShieldType("wood", 1F/1F/20F, 1F/20F, 40, 15, 0xFFbc9862); // 1 second block
    public static final ShieldType HIDE = new ShieldType("hide", 1F/1.5F/20F, 1F/20F, 40, 12, 0xFF9b482b); //1.5 second block
    public static final ShieldType IRON = new ShieldType("iron", 1F/3F/20F, 1F/20F, 120,  9, 0xFFacacac); //3 second block
    public static final ShieldType DIAMOND = new ShieldType("diamond", 1F/5F/20F, 1F/20F, 263, 10, 0xFF23bfbf); //5 second block
    public static final ShieldType GOLD = new ShieldType("gold", 1F/2F/20F, 1F/20F, 56, 25, 0xFFa8a400); //2 second block

    private final float decayRate;
    private final float damageDecay;
    private final String name;
    private final int enchantability;
    private final int maxDamage;
    private final int defaultRGB;

    public ShieldType(String name, float decayRate, float damageDecay, int maxDamage, int enchantability, int defaultColour){
        this.name = name;
        this.decayRate = decayRate;
        this.damageDecay = damageDecay;
        this.enchantability = enchantability;
        this.maxDamage = maxDamage;
        defaultRGB = defaultColour;
    }

    public float getDecayRate() {
        return decayRate;
    }

    public float getDamageDecay() {
        return damageDecay;
    }

    public String getName() {
        return name;
    }

    public int getEnchantability() {
        return enchantability;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getDefaultRGB() {
        return defaultRGB;
    }
}
