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

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * A modified version of the Minecraft text field.
 * You can initialize it over the full element span.
 * The mouse click area is increased to the full element
 * subtracted with the defined padding.
 * <p>
 * The rendering does pay attention to the size of the '_' caret.
 */
public class MEGuiTextField extends GuiTextField {
    private static final int PADDING = 2;

    private final int _xPos;
    private final int _yPos;
    private final int _width;
    private final int _height;

    /**
     * Uses the values to instantiate a padded version of a text field.
     * Pays attention to the '_' caret.
     *
     * @param fontRenderer renderer for the strings
     * @param xPos         absolute left position
     * @param yPos         absolute top position
     * @param width        absolute width
     * @param height       absolute height
     */
    public MEGuiTextField(
        final FontRenderer fontRenderer,
        final int xPos,
        final int yPos,
        final int width,
        final int height
    ) {
        super(
            fontRenderer,
            xPos + PADDING,
            yPos + PADDING,
            width - 2 * PADDING - fontRenderer.getCharWidth('_'),
            height - 2 * PADDING
        );

        this._xPos = xPos;
        this._yPos = yPos;
        this._width = width;
        this._height = height;
    }

    @Override
    public void mouseClicked(final int xPos, final int yPos, final int button) {
        super.mouseClicked(xPos, yPos, button);

        final boolean requiresFocus = this.isMouseIn(xPos, yPos);

        this.setFocused(requiresFocus);
    }

    /**
     * Checks if the mouse is within the element
     *
     * @param xCoord current x coord of the mouse
     * @param yCoord current y coord of the mouse
     * @return true if mouse position is within the text field area
     */
    public boolean isMouseIn(final int xCoord, final int yCoord) {
        final boolean withinXRange
            = this._xPos <= xCoord && xCoord < this._xPos + this._width;
        final boolean withinYRange
            = this._yPos <= yCoord && yCoord < this._yPos + this._height;

        return withinXRange && withinYRange;
    }
}
