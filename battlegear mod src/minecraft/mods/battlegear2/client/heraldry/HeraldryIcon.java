package mods.battlegear2.client.heraldry;

import mods.battlegear2.common.BattleGear;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public enum HeraldryIcon {
	Blank("blank"),
	Targ("targ"),
	Lannister("lann");
	
	private String name;
	
	private HeraldryIcon(String name){
		this.name = name;
	}
	
	public String getForegroundImagePath(){
		return BattleGear.imageFolder+"items/heraldry/sigils/"+name+"-1.png";
	}
	
	public String getBackgroundImagePath(){
		return BattleGear.imageFolder+"items/heraldry/sigils/"+name+"-2.png";
	}
}
