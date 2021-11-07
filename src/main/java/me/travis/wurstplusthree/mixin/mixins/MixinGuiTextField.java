package me.travis.wurstplusthree.mixin.mixins;

import me.travis.wurstplusthree.WurstplusThree;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Madmegsox1
 * @since 07/11/2021
 */

@Mixin(GuiTextField.class)
public abstract class MixinGuiTextField extends Gui {

    @Shadow
    public abstract String getText();

    @Inject(method = "textboxKeyTyped", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiTextField;writeText(Ljava/lang/String;)V"))
    private void typed(char typedChar, int keyCode, CallbackInfoReturnable<Boolean> cir){
        String text = getText();
        if(text.startsWith(WurstplusThree.COMMANDS.getPrefix())) {
            text = text.replace(WurstplusThree.COMMANDS.getPrefix(), "");
            //WurstplusThree.COMMANDS.getNextArgument(text.split(" "));
        }
    }

    @Inject(method = "drawTextBox", at=@At(value = "RETURN"))
    private void renderBox(CallbackInfo ci){

    }
}
