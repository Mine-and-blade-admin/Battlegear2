package mods.battlegear2.api;

/**
 * CANCELLED:
 * Utility to use one of the default Battlegear2 renderer
 * Implement in an item to get its renderer automatically registered
 * This is obviously client-side only
 */
public interface IDefaultRender {
    public enum RenderType{
        Bow,
        FlagPole,
        Quiver,
        Shield,
        Spear,
        HeraldryCrest,//Work in progress, not recommended for use
        HeraldryItem//Work in progress, not recommended for use
    }

    /**
     * Called before anything else
     * @return false to disable all features of this interface
     */
    public boolean useDefaultRenderer();

    /**
     * Called when registering the {@link IItemRenderer} for this item
     * @return the type of renderer to use
     */
    public RenderType getRenderer();

    /**
     * Called by Battlegear2 configuration GUI when the rendering option is toggled
     * Use this to react, using another renderer when this one is disabled for example
     * @param isEnabled the new rendering state: on true, the renderer is going to be used, on false, it is disabled
     */
    public void setRenderState(boolean isEnabled);
}
