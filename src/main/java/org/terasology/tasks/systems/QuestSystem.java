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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.network.ClientComponent;
import org.terasology.network.ClientInfoComponent;
import org.terasology.persistence.typeHandling.PersistedDataMap;
import org.terasology.registry.Share;
import org.terasology.tasks.AbstractTaskFactory;
import org.terasology.tasks.CollectBlocksTask;
import org.terasology.tasks.DefaultQuest;
import org.terasology.tasks.GoToBeaconTask;
import org.terasology.tasks.ModifiableTask;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Status;
import org.terasology.tasks.Task;
import org.terasology.tasks.TaskFactory;
import org.terasology.tasks.TimeConstraintTask;
import org.terasology.tasks.components.QuestComponent;
import org.terasology.tasks.components.TaskElement;
import org.terasology.tasks.events.QuestCompleteEvent;
import org.terasology.tasks.events.QuestStartedEvent;
import org.terasology.tasks.events.TaskCompletedEvent;
import org.terasology.tasks.events.StartTaskEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

/**
 * This controls the main logic of the quest, and defines what to do with a "quest card"
 */
@Share(QuestSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class QuestSystem extends BaseComponentSystem {

    private final ListMultimap<EntityRef, Quest> quests = ArrayListMultimap.create();
    private final Collection<Quest> activeQuestView = Collections2.filter(quests.values(),
            quest -> quest.getStatus() == Status.ACTIVE);

    /**
     * This updates the quest card variables for tasks calls.
     */
    @ReceiveEvent(components = {QuestComponent.class})
    public void onActivate(ActivateEvent event, EntityRef questItem) {
        QuestComponent questComp = questItem.getComponent(QuestComponent.class);
        EntityRef entity = event.getInstigator().getOwner();
        List<TaskFactory<?>> factories = createTaskFactories();
        Map<String, ModifiableTask> taskMap = new LinkedHashMap<>();
        for (TaskElement ele : questComp.tasks) {
            for (TaskFactory<?> factory : factories) {
                if (factory.matches(ele.type)) {
                    ModifiableTask task = factory.newInstance(ele.data.getData().getAsValueMap());
                    taskMap.put(ele.id, task);
                }
            }
        }

        for (TaskElement ele : questComp.tasks) {
            if (ele.dependsOn != null) {
                ModifiableTask task = taskMap.get(ele.id);
                for (String depId : ele.dependsOn) {
                    task.addDependency(taskMap.get(depId));
                }
            }
        }

        ArrayList<Task> taskList = new ArrayList<>(taskMap.values());

        DefaultQuest quest = new DefaultQuest(entity, questComp.shortName, questComp.description, taskList);
        quests.put(entity, quest);

        entity.send(new QuestStartedEvent(quest));

        for (Task task : taskList) {
            if (!task.getStatus().isPending()) {
                entity.send(new StartTaskEvent(quest, task));
            }
        }
    }

    @ReceiveEvent
    public void onTaskComplete(TaskCompletedEvent event, EntityRef entity) {
        Quest quest = event.getQuest();
        for (Task task : quest.getAllTasks()) {
            if (task.getDependencies().contains(event.getTask())) {
                if (!task.getStatus().isPending()) {
                    entity.send(new StartTaskEvent(quest, task));
                }
            }
        }
        if (quest.getStatus().isComplete()) {
            entity.send(new QuestCompleteEvent(quest, quest.getStatus().isSuccess()));
        }
    }

    /**
     * @return an unmodifiable map of all known quests.
     */
    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(quests.values());
    }

    /**
     * @param entity the entity of interest
     * @return an unmodifiable map of all known quests for a given entity.
     */
    public List<Quest> getQuestsFor(EntityRef entity) {
        return Collections.unmodifiableList(quests.get(entity));
    }

    /**
     * @return an unmodifiable map of all active quests (Status == {@link Status#ACTIVE}).
     */
    public Collection<Quest> getActiveQuests() {
        return Collections.unmodifiableCollection(activeQuestView);
    }

    /**
     * @param shortName the case-sensitive short name
     * @return the first matching quest if it exists
     */
    public Optional<Quest> findQuest(String shortName) {
        for (Quest q : quests.values()) {
            if (q.getShortName().equals(shortName)) {
                return Optional.of(q);
            }
        }
        return Optional.empty();
    }

    /**
     * @param quest the quest to complete
     * @param success if the quest was successful
     */
    void removeQuest(Quest quest, boolean success) {
        for (EntityRef ref : quests.keys()) {
            quests.remove(ref, quest);
        }
    }

    private List<TaskFactory<?>> createTaskFactories() {

        List<TaskFactory<?>> factories = new ArrayList<>();
        factories.add(new AbstractTaskFactory<CollectBlocksTask>("CollectBlocksTask") {

            @Override
            public CollectBlocksTask newInstance(PersistedDataMap data) {
                return new CollectBlocksTask(
                        data.get("amount").getAsInteger(),
                        data.get("itemId").getAsString());
            }
        });

        factories.add(new AbstractTaskFactory<TimeConstraintTask>("TimeConstraintTask") {

            @Override
            public TimeConstraintTask newInstance(PersistedDataMap data) {
                return new TimeConstraintTask(
                        data.get("targetTime").getAsFloat());
            }
        });

        factories.add(new AbstractTaskFactory<GoToBeaconTask>("GoToBeaconTask") {

            @Override
            public GoToBeaconTask newInstance(PersistedDataMap data) {
                return new GoToBeaconTask(
                        data.get("beaconId").getAsString());
            }
        });

        return factories;
    }
}
