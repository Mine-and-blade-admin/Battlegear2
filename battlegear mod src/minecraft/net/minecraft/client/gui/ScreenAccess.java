package net.minecraft.client.gui;

import net.minecraft.client.gui.GuiScreen;

import java.util.List;

public class ScreenAccess {

    public static List getButtons(GuiScreen screen){
        return screen.buttonList;
    }
}
