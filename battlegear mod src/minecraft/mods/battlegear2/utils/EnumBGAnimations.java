package mods.battlegear2.utils;

import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.shield.IShield;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public enum EnumBGAnimations {

    OffHandSwing {
        @Override
        public void processAnimation(EntityPlayer entity) {
            entity.swingArm(EnumHand.OFF_HAND);
        }
    }, SpecialAction {
        @Override
        public void processAnimation(EntityPlayer entity) {
            ItemStack offhand = entity.getHeldItemMainhand();
            if(offhand.getItem() instanceof IShield){
                ((IBattlePlayer)entity).setSpecialActionTimer(((IShield)offhand.getItem()).getBashTimer(offhand));
            }
        }
    };


    public abstract void processAnimation(EntityPlayer entity);

}
