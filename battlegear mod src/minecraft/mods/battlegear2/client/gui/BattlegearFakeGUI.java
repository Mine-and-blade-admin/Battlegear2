package mods.battlegear2.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import mods.battlegear2.client.gui.controls.GuiDrawButton;
import mods.battlegear2.utils.BattlegearConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

/**
 * A gui that displays like the in-game screen, where each element is a {@link GuiDrawButton}
 * Used to move gui elements and save their position into configuration file
 */
public class BattlegearFakeGUI extends GuiScreen{
    private final GuiScreen previous;
    private final BattlegearInGameGUI helper = new BattlegearInGameGUI();
    public BattlegearFakeGUI(GuiScreen parent){
        this.previous = parent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui(){
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 25, I18n.format("gui.done")));
        this.buttonList.add(new GuiDrawButton(2, this.width / 2+BattlegearConfig.quiverBarOffset[0], BattlegearConfig.quiverBarOffset[1], 41, 22, new QuiverSlotRenderer()));
        this.buttonList.add(new GuiDrawButton(3, this.width / 2 - 91+BattlegearConfig.shieldBarOffset[0], this.height - 35+BattlegearConfig.shieldBarOffset[1], 182, 9, new BlockBarRenderer()));
        this.buttonList.add(new GuiDrawButton(4, this.width / 2 - 184+BattlegearConfig.battleBarOffset[0], this.height - 22+BattlegearConfig.battleBarOffset[1], 62, 22, new WeaponSlotRenderer(false)));
        this.buttonList.add(new GuiDrawButton(5, this.width / 2 + 121+BattlegearConfig.battleBarOffset[2], this.height - 22+BattlegearConfig.battleBarOffset[3], 62, 22, new WeaponSlotRenderer(true)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float frame){
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        super.drawScreen(mouseX, mouseY, frame);
        for(Object obj:this.buttonList)
            if(((GuiButton)obj).func_146115_a()){
                drawCreativeTabHoveringText(I18n.format("gui.fake.help"+((GuiButton) obj).id), mouseX, mouseY);
            }
    }

    @Override
    protected void actionPerformed(GuiButton button){
        if (button.enabled && button.id == 1){
            FMLClientHandler.instance().showGuiScreen(previous);
        }
    }

    @Override
    public void onGuiClosed(){
        int varX, varY;
        for(Object obj:this.buttonList){
            if(obj instanceof GuiDrawButton){
                varX = ((GuiDrawButton) obj).getDragX();
                varY = ((GuiDrawButton) obj).getDragY();
                switch (((GuiDrawButton) obj).id){
                    case 2:
                        BattlegearConfig.quiverBarOffset[0] += varX;
                        BattlegearConfig.quiverBarOffset[1] += varY;
                        break;
                    case 3:
                        BattlegearConfig.shieldBarOffset[0] += varX;
                        BattlegearConfig.shieldBarOffset[1] += varY;
                        break;
                    case 4:
                        BattlegearConfig.battleBarOffset[0] += varX;
                        BattlegearConfig.battleBarOffset[1] += varY;
                        break;
                    case 5:
                        BattlegearConfig.battleBarOffset[2] += varX;
                        BattlegearConfig.battleBarOffset[3] += varY;
                        break;
                }
            }
        }
        BattlegearConfig.refreshGuiValues();
    }

    public class WeaponSlotRenderer implements GuiDrawButton.IDrawnHandler{
        private final boolean isMainHand;
        public WeaponSlotRenderer(boolean isMainHand){
            this.isMainHand = isMainHand;
        }

        @Override
        public void drawElement(ScaledResolution resolution, int varX, int varY) {
            helper.renderBattleSlots(varX, varY, 0, isMainHand);
        }
    }

    public class BlockBarRenderer implements GuiDrawButton.IDrawnHandler{
        ItemStack dummy;
        public BlockBarRenderer(){
            if(BattlegearConfig.shield[0]!=null)
                dummy = new ItemStack(BattlegearConfig.shield[0]);
        }
        @Override
        public void drawElement(ScaledResolution resolution, int varX, int varY) {
            if(dummy!=null){
                helper.renderBlockBar(varX, varY);
            }
        }
    }

    public class QuiverSlotRenderer implements GuiDrawButton.IDrawnHandler{
        ItemStack dummy;
        public QuiverSlotRenderer(){
            if(BattlegearConfig.quiver!=null)
                dummy = new ItemStack(BattlegearConfig.quiver);
        }

        @Override
        public void drawElement(ScaledResolution resolution, int varX, int varY) {
            if(dummy!=null){
                helper.renderQuiverBar(dummy, 0, varX, varY);
            }
        }
    }
}
