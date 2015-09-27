/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.tasks.systems;

import java.util.ArrayList;
import java.util.Collection;

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.registry.In;
import org.terasology.tasks.Task;
import org.terasology.tasks.TimeConstraintTask;
import org.terasology.tasks.events.QuestStartedEvent;
import org.terasology.tasks.events.TaskStartedEvent;

/**
 * TODO Type description
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class TimedTaskSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    @In
    private Time time;

    private final Collection<TimeConstraintTask> timeTasks = new ArrayList<>();

    @ReceiveEvent
    public void onQuestStarted(QuestStartedEvent event, EntityRef entity) {
        for (TimeConstraintTask task : event.getQuest().getTasks(TimeConstraintTask.class)) {
            timeTasks.add(task);
        }
    }

    @Override
    public void update(float delta) {
        for (TimeConstraintTask task : timeTasks) {
            task.setTime(time.getGameTime());
        }
    }

}
