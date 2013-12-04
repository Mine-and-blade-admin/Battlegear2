package mods.battlegear2.api.heraldry;


import mods.battlegear2.heraldry.HeraldyPattern;

public interface IHeraldyArmour extends IHeraldryItem{
	
	public String getBaseArmourPath(int armourSlot);

    public String getPatternArmourPath(HeraldyPattern pattern, int armourSlot);
}
