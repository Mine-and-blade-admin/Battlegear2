package mods.battlegear2.coremod.transformers;

import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public final class BattlegearAccessTransformer extends AccessTransformer {
    public BattlegearAccessTransformer() throws IOException {
        super("battlegear_at.cfg");
    }
}
