package mods.battlegear2.client.heraldry;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;
import mods.battlegear2.api.IHeraldryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

public class HeradryWeponRenderer implements IItemRenderer{

	private Icon weaponBase;
	private Icon hilt;
	private Icon gem;
	
	private Minecraft mc;
	private RenderItem itemRenderer;
	
	public HeradryWeponRenderer(Icon weaponBase, Icon hilt, Icon gem){
		this.weaponBase = weaponBase;
		this.hilt = hilt;
		this.gem = gem;
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		if(item.hasTagCompound() && item.getTagCompound().hasKey("colour")){
			return (type == ItemRenderType.EQUIPPED || type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY);
		}else{
			return false;
		}
	}
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return (type == ItemRenderType.ENTITY && 
				(helper == ItemRendererHelper.ENTITY_BOBBING || 
				helper == ItemRendererHelper.ENTITY_ROTATION
				)
				);
	}
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		if(mc == null){
			mc = FMLClientHandler.instance().getClient();
			itemRenderer = new RenderItem();
		}
		
		
		if(type == ItemRenderType.EQUIPPED){
			drawEquippedHeraldryItem(item, data);
		}
		
		if(type == ItemRenderType.INVENTORY){
			drawInventoryHeraldryItem(item, data);
		}
		
		if(type == ItemRenderType.ENTITY){
			drawIEntityHeraldryItem(item, data);
		}
	}
	
	
	protected void drawIEntityHeraldryItem(ItemStack item, Object[] data) {
		/*this.mc.renderEngine.bindTexture("/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		
		int code = item.getTagCompound().getInteger("colour");
		
		*/
		EntityItem entiyItem = (EntityItem)data[1];
		GL11.glPushMatrix();
		
		
		if(RenderItem.renderInFrame){
			GL11.glRotatef(-90, 0, 1, 0);
			GL11.glTranslatef(-0.8F, -0.8F, 0);
			GL11.glScalef(1.7F, 1.7F, 1F);
		}else{
			GL11.glScalef(2, 2, 2);
			GL11.glTranslatef(-0.5F, 0, 0);
			
		}
		drawEquippedHeraldryItem(item, data);
		GL11.glPopMatrix();
	}

	protected void drawInventoryHeraldryItem(ItemStack item, Object[] data) {
		this.mc.renderEngine.bindTexture("/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		
		int code = item.getTagCompound().getInteger("colour");
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		itemRenderer.renderIcon(0, 0, weaponBase, 16, 16);
		
		GL11.glDepthFunc(GL11.GL_GREATER);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    itemRenderer.zLevel -= 50.0F;
	    
	    GL11.glPushMatrix();
	    
	    float[] colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getColour1(code)]);
	    GL11.glColor4f(colour[2], colour[1], colour[0], 1);
	    itemRenderer.renderIcon(0, 0, hilt, 16, 16);
	    
	    itemRenderer.zLevel -= 5.0F;
	    colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getColour2(code)]);
	    GL11.glColor4f(colour[2], colour[1], colour[0], 1);
	    itemRenderer.renderIcon(0, 0, gem, 16, 16);
	    itemRenderer.zLevel += 5.0F;
	    
	    //TODO Do enchantment rendering
	    
	    GL11.glPopMatrix();
	    
	    GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        itemRenderer.zLevel += 50.0F;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        
        
        GL11.glColor4f(1, 1, 1, 1);
		
	}
	
	public void drawEquippedHeraldryItem(ItemStack item, Object... data){
		this.mc.renderEngine.bindTexture("/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		
		int code = item.getTagCompound().getInteger("colour");
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		Icon icon = weaponBase;
		
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        float f4 = 0.0F;
        float f5 = 0.3F;

        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F);
        
        //GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        //GL11.glEnable(GL11.GL_BLEND);
	    //GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
        //GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        
        icon = hilt;
        float[] colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getColour1(code)]);
	    GL11.glColor4f(colour[2], colour[1], colour[0], 1);
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, 
        		icon.getMaxU(), 
        		icon.getMinV(), 
        		icon.getMinU(), 
        		icon.getMaxV(), 
        		icon.getSheetWidth(), 
        		icon.getSheetHeight(), 
        		0.0625F);        
        
        
        icon = gem;
        colour = SigilHelper.convertColourToARGBArray(SigilHelper.colours[SigilHelper.getColour2(code)]);
	    GL11.glColor4f(colour[2], colour[1], colour[0], 1);
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, 
        		icon.getMaxU(), 
        		icon.getMinV(), 
        		icon.getMinU(), 
        		icon.getMaxV(), 
        		icon.getSheetWidth(), 
        		icon.getSheetHeight(), 
        		0.0625F); 
        		
        
        GL11.glPopMatrix();
        //GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glColor4f(1, 1, 1, 1);
        

        //TODO: Enchantment rendering
	}
	

}
