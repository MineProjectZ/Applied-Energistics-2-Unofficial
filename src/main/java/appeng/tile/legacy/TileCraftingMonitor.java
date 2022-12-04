package appeng.tile.legacy;

import java.util.EnumSet;

import appeng.api.networking.GridFlags;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import net.minecraftforge.common.util.ForgeDirection;

public class TileCraftingMonitor extends TileLegacyDisplay implements ITerminalHost {
    public TileCraftingMonitor() {
        this.getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getProxy().setIdlePowerUsage(0.5);
        this.getProxy().setValidSides(EnumSet.allOf(ForgeDirection.class));
    }

    @Override
    public IMEMonitor<IAEItemStack> getItemInventory() {
        return null;
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory() {
        return null;
    }

    @Override
    public IConfigManager getConfigManager() {
        return null;
    }
}
