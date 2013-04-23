package mods.battlegear2.client.heraldry;

import javax.swing.ImageIcon;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public enum HeraldryPattern {
	
	VERICAL_BLOCK("battlegear2:heraldry/patterns/pattern-0"),
	HORIZONTAL_BLOCK("battlegear2:heraldry/patterns/pattern-1"),
	QUARTERD("battlegear2:heraldry/patterns/pattern-2"),
	HORIZONTAL_MID("battlegear2:heraldry/patterns/pattern-3"),
	VERTICAL_MID("battlegear2:heraldry/patterns/pattern-4"),
	HORIZONTAL_BAR("battlegear2:heraldry/patterns/pattern-5"),
	VERTICAL_BAR("battlegear2:heraldry/patterns/pattern-6"),
	SMALL_CHECKERED("battlegear2:heraldry/patterns/pattern-7"),
	DIAG_DOWN("battlegear2:heraldry/patterns/pattern-8"),
	DIAG_UP("battlegear2:heraldry/patterns/pattern-9"),
	LOWER_TRIANGLE("battlegear2:heraldry/patterns/pattern-10"),
	UPPER_TRIANGLE("battlegear2:heraldry/patterns/pattern-11"),
	VERTICAL_TRIANGLES("battlegear2:heraldry/patterns/pattern-12"),
	HORIZONTAL_TRIANGLES("battlegear2:heraldry/patterns/pattern-13"),
	CROSS("battlegear2:heraldry/patterns/pattern-14"),
	DIAG_CROSS("battlegear2:heraldry/patterns/pattern-15");
	
	private Icon icon;
	private String name;
	
	private HeraldryPattern(String name){
		this.name = name;
	}
	
	public static void setAllIcon(IconRegister register){
		for (HeraldryPattern pattern : HeraldryPattern.values()) {
			pattern.setIcon(register.registerIcon(pattern.name));
		}
	}

	public Icon getIcon() {
		return icon;
	}
	
	private void setIcon(Icon icon){
		this.icon = icon;
	}
	
	
		

}
