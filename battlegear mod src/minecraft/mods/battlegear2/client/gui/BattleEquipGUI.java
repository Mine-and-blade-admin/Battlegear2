package mods.battlegear2.client.gui;

import mods.battlegear2.Battlegear;
import mods.battlegear2.client.BattlegearClientEvents;
import mods.battlegear2.client.ClientProxy;
import mods.battlegear2.gui.BattlegearGUIHandeler;
import mods.battlegear2.gui.ContainerBattle;
import mods.battlegear2.packet.BattlegearGUIPacket;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class BattleEquipGUI extends InventoryEffectRenderer {

    public static final ResourceLocation resource = new ResourceLocation("battlegear2", "textures/gui/Equip GUI.png");
    public static Class equipTab;
    
    /**
     * x size of the inventory window in pixels. Defined as float, passed as int
     */
    private float xSize_lo;

    /**
     * y size of the inventory window in pixels. Defined as float, passed as int.
     */
    private float ySize_lo;

    public BattleEquipGUI(EntityPlayer entityPlayer, boolean isRemote) {
        super(new ContainerBattle(entityPlayer.inventory, !isRemote, entityPlayer));
        this.allowUserInput = true;

        //Don't need this, however maybe we can add a stat later on. I will keep it comented out for now
        //entityPlayer.addStat(AchievementList.openInventory, 1);
    }

    @Override
    public void initGui ()
    {
        super.initGui();
        BattlegearClientEvents.onOpenGui(this.buttonList, guiLeft-30, guiTop);
        if(ClientProxy.tconstructEnabled){
            this.buttonList.clear();
            try{
                if(equipTab==null){
                    equipTab = Class.forName("mods.battlegear2.client.gui.controls.EquipGearTab");
                }
                ClientProxy.updateTab.invoke(null, guiLeft, guiTop, equipTab);
                ClientProxy.addTabs.invoke(null, this.buttonList);
            }catch(Exception e){
                ClientProxy.tconstructEnabled = false;
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3){
        super.drawScreen(par1, par2, par3);
        this.xSize_lo = (float) par1;
        this.ySize_lo = (float) par2;
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(resource);
        int var5 = this.guiLeft;
        int var6 = this.guiTop;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        GuiInventory.func_147046_a(var5 + 31, var6 + 75, 30, (float) (var5 + 51) - this.xSize_lo, (float) (var6 + 75 - 50) - this.ySize_lo, mc.thePlayer);
    }
    
    public static void open(EntityPlayer player){
    	//send packet to open container on server
        Battlegear.packetHandler.sendPacketToServer(new BattlegearGUIPacket(BattlegearGUIHandeler.equipID).generatePacket());
    }
}
