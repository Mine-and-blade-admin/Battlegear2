package mods.mum.gui;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import mods.battlegear2.Battlegear;
import mods.mum.ModUpdateManager;
import mods.mum.UpdateEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

public class ModListGui extends GuiScreen
{
    private GuiSlotModList modList;
    private int selected = -1;
    private UpdateEntry selectedMod;
    private int listWidth;
    private ArrayList<UpdateEntry> entries;

    private String[] changelog;

    int lineStart = 0;
    int scrollbarHeight = 24;
    int scrollLocHeight;

    private GuiButton download;
    private GuiButton close1;
    private GuiButton ok;

    private boolean isDownloading = false;
    private boolean downloadComplete = false;
    private boolean downloadFailed = false;
    private float downloadPercent;


    private char[] bullets = new char[]{0x2219, 0x25E6, 0x2023};
    private int[] bulletWidth = new int[3];

    private Thread getChangeLogThread;

    public ModListGui()
    {
        changelog = new String[]{"Loading Changelog from server"};
        this.entries=new ArrayList<UpdateEntry>(ModUpdateManager.getAllUpdateEntries());
    }

    @Override

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        listWidth=100;
        this.buttonList.clear();
        this.modList=new GuiSlotModList(this, entries, listWidth);
        this.modList.registerScrollButtons(this.buttonList, 7, 8);


        for(int i= 0; i < bullets.length; i++){
            bulletWidth[i] = fontRenderer.getStringWidth(bullets[i]+" ");
        }

        download = new GuiButton(4, 30, height-35, 150, 20, StatCollector.translateToLocal("button.download.latest"));
        download.enabled = false;
        close1 = new GuiButton(5, width-30-150, height-35, 150, 20, StatCollector.translateToLocal("button.close"));
        ok = new GuiButton(6, (width - 200)/2 + 5, (height - 150)/2+115, 190, 20, StatCollector.translateToLocal("button.ok"));
        ok.drawButton = false;
        ok.enabled = false;


        buttonList.add(download);
        buttonList.add(close1);
        buttonList.add(ok);
    }

    protected void keyTyped(char par1, int par2)
    {
        if(!isDownloading){
            super.keyTyped(par1, par2);
        }
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled)
        {
            if(!isDownloading || (downloadFailed || downloadComplete) ){
                switch (button.id)
                {
                    case 4:
                            ModContainer mc = selectedMod.getMc();
                            String filename = String.format("[%s] %s - %s.jar",
                                    Loader.instance().getMCVersionString().replaceAll("Minecraft", "").trim(),
                                    mc.getName(),
                                    selectedMod.getLatest().getVersionString());
                            File newFile = new File(mc.getSource().getParent(), filename);
                            Thread t = new Thread(new Downloader(selectedMod.getLatest().url,
                                    newFile, mc.getSource()
                            ));
                            t.start();
                            isDownloading = true;
                            ok.drawButton = true;
                            close1.enabled = false;
                            download.enabled = false;
                        return;
                    case 5:
                        this.mc.displayGuiScreen(null);
                        this.mc.setIngameFocus();
                        return;
                    case 6:
                        isDownloading = false;
                        downloadComplete = false;
                        downloadFailed = false;
                        ok.drawButton = false;
                        close1.enabled = true;
                        return;
                }
            }
        }
        super.actionPerformed(button);
    }


    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
    }


    @Override
    public void drawDefaultBackground() {
        super.drawBackground(0);
    }

    @Override

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
    {
        if(modList != null)
            this.modList.drawScreen(p_571_1_, p_571_2_, p_571_3_);

        if(selectedMod != null){

            int start = 32;
            for(int i = lineStart; i < changelog.length && start < height-52; i++){
                start = drawText(changelog[i], start);
            }

        }

        int mouse_x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouse_y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        if(mouse_x > 125 && mouse_y > 35 && mouse_y < height-50){
            int scroll = Mouse.getDWheel();
            if(scroll < 0 && lineStart < changelog.length-15){
                lineStart++;
            }
            if(scroll > 0 && lineStart > 0){
                lineStart--;
            }
        }

        if(isDownloading){
            ok.drawButton = true;
            int x = (width - 200)/2;
            int y = (height - 150)/2;

            drawRect(x-1,y-1,x+201, y+151, 0xFFFFFFFF);
            drawRect(x,y,x+200, y+150, 0xFF000000);

            drawCenteredString(fontRenderer, StatCollector.translateToLocal("gui.downloading"), width/2, y + 15, 0xFFFFFF00);

            drawRect(x + 24, y + 39, x+176, y+56, 0xFFFFFFFF);
            drawRect(x + 25, y + 40, (x+25 + 150), y+55, 0xFF000000);


            drawRect(x + 25, y + 40, (int)(x+25 + 150*downloadPercent), y+55, 0xFFc0c0c0);
            drawVerticalLine((int)(x+25 + 150*downloadPercent)-1, y + 39, y+55, 0xFF808080);
            drawHorizontalLine(x + 25, (int)(x+25 + 150*downloadPercent)-1, y + 54, 0xFF808080);

            if(downloadComplete){
                drawCenteredString(fontRenderer, StatCollector.translateToLocal("gui.download.complete"), width/2, y + 70, 0xFF44FF44);
                drawCenteredString(fontRenderer, StatCollector.translateToLocal("gui.restart"), width/2, y + 85, 0xFFFFFFFF);
            }

            if(downloadFailed){
                drawCenteredString(fontRenderer, StatCollector.translateToLocal("gui.download.failed"), width/2, y + 70, 0xFFFF0000);
            }
            ok.drawButton(mc, p_571_1_, p_571_2_);

        }

        super.drawScreen(p_571_1_, p_571_2_, p_571_3_);
    }

    private int drawText(String line, int start) {
        int startX = 125;

        if(line == null){
            return 5;
        } else if(line.startsWith("==") && line.substring(2).contains("==")){

            String main = line.substring(2, line.lastIndexOf("==")).trim();

            float scale = 1F;
            GL11.glScalef(1/scale, 1/scale, 1/scale);
            this.drawString(fontRenderer, main.replaceAll("=", "").trim(), (int)((startX)*scale), (int)(start*scale), 0xFFFFFF00);
            GL11.glScalef(scale, scale, scale);

            if(line.lastIndexOf("==")+2 <= line.length()){
                String sub = line.substring(line.lastIndexOf("==")+2, line.length()).trim();
                GL11.glScalef(1/scale, 1/scale, 1/scale);
                this.drawString(fontRenderer, sub.replaceAll("=", "").trim(), (int)((startX+fontRenderer.getStringWidth(main+"   "))*scale), (int)(start*scale), 0xFF2222FF);
                GL11.glScalef(scale, scale, scale);
            }

            return (int)(1F/scale * 10)+start;
        }else if(line.startsWith("**") && line.endsWith("**")){
            float scale = 1.1F;
            GL11.glScalef(1/scale, 1/scale, 1/scale);
            this.drawString(fontRenderer, line.replaceAll("\\*\\*", "").trim(), (int)((startX)*scale), (int)(start*scale), 0xFFFFFFFF);
            GL11.glScalef(scale, scale, scale);
            return (int)(1F/scale * 10)+start;
        }else{
            float scale = 1.2F;
            GL11.glScalef(1/scale, 1/scale, 1/scale);

            int bullet = -1;
            while(line.startsWith("*")){
                startX+=10;
                line = line.substring(1).trim();
                bullet++;
            }

            if(bullet > 3){
                bullet = 3;
            }

            List<String> lineList = fontRenderer.listFormattedStringToWidth(line, width - 10 - startX);
            Iterator<String> it = lineList.iterator();
            for(int i = 0; it.hasNext(); i++){
                String subline = it.next().trim();

                if(i == 0 && bullet > -1){
                    subline = bullets[bullet]+" "+subline;
                }else if (bullet > -1){
                    startX+=bulletWidth[bullet];
                }
                this.drawString(fontRenderer, subline, (int) ((startX) * scale), (int) (start * scale), 0xFFFFFFFF);
                start += (int)(1F/scale * 10);
            }


            GL11.glScalef(scale, scale, scale);
            return start;
        }
    }

    Minecraft getMinecraftInstance() {
        return mc;
    }

    FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    /**
     * @param var1
     */
    public void selectModIndex(int var1)
    {
        if(getChangeLogThread == null || !getChangeLogThread.isAlive()){
            this.selected=var1;
            if (var1>=0 && var1<=entries.size()) {
                this.selectedMod=entries.get(selected);
            } else {
                this.selectedMod=null;
            }

            if(selectedMod.getChangelogURL() == null){
                changelog = new String[]{"No Changelog Provided"};
            }else{
                getChangeLogThread = new Thread(new ChangelogLoader(selectedMod.getChangelogURL()));
                getChangeLogThread.start();
                changelog = new String[]{"Loading changelog from server"};
            }

            try{
                if(!selectedMod.isUpToDate()){
                    download.enabled = true;
                    download.displayString = StatCollector.translateToLocal("button.download.latest");
                }
            }catch (Exception e){

            }
        }
    }

    public boolean modIndexSelected(int var1)
    {
        return var1==selected;
    }

    private class ChangelogLoader implements Runnable{

        URL changelogURL;

        private ChangelogLoader(URL changelogURL) {
            this.changelogURL = changelogURL;
        }

        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(changelogURL.openStream()));
                String line = br.readLine();
                ArrayList<String> lines = new ArrayList<String>(100);
                if(line != null)
                    lines.add(line);
                while(line != null){
                    line = br.readLine();
                    lines.add(line);
                }

                changelog = lines.toArray(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
                changelog = new String[]{"Failed to download changelog"};
            }
        }
    }

    private class Downloader implements Runnable{

        private String downloadUrl = "";
        private File file = null;
        private File orginial;

        public Downloader(String url, File location, File originalFile){
            this.downloadUrl = url;
            this.file = location;
            this.orginial = originalFile;
        }

        @Override
        public void run() {
            try {
                isDownloading = true;

                if(file.exists())
                    file.delete();
                file.createNewFile();

                URL url=new URL(downloadUrl);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                int filesize = connection.getContentLength();
                float totalDataRead=0;
                java.io.BufferedInputStream in = new java.io.BufferedInputStream(connection.getInputStream());
                java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
                java.io.BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
                byte[] data = new byte[1024];
                int i;
                while((i=in.read(data,0,1024))>=0)
                {
                    totalDataRead=totalDataRead+i;
                    bout.write(data,0,i);
                    downloadPercent=(totalDataRead)/filesize;
                }
                bout.close();
                in.close();
                downloadComplete = true;
                ok.enabled = true;

                if(orginial.exists() &&
                        !orginial.getName().equals(file.getName()) &&
                        !World.class.getName().equals("net.minecraft.world.World")
                        ){
                    if(!orginial.delete()){
                        System.out.println("Spawning new process to delete");
                        Runtime.getRuntime().exec("cmd /c  java -classpath \""+file.getAbsolutePath()+"\" mods.mum.utils.FileDeleter \""+orginial.getAbsolutePath()+"\"");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Download failed");
                downloadFailed = true;
                ok.enabled = true;
            }
        }


    }
}
