package appeng.me.cluster.implementations;

import appeng.api.util.WorldCoord;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.MBCalculator;
import appeng.tile.legacy.TileTransitionPlane;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TransitionPlaneCalculator extends MBCalculator {
    public TileTransitionPlane ttp;

    public TransitionPlaneCalculator(TileTransitionPlane ttp) {
        super(ttp);
        this.ttp = ttp;
    }

    @Override
    public boolean checkMultiblockScale(WorldCoord min, WorldCoord max) {
        return min.x == max.x || min.y == max.y || min.z == max.z;
    }

    @Override
    public IAECluster createCluster(World w, WorldCoord min, WorldCoord max) {
        return new TransitionPlaneCluster(min, max);
    }

    @Override
    public boolean
    verifyInternalStructure(World worldObj, WorldCoord min, WorldCoord max) {
        for (int x = min.x; x < max.x; x++)
            for (int y = min.y; y < max.y; y++)
                for (int z = min.z; z < max.z; z++)
                    if (!this.isValidTile(worldObj.getTileEntity(x, y, z)))
                        return false;

        return true;
    }

    @Override
    public void disconnect() {
        this.ttp.disconnect(true);
    }

    @Override
    public void updateTiles(IAECluster c, World w, WorldCoord min, WorldCoord max) {
        TransitionPlaneCluster tpc = (TransitionPlaneCluster) c;
        for (int x = min.x; x <= max.x; x++) {
            for (int y = min.y; y <= max.y; y++) {
                for (int z = min.z; z <= max.z; z++) {
                    TileTransitionPlane ttp
                        = (TileTransitionPlane) w.getTileEntity(x, y, z);
                    ttp.updateStatus(tpc);
                    tpc.tiles.add(ttp);
                }
            }
        }
    }

    @Override
    public boolean isValidTile(TileEntity te) {
        // Wrong tile
        if (!(te instanceof TileTransitionPlane))
            return false;

        TileTransitionPlane ottp = (TileTransitionPlane) te;

        if (this.ttp.getForward() == ForgeDirection.UNKNOWN
            || ottp.getForward() == ForgeDirection.UNKNOWN)
            throw new IllegalStateException("alec");

        // facing in the wrong direction
        if (this.ttp.getForward() != ottp.getForward())
            return false;

        // not aligned in the Z-axis
        boolean zaligned = false;
        switch (this.ttp.getForward()) {
            case DOWN:
            case UP:
                zaligned = this.ttp.yCoord == ottp.yCoord;
                break;

            case NORTH:
            case SOUTH:
                zaligned = this.ttp.zCoord == ottp.zCoord;
                break;

            case WEST:
            case EAST:
                zaligned = this.ttp.xCoord == ottp.xCoord;
                break;

            case UNKNOWN:
                throw new IllegalStateException("alec");
        }

        if (!zaligned)
            return false;

        return true;
    }
}
