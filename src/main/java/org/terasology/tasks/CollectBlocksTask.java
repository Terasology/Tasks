/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.tasks;

import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;

public class CollectBlocksTask extends ModifiableTask {

    private final int targetAmount;
    private final String itemId;

    private final ItemIcon icon = new ItemIcon();

    private int amount;

    public CollectBlocksTask(int amount, String itemId) {
        this.targetAmount = amount;
        this.itemId = itemId;
        this.icon.setQuantity(targetAmount);
//        this.icon.setIcon(entity.getComponent(ItemComponent.class).icon);
    }

    @Override
    public ItemIcon getIcon() {
        return null;
    }

    @Override
    public String getShortName() {
        return "Fetch Blocks";
    }

    @Override
    public String getDescription() {
        return String.format("Collect %s: %d / %d", itemId, amount, targetAmount);
    }

    public String getItemId() {
        return itemId;
    }

    public void setAmount(int amount) {
        // TODO: consider ignoring changes only if status == ACTIVE
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public Status getStatus() {
        Status deps = getDependencyStatus();
        if (deps == Status.SUCCEEDED) {
            // it is not possible to fail this task
            return (amount >= targetAmount) ? Status.SUCCEEDED : Status.ACTIVE;
        }
        return deps;
    }

    @Override
    public String toString() {
        return String.format("CollectBlocksTask [%d/%d %s]", amount, targetAmount, itemId);
    }
}

