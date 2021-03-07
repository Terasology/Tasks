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

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.tasks.Quest;

/**
 *
 */
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
