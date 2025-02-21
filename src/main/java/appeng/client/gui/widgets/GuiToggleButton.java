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

package appeng.client.gui.widgets;

import java.util.regex.Pattern;

import appeng.client.texture.ExtraBlockTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiToggleButton extends GuiButton implements ITooltip {
    private static final Pattern PATTERN_NEW_LINE
        = Pattern.compile("\\n", Pattern.LITERAL);
    private final int iconIdxOn;
    private final int iconIdxOff;

    private final String displayName;
    private final String displayHint;

    private boolean isActive;

    public GuiToggleButton(
        final int x,
        final int y,
        final int on,
        final int off,
        final String displayName,
        final String displayHint
    ) {
        super(0, 0, 16, "");
        this.iconIdxOn = on;
        this.iconIdxOff = off;
        this.displayName = displayName;
        this.displayHint = displayHint;
        this.xPosition = x;
        this.yPosition = y;
        this.width = 16;
        this.height = 16;
    }

    public void setState(final boolean isOn) {
        this.isActive = isOn;
    }

    @Override
    public void
    drawButton(final Minecraft par1Minecraft, final int par2, final int par3) {
        if (this.visible) {
            final int iconIndex = this.getIconIndex();

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            par1Minecraft.renderEngine.bindTexture(
                ExtraBlockTextures.guiTexture("guis/states.png")
            );
            this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition
                && par2 < this.xPosition + this.width
                && par3 < this.yPosition + this.height;

            final int uv_y = (int) Math.floor(iconIndex / 16);
            final int uv_x = iconIndex - uv_y * 16;

            this.drawTexturedModalRect(
                this.xPosition, this.yPosition, 256 - 16, 256 - 16, 16, 16
            );
            this.drawTexturedModalRect(
                this.xPosition, this.yPosition, uv_x * 16, uv_y * 16, 16, 16
            );
            this.mouseDragged(par1Minecraft, par2, par3);
        }
    }

    private int getIconIndex() {
        return this.isActive ? this.iconIdxOn : this.iconIdxOff;
    }

    @Override
    public String getMessage() {
        if (this.displayName != null) {
            String name = StatCollector.translateToLocal(this.displayName);
            String value = StatCollector.translateToLocal(this.displayHint);

            if (name == null || name.isEmpty()) {
                name = this.displayName;
            }
            if (value == null || value.isEmpty()) {
                value = this.displayHint;
            }

            value = PATTERN_NEW_LINE.matcher(value).replaceAll("\n");
            final StringBuilder sb = new StringBuilder(value);

            int i = sb.lastIndexOf("\n");
            if (i <= 0) {
                i = 0;
            }
            while (i + 30 < sb.length() && (i = sb.lastIndexOf(" ", i + 30)) != -1) {
                sb.replace(i, i + 1, "\n");
            }

            return name + '\n' + sb;
        }
        return null;
    }

    @Override
    public int xPos() {
        return this.xPosition;
    }

    @Override
    public int yPos() {
        return this.yPosition;
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }
}
