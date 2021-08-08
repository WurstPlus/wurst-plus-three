package me.travis.wurstplusthree.mixin.mixins.accessors;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Madmegsox1
 * @since 29/07/2021
 */
@Mixin(value = Minecraft.class)
public interface IMinecraft {
    @Accessor(value = "session")
    public Session getSession();

    @Accessor(value = "session")
    public void setSession(Session session);
}
