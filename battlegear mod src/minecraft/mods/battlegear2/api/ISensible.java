package mods.battlegear2.api;

public interface ISensible {
    /**
     * @return true if first arg is different from second arg
     */
    public boolean diffWith(StackHolder holder1, StackHolder holder2);
}
