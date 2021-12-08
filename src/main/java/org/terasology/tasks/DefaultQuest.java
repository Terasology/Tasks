// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks;

import org.terasology.engine.entitySystem.entity.EntityRef;

public class DefaultQuest implements Quest {
    private final String shortName;
    private final String description;
    private final TaskGraph tasks;
    private final EntityRef entity;

    public DefaultQuest(EntityRef entity, String shortName, String description, TaskGraph tasks) {
        this.entity = entity;
        this.shortName = shortName;
        this.description = description;
        this.tasks = tasks;
    }

    @Override
    public EntityRef getEntity() {
        return entity;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public TaskGraph getTaskGraph() {
        return tasks;
    }

    /**
     * The quest fails if any task failed. Otherwise, the quest is active if any task is active. Otherwise,
     * the quest has completed successfully.
     * @return the quest status
     */
    @Override
    public Status getStatus() {
        for (Task task : tasks) {
            Status taskStatus = tasks.getTaskStatus(task);
            if (taskStatus == Status.FAILED) {
                return Status.FAILED;
            }
            if (taskStatus == Status.ACTIVE) {
                return Status.ACTIVE;
            }
        }
        return Status.SUCCEEDED;
    }

    @Override
    public String toString() {
        return String.format("DefaultQuest [%s]", shortName);
    }
}
