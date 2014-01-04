package net.minecraft.client.gui.inventory;

public class ContainerAccess {
    public static int getTop(GuiContainer container) {
        return container.guiTop;
    }

    public static int getLeft(GuiContainer container){
        return container.guiLeft;
    }
}
