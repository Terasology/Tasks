// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.tasks.TaskGraph;

public class QuestComponent implements Component {
    public String shortName;
    public String description;
    public TaskGraph tasks;
}
