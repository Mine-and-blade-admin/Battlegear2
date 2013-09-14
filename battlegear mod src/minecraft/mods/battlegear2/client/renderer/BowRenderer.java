package mods.battlegear2.client.renderer;

import mods.battlegear2.BowHookContainerClass2;
import mods.battlegear2.MobHookContainerClass;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.client.ClientProxy;
import mods.battlegear2.client.utils.BattlegearRenderHelper;
import mods.battlegear2.items.ItemMBArrow;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

public class BowRenderer implements IItemRenderer {


    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }


    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type){
            case EQUIPPED_FIRST_PERSON:
                renderEquippedBow(item, (EntityLivingBase) data[1], true);
                break;
            case EQUIPPED:
                renderEquippedBow(item, (EntityLivingBase) data[1], false);
                break;
        }
    }

    private void renderEquippedBow(ItemStack item, EntityLivingBase entityLivingBase, boolean firstPerson) {

        Icon icon = item.getIconIndex();

        ItemStack arrowStack = new ItemStack(Item.arrow);
        int drawAmount = -2;
        if(entityLivingBase instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer)entityLivingBase;

            int timer =  player.getItemInUseDuration();
            if (timer >= 18)
                drawAmount = 2;
            else if (timer > 13)
                drawAmount = 1;
            else if (timer > 0)
                drawAmount = 0;

            ItemStack quiver = BowHookContainerClass2.getArrowContainer(item, (EntityPlayer) entityLivingBase);
            if(quiver != null){
                arrowStack = ((IArrowContainer2)quiver.getItem()).getStackInSlot(quiver, ((IArrowContainer2)quiver.getItem()).getSelectedSlot(quiver));
            }

            if(drawAmount >= 0){
                if(arrowStack != null && (arrowStack.getItem() instanceof ItemMBArrow || arrowStack.getItem().itemID == Item.arrow.itemID)){
                    icon = ClientProxy.bowIcons[drawAmount];
                }else{
                    icon = Item.bow.getItemIconForUseDuration(drawAmount);
                }
            }
        }else if (entityLivingBase instanceof EntitySkeleton){
            int type = entityLivingBase.getDataWatcher().getWatchableObjectByte(MobHookContainerClass.Skell_Arrow_Datawatcher);
            if(type > -1){
                arrowStack = new ItemStack(BattlegearConfig.MbArrows, 1, type);
            }
        }else if (entityLivingBase == null || entityLivingBase.equals(BattlegearRenderHelper.dummyEntity)){
            arrowStack = null;
        }

        Tessellator tessellator = Tessellator.instance;
        ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getOriginX(), icon.getOriginY(), 0.0625F);

        if(arrowStack != null && (arrowStack.getItem() instanceof ItemMBArrow || arrowStack.getItem().itemID == Item.arrow.itemID)){
            icon = arrowStack.getIconIndex();
            GL11.glPushMatrix();
            GL11.glTranslatef(-(-3F+drawAmount)/16F, -(-2F+drawAmount)/16F, firstPerson?-0.5F/16F:0.5F/16F);
            ItemRenderer.renderItemIn2D(tessellator, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getOriginX(), icon.getOriginY(), 0.0625F);
            GL11.glPopMatrix();
        }

        if(item.isItemEnchanted())
            SpearRenderer.renderEnchantmentEffects(tessellator);
    }
}
