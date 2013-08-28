package mods.battlegear2.client.heraldry;

import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class PatternStore {

    public static final ResourceLocation small_image = new ResourceLocation("battlegear2", "textures/heraldry/Patterns-small.png");


    public static final int IMAGES_X = 8;
    public static final int IMAGES_Y = 4;
    
    


    public static int[][][][] small_rgbs;

    public static void initialise(ResourceManager rm){
        try{
            InputStream inputstream = rm.func_110536_a(small_image).func_110527_b();
            BufferedImage image = ImageIO.read(inputstream);

            small_rgbs = new int[IMAGES_X * IMAGES_Y][3][(image.getWidth() / IMAGES_X)][(image.getHeight() / IMAGES_Y)];

            int imageRes = image.getWidth() / IMAGES_X;
            for(int x = 0; x < image.getWidth(); x++){
                for(int y = 0; y < image.getHeight(); y++){
                    int imageNo = (x / imageRes) + IMAGES_X * (y / imageRes);
                    int rgb = image.getRGB(x,y);
                    int red = (rgb >> 16) & 0x000000FF;
                    int green = (rgb >> 8) & 0x000000FF;
                    int blue = (rgb) & 0x000000FF;

                    int total = red+green+blue;

                    if(total == 0){
                        small_rgbs[imageNo][0][x%imageRes][y%imageRes] = 255;
                        small_rgbs[imageNo][1][x%imageRes][y%imageRes] = 0;
                        small_rgbs[imageNo][2][x%imageRes][y%imageRes] = 0;
                    }else{
                        small_rgbs[imageNo][0][x%imageRes][y%imageRes] = (255 * red) / total;
                        small_rgbs[imageNo][1][x%imageRes][y%imageRes] = (255 * green) / total;
                        small_rgbs[imageNo][2][x%imageRes][y%imageRes] = (255 * blue) / total;
                    }
                }
                
                
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static int getBlendedSmallPixel(byte imageNo, int x, int y, int col1, int col2, int col3){

        int red = ((((col1 >> 16) & 0xFF) * small_rgbs[imageNo][0][x][y]) / 255) +
                ((((col2 >> 16) & 0xFF) * small_rgbs[imageNo][1][x][y]) / 255) +
                ((((col3 >> 16) & 0xFF) * small_rgbs[imageNo][2][x][y])/ 255);

        int green = (((col1 >> 8) & 0xFF) * small_rgbs[imageNo][0][x][y] / 255) +
                (((col2 >> 8) & 0xFF) * small_rgbs[imageNo][1][x][y] / 255) +
                (((col3 >> 8) & 0xFF) * small_rgbs[imageNo][2][x][y] / 255);

        int blue = (((col1) & 0xFF) * small_rgbs[imageNo][0][x][y] / 255) +
                (((col2) & 0xFF) * small_rgbs[imageNo][1][x][y] / 255) +
                (((col3) & 0xFF) * small_rgbs[imageNo][2][x][y] / 255);

        return 0xFF000000 |
                ((red << 16) & 0x00FF0000) |
                ((green << 8) & 0x0000FF00) |
                ((blue) & 0x000000FF);
    }





    /*
    public static void initialise(ResourceManager rm){

        try{
            InputStream inputstream = rm.func_110536_a(small_image).func_110527_b();
            BufferedImage image = ImageIO.read(inputstream);

            if(image != null){

                small_col1 = new byte[IMAGES_X * IMAGES_Y][(image.getWidth() / IMAGES_X)][(image.getHeight() / IMAGES_Y)];
                small_col2 = new byte[IMAGES_X * IMAGES_Y][(image.getWidth() / IMAGES_X)][(image.getHeight() / IMAGES_Y)];
                small_col3 = new byte[IMAGES_X * IMAGES_Y][(image.getWidth() / IMAGES_X)][(image.getHeight() / IMAGES_Y)];

                for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight() / 2; y++){

                        int imageNo = (x / IMAGES_X) + IMAGES_X * (y / IMAGES_Y);

                        int rgb = image.getRGB(x,y);
                        small_col1[imageNo][x][y] = (byte) ((rgb >> 16) & 0x000000FF);
                        small_col2[imageNo][x][y] = (byte) ((rgb >>8 ) & 0x000000FF);
                        small_col3[imageNo][x][y] = (byte) ((rgb) & 0x000000FF);

                        if(small_col1[imageNo][x][y] == 0 &&
                                small_col2[imageNo][x][y] == 0 &&
                                small_col3[imageNo][x][y] == 0){
                            small_col1[imageNo][x][y] = (byte)255;

                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();;
        }
        /*
        try{
            InputStream inputstream = rm.func_110536_a(large_image).func_110527_b();
            BufferedImage image = ImageIO.read(inputstream);

            if(image != null){

                large_col1 = new float[IMAGES_X * IMAGES_Y][(image.getWidth() / IMAGES_X)][(image.getHeight() / (IMAGES_Y * 2))];
                large_col2 = new float[IMAGES_X * IMAGES_Y][(image.getWidth() / IMAGES_X)][(image.getHeight() / (IMAGES_Y * 2))];
                large_col3 = new float[IMAGES_X * IMAGES_Y][(image.getWidth() / IMAGES_X)][(image.getHeight() / (IMAGES_Y * 2))];


                for(int x = 0; x < image.getWidth(); x++){
                    for(int y = 0; y < image.getHeight() / 2; y++){

                        int imageNo = (x / IMAGES_X) + IMAGES_X * (y / (2*IMAGES_Y));

                        int rgb = image.getRGB(x,y);
                        int red = (rgb >> 16) & 0x000000FF;
                        int green = (rgb >>8 ) & 0x000000FF;
                        int blue = (rgb) & 0x000000FF;

                        if(red == 0 && green == 0 && blue == 0){
                            large_col1[imageNo][x][y] = 0F;
                        }else{
                            large_col1[imageNo][x][y] = (float)red / (float)(red + green + blue);
                            large_col2[imageNo][x][y] = (float)red / (float)(red + green + blue);
                            large_col3[imageNo][x][y] = (float)red / (float)(red + green + blue);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();;
        }


    }
        */


}
