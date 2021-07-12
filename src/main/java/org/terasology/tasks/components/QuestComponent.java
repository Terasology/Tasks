// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.components;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.tasks.TaskGraph;

public class QuestComponent implements Component<QuestComponent> {
    public String shortName;
    public String description;
    public TaskGraph tasks;

    @Override
    public void copy(QuestComponent other) {
        this.shortName = other.shortName;
        this.description = other.description;
        this.tasks = other.tasks;
    }
}
