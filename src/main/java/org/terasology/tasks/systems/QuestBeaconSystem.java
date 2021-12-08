// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.systems;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.physics.events.CollideEvent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.tasks.GoToBeaconTask;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Status;
import org.terasology.tasks.TaskGraph;
import org.terasology.tasks.components.QuestBeaconComponent;
import org.terasology.tasks.events.StartTaskEvent;
import org.terasology.tasks.events.TaskCompletedEvent;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

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

    @ReceiveEvent
    public void onCompletedTask(TaskCompletedEvent event, EntityRef entity) {
        tasks.remove(event.getTask());
    }

    @ReceiveEvent(components = QuestBeaconComponent.class)
    public void onCollision(CollideEvent event, EntityRef beacon) {
        EntityRef charEnt = event.getOtherEntity();
        QuestBeaconComponent component = beacon.getComponent(QuestBeaconComponent.class);

        Iterator<Entry<GoToBeaconTask, Quest>> it = tasks.entrySet().iterator();
        while (it.hasNext()) {
            Entry<GoToBeaconTask, Quest> entry = it.next();
            GoToBeaconTask task = entry.getKey();
            if (task.getTargetBeaconId().equals(component.beaconId)) {
                TaskGraph taskGraph = entry.getValue().getTaskGraph();
                Status prevStatus = taskGraph.getTaskStatus(task);

                if (prevStatus == Status.ACTIVE) {
                    task.targetReached();
                }

                Status status = taskGraph.getTaskStatus(task);
                if (prevStatus != status && status.isComplete()) {
                    TaskCompletedEvent taskCompletedEvent = new TaskCompletedEvent(tasks.get(task), task, status.isSuccess());
                    it.remove();
                    EntityRef client = charEnt.getOwner();
                    client.send(taskCompletedEvent);
                }
            }
        }
    }
}
