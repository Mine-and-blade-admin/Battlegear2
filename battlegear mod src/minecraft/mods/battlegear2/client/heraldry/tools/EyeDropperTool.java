package mods.battlegear2.client.heraldry.tools;

import mods.battlegear2.api.heraldry.ITool;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Aaron on 3/08/13.
 */
public class EyeDropperTool implements ITool {
    @Override
    public String getToolName() {
        return "tool.dropper";
    }

    @Override
    public ResourceLocation getToolImage() {
        return new ResourceLocation("battlegear2:textures/"+getToolName()+".png");
    }

    @Override
    public void drawOverlay(int x, int y, int[] pixelsCurrent, DynamicTexture overlay, int rgb, boolean shift) {
        overlay.updateDynamicTexture();
    }

    @Override
    public void draw(int x, int y, int[] pixelsCurrent, int rgb, boolean shift) {
    }
}
