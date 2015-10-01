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

public class GoToBeaconTask extends ModifiableTask {

    private final String targetBeaconName;

    private final ItemIcon icon = new ItemIcon();

    private boolean targetReached;

    public GoToBeaconTask(String id, String targetBeaconName) {
        super(id);
        this.targetBeaconName = targetBeaconName;
//        this.icon.setIcon(entity.getComponent(ItemComponent.class).icon);
    }

    @Override
    public ItemIcon getIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        return String.format("Go to %s", targetBeaconName);
    }

    /**
     * @return the name of beacon entity that the player needs to go to
     */
    public String getTargetBeaconName() {
        return targetBeaconName;
    }

    public void targetReached() {
        if (getStatus() == Status.ACTIVE) {
            targetReached = true;
        }
    }

    @Override
    public Status getStatus() {
        Status deps = getDependencyStatus();
        if (deps == Status.SUCCEEDED) {
            // it is not possible to fail this task
            return targetReached ? Status.SUCCEEDED : Status.ACTIVE;
        }
        return deps;
    }

    @Override
    public String toString() {
        return String.format("GoToBeaconTask [%s]", targetBeaconName);
    }
}

