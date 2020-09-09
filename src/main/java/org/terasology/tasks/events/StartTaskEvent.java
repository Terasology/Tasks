// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Task;

/**
 *
 */
public class StartTaskEvent implements Event {

    private final Task task;
    private final Quest quest;

    /**
     * @param quest the quest the task is part of
     * @param task the completed task
     */
    public StartTaskEvent(Quest quest, Task task) {
        this.task = task;
        this.quest = quest;
    }

    /**
     * @return the quest the task is part of
     */
    public Quest getQuest() {
        return quest;
    }

    /**
     * @return the completed task
     */
    public Task getTask() {
        return task;
    }
}
