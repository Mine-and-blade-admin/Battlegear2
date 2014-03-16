package mods.battlegear2.api.heraldry;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Aaron on 3/08/13.
 */
public interface ITool {

    public String getToolName();

    public ResourceLocation getToolImage();

    public void drawOverlay(int x, int y, int[] pixals, DynamicTexture overlay, int rgb, boolean shift);

    public void draw(int x, int y, int[] pixels, int rgb, boolean shift);

}
