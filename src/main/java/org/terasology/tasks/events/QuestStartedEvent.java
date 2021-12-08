// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.events;

import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.tasks.Quest;

public class QuestStartedEvent implements Event {

    private final Quest quest;

    /**
     * @param quest the quest the task is part of
     */
    public QuestStartedEvent(Quest quest) {
        this.quest = quest;
    }

    /**
     * @return the quest the completed quest
     */
    public Quest getQuest() {
        return quest;
    }
}
