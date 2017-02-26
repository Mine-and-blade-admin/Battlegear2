package mods.battlegear2.client.utils;

import mods.battlegear2.api.IBackSheathedRender;
import mods.battlegear2.api.ISheathed;
import mods.battlegear2.api.RenderPlayerEventChild.*;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.api.core.IOffhandRender;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import mods.battlegear2.api.shield.IArrowDisplay;
import mods.battlegear2.api.shield.IShield;
import mods.battlegear2.client.BattlegearClientTickHandeler;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.Sheath;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public final class BattlegearRenderHelper {

    public static final float RENDER_UNIT = 1F/16F;//0.0625
    public static float PROGRESS_INCREMENT_LIMIT = 0.4F;

    private static final ResourceLocation DEFAULT_ARROW = new ResourceLocation("textures/entity/arrow.png");

    public static final float[] arrowX = new float[64];
    public static final float[] arrowY = new float[arrowX.length];
    public static final float[] arrowDepth = new float[arrowX.length];
    public static final float[] arrowPitch = new float[arrowX.length];
    public static final float[] arrowYaw = new float[arrowX.length];

    static{
        for(int i = 0; i < arrowX.length; i++){
            double r = Math.random()*5;
            double theta = Math.random()*Math.PI*2;

            arrowX[i] = (float)(r * Math.cos(theta));
            arrowY[i] = (float)(r * Math.sin(theta));
            arrowDepth[i] = (float)(Math.random()* 0.5 + 0.5F);

            arrowPitch[i] = (float)(Math.random()*50 - 25);
            arrowYaw[i] = (float)(Math.random()*50 - 25);
        }
    }

    /**
     * Add Shield swing for the offhand first person view
     * @param progress Equip Progress for offhand
     * @param player The player
     * @param itemRenderer
     */
    public static void renderItemInFirstPerson(float progress, EntityPlayer player, ItemRenderer itemRenderer) {

        IOffhandRender offhandRender = (IOffhandRender)itemRenderer;
        if (offhandRender.getItemToRender().getItem() instanceof IShield) {

            GlStateManager.pushMatrix();

            float swingProgress =
                    (float)((IBattlePlayer)player).getSpecialActionTimer() / (
                            float)((IShield)offhandRender.getItemToRender().getItem()).getBashTimer(
                            offhandRender.getItemToRender());
            swingProgress = MathHelper.sin(swingProgress * (float) Math.PI);
            GlStateManager.translate(-0.95F + 0.25F * swingProgress,
                    -1.3F - progress * 0.6F,
                    -0.8F - 0.25F * swingProgress);

            if(((IBattlePlayer)player).isBlockingWithShield()){
                GlStateManager.translate(0.25F, 0.15F, 0);
            }

            GlStateManager.rotate(-25, 0, 0, 1);
            GlStateManager.rotate(145 - 35 * swingProgress, 0, 1, 0);

            itemRenderer.renderItem(player, offhandRender.getItemToRender(), ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);//TODO

            GlStateManager.popMatrix();
        }
    }

    /**
     * Add scrolling support for equip animation on offhand
     * @param itemRenderer
     * @param mc
     */
    public static void updateEquippedItem(ItemRenderer itemRenderer, Minecraft mc) {
        IOffhandRender offhandRender = (IOffhandRender)itemRenderer;
        //offhandRender.setPrevEquippedProgress(offhandRender.getEquippedProgress());
        if (mc.player.isRowingBoat() || !BattlegearUtils.isPlayerInBattlemode(mc.player))
            return;
        EntityPlayer var1 = mc.player;
        ItemStack var2 = var1.getHeldItemOffhand();
        if (offhandRender.getItemToRender().isEmpty() || var2.isEmpty())
            return;
        int slot = var1.inventory.currentItem + InventoryPlayerBattle.WEAPON_SETS;
        boolean reequip = offhandRender.getItemToRender().getItem().shouldCauseReequipAnimation(offhandRender.getItemToRender(), var2, offhandRender.getEquippedItemSlot() != slot);
        if(reequip) {
            offhandRender.setEquippedProgress(offhandRender.getPrevEquippedProgress());
            float increment = MathHelper.clamp(-offhandRender.getEquippedProgress(), -PROGRESS_INCREMENT_LIMIT, PROGRESS_INCREMENT_LIMIT);
            offhandRender.setEquippedProgress(offhandRender.getEquippedProgress() + increment);
        }

        if (offhandRender.getEquippedProgress() < 0.1F) {
            offhandRender.setItemToRender(var2);
            offhandRender.setEquippedItemSlot(slot);
        }
    }

    /**
     * Add Shield swing for the offhand third person view
     * @param entity
     * @param biped
     * @param frame
     */
    public static void moveOffHandArm(Entity entity, ModelBiped biped, float frame) {
        if (entity instanceof IBattlePlayer) {
            IBattlePlayer player = (IBattlePlayer) entity;
            float offhandSwing = 0;

            if(player.isBattlemode()){
                ItemStack offhand = ((InventoryPlayerBattle)((EntityPlayer) entity).inventory).getCurrentOffhandWeapon();
                if(offhand.getItem() instanceof IShield){
                    offhandSwing = (float)player.getSpecialActionTimer() / (float)((IShield)offhand.getItem()).getBashTimer(offhand);
                }
            }

            if (offhandSwing > 0) {
                EnumHandSide side = ((EntityPlayer) entity).getPrimaryHand().opposite();
                ModelRenderer model = side == EnumHandSide.LEFT ? biped.bipedLeftArm : biped.bipedRightArm;
                /*if(biped.bipedBody.rotateAngleY!=0){
                    biped.bipedLeftArm.rotateAngleY -= biped.bipedBody.rotateAngleY;
                    biped.bipedLeftArm.rotateAngleX -= biped.bipedBody.rotateAngleY;
                }*/
                biped.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(offhandSwing) * (float)Math.PI * 2) * 0.2F;
                if (side == EnumHandSide.LEFT)
                    biped.bipedBody.rotateAngleY *= -1F;

                biped.bipedRightArm.rotationPointZ = MathHelper.sin(biped.bipedBody.rotateAngleY) * 5.0F;
                biped.bipedRightArm.rotationPointX = -MathHelper.cos(biped.bipedBody.rotateAngleY) * 5.0F;
                biped.bipedLeftArm.rotationPointZ = -MathHelper.sin(biped.bipedBody.rotateAngleY) * 5.0F;
                biped.bipedLeftArm.rotationPointX = MathHelper.cos(biped.bipedBody.rotateAngleY) * 5.0F;
                biped.bipedRightArm.rotateAngleY += biped.bipedBody.rotateAngleY;
                biped.bipedLeftArm.rotateAngleY += biped.bipedBody.rotateAngleY;
                biped.bipedLeftArm.rotateAngleX += biped.bipedBody.rotateAngleY;
                float f6 = 1 - offhandSwing;
                f6 = 1 - f6*f6*f6*f6;
                double f8 = MathHelper.sin(f6 * (float)Math.PI) * 1.2D;
                double f10 = MathHelper.sin(offhandSwing * (float)Math.PI) * -(biped.bipedHead.rotateAngleX - 0.7F) * 0.75F;
                model.rotateAngleX -= f8 + f10;
                model.rotateAngleY += biped.bipedBody.rotateAngleY * 2;
                model.rotateAngleZ += MathHelper.sin(offhandSwing * (float)Math.PI) * -0.4F;
            }
        }
    }

    public static void renderItemIn3rdPerson(EntityPlayer par1EntityPlayer, RenderPlayer render, float frame) {
        if (!((IBattlePlayer) par1EntityPlayer).isBattlemode()) {
            renderSheathedItems(par1EntityPlayer, render, frame);
        }
    }

    public static void renderSheathedItems(EntityPlayer par1EntityPlayer, RenderPlayer render, float frame) {
        if(BattlegearConfig.forceSheath==Sheath.NONE)
            return;

        Pair<Boolean, ModelBiped> chest = getEquippedModel(par1EntityPlayer, render, EntityEquipmentSlot.CHEST);
        Pair<Boolean, ModelBiped> legs = getEquippedModel(par1EntityPlayer, render, EntityEquipmentSlot.LEGS);

        int backCount = chest.getLeft() ? 1 : 0;
        RenderPlayerEvent preRender = new RenderPlayerEvent.Pre(par1EntityPlayer, render, frame);
        RenderPlayerEvent postRender = new RenderPlayerEvent.Post(par1EntityPlayer, render, frame);

        ItemStack sheathed = BattlegearClientTickHandeler.getPreviousMainhand(par1EntityPlayer);
        if(!sheathed.isEmpty() && !(sheathed.getItem() instanceof ItemBlock)){

            boolean onBack = isBackSheathed(sheathed);

            ModelBiped target = getTarget(render, chest.getRight(), legs.getRight(), onBack);

            GlStateManager.pushMatrix();
            target.bipedBody.postRender(RENDER_UNIT);
            if(onBack){
                GlStateManager.translate(0, 5 * RENDER_UNIT, (2.5 + backCount) * RENDER_UNIT);
                GlStateManager.rotate(180, 0, 1, 0);
                if(sheathed.getItem() instanceof IBackSheathedRender){
                    ((IBackSheathedRender)sheathed.getItem()).preRenderBackSheathed(sheathed, backCount, preRender, true);
                }
                backCount++;
            }else{
                GlStateManager.translate(4 * RENDER_UNIT, 10 * RENDER_UNIT, 0);
                if (chest.getLeft() || legs.getLeft()) {
                    GlStateManager.translate(RENDER_UNIT, 0, 0);
                }
                GlStateManager.rotate(270, 0, 1, 0);
            }
            Vector3f scale = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(sheathed).getItemCameraTransforms().thirdperson_right.scale;
            GlStateManager.scale(scale.getX(), scale.getY(), scale.getZ());

            if(!BattlegearUtils.RENDER_BUS.post(new PreRenderSheathed(preRender, onBack, backCount, true, sheathed))){
                renderItemAllPasses(null, true, sheathed);
            }

            BattlegearUtils.RENDER_BUS.post(new PostRenderSheathed(postRender, onBack, backCount, true, sheathed));
            GlStateManager.popMatrix();
        }

        sheathed = BattlegearClientTickHandeler.getPreviousOffhand(par1EntityPlayer);
        if(!sheathed.isEmpty() && !(sheathed.getItem() instanceof ItemBlock)){
            boolean onBack = isBackSheathed(sheathed);

            ModelBiped target = getTarget(render, chest.getRight(), legs.getRight(), onBack);

            GlStateManager.pushMatrix();
            target.bipedBody.postRender(RENDER_UNIT);

            if(onBack){
                GlStateManager.translate(0, 5 * RENDER_UNIT, (2.5 + backCount) * RENDER_UNIT);
                if(sheathed.getItem() instanceof IBackSheathedRender){
                    ((IBackSheathedRender)sheathed.getItem()).preRenderBackSheathed(sheathed, backCount, preRender, false);
                }else if(sheathed.getItem() instanceof IShield){
                    GlStateManager.rotate(180F, 0, 1, 0);
                    GlStateManager.rotate(180F, 0, 0, 1);
                }
                backCount++;
            }else{
                GlStateManager.translate(-4 * RENDER_UNIT, 10 * RENDER_UNIT, 0);
                if (chest.getLeft() || legs.getLeft()) {
                    GlStateManager.translate(- RENDER_UNIT, 0, 0);
                }
                GlStateManager.rotate(270, 0, 1, 0);
            }
            Vector3f scale = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(sheathed).getItemCameraTransforms().thirdperson_left.scale;
            GlStateManager.scale(scale.getX(), scale.getY(), scale.getZ());
            if(!BattlegearUtils.RENDER_BUS.post(new PreRenderSheathed(preRender, onBack, backCount, false, sheathed))){
                renderItemAllPasses(null, false, sheathed);
            }

            BattlegearUtils.RENDER_BUS.post(new PostRenderSheathed(postRender, onBack, backCount, false, sheathed));
            GlStateManager.popMatrix();
        }
    }

    private static ModelBiped getTarget(RenderPlayer render, ModelBase chest, ModelBase legs, boolean onBack) {
        if (chest instanceof ModelBiped) {
            return (ModelBiped) chest;
        } else if (legs instanceof ModelBiped && !onBack) {
            return (ModelBiped) legs;
        }
        return render.getMainModel();
    }

    public static Pair<Boolean, ModelBiped> getEquippedModel(EntityPlayer player, RenderPlayer render, EntityEquipmentSlot slot) {
        ItemStack equip = player.getItemStackFromSlot(slot);
        boolean hasEquip = !equip.isEmpty();
        if (hasEquip) {
            for (Object object : render.layerRenderers) {
                if (object instanceof LayerArmorBase && ((LayerArmorBase) object).getModelFromSlot(slot) instanceof ModelBiped) {
                    return Pair.of(true, ForgeHooksClient.getArmorModel(player, equip, slot, (ModelBiped)((LayerArmorBase) object).getModelFromSlot(slot)));
                }
            }
        }
        return Pair.of(hasEquip, null);
    }

    public static void renderItemAllPasses(EntityLivingBase livingBase, boolean inMainHand, ItemStack itemStack) {
        if (livingBase!=null)
            Minecraft.getMinecraft().getItemRenderer().renderItem(livingBase, itemStack, inMainHand ? ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
        else {
            GlStateManager.pushMatrix();
            boolean flag = Minecraft.getMinecraft().getRenderItem().shouldRenderItemIn3D(itemStack) && Block.getBlockFromItem(itemStack.getItem()).getBlockLayer() == BlockRenderLayer.TRANSLUCENT;

            if (flag)
            {
                GlStateManager.depthMask(false);
            }
            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack, ItemCameraTransforms.TransformType.NONE);//TODO
            if (flag)
            {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }
    }

    private static boolean isBackSheathed(ItemStack sheathed) {
        if(sheathed.getItem() instanceof ISheathed){
            return ((ISheathed) sheathed.getItem()).sheatheOnBack(sheathed);
        }else if (BattlegearUtils.isBow(sheathed.getItem())){
            return true;
        }
        return BattlegearConfig.forceSheath == Sheath.BACK;
    }

    public static void renderArrows(ItemStack stack, boolean isEntity){
        if(stack.getItem() instanceof IArrowDisplay){
            int arrowCount = ((IArrowDisplay)stack.getItem()).getArrowCount(stack);
            //Bounds checking (rendering this many is quite silly, any more would look VERY silly)
            if(arrowCount > 64)
                arrowCount = 64;
            for(int i = 0; i < arrowCount; i++){
                BattlegearRenderHelper.renderArrow(isEntity, i);
            }
        }
    }

    public static void renderArrow(boolean isEntity, int id){
        if(id<arrowX.length){
            float pitch = arrowPitch[id]+90F;
            float yaw = arrowYaw[id]+45F;
            renderArrow(isEntity, arrowX[id], arrowY[id], arrowDepth[id], pitch, yaw);
        }
    }

    public static void renderArrow(boolean isEntity, float x, float y, float depth, float pitch, float yaw){
        GlStateManager.pushMatrix();
        //depth = 1;
        Minecraft.getMinecraft().renderEngine.bindTexture(DEFAULT_ARROW);

        float f10 = 0.05F;
        GlStateManager.scale(f10, f10, f10);
        if(isEntity){
            GlStateManager.scale(1, 1, -1);
        }

        GlStateManager.translate(x + 10.5F, y + 9.5F, 0);

        GlStateManager.rotate(pitch, 0, 1, 0);
        GlStateManager.rotate(yaw, 1, 0, 0);
        GL11.glNormal3f(f10, 0, 0);

        double f2 = 12F/32F * depth;
        double f5 = 5F/32F;
        Tessellator tessellator = Tessellator.getInstance();
        for (int i = 0; i < 2; ++i)
        {
            GlStateManager.rotate(90, 1, 0, 0);
            GL11.glNormal3f(0, 0, f10);
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            tessellator.getBuffer().pos(0, -2, 0).tex(f2, 0).endVertex();
            tessellator.getBuffer().pos(16 * depth, -2, 0).tex(0, 0).endVertex();
            tessellator.getBuffer().pos(16 * depth, 2, 0).tex(0, f5).endVertex();
            tessellator.getBuffer().pos(0, 2, 0).tex(f2, f5).endVertex();
            tessellator.draw();

            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            tessellator.getBuffer().pos(0, 2, 0).tex(f2, f5).endVertex();
            tessellator.getBuffer().pos(16 * depth, 2, 0).tex(0, f5).endVertex();
            tessellator.getBuffer().pos(16 * depth, -2, 0).tex(0, 0).endVertex();
            tessellator.getBuffer().pos(0 * depth, -2, 0).tex(f2, 0).endVertex();
            tessellator.draw();
        }
        GlStateManager.popMatrix();
    }

    public static void renderTexturedQuad(int x, int y, float z, int width, int height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        tessellator.getBuffer().pos((double) (x), (double) (y + height), (double) z).tex(0D, 1D).endVertex();
        tessellator.getBuffer().pos((double) (x + width), (double) (y + height), (double) z).tex(1D, 1D).endVertex();
        tessellator.getBuffer().pos((double) (x + width), (double) (y), (double) z).tex(1D, 0D).endVertex();
        tessellator.getBuffer().pos((double) (x), (double) (y), (double) z).tex(0D, 0D).endVertex();
        tessellator.draw();
    }
}
