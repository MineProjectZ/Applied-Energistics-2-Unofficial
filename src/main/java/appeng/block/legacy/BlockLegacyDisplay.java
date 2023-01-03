package appeng.block.legacy;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseTileBlock;
import appeng.client.render.BaseBlockRender;
import appeng.client.render.blocks.RenderBlockLegacyDisplay;
import appeng.tile.AEBaseTile;
import appeng.tile.legacy.TileLegacyDisplay;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class BlockLegacyDisplay extends AEBaseTileBlock {
    public BlockLegacyDisplay(Material mat) {
        super(mat);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected BaseBlockRender<? extends AEBaseBlock, ? extends AEBaseTile> getRenderer() {
        return new RenderBlockLegacyDisplay();
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        return ((TileLegacyDisplay) te).isDisplayPowered() ? 7 : 0;
    }
}
