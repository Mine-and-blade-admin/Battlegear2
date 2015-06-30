package tconstruct.client.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

public abstract class AbstractTab extends GuiButton
{
    ItemStack renderStack;

    public AbstractTab(int id, int posX, int posY, ItemStack renderStack)
    {
        super(id, posX, posY, 28, 32, "");
        this.renderStack = renderStack;
    }

    @Override
    public boolean mousePressed (Minecraft mc, int mouseX, int mouseY)
    {
        boolean inWindow = this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        if (inWindow)
        {
            this.onTabClicked();
        }

        return inWindow;
    }

    public abstract void onTabClicked ();

    public abstract boolean shouldAddToList ();
}
