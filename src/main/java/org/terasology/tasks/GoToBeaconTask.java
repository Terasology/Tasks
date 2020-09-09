// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks;

import org.terasology.inventory.rendering.nui.layers.ingame.ItemIcon;

public class GoToBeaconTask extends ModifiableTask {

    private final String targetBeaconId;

    private transient final ItemIcon icon = new ItemIcon();

    private transient boolean targetReached;

    public GoToBeaconTask(String id, String targetBeaconId) {
        super(id);
        this.targetBeaconId = targetBeaconId;
//        this.icon.setIcon(entity.getComponent(ItemComponent.class).icon);
    }

    @Override
    public ItemIcon getIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        return String.format("Go to %s", targetBeaconId);
    }

    /**
     * @return the name of beacon entity that the player needs to go to
     */
    public String getTargetBeaconId() {
        return targetBeaconId;
    }

    public void targetReached() {
        targetReached = true;
    }

    @Override
    public Status getStatus() {
        // it is not possible to fail this task
        return targetReached ? Status.SUCCEEDED : Status.ACTIVE;
    }

    @Override
    public String toString() {
        return String.format("GoToBeaconTask [%s]", targetBeaconId);
    }
}

