package mods.battlegear2.client.heraldry;

import mods.battlegear2.common.BattleGear;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public enum HeraldryIcon {
	Blank("blank"), //Yes (need gimp file)
	//16* ASOIAF
	Wolf("wolf"), //Stark Yes
	TripDragon("trip-dragon"), //Targaryen Yes
	Lion("lion"), //Lannister Yes
	Stag("stag"), //Baratheon Yes (need gimp file)
	Bird("bird"), //Aryn Yes
	Flower("flower"), //Tyrell
	Kraken("kraken"), //Greyjoy
	Fish("fish"), // Tully yes
	Sun("sun"), //Martell
	Griffen("griffen"), //Connington
	Bear("bear"), //Mormont
	Mermaid("mermaid"), //Manderly
	Lightning("lightning"), //Dondarion Yes
	Tower("tower"), //Frey yes
	FlayedMan("flay-man"), //Bolton
	Croc("croc"), //Reed Yes
	
	//up to 14 of others
	BirdHead("birdhead"),// Yes
	//Snake("snake"),
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
