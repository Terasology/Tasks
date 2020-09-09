// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks;

import org.terasology.inventory.rendering.nui.layers.ingame.ItemIcon;

public class CollectBlocksTask extends ModifiableTask {

    private final int targetAmount;
    private final String itemId;

    private transient final ItemIcon icon = new ItemIcon();

    private transient int amount;

    public CollectBlocksTask(String id, int amount, String itemId) {
        super(id);
        this.targetAmount = amount;
        this.itemId = itemId;

//        BlockItemComponent blockItemComp = item.getComponent(BlockItemComponent.class);
//        if (itemComp != null && itemComp.icon != null) {
//            itemIcon.setIcon(itemComp.icon);
//        } else if (blockItemComp != null) {
//            itemIcon.setMesh(blockItemComp.blockFamily.getArchetypeBlock().getMesh());
//            itemIcon.setMeshTexture(Assets.getTexture("engine:terrain").get());
//        }
//      this.icon.setQuantity(targetAmount);

    }

    @Override
    public ItemIcon getIcon() {
        return icon;
    }

    @Override
    public String getDescription() {
        return String.format("Collect %s: %d / %d", itemId, amount, targetAmount);
    }

    public String getItemId() {
        return itemId;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        // TODO: consider ignoring changes only if status == ACTIVE
        this.amount = amount;
    }

    @Override
    public Status getStatus() {
        // it is not possible to fail this task
        return (amount >= targetAmount) ? Status.SUCCEEDED : Status.ACTIVE;
    }

    @Override
    public String toString() {
        return String.format("CollectBlocksTask [%d/%d %s]", amount, targetAmount, itemId);
    }
}

