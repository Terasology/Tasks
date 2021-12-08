// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.events;

import org.terasology.gestalt.entitysystem.event.Event;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Task;

public class TaskCompletedEvent implements Event {

    private final Task task;
    private final boolean success;
    private final Quest quest;

    /**
     * @param quest the quest the task is part of
     * @param task the completed task
     * @param success true if successfully completed, false otherwise
     */
    public TaskCompletedEvent(Quest quest, Task task, boolean success) {
        this.task = task;
        this.quest = quest;
        this.success = success;
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

    /**
     * @return true if successfully completed, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
}
