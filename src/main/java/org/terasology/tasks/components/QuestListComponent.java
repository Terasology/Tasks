// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.components;

import org.terasology.engine.entitySystem.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a list of available quests.
 */
public final class QuestListComponent implements Component {

    public List<String> questItems = new ArrayList<>();
}
