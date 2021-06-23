package me.travis.wurstplusthree.hack.hacks.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render2DEvent;
import me.travis.wurstplusthree.event.events.TestEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.util.HudUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Hack.Registration(name = "HUD", description = "hud elements", category = Hack.Category.CLIENT, enabled = true)
public class Hud extends Hack {

    BooleanSetting welcomer = new BooleanSetting("Welcomer", false, this);//

    BooleanSetting watermark = new BooleanSetting("Watermark", true, this);//
    BooleanSetting fps = new BooleanSetting("Fps", false, this);//
    BooleanSetting tps = new BooleanSetting("Tps", false, this);//
    BooleanSetting ping = new BooleanSetting("Ping", false, this);//
    BooleanSetting clock = new BooleanSetting("Clock", true, this);//
    BooleanSetting arrayList = new BooleanSetting("ArrayList", true, this);//
    BooleanSetting helper = new BooleanSetting("Helper", true, this);
    BooleanSetting lagNot = new BooleanSetting("Lag Notification", true, this);//
    BooleanSetting coords = new BooleanSetting("Coords", true, this);//
    BooleanSetting friends = new BooleanSetting("Friends", true, this);//

    BooleanSetting armour = new BooleanSetting("Armour", false, this);//

    ColourSetting fontColour = new ColourSetting("Font", new Colour(255, 255, 255, 200), this);
    ColourSetting outlineColour = new ColourSetting("Outline", new Colour(0, 0, 0, 255), this);

    BooleanSetting customFont = new BooleanSetting("Custom Font", true, this);

    private ScaledResolution scaledResolution;

    public boolean debugHud;
    public static Hud INSTANCE;
    private Runtime runtime;

    public Hud(){
        INSTANCE = this;
        debugHud = false;
        runtime = Runtime.getRuntime();
    }

    private void runEvents(){
        long start = System.currentTimeMillis();
        while (true){
            if(start > System.currentTimeMillis() - 100){
                TestEvent testEvent = new TestEvent();
                WurstplusThree.EVENT_PROCESSOR.addEventListener(this);
                WurstplusThree.EVENT_PROCESSOR.postEvent(testEvent);
                WurstplusThree.EVENT_PROCESSOR.removeEventListener(this);
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        scaledResolution = new ScaledResolution(mc);

        if(debugHud){
            int y = 284;
            long maxMemory = runtime.maxMemory() /1024/1024;
            long allocatedMemory = runtime.totalMemory()/1024/1024;
            long freeMemory = runtime.freeMemory()/1024/1024;
            drawString("MAX MEMORY: " + ChatFormatting.AQUA + maxMemory + " Mb", 10, y);
            y+=12;
            drawString("ALLOCATED MEMORY: " + ChatFormatting.AQUA + allocatedMemory + " Mb", 10, y);
            y+= 12;
            drawString("FREE MEMORY: " + ChatFormatting.AQUA + freeMemory + " Mb", 10, y);
            y+= 12;
            drawString("TOTAL FREE MEMORY: " + ChatFormatting.AQUA + (freeMemory + (maxMemory - allocatedMemory)) + " Mb", 10, y);
        }

        if (welcomer.getValue()) {
            String line = HudUtil.getWelcomerLine();
            if (clock.getValue()) {
                String clock = HudUtil.getAnaTimeLine() + " | " + HudUtil.getDate();
                this.drawStringCenterX(clock, 8);
            }
            this.drawStringCenterX(line, 26);
        }

        if (this.lagNot.getValue() && WurstplusThree.SERVER_MANAGER.isServerNotResponding()) {
            this.drawStringCenterX(ChatFormatting.RED + "Server is not responding " + Math.round(WurstplusThree.SERVER_MANAGER.serverRespondingTime() / 1000.0f), 56);
        }

        this.doTopleft();
        // this.doTopRight();
        this.doBottomRight();
        this.doHelper();
        this.doBottomLeft();

        if (this.armour.getValue()) {
            this.renderArmorHUD(true);
        }

        if (this.friends.getValue()) {
            this.renderFriends();
        }

    }

    private void doBottomRight() {
        if (this.arrayList.getValue()) {
            List<Hack> hacks = WurstplusThree.HACKS.getSortedHacks(false, this.customFont.getValue());
            int y = scaledResolution.getScaledHeight() - (11 * hacks.size()) + 2 + (11 * WurstplusThree.HACKS.getDrawnHacks().size());
            for (Hack hack : hacks) {
                if (WurstplusThree.HACKS.isDrawHack(hack)) continue;
                String name = hack.getFullArrayString();
                drawString(name, this.getRightX(name, 2), y);
                y += 11;
            }
        }
    }

    private void doHelper() {
        if (!this.helper.getValue()) return;
        int y = 200;
        String ca = (WurstplusThree.HACKS.ishackEnabled("Crystal Aura") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        String holefill = (WurstplusThree.HACKS.ishackEnabled("Hole Fill") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        String trap = (WurstplusThree.HACKS.ishackEnabled("Trap") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        String surround = (WurstplusThree.HACKS.ishackEnabled("Surround") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        String ka = (WurstplusThree.HACKS.ishackEnabled("Kill Aura") ? ChatFormatting.GREEN + "1" : ChatFormatting.RED + "0") + ChatFormatting.RESET;
        this.drawString("Totems " + HudUtil.getTotems(), 10, y);
        y += 12;
        this.drawString("CAura " + ca, 10, y);
        y += 12;
        this.drawString("HoleFill " + holefill, 10, y);
        y += 12;
        this.drawString("Trap " + trap, 10, y);
        y += 12;
        this.drawString("Surround " + surround, 10, y);
        y += 12;
        this.drawString("KAura " + ka, 10, y);
    }

    private void renderFriends() {
        List<String> friends = new ArrayList<>();
        for (EntityPlayer player : mc.world.playerEntities) {
            if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) {
                friends.add(player.getName());
            }
        }
        int y = 72;
        if (friends.isEmpty()) {
            drawString(ChatFormatting.BOLD + "U got no friends", 10, y);
        } else {
            drawString(ChatFormatting.BOLD + "the_fellas", 10, y);
            y += 12;
            for (String friend : friends) {
                drawString(friend, 10, y);
                y += 12;
            }
        }
    }

    private void doTopleft() {
        int y = 10;
        if (watermark.getValue()) {
            drawString(ChatFormatting.GOLD + WurstplusThree.MODNAME + ChatFormatting.RESET
                    + " v" + WurstplusThree.MODVER, 10, y);
            y += 16;
        }
        if (fps.getValue()) {
            drawString("Fps " + HudUtil.getFpsLine(), 10, y);
            y += 12;
        }
        if (tps.getValue()) {
            drawString("Tps " + HudUtil.getTpsLine(), 10, y);
            y += 12;
        }
        if (ping.getValue()) {
            drawString("Ping " + HudUtil.getPingLine(), 10, y);
            y += 12;
        }
    }

    private void doBottomLeft() {
        if (!this.coords.getValue()) return;
        String x = "[" + (int) (mc.player.posX) + "]";
        String y = "[" + (int) (mc.player.posY) + "]";
        String z = "[" + (int) (mc.player.posZ) + "]";

        String x_nether = "[" + Math.round(mc.player.dimension != -1 ? (mc.player.posX / 8) : (mc.player.posX * 8)) + "]";
        String z_nether = "[" + Math.round(mc.player.dimension != -1 ? (mc.player.posZ / 8) : (mc.player.posZ * 8)) + "]";

        String line = "XYZ " + x + y + z + " XZ " + x_nether + z_nether;

        drawString(line, 7, scaledResolution.getScaledHeight() - 11);
    }



    /*
    private void doTopRight() {
        int y = 10;
    }

    Does this have any point? -A2H
    */


    private int getRightX(String string, int x) {
        if (this.customFont.getValue()) {
            return scaledResolution.getScaledWidth() - x - WurstplusThree.GUI_FONT_MANAGER.getTextWidth(string);
        } else {
            return scaledResolution.getScaledWidth() - x - mc.fontRenderer.getStringWidth(string);
        }
    }

    private void drawString(String string, int x, int y) {
        if (this.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(string, x, y, fontColour.getValue().getRGB());
        } else {
            mc.fontRenderer.drawStringWithShadow(string, x, y, fontColour.getValue().getRGB());
        }
    }

    private void drawString(String string, int x, int y, int colour) {
        if (this.customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(string, x, y, colour);
        } else {
            mc.fontRenderer.drawStringWithShadow(string, x, y, colour);
        }
    }

    private int getStringWidth(String string) {
        if (this.customFont.getValue()) {
            return WurstplusThree.GUI_FONT_MANAGER.getTextWidth(string);
        } else {
            return mc.fontRenderer.getStringWidth(string);
        }
    }

    private void drawStringCenterX(String string, int y) {
        if (this.customFont.getValue()) {
            int x = (scaledResolution.getScaledWidth() / 2) - (WurstplusThree.GUI_FONT_MANAGER.getTextWidth(string) / 2);
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(string, x, y, fontColour.getValue().getRGB());
        } else {
            int x = (scaledResolution.getScaledWidth() / 2) - (mc.fontRenderer.getStringWidth(string) / 2);
            mc.fontRenderer.drawStringWithShadow(string, x, y, fontColour.getValue().getRGB());
        }
    }

    public void renderArmorHUD(final boolean percent) {
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();
        GlStateManager.enableTexture2D();
        final int i = width / 2;
        int iteration = 0;
        final int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        for (final ItemStack is : mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = 200.0f;
            mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            mc.getRenderItem().zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            this.drawString(s, x + 19 - 2 - this.getStringWidth(s), y + 7, 1677215);
            if (!percent) {
                continue;
            }
            int dmg = 0;
            float green = (is.getMaxDamage() - (float) is.getItemDamage()) / is.getMaxDamage();
            float red = 1.0f - green;
            dmg = 100 - (int) (red * 100.0f);
            if (red > 1f) red = 1f;
            if (green > 1f) green = 1f;
            if (red < 0f) red = 0f;
            if (green < 0f) green = 0f;
            this.drawString(dmg+"", x + 8 - this.getStringWidth(dmg+"") / 2, y - 9, (new Color(red, green, 0)).getRGB());
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

}
