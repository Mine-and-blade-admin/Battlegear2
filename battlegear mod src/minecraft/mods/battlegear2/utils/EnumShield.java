package mods.battlegear2.utils;

public enum EnumShield {


    WOOD("wood", 1F/1F/20F, 1F/20F, 15), // 1 second block
    HIDE("hide", 1F/1.5F/20F, 1F/20F, 12), //1.5 second block
    IRON("iron", 0 * 1F/3F/20F, 1F/20F, 9), //3 second block
    DIAMOD("diamond", 1F/5F/20F, 1F/20F, 10), //5 second block
    GOLD("gold", 1F/2F/20F, 1F/20F, 25); //2 second block

    private final float decayRate;
    private final float damageDecay;
    private final String name;
    private final int enchantability;

    EnumShield(String name, float decayRate, float damageDecay, int enchantability){
        this.name = name;
        this.decayRate = decayRate;
        this.damageDecay = damageDecay;
        this.enchantability = enchantability;
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
}
