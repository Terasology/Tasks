// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.events;

import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.tasks.Quest;

public class QuestCompleteEvent implements Event {

    private final boolean success;
    private final Quest quest;

    /**
     * @param quest the quest the task is part of
     * @param success true if successfully completed, false otherwise
     */
    public QuestCompleteEvent(Quest quest, boolean success) {
        this.quest = quest;
        this.success = success;
    }

    /**
     * @return the quest the completed quest
     */
    public Quest getQuest() {
        return quest;
    }

    /**
     * @return true if successfully completed, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
}
