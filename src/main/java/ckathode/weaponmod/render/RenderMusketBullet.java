package ckathode.weaponmod.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ckathode.weaponmod.WeaponModResources;
import ckathode.weaponmod.entity.projectile.EntityMusketBullet;

public class RenderMusketBullet extends Render {

    public void renderMusketBullet(EntityMusketBullet entityarrow, double d, double d1, double d2, float f, float f1) {
        bindEntityTexture(entityarrow);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d, (float) d1, (float) d2);
        Tessellator tessellator = Tessellator.instance;
        float f2 = 0.0F;
        float f3 = 5F / 16F;
        float f10 = 0.05625F;
        GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
        GL11.glScalef(0.07F, 0.07F, 0.07F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0D, -1D, -1D, f2, f2);
        tessellator.addVertexWithUV(0D, -1D, 1D, f3, f2);
        tessellator.addVertexWithUV(0D, 1D, 1D, f3, f3);
        tessellator.addVertexWithUV(0D, 1D, -1D, f2, f3);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0D, 1D, -1D, f2, f2);
        tessellator.addVertexWithUV(0D, 1D, 1D, f3, f2);
        tessellator.addVertexWithUV(0D, -1D, 1D, f3, f3);
        tessellator.addVertexWithUV(0D, -1D, -1D, f2, f3);
        tessellator.draw();
        for (int j = 0; j < 4; j++) {
            GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-1D, -1D, 0.0D, f2, f2);
            tessellator.addVertexWithUV(1D, -1D, 0.0D, f3, f2);
            tessellator.addVertexWithUV(1D, 1D, 0.0D, f3, f3);
            tessellator.addVertexWithUV(-1D, 1D, 0.0D, f2, f3);
            tessellator.draw();
        }

        GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
        renderMusketBullet((EntityMusketBullet) entity, d, d1, d2, f, f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return WeaponModResources.Textures.BULLET;
    }
}
