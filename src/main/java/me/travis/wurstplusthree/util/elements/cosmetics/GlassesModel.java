package me.travis.wurstplusthree.util.elements.cosmetics;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * @author Madmegsox1
 * @since 01/05/2021
 */

public class GlassesModel extends ModelBase {
    public final ResourceLocation glassesTexture = new ResourceLocation("textures/cosmetics/sunglasses.png");
    public ModelRenderer firstLeftFrame;
    public ModelRenderer firstRightFrame;
    public ModelRenderer centerBar;
    public ModelRenderer farLeftBar;
    public ModelRenderer farRightBar;
    public ModelRenderer leftEar;
    public ModelRenderer rightEar;

    public GlassesModel(){
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.farLeftBar = new ModelRenderer((ModelBase)this, 0, 13);
        this.farLeftBar.setRotationPoint(-4.0f, 3.5f, -4.0f);
        this.farLeftBar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        this.rightEar = new ModelRenderer((ModelBase)this, 10, 0);
        this.rightEar.setRotationPoint(3.2f, 3.5f, -4.0f);
        this.rightEar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
        this.centerBar = new ModelRenderer((ModelBase)this, 0, 9);
        this.centerBar.setRotationPoint(-1.0f, 3.5f, -4.0f);
        this.centerBar.addBox(0.0f, 0.0f, 0.0f, 2, 1, 1, 0.0f);
        this.firstLeftFrame = new ModelRenderer((ModelBase)this, 0, 0);
        this.firstLeftFrame.setRotationPoint(-3.0f, 3.0f, -4.0f);
        this.firstLeftFrame.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
        this.firstRightFrame = new ModelRenderer((ModelBase)this, 0, 5);
        this.firstRightFrame.setRotationPoint(1.0f, 3.0f, -4.0f);
        this.firstRightFrame.addBox(0.0f, 0.0f, 0.0f, 2, 2, 1, 0.0f);
        this.leftEar = new ModelRenderer((ModelBase)this, 20, 0);
        this.leftEar.setRotationPoint(-4.2f, 3.5f, -4.0f);
        this.leftEar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 3, 0.0f);
        this.farRightBar = new ModelRenderer((ModelBase)this, 0, 17);
        this.farRightBar.setRotationPoint(3.0f, 3.5f, -4.0f);
        this.farRightBar.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.farLeftBar.render(f5);
        this.rightEar.render(f5);
        this.centerBar.render(f5);
        this.firstLeftFrame.render(f5);
        this.firstRightFrame.render(f5);
        this.leftEar.render(f5);
        this.farRightBar.render(f5);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
