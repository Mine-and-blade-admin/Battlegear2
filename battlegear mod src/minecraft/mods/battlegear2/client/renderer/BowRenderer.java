package mods.battlegear2.client.renderer;

public class BowRenderer {
/*
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

        IIcon icon = item.getIconIndex();

        ItemStack arrowStack = new ItemStack(Items.arrow);
        int drawAmount = -2;
        boolean drawArrows = false;
        if(entityLivingBase instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer)entityLivingBase;

            int timer =  player.getItemInUseDuration();
            if(timer > 0){
                drawAmount = timer >= 18?2:timer > 13?1:0;
                drawArrows = true;
            }
            ItemStack quiver = QuiverArrowRegistry.getArrowContainer(item, (EntityPlayer) entityLivingBase);
            if(quiver != null){
                arrowStack = ((IArrowContainer2)quiver.getItem()).getStackInSlot(quiver, ((IArrowContainer2)quiver.getItem()).getSelectedSlot(quiver));
            }

            if(drawAmount >= 0){
                if(arrowStack != null && QuiverArrowRegistry.isKnownArrow(arrowStack)){
                    icon = ClientProxy.bowIcons[drawAmount];
                }else{
                    icon = Items.bow.getItemIconForUseDuration(drawAmount);
                }
            }
        }else if (entityLivingBase instanceof EntitySkeleton){
            arrowStack = MobHookContainerClass.INSTANCE.getArrowForMob((EntitySkeleton) entityLivingBase);
            drawArrows = true;
        }else if (entityLivingBase == null || entityLivingBase.equals(BattlegearRenderHelper.dummyEntity)){
            arrowStack = null;
        }
        
        if(BattlegearConfig.arrowForceRendered){
        	drawArrows = true;
        }

        Tessellator tessellator = Tessellator.getInstance();
        ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);

        if(drawArrows && QuiverArrowRegistry.isKnownArrow(arrowStack)){
            icon = arrowStack.getIconIndex();
            GL11.glPushMatrix();
            GL11.glTranslatef(-(-3F+drawAmount)/16F, -(-2F+drawAmount)/16F, firstPerson?-0.5F/16F:0.5F/16F);
            ItemRenderer.renderItemIn2D(tessellator, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
            GL11.glPopMatrix();
        }

        if(item.hasEffect(0))
            BattlegearRenderHelper.renderEnchantmentEffects(tessellator);
    }*/
}
