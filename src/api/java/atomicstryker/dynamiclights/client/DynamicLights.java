package atomicstryker.dynamiclights.client;

/**
 * 
 * @author AtomicStryker
 * 
 * Rewritten and now-awesome Dynamic Lights Mod.
 * 
 * Instead of the crude base edits and inefficient giant loops of the original,
 * this Mod uses ASM transforming to hook into Minecraft with style and has an
 * API that does't suck. It also uses Forge events to register dropped Items.
 *
 */
//@Mod(modid = "DynamicLights", name = "Dynamic Lights", version = "1.3.5")
public class DynamicLights
{
    
    /**
     * Exposed method to register active Dynamic Light Sources with. Does all the necessary
     * checks, prints errors if any occur, creates new World entries in the worldLightsMap
     * @param lightToAdd IDynamicLightSource to register
     */
    public static void addLightSource(IDynamicLightSource lightToAdd)
    {

    }
    
    /**
     * Exposed method to remove active Dynamic Light sources with. If it fails for whatever reason,
     * it does so quietly.
     * @param lightToRemove IDynamicLightSource you want removed.
     */
    public static void removeLightSource(IDynamicLightSource lightToRemove)
    {

    }
}
