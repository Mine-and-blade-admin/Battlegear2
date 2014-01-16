package mods.battlegear2.utils;

import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.IShield;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public enum EnumBGAnimations {

    OffHandSwing {
        @Override
        public void processAnimation(Entity entity) {
            if (entity instanceof IBattlePlayer)
                ((IBattlePlayer) entity).swingOffItem();
        }
    }, SpecialAction {
        @Override
        public void processAnimation(Entity entity) {
            if (entity instanceof IBattlePlayer){
                ItemStack offhand =  ((InventoryPlayerBattle)((EntityPlayer) entity).inventory).getCurrentOffhandWeapon();

                if(offhand != null && offhand.getItem() instanceof IShield){
                    ((IBattlePlayer) entity).setSpecialActionTimer(((IShield)offhand.getItem()).getBashTimer(offhand));
                }

            }
        }
    };


    public abstract void processAnimation(Entity entity);

}
