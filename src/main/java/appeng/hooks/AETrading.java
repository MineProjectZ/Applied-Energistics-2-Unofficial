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

package appeng.hooks;

import java.util.Random;

import appeng.api.AEApi;
import appeng.api.definitions.IItemDefinition;
import appeng.api.definitions.IMaterials;
import com.google.common.base.Optional;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class AETrading implements IVillageTradeHandler {
    @Override
    public void manipulateTradesForVillager(
        final EntityVillager villager,
        final MerchantRecipeList recipeList,
        final Random random
    ) {
        final IMaterials materials = AEApi.instance().definitions().materials();

        this.addMerchant(recipeList, materials.silicon(), 1, random, 2);
        this.addMerchant(recipeList, materials.certusQuartzCrystal(), 2, random, 4);
        this.addMerchant(recipeList, materials.certusQuartzDust(), 1, random, 3);

        this.addTrade(
            recipeList,
            materials.certusQuartzDust(),
            materials.certusQuartzCrystal(),
            random,
            2
        );
    }

    private void addMerchant(
        final MerchantRecipeList list,
        final IItemDefinition item,
        final int emera,
        final Random rand,
        final int greed
    ) {
        for (final ItemStack itemStack : item.maybeStack(1).asSet()) {
            // Sell
            final ItemStack from = itemStack.copy();
            final ItemStack to = new ItemStack(Items.emerald);

            final int multiplier = (Math.abs(rand.nextInt()) % 6);
            final int emeraldCost
                = emera + (Math.abs(rand.nextInt()) % greed) - multiplier;
            final int mood = rand.nextInt() % 2;

            from.stackSize = multiplier + mood;
            to.stackSize = multiplier * emeraldCost - mood;

            if (to.stackSize < 0) {
                from.stackSize -= to.stackSize;
                to.stackSize -= to.stackSize;
            }

            this.addToList(list, from, to);

            // Buy
            final ItemStack reverseTo = from.copy();
            final ItemStack reverseFrom = to.copy();

            reverseFrom.stackSize *= rand.nextFloat() * 3.0f + 1.0f;

            this.addToList(list, reverseFrom, reverseTo);
        }
    }

    private void addTrade(
        final MerchantRecipeList list,
        final IItemDefinition inputDefinition,
        final IItemDefinition outputDefinition,
        final Random rand,
        final int conversionVariance
    ) {
        final Optional<ItemStack> maybeInputStack = inputDefinition.maybeStack(1);
        final Optional<ItemStack> maybeOutputStack = outputDefinition.maybeStack(1);

        if (maybeInputStack.isPresent() && maybeOutputStack.isPresent()) {
            // Sell
            final ItemStack inputStack = maybeInputStack.get().copy();
            final ItemStack outputStack = maybeOutputStack.get().copy();

            inputStack.stackSize
                = 1 + (Math.abs(rand.nextInt()) % (1 + conversionVariance));
            outputStack.stackSize = 1;

            this.addToList(list, inputStack, outputStack);
        }
    }

    private void
    addToList(final MerchantRecipeList l, final ItemStack a, final ItemStack b) {
        if (a.stackSize < 1) {
            a.stackSize = 1;
        }
        if (b.stackSize < 1) {
            b.stackSize = 1;
        }

        if (a.stackSize > a.getMaxStackSize()) {
            a.stackSize = a.getMaxStackSize();
        }
        if (b.stackSize > b.getMaxStackSize()) {
            b.stackSize = b.getMaxStackSize();
        }

        l.add(new MerchantRecipe(a, b));
    }
}
