package mods.battlegear2.client.renderer;

import com.google.common.collect.ImmutableMap;
import mods.battlegear2.Battlegear;
import mods.battlegear2.MobHookContainerClass;
import mods.battlegear2.api.quiver.IArrowContainer2;
import mods.battlegear2.api.quiver.QuiverArrowRegistry;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;

public class BowRenderer extends BaseModelLoader{

    @SubscribeEvent
    public void onBakeModel(ModelBakeEvent bakeEvent){
        setLoader(bakeEvent.modelLoader);
        for(int i = 0; i < ItemBow.bowPullIconNameArray.length; i++) {
            ModelResourceLocation defaultResourceLocation = new ModelResourceLocation("bow_" + ItemBow.bowPullIconNameArray[i], "inventory");
            IModel bowModel = getModel(new ResourceLocation(Battlegear.MODID, "item/" + defaultResourceLocation.getResourcePath()));
            IModel arrowModel = getModel(new ResourceLocation(Battlegear.MODID, "item/arr" + defaultResourceLocation.getResourcePath().substring(1)));
            /*if(originalModel instanceof IRetexturableModel){
                ModelBlock internalFrontModel = getInternalModel(((IRetexturableModel) originalModel).retexture(ImmutableMap.of("layer1", BattlegearConfig.MODID + "items/mb.arrow.ender")));
                if (internalFrontModel != null) {
                    ModelBlock front = makeItem(internalFrontModel);
                    if (front != null) {
                        IFlexibleBakedModel baked = wrap(front);
                        bakeEvent.modelRegistry.putObject(defaultResourceLocation, baked);
                    }
                }
            }*/
        }
        //bakeEvent.modelRegistry.putObject(defaultResourceLocation, object);
        setLoader(null);
    }

    public void renderEquippedBow(ItemStack item, EntityLivingBase entityLivingBase, boolean firstPerson) {
        String icon = "bow";
        ItemStack arrowStack = new ItemStack(Items.arrow);
        int drawAmount = -2;
        boolean drawArrows = false;
        if(entityLivingBase instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer)entityLivingBase;

            int timer = player.getItemInUseDuration();
            if(timer > 0){
                drawAmount = timer >= 18?2:timer > 13?1:0;
                drawArrows = true;
            }
            ItemStack quiver = QuiverArrowRegistry.getArrowContainer(item, (EntityPlayer) entityLivingBase);
            if(quiver != null){
                arrowStack = ((IArrowContainer2)quiver.getItem()).getStackInSlot(quiver, ((IArrowContainer2)quiver.getItem()).getSelectedSlot(quiver));
            }
            if(drawAmount >= 0){
                icon = "bow_" + ItemBow.bowPullIconNameArray[drawAmount];
                if(arrowStack != null && QuiverArrowRegistry.isKnownArrow(arrowStack)){
                    icon = BattlegearConfig.MODID + icon;
                }
            }
        }else if (entityLivingBase instanceof EntitySkeleton){
            arrowStack = MobHookContainerClass.INSTANCE.getArrowForMob((EntitySkeleton) entityLivingBase);
            drawArrows = true;
        }else if (entityLivingBase == null){
            arrowStack = null;
        }
        
        if(BattlegearConfig.arrowForceRendered){
        	drawArrows = true;
        }

        /*Tessellator tessellator = Tessellator.getInstance();
        ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
*/
        if(drawArrows && QuiverArrowRegistry.isKnownArrow(arrowStack)){
            /*icon = arrowStack.getIconIndex();
            GL11.glPushMatrix();
            GL11.glTranslatef(-(-3F+drawAmount)/16F, -(-2F+drawAmount)/16F, firstPerson?-0.5F/16F:0.5F/16F);
            ItemRenderer.renderItemIn2D(tessellator, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
            GL11.glPopMatrix();*/
        }

        /*if(item.hasEffect(0))
            BattlegearRenderHelper.renderEnchantmentEffects(tessellator);*/
    }
}
