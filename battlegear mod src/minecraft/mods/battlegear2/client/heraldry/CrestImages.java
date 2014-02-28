package mods.battlegear2.client.heraldry;

import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CrestImages implements Comparable<CrestImages>{

    private static final int X_AMOUNT = 16;
    private static final int Y_AMOUNT = 16;


    private int ID;
    private String name;
    private byte[][] col;
    public static CrestImages[] images = new CrestImages[5 * 16 * 16 + 1];

    private BufferedImage imageCache = null;
    private int lastRGB;

    public static final ResourceLocation crestNames = new ResourceLocation("battlegear2:textures/heraldry/crests/names");


    private CrestImages(){
        ID = 0;
        name = "None";
        col = new byte[][]{{0}};
        images[ID] = this;
    }

    private CrestImages(int sheet, int x_pos, int y_pos, String name, BufferedImage image){
        this.ID = x_pos + y_pos*X_AMOUNT + sheet * X_AMOUNT * Y_AMOUNT + 1;
        this.name = name;
        
        
        BufferedImage sub = image.getSubimage(x_pos*X_AMOUNT, y_pos*Y_AMOUNT, image.getWidth() / X_AMOUNT, image.getHeight() / Y_AMOUNT);
        col = new byte[sub.getWidth()][sub.getHeight()];
        for(int x = 0; x < sub.getWidth(); x++){
            for(int y = 0; y < sub.getHeight(); y++){
                col[x][y] = (byte) (((sub.getRGB(x, y) >> 16) & 0x000000FF));
            }
        }
        images[ID] = this;
    }

    public BufferedImage getImage(int rgb){
        if(imageCache != null && rgb == lastRGB){
            return imageCache;
        }else{
            imageCache = new BufferedImage(col.length, col[0].length, BufferedImage.TYPE_4BYTE_ABGR);
            System.out.println(imageCache.getHeight());
            lastRGB = rgb;
            for(int x = 0; x < col.length; x++){
                for(int y = 0; y < col[x].length; y++){
                    imageCache.setRGB(x,y, (rgb & 0x00FFFFFF) | ((((int)col[x][y])<<24) & 0xFF000000));
                }
            }
            return imageCache;
       }
    }

    public static void initialise(IResourceManager rm){
        new CrestImages();

        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new InputStreamReader(rm.getResource(crestNames).getInputStream()));

            int current_sheet = 0;
            int currentID = 0;
            BufferedImage image = null;

            while(reader.ready()){
                String line = reader.readLine();
                if(line.startsWith("Sheet: ")){
                    currentID = 0;
                    current_sheet = Integer.parseInt(String.valueOf(line.charAt(line.length()-1)));
                    image = ImageIO.read(rm.getResource(new ResourceLocation("battlegear2:textures/heraldry/crests/icons-"+current_sheet+".png")).getInputStream());

                }else{
                    String[] names = line.split(",");
                    for(int i = 0; i < names.length; i++){
                        int x = (currentID) % X_AMOUNT;
                        int y = (currentID) / X_AMOUNT;
                        new CrestImages(current_sheet, x, y, names[i], image);
                        currentID++;
                    }
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        } finally {
            BattlegearUtils.closeStream(reader);
        }


    }

    @Override
    public String toString() {
        return "CrestImages{" +
                "ID=" + ID +
                ", name='" + name +
                ", size='" + col.length + "x"+col[0].length+'\'' +
                '}';
    }

    @Override
    public int compareTo(CrestImages crestImages) {
        if(ID == crestImages.ID){
            return 0;
        }else{
            if(ID == 0){
                return 1;
            }else if (crestImages.ID == 0){
                return -1;
            }else{
                return name.compareTo(crestImages.name);
            }
        }
    }

    public int getId() {
        return ID;
    }
}
