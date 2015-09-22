/*
 * Copyright 2015 MovingBlocks
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
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.inventory.events.InventorySlotChangedEvent;
import org.terasology.registry.In;
import org.terasology.tasks.CollectBlocksTask;
import org.terasology.tasks.Quest;
import org.terasology.tasks.components.QuestBeaconComponent;
import org.terasology.tasks.events.ReachedBeaconEvent;
import org.terasology.tasks.events.TaskCompleteEvent;

/**
 *
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class CollectBlocksTaskSystem extends BaseComponentSystem
{
    private static final Logger logger = LoggerFactory.getLogger(CollectBlocksTaskSystem.class);

    @In
    private QuestSystem questSystem;

    @ReceiveEvent(components = {InventoryComponent.class}, priority = EventPriority.PRIORITY_HIGH)
    public void onReceiveItem(InventorySlotChangedEvent event, EntityRef entity) {
        ItemComponent newItem = event.getNewItem().getComponent(ItemComponent.class);
        ItemComponent oldItem = event.getOldItem().getComponent(ItemComponent.class);

        ItemComponent item = newItem != null ? newItem : oldItem;

        String stackID = item.stackId;

        for (Quest quest : questSystem.getActiveQuests()) {
            for (CollectBlocksTask task : quest.getTasks(CollectBlocksTask.class)) {
                // consider using InventoryUtils.isSameItem(EntityRef, EntityRef)
                if (stackID.equals(task.getItemId())) {
                    task.setAmount(item.stackCount);
                    logger.info("Task {} updated", task);
                }
            }
        }
    }

    @ReceiveEvent
    public void onBeaconReached(ReachedBeaconEvent event, EntityRef entity) {
        for (Quest quest : questSystem.getActiveQuests()) {
            for (CollectBlocksTask task : quest.getTasks(CollectBlocksTask.class)) {
                EntityRef beaconEntity = event.getBeaconEntity();
                QuestBeaconComponent beacon = beaconEntity.getComponent(QuestBeaconComponent.class);
                if (task.getTargetBeaconName().equals(beacon.beaconName)) {
                    task.targetReached();
                    logger.info("Target reached!");

                    if (task.getStatus().isComplete()) {
                        event.getInstigatorEntity().send(new TaskCompleteEvent(quest, task, true));
                    }
                }
            }
        }
    }
}
