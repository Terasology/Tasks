// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.events;

import org.terasology.engine.entitySystem.event.AbstractConsumableEvent;

/**
 * Event triggered before addition of new quest. This event is sent to the quest entity to allow
 * cancellation of quest if required.
 */
public class BeforeQuestEvent extends AbstractConsumableEvent {

    private final String name;

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
