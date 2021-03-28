package appeng.block.legacy;

import appeng.block.AEBaseTileBlock;
import appeng.core.features.AEFeature;
import appeng.core.sync.GuiBridge;
import appeng.tile.legacy.TileCraftTerminal;
import appeng.util.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

public class BlockCraftTerminal extends AEBaseTileBlock {

    public BlockCraftTerminal() {
        super(Material.iron);
        this.setTileEntity(TileCraftTerminal.class);
        this.setFeature( EnumSet.of(AEFeature.Legacy) );
    }

    @Override
    public boolean onActivated(World w, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        final TileCraftTerminal tile = this.getTileEntity(w, x, y, z);
        if( tile != null && !player.isSneaking() ) {
            if( Platform.isClient() )
            {
                return true;
            }
            Platform.openGUI(player, tile, ForgeDirection.getOrientation(side), GuiBridge.GUI_CRAFTING_TERMINAL);
            return true;
        }
        return false;
    }
}
