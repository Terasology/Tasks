/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.tasks;

import java.util.Collections;
import java.util.List;

import org.terasology.entitySystem.entity.EntityRef;

/**
 *
 */
public class DefaultQuest implements Quest {
    private final String shortName;
    private final String description;
    private final List<Task> tasks;
    private final EntityRef entity;

    public DefaultQuest(EntityRef entity, String shortName, String description, List<Task> tasks) {
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
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * The quest fails if any task failed. Otherwise, the quest is active if any task is active. Otherwise,
     * the quest has completed successfully.
     * @return the quest status
     */
    @Override
    public Status getStatus() {
        for (Task task : tasks) {
            Status taskStatus = task.getStatus();
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
