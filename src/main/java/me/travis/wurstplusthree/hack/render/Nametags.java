package me.travis.wurstplusthree.hack.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.type.BooleanSetting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.DoubleSetting;
import me.travis.wurstplusthree.setting.type.IntSetting;
import me.travis.wurstplusthree.util.RenderUtil;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.NametagRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@Hack.Registration(name = "Nametags", description = "makes name above player", category = Hack.Category.RENDER, isListening = false)
public class Nametags extends Hack {

    public static Nametags INSTANCE;

    public Nametags() {
        INSTANCE = this;
    }

    public BooleanSetting customFont = new BooleanSetting("Custom Font", true, this);
    public BooleanSetting simple = new BooleanSetting("Simplfy", false, this);
    public BooleanSetting gameMode = new BooleanSetting("Gamemode", false, this);
    public BooleanSetting armour = new BooleanSetting("Armour", true, this);
    public BooleanSetting durability = new BooleanSetting("Durability", true, this);
    public BooleanSetting popCounter = new BooleanSetting("Totem Pops", true, this);
    public BooleanSetting invisibles = new BooleanSetting("Invisibles", false, this);
    public IntSetting distance = new IntSetting("Distance", 250, 0, 500, this);
    public IntSetting arrowPos = new IntSetting("Arrow Pos", 28, 0, 50, this);
    public DoubleSetting scale = new DoubleSetting("Scale", 0.05, 0.01, 0.1, this);
    public DoubleSetting height = new DoubleSetting("Height", 2.5, 0.5, 5.0, this);
    public IntSetting textOffset = new IntSetting("TextOffset", 0, -5, 5, this, s -> customFont.getValue());

    public BooleanSetting outline = new BooleanSetting("Outline", true, this);
    public DoubleSetting outlineWidth = new DoubleSetting("Width", 1.5, 0.1, 3.0, this);
    public ColourSetting fontColour = new ColourSetting("Font Colour", new Colour(255, 120, 0, 200), this);
    public ColourSetting outlineColour = new ColourSetting("Outline Colour", new Colour(255, 80, 0, 150), this);
    public ColourSetting outlineColourFriend = new ColourSetting("Friend Colour", new Colour(20, 20, 255, 150), this);
    public ColourSetting outlineColourEnemy = new ColourSetting("Enemy Colour", new Colour(255, 20, 20, 150), this);

    private final NametagRenderer nametagRenderer = new NametagRenderer();
    private final ICamera camera = new Frustum();
    private boolean shownItem;

    @Override
    public void onRender3D(Render3DEvent event) {
        if (nullCheck()) return;

        double cx = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double)event.getPartialTicks();
        double cy = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double)event.getPartialTicks();
        double cz = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double)event.getPartialTicks();

        camera.setPosition(cx, cy, cz);

        try {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (!player.isEntityAlive() || player == mc.getRenderViewEntity() ||
                        player.getDistance(mc.player) > this.distance.getValue() ||
                        !camera.isBoundingBoxInFrustum(player.getEntityBoundingBox())) continue;
                NetworkPlayerInfo npi = mc.player.connection.getPlayerInfo(player.getGameProfile().getId());
                double pX = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks
                        - mc.renderManager.renderPosX;
                double pY = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks
                        - mc.renderManager.renderPosY;
                double pZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks
                        - mc.renderManager.renderPosZ;
                if (this.getShortGamemode(npi.getGameType().getName()).equalsIgnoreCase("SP") && !invisibles.getValue()) continue;
                if (!player.getName().startsWith("Body #")) {
                    renderNametag(player, pX, pY, pZ);
                }
            }
        } catch (Exception e) {
        }

        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
    }

    public void renderNametag(EntityPlayer player, double x, double y, double z) {
        shownItem = false;
        GlStateManager.pushMatrix();
        FontRenderer var13 = mc.fontRenderer;
        NetworkPlayerInfo npi = mc.player.connection.getPlayerInfo(player.getGameProfile().getId());
        boolean isFriend = WurstplusThree.FRIEND_MANAGER.isFriend(player.getName());
        boolean isEnemy = WurstplusThree.ENEMY_MANAGER.isEnemy(player.getName());
        String name = ((isFriend || isEnemy) && player.isSneaking() ? SECTIONSIGN + "9" : SECTIONSIGN + "r")
                + (isFriend ? ChatFormatting.AQUA :
                isEnemy ? ChatFormatting.RED : "")
                + player.getName() + ChatFormatting.RESET
                + (gameMode.getValue() ? " [" + getShortGamemode(npi.getGameType().getName()) + "]" : "")
                + " " + SECTIONSIGN + getPing(npi.getResponseTime()) + npi.getResponseTime() + "ms"
                + " " + SECTIONSIGN + getHealth(player.getHealth() + player.getAbsorptionAmount())
                + MathHelper.ceil(player.getHealth() + player.getAbsorptionAmount())
                + (popCounter.getValue() ? " " + SECTIONSIGN + this.getPop(WurstplusThree.POP_MANAGER.getTotemPops(player))
                + SECTIONSIGN + "r" : "");
        name = name.replace(".0", "");
        float distance = mc.player.getDistance(player);
        float var15 = (float) (((distance / 5 <= 2 ? 2.0F : (distance / 5) * ((scale.getValue() * 10) + 1)) * 2.5f) * (scale.getValue() / 10));
        boolean far = distance / 5 > 2;
        GL11.glTranslated((float) x, (float) y + height.getValue() - (player.isSneaking() ? 0.4 : 0) + (far ? distance / 12 - 0.7 : 0.25), (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-mc.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(mc.renderManager.playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, (float)0);
        GL11.glScalef(-var15, -var15, var15);
        nametagRenderer.disableGlCap(GL_LIGHTING, GL_DEPTH_TEST);
        nametagRenderer.enableGlCap(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        int width;
        if (customFont.getValue()) {
            width = WurstplusThree.GUI_FONT_MANAGER.getTextWidth(name) / 2 + 1;
        } else {
            width = var13.getStringWidth(name) / 2;
        }
        int color = Color.BLACK.getRGB();
        int outlineColor = WurstplusThree.FRIEND_MANAGER.isFriend(player.getName()) ? outlineColourFriend.getValue().getRGB()
                : WurstplusThree.ENEMY_MANAGER.isEnemy(player.getName()) ? outlineColourEnemy.getValue().getRGB()
                : outlineColour.getValue().getRGB();
        Gui.drawRect(-width - 2, 10, width + 1, 20, changeAlpha(color, 120));
        if (outline.getValue()) {
            RenderUtil.drawOutlineLine(-width - 2, 10, width + 1, 20, outlineWidth.getValue(), outlineColor);
        }
        if (!far) {
            RenderUtil.drawTriangleOutline(width - WurstplusThree.GUI_FONT_MANAGER.getTextWidth(name) / 2f, arrowPos.getValue(), 5, 2, 1, outlineWidth.getValue().floatValue(), outlineColor);
        }
        if (customFont.getValue()) {
            WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(name, -width, 13 + textOffset.getValue(), fontColour.getValue().getRGB());
        } else {
            mc.fontRenderer.drawStringWithShadow(name, -width, 11, fontColour.getValue().getRGB());
        }
        if (armour.getValue()) {
            int xOffset = -6;
            int count = 0;
            for (ItemStack armourStack : player.inventory.armorInventory) {
                if (armourStack != null) {
                    xOffset -= 8;
                    if (armourStack.getItem() != Items.AIR) ++count;
                }
            }
            if (player.getHeldItemOffhand().getItem() != Items.AIR) ++count;

            int cacheX = xOffset - 8;
            xOffset += 8 * (5 - count) - (count == 0 ? 4 : 0);

            ItemStack renderStack;
            if (player.getHeldItemMainhand().getItem() != Items.AIR) {
                xOffset -= 8;
                renderStack = player.getHeldItemMainhand().copy();
                renderItem(player, renderStack, xOffset, -7, cacheX, true);
                xOffset += 16;
            } else {
                shownItem = true;
            }
            for (int index = 3; index >= 0; --index) {
                ItemStack armourStack = player.inventory.armorInventory.get(index);
                if (armourStack.getItem() != Items.AIR) {
                    ItemStack renderStack1 = armourStack.copy();
                    renderItem(player, renderStack1, xOffset, -7, cacheX, false);
                    xOffset += 16;
                }
            }
            ItemStack renderOffhand;
            if ( player.getHeldItemOffhand().getItem() != Items.AIR) {
                renderOffhand = player.getHeldItemOffhand().copy();
                renderItem(player, renderOffhand, xOffset, -7, cacheX, false);
            }
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        } else {
            if (durability.getValue()) {
                int xOffset = -6;
                int count = 0;
                for (ItemStack armourStack : player.inventory.armorInventory) {
                    if (armourStack != null) {
                        xOffset -= 8;
                        if (armourStack.getItem() != Items.AIR) count++;
                    }
                }
                if (player.getHeldItemOffhand().getItem() != Items.AIR) count++;
                xOffset += 8 * (5 - count) - (count == 0 ? 4 : 0);
                for (int index = 3; index >= 0; --index) {
                    ItemStack armourStack = player.inventory.armorInventory.get(index);
                    if (armourStack.getItem() != Items.AIR) {
                        ItemStack renderStack1 = armourStack.copy();
                        renderDurabilityText(renderStack1, xOffset);
                        xOffset += 16;
                    }
                }
                GlStateManager.enableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.enableTexture2D();
            }
        }
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        nametagRenderer.resetCaps();
        GlStateManager.resetColor();
        glColor4f(1F, 1F, 1F, 1F);
        glPopMatrix();
    }

    private void renderDurabilityText(ItemStack stack, int x) {
        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword
                || stack.getItem() instanceof ItemTool) {
            float green = ((float) stack.getMaxDamage() - (float) stack.getItemDamage()) / (float) stack.getMaxDamage();
            float red = 1 - green;
            int dmg = 100 - (int) (red * 100);
            if (customFont.getValue()) {
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(dmg + "%", x * 2 + 4, -7 - 10,
                        (new Color(red, green, 0)).getRGB());
            } else {
                mc.fontRenderer.drawStringWithShadow(dmg + "%", x * 2 + 4, -7 - 10,
                        (new Color(red, green, 0)).getRGB());
            }
        }
    }

    public void renderItem(EntityPlayer player, ItemStack stack, int x, int y, int nameX, boolean showHeldItemText) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);

        // enchant fix
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -100.0F;
        GlStateManager.scale(1, 1, 0.01f);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, (y / 2) - 12);
        if (durability.getValue()) {
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, (y / 2) - 12);
        }
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.scale(1, 1, 1);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
        GlStateManager.disableDepth();
        renderEnchantText(player, stack, x, y - 18);
        if (!shownItem && showHeldItemText) {
            if (customFont.getValue()) {
                WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), nameX * 2 + 95 - (WurstplusThree.GUI_FONT_MANAGER.getTextWidth(stack.getDisplayName()) / 2f), y - 37, -1);
            } else {
                mc.fontRenderer.drawStringWithShadow(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), nameX * 2 + 95 - (mc.fontRenderer.getStringWidth(stack.getDisplayName()) / 2f), y - 37, -1);
            }
            shownItem = true;
        }
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GL11.glPopMatrix();
    }

    public void renderEnchantText(EntityPlayer player, ItemStack stack, int x, int y) {
        int yCount = y;
        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword
                || stack.getItem() instanceof ItemTool) {
            if (durability.getValue()) {
                float green = ((float) stack.getMaxDamage() - (float) stack.getItemDamage()) / (float) stack.getMaxDamage();
                float red = 1 - green;
                int dmg = 100 - (int) (red * 100);
                if (customFont.getValue()) {
                    WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10,
                            (new Color(red, green, 0)).getRGB());
                } else {
                    mc.fontRenderer.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10,
                            (new Color(red, green, 0)).getRGB());
                }
            }
        }
        if (simple.getValue()) {
            if (isMaxEnchants(stack)) {
                GL11.glPushMatrix();
                GL11.glScalef(1f, 1f, 0);
                if (simple.getValue()) {
                    if (customFont.getValue()) {
                        WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow("Max", x * 2 + 7, yCount + 24, Colour.RED.getRGB());
                    } else {
                        mc.fontRenderer.drawStringWithShadow("Max", x * 2 + 7, yCount + 24, Colour.RED.getRGB());
                    }
                }
                GL11.glScalef(1f, 1f, 1);
                GL11.glPopMatrix();
                return;
            }
        }
        NBTTagList enchants = stack.getEnchantmentTagList();
        for (int index = 0; index < enchants.tagCount(); ++index) {
            short id = enchants.getCompoundTagAt(index).getShort("id");
            short level = enchants.getCompoundTagAt(index).getShort("lvl");
            Enchantment enc = Enchantment.getEnchantmentByID(id);
            if (enc != null) {
                if (enc.isCurse()) continue;
                String encName = level == 1 ? enc.getTranslatedName(level).substring(0, 3).toLowerCase() : enc.getTranslatedName(level).substring(0, 2).toLowerCase() + level;
                //encName = encName + level;
                encName = encName.substring(0, 1).toUpperCase() + encName.substring(1);
                GL11.glPushMatrix();
                GL11.glScalef(1f, 1f, 0);
                if (customFont.getValue()) {
                    WurstplusThree.GUI_FONT_MANAGER.drawStringWithShadow(encName, x * 2 + 3, yCount, -1);
                } else {
                    mc.fontRenderer.drawStringWithShadow(encName, x * 2 + 3, yCount, -1);
                }
                GL11.glScalef(1f, 1f, 1);
                GL11.glPopMatrix();
                yCount += 8;
            }
        }
    }

    public boolean isMaxEnchants(ItemStack stack) {
        return stack.getEnchantmentTagList().tagCount() > 2;
    }

    public String getHealth(float health) {
        if (health > 18) {
            return "a";
        }
        else if (health > 16) {
            return "2";
        }
        else if (health > 12) {
            return "e";
        }
        else if (health > 8) {
            return "6";
        }
        else if (health > 5) {
            return "c";
        }
        else {
            return "4";
        }
    }

    public String getPing(float ping) {
        if (ping > 200) {
            return "c";
        }
        else if (ping > 100) {
            return "e";
        }
        else {
            return "a";
        }
    }

    public String getPop(int pops) {
        if (pops > 10) {
            return "c" + pops;
        }
        else if (pops > 4) {
            return "e" + pops;
        }
        else {
            return "a" + pops;
        }
    }

    public static int changeAlpha(int origColor, int userInputedAlpha) {
        origColor = origColor & 0x00ffffff;
        return (userInputedAlpha << 24) | origColor;
    }

    public String getShortGamemode(String gameType) {
        if (gameType.equalsIgnoreCase("survival")) {
            return "S";
        }
        else if (gameType.equalsIgnoreCase("creative")) {
            return "C";
        }
        else if (gameType.equalsIgnoreCase("adventure")) {
            return "A";
        }
        else if (gameType.equalsIgnoreCase("spectator")) {
            return "SP";
        }
        else {
            return "NONE";
        }
    }
}
