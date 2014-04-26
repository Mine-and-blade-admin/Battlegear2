package mods.mud.gui;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import mods.mud.ModUpdateDetector;
import mods.mud.UpdateChecker;
import mods.mud.UpdateEntry;
import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.*;
import java.util.List;

public class GuiChangelogDownload extends GuiScreen
{
    private GuiSlotModList modList;
    private int selected = -1;
    private UpdateEntry selectedMod;
    private ArrayList<UpdateEntry> entries;

    private String[] changelog;

    int lineStart = 0;
    private GuiButton disable;
    private GuiButton download;
    private GuiButton close1;
    private GuiButton ok;
    private GuiButton urlButton;

    private boolean isDownloading = false;
    private boolean downloadComplete = false;
    private boolean downloadFailed = false;
    private float downloadPercent;
    private String message;

    private GuiScreen parent;
    private char[] bullets = new char[]{0x2219, 0x25E6, 0x2023};
    private int[] bulletWidth = new int[3];

    private Thread getChangeLogThread;

    public GuiChangelogDownload(GuiScreen parent){
        this.parent = parent;
        changelog = new String[]{StatCollector.translateToLocal("log.message.loading")};
        if(!ModUpdateDetector.hasChecked){
            new UpdateChecker(ModUpdateDetector.getAllUpdateEntries()).run();
        }

        this.entries=new ArrayList<UpdateEntry>(ModUpdateDetector.getAllUpdateEntries());
    }

    public GuiChangelogDownload(){
        this(null);
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.modList=new GuiSlotModList(this, entries, 100);
        this.modList.registerScrollButtons(this.buttonList, 1, 2);

        for(int i= 0; i < bullets.length; i++){
            bulletWidth[i] = fontRendererObj.getStringWidth(bullets[i]+" ");
        }
        disable = new GuiButton(3, 15, 10, 125, 20, StatCollector.translateToLocal("mud.disable")+": "+Boolean.toString(!ModUpdateDetector.enabled));
        download = new GuiButton(4, 15, height-35, 125, 20, StatCollector.translateToLocal("button.download.latest"));
        download.enabled = false;
        close1 = new GuiButton(5, width-140, height-35, 125, 20, StatCollector.translateToLocal("gui.done"));
        ok = new GuiButton(6, (width - 200)/2 + 5, (height - 150)/2+115, 190, 20, StatCollector.translateToLocal("button.ok"));
        urlButton = new GuiButton(7, (width - 125)/2, height-35, 125, 20, StatCollector.translateToLocal("button.url"));
        urlButton.enabled = false;
        ok.visible = isDownloading;
        ok.enabled = downloadComplete || downloadFailed;

        buttonList.add(disable);
        buttonList.add(download);
        buttonList.add(close1);
        buttonList.add(ok);
        buttonList.add(urlButton);
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
        if(!isDownloading){
            if (par2 == 1)
            {
                this.mc.displayGuiScreen(parent);
                this.mc.setIngameFocus();
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled)
        {
            if(!isDownloading || (downloadFailed || downloadComplete) ){
                switch (button.id)
                {
                    case 3:
                        isDownloading = false;
                        close1.enabled = true;
                        ModUpdateDetector.toggleState();
                        disable.displayString = StatCollector.translateToLocal("mud.disable")+":"+Boolean.toString(!ModUpdateDetector.enabled);
                        return;
                    case 4:
                        ModContainer mc = selectedMod.getMc();
                        String filename = String.format("[%s] %s - %s.jar",
                                Loader.instance().getMCVersionString().replaceAll("Minecraft", "").trim(),
                                mc.getName(),
                                selectedMod.getLatest().getVersionString());
                        File newFile = new File(mc.getSource().getParent(), filename);
                        Thread t = new Thread(new Downloader(selectedMod.getLatest().download,
                                newFile, mc.getSource(), selectedMod.getLatest().md5
                        ));
                        t.start();
                        isDownloading = true;
                        ok.visible = true;
                        close1.enabled = false;
                        download.enabled = false;
                        urlButton.enabled = false;
                        return;
                    case 5:
                        this.mc.displayGuiScreen(parent);
                        return;
                    case 6:
                        isDownloading = false;
                        downloadComplete = false;
                        downloadFailed = false;
                        ok.visible = false;
                        close1.enabled = true;
                        urlButton.enabled=true;
                        return;
                    case 7:
                        if(selectedMod != null && selectedMod.getLatest() != null && selectedMod.getLatest().url != null){
                            try {
                                Desktop.getDesktop().browse(new URI(selectedMod.getLatest().url));
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }catch (URISyntaxException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                        return;
                }
            }
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawDefaultBackground() {
        super.drawBackground(0);
    }

    @Override
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
            ok.visible = true;
            int x = (width - 200)/2;
            int y = (height - 150)/2;

            drawRect(x-1,y-1,x+201, y+151, 0xFFFFFFFF);
            drawRect(x,y,x+200, y+150, 0xFF000000);

            drawCenteredString(fontRendererObj, StatCollector.translateToLocal("gui.downloading"), width/2, y + 15, 0xFFFFFF00);

            drawRect(x + 24, y + 39, x+176, y+56, 0xFFFFFFFF);
            drawRect(x + 25, y + 40, (x+25 + 150), y+55, 0xFF000000);


            drawRect(x + 25, y + 40, (int)(x+25 + 150*downloadPercent), y+55, 0xFFc0c0c0);
            drawVerticalLine((int)(x+25 + 150*downloadPercent)-1, y + 39, y+55, 0xFF808080);
            drawHorizontalLine(x + 25, (int)(x+25 + 150*downloadPercent)-1, y + 54, 0xFF808080);

            if(downloadComplete){
                drawCenteredString(fontRendererObj, StatCollector.translateToLocal("gui.download.complete"), width/2, y + 70, 0xFF44FF44);
            }

            if(downloadFailed){
                drawCenteredString(fontRendererObj, StatCollector.translateToLocal("gui.download.failed"), width/2, y + 70, 0xFFFF0000);
            }

            if(message != null){
                drawCenteredString(fontRendererObj, message, width/2, y + 85, 0xFFFFFFFF);
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
            this.drawString(fontRendererObj, main.replaceAll("=", "").trim(), (int)((startX)*scale), (int)(start*scale), 0xFFFFFF00);
            GL11.glScalef(scale, scale, scale);

            if(line.lastIndexOf("==")+2 <= line.length()){
                String sub = line.substring(line.lastIndexOf("==")+2, line.length()).trim();
                GL11.glScalef(1/scale, 1/scale, 1/scale);
                this.drawString(fontRendererObj, sub.replaceAll("=", "").trim(), (int)((startX+fontRendererObj.getStringWidth(main+"   "))*scale), (int)(start*scale), 0xFF2222FF);
                GL11.glScalef(scale, scale, scale);
            }

            return (int)(1F/scale * 10)+start;
        }else if(line.trim().startsWith("**") && line.trim().endsWith("**")){
            float scale = 1.1F;
            GL11.glScalef(1/scale, 1/scale, 1/scale);
            this.drawString(fontRendererObj, line.replaceAll("\\*\\*", "").trim(), (int)((startX)*scale), (int)(start*scale), 0xFFFFFFFF);
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

            List<String> lineList = fontRendererObj.listFormattedStringToWidth(line, width - 10 - startX);
            Iterator<String> it = lineList.iterator();
            for(int i = 0; it.hasNext(); i++){
                String subline = it.next().trim();

                if(i == 0 && bullet > -1){
                    subline = bullets[bullet]+" "+subline;
                }else if (bullet > -1){
                    startX+=bulletWidth[bullet];
                }
                this.drawString(fontRendererObj, subline, (int) ((startX) * scale), (int) (start * scale), 0xFFFFFFFF);
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
        return fontRendererObj;
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

            urlButton.enabled = selectedMod!=null && selectedMod.getLatest()!=null && selectedMod.getLatest().url!=null;
            if(selectedMod!=null) {
                if (selectedMod.getChangelogURL() == null) {
                    changelog = new String[]{StatCollector.translateToLocal("log.message.none")};
                } else {
                    getChangeLogThread = new Thread(new ChangelogLoader(selectedMod.getChangelogURL()));
                    getChangeLogThread.start();
                    changelog = new String[]{StatCollector.translateToLocal("log.message.loading")};
                }

                try {
                    if (!selectedMod.isUpToDate()) {
                        download.enabled = true;
                        download.displayString = StatCollector.translateToLocal("button.download.latest");
                    }
                } catch (Exception e) {

                }
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
                changelog = new String[]{StatCollector.translateToLocal("log.message.fail")};
            }
        }
    }

    private class Downloader implements Runnable{

        private String downloadUrl = "";
        private File file = null;
        private File orginial;
        private byte[] expectedMd5;

        public Downloader(String url, File location, File originalFile, String md5){
            this.downloadUrl = url;
            this.file = location;
            this.orginial = originalFile;
            if(md5 != null){
                try{
                    this.expectedMd5 =  DatatypeConverter.parseHexBinary(md5.toUpperCase());
                }catch (Exception e){
                    e.printStackTrace();
                    this.expectedMd5 = null;
                }
            }

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

                if(expectedMd5 != null){
                    DataInputStream dis = null;
                    try {
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte [] fileData = new byte[(int)file.length()];
                        dis = new DataInputStream((new FileInputStream(file)));
                        dis.readFully(fileData);
                        dis.close();
                        byte[] md5 = md.digest(fileData);

                        ModUpdateDetector.logger.trace("Expected MD5: "+bytArrayToHex(expectedMd5));
                        ModUpdateDetector.logger.trace("File MD5: "+bytArrayToHex(md5));

                        if(Arrays.equals(md5, expectedMd5)){
                            downloadComplete = true;
                            ok.enabled = true;
                            message = StatCollector.translateToLocal("gui.restart");
                        }else{
                            downloadComplete = false;
                            downloadFailed = true;
                            ok.enabled = true;
                            message = StatCollector.translateToLocal("gui.md5.fail");

                            //file.delete();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        if(dis != null){
                            try{
                                dis.close();
                            }catch (Exception ex){}
                        }

                        downloadFailed = true;
                        ok.enabled = true;

                    }
                }else{
                    downloadComplete = true;
                    ok.enabled = true;
                    message = StatCollector.translateToLocal("gui.restart");
                }

                if(downloadComplete &&
                        orginial.exists() &&
                        !orginial.getName().equals(file.getName()) &&
                        !orginial.isDirectory()
                        ){
                    ModUpdateDetector.logger.trace("Deleting: "+orginial.getAbsolutePath());
                    if(!orginial.delete()){
                        ModUpdateDetector.logger.trace("Deleting failed, spawning new process to delete");
                        String cmd = "java -classpath \""+file.getAbsolutePath()+"\" mods.mud.utils.FileDeleter \""+orginial.getAbsolutePath()+"\"";
                        Runtime.getRuntime().exec(cmd);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                message = StatCollector.translateToLocal(e.getLocalizedMessage());
                downloadFailed = true;
                ok.enabled = true;
            }
        }


    }

    String bytArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }
}
