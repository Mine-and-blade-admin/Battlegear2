package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import net.minecraft.item.ItemBlock;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

@SideOnly(Side.CLIENT)
public class RenderPlayer extends RenderLiving
{
    private ModelBiped modelBipedMain;
    private ModelBiped modelArmorChestplate;
    private ModelBiped modelArmor;
    public static String[] armorFilenamePrefix = new String[] {"cloth", "chain", "iron", "diamond", "gold"};
    public static float NAME_TAG_RANGE = 64.0f;
    public static float NAME_TAG_RANGE_SNEAK = 32.0f;

    public RenderPlayer()
    {
        super(new ModelBiped(0.0F), 0.5F);
        this.modelBipedMain = (ModelBiped)this.mainModel;
        this.modelArmorChestplate = new ModelBiped(1.0F);
        this.modelArmor = new ModelBiped(0.5F);
    }

    protected void func_98191_a(EntityPlayer par1EntityPlayer)
    {
        this.loadDownloadableImageTexture(par1EntityPlayer.skinUrl, par1EntityPlayer.getTexture());
    }

    /**
     * Set the specified armor model as the player model. Args: player, armorSlot, partialTick
     */
    protected int setArmorModel(EntityPlayer par1EntityPlayer, int par2, float par3)
    {
        ItemStack itemstack = par1EntityPlayer.inventory.armorItemInSlot(3 - par2);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                ItemArmor itemarmor = (ItemArmor)item;
                this.loadTexture(ForgeHooksClient.getArmorTexture(par1EntityPlayer, itemstack, "/armor/" + armorFilenamePrefix[itemarmor.renderIndex] + "_" + (par2 == 2 ? 2 : 1) + ".png", par2, 1));
                ModelBiped modelbiped = par2 == 2 ? this.modelArmor : this.modelArmorChestplate;
                modelbiped.bipedHead.showModel = par2 == 0;
                modelbiped.bipedHeadwear.showModel = par2 == 0;
                modelbiped.bipedBody.showModel = par2 == 1 || par2 == 2;
                modelbiped.bipedRightArm.showModel = par2 == 1;
                modelbiped.bipedLeftArm.showModel = par2 == 1;
                modelbiped.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
                modelbiped.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
                modelbiped = ForgeHooksClient.getArmorModel(par1EntityPlayer, itemstack, par2, modelbiped);
                this.setRenderPassModel(modelbiped);

                if (modelbiped != null)
                {
                    modelbiped.onGround = this.mainModel.onGround;
                }

                if (modelbiped != null)
                {
                    modelbiped.isRiding = this.mainModel.isRiding;
                }

                if (modelbiped != null)
                {
                    modelbiped.isChild = this.mainModel.isChild;
                }

                float f1 = 1.0F;

                //Move outside if to allow for more then just CLOTH
                int j = itemarmor.getColor(itemstack);
                if (j != -1)
                {
                    float f2 = (float)(j >> 16 & 255) / 255.0F;
                    float f3 = (float)(j >> 8 & 255) / 255.0F;
                    float f4 = (float)(j & 255) / 255.0F;
                    GL11.glColor3f(f1 * f2, f1 * f3, f1 * f4);

                    if (itemstack.isItemEnchanted())
                    {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(f1, f1, f1);

                if (itemstack.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    protected void func_82439_b(EntityPlayer par1EntityPlayer, int par2, float par3)
    {
        ItemStack itemstack = par1EntityPlayer.inventory.armorItemInSlot(3 - par2);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                ItemArmor itemarmor = (ItemArmor)item;
                this.loadTexture(ForgeHooksClient.getArmorTexture(par1EntityPlayer, itemstack, "/armor/" + armorFilenamePrefix[itemarmor.renderIndex] + "_" + (par2 == 2 ? 2 : 1) + "_b.png", par2, 2));
                float f1 = 1.0F;
                GL11.glColor3f(f1, f1, f1);
            }
        }
    }

    public void renderPlayer(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, float par8, float par9)
    {
        float f2 = 1.0F;
        GL11.glColor3f(f2, f2, f2);
        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = itemstack != null ? 1 : 0;

        if (itemstack != null && par1EntityPlayer.getItemInUseCount() > 0)
        {
            EnumAction enumaction = itemstack.getItemUseAction();

            if (enumaction == EnumAction.block)
            {
                this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 3;
            }
            else if (enumaction == EnumAction.bow)
            {
                this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = true;
            }
        }

        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = par1EntityPlayer.isSneaking();
        double d3 = par4 - (double)par1EntityPlayer.yOffset;

        if (par1EntityPlayer.isSneaking() && !(par1EntityPlayer instanceof EntityPlayerSP))
        {
            d3 -= 0.125D;
        }

        super.doRenderLiving(par1EntityPlayer, par2, d3, par6, par8, par9);
        this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
    }

    /**
     * Method for adding special render rules
     */
    protected void renderSpecials(EntityPlayer par1EntityPlayer, float par2)
    {
        float f1 = 1.0F;
        GL11.glColor3f(f1, f1, f1);
        super.renderEquippedItems(par1EntityPlayer, par2);
        super.renderArrowsStuckInEntity(par1EntityPlayer, par2);
        ItemStack itemstack = par1EntityPlayer.inventory.armorItemInSlot(3);

        if (itemstack != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedHead.postRender(0.0625F);
            float f2;

            if (itemstack != null && itemstack.getItem() instanceof ItemBlock)
            {
                IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
                boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));

                if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType()))
                {
                    f2 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(f2, -f2, -f2);
                }

                this.renderManager.itemRenderer.renderItem(par1EntityPlayer, itemstack, 0);
            }
            else if (itemstack.getItem().itemID == Item.skull.itemID)
            {
                f2 = 1.0625F;
                GL11.glScalef(f2, -f2, -f2);
                String s = "";

                if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("SkullOwner"))
                {
                    s = itemstack.getTagCompound().getString("SkullOwner");
                }

                TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, itemstack.getItemDamage(), s);
            }

            GL11.glPopMatrix();
        }

        float f3;
        float f4;

        if (par1EntityPlayer.username.equals("deadmau5") && this.loadDownloadableImageTexture(par1EntityPlayer.skinUrl, (String)null))
        {
            for (int i = 0; i < 2; ++i)
            {
                float f5 = par1EntityPlayer.prevRotationYaw + (par1EntityPlayer.rotationYaw - par1EntityPlayer.prevRotationYaw) * par2 - (par1EntityPlayer.prevRenderYawOffset + (par1EntityPlayer.renderYawOffset - par1EntityPlayer.prevRenderYawOffset) * par2);
                f3 = par1EntityPlayer.prevRotationPitch + (par1EntityPlayer.rotationPitch - par1EntityPlayer.prevRotationPitch) * par2;
                GL11.glPushMatrix();
                GL11.glRotatef(f5, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(f3, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.375F * (float)(i * 2 - 1), 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.375F, 0.0F);
                GL11.glRotatef(-f3, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-f5, 0.0F, 1.0F, 0.0F);
                f4 = 1.3333334F;
                GL11.glScalef(f4, f4, f4);
                this.modelBipedMain.renderEars(0.0625F);
                GL11.glPopMatrix();
            }
        }

        float f6;

        if (this.loadDownloadableImageTexture(par1EntityPlayer.cloakUrl, (String)null) && !par1EntityPlayer.isInvisible() && !par1EntityPlayer.getHideCape())
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.125F);
            double d0 = par1EntityPlayer.field_71091_bM + (par1EntityPlayer.field_71094_bP - par1EntityPlayer.field_71091_bM) * (double)par2 - (par1EntityPlayer.prevPosX + (par1EntityPlayer.posX - par1EntityPlayer.prevPosX) * (double)par2);
            double d1 = par1EntityPlayer.field_71096_bN + (par1EntityPlayer.field_71095_bQ - par1EntityPlayer.field_71096_bN) * (double)par2 - (par1EntityPlayer.prevPosY + (par1EntityPlayer.posY - par1EntityPlayer.prevPosY) * (double)par2);
            double d2 = par1EntityPlayer.field_71097_bO + (par1EntityPlayer.field_71085_bR - par1EntityPlayer.field_71097_bO) * (double)par2 - (par1EntityPlayer.prevPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.prevPosZ) * (double)par2);
            f6 = par1EntityPlayer.prevRenderYawOffset + (par1EntityPlayer.renderYawOffset - par1EntityPlayer.prevRenderYawOffset) * par2;
            double d3 = (double)MathHelper.sin(f6 * (float)Math.PI / 180.0F);
            double d4 = (double)(-MathHelper.cos(f6 * (float)Math.PI / 180.0F));
            float f7 = (float)d1 * 10.0F;

            if (f7 < -6.0F)
            {
                f7 = -6.0F;
            }

            if (f7 > 32.0F)
            {
                f7 = 32.0F;
            }

            float f8 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            float f9 = (float)(d0 * d4 - d2 * d3) * 100.0F;

            if (f8 < 0.0F)
            {
                f8 = 0.0F;
            }

            float f10 = par1EntityPlayer.prevCameraYaw + (par1EntityPlayer.cameraYaw - par1EntityPlayer.prevCameraYaw) * par2;
            f7 += MathHelper.sin((par1EntityPlayer.prevDistanceWalkedModified + (par1EntityPlayer.distanceWalkedModified - par1EntityPlayer.prevDistanceWalkedModified) * par2) * 6.0F) * 32.0F * f10;

            if (par1EntityPlayer.isSneaking())
            {
                f7 += 25.0F;
            }

            GL11.glRotatef(6.0F + f8 / 2.0F + f7, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f9 / 2.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f9 / 2.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            this.modelBipedMain.renderCloak(0.0625F);
            GL11.glPopMatrix();
        }

        ItemStack itemstack1 = par1EntityPlayer.inventory.getCurrentItem();

        if (itemstack1 != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

            if (par1EntityPlayer.fishEntity != null)
            {
                itemstack1 = new ItemStack(Item.stick);
            }

            EnumAction enumaction = null;

            if (par1EntityPlayer.getItemInUseCount() > 0)
            {
                enumaction = itemstack1.getItemUseAction();
            }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack1, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack1, BLOCK_3D));
            boolean isBlock = itemstack1.itemID < Block.blocksList.length && itemstack1.getItemSpriteNumber() == 0;

            if (is3D || (isBlock && RenderBlocks.renderItemIn3d(Block.blocksList[itemstack1.itemID].getRenderType())))
            {
                f3 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f3 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-f3, -f3, f3);
            }
            else if (itemstack1.itemID == Item.bow.itemID)
            {
                f3 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f3, -f3, f3);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (Item.itemsList[itemstack1.itemID].isFull3D())
            {
                f3 = 0.625F;

                if (Item.itemsList[itemstack1.itemID].shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                if (par1EntityPlayer.getItemInUseCount() > 0 && enumaction == EnumAction.block)
                {
                    GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                GL11.glScalef(f3, -f3, f3);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                f3 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f3, f3, f3);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            float f11;
            int j;
            float f12;

            if (itemstack1.getItem().requiresMultipleRenderPasses())
            {
                for (j = 0; j < itemstack1.getItem().getRenderPasses(itemstack1.getItemDamage()); ++j)
                {
                    int k = itemstack1.getItem().getColorFromItemStack(itemstack1, j);
                    f12 = (float)(k >> 16 & 255) / 255.0F;
                    f11 = (float)(k >> 8 & 255) / 255.0F;
                    f6 = (float)(k & 255) / 255.0F;
                    GL11.glColor4f(f12, f11, f6, 1.0F);
                    this.renderManager.itemRenderer.renderItem(par1EntityPlayer, itemstack1, j);
                }
            }
            else
            {
                j = itemstack1.getItem().getColorFromItemStack(itemstack1, 0);
                f4 = (float)(j >> 16 & 255) / 255.0F;
                f12 = (float)(j >> 8 & 255) / 255.0F;
                f11 = (float)(j & 255) / 255.0F;
                GL11.glColor4f(f4, f12, f11, 1.0F);
                this.renderManager.itemRenderer.renderItem(par1EntityPlayer, itemstack1, 0);
            }

            GL11.glPopMatrix();
        }
    }

    protected void renderPlayerScale(EntityPlayer par1EntityPlayer, float par2)
    {
        float f1 = 0.9375F;
        GL11.glScalef(f1, f1, f1);
    }

    protected void func_96450_a(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, String par8Str, float par9, double par10)
    {
        if (par10 < 100.0D)
        {
            Scoreboard scoreboard = par1EntityPlayer.getWorldScoreboard();
            ScoreObjective scoreobjective = scoreboard.func_96539_a(2);

            if (scoreobjective != null)
            {
                Score score = scoreboard.func_96529_a(par1EntityPlayer.getEntityName(), scoreobjective);

                if (par1EntityPlayer.isPlayerSleeping())
                {
                    this.renderLivingLabel(par1EntityPlayer, score.func_96652_c() + " " + scoreobjective.getDisplayName(), par2, par4 - 1.5D, par6, 64);
                }
                else
                {
                    this.renderLivingLabel(par1EntityPlayer, score.func_96652_c() + " " + scoreobjective.getDisplayName(), par2, par4, par6, 64);
                }

                par4 += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * par9);
            }
        }

        super.func_96449_a(par1EntityPlayer, par2, par4, par6, par8Str, par9, par10);
    }

    public void renderFirstPersonArm(EntityPlayer par1EntityPlayer)
    {
        float f = 1.0F;
        GL11.glColor3f(f, f, f);
        this.modelBipedMain.onGround = 0.0F;
        this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, par1EntityPlayer);
        this.modelBipedMain.bipedRightArm.render(0.0625F);
    }

    /**
     * Renders player with sleeping offset if sleeping
     */
    protected void renderPlayerSleep(EntityPlayer par1EntityPlayer, double par2, double par4, double par6)
    {
        if (par1EntityPlayer.isEntityAlive() && par1EntityPlayer.isPlayerSleeping())
        {
            super.renderLivingAt(par1EntityPlayer, par2 + (double)par1EntityPlayer.field_71079_bU, par4 + (double)par1EntityPlayer.field_71082_cx, par6 + (double)par1EntityPlayer.field_71089_bV);
        }
        else
        {
            super.renderLivingAt(par1EntityPlayer, par2, par4, par6);
        }
    }

    /**
     * Rotates the player if the player is sleeping. This method is called in rotateCorpse.
     */
    protected void rotatePlayer(EntityPlayer par1EntityPlayer, float par2, float par3, float par4)
    {
        if (par1EntityPlayer.isEntityAlive() && par1EntityPlayer.isPlayerSleeping())
        {
            GL11.glRotatef(par1EntityPlayer.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.getDeathMaxRotation(par1EntityPlayer), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
        }
        else
        {
            super.rotateCorpse(par1EntityPlayer, par2, par3, par4);
        }
    }

    protected void func_96449_a(EntityLiving par1EntityLiving, double par2, double par4, double par6, String par8Str, float par9, double par10)
    {
        this.func_96450_a((EntityPlayer)par1EntityLiving, par2, par4, par6, par8Str, par9, par10);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLiving par1EntityLiving, float par2)
    {
        this.renderPlayerScale((EntityPlayer)par1EntityLiving, par2);
    }

    protected void func_82408_c(EntityLiving par1EntityLiving, int par2, float par3)
    {
        this.func_82439_b((EntityPlayer)par1EntityLiving, par2, par3);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        return this.setArmorModel((EntityPlayer)par1EntityLiving, par2, par3);
    }

    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
        this.renderSpecials((EntityPlayer)par1EntityLiving, par2);
    }

    protected void rotateCorpse(EntityLiving par1EntityLiving, float par2, float par3, float par4)
    {
        this.rotatePlayer((EntityPlayer)par1EntityLiving, par2, par3, par4);
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(EntityLiving par1EntityLiving, double par2, double par4, double par6)
    {
        this.renderPlayerSleep((EntityPlayer)par1EntityLiving, par2, par4, par6);
    }

    protected void func_98190_a(EntityLiving par1EntityLiving)
    {
        this.func_98191_a((EntityPlayer)par1EntityLiving);
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderPlayer((EntityPlayer)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderPlayer((EntityPlayer)par1Entity, par2, par4, par6, par8, par9);
    }
}
