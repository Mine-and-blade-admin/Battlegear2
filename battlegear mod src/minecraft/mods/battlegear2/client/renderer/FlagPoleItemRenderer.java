package mods.battlegear2.client.renderer;

import mods.battlegear2.heraldry.BlockFlagPole;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by Aaron on 2/08/13.
 */
public class FlagPoleItemRenderer implements IItemRenderer {
    private BlockFlagPole pole;
    private Item itemCache;
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != IItemRenderer.ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        int poleType = item.getItemDamage();
        if(pole==null||item.getItem()!=itemCache){
            itemCache = item.getItem();
            Block temp = Block.getBlockFromItem(itemCache);
            if(temp instanceof BlockFlagPole){
                pole = (BlockFlagPole) temp;
            }else{
                return;
            }
        }
        IIcon icon = pole.getIcon(2, poleType);

        Tessellator tess = Tessellator.instance;
        float[] dims = new float[5];
        for(int i =0; i<5; i++){
            dims[i] = pole.getTextDim(poleType, i);
        }
        float size = 1.5F;
        switch (type) {
            case ENTITY:
                size = 1;
                GL11.glTranslatef(-0.5F, 0, 0F);
                if(item.isOnItemFrame()){
                    GL11.glTranslatef(0,-0.25F, 0);
                    size = 0.75F;
                }

            case EQUIPPED:

                tess.startDrawingQuads();
                tess.addVertexWithUV(7F/16F, size, 0, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(9F/16F, size, 0, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(9F/16F, 0, 0, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(7F/16F, 0, 0, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV(dims[0]));
                tess.draw();


                tess.startDrawingQuads();
                tess.addVertexWithUV(7F/16F, 0, 0F/16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(7F/16F, 0, 2F/16F, icon.getInterpolatedU((dims[2])), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(7F/16F, size, 2F/16F, icon.getInterpolatedU((dims[2])), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(7F/16F, size, 0F/16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV((dims[4])));
                tess.draw();


                tess.startDrawingQuads();
                tess.addVertexWithUV(7F/16F, 0, 2F/16F, icon.getInterpolatedU((dims[2])), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(9F/16F, 0, 2F/16F, icon.getInterpolatedU((dims[3])), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(9F/16F, size, 2F/16F, icon.getInterpolatedU((dims[3])), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(7F/16F, size, 2F/16F, icon.getInterpolatedU((dims[2])), icon.getInterpolatedV((dims[4])));
                tess.draw();


                tess.startDrawingQuads();
                tess.addVertexWithUV(9F/16F, size, 0F/16F, icon.getInterpolatedU((dims[3])), icon.getInterpolatedV((dims[4])));

                tess.addVertexWithUV(9F/16F, size, 2F/16F, icon.getInterpolatedU((dims[4])), icon.getInterpolatedV((dims[4])));

                tess.addVertexWithUV(9F/16F, 0, 2F/16F, icon.getInterpolatedU((dims[4])), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(9F/16F, 0, 0F/16F, icon.getInterpolatedU((dims[3])), icon.getInterpolatedV(dims[0]));
                tess.draw();

                icon = pole.getIcon(0, poleType);

                tess.startDrawingQuads();
                tess.addVertexWithUV(7F / 16F, 0, 0F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(6));
                tess.addVertexWithUV(9F / 16F, 0, 0F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(6));
                tess.addVertexWithUV(9F / 16F, 0, 2F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(10));
                tess.addVertexWithUV(7F / 16F, 0, 2F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(10));
                tess.draw();

                tess.startDrawingQuads();
                tess.addVertexWithUV(9F / 16F, size, 0F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(6));
                tess.addVertexWithUV(7F / 16F, size, 0F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(6));
                tess.addVertexWithUV(7F / 16F, size, 2F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(10));
                tess.addVertexWithUV(9F / 16F, size, 2F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(10));
                tess.draw();


                break;
            case EQUIPPED_FIRST_PERSON:

                tess.startDrawingQuads();
                tess.addVertexWithUV(7F/16F, 1, 0, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(9F/16F, 1, 0, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(9F/16F, 0, 0, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(7F/16F, 0, 0, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV(dims[0]));
                tess.draw();


                tess.startDrawingQuads();
                tess.addVertexWithUV(7F/16F, 0, 0F/16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(7F/16F, 0, 2F/16F, icon.getInterpolatedU((dims[2])), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(7F/16F, 1, 2F/16F, icon.getInterpolatedU((dims[2])), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(7F/16F, 1, 0F/16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV((dims[4])));
                tess.draw();



                break;
            case INVENTORY:

                tess.startDrawingQuads();
                tess.addVertexWithUV(6, 13, 0, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(8, 15, 0, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(8, 5, 0, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
                tess.addVertexWithUV(6, 3, 0, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV(dims[0]));
                tess.draw();


                tess.startDrawingQuads();
                tess.addVertexWithUV(10, 3, 0, icon.getInterpolatedU((dims[2])), icon.getInterpolatedV(0));
                tess.addVertexWithUV(8, 5, 0, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(0));
                tess.addVertexWithUV(8, 15, 0, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV((dims[4])));
                tess.addVertexWithUV(10, 13, 0, icon.getInterpolatedU((dims[2])), icon.getInterpolatedV((dims[4])));
                tess.draw();


                icon = pole.getIcon(0, poleType);

                tess.startDrawingQuads();
                tess.addVertexWithUV(8, 2, 0, icon.getInterpolatedU(6), icon.getInterpolatedV(10));
                tess.addVertexWithUV(6, 3, 0, icon.getInterpolatedU(10), icon.getInterpolatedV(10));
                tess.addVertexWithUV(8, 5, 0, icon.getInterpolatedU(10), icon.getInterpolatedV(6));
                tess.addVertexWithUV(10, 3, 0, icon.getInterpolatedU(6), icon.getInterpolatedV(6));
                tess.draw();


                break;
            case FIRST_PERSON_MAP:
                break;
        }
    }
}


