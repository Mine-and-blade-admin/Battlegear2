package mods.battlegear2.client.heraldry;

import mods.battlegear2.common.BattleGear;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public enum HeraldryIcon {
	Blank("blank"),
	//16* ASOIAF
	Wolf("wolf"), //Stark
	TripDragon("trip-dragon"), //Targaryen
	Lion("lion"), //Lannister
	Stag("stag"), //Baratheon
	Bird("bird"), //Aryn
	Flower("flower"), //Tyrell
	Kraken("kraken"), //Greyjoy
	Fish("fish"), // Tully
	Sun("sun"), //Martell
	Griffen("griffen"), //Connington
	Bear("bear"), //Mormont
	Mermaid("mermaid"), //Manderly
	Lightning("lightning"), //Dondarion
	Tower("tower"), //Frey
	FlayedMan("flay-man"), //Bolton
	Croc("croc"), //Reed
	
	//up to 14 of others
	BirdHead("birdhead"),
	Snake("snake"),
	;
	private String name;
	
	private HeraldryIcon(String name){
		this.name = name;
	}
	
	public String getForegroundImagePath(){
		return "%clamp%"+BattleGear.imageFolder+"items/heraldry/sigils/"+name+"-1.png";
	}
	
	public String getBackgroundImagePath(){
		return "%clamp%"+BattleGear.imageFolder+"items/heraldry/sigils/"+name+"-2.png";
	}
}
