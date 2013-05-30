package mods.battlegear2.client.heraldry;

import javax.swing.ImageIcon;

import mods.battlegear2.common.BattleGear;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public enum HeraldyPattern {
	
	VERICAL_BLOCK("pattern-0"),
	HORIZONTAL_BLOCK("pattern-1"),
	QUARTERD("pattern-2"),
	HORIZONTAL_MID("pattern-3"),
	VERTICAL_MID("pattern-4"),
	HORIZONTAL_BAR("pattern-5"),
	VERTICAL_BAR("pattern-6"),
	SMALL_CHECKERED("pattern-7"),
	DIAG_DOWN("pattern-8"),
	DIAG_UP("pattern-9"),
	LOWER_TRIANGLE("pattern-10"),
	UPPER_TRIANGLE("pattern-11"),
	VERTICAL_TRIANGLES("pattern-12"),
	UP_ARROW("pattern-13"),
	CROSS("pattern-14"),
	DIAG_CROSS("pattern-15");
	
	private Icon icon;
	private String name;
	
	private HeraldyPattern(String name){
		this.name = name;
	}
	
	public static void setAllIcon(IconRegister register){
		for (HeraldyPattern pattern : HeraldyPattern.values()) {
			pattern.setIcon(register.registerIcon("battlegear2:heraldry/patterns/"+pattern.name));
		}
	}
	
	public String getPath(){
		return BattleGear.imageFolder.concat("items/heraldry/patterns/"+name+".png");
	}

	public Icon getIcon() {
		return icon;
	}
	
	private void setIcon(Icon icon){
		this.icon = icon;
	}
}
