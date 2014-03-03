package mods.battlegear2.api.heraldry;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class HeraldryPattern {

    public static List<HeraldryPattern> patterns = new ArrayList<HeraldryPattern>();
    public static final HeraldryPattern VERTICAL_BLOCK = new HeraldryPattern("battlegear2","patterns/pattern-0");
    public static final HeraldryPattern HORIZONTAL_BLOCK= new HeraldryPattern("battlegear2","patterns/pattern-1");
    public static final HeraldryPattern QUARTERD= new HeraldryPattern("battlegear2","patterns/pattern-2");
    public static final HeraldryPattern HORIZONTAL_MID= new HeraldryPattern("battlegear2","patterns/pattern-3");
    public static final HeraldryPattern VERTICAL_MID= new HeraldryPattern("battlegear2","patterns/pattern-4");
    public static final HeraldryPattern HORIZONTAL_BAR= new HeraldryPattern("battlegear2","patterns/pattern-5");
    public static final HeraldryPattern VERTICAL_BAR= new HeraldryPattern("battlegear2","patterns/pattern-6");
    public static final HeraldryPattern SMALL_CHECKERED= new HeraldryPattern("battlegear2","patterns/pattern-7");
    public static final HeraldryPattern DIAG_DOWN= new HeraldryPattern("battlegear2","patterns/pattern-8");
    public static final HeraldryPattern DIAG_UP= new HeraldryPattern("battlegear2","patterns/pattern-9");
    public static final HeraldryPattern LOWER_TRIANGLE= new HeraldryPattern("battlegear2","patterns/pattern-10");
    public static final HeraldryPattern UPPER_TRIANGLE= new HeraldryPattern("battlegear2","patterns/pattern-11");
    public static final HeraldryPattern VERTICAL_TRIANGLES= new HeraldryPattern("battlegear2","patterns/pattern-12");
    public static final HeraldryPattern UP_ARROW= new HeraldryPattern("battlegear2","patterns/pattern-13");
    public static final HeraldryPattern CROSS= new HeraldryPattern("battlegear2","patterns/pattern-14");
    public static final HeraldryPattern DIAG_CROSS= new HeraldryPattern("battlegear2","patterns/pattern-15");

	private IIcon icon;
	public final String name;
    public final String mod;
	
	public HeraldryPattern(String modid, String name){
        this.mod = modid;
		this.name = name;
        patterns.add(this);
	}
	
	public String getPath(){
		return "assets/"+mod+"/textures/items/heraldry/"+name+".png";
	}

	public IIcon getIcon() {
		return icon;
	}

	public void registerIcon(TextureMap map){
		this.icon = map.registerIcon(mod+":heraldry/"+name);
	}
}
