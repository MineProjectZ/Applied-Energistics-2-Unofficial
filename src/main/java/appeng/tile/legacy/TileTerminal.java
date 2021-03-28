package appeng.tile.legacy;

import appeng.api.config.Settings;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.networking.GridFlags;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.me.GridAccessException;
import appeng.tile.grid.AENetworkTile;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

public class TileTerminal extends AENetworkTile implements ITerminalHost, IConfigManagerHost {

    private final IConfigManager cm = new ConfigManager( this );

    public TileTerminal()
    {
        this.getProxy().setFlags( GridFlags.REQUIRE_CHANNEL );
        this.getProxy().setIdlePowerUsage( 0.5 );
        this.getProxy().setValidSides( EnumSet.allOf( ForgeDirection.class ) );

        this.cm.registerSetting( Settings.SORT_BY, SortOrder.NAME );
        this.cm.registerSetting( Settings.VIEW_MODE, ViewItems.ALL );
        this.cm.registerSetting( Settings.SORT_DIRECTION, SortDir.ASCENDING );
    }

    @Override
    public IMEMonitor<IAEItemStack> getItemInventory() {
        try
        {
            return this.getProxy().getStorage().getItemInventory();
        }
        catch( final GridAccessException e )
        {
            // err nope?
        }
        return null;
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory() {
        try
        {
            return this.getProxy().getStorage().getFluidInventory();
        }
        catch( final GridAccessException e )
        {
            // err nope?
        }
        return null;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.cm;
    }

    @Override
    public void updateSetting(IConfigManager manager, Enum settingName, Enum newValue) {

    }

}
