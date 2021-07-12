// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.components;

import org.terasology.gestalt.entitysystem.component.Component;

public class QuestBeaconComponent implements Component<QuestBeaconComponent> {
    public String beaconId;

    @Override
    public void copy(QuestBeaconComponent other) {
        this.beaconId = other.beaconId;
    }
}
