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
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.tasks.AbstractTaskFactory;
import org.terasology.tasks.CollectBlocksTask;
import org.terasology.tasks.DefaultQuest;
import org.terasology.tasks.GoToBeaconTask;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Status;
import org.terasology.tasks.Task;
import org.terasology.tasks.TaskFactory;
import org.terasology.tasks.TimeConstraintTask;
import org.terasology.tasks.components.QuestComponent;
import org.terasology.tasks.components.TaskElement;
import org.terasology.tasks.events.QuestCompleteEvent;
import org.terasology.tasks.events.TaskCompleteEvent;

import com.google.common.collect.Collections2;
import com.google.gson.JsonObject;

/**
 * This controls the main logic of the quest, and defines what to do with a "quest card"
 */
@Share(QuestSystem.class)
@RegisterSystem(RegisterMode.AUTHORITY)
public class QuestSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(QuestSystem.class);

    private final List<Quest> quests = new ArrayList<>();
    private final Collection<Quest> activeQuestView = Collections2.filter(quests,
            quest -> quest.getStatus() == Status.ACTIVE);

    @In
    private Time time;         // TODO: move to some place that is related to TimeConstraintTask

    /**
     * This updates the quest card variables for tasks calls.
     */
    @ReceiveEvent(components = {QuestComponent.class})
    public void onActivate(ActivateEvent event, EntityRef entity) {
        QuestComponent questComp = entity.getComponent(QuestComponent.class);
        List<TaskFactory<?>> factories = createTaskFactories();
        List<Task> tasks = new ArrayList<>();
        for (TaskElement ele : questComp.tasks) {
            for (TaskFactory<?> factory : factories) {
                if (factory.matches(ele.type)) {
                    tasks.add(factory.newInstance(ele.data));
                }
            }
        }

        quests.add(new DefaultQuest(questComp.shortName, questComp.description, tasks));

        logger.info("Quest is now active! The quest is {}", new DefaultQuest(questComp.shortName, questComp.description, tasks).getShortName());
    }

    @ReceiveEvent
    public void onTaskComplete(TaskCompleteEvent event, EntityRef entity) {
        Quest q = event.getQuest();
        if (q.getStatus().isComplete()) {
            quests.remove(q);
            entity.send(new QuestCompleteEvent(q, q.getStatus().isSuccess()));
        }
    }

    /**
     * Create a quest
     * @param name is the name of the quest, not the one that people see though.
     * @param questGoal is the item that you need to get in the quest.
     * @param friendlyQuestGoal is the goal that people see.
     * @param amountToGet is how much to get.
     * @param playerReturnTo is the entity that the player needs to return to for the quest to end.
     */

    /**
     * @return an unmodifiable map of all known quests.
     */
    public List<Quest> getQuests() {
        return Collections.unmodifiableList(quests);
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
        for (Quest q : quests) {
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
    void finishQuest(Quest quest, boolean success) {
        quests.remove(quest);
    }

    private List<TaskFactory<?>> createTaskFactories() {

        List<TaskFactory<?>> factories = new ArrayList<>();
        factories.add(new AbstractTaskFactory<CollectBlocksTask>("CollectBlocksTask") {

            @Override
            public CollectBlocksTask newInstance(JsonObject data) {
                return new CollectBlocksTask(
                        data.get("amount").getAsInt(),
                        data.get("itemId").getAsString());
            }
        });

        factories.add(new AbstractTaskFactory<TimeConstraintTask>("TimeConstraintTask") {

            @Override
            public TimeConstraintTask newInstance(JsonObject data) {
                return new TimeConstraintTask(time,
                        data.get("targetTime").getAsFloat());
            }
        });

        factories.add(new AbstractTaskFactory<GoToBeaconTask>("GoToBeaconTask") {

            @Override
            public GoToBeaconTask newInstance(JsonObject data) {
                return new GoToBeaconTask(
                        data.get("targetBeacon").getAsString());
            }
        });

        return factories;
    }
}
