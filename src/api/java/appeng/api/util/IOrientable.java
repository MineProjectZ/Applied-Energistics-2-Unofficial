/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AlgorithmX2
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package appeng.api.util;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Nearly all of AE's Tile Entities implement IOrientable.
 * <p>
 * and it can be used to manipulate the direction of some machines, most of these
 * orientations are purely visual. <p> AE also responds to Block.rotateBlock
 */
public interface IOrientable {
    /**
     * @return true or false, if the tile rotation is meaningful, or even changeable
     */
    boolean canBeRotated();

    /**
     * @return the direction the tile is facing
     */
    ForgeDirection getForward();

    /**
     * @return the direction top of the tile
     */
    ForgeDirection getUp();

    /**
     * Update the orientation
     *
     * @param Forward new forward direction
     * @param Up      new upwards direction
     */
    void setOrientation(ForgeDirection Forward, ForgeDirection Up);
}