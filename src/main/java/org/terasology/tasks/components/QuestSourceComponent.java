// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tasks.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;

public class QuestSourceComponent implements Component {
    public EntityRef source;

    public QuestSourceComponent(EntityRef source) {
        this.source = source;
    }

    public QuestSourceComponent() {
    }
}
