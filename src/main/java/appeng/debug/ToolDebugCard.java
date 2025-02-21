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

package appeng.debug;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.pathing.ControllerState;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.networking.ticking.ITickManager;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.core.features.AEFeature;
import appeng.hooks.TickHandler;
import appeng.items.AEBaseItem;
import appeng.me.Grid;
import appeng.me.GridNode;
import appeng.me.cache.TickManagerCache;
import appeng.parts.p2p.PartP2PTunnel;
import appeng.tile.networking.TileController;
import appeng.util.Platform;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

public class ToolDebugCard extends AEBaseItem {
    public ToolDebugCard() {
        this.setFeature(
            EnumSet.of(AEFeature.UnsupportedDeveloperTools, AEFeature.Creative)
        );
    }

    @Override
    public boolean onItemUseFirst(
        final ItemStack stack,
        final EntityPlayer player,
        final World world,
        final int x,
        final int y,
        final int z,
        final int side,
        final float hitX,
        final float hitY,
        final float hitZ
    ) {
        if (ForgeEventFactory.onItemUseStart(player, stack, 1) <= 0)
            return true;

        if (Platform.isClient()) {
            return false;
        }

        if (player.isSneaking()) {
            int grids = 0;
            int totalNodes = 0;

            for (final Grid g : TickHandler.INSTANCE.getGridList()) {
                grids++;
                totalNodes += g.getNodes().size();
            }

            this.outputMsg(player, "Grids: " + grids);
            this.outputMsg(player, "Total Nodes: " + totalNodes);
        } else {
            final TileEntity te = world.getTileEntity(x, y, z);

            if (te instanceof IGridHost) {
                final GridNode node
                    = (GridNode) ((IGridHost) te)
                          .getGridNode(ForgeDirection.getOrientation(side));
                if (node != null) {
                    final Grid g = node.getInternalGrid();
                    final IGridNode center = g.getPivot();
                    this.outputMsg(player, "This Node: " + node.toString());
                    this.outputMsg(player, "Center Node: " + center.toString());

                    final IPathingGrid pg = g.getCache(IPathingGrid.class);
                    if (pg.getControllerState() == ControllerState.CONTROLLER_ONLINE) {
                        Set<IGridNode> next = new HashSet<IGridNode>();
                        next.add(node);

                        final int maxLength = 10000;

                        int length = 0;
                    outer:
                        while (!next.isEmpty()) {
                            final Iterable<IGridNode> current = next;
                            next = new HashSet<IGridNode>();

                            for (final IGridNode n : current) {
                                if (n.getMachine() instanceof TileController) {
                                    break outer;
                                }

                                for (final IGridConnection c : n.getConnections()) {
                                    next.add(c.getOtherSide(n));
                                }
                            }

                            length++;

                            if (length > maxLength) {
                                break;
                            }
                        }

                        this.outputMsg(player, "Cable Distance: " + length);
                    }

                    if (center.getMachine() instanceof PartP2PTunnel) {
                        this.outputMsg(
                            player,
                            "Freq: "
                                + ((PartP2PTunnel) center.getMachine()).getFrequency()
                        );
                    }

                    final TickManagerCache tmc = g.getCache(ITickManager.class);
                    for (final Class<? extends IGridHost> c : g.getMachineClasses()) {
                        int o = 0;
                        long nanos = 0;
                        for (final IGridNode oj : g.getMachines(c)) {
                            o++;
                            nanos += tmc.getAvgNanoTime(oj);
                        }

                        if (nanos < 0) {
                            this.outputMsg(player, c.getSimpleName() + " - " + o);
                        } else {
                            this.outputMsg(
                                player,
                                c.getSimpleName() + " - " + o + "; "
                                    + this.timeMeasurement(nanos)
                            );
                        }
                    }
                } else {
                    this.outputMsg(player, "No Node Available.");
                }
            } else {
                this.outputMsg(player, "Not Networked Block");
            }

            if (te instanceof IPartHost) {
                final IPart center = ((IPartHost) te).getPart(ForgeDirection.UNKNOWN);
                ((IPartHost) te).markForUpdate();
                if (center != null) {
                    final GridNode n = (GridNode) center.getGridNode();
                    this.outputMsg(player, "Node Channels: " + n.usedChannels());
                    for (final IGridConnection gc : n.getConnections()) {
                        final ForgeDirection fd = gc.getDirection(n);
                        if (fd != ForgeDirection.UNKNOWN) {
                            this.outputMsg(
                                player, fd.toString() + ": " + gc.getUsedChannels()
                            );
                        }
                    }
                }
            }

            if (te instanceof IAEPowerStorage) {
                final IAEPowerStorage ps = (IAEPowerStorage) te;
                this.outputMsg(
                    player,
                    "Energy: " + ps.getAECurrentPower() + " / " + ps.getAEMaxPower()
                );

                if (te instanceof IGridHost) {
                    final IGridNode node
                        = ((IGridHost) te)
                              .getGridNode(ForgeDirection.getOrientation(side));
                    if (node != null && node.getGrid() != null) {
                        final IEnergyGrid eg = node.getGrid().getCache(IEnergyGrid.class);
                        this.outputMsg(
                            player,
                            "GridEnergy: " + eg.getStoredPower() + " : "
                                + eg.getEnergyDemand(Double.MAX_VALUE)
                        );
                    }
                }
            }
        }
        return true;
    }

    private void outputMsg(final ICommandSender player, final String string) {
        player.addChatMessage(new ChatComponentText(string));
    }

    private String timeMeasurement(final long nanos) {
        final long ms = nanos / 100000;
        if (nanos <= 100000) {
            return nanos + "ns";
        }
        return (ms / 10.0f) + "ms";
    }
}
