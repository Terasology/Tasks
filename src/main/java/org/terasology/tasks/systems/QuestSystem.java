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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.registry.Share;
import org.terasology.tasks.DefaultQuest;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Status;
import org.terasology.tasks.Task;
import org.terasology.tasks.TaskGraph;
import org.terasology.tasks.components.QuestComponent;
import org.terasology.tasks.events.BeforeQuestEvent;
import org.terasology.tasks.events.QuestCompleteEvent;
import org.terasology.tasks.events.StartTaskEvent;
import org.terasology.tasks.events.TaskCompletedEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This controls the main logic of the quest, and defines what to do with a "quest card"
 */
@Share(QuestSystem.class)
@RegisterSystem(RegisterMode.CLIENT)
public class QuestSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(QuestSystem.class);

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

        BeforeQuestEvent beforeQuestEvent = questItem.send(new BeforeQuestEvent(questComp.shortName));
        if (!beforeQuestEvent.isConsumed()) {
            TaskGraph taskGraph = questComp.tasks;

            DefaultQuest quest = new DefaultQuest(entity, questComp.shortName, questComp.description, taskGraph);
            quests.put(entity, quest);

            for (Task task : taskGraph) {
                if (taskGraph.getTaskStatus(task) != Status.PENDING) {
                    logger.info("Starting task {}", task);
                    entity.send(new StartTaskEvent(quest, task));
                }
            }
        }
    }

    @ReceiveEvent
    public void onTaskComplete(TaskCompletedEvent event, EntityRef entity) {
        Quest quest = event.getQuest();
        logger.info("Task {} complete", event.getTask());
        TaskGraph taskGraph = quest.getTaskGraph();
        for (Task task : taskGraph) {
            if (taskGraph.getDependencies(task).contains(event.getTask())) {
                if (taskGraph.getTaskStatus(task) != Status.PENDING) {
                    logger.info("Starting task {}", task);
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
        // try exact match first
        for (Quest q : quests.values()) {
            if (q.getShortName().equals(shortName)) {
                return Optional.of(q);
            }
        }

        // then try ignoring case
        for (Quest q : quests.values()) {
            if (q.getShortName().equalsIgnoreCase(shortName)) {
                return Optional.of(q);
            }
        }
        return Optional.empty();
    }

    /**
     * @param quest the quest to complete
     * @param success if the quest was successful
     */
    public void removeQuest(Quest quest, boolean success) {
        for (EntityRef ref : quests.keys()) {
            quests.remove(ref, quest);
            break;
        }
    }
}
