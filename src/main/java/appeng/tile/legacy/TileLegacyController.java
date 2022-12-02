package appeng.tile.legacy;

import appeng.api.config.Actionable;
import appeng.me.GridAccessException;
import appeng.tile.grid.AENetworkPowerTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.InvOperation;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileLegacyController extends AENetworkPowerTile {
    private static final IInventory NULL_INVENTORY = new AppEngInternalInventory(null, 0);
    private static final int[] ACCESSIBLE_SLOTS_BY_SIDE = {};

    public TileLegacyController() { //TODO Fix power storage
        this.setInternalMaxPower(10000);
        this.setInternalPublicPowerStorage(true);
        this.getProxy().setIdlePowerUsage(6.0);
    }

    @Override
    public IInventory getInternalInventory() {
        return NULL_INVENTORY;
    }

    @Override
    public void onChangeInventory(
        IInventory inv, int slot, InvOperation mc, ItemStack removed, ItemStack added
    ) {}

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection whichSide) {
        return ACCESSIBLE_SLOTS_BY_SIDE;
    }

    @Override
    protected double getFunnelPowerDemand(final double maxReceived) {
        updateMeta();
        try {
            return this.getProxy().getEnergy().getEnergyDemand(10000);
        } catch (final GridAccessException e) {
            // no grid? use local...
            return super.getFunnelPowerDemand(maxReceived);
        }
    }

    @Override
    protected double funnelPowerIntoStorage(final double power, final Actionable mode) {
        updateMeta();
        try {
            final double ret = this.getProxy().getEnergy().injectPower(power, mode);
            if (mode == Actionable.SIMULATE) {
                return ret;
            }
            return 0;
        } catch (final GridAccessException e) {
            // no grid? use local...
            return super.funnelPowerIntoStorage(power, mode);
        }
    }

    public void updateMeta() {
        int meta = (int
        ) Math.ceil((5.0 * this.getInternalCurrentPower() / this.getInternalMaxPower()));
        if (meta < 0) {
            meta = 0;
        } else if (meta > 5) {
            meta = 5;
        }
        if (getProxy().isActive() && getInternalCurrentPower() == 0.0) {
            meta = 6;
        }
        this.worldObj.setBlockMetadataWithNotify(
            this.xCoord, this.yCoord, this.zCoord, meta, 2
        );
    }
}
