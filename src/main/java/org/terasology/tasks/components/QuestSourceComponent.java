// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tasks.components;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

public class QuestSourceComponent implements Component<QuestSourceComponent> {
    public EntityRef source;

    public QuestSourceComponent(EntityRef source) {
        this.source = source;
    }

    public QuestSourceComponent() {
    }
}
