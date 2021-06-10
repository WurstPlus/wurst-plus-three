package me.travis.wurstplusthree.gui.chat;

import com.google.common.collect.Lists;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.chat.CustomChat;
import me.travis.wurstplusthree.hack.player.PlayerSpoofer;
import me.travis.wurstplusthree.util.AnimationUtil;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.Rainbow;
import me.travis.wurstplusthree.util.elements.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * @author Madmegsox1
 * @since 27/04/2021
 */
@SideOnly(Side.CLIENT)
public class GuiChat extends GuiNewChat implements Globals {
    private static final Logger LOGGER = WurstplusThree.LOGGER;
    private final Minecraft mc;
    private final Timer messageTimer = new Timer();

    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();

    private int scrollPos;
    private boolean isScrolled;
    public static float percentComplete = 0.0F;
    public static int newLines;
    public static long prevMillis = -1L;
    public static int messageAdd;
    public boolean configuring;

    public GuiChat(Minecraft mcIn) {
        super(mcIn);
        mc = mcIn;
    }

    public static void updatePercentage(long diff) {
        if (percentComplete < 1.0F) percentComplete += 0.004F * (float) diff;
        percentComplete = AnimationUtil.clamp(percentComplete, 0.0F, 1.0F);
    }

    public void drawChat(int updateCounter) {

        if (this.configuring)
            return;
        if (prevMillis == -1L) {
            prevMillis = System.currentTimeMillis();
            return;
        }
        long current = System.currentTimeMillis();
        long diff = current - prevMillis;
        prevMillis = current;
        updatePercentage(diff);
        float t = percentComplete;
        float percent = 1.0F - --t * t * t * t;
        percent = AnimationUtil.clamp(percent, 0.0F, 1.0F);
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = getLineCount();
            int j = this.drawnChatLines.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            if (j > 0) {
                boolean flag = false;
                if (getChatOpen()) {
                    flag = true;
                }
                float f1 = getChatScale();
                GlStateManager.pushMatrix();
                if ((CustomChat.INSTANCE.smoothChat.getValue()) && CustomChat.INSTANCE.type.is("Horizontal") && !this.isScrolled) {
                    GlStateManager.translate(2.0F +  CustomChat.INSTANCE.xOffset.getValue().floatValue(), 8.0F + CustomChat.INSTANCE.yOffset.getValue().floatValue() + (9.0F - 9.0F * percent) * f1, 0.0F);
                } else {
                    GlStateManager.translate(2.0F + CustomChat.INSTANCE.xOffset.getValue().floatValue(), 8.0F +  CustomChat.INSTANCE.yOffset.getValue().floatValue(), 0.0F);
                }
                GlStateManager.scale(f1, f1, 1.0F);
                int l = 0;
                for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; i1++) {
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatline != null) {
                        int j1 = updateCounter - chatline.getUpdatedCounter();
                        if (j1 < 200 || flag) {
                            double d0 = j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 *= 10.0D;
                            d0 = MathHelper.clamp(d0, 0.0D, 1.0D);
                            d0 *= d0;
                            int l1 = (int) (255.0D * d0);
                            if (flag) {
                                l1 = 255;
                            }
                            l1 = (int) (l1 * f);
                            l++;
                            if (l1 > 3) {
                                int i2 = 0;
                                int j2 = -i1 * 9;
                                String s = chatline.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                if (CustomChat.INSTANCE.smoothChat.getValue() && i1 <= newLines) {
                                    if (this.messageTimer.passedMs(CustomChat.INSTANCE.vSpeed.getValue().intValue()) && messageAdd < 0) {
                                        messageAdd += CustomChat.INSTANCE.vIncrements.getValue().intValue();
                                        if (messageAdd > 0) messageAdd = 0;
                                        this.messageTimer.reset();
                                    }
                                    if (CustomChat.INSTANCE.rainbow.getValue()){
                                        if (CustomChat.INSTANCE.customFont.getValue()) {
                                            WurstplusThree.GUI_FONT_MANAGER.drawStringRainbow(s, 0.0f + (CustomChat.INSTANCE.type.is("Vertical") ? messageAdd : 0), (j2 - 8), true);
                                        } else {
                                            this.mc.fontRenderer.drawStringWithShadow(s, 0.0F + (CustomChat.INSTANCE.type.is("Vertical") ? messageAdd : 0), (j2 - 8), Rainbow.getColour().getRGB());
                                        }
                                    } else {
                                        if (CustomChat.INSTANCE.customFont.getValue()) {
                                            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(s, 0.0f + (CustomChat.INSTANCE.type.is("Vertical") ? messageAdd : 0), (j2 - 8), 16777215 + ((l1) << 24));
                                        } else {
                                            this.mc.fontRenderer.drawStringWithShadow(s, 0.0F + (CustomChat.INSTANCE.type.is("Vertical") ? messageAdd : 0), (j2 - 8), 16777215 + ((l1) << 24));
                                        }
                                    }
                                } else {
                                    if (CustomChat.INSTANCE.rainbow.getValue()){
                                        if (CustomChat.INSTANCE.customFont.getValue()) {
                                            WurstplusThree.GUI_FONT_MANAGER.drawStringRainbow(s, 0.0f, (j2 - 8), true);
                                        } else {
                                            this.mc.fontRenderer.drawStringWithShadow(s, 0.0f, (j2 - 8), Rainbow.getColour().getRGB());
                                        }
                                    } else {
                                        if (CustomChat.INSTANCE.customFont.getValue()) {
                                            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(s, i2, (j2 - 8), 16777215 + (l1 << 24));
                                        } else {
                                            this.mc.fontRenderer.drawStringWithShadow(s, i2, (j2 - 8), 16777215 + (l1 << 24));
                                        }
                                    }
                                }
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }
                if (flag) {
                    int k2 = this.mc.fontRenderer.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int l2 = j * k2 + j;
                    int i3 = l * k2 + l;
                    int j3 = this.scrollPos * i3 / j;
                    int k1 = i3 * i3 / l2;
                    if (l2 != i3) {
                        int k3 = (j3 > 0) ? 170 : 96;
                        int l3 = this.isScrolled ? 13382451 : 3355562;
                        Gui.drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                        Gui.drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public void printChatMessage(ITextComponent chatComponent) {
        if (mc.player != null) {
            if (PlayerSpoofer.INSTANCE.isEnabled() && PlayerSpoofer.INSTANCE.name != null) {
                String toChange = chatComponent.getFormattedText();
                toChange = toChange.replace(PlayerSpoofer.INSTANCE.getOldName(), PlayerSpoofer.INSTANCE.name);
                ITextComponent toSend = new TextComponentString(toChange);
                printChatMessageWithOptionalDeletion(toSend, 0);
            }
            else {
                printChatMessageWithOptionalDeletion(chatComponent, 0);
            }
        }
        else {
            printChatMessageWithOptionalDeletion(chatComponent, 0);
        }
    }

    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId) {
        percentComplete = 0.0F;
        setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT] {}", chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        messageAdd = -CustomChat.INSTANCE.vLength.getValue().intValue();
        if (chatLineId != 0) {
            deleteChatLine(chatLineId);
        }
        int i = MathHelper.floor(getChatWidth() / getChatScale());
        List<ITextComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, this.mc.fontRenderer, false, false);
        boolean flag = getChatOpen();
        newLines = list.size() - 1;
        for (ITextComponent itextcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                scroll(1);
            }
            this.drawnChatLines.add(0, new ChatLine(updateCounter, itextcomponent, chatLineId));
        }
        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }
        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
        resetScroll();
        for (int i = this.chatLines.size() - 1; i >= 0; i--) {
            ChatLine chatline = this.chatLines.get(i);
            setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    public void addToSentMessages(String message) {
        if (this.sentMessages.isEmpty() || !((String) this.sentMessages.get(this.sentMessages.size() - 1)).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void scroll(int amount) {
        this.scrollPos += amount;
        int i = this.drawnChatLines.size();
        if (this.scrollPos > i - getLineCount()) {
            this.scrollPos = i - getLineCount();
        }
        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    @Nullable
    public ITextComponent getChatComponent(int mouseX, int mouseY) {
        if (!getChatOpen()) {
            return null;
        }
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i = scaledresolution.getScaleFactor();
        float f = getChatScale();
        int j = mouseX / i - 2 - (CustomChat.INSTANCE.xOffset.getValue()).intValue();
        int k = mouseY / i - 40 + (CustomChat.INSTANCE.yOffset.getValue()).intValue();
        j = MathHelper.floor(j / f);
        k = MathHelper.floor(k / f);
        if (j >= 0 && k >= 0) {
            int l = Math.min(getLineCount(), this.drawnChatLines.size());
            if (j <= MathHelper.floor(getChatWidth() / getChatScale()) && k < this.mc.fontRenderer.FONT_HEIGHT * l + l) {
                int i1 = k / this.mc.fontRenderer.FONT_HEIGHT + this.scrollPos;
                if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                    ChatLine chatline = this.drawnChatLines.get(i1);
                    int j1 = 0;
                    for (ITextComponent itextcomponent : chatline.getChatComponent()) {
                        if (itextcomponent instanceof TextComponentString) {
                            j1 += this.mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString) itextcomponent).getText(), false));
                            if (j1 > j) {
                                return itextcomponent;
                            }
                        }
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof net.minecraft.client.gui.GuiChat;
    }

    public void deleteChatLine(int id) {
        Iterator<ChatLine> iterator = this.drawnChatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline = iterator.next();
            if (chatline.getChatLineID() == id) {
                iterator.remove();
            }
        }
        iterator = this.chatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline1 = iterator.next();
            if (chatline1.getChatLineID() == id) {
                iterator.remove();
                break;
            }
        }
    }

    public int getChatWidth() {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }


    public int getChatHeight() {
        return calculateChatboxHeight(getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }


    public static int calculateChatboxWidth(float scale) {
        return MathHelper.floor(scale * 280.0F + 40.0F);
    }


    public static int calculateChatboxHeight(float scale) {
        return MathHelper.floor(scale * 160.0F + 20.0F);
    }

    public int getLineCount() {
        return getChatHeight() / 9;
    }

}
