package mods.battlegear2.client.renderer;

import mods.battlegear2.api.heraldry.IFlagHolder;
import mods.battlegear2.client.utils.ImageCache;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * User: nerd-boy
 * Date: 2/08/13
 * Time: 2:33 PM
 * TODO: Add discription
 */
public class FlagPoleTileRenderer extends TileEntitySpecialRenderer {
    public static int period = 250;
    public static int flag_sections = 16;
    public static double getZLevel(float x, float size, long time){
        return Math.pow(x, 0.5/(size/5)) * Math.sin(Math.PI * ( -x/size * 3 + ((float)(time% period)) / (0.5F*(float)period))) / 4;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f, int damage) {

        if(tileentity instanceof IFlagHolder){
            IBlockState banner = tileentity.getWorld().getBlockState(tileentity.getPos());
            if (banner.getBlock() instanceof BlockAir) {
                return;
            }
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int type = banner.getBlock().getMetaFromState(banner);
            int side = ((IFlagHolder) tileentity).getOrientation();

            GL11.glPushMatrix();
            GL11.glTranslated(d0, d1, d2);
            GL11.glColor3f(1, 1, 1);

            float[] dims = new float[5];
            for(int i=0; i<5; i++){
                dims[i] = ((IFlagHolder)tileentity).getTextureDimensions(type, i);
            }
            switch (side){
                case 0:
                    renderYFlagPole(banner, f, type, dims);
                    renderYFlag((IFlagHolder)tileentity, d0, d1, d2, f, type);
                    break;
                case 1:
                    renderZFlagPole(banner, f, type, dims);
                    renderZFlag((IFlagHolder)tileentity, d0, d1, d2, f, type);
                    break;
                case 2:
                    GL11.glRotatef(90, 0, 1, 0);
                    GL11.glTranslatef(-1, 0, 0);
                    renderZFlagPole(banner, f, type, dims);
                    renderZFlag((IFlagHolder)tileentity, d0, d1, d2, f, type);
                    break;
            }

            GL11.glPopMatrix();
        }
    }

    private void renderZFlag(IFlagHolder tileentity, double d0, double d1, double d2, float f, int type) {

        List<ItemStack> flags = tileentity.getFlags();
        if(flags.size()>0)
        {
            Tessellator tess = Tessellator.getInstance();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glRotatef(-90, 0, 0, 1);
            for(int flagIndex = 0; flagIndex < flags.size(); flagIndex++){
                ItemStack flag = flags.get(flagIndex);
                ImageCache.setTexture(flag);

                if(flag_sections == 0){
                    tess.getWorldRenderer().startDrawingQuads();

                    tess.getWorldRenderer().addVertexWithUV(8F / 16F, -flagIndex + 1 - 2F / 16F, 0, 0, 0.001);
                    tess.getWorldRenderer().addVertexWithUV(8F / 16F, -flagIndex - 2F / 16F, 0, 1.00, 0.001);
                    tess.getWorldRenderer().addVertexWithUV(8F / 16F, -flagIndex - 2F / 16F, 1, 1.00, 0.999);
                    tess.getWorldRenderer().addVertexWithUV(8F / 16F, -flagIndex + 1 - 2F / 16F, 1, 0, 0.999);

                    tess.getWorldRenderer().addVertexWithUV(8F / 16F, -flagIndex + 1 - 2F / 16F, 1, 0, 0.999);
                    tess.getWorldRenderer().addVertexWithUV(8F / 16F, -flagIndex - 2F / 16F, 1, 1.00, 0.999);
                    tess.getWorldRenderer().addVertexWithUV(8F / 16F, -flagIndex - 2F / 16F, 0, 1.00, 0.001);
                    tess.getWorldRenderer().addVertexWithUV(8F / 16F, -flagIndex + 1 - 2F / 16F, 0, 0, 0.001);


                    tess.draw();

                }else{

                    long time = System.currentTimeMillis();
                    for(int i = 0; i < flag_sections; i++){
                        tess.getWorldRenderer().startDrawingQuads();

                        double z1 = getZLevel((float)((flag_sections - i)) / (float)flag_sections + flagIndex, 5, time) / 5F;
                        double z2 = getZLevel((float)(flag_sections - i+1) / (float)flag_sections + flagIndex, 5, time) / 5F;

                        tess.getWorldRenderer().addVertexWithUV(8F / 16F + z1, -flagIndex + (float) (i + 1) / (float) flag_sections - 2F / 16F, 0, (float) (i + 1) / (float) flag_sections, 0.999);
                        tess.getWorldRenderer().addVertexWithUV(8F / 16F + z2, -flagIndex + (float) (i) / (float) flag_sections - 2F / 16F, 0, (float) (i) / (float) flag_sections, 0.999);
                        tess.getWorldRenderer().addVertexWithUV(8F / 16F + z2, -flagIndex + (float) (i) / (float) flag_sections - 2F / 16F, 1, (float) (i) / (float) flag_sections, 0.001);
                        tess.getWorldRenderer().addVertexWithUV(8F / 16F + z1, -flagIndex + (float) (i + 1) / (float) flag_sections - 2F / 16F, 1, (float) (i + 1) / (float) flag_sections, 0.001);

                        tess.getWorldRenderer().addVertexWithUV(8F / 16F + z1, -flagIndex + (float) (i + 1) / (float) flag_sections - 2F / 16F, 1, (float) (i + 1) / (float) flag_sections, 0.001);
                        tess.getWorldRenderer().addVertexWithUV(8F / 16F + z2, -flagIndex + (float) (i) / (float) flag_sections - 2F / 16F, 1, (float) (i) / (float) flag_sections, 0.001);
                        tess.getWorldRenderer().addVertexWithUV(8F / 16F + z2, -flagIndex + (float) (i) / (float) flag_sections - 2F / 16F, 0, (float) (i) / (float) flag_sections, 0.999);
                        tess.getWorldRenderer().addVertexWithUV(8F / 16F + z1, -flagIndex + (float) (i + 1) / (float) flag_sections - 2F / 16F, 0, (float) (i + 1) / (float) flag_sections, 0.999);


                        tess.draw();
                    }

                }

            }

            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private void renderZFlagPole(IBlockState banner, float f, int type, float[] dims) {
        TextureAtlasSprite icon = getIcon(banner);//getIcon(2, type);
        Tessellator tess = Tessellator.getInstance();
        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 14F / 16F, 0F / 16F, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 16F / 16F, 0F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 16F / 16F, 16F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 14F / 16F, 16F / 16F, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV(dims[4]));
        tess.draw();

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 14F / 16F, 0F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 14F / 16F, 0F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 14F / 16F, 16F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 14F / 16F, 16F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[4]));
        tess.draw();

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 14F / 16F, 16F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 16F / 16F, 16F / 16F, icon.getInterpolatedU(dims[3]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 16F / 16F, 0F / 16F, icon.getInterpolatedU(dims[3]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 14F / 16F, 0F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[0]));
        tess.draw();

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 16F / 16F, 16F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 16F / 16F, 16F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 16F / 16F, 0F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 16F / 16F, 0F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[0]));
        tess.draw();

        icon = getIcon(banner);//getIcon(0, type);

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 16F / 16F, 0F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(10));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 16F / 16F, 0F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(10));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 14F / 16F, 0F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(6));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 14F / 16F, 0F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(6));
        tess.draw();

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 14F / 16F, 16F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(6));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 14F / 16F, 16F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(6));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 16F / 16F, 16F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(10));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 16F / 16F, 16F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(10));
        tess.draw();
    }

    private void renderYFlag(IFlagHolder tileentity, double d0, double d1, double d2, float f, int type) {

        List<ItemStack> flags = tileentity.getFlags();
        if(flags.size()>0)
        {
            Tessellator tess = Tessellator.getInstance();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);



            GL11.glDisable(GL11.GL_LIGHTING);
            for(int flagIndex = 0; flagIndex < flags.size(); flagIndex++){
                ItemStack flag = flags.get(flagIndex);
                ImageCache.setTexture(flag);


                if(flag_sections == 0){
                    tess.getWorldRenderer().startDrawingQuads();
                    tess.getWorldRenderer().addVertexWithUV(7F / 16F - flagIndex, 0, 8F / 16F, 0, 0.999);
                    tess.getWorldRenderer().addVertexWithUV(7F / 16F - flagIndex - 1, 0, 8F / 16F, 1.0025, 0.999);
                    tess.getWorldRenderer().addVertexWithUV(7F / 16F - flagIndex - 1, 1, 8F / 16F, 1.0025, 0.001);
                    tess.getWorldRenderer().addVertexWithUV(7F / 16F - flagIndex, 1, 8F / 16F, 0, 0.001);

                    tess.getWorldRenderer().addVertexWithUV(7F / 16F - flagIndex, 1, 8F / 16F, 0, 0.001);
                    tess.getWorldRenderer().addVertexWithUV(7F / 16F - flagIndex - 1, 1, 8F / 16F, 1.0025, 0.001);
                    tess.getWorldRenderer().addVertexWithUV(7F / 16F - flagIndex - 1, 0, 8F / 16F, 1.0025, 0.999);
                    tess.getWorldRenderer().addVertexWithUV(7F / 16F - flagIndex, 0, 8F / 16F, 0, 0.999);

                    tess.draw();

                }else{
                    long time = System.currentTimeMillis();
                    for(int i = 0; i < flag_sections; i++){
                        tess.getWorldRenderer().startDrawingQuads();

                        double z1 = getZLevel((float)(i) / (float)flag_sections + flagIndex, 3, time);
                        double z2 = getZLevel((float)(i+1) / (float)flag_sections + flagIndex, 3, time);

                        tess.getWorldRenderer().addVertexWithUV(7F / 16F - (float) (i) / (float) flag_sections - flagIndex, 0, 8F / 16F + z1, (float) (i) / (float) flag_sections, 0.999);
                        tess.getWorldRenderer().addVertexWithUV(7F / 16F - (float) (i + 1) / (float) flag_sections - flagIndex, 0, 8F / 16F + z2, (float) (i + 1) / (float) flag_sections, 0.999);
                        tess.getWorldRenderer().addVertexWithUV(7F / 16F - (float) (i + 1) / (float) flag_sections - flagIndex, 1.0025, 8F / 16F + z2, (float) (i + 1) / (float) flag_sections, 0.001);
                        tess.getWorldRenderer().addVertexWithUV(7F / 16F - (float) (i) / (float) flag_sections - flagIndex, 1.0025, 8F / 16F + z1, (float) (i) / (float) flag_sections, 0.001);

                        tess.getWorldRenderer().addVertexWithUV(7F / 16F - (float) (i) / (float) flag_sections - flagIndex, 1.0025, 8F / 16F + z1, (float) (i) / (float) flag_sections, 0.001);
                        tess.getWorldRenderer().addVertexWithUV(7F / 16F - (float) (i + 1) / (float) flag_sections - flagIndex, 1.0025, 8F / 16F + z2, (float) (i + 1) / (float) flag_sections, 0.001);
                        tess.getWorldRenderer().addVertexWithUV(7F / 16F - (float) (i + 1) / (float) flag_sections - flagIndex, 0, 8F / 16F + z2, (float) (i + 1) / (float) flag_sections, 0.999);
                        tess.getWorldRenderer().addVertexWithUV(7F / 16F - (float) (i) / (float) flag_sections - flagIndex, 0, 8F / 16F + z1, (float) (i) / (float) flag_sections, 0.999);

                        tess.draw();
                    }
                }
            }
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private void renderYFlagPole(IBlockState banner, float f, int type, float[] dims) {

        TextureAtlasSprite icon = getIcon(banner);//getIcon(2, type);
        Tessellator tess = Tessellator.getInstance();

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 0, 9F / 16F, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 0, 9F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 1, 9F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 1, 9F / 16F, icon.getInterpolatedU(dims[0]), icon.getInterpolatedV(dims[4]));
        tess.draw();

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 0, 9F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 0, 7F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 1, 7F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 1, 9F / 16F, icon.getInterpolatedU(dims[1]), icon.getInterpolatedV(dims[4]));
        tess.draw();

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 0, 7F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 0, 7F / 16F, icon.getInterpolatedU(dims[3]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 1, 7F / 16F, icon.getInterpolatedU(dims[3]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 1, 7F / 16F, icon.getInterpolatedU(dims[2]), icon.getInterpolatedV(dims[4]));
        tess.draw();

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 0, 7F / 16F, icon.getInterpolatedU(dims[3]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 0, 9F / 16F, icon.getInterpolatedU(dims[4]), icon.getInterpolatedV(dims[0]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 1, 9F / 16F, icon.getInterpolatedU(dims[4]), icon.getInterpolatedV(dims[4]));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 1, 7F / 16F, icon.getInterpolatedU(dims[3]), icon.getInterpolatedV(dims[4]));
        tess.draw();

        icon = getIcon(banner);//getIcon(0, type);

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 0, 7F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(6));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 0, 7F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(6));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 0, 9F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(10));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 0, 9F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(10));
        tess.draw();

        tess.getWorldRenderer().startDrawingQuads();
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 1, 7F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(6));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 1, 7F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(6));
        tess.getWorldRenderer().addVertexWithUV(7F / 16F, 1, 9F / 16F, icon.getInterpolatedU(10), icon.getInterpolatedV(10));
        tess.getWorldRenderer().addVertexWithUV(9F / 16F, 1, 9F / 16F, icon.getInterpolatedU(6), icon.getInterpolatedV(10));
        tess.draw();
    }

    private TextureAtlasSprite getIcon(IBlockState state) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

    /*
    public IIcon getIcon(int par1, int meta) {
        if(meta == 4)
            return Blocks.iron_block.getIcon(par1, 0);
        else if(meta < 4)
            return Blocks.log.getIcon(par1, meta);
        else
            return Blocks.log2.getIcon(par1, meta - 5);
    }*/
}
