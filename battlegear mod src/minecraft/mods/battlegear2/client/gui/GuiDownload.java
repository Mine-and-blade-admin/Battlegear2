package mods.battlegear2.client.gui;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import mods.battlegear2.Battlegear;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiDownload extends GuiScreen{

    private static final String changelogURL = "https://raw.github.com/Mine-and-blade-admin/Battlegear2/master/changelog.md";
    private String[] changelog;

    private int width;

    int lineStart = 0;
    int scrollbarHeight = 24;
    int scrollLocHeight;

    private GuiButton download;
    private GuiButton close1;
    private GuiButton close2;

    private char[] bullets = new char[]{0x2219, 0x25E6, 0x2023};
    private int[] bulletWidth = new int[3];

    private boolean isDownloading;
    private float downloadPercent;
    private boolean downloadComplete = false;
    private boolean downloadFailed = false;

    public GuiDownload(){
        changelog = new String[]{"Loading Changelog from server"};
        isDownloading = false;

    }

    @Override
    public void initGui() {
        super.initGui();

        Thread t = new Thread(new ChangelogLoader());
        t.start();

        ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        width = scaledresolution.getScaledWidth();
        height = scaledresolution.getScaledHeight();

        buttonList.clear();
        if(Battlegear.latestRelease == null){
            download = new GuiButton(0, 30, height-35, 150, 20, StatCollector.translateToLocal("button.download"));
            download.enabled = false;
        }else{
            download = new GuiButton(0, 30, height-35, 150, 20, StatCollector.translateToLocal("button.download")+" "+Battlegear.latestRelease.getVersionString());
            download.enabled = true;
        }

        close1 = new GuiButton(1, width-30-150, height-35, 150, 20, StatCollector.translateToLocal("button.close"));

        close2 = new GuiButton(2, (width - 200)/2 + 5, (height - 150)/2+115, 190, 20, StatCollector.translateToLocal("button.close"));
        close2.drawButton = false;
        close2.enabled = false;

        buttonList.add(download);
        buttonList.add(close1);
        buttonList.add(close2);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if(!isDownloading){
            super.keyTyped(par1, par2);
        }
    }

    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawBackground(0);
        super.drawScreen(par1, par2, par3);



        scrollLocHeight = (height-37) - 25;

        drawGradientRect(0, 25, width, 30, 0xFF000000, 0x88000000);
        drawGradientRect(0, 30, width, height-42, 0x88000000, 0x88000000);
        drawGradientRect(0, height-42, width, height-37, 0x88000000, 0xFF000000);

        drawRect(width - 50, 25, width - 45, 25+scrollLocHeight, 0xFF000000);

        if(changelog.length > 15){
            drawRect(width-50, 25 + (lineStart)*(scrollLocHeight-scrollbarHeight)/(changelog.length-15),
                    width-45, 25+scrollbarHeight + (lineStart)*(scrollLocHeight-scrollbarHeight)/(changelog.length-15),
                    0xFFc0c0c0);

        }else{
            drawRect(width-50, 25, width-45, height-37, 0xFFc0c0c0);
        }

        for(int i= 0; i < bullets.length; i++){
            bulletWidth[i] = fontRenderer.getStringWidth(bullets[i]+" ");
        }

        int start = 32;
        for(int i = lineStart; i < changelog.length && start < height-52; i++){
            start = drawText(changelog[i], start);
        }

        if(isDownloading){
            close2.drawButton = true;
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


            close2.drawButton(mc, par1, par2);

        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int scroll = Mouse.getDWheel();
        if(scroll < 0 && lineStart < changelog.length-15){
            lineStart++;
        }

        if(scroll > 0 && lineStart > 0){
            lineStart--;
        }

    }

    private int drawText(String line, int start) {


        int startX = 50;

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


            List<String> lineList = fontRenderer.listFormattedStringToWidth(line, width - 50 - startX);
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

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        super.actionPerformed(par1GuiButton);

        if(par1GuiButton.id == 0){
            if(!isDownloading){

                ModContainer mc = FMLCommonHandler.instance().findContainerFor(Battlegear.INSTANCE);
                String filename = String.format("[%s] %s - %s.jar",
                        Loader.instance().getMCVersionString().replaceAll("Minecraft", "").trim(),
                        mc.getName(),
                        Battlegear.latestRelease.getVersionString());

                Thread t = new Thread(new Downloader(Battlegear.latestRelease.download,
                        new File(Battlegear.modSrc.getParent(), filename)));
                t.start();
                isDownloading = true;
                close2.drawButton = true;
                close1.enabled = false;
                download.enabled = false;
            }
        }else{
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }

    }

    private class ChangelogLoader implements Runnable{

        @Override
        public void run() {
            try {

                BufferedReader br = new BufferedReader(new InputStreamReader(new URL(changelogURL).openStream()));
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

        public Downloader(String url, File location){
            this.downloadUrl = url;
            this.file = location;
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
                if(Battlegear.modSrc.exists() && !Battlegear.modSrc.getAbsolutePath().equals(file.getAbsolutePath())){
                    Battlegear.modSrc.delete();
                }
                downloadComplete = true;
                close2.enabled = true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Download failed");
                downloadFailed = true;
                close2.enabled = true;
            }
        }


    }


}
