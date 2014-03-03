package mods.battlegear2.client.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * User: nerd-boy
 * Date: 13/08/13
 * Time: 10:32 AM
 * TODO: Add discription
 */
public class GuiTextFieldAlt extends GuiTextField {
    public GuiTextFieldAlt(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5) {
        super(par1FontRenderer, par2, par3, par4, par5);
    }


    @Override
    public void setText(String par1Str) {
        super.setText(removeInvalidChars(par1Str));
    }

    @Override
    public void writeText(String par1Str) {
        super.writeText(removeInvalidChars(par1Str));
    }

    private String removeInvalidChars(String string){

        StringBuffer sb = new StringBuffer();
        string = string.toUpperCase();
        for(int i = 0; i < string.length(); i++){
            char next = string.charAt(i);
            if(Character.isDigit(next) || (next >= 'A' && next <= 'F'))
                sb.append(next);
        }
        return sb.toString();

    }

    public int parseText() {

        int c1 = Integer.parseInt(getText(), 16);
        //0xa0r0g0b0
        //0x0000argb
        return (
                ((c1 & 0xF000) << 16) |
                        ((c1 & 0x0F00) << 12) |
                        ((c1 & 0x00F0) << 8) |
                        ((c1 & 0x000F) << 4) |
                        0x08080808
        );

    }
}
