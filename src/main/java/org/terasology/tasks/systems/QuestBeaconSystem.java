/*
 * Copyright 2013 MovingBlocks
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

package org.terasology.tasks.systems;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.physics.events.CollideEvent;
import org.terasology.registry.In;
import org.terasology.tasks.GoToBeaconTask;
import org.terasology.tasks.Quest;
import org.terasology.tasks.components.QuestBeaconComponent;
import org.terasology.tasks.events.TaskCompleteEvent;

/**
 * This class is used for the quest beacons, to see where the player is in relation to the beacon.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class QuestBeaconSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(QuestBeaconSystem.class);

    @In
    private QuestSystem questSystem;

    @ReceiveEvent(components = QuestBeaconComponent.class)
    public void onCollision(CollideEvent event, EntityRef entity) {
        QuestBeaconComponent component = entity.getComponent(QuestBeaconComponent.class);
        EntityRef beacon = event.getOtherEntity();

        for (Quest quest : questSystem.getActiveQuests()) {
            for (GoToBeaconTask task : quest.getTasks(GoToBeaconTask.class)) {
                if (task.getTargetBeaconName().equals(component.beaconName)) {
                    task.targetReached();
                    logger.info("Target reached!");

                    if (task.getStatus().isComplete()) {
                        entity.send(new TaskCompleteEvent(quest, task, true));
                    }
                }
            }
        }
    }
}
