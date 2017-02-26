package mods.battlegear2.api.core;

/**
 * Created by GotoLink on 24/04/2014.
 */
public enum InventorySlotType {
    /**
     * The hotbar inventory space (slots up to 8)
     */
    HOTBAR(0),
    /**
     * The main inventory space (slots up to 35)
     */
    MAIN(9),
    /**
     * The armor inventory space (slots up to 103)
     */
    ARMOR(100),
    /**
     * The inventory space added by battlegear (slots 151 and above)
     */
    BATTLE(151);
    public final int start;
    InventorySlotType(int init){
        start = init;
    }
}
