package assets.battlegear2.api;

import assets.battlegear2.client.heraldry.HeraldyPattern;

public interface IHeraldyArmour extends IHeraldyItem{
	
	public String getBaseArmourPath(int armourSlot);
	public String getPatternArmourPath(HeraldyPattern pattern, int armourSlot);
}
