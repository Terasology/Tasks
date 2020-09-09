// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks;

import org.terasology.engine.entitySystem.entity.EntityRef;

public interface Quest {

    /**
     * @return the id/name of the quest, not the one that people see though.
     */
    String getShortName();

    /**
     * @return human-readable description and explanation of the quest
     */
    String getDescription();

    TaskGraph getTaskGraph();

    /**
     * @return the status of the quest as a whole
     */
    Status getStatus();


    /**
     * @return the target entity for this quest
     */
    EntityRef getEntity();
}
