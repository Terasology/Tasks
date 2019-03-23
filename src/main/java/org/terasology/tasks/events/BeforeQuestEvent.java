/*
 * Copyright 2019 MovingBlocks
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

import org.terasology.entitySystem.event.AbstractConsumableEvent;

/**
 * Event triggered before addition of new quest. This event is sent to the quest entity to allow
 * cancellation of quest if required.
 */
public class BeforeQuestEvent extends AbstractConsumableEvent {

    private String name;

    /**
     * @param name the quest the task is part of
     */
    public BeforeQuestEvent(String name) {
        this.name = name;
    }

    /**
     * @return the short name of the quest
     */
    public String getQuestName() {
        return name;
    }

}
