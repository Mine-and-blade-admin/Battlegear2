package mods.battlegear2.client.gui;

import mods.battlegear2.Battlegear;
import mods.battlegear2.api.heraldry.IHeraldryItem;
import mods.battlegear2.api.heraldry.ITool;
import mods.battlegear2.client.gui.controls.*;
import mods.battlegear2.client.heraldry.tools.*;
import mods.battlegear2.client.utils.*;
import mods.battlegear2.packet.BattlegearChangeHeraldryPacket;
import mods.battlegear2.utils.FileExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Created by Aaron on 3/08/13.
 */
public class GuiFlagDesigner extends GuiScreen {
    public static boolean FcLoadImages = true;
    public static int BUFFER_SIZE = 10;
    private GuiColourPicker colourPicker;
    private GuiToggeableButton[] toggleButtons;
    private ITool[] tools;
    private ITool selectedTool;
    private GuiSliderAlt slider;

    private static final ResourceLocation map_background = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation background = new ResourceLocation("battlegear2:textures/gui.designer.png");
    private static final int ID_SAVE = 0;
    private static final int ID_LOAD = 1;
    private static final int ID_OK = 2;
    private static final int ID_LOAD_SECTION = 3;
    private static final int ID_COLOUR_PICKER = 4;
    private static final int SLIDER = 5;
    private int guiLeft, guiTop, xSize, ySize;

    private static final int canvusMult = 5;
    private static final int canvusSize = canvusMult * 32;

    private static final DynamicTexture canvus_back = new DynamicTexture(2,2);
    private static final DynamicTexture overlay = new DynamicTexture(ImageData.IMAGE_RES, ImageData.IMAGE_RES);

    private GuiTextFieldAlt colourTextField;

    private JFileChooser fc;

    private EntityPlayer player;

    private int[][] imageBuffer = new int[BUFFER_SIZE][];
    private int bufferPointer = 0;

    private org.lwjgl.input.Cursor[] cursors;

    int x_lpanel_width = 30;
    int panel_space = 10;
    int canvus_pad = 16;
    int x_rpanel_width = 92;
    int x_tpanel_width;

    int x_tpanel_start;
    int x_lpanel_start;
    int x_canvus_start;
    int x_rpanel_start;


    int y_tpanel_height = 20 + 10;
    int y_panel_space = 10;
    int y_rpanel_height = canvusSize;
    int y_lpanel_height = canvusSize;

    int y_tpanel_start;
    int y_lpanel_start;
    int y_canvus_start;
    int y_rpanel_start;


    long last_refresh = System.currentTimeMillis() - 500;

    static{
        int[] pixels = canvus_back.getTextureData();
        pixels[0] = 0xFF666666;
        pixels[1] = 0xFF999999;
        pixels[2] = 0xFF999999;
        pixels[3] = 0xFF666666;
    }

    private int toolIndex = 0;

    public GuiFlagDesigner(EntityPlayer player) {
        this.player = player;
        fc = new JFileChooser() {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                // intercept the dialog created by JFileChooser
                JDialog dialog = super.createDialog(parent);
                dialog.setModal(true); // set modality (or setModalityType)
                dialog.setAlwaysOnTop(true);
                return dialog;
            }
        };
        fc.setFileFilter(new ImageFilter(ImageFilter.DEFAULT));
        fc.setAcceptAllFileFilterUsed(false);
        if(FcLoadImages){
            fc.setFileView(new ImageFileViewer());
            ImagePreviewPanel preview = new ImagePreviewPanel();
            fc.setAccessory(preview);
            fc.addPropertyChangeListener(preview);
        }

        imageBuffer = new int[BUFFER_SIZE][];
        imageBuffer[bufferPointer] = new int[ImageData.IMAGE_RES * ImageData.IMAGE_RES];
        ItemStack item = player.getHeldItem();
        if(item != null && item.getItem() instanceof IHeraldryItem){
            if(((IHeraldryItem) item.getItem()).hasHeraldry(item)){
                ImageData image = new ImageData(((IHeraldryItem) item.getItem()).getHeraldry(item));
                image.setTexture(imageBuffer[bufferPointer]);
            }else{
                ImageData.defaultImage.setTexture(imageBuffer[bufferPointer]);
            }
        }else{
            ImageData.defaultImage.setTexture(imageBuffer[bufferPointer]);
        }

    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);

        int x = (Mouse.getEventX() * this.width / this.mc.displayWidth -90 -guiLeft)/canvusMult;
        int y = (this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - 25- guiTop)/canvusMult;

        if(colourTextField.textboxKeyTyped(par1, par2)){

            if(colourTextField.getText().length() == 4){
                colourPicker.selectColour(colourTextField.parseText());
                //colourPicker.hasChanged = false;
            }

        }else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)){

            if(par2 == Keyboard.KEY_Z){
                int prev = (bufferPointer+BUFFER_SIZE-1) % BUFFER_SIZE;
                if(imageBuffer[prev]==null) {
                    Toolkit.getDefaultToolkit().beep();
                }else{
                    bufferPointer = prev;
                    selectedTool.drawOverlay(x,y,imageBuffer[bufferPointer],overlay,ImageData.roundColour(colourPicker.getRGB()), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
                }
            }else if (par2 == Keyboard.KEY_Y){
                int next = (bufferPointer+1) % BUFFER_SIZE;
                if(imageBuffer[next] == null) {
                    Toolkit.getDefaultToolkit().beep();
                }else{
                    bufferPointer = next;
                    selectedTool.drawOverlay(x,y,imageBuffer[bufferPointer],overlay,ImageData.roundColour(colourPicker.getRGB()), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
                }
            }
        }else if (selectedTool instanceof TextTool){

            if(par1 == '\b'){
                if(((TextTool) selectedTool).text.length() > 0){

                    ((TextTool) selectedTool).text = ((TextTool) selectedTool).text.substring(0, ((TextTool) selectedTool).text.length() - 1);
                }

            } else if (par1 == '\n' || par1 == '\r'){

                int next = (bufferPointer+1) % BUFFER_SIZE;
                imageBuffer[next] = Arrays.copyOf(imageBuffer[bufferPointer], ImageData.IMAGE_RES * ImageData.IMAGE_RES);
                bufferPointer = next;

                //Clear the next
                imageBuffer[(bufferPointer+1) % BUFFER_SIZE] = null;

                ((TextTool) selectedTool).pressEnter(imageBuffer[bufferPointer], ImageData.roundColour(colourPicker.getRGB()));
            } else{
                if(ChatAllowedCharacters.isAllowedCharacter(par1))
                    ((TextTool) selectedTool).text += Character.toString(par1);
            }

            selectedTool.drawOverlay(x,y,imageBuffer[bufferPointer],overlay,ImageData.roundColour(colourPicker.getRGB()), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));


        }
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.clear();

        this.xSize = x_lpanel_width + panel_space + canvus_pad + canvusSize + canvus_pad + panel_space + x_rpanel_width;
        this.ySize = y_tpanel_height + panel_space + canvus_pad + canvusSize + canvus_pad;

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;

        x_lpanel_start = guiLeft;
        x_canvus_start = x_lpanel_start + panel_space + canvus_pad + x_lpanel_width;
        x_rpanel_start = x_canvus_start + canvusSize + canvus_pad + panel_space;
        x_tpanel_start = guiLeft;
        x_tpanel_width = xSize;

        y_tpanel_start = guiTop;
        y_lpanel_start = y_tpanel_start + y_tpanel_height + panel_space + canvus_pad;
        y_canvus_start = y_lpanel_start;
        y_rpanel_start = y_lpanel_start;


        this.buttonList.add(new GuiButton(ID_OK, 6 + x_rpanel_start, y_rpanel_start + y_rpanel_height - 25, 80, 20, StatCollector.translateToLocal("gui.done")));
        this.buttonList.add(new GuiButton(ID_SAVE, guiLeft + 5, guiTop + 5, 100, 20,StatCollector.translateToLocal("flag.design.save")));
        this.buttonList.add(new GuiButton(ID_LOAD, guiLeft + 5+100+11, guiTop + 5, 100, 20, StatCollector.translateToLocal("flag.design.load")));
        this.buttonList.add(new GuiButton(ID_LOAD_SECTION, guiLeft + 5+200+22, guiTop + 5, 100, 20, StatCollector.translateToLocal("flag.design.load.sections")));
        colourPicker = new GuiColourPicker(ID_COLOUR_PICKER, x_rpanel_start+6, y_rpanel_start+5, 0xFF000000, 7);
        colourPicker.addListener(new IControlListener() {
            @Override
            public void actionPreformed(GuiButton button) {
                int rgb = colourPicker.getRGB();

                StringBuffer sb = new StringBuffer();
                sb.append(Integer.toHexString((rgb >> 28) & 0xF));
                sb.append(Integer.toHexString((rgb >> 20) & 0xF));
                sb.append(Integer.toHexString((rgb >> 12) & 0xF));
                sb.append(Integer.toHexString((rgb >> 4) & 0xF));

                colourTextField.setText(sb.toString());
            }
        });
        this.buttonList.add(colourPicker);

        tools = new ITool[6];
        toggleButtons = new GuiToggeableButton[tools.length];
        tools[0] = new PenTool();
        tools[1] = new RectangleTool();
        tools[2] = new CircleTool();
        tools[3] = new FloodFillTool();
// tools[4] = new EyeDropperTool();
        tools[4] = new TextTool();
        tools[5] = new EyeDropperTool();
        cursors = new org.lwjgl.input.Cursor[tools.length+1];
        cursors[0] = Mouse.getNativeCursor();

        for(int i = 0; i < toggleButtons.length; i++){
            toggleButtons[i] = new GuiToggeableButton(10+ i, x_lpanel_start+5, y_lpanel_start +i*26 + 5, 20, 20, StatCollector.translateToLocal(tools[i].getToolName()), i==0, tools[i].getToolImage());
            this.buttonList.add(toggleButtons[i]);

            try{
                int[] rgbs = TextureUtil.readImageData(Minecraft.getMinecraft().getResourceManager(), tools[i].getToolImage());
                IntBuffer buffer = IntBuffer.wrap(rgbs);
                int res = (int) Math.sqrt(buffer.array().length);
                cursors[i+1] = new org.lwjgl.input.Cursor(res, res, i==1||i==2?res/2:0, i==1||i==2?res/2:0, 1, buffer, null);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        selectedTool = tools[0];

        slider = new GuiSliderAlt(SLIDER, guiLeft, guiTop+25+5*22, 80, StatCollector.translateToLocal("gui.threshold"), 0, 0 , 64);

        //this.buttonList.add(slider);

        slider.enabled = false;
        slider.visible = false;

        colourTextField = new GuiTextFieldAlt(this.fontRendererObj, x_rpanel_start + 10 , y_rpanel_start+91, 75, 20);
        colourTextField.setText("F000");
        colourTextField.setMaxStringLength(4);
        colourTextField.setEnableBackgroundDrawing(false);
    }

    @Override
    public void updateScreen() {
        int x = (Mouse.getEventX() * this.width / this.mc.displayWidth -(x_canvus_start))/canvusMult;
        int y = (this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - (y_canvus_start))/canvusMult;

        try{
            if(x >= 0 && x < ImageData.IMAGE_RES && y >= 0 && y < ImageData.IMAGE_RES){
                Mouse.setNativeCursor(cursors[toolIndex + 1]);
            }else{
                Mouse.setNativeCursor(cursors[0]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        if(selectedTool instanceof TextTool && (last_refresh + 500 < System.currentTimeMillis())){
            last_refresh = System.currentTimeMillis();
            selectedTool.drawOverlay(x,y,imageBuffer[bufferPointer],overlay,ImageData.roundColour(colourPicker.getRGB()), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));

        }

        super.updateScreen();
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {

        int x = (Mouse.getEventX() * this.width / this.mc.displayWidth -(x_canvus_start))/canvusMult;
        int y = (this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - (y_canvus_start))/canvusMult;

        if(x >= 0 && x < ImageData.IMAGE_RES && y >= 0 && y < ImageData.IMAGE_RES){
            if(selectedTool instanceof EyeDropperTool){
                colourPicker.selectColour(imageBuffer[bufferPointer][x+ImageData.IMAGE_RES*y]);
            }else if (selectedTool instanceof RectangleTool){
                ((RectangleTool) selectedTool).last_x = x;
                ((RectangleTool) selectedTool).last_y = y;
            }else if(selectedTool instanceof TextTool){
                selectedTool.draw(x, y, imageBuffer[bufferPointer], ImageData.roundColour(colourPicker.getRGB()), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
            }else{
                int next = (bufferPointer+1) % BUFFER_SIZE;
                imageBuffer[next] = Arrays.copyOf(imageBuffer[bufferPointer], ImageData.IMAGE_RES * ImageData.IMAGE_RES);
                bufferPointer = next;

                //Clear the next
                imageBuffer[(bufferPointer+1) % BUFFER_SIZE] = null;

                selectedTool.draw(x, y, imageBuffer[bufferPointer], ImageData.roundColour(colourPicker.getRGB()), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
            }
        }else if (selectedTool instanceof RectangleTool){
            ((RectangleTool) selectedTool).last_x = -1000;
            ((RectangleTool) selectedTool).last_y = -1000;
        }

        super.mouseClicked(par1, par2, par3);
        colourTextField.mouseClicked(par1, par2, par3);
    }

    @Override
    public void handleMouseInput() {

        super.handleMouseInput();

        int x = (Mouse.getEventX() * this.width / this.mc.displayWidth -(x_canvus_start))/canvusMult;
        int y = (this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - (y_canvus_start))/canvusMult;

        if(! Mouse.getEventButtonState() && Mouse.getEventButton() == 0){
            if(selectedTool instanceof RectangleTool){
                int next = (bufferPointer+1) % BUFFER_SIZE;
                imageBuffer[next] = Arrays.copyOf(imageBuffer[bufferPointer], ImageData.IMAGE_RES * ImageData.IMAGE_RES);
                bufferPointer = next;

                //Clear the next
                imageBuffer[(bufferPointer+1) % BUFFER_SIZE] = null;

                selectedTool.draw(x, y, imageBuffer[bufferPointer], ImageData.roundColour(colourPicker.getRGB()), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
            }
        }

        selectedTool.drawOverlay(x,y,imageBuffer[bufferPointer],overlay, ImageData.roundColour(colourPicker.getRGB()), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));

    }

    @Override
    protected void mouseClickMove(int par1, int par2, int par3, long par4) {
        super.mouseClickMove(par1, par2, par3, par4);

        int x = (Mouse.getEventX() * this.width / this.mc.displayWidth -(x_canvus_start))/canvusMult;
        int y = (this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - (y_canvus_start))/canvusMult;

        if(Mouse.isButtonDown(0)){
            if(selectedTool instanceof EyeDropperTool){
                if (x > -1 && x < ImageData.IMAGE_RES && y > -1 && y < ImageData.IMAGE_RES){
                    colourPicker.selectColour(imageBuffer[bufferPointer][x+ImageData.IMAGE_RES*y]);
                }
            }else{
                selectedTool.draw(x, y, imageBuffer[bufferPointer], ImageData.roundColour(colourPicker.getRGB()), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        int[] overPixels = overlay.getTextureData();
        for(int i = 0; i < overPixels.length; i++){
            overPixels[i] = 0x00000000;
        }

        try{
            Mouse.setNativeCursor(cursors[0]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();

        mc.renderEngine.bindTexture(map_background);
        drawTexturedModalRect(x_canvus_start - canvus_pad, y_canvus_start - canvus_pad, canvusSize + 2 * canvus_pad, canvusSize + 2 * canvus_pad, 0, 0, 1, 1);

        mc.renderEngine.bindTexture(background);
        //Draw Top Panel
        drawTexturedModalRect(x_tpanel_start, y_tpanel_start, 0,0, x_tpanel_width/2, y_tpanel_height);
        drawTexturedModalRect(x_tpanel_start+x_tpanel_width/2, y_tpanel_start, 0,30, x_tpanel_width/2, y_tpanel_height);
        //drawRect(x_tpanel_start, y_tpanel_start, x_tpanel_start + x_tpanel_width, y_tpanel_start + y_tpanel_height, 0xFFAAAAAA);

        //Draw Left Panel
        drawTexturedModalRect(x_lpanel_start, y_lpanel_start, 0,60, x_lpanel_width, y_lpanel_height);
        //drawRect(x_lpanel_start, y_lpanel_start, x_lpanel_start + x_lpanel_width, y_lpanel_start + y_lpanel_height, 0xFFAAAAAA);

        //Draw Right Panel
        drawTexturedModalRect(x_rpanel_start, y_rpanel_start, 30,60, x_rpanel_width, y_rpanel_height);


        colourTextField.drawTextBox();

        GL11.glColor3f(1, 1, 1);

        //Draw Canvas
        canvus_back.updateDynamicTexture();
        drawTexturedModalRect(x_canvus_start, y_canvus_start, canvusSize, canvusSize, 0, 0, 32, 32);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        overlay.updateDynamicTexture();
        drawTexturedModalRect(x_canvus_start, y_canvus_start, canvusSize, canvusSize, 0, 0, 1, 1);
        GL11.glDisable(GL11.GL_BLEND);

        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        super.actionPerformed(par1GuiButton);

        if(par1GuiButton.id >=10 && par1GuiButton.id < 10+toggleButtons.length){
            for(int i = 0; i < toggleButtons.length; i++){
                toggleButtons[i].setToggle(i+10==par1GuiButton.id);
            }
            selectedTool = tools[par1GuiButton.id - 10];
            toolIndex = par1GuiButton.id - 10;

            slider.enabled = selectedTool instanceof FloodFillTool;
            slider.visible = selectedTool instanceof FloodFillTool;

            if(selectedTool instanceof TextTool){
                ((TextTool) selectedTool).text="";
                ((TextTool) selectedTool).click_x = -1000;
                ((TextTool) selectedTool).click_y = -1000;
            }
        }

        switch (par1GuiButton.id){
            case ID_OK:
                ItemStack stack = player.getCurrentEquippedItem();
                if(stack != null && stack.getItem() instanceof IHeraldryItem){
                    ((IHeraldryItem) stack.getItem()).setHeraldry(stack, new ImageData(imageBuffer[bufferPointer], ImageData.IMAGE_RES, ImageData.IMAGE_RES).getByteArray());
                    Battlegear.packetHandler.sendPacketToServer(new BattlegearChangeHeraldryPacket(player.getCommandSenderName(), ((IHeraldryItem) stack.getItem()).getHeraldry(stack)).generatePacket());
                    this.keyTyped('c',1);
                }
                break;
            case ID_SAVE:
                if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
                    BufferedImage image = new BufferedImage(ImageData.IMAGE_RES, ImageData.IMAGE_RES, BufferedImage.TYPE_4BYTE_ABGR);
                    int[] pixels = imageBuffer[bufferPointer];
                    for(int x = 0; x < image.getWidth(); x++){
                        for(int y = 0; y < image.getHeight(); y++){
                            image.setRGB(x, y, pixels[x+ImageData.IMAGE_RES*y]);
                        }
                    }

                    try {

                        File f = fc.getSelectedFile();
                        if(new FileExtension(f.getName()).get() == null){
                            f = new File(f.getParentFile(), f.getName()+".png");
                        }

                        f.createNewFile();

                        ImageIO.write(image, "png", f);

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ID_LOAD:
                if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    try{
                        ImageData image = new ImageData(ImageIO.read(fc.getSelectedFile()), ImageData.IMAGE_RES, ImageData.IMAGE_RES);
                        image.setTexture(imageBuffer[bufferPointer]);

                        int next = (bufferPointer+1) % BUFFER_SIZE;
                        imageBuffer[next] = Arrays.copyOf(imageBuffer[bufferPointer], ImageData.IMAGE_RES * ImageData.IMAGE_RES);
                        bufferPointer = next;
                        imageBuffer[(bufferPointer+1) % BUFFER_SIZE] = null;

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case ID_LOAD_SECTION:
                if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    try{
                        BufferedImage original = ImageIO.read(fc.getSelectedFile());

                        ImageSplitDialog dialog = new ImageSplitDialog(original);
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);

                        if(dialog.imageSection != null){
                            ImageData image = new ImageData(
                                    dialog.imageSection,
                                    ImageData.IMAGE_RES, ImageData.IMAGE_RES);

                            image.setTexture(imageBuffer[bufferPointer]);

                            int next = (bufferPointer+1) % BUFFER_SIZE;
                            imageBuffer[next] = Arrays.copyOf(imageBuffer[bufferPointer], ImageData.IMAGE_RES * ImageData.IMAGE_RES);
                            bufferPointer = next;
                            imageBuffer[(bufferPointer+1) % BUFFER_SIZE] = null;
                        }

                        dialog.dispose();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case SLIDER:
                ((FloodFillTool)tools[1]).threshold = slider.getValue();
                break;
        }
    }

    public void drawTexturedModalRect(int x, int y, int width, int height, int tex_x, int tex_y, int tex_width, int tex_height)
    {
        float f = 1F;
        float f1 = 1F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)this.zLevel, (double)((float)(tex_x + 0) * f), (double)((float)(tex_y + tex_height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((float)(tex_x + tex_width) * f), (double)((float)(tex_y + tex_height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel, (double)((float)(tex_x + tex_width) * f), (double)((float)(tex_y + 0) * f1));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(tex_x + 0) * f), (double)((float)(tex_y + 0) * f1));
        tessellator.draw();
    }

}