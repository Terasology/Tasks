// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.components;

import org.terasology.gestalt.entitysystem.component.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a list of available quests.
 */
public final class QuestListComponent implements Component<QuestListComponent> {

    public List<String> questItems = new ArrayList<>();

    @Override
    public void copyFrom(QuestListComponent other) {
        this.questItems.clear();
        this.questItems.addAll(other.questItems);
    }
}
