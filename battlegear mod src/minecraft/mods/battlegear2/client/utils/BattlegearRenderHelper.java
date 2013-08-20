package mods.battlegear2.client.utils;


import mods.battlegear2.api.IBattlegearWeapon;
import mods.battlegear2.api.IShield;
import mods.battlegear2.client.BattlegearKeyHandeler;
import mods.battlegear2.inventory.InventoryPlayerBattle;
import mods.battlegear2.items.ItemShield;
import mods.battlegear2.items.ItemSpear;
import mods.battlegear2.utils.BattlegearConfig;
import mods.battlegear2.utils.BattlegearUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

public class BattlegearRenderHelper {

    private static final ItemStack dummyStack = new ItemStack(Block.lavaMoving);


    public static void renderItemInFirstPerson(float frame, Minecraft mc, ItemRenderer itemRenderer, ItemStack itemToRender) {

        if (itemRenderer.offHandItemToRender != dummyStack &&
                (itemToRender == null || BattlegearUtils.isOffHand(itemToRender.itemID))) {
            float progress = itemRenderer.prevEquippedOffHandProgress + (itemRenderer.equippedOffHandProgress - itemRenderer.prevEquippedOffHandProgress) * frame;

            EntityClientPlayerMP player = mc.thePlayer;

            float rotation = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * frame;
            GL11.glPushMatrix();
            GL11.glRotatef(rotation, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * frame, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();
            float var6;
            float var7;

            if (player instanceof EntityPlayerSP) {
                EntityPlayerSP var5 = player;
                var6 = var5.prevRenderArmPitch + (var5.renderArmPitch - var5.prevRenderArmPitch) * frame;
                var7 = var5.prevRenderArmYaw + (var5.renderArmYaw - var5.prevRenderArmYaw) * frame;
                GL11.glRotatef((player.rotationPitch - var6) * 0.1F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef((player.rotationYaw - var7) * 0.1F, 0.0F, 1.0F, 0.0F);
            }


            var6 = mc.theWorld.getLightBrightness(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
            var6 = 1.0F;
            int var18 = mc.theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ), 0);
            int var8 = var18 % 65536;
            int var9 = var18 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var8 / 1.0F, (float) var9 / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            float var10;
            float var21;
            float var20;

            if (itemRenderer.offHandItemToRender != null) {
                var18 = Item.itemsList[itemRenderer.offHandItemToRender.itemID].getColorFromItemStack(itemRenderer.offHandItemToRender, 0);
                var20 = (float) (var18 >> 16 & 255) / 255.0F;
                var21 = (float) (var18 >> 8 & 255) / 255.0F;
                var10 = (float) (var18 & 255) / 255.0F;
                GL11.glColor4f(var6 * var20, var6 * var21, var6 * var10, 1.0F);
            } else {
                GL11.glColor4f(var6, var6, var6, 1.0F);
            }

            float var11;
            float var12;
            float var13;
            Render var24;
            RenderPlayer var26;

            if (itemRenderer.offHandItemToRender != null) {

	        	if(itemRenderer.offHandItemToRender.getItem() instanceof IShield){
                    GL11.glPushMatrix();

	        		var7 = 0.8F;


                    float swingProgress =
                            (float)player.specialActionTimer / (
                                    float)((IShield)itemRenderer.offHandItemToRender.getItem()).getBashTimer(
                                    itemRenderer.offHandItemToRender);

	        		GL11.glTranslatef(-0.7F * var7 + 0.25F*MathHelper.sin(swingProgress*3.14159F),
	        				-0.65F * var7 - (1.0F - progress) * 0.6F - 0.4F,
                            -0.9F * var7+0.1F - 0.25F*MathHelper.sin(swingProgress*3.14159F));

	        		if(player.isBlockingWithShield()){
	        			GL11.glTranslatef(0.25F, 0.15F, 0);
	        		}


	        		GL11.glRotatef(25, 0, 0, 1);
	        		GL11.glRotatef(325-35*MathHelper.sin(swingProgress*3.14159F), 0, 1, 0);


	        		itemRenderer.renderItem(player, itemRenderer.offHandItemToRender, 0);

	        		GL11.glPopMatrix();


	        	}else{
                    GL11.glPushMatrix();
                    var7 = 0.8F;

                    if (player.getItemInUseCount() > 0) {
                        EnumAction action = itemRenderer.offHandItemToRender.getItemUseAction();

                        if (action == EnumAction.eat || action == EnumAction.drink) {
                            var21 = (float) player.getItemInUseCount() - frame + 1.0F;
                            var10 = 1.0F - var21 / (float) itemRenderer.offHandItemToRender.getMaxItemUseDuration();
                            var11 = 1.0F - var10;
                            var11 = var11 * var11 * var11;
                            var11 = var11 * var11 * var11;
                            var11 = var11 * var11 * var11;
                            var12 = 1.0F - var11;
                            GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(var21 / 4.0F * (float) Math.PI) * 0.1F) * (float) ((double) var10 > 0.2D ? 1 : 0), 0.0F);
                            GL11.glTranslatef(var12 * 0.6F, -var12 * 0.5F, 0.0F);
                            GL11.glRotatef(var12 * 90.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glRotatef(var12 * 10.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(var12 * 30.0F, 0.0F, 0.0F, 1.0F);
                        }
                    } else {
                        var20 = player.getOffSwingProgress(frame);
                        var21 = MathHelper.sin(var20 * (float) Math.PI);
                        var10 = MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI);
                        //Flip the (x direction)
                        GL11.glTranslatef(var10 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI * 2.0F) * 0.2F, -var21 * 0.2F);
                    }
                    //Translate x in the opposite direction
                    GL11.glTranslatef(-0.7F * var7, -0.65F * var7 - (1.0F - progress) * 0.6F, -0.9F * var7);

                    //Rotate y in the opposite direction
                    GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);

                    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                    var20 = player.getOffSwingProgress(frame);


                    var21 = MathHelper.sin(var20 * var20 * (float) Math.PI);
                    var10 = MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI);

                    GL11.glRotatef(-var21 * 20.0F, 0.0F, 1.0F, 0.0F);
                    //Rotate z in the opposite direction
                    GL11.glRotatef(var10 * 20.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-var10 * 80.0F, 1.0F, 0.0F, 0.0F);

                    //Rotate y back to original position + 45
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);


                    var11 = 0.4F;
                    GL11.glScalef(var11, var11, var11);
                    float var14;
                    float var15;

                    if (player.getItemInUseCount() > 0) {
                        EnumAction action = itemRenderer.offHandItemToRender.getItemUseAction();

                        if (action == EnumAction.block) {
                            GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
                            GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
                        } else if (action == EnumAction.bow) {
                            GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                            GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
                            var13 = (float) itemRenderer.offHandItemToRender.getMaxItemUseDuration() - ((float) player.getItemInUseCount() - frame + 1.0F);
                            var14 = var13 / 20.0F;
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
                            var15 = 1.0F + var14 * 0.2F;
                            GL11.glScalef(1.0F, 1.0F, var15);
                            GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
                        }
                    }

                    if (itemRenderer.offHandItemToRender.getItem().shouldRotateAroundWhenRendering()) {
                        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    }

                    if (itemRenderer.offHandItemToRender.getItem().requiresMultipleRenderPasses()) {
                        itemRenderer.renderItem(player, itemRenderer.offHandItemToRender, 0);
                        for (int x = 1; x < itemRenderer.offHandItemToRender.getItem().getRenderPasses(itemRenderer.offHandItemToRender.getItemDamage()); x++) {
                            int var25 = Item.itemsList[itemRenderer.offHandItemToRender.itemID].getColorFromItemStack(itemRenderer.offHandItemToRender, x);
                            var13 = (float) (var25 >> 16 & 255) / 255.0F;
                            var14 = (float) (var25 >> 8 & 255) / 255.0F;
                            var15 = (float) (var25 & 255) / 255.0F;
                            GL11.glColor4f(var6 * var13, var6 * var14, var6 * var15, 1.0F);
                            itemRenderer.renderItem(player, itemRenderer.offHandItemToRender, x);
                        }
                    } else {
                        itemRenderer.renderItem(player, itemRenderer.offHandItemToRender, 0);
                    }

                    GL11.glPopMatrix();
                }
            } else if (!player.isInvisible()) {
                GL11.glPushMatrix();
                var7 = 0.8F;


                GL11.glScalef(-1.0F, 1.0F, 1.0F);


                var20 = player.getOffSwingProgress(frame);
                var21 = MathHelper.sin(var20 * (float) Math.PI);
                var10 = MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI);
                GL11.glTranslatef(-var10 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI * 2.0F) * 0.4F, -var21 * 0.4F);
                GL11.glTranslatef(0.8F * var7, -0.75F * var7 - (1.0F - progress) * 0.6F, -0.9F * var7);

                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);

                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                var20 = player.getOffSwingProgress(frame);
                var21 = MathHelper.sin(var20 * var20 * (float) Math.PI);
                var10 = MathHelper.sin(MathHelper.sqrt_float(var20) * (float) Math.PI);
                GL11.glRotatef(var10 * 70.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(var21 * 20.0F, 0.0F, 0.0F, 1.0F);


                mc.func_110434_K().func_110577_a(player.func_110306_p());
                GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
                GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);

                GL11.glScalef(1.0F, 1.0F, -1.0F);
                GL11.glTranslatef(5.6F, 0.0F, 0.0F);
                var24 = RenderManager.instance.getEntityRenderObject(mc.thePlayer);
                var26 = (RenderPlayer) var24;
                var13 = 1.0F;
                GL11.glScalef(var13, var13, var13);
                var26.renderFirstPersonArm(mc.thePlayer);
                GL11.glPopMatrix();
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
        }
    }

    public static void updateEquippedItem(ItemRenderer itemRenderer, Minecraft mc, ItemStack mainhandToRender) {
        itemRenderer.prevEquippedOffHandProgress = itemRenderer.equippedOffHandProgress;
        EntityClientPlayerMP var1 = mc.thePlayer;
        ItemStack var2 = var1.isBattlemode() && itemRenderer.equippedItemOffhandSlot > 0 ?
                var1.inventory.getStackInSlot(itemRenderer.equippedItemOffhandSlot) : dummyStack;

        boolean var3 = itemRenderer.equippedItemOffhandSlot == var1.inventory.currentItem + 3 && var2 == itemRenderer.offHandItemToRender;

        if (itemRenderer.offHandItemToRender == null && var2 == null) {
            var3 = true;
        }

        if (var2 != null &&
                itemRenderer.offHandItemToRender != null &&
                var2 != itemRenderer.offHandItemToRender &&
                var2.itemID == itemRenderer.offHandItemToRender.itemID &&
                var2.getItemDamage() == itemRenderer.offHandItemToRender.getItemDamage()) {
            itemRenderer.offHandItemToRender = var2;
            var3 = true;
        }


        ItemStack offhand = var1.isBattlemode() ? var1.inventory.getStackInSlot(var1.inventory.currentItem + 3) : dummyStack;

        offhand = (mainhandToRender == null || BattlegearUtils.isMainHand(mainhandToRender.itemID) ||
                (BattlegearUtils.allowsShield(mainhandToRender.itemID) && offhand != null && offhand.getItem() instanceof IShield)) ? offhand : dummyStack;
        var3 = var3 & (itemRenderer.equippedItemOffhandSlot == var1.inventory.currentItem + 3 && offhand == itemRenderer.offHandItemToRender);

        float var4 = 0.4F;
        float var5 = var3 ? 1.0F : 0.0F;
        float var6 = var5 - itemRenderer.equippedOffHandProgress;

        if (var6 < -var4) {
            var6 = -var4;
        }

        if (var6 > var4) {
            var6 = var4;
        }

        itemRenderer.equippedOffHandProgress += var6;

        if (itemRenderer.equippedOffHandProgress < 0.1F) {
            itemRenderer.offHandItemToRender = var2;
            itemRenderer.equippedItemOffhandSlot = var1.inventory.currentItem + 3;
        }
    }

    public static void moveOffHandArm(Entity entity, ModelBiped biped, float frame) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            float offhandSwing = player.getOffSwingProgress(frame);

            if(((EntityPlayer) entity).isBattlemode()){
                ItemStack offhand = ((InventoryPlayerBattle)((EntityPlayer) entity).inventory).getCurrentOffhandWeapon();
                if(offhand != null && offhand.getItem() instanceof IShield){
                    offhandSwing = (float)player.specialActionTimer / (float)((IShield)offhand.getItem()).getBashTimer(offhand);
                }

                boolean isBlockWithShield = ((EntityPlayer) entity).isBlockingWithShield();
                //biped.heldItemLeft = isBlockWithShield?3:1;

                if(offhand != null)
                    biped.bipedLeftArm.rotateAngleX = biped.bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F) * (isBlockWithShield?3:1);

            }else{
                //biped.heldItemLeft = 0;
            }

            if (offhandSwing > 0.0F) {
                biped.bipedBody.rotateAngleY = -MathHelper.sin(MathHelper.sqrt_float(offhandSwing) * 3.141593F * 2.0F) * 0.2F;

                biped.bipedRightArm.rotationPointZ = MathHelper.sin(biped.bipedBody.rotateAngleY) * 5F;
                biped.bipedRightArm.rotationPointX = -MathHelper.cos(biped.bipedBody.rotateAngleY) * 5F;

                biped.bipedLeftArm.rotationPointZ = -MathHelper.sin(biped.bipedBody.rotateAngleY) * 5F;
                biped.bipedLeftArm.rotationPointX = MathHelper.cos(biped.bipedBody.rotateAngleY) * 5F;

                biped.bipedLeftArm.rotateAngleY += biped.bipedBody.rotateAngleY;
                biped.bipedRightArm.rotateAngleY += biped.bipedBody.rotateAngleY;
                biped.bipedRightArm.rotateAngleX += biped.bipedBody.rotateAngleY;
                float f6 = 1.0F - offhandSwing;
                f6 *= f6;
                f6 *= f6;
                f6 = 1.0F - f6;
                float f8 = MathHelper.sin(f6 * 3.141593F);
                float f10 = MathHelper.sin(offhandSwing * 3.141593F) * -(biped.bipedHead.rotateAngleX - 0.7F) * 0.75F;
                biped.bipedLeftArm.rotateAngleX -= (double) f8 * 1.2D + (double) f10;
                biped.bipedLeftArm.rotateAngleY += biped.bipedBody.rotateAngleY * 2.0F;
                biped.bipedLeftArm.rotateAngleZ = MathHelper.sin(offhandSwing * 3.141593F) * -0.4F;
            }
        }
    }

    public static void renderItemIn3rdPerson(EntityPlayer par1EntityPlayer,
                                             ModelBiped modelBipedMain, double frame) {

        ItemStack var21 = par1EntityPlayer.inventory.getStackInSlot(par1EntityPlayer.inventory.currentItem + 3);

        if (var21 != null && par1EntityPlayer.isBattlemode()) {





            float var7;
            float var8;
            float var11;
            GL11.glPushMatrix();
            modelBipedMain.bipedLeftArm.postRender(0.0625F);
            GL11.glTranslatef(0.0625F, 0.4375F, 0.0625F);

            if (par1EntityPlayer.fishEntity != null) {
                var21 = new ItemStack(Item.stick);
            }

            EnumAction var23 = null;

            if (par1EntityPlayer.getItemInUseCount() > 0) {
                var23 = var21.getItemUseAction();
            }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var21, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, var21, BLOCK_3D));

            if(var21.getItem() instanceof IShield){
                var7 = 0.625F;
                GL11.glScalef(var7, -var7, var7);

                GL11.glTranslated(8F/16F, -11F/16F, -1F/16F);

                GL11.glRotatef(-100.0F+90, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F-90, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(25, 0.0F, 0.0F, 1.0F);


                if (var21.getItem().requiresMultipleRenderPasses()) {
                    for (int var27 = 0; var27 < var21.getItem().getRenderPasses(var21.getItemDamage()); ++var27) {
                        int var26 = var21.getItem().getColorFromItemStack(var21, var27);
                        float var28 = (float) (var26 >> 16 & 255) / 255.0F;
                        float var10 = (float) (var26 >> 8 & 255) / 255.0F;
                        var11 = (float) (var26 & 255) / 255.0F;
                        GL11.glColor4f(var28, var10, var11, 1.0F);
                        RenderManager.instance.itemRenderer.renderItem(par1EntityPlayer, var21, var27);
                    }
                } else {
                    int var27 = var21.getItem().getColorFromItemStack(var21, 0);
                    var8 = (float) (var27 >> 16 & 255) / 255.0F;
                    float var28 = (float) (var27 >> 8 & 255) / 255.0F;
                    float var10 = (float) (var27 & 255) / 255.0F;
                    GL11.glColor4f(var8, var28, var10, 1.0F);
                    RenderManager.instance.itemRenderer.renderItem(par1EntityPlayer, var21, 0);
                }

            }else{

                if (var21.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[var21.itemID].getRenderType()))) {
                    var7 = 0.5F;
                    GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                    var7 *= 0.75F;
                    GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(-var7, -var7, var7);
                } else if (var21.itemID == Item.bow.itemID) {
                    var7 = 0.625F;
                    GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                    GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var7, -var7, var7);
                    GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                } else if (Item.itemsList[var21.itemID].isFull3D()) {
                    var7 = 0.625F;

                    if (Item.itemsList[var21.itemID].shouldRotateAroundWhenRendering()) {
                        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                    }

                    if (par1EntityPlayer.getItemInUseCount() > 0 && var23 == EnumAction.block) {
                        GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                    }

                    GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                    GL11.glScalef(var7, -var7, var7);
                    GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    var7 = 0.375F;
                    GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                    GL11.glScalef(var7, var7, var7);
                    GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                }

                float var10;
                int var27;
                float var28;

                if (var21.getItem().requiresMultipleRenderPasses()) {
                    for (var27 = 0; var27 < var21.getItem().getRenderPasses(var21.getItemDamage()); ++var27) {
                        int var26 = var21.getItem().getColorFromItemStack(var21, var27);
                        var28 = (float) (var26 >> 16 & 255) / 255.0F;
                        var10 = (float) (var26 >> 8 & 255) / 255.0F;
                        var11 = (float) (var26 & 255) / 255.0F;
                        GL11.glColor4f(var28, var10, var11, 1.0F);
                        RenderManager.instance.itemRenderer.renderItem(par1EntityPlayer, var21, var27);
                    }
                } else {
                    var27 = var21.getItem().getColorFromItemStack(var21, 0);
                    var8 = (float) (var27 >> 16 & 255) / 255.0F;
                    var28 = (float) (var27 >> 8 & 255) / 255.0F;
                    var10 = (float) (var27 & 255) / 255.0F;
                    GL11.glColor4f(var8, var28, var10, 1.0F);
                    RenderManager.instance.itemRenderer.renderItem(par1EntityPlayer, var21, 0);
                }
            }

            GL11.glPopMatrix();
        } else {
            if(!par1EntityPlayer.isBattlemode())
                renderSheathedItems(par1EntityPlayer, modelBipedMain, frame);
        }
    }

    private static void renderSheathedItems(EntityPlayer par1EntityPlayer, ModelBiped modelBipedMain, double frame) {

        ItemStack mainhandSheathed = par1EntityPlayer.inventory.getStackInSlot(BattlegearKeyHandeler.previousBattlemode);
        ItemStack offhandSheathed = par1EntityPlayer.inventory.getStackInSlot(BattlegearKeyHandeler.previousBattlemode+InventoryPlayerBattle.WEAPON_SETS);

        ModelBiped chestModel = null;
        ModelBiped legsModel = null;

        boolean hasChestArmour = false;
        boolean hasLegArmour = false;
        ItemStack chest =  par1EntityPlayer.getCurrentItemOrArmor(2);
        if(chest != null){
            chestModel = chest.getItem().getArmorModel(par1EntityPlayer, chest, 1);
            hasChestArmour = true;
        }
        ItemStack legs =  par1EntityPlayer.getCurrentItemOrArmor(3);
        if(legs != null){
            legsModel = legs.getItem().getArmorModel(par1EntityPlayer, legs, 2);
            hasLegArmour = true;
        }

        int backCount = hasChestArmour?1:0;

        if(mainhandSheathed != null){

            boolean onBack = BattlegearConfig.forceBackSheath;
            if(mainhandSheathed.getItem() instanceof IBattlegearWeapon){
                onBack = ((IBattlegearWeapon) mainhandSheathed.getItem()).sheatheOnBack();
            }else if (mainhandSheathed.getItem() instanceof ItemBow){
                onBack = true;
            }

            ModelBiped target = modelBipedMain;
            if(chestModel != null){
                target = chestModel;
            }else if(legsModel != null && !onBack){
                target = legsModel;
            }


            GL11.glPushMatrix();

            if(onBack){
                target.bipedBody.postRender(0.0625F);
                if(mainhandSheathed.getItem() instanceof ItemSpear){
                    GL11.glScalef(0.6F, -0.6F, 0.6F);
                    GL11.glTranslatef(0, -1, 0);
                }else
                    GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glTranslatef(-8F / 16F, 0, 6F / 16F);
                GL11.glRotatef(-5F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(40.0F+90, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0, 0, 4F/16F - backCount*2F/16F);
                backCount++;
            }else{
                target.bipedBody.postRender(0.0625F);
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glTranslatef(8F/16F, 1, -4F/16F);
                if(hasChestArmour || hasLegArmour){
                    GL11.glTranslatef(2F/16F, 0, 0);
                }
                GL11.glRotatef(35F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);
            }


            if (mainhandSheathed.getItem().requiresMultipleRenderPasses()) {
                for (int var27 = 0; var27 < mainhandSheathed.getItem().getRenderPasses(mainhandSheathed.getItemDamage()); ++var27) {
                    int var26 = mainhandSheathed.getItem().getColorFromItemStack(mainhandSheathed, var27);
                    float var28 = (float) (var26 >> 16 & 255) / 255.0F;
                    float var10 = (float) (var26 >> 8 & 255) / 255.0F;
                    float var11 = (float) (var26 & 255) / 255.0F;
                    GL11.glColor4f(var28, var10, var11, 1.0F);
                    RenderManager.instance.itemRenderer.renderItem(par1EntityPlayer, mainhandSheathed, var27);
                }
            } else {
                int var27 = mainhandSheathed.getItem().getColorFromItemStack(mainhandSheathed, 0);
                float var8 = (float) (var27 >> 16 & 255) / 255.0F;
                float var28 = (float) (var27 >> 8 & 255) / 255.0F;
                float var10 = (float) (var27 & 255) / 255.0F;
                GL11.glColor4f(var8, var28, var10, 1.0F);
                RenderManager.instance.itemRenderer.renderItem(par1EntityPlayer, mainhandSheathed, 0);
            }

            GL11.glPopMatrix();


        }

        if(offhandSheathed != null){
            boolean onBack = BattlegearConfig.forceBackSheath;
            if(offhandSheathed.getItem() instanceof IBattlegearWeapon){
                onBack = ((IBattlegearWeapon) offhandSheathed.getItem()).sheatheOnBack();
            }else if (offhandSheathed.getItem() instanceof IShield){
                onBack = true;
            }

            ModelBiped target = modelBipedMain;
            if(chestModel != null){
                target = chestModel;
            }else if(legsModel != null && !onBack){
                target = legsModel;
            }


            GL11.glPushMatrix();

            if(onBack){
                target.bipedBody.postRender(0.0625F);
                if(offhandSheathed.getItem() instanceof IShield){
                    GL11.glScalef(-0.6F, -0.6F, 0.6F);
                }else{
                    GL11.glScalef(-0.6F, 0.6F, 0.6F);
                }
                GL11.glTranslatef(-8F / 16F, -1, 6F / 16F);
                GL11.glRotatef(-5F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(40.0F+90, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0, 0, 4F/16F - backCount*2F/16F);
                backCount++;
            }else{
                target.bipedBody.postRender(0.0625F);
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glTranslatef(-7F/16F, 1, -4F/16F);
                if(hasChestArmour || hasLegArmour){
                    GL11.glTranslatef(-2F/16F, 0, 0);
                }
                GL11.glRotatef(35F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(40.0F, 0.0F, 1.0F, 0.0F);

            }


            if (offhandSheathed.getItem().requiresMultipleRenderPasses()) {
                for (int var27 = 0; var27 < offhandSheathed.getItem().getRenderPasses(offhandSheathed.getItemDamage()); ++var27) {
                    int var26 = offhandSheathed.getItem().getColorFromItemStack(offhandSheathed, var27);
                    float var28 = (float) (var26 >> 16 & 255) / 255.0F;
                    float var10 = (float) (var26 >> 8 & 255) / 255.0F;
                    float var11 = (float) (var26 & 255) / 255.0F;
                    GL11.glColor4f(var28, var10, var11, 1.0F);
                    RenderManager.instance.itemRenderer.renderItem(par1EntityPlayer, offhandSheathed, var27);
                }
            } else {
                int var27 = offhandSheathed.getItem().getColorFromItemStack(offhandSheathed, 0);
                float var8 = (float) (var27 >> 16 & 255) / 255.0F;
                float var28 = (float) (var27 >> 8 & 255) / 255.0F;
                float var10 = (float) (var27 & 255) / 255.0F;
                GL11.glColor4f(var8, var28, var10, 1.0F);
                RenderManager.instance.itemRenderer.renderItem(par1EntityPlayer, offhandSheathed, 0);
            }

            GL11.glPopMatrix();
        }

    }

}
