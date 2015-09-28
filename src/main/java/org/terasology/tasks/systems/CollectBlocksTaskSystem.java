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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.inventory.events.InventorySlotStackSizeChangedEvent;
import org.terasology.registry.In;
import org.terasology.tasks.CollectBlocksTask;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Status;
import org.terasology.tasks.TimeConstraintTask;
import org.terasology.tasks.events.TaskCompletedEvent;
import org.terasology.tasks.events.StartTaskEvent;

/**
 *
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class CollectBlocksTaskSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(CollectBlocksTaskSystem.class);

    private final Map<CollectBlocksTask, Quest> tasks = new LinkedHashMap<>();

    @ReceiveEvent(components = {InventoryComponent.class})
    public void onStartTask(StartTaskEvent event, EntityRef entity) {
        if (event.getTask() instanceof CollectBlocksTask) {
            CollectBlocksTask task = (CollectBlocksTask) event.getTask();
            InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
            int count = 0;
            for (EntityRef itemRef : inventory.itemSlots) {
                ItemComponent item = itemRef.getComponent(ItemComponent.class);
                if (item != null && item.stackId.equalsIgnoreCase(task.getItemId())) {
                    task.setAmount(count += item.stackCount);
                }
            }
            task.setAmount(count);
            tasks.put(task, event.getQuest());
        }
    }

    @ReceiveEvent(components = {InventoryComponent.class})
    public void onInventoryChange(InventorySlotStackSizeChangedEvent event, EntityRef charEntity) {
        InventoryComponent inventory = charEntity.getComponent(InventoryComponent.class);
        EntityRef itemRef = inventory.itemSlots.get(event.getSlot());
        ItemComponent item = itemRef.getComponent(ItemComponent.class);

        String stackID = item.stackId;

        for (CollectBlocksTask task : tasks.keySet()) {
            // consider using InventoryUtils.isSameItem(EntityRef, EntityRef)
            if (stackID.equalsIgnoreCase(task.getItemId())) {
                Status prevStatus = task.getStatus();
                int amountChange = event.getNewSize() - event.getOldSize();
                task.setAmount(task.getAmount() + amountChange);
                Status status = task.getStatus();
                if (prevStatus != status && status.isComplete()) {
                    EntityRef client = charEntity.getOwner();
                    client.send(new TaskCompletedEvent(tasks.get(task), task, status.isSuccess()));
                    tasks.remove(task);
                }
            }
        }
    }
}
