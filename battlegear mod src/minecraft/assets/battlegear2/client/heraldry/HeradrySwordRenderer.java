package assets.battlegear2.client.heraldry;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import assets.battlegear2.api.IHeraldyItem;
import assets.battlegear2.client.ClientProxy;
import assets.battlegear2.common.BattleGear;
import assets.battlegear2.common.heraldry.SigilHelper;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

public class HeradrySwordRenderer implements IItemRenderer{

	private Minecraft mc;
	private RenderItem itemRenderer;
	
	private final int swordType;
	
	
	public HeradrySwordRenderer(int swordType){
		this.swordType = swordType;
	}
	
	/*public HeradrySwordRenderer(Icon weaponBase, Icon hilt, Icon gem){
		this.weaponBase = weaponBase;
		this.hilt = hilt;
		this.gem = gem;
	}*/
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {

		if(item.hasTagCompound() && item.getTagCompound().hasKey("colour")){
			return (type == ItemRenderType.EQUIPPED || 
					type == ItemRenderType.EQUIPPED_FIRST_PERSON ||
					type == ItemRenderType.INVENTORY || 
					type == ItemRenderType.ENTITY);
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
		
		
		if(type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON){
			drawEquippedHeraldryItem(item, data);
		}
		
		if(type == ItemRenderType.INVENTORY){
			drawInventoryHeraldryItem(item, data);
		}
		
		if(type == ItemRenderType.ENTITY){
			drawIEntityHeraldryItem(item, data);
		}
	}
	
	private Icon getIcon(int type, int pass){
		return ((ClientProxy)BattleGear.proxy).swordIcons[type][pass];
	}
	
	protected void drawIEntityHeraldryItem(ItemStack item, Object[] data) {
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
		this.mc.renderEngine.func_110577_a(TextureMap.field_110576_c);//.bindTexture("/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		
		byte[] code = item.getTagCompound().getByteArray("hc2");
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		itemRenderer.renderIcon(0, 0, getIcon(swordType, 0), 16, 16);
	    GL11.glPushMatrix();
	    
	    float[] colour = SigilHelper.getPrimaryColourArray(code);
	    GL11.glColor3f(colour[0], colour[1], colour[2]);
	    itemRenderer.renderIcon(0, 0, getIcon(swordType, 1), 16, 16);
	    
	    colour = SigilHelper.getSecondaryColourArray(code);
	    GL11.glColor3f(colour[0], colour[1], colour[2]);
	    itemRenderer.renderIcon(0, 0, getIcon(swordType, 2), 16, 16);
	    
	    GL11.glPopMatrix();

        GL11.glColor4f(1, 1, 1, 1);
		
	}
	

	public void drawEquippedHeraldryItem(ItemStack item, Object... data){
		this.mc.renderEngine.func_110577_a(TextureMap.field_110576_c);//.bindTexture("/gui/items.png");
		Tessellator tessellator = Tessellator.instance;
		
		byte[] code = item.getTagCompound().getByteArray("hc2");
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		
		Icon icon = getIcon(swordType, 0);
		
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        float f4 = 0.0F;
        float f5 = 0.3F;

        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getOriginX(), icon.getOriginY(), 0.0625F);
        
        //GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        //GL11.glEnable(GL11.GL_BLEND);
	    //GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    
        //GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();
        
        icon = getIcon(swordType, 1);
        float[] colour = SigilHelper.getPrimaryColourArray(code);
        GL11.glColor3f(colour[0], colour[1], colour[2]);
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, 
        		icon.getMaxU(), 
        		icon.getMinV(), 
        		icon.getMinU(), 
        		icon.getMaxV(), 
        		icon.getOriginX(), 
        		icon.getOriginY(), 
        		0.0625F);        
        
        
        icon = getIcon(swordType, 2);
        colour = SigilHelper.getSecondaryColourArray(code);
        GL11.glColor3f(colour[0], colour[1], colour[2]);
        RenderManager.instance.itemRenderer.renderItemIn2D(tessellator, 
        		icon.getMaxU(), 
        		icon.getMinV(), 
        		icon.getMinU(), 
        		icon.getMaxV(), 
        		icon.getOriginX(), 
        		icon.getOriginY(), 
        		0.0625F); 
        		
        
        GL11.glPopMatrix();
        
        GL11.glColor4f(1, 1, 1, 1);
        
        if (item != null && item.hasEffect(0)){
        	HeraldryItemRenderer.renderEnchantmentEffects(tessellator);
        }
        
        //GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        
        

	    
        //TODO: Enchantment rendering
	}
	

}
