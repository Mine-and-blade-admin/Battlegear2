package mods.battlegear2.utils;

import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public enum EnumBGAnimations {

    OffHandSwing {
        @Override
        public void processAnimation(IBattlePlayer entity) {
            entity.swingOffItem();
        }
    }, SpecialAction {
        @Override
        public void processAnimation(IBattlePlayer entity) {
            ItemStack offhand =  ((InventoryPlayerBattle)((EntityPlayer) entity).inventory).getCurrentOffhandWeapon();
            if(offhand != null && offhand.getItem() instanceof IShield){
                entity.setSpecialActionTimer(((IShield)offhand.getItem()).getBashTimer(offhand));
            }
        }
    };


    public abstract void processAnimation(IBattlePlayer entity);

}
