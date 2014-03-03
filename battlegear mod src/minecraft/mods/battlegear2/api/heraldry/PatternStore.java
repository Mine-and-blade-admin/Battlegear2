package mods.battlegear2.api.heraldry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PatternStore {

    public static final IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
    public static final PatternStore DEFAULT = new PatternStore(8,4);
    private final int IMAGES_X;
    private final int IMAGES_Y;
    public List<int[][][][]> patterns = new ArrayList<int[][][][]>();

    public PatternStore(int xSections,int ySections){
        this.IMAGES_X = xSections;
        this.IMAGES_Y = ySections;
    }
    /**
     * Deconstruct an image and store its data for later use
     * @param image
     * @return the index used to get the data back
     */
    public int buildPatternAndStore(ResourceLocation image){
        try{
            if(patterns.add(buildPatternFrom(image))){
                return patterns.size()-1;
            }else{
                return -1;
            }
        }catch (IOException io){
            return -1;
        }
    }

    /**
     * See {@link #buildPatternFrom(java.awt.image.BufferedImage)}
     * @throws IOException if the image can't be read
     */
    public int[][][][] buildPatternFrom(ResourceLocation image) throws IOException {
        return buildPatternFrom(rm.getResource(image).getInputStream());
    }

    /**
     * See {@link #buildPatternFrom(java.awt.image.BufferedImage)}
     * @throws IOException if the image can't be read
     */
    public int[][][][] buildPatternFrom(InputStream resourceStream) throws IOException {
        return buildPatternFrom(ImageIO.read(resourceStream));
    }

    /**
     * Analyse the given image by cutting it into subimages
     * @param image
     * @return the subimages rgb values into arrays
     */
    public int[][][][] buildPatternFrom(BufferedImage image){
        int[][][][] rgbs = new int[IMAGES_X * IMAGES_Y][3][(image.getWidth() / IMAGES_X)][(image.getHeight() / IMAGES_Y)];

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
                    rgbs[imageNo][0][x%imageRes][y%imageRes] = 255;
                    rgbs[imageNo][1][x%imageRes][y%imageRes] = 0;
                    rgbs[imageNo][2][x%imageRes][y%imageRes] = 0;
                }else{
                    rgbs[imageNo][0][x%imageRes][y%imageRes] = (255 * red) / total;
                    rgbs[imageNo][1][x%imageRes][y%imageRes] = (255 * green) / total;
                    rgbs[imageNo][2][x%imageRes][y%imageRes] = (255 * blue) / total;
                }
            }
        }
        return rgbs;
    }

    public int getBlendedSmallPixel(int index, byte imageNo, int x, int y, int col1, int col2, int col3){
        return getBlendedSmallPixel(patterns.get(index)[imageNo][0][x][y], patterns.get(index)[imageNo][1][x][y], patterns.get(index)[imageNo][2][x][y], col1, col2, col3);
    }

    public static int getBlendedSmallPixel(int[][][][] rgbs, byte imageNo, int x, int y, int col1, int col2, int col3){
        return getBlendedSmallPixel(rgbs[imageNo][0][x][y], rgbs[imageNo][1][x][y], rgbs[imageNo][2][x][y], col1, col2, col3);
    }

    public static int getBlendedSmallPixel(int a, int b, int c, int col1, int col2, int col3){
        int red = ((((col1 >> 16) & 0xFF) * a) / 255) +
                ((((col2 >> 16) & 0xFF) * b) / 255) +
                ((((col3 >> 16) & 0xFF) * c)/ 255);

        int green = (((col1 >> 8) & 0xFF) * a / 255) +
                (((col2 >> 8) & 0xFF) * b / 255) +
                (((col3 >> 8) & 0xFF) * c / 255);

        int blue = (((col1) & 0xFF) * a / 255) +
                (((col2) & 0xFF) * b / 255) +
                (((col3) & 0xFF) * c / 255);

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
