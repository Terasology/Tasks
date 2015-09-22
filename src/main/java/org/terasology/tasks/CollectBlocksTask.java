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

public class CollectBlocksTask implements Task {

    private final int targetAmount;
    private final String itemId;
    private final String targetBeaconName;

    private final ItemIcon icon = new ItemIcon();

    private int amount;
    private boolean reachedTarget;

    public CollectBlocksTask(int amount, String itemId, String targetBeaconName) {
        this.targetAmount = amount;
        this.itemId = itemId;
        this.targetBeaconName = targetBeaconName;
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
        return String.format("Fetched %d/%d blocks of %s and return them to %s", amount, targetAmount, itemId, targetBeaconName);
    }

    public String getItemId() {
        return itemId;
    }

    public String getTargetBeaconName() {
        return targetBeaconName;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void targetReached() {
        if (amount == targetAmount) {
            reachedTarget = true;
        }
        // ignore if amount has not yet been reached
    }

    @Override
    public Status getStatus() {
        // it is not possible to fail this task
        return reachedTarget ? Status.SUCCEEDED : Status.ACTIVE;
    }

    @Override
    public String toString() {
        return String.format("CollectBlocksTask [%d/%d %s to %s]", amount, targetAmount, itemId, targetBeaconName);
    }
}

