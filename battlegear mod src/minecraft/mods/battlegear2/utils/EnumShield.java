package mods.battlegear2.utils;

public enum EnumShield {


    WOOD("wood", 1F/1F/20F, 1F/20F, 40, 15), // 1 second block
    HIDE("hide", 1F/1.5F/20F, 1F/20F, 40, 12), //1.5 second block
    IRON("iron", 1F/3F/20F, 1F/20F, 120,  9), //3 second block
    DIAMOD("diamond", 1F/5F/20F, 1F/20F, 263, 10), //5 second block
    GOLD("gold", 1F/2F/20F, 1F/20F, 56, 25); //2 second block

    private final float decayRate;
    private final float damageDecay;
    private final String name;
    private final int enchantability;
    private final int maxDamage;

    EnumShield(String name, float decayRate, float damageDecay, int maxDamage, int enchantability){
        this.name = name;
        this.decayRate = decayRate;
        this.damageDecay = damageDecay;
        this.enchantability = enchantability;
        this.maxDamage = maxDamage;
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
}
