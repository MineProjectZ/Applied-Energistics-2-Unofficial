/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.client.render.effects;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class LightningFX extends EntityFX {
    private static final Random RANDOM_GENERATOR = new Random();
    private static final int STEPS = 5;

    private final double[][] precomputedSteps;
    private final double[] vertices = new double[3];
    private final double[] verticesWithUV = new double[3];
    private boolean hasData = false;

    public LightningFX(
        final World w,
        final double x,
        final double y,
        final double z,
        final double r,
        final double g,
        final double b
    ) {
        this(w, x, y, z, r, g, b, 6);
        this.regen();
    }

    protected LightningFX(
        final World w,
        final double x,
        final double y,
        final double z,
        final double r,
        final double g,
        final double b,
        final int maxAge
    ) {
        super(w, x, y, z, r, g, b);
        this.precomputedSteps = new double[LightningFX.STEPS][3];
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.particleMaxAge = maxAge;
        this.noClip = true;
    }

    protected void regen() {
        double lastDirectionX = (RANDOM_GENERATOR.nextDouble() - 0.5) * 0.9;
        double lastDirectionY = (RANDOM_GENERATOR.nextDouble() - 0.5) * 0.9;
        double lastDirectionZ = (RANDOM_GENERATOR.nextDouble() - 0.5) * 0.9;

        for (int s = 0; s < LightningFX.STEPS; s++) {
            this.precomputedSteps[s][0] = lastDirectionX
                = (lastDirectionX + (RANDOM_GENERATOR.nextDouble() - 0.5) * 0.9) / 2.0;
            this.precomputedSteps[s][1] = lastDirectionY
                = (lastDirectionY + (RANDOM_GENERATOR.nextDouble() - 0.5) * 0.9) / 2.0;
            this.precomputedSteps[s][2] = lastDirectionZ
                = (lastDirectionZ + (RANDOM_GENERATOR.nextDouble() - 0.5) * 0.9) / 2.0;
        }
    }

    protected int getSteps() {
        return LightningFX.STEPS;
    }

    @Override
    public int getBrightnessForRender(final float par1) {
        final int j1 = 13;
        return j1 << 20 | j1 << 4;
    }

    @Override
    public void renderParticle(
        final Tessellator tess,
        final float l,
        final float rX,
        final float rY,
        final float rZ,
        final float rYZ,
        final float rXY
    ) {
        final float j = 1.0f;

        tess.setColorRGBA_F(
            this.particleRed * j * 0.9f,
            this.particleGreen * j * 0.95f,
            this.particleBlue * j,
            this.particleAlpha
        );

        if (this.particleAge == 3) {
            this.regen();
        }
        double f6 = this.particleTextureIndexX / 16.0;
        final double f7 = f6 + 0.0324375F;
        double f8 = this.particleTextureIndexY / 16.0;
        final double f9 = f8 + 0.0324375F;

        f6 = f7;
        f8 = f9;

        double scale = 0.02; // 0.02F * this.particleScale;

        final double[] a = new double[3];
        final double[] b = new double[3];

        double ox = 0;
        double oy = 0;
        double oz = 0;

        final EntityPlayer p = Minecraft.getMinecraft().thePlayer;
        double offX = -rZ;
        double offY
            = MathHelper.cos((float) (Math.PI / 2.0f + p.rotationPitch * 0.017453292F));
        double offZ = rX;

        for (int layer = 0; layer < 2; layer++) {
            if (layer == 0) {
                scale = 0.04;
                offX *= 0.001;
                offY *= 0.001;
                offZ *= 0.001;
                tess.setColorRGBA_F(
                    this.particleRed * j * 0.4f,
                    this.particleGreen * j * 0.25f,
                    this.particleBlue * j * 0.45f,
                    this.particleAlpha
                );
            } else {
                offX = 0;
                offY = 0;
                offZ = 0;
                scale = 0.02;
                tess.setColorRGBA_F(
                    this.particleRed * j * 0.9f,
                    this.particleGreen * j * 0.65f,
                    this.particleBlue * j * 0.85f,
                    this.particleAlpha
                );
            }

            for (int cycle = 0; cycle < 3; cycle++) {
                this.clear();

                double x = (this.prevPosX + (this.posX - this.prevPosX) * l - interpPosX)
                    - offX;
                double y = (this.prevPosY + (this.posY - this.prevPosY) * l - interpPosY)
                    - offY;
                double z = (this.prevPosZ + (this.posZ - this.prevPosZ) * l - interpPosZ)
                    - offZ;

                for (int s = 0; s < LightningFX.STEPS; s++) {
                    final double xN = x + this.precomputedSteps[s][0];
                    final double yN = y + this.precomputedSteps[s][1];
                    final double zN = z + this.precomputedSteps[s][2];

                    final double xD = xN - x;
                    final double yD = yN - y;
                    final double zD = zN - z;

                    if (cycle == 0) {
                        ox = (yD * 0) - (1 * zD);
                        oy = (zD * 0) - (0 * xD);
                        oz = (xD * 1) - (0 * yD);
                    }
                    if (cycle == 1) {
                        ox = (yD * 1) - (0 * zD);
                        oy = (zD * 0) - (1 * xD);
                        oz = (xD * 0) - (0 * yD);
                    }
                    if (cycle == 2) {
                        ox = (yD * 0) - (0 * zD);
                        oy = (zD * 1) - (0 * xD);
                        oz = (xD * 0) - (1 * yD);
                    }

                    final double ss = Math.sqrt(ox * ox + oy * oy + oz * oz)
                        / ((((double) LightningFX.STEPS - (double) s) / LightningFX.STEPS)
                           * scale);
                    ox /= ss;
                    oy /= ss;
                    oz /= ss;

                    a[0] = x + ox;
                    a[1] = y + oy;
                    a[2] = z + oz;

                    b[0] = x;
                    b[1] = y;
                    b[2] = z;

                    this.draw(tess, a, b, f6, f8);

                    x = xN;
                    y = yN;
                    z = zN;
                }
            }
        }
    }

    private void clear() {
        this.hasData = false;
    }

    private void draw(
        final Tessellator tess,
        final double[] a,
        final double[] b,
        final double f6,
        final double f8
    ) {
        if (this.hasData) {
            tess.addVertexWithUV(a[0], a[1], a[2], f6, f8);
            tess.addVertexWithUV(
                this.vertices[0], this.vertices[1], this.vertices[2], f6, f8
            );
            tess.addVertexWithUV(
                this.verticesWithUV[0],
                this.verticesWithUV[1],
                this.verticesWithUV[2],
                f6,
                f8
            );
            tess.addVertexWithUV(b[0], b[1], b[2], f6, f8);
        }

        this.hasData = true;

        for (int x = 0; x < 3; x++) {
            this.vertices[x] = a[x];
            this.verticesWithUV[x] = b[x];
        }
    }

    protected double[][] getPrecomputedSteps() {
        return this.precomputedSteps;
    }
}
