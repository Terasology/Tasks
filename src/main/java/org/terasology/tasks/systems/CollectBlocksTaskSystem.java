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

/**
 *
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class CollectBlocksTaskSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(CollectBlocksTaskSystem.class);

    @In
    private QuestSystem questSystem;

    @ReceiveEvent(components = {InventoryComponent.class})
    public void onInventoryChange(InventorySlotStackSizeChangedEvent event, EntityRef entity) {
        InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
        EntityRef itemRef = inventory.itemSlots.get(event.getSlot());
        ItemComponent item = itemRef.getComponent(ItemComponent.class);

        String stackID = item.stackId;

        for (Quest quest : questSystem.getActiveQuests()) {
            for (CollectBlocksTask task : quest.getTasks(CollectBlocksTask.class)) {
                // consider using InventoryUtils.isSameItem(EntityRef, EntityRef)
                if (stackID.equalsIgnoreCase(task.getItemId())) {
                    task.setAmount(item.stackCount);
                    logger.info("Task {} updated", task);
                }
            }
        }
    }
}
