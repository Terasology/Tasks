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

package org.terasology.tasks.events;

import org.terasology.entitySystem.event.Event;
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
