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
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public final class BattlegearRenderHelper {

    private static final ItemStack dummyStack = new ItemStack(Blocks.flowing_lava);
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

    public static void renderItemInFirstPerson(float frame, Minecraft mc, ItemRenderer itemRenderer) {

        IOffhandRender offhandRender = (IOffhandRender)itemRenderer;

        if (offhandRender.getItemToRender() != dummyStack) {
            float progress = offhandRender.getPrevEquippedProgress() + (offhandRender.getEquippedProgress() - offhandRender.getPrevEquippedProgress()) * frame;

            EntityPlayerSP player = mc.thePlayer;

            float rotation = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * frame;
            GL11.glPushMatrix();
            GL11.glRotatef(rotation, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * frame, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();

            int var18 = mc.theWorld.getCombinedLight(new BlockPos(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ), 0);
            float var6 = var18 & 65535;
            float var7 = var18 >> 16;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var6, var7);

            var6 = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * frame;
            var7 = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * frame;
            GL11.glRotatef((player.rotationPitch - var6) * 0.1F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef((player.rotationYaw - var7) * 0.1F, 0.0F, 1.0F, 0.0F);

            float var10;
            float var21;
            float var20;
            RenderPlayer var26 = (RenderPlayer) mc.getRenderManager().getEntityRenderObject(player);
            RenderPlayerEvent preRender = new RenderPlayerEvent.Pre(player, var26, frame);
            RenderPlayerEvent postRender = new RenderPlayerEvent.Post(player, var26, frame);
            if (offhandRender.getItemToRender() != null) {

                applyColorFromItemStack(offhandRender.getItemToRender(), 0);
                GL11.glPushMatrix();
                if(offhandRender.getItemToRender().getItem() instanceof IShield){

                    float swingProgress =
                            (float)((IBattlePlayer)player).getSpecialActionTimer() / (
                                    float)((IShield)offhandRender.getItemToRender().getItem()).getBashTimer(
                                    offhandRender.getItemToRender());
                    var7 = 0.8F;
                    GL11.glTranslatef(-0.7F * var7 + 0.25F * MathHelper.sin(swingProgress * (float) Math.PI),
                            -0.65F * var7 - (1.0F - progress) * 0.6F - 0.4F,
                            -0.9F * var7 + 0.1F - 0.25F * MathHelper.sin(swingProgress * (float) Math.PI));

	        		if(((IBattlePlayer)player).isBlockingWithShield()){
	        			GL11.glTranslatef(0.25F, 0.15F, 0);
	        		}

	        		GL11.glRotatef(25, 0, 0, 1);
                    GL11.glRotatef(325 - 35 * MathHelper.sin(swingProgress * (float) Math.PI), 0, 1, 0);

	        		if(!BattlegearUtils.RENDER_BUS.post(new PreRenderPlayerElement(preRender, true, PlayerElementType.ItemOffhand, offhandRender.getItemToRender())))
                        itemRenderer.renderItem(player, offhandRender.getItemToRender(), ItemCameraTransforms.TransformType.FIRST_PERSON);
                    BattlegearUtils.RENDER_BUS.post(new PostRenderPlayerElement(postRender, true, PlayerElementType.ItemOffhand, offhandRender.getItemToRender()));

	        	}else{

                    if (player.getItemInUseCount() > 0) {
                        EnumAction action = offhandRender.getItemToRender().getItemUseAction();

                        if (action == EnumAction.EAT || action == EnumAction.DRINK) {
                            var21 = player.getItemInUseCount() - frame + 1.0F;
                            float var11 = var21 / (float) offhandRender.getItemToRender().getMaxItemUseDuration();
                            if (var11 < 0.8F) {
                                var10 = MathHelper.abs(MathHelper.cos(var21 / 4.0F * (float) Math.PI) * 0.1F);
                            } else {
                                var10 = 0;
                            }
                            float var12 = 1.0F - (float) Math.pow((double) var11, 27);
                            GL11.glTranslatef(0.0F, var10, 0.0F);
                            GL11.glTranslatef(var12 * 0.6F, -var12 * 0.5F, 0.0F);
                            GL11.glRotatef(var12 * 90.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glRotatef(var12 * 10.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(var12 * 30.0F, 0.0F, 0.0F, 1.0F);
                        }
                    } else {
                        var20 = ((IBattlePlayer)player).getOffSwingProgress(frame);
                        var21 = MathHelper.sin(var20 * (float) Math.PI);
                        var10 = MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI);
                        //Flip the (x direction)
                        GL11.glTranslatef(var10 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI * 2.0F) * 0.2F, -var21 * 0.2F);
                    }
                    //Translate x in the opposite direction
                    GL11.glTranslatef(-0.56F, -0.52F - (1.0F - progress) * 0.6F, -0.72F);

                    //Rotate y in the opposite direction
                    GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);

                    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                    var20 = ((IBattlePlayer)player).getOffSwingProgress(frame);


                    var21 = MathHelper.sin(var20 * var20 * (float) Math.PI);
                    var10 = MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI);

                    GL11.glRotatef(-var21 * 20.0F, 0.0F, 1.0F, 0.0F);
                    //Rotate z in the opposite direction
                    GL11.glRotatef(var10 * 20.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-var10 * 80.0F, 1.0F, 0.0F, 0.0F);

                    //Rotate y back to original position + 45
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);

                    GL11.glScalef(0.4F, 0.4F, 0.4F);

                    if (player.getItemInUseCount() > 0) {
                        EnumAction action = offhandRender.getItemToRender().getItemUseAction();

                        if (action == EnumAction.BLOCK) {
                            GL11.glTranslatef(0.0F, 0.2F, 0.0F);
                            GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
                        } else if (action == EnumAction.BOW) {
                            GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
                            float var13 = (float) offhandRender.getItemToRender().getMaxItemUseDuration() - ((float) player.getItemInUseCount() - frame + 1.0F);
                            float var14 = var13 / 20.0F;
                            var14 = (var14 * var14 + var14 * 2.0F) / 3.0F;

                            if (var14 > 1.0F) {
                                var14 = 1.0F;
                            }

                            if (var14 > 0.1F) {
                                GL11.glTranslatef(0.0F, MathHelper.sin((var13 - 0.1F) * 1.3F) * 0.01F * (var14 - 0.1F), 0.0F);
                            }

                            GL11.glTranslatef(0.0F, 0.0F, var14 * 0.1F);
                            GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                            GL11.glScalef(1.0F, 1.0F, 1.0F + var14 * 0.2F);
                            GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
                        }
                    }

                    if (offhandRender.getItemToRender().getItem().shouldRotateAroundWhenRendering()) {
                        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    }
                    if(!BattlegearUtils.RENDER_BUS.post(new PreRenderPlayerElement(preRender, true, PlayerElementType.ItemOffhand, offhandRender.getItemToRender()))){

                        itemRenderer.renderItem(player, offhandRender.getItemToRender(), ItemCameraTransforms.TransformType.FIRST_PERSON);
                    }
                    BattlegearUtils.RENDER_BUS.post(new PostRenderPlayerElement(postRender, true, PlayerElementType.ItemOffhand, offhandRender.getItemToRender()));

                }
                GL11.glPopMatrix();
            } else if (!player.isInvisible()) {
                GL11.glPushMatrix();

                GL11.glScalef(-1.0F, 1.0F, 1.0F);

                var20 = ((IBattlePlayer)player).getOffSwingProgress(frame);
                var21 = MathHelper.sin(var20 * (float) Math.PI);
                var10 = MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI);
                GL11.glTranslatef(-var10 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI * 2.0F) * 0.4F, -var21 * 0.4F);
                GL11.glTranslatef(0.64F, -0.6F - (1.0F - progress) * 0.6F, -0.72F);

                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                var21 = MathHelper.sin(var20 * var20 * (float) Math.PI);
                GL11.glRotatef(var10 * 70.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(var21 * 20.0F, 0.0F, 0.0F, 1.0F);

                mc.getTextureManager().bindTexture(player.getLocationSkin());
                GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
                GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);

                GL11.glScalef(1.0F, 1.0F, -1.0F);
                GL11.glTranslatef(5.6F, 0.0F, 0.0F);
                GL11.glScalef(1.0F, 1.0F, 1.0F);
                if(!BattlegearUtils.RENDER_BUS.post(new PreRenderPlayerElement(preRender, true, PlayerElementType.Offhand, null))) {
                    var26.renderRightArm(player);
                }
                BattlegearUtils.RENDER_BUS.post(new PostRenderPlayerElement(postRender, true, PlayerElementType.Offhand, null));
	        		
                GL11.glPopMatrix();
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
        }
    }

    public static void updateEquippedItem(ItemRenderer itemRenderer, Minecraft mc) {
        IOffhandRender offhandRender = (IOffhandRender)itemRenderer;
        offhandRender.setPrevEquippedProgress(offhandRender.getEquippedProgress());
        EntityPlayer var1 = mc.thePlayer;
        int slot = var1.inventory.currentItem + InventoryPlayerBattle.WEAPON_SETS;
        ItemStack var2 = ((IBattlePlayer)var1).isBattlemode() ? var1.inventory.getStackInSlot(slot) : dummyStack;

        boolean sameItem = offhandRender.getEquippedItemSlot() == slot && var2 == offhandRender.getItemToRender();

        if (offhandRender.getItemToRender() == null && var2 == null) {
            sameItem = true;
        } else if (var2 != null && offhandRender.getItemToRender() != null) {
            sameItem = offhandRender.getItemToRender().getIsItemStackEqual(var2);
        }

        float increment = (sameItem ? 1.0F : 0.0F) - offhandRender.getEquippedProgress();

        if (increment < -PROGRESS_INCREMENT_LIMIT) {
            increment = -PROGRESS_INCREMENT_LIMIT;
        }

        if (increment > PROGRESS_INCREMENT_LIMIT) {
            increment = PROGRESS_INCREMENT_LIMIT;
        }

        offhandRender.setEquippedProgress(offhandRender.getEquippedProgress()+increment);

        if (offhandRender.getEquippedProgress() < 0.1F) {
            offhandRender.setItemToRender(var2);
            offhandRender.setEquippedItemSlot(slot);
        }
    }

    public static void moveOffHandArm(Entity entity, ModelBiped biped, float frame) {
        if (entity instanceof IBattlePlayer) {
            IBattlePlayer player = (IBattlePlayer) entity;
            float offhandSwing = 0.0F;

            if(player.isBattlemode()){
                ItemStack offhand = ((InventoryPlayerBattle)((EntityPlayer) entity).inventory).getCurrentOffhandWeapon();
                if(offhand != null && offhand.getItem() instanceof IShield){
                    offhandSwing = (float)player.getSpecialActionTimer() / (float)((IShield)offhand.getItem()).getBashTimer(offhand);
                }else{
                    offhandSwing = player.getOffSwingProgress(frame);
                }
            }

            if (offhandSwing > 0.0F) {
                if(biped.bipedBody.rotateAngleY!=0.0F){
                    biped.bipedLeftArm.rotateAngleY -= biped.bipedBody.rotateAngleY;
                    biped.bipedLeftArm.rotateAngleX -= biped.bipedBody.rotateAngleY;
                }
                biped.bipedBody.rotateAngleY = -MathHelper.sin(MathHelper.sqrt_float(offhandSwing) * (float)Math.PI * 2.0F) * 0.2F;

                //biped.bipedRightArm.rotationPointZ = MathHelper.sin(biped.bipedBody.rotateAngleY) * 5.0F;
                //biped.bipedRightArm.rotationPointX = -MathHelper.cos(biped.bipedBody.rotateAngleY) * 5.0F;

                biped.bipedLeftArm.rotationPointZ = -MathHelper.sin(biped.bipedBody.rotateAngleY) * 5.0F;
                biped.bipedLeftArm.rotationPointX = MathHelper.cos(biped.bipedBody.rotateAngleY) * 5.0F;

                //biped.bipedRightArm.rotateAngleY += biped.bipedBody.rotateAngleY;
                //biped.bipedRightArm.rotateAngleX += biped.bipedBody.rotateAngleY;
                float f6 = 1.0F - offhandSwing;
                f6 = 1.0F - f6*f6*f6;
                double f8 = MathHelper.sin(f6 * (float)Math.PI) * 1.2D;
                double f10 = MathHelper.sin(offhandSwing * (float)Math.PI) * -(biped.bipedHead.rotateAngleX - 0.7F) * 0.75F;
                biped.bipedLeftArm.rotateAngleX -= f8 + f10;
                biped.bipedLeftArm.rotateAngleY += biped.bipedBody.rotateAngleY * 3.0F;
                biped.bipedLeftArm.rotateAngleZ = MathHelper.sin(offhandSwing * (float)Math.PI) * -0.4F;
            }
        }
    }

    public static void renderItemIn3rdPerson(EntityPlayer par1EntityPlayer, RenderPlayer render, float frame) {

        ItemStack var21 = ((InventoryPlayerBattle) par1EntityPlayer.inventory).getCurrentOffhandWeapon();

        if (var21 != null) {
            float var7;
            RenderPlayerEvent preRender = new RenderPlayerEvent.Pre(par1EntityPlayer, render, frame);
            RenderPlayerEvent postRender = new RenderPlayerEvent.Post(par1EntityPlayer, render, frame);
            
            GL11.glPushMatrix();
            render.getPlayerModel().bipedLeftArm.postRender(RENDER_UNIT);

            GL11.glTranslatef(RENDER_UNIT, 7*RENDER_UNIT, RENDER_UNIT);

            if (par1EntityPlayer.fishEntity != null) {
                var21 = new ItemStack(Items.fishing_rod, 0);
            }

            if (var21.getItem() instanceof ItemBlock && Block.getBlockFromItem(var21.getItem()).getRenderType() == 2) {
                GL11.glTranslatef(0.0F, 3 * RENDER_UNIT, -5 * RENDER_UNIT);
                var7 = 0.375F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-var7, -var7, var7);
            } /*else if (BattlegearUtils.isBow(var21.getItem())) {
                var7 = 10*RENDER_UNIT;
                GL11.glTranslatef(0, 2*RENDER_UNIT, 5*RENDER_UNIT);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var7, -var7, var7);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (var21.getItem().isFull3D()) {
                var7 = 10*RENDER_UNIT;

                if (var21.getItem().shouldRotateAroundWhenRendering()) {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0, -2*RENDER_UNIT, 0);
                }

                if (par1EntityPlayer.getItemInUseCount() > 0 && var21.getItemUseAction() == EnumAction.BLOCK) {
                    GL11.glTranslatef(-0.05F, 0.0F, -0.1F);
                    GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glTranslatef(0, 3*RENDER_UNIT, 0);
                GL11.glScalef(var7, -var7, var7);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else {
                var7 = 6*RENDER_UNIT;
                GL11.glTranslatef(4*RENDER_UNIT, 3*RENDER_UNIT, -3*RENDER_UNIT);
                GL11.glScalef(var7, var7, var7);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }*/

            if (!BattlegearUtils.RENDER_BUS.post(new PreRenderPlayerElement(preRender, false, PlayerElementType.ItemOffhand, var21))) {
                renderItemAllPasses(par1EntityPlayer, var21);
            }
            BattlegearUtils.RENDER_BUS.post(new PostRenderPlayerElement(postRender, false, PlayerElementType.ItemOffhand, var21));
            GL11.glPopMatrix();
        } else if (!((IBattlePlayer) par1EntityPlayer).isBattlemode()) {
            renderSheathedItems(par1EntityPlayer, render, frame);
        }
    }

    private static void renderSheathedItems(EntityPlayer par1EntityPlayer, RenderPlayer render, float frame) {
        if(BattlegearConfig.forceSheath==Sheath.NONE)
            return;
        ItemStack mainhandSheathed = BattlegearClientTickHandeler.getPreviousMainhand(par1EntityPlayer);
        ItemStack offhandSheathed = BattlegearClientTickHandeler.getPreviousOffhand(par1EntityPlayer);

        Pair<Boolean, ModelBase> chest = getEquippedModel(par1EntityPlayer, render, 3);
        Pair<Boolean, ModelBase> legs = getEquippedModel(par1EntityPlayer, render, 2);

        int backCount = chest.getLeft() ? 1 : 0;
        RenderPlayerEvent preRender = new RenderPlayerEvent.Pre(par1EntityPlayer, render, frame);
        RenderPlayerEvent postRender = new RenderPlayerEvent.Post(par1EntityPlayer, render, frame);
        
        if(mainhandSheathed != null && !(mainhandSheathed.getItem() instanceof ItemBlock)){

            boolean onBack = isBackSheathed(mainhandSheathed);

            ModelBiped target = getTarget(render, chest.getRight(), legs.getRight(), onBack);

            GL11.glPushMatrix();
            target.bipedBody.postRender(RENDER_UNIT);
            if(onBack){
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glTranslatef(0, 10 * RENDER_UNIT, 4 * RENDER_UNIT + backCount * RENDER_UNIT);
                GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
                if(mainhandSheathed.getItem() instanceof IBackSheathedRender){
                    ((IBackSheathedRender)mainhandSheathed.getItem()).preRenderBackSheathed(mainhandSheathed, backCount, preRender, true);
                }
                backCount++;
            }else{
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glTranslatef(7 * RENDER_UNIT, 20 * RENDER_UNIT, 0);
                if (chest.getLeft() || legs.getLeft()) {
                    GL11.glTranslatef(2*RENDER_UNIT, 0, 0);
                }
                GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
            }

            if(!BattlegearUtils.RENDER_BUS.post(new PreRenderSheathed(preRender, onBack, backCount, true, mainhandSheathed))){
                renderItemAllPasses(null, mainhandSheathed);
            }

            BattlegearUtils.RENDER_BUS.post(new PostRenderSheathed(postRender, onBack, backCount, true, mainhandSheathed));
            
            GL11.glPopMatrix();
        }

        if(offhandSheathed != null && !(offhandSheathed.getItem() instanceof ItemBlock)){
            boolean onBack = isBackSheathed(offhandSheathed);

            ModelBiped target = getTarget(render, chest.getRight(), legs.getRight(), onBack);

            GL11.glPushMatrix();
            target.bipedBody.postRender(RENDER_UNIT);

            if(onBack){
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glTranslatef(0, 10 * RENDER_UNIT, 4 * RENDER_UNIT + backCount * RENDER_UNIT);
                if(offhandSheathed.getItem() instanceof IBackSheathedRender){
                    ((IBackSheathedRender)offhandSheathed.getItem()).preRenderBackSheathed(offhandSheathed, backCount, preRender, false);
                }else if(offhandSheathed.getItem() instanceof IShield){
                    GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
                }
                backCount++;
            }else{
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glTranslatef(-7 * RENDER_UNIT, 20 * RENDER_UNIT, 0);
                if (chest.getLeft() || legs.getLeft()) {
                    GL11.glTranslatef(-2*RENDER_UNIT, 0, 0);
                }
                GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
            }
            if(!BattlegearUtils.RENDER_BUS.post(new PreRenderSheathed(preRender, onBack, backCount, false, offhandSheathed))){
                renderItemAllPasses(null, offhandSheathed);
            }

            BattlegearUtils.RENDER_BUS.post(new PostRenderSheathed(postRender, onBack, backCount, false, offhandSheathed));
            GL11.glPopMatrix();
        }
    }

    private static ModelBiped getTarget(RenderPlayer render, ModelBase chest, ModelBase legs, boolean onBack) {
        if (chest instanceof ModelBiped) {
            return (ModelBiped) chest;
        } else if (legs instanceof ModelBiped && !onBack) {
            return (ModelBiped) legs;
        }
        return render.getPlayerModel();
    }

    public static Pair<Boolean, ModelBase> getEquippedModel(EntityPlayer player, RenderPlayer render, int slot) {
        ItemStack equip = player.getEquipmentInSlot(slot);
        boolean hasEquip = equip != null;
        if (hasEquip) {
            for (Object object : render.layerRenderers) {
                if (object instanceof LayerArmorBase) {
                    return Pair.of(true, ForgeHooksClient.getArmorModel(player, equip, slot, ((LayerArmorBase) object).func_177175_a(slot)));
                }
            }
        }
        return Pair.of(hasEquip, null);
    }

    public static void renderItemAllPasses(EntityLivingBase livingBase, ItemStack itemStack) {
        Minecraft.getMinecraft().getItemRenderer().renderItem(livingBase, itemStack, livingBase != null ? ItemCameraTransforms.TransformType.THIRD_PERSON : ItemCameraTransforms.TransformType.NONE);
    }

    public static void applyColorFromItemStack(ItemStack itemStack, int pass){
        int col = itemStack.getItem().getColorFromItemStack(itemStack, pass);
        float r = (float) (col >> 16 & 255) / 255.0F;
        float g = (float) (col >> 8 & 255) / 255.0F;
        float b = (float) (col & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);
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
        GL11.glPushMatrix();
        //depth = 1;
        Minecraft.getMinecraft().renderEngine.bindTexture(DEFAULT_ARROW);

        float f10 = 0.05F;
        GL11.glScalef(f10, f10, f10);
        if(isEntity){
            GL11.glScalef(1, 1, -1);
        }

        GL11.glTranslatef(x + 10.5F, y + 9.5F, 0);

        GL11.glRotatef(pitch, 0, 1, 0);
        GL11.glRotatef(yaw, 1, 0, 0);
        GL11.glNormal3f(f10, 0, 0);

        double f2 = 12F/32F * depth;
        double f5 = 5 / 32.0F;
        Tessellator tessellator = Tessellator.getInstance();
        for (int i = 0; i < 2; ++i)
        {
            GL11.glRotatef(90, 1, 0, 0);
            GL11.glNormal3f(0, 0, f10);
            tessellator.getWorldRenderer().startDrawingQuads();
            tessellator.getWorldRenderer().addVertexWithUV(0, -2, 0, f2, 0);
            tessellator.getWorldRenderer().addVertexWithUV(16 * depth, -2, 0, 0, 0);
            tessellator.getWorldRenderer().addVertexWithUV(16 * depth, 2, 0, 0, f5);
            tessellator.getWorldRenderer().addVertexWithUV(0, 2, 0, f2, f5);
            tessellator.draw();

            tessellator.getWorldRenderer().startDrawingQuads();
            tessellator.getWorldRenderer().addVertexWithUV(0, 2, 0, f2, f5);
            tessellator.getWorldRenderer().addVertexWithUV(16 * depth, 2, 0, 0, f5);
            tessellator.getWorldRenderer().addVertexWithUV(16 * depth, -2, 0, 0, 0);
            tessellator.getWorldRenderer().addVertexWithUV(0 * depth, -2, 0, f2, 0);
            tessellator.draw();
        }
        GL11.glPopMatrix();
    }

    public static void renderTexturedQuad(int x, int y, float z, int width, int height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().startDrawingQuads();
        tessellator.getWorldRenderer().addVertexWithUV((double) (x + 0), (double) (y + height), (double) z, 0D, 1D);
        tessellator.getWorldRenderer().addVertexWithUV((double) (x + width), (double) (y + height), (double) z, 1D, 1D);
        tessellator.getWorldRenderer().addVertexWithUV((double) (x + width), (double) (y + 0), (double) z, 1D, 0D);
        tessellator.getWorldRenderer().addVertexWithUV((double) (x + 0), (double) (y + 0), (double) z, 0D, 0D);
        tessellator.draw();
    }
}
