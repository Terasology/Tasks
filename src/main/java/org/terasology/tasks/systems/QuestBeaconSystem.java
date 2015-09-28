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

import java.util.LinkedHashMap;
import java.util.Map;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.physics.events.CollideEvent;
import org.terasology.tasks.GoToBeaconTask;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Status;
import org.terasology.tasks.components.QuestBeaconComponent;
import org.terasology.tasks.events.TaskCompletedEvent;
import org.terasology.tasks.events.StartTaskEvent;

/**
 * This class is used for the quest beacons, to see where the player is in relation to the beacon.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class QuestBeaconSystem extends BaseComponentSystem {

    private final Map<GoToBeaconTask, Quest> tasks = new LinkedHashMap<>();

    @ReceiveEvent
    public void onStartTask(StartTaskEvent event, EntityRef entity) {
        if (event.getTask() instanceof GoToBeaconTask) {
            GoToBeaconTask task = (GoToBeaconTask) event.getTask();
            tasks.put(task, event.getQuest());
        }
    }

    @ReceiveEvent(components = QuestBeaconComponent.class)
    public void onCollision(CollideEvent event, EntityRef entity) {
        EntityRef beacon = event.getOtherEntity();
        QuestBeaconComponent component = beacon.getComponent(QuestBeaconComponent.class);

        for (GoToBeaconTask task : tasks.keySet()) {
            if (task.getTargetBeaconName().equals(component.beaconId)) {
                Status prevStatus = task.getStatus();
                task.targetReached();

                Status status = task.getStatus();
                if (prevStatus != status && status.isComplete()) {
                    entity.send(new TaskCompletedEvent(tasks.get(task), task, status.isSuccess()));
                }
            }
        }
    }
}
