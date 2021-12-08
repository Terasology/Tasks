// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.systems;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.network.ClientComponent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.events.InventorySlotChangedEvent;
import org.terasology.module.inventory.events.InventorySlotStackSizeChangedEvent;
import org.terasology.tasks.CollectBlocksTask;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Status;
import org.terasology.tasks.TaskGraph;
import org.terasology.tasks.events.StartTaskEvent;
import org.terasology.tasks.events.TaskCompletedEvent;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

@RegisterSystem(RegisterMode.AUTHORITY)
public class CollectBlocksTaskSystem extends BaseComponentSystem {

    private final Map<CollectBlocksTask, Quest> tasks = new LinkedHashMap<>();

    @ReceiveEvent(components = ClientComponent.class)
    public void onStartTask(StartTaskEvent event, EntityRef entity) {
        if (event.getTask() instanceof CollectBlocksTask) {
            CollectBlocksTask task = (CollectBlocksTask) event.getTask();
            EntityRef charEntity = entity.getComponent(ClientComponent.class).character;
            InventoryComponent inventory = charEntity.getComponent(InventoryComponent.class);
            int count = 0;
            for (EntityRef itemRef : inventory.itemSlots) {
                ItemComponent item = itemRef.getComponent(ItemComponent.class);
                if (item != null && item.stackId.equalsIgnoreCase(task.getItemId())) {
                    count += item.stackCount;
                }
            }
            task.setAmount(count);
            tasks.put(task, event.getQuest());
        }
    }

    @ReceiveEvent
    public void onCompletedTask(TaskCompletedEvent event, EntityRef entity) {
        tasks.remove(event.getTask());
    }

    @ReceiveEvent(components = InventoryComponent.class)
    public void onInventoryChange(InventorySlotChangedEvent event, EntityRef charEntity) {
        ItemComponent newItem = event.getNewItem().getComponent(ItemComponent.class);
        if (newItem != null) {
            onInventoryChange(charEntity, newItem.stackId, newItem.stackCount);
        }

        ItemComponent oldItem = event.getOldItem().getComponent(ItemComponent.class);
        if (oldItem != null) {
            onInventoryChange(charEntity, oldItem.stackId, -oldItem.stackCount);
        }
    }

    @ReceiveEvent(components = InventoryComponent.class)
    public void onInventoryChange(InventorySlotStackSizeChangedEvent event, EntityRef charEntity) {
        InventoryComponent inventory = charEntity.getComponent(InventoryComponent.class);
        EntityRef itemRef = inventory.itemSlots.get(event.getSlot());
        ItemComponent item = itemRef.getComponent(ItemComponent.class);

        int amountChange = event.getNewSize() - event.getOldSize();
        onInventoryChange(charEntity, item.stackId, amountChange);
    }

    private void onInventoryChange(EntityRef charEntity, String stackId, int amountChange) {

        Iterator<Entry<CollectBlocksTask, Quest>> it = tasks.entrySet().iterator();
        while (it.hasNext()) {
            Entry<CollectBlocksTask, Quest> entry = it.next();
            CollectBlocksTask task = entry.getKey();

            // consider using InventoryUtils.isSameItem(EntityRef, EntityRef)
            if (stackId.equalsIgnoreCase(task.getItemId())) {
                TaskGraph taskGraph = entry.getValue().getTaskGraph();

                Status prevStatus = taskGraph.getTaskStatus(task);

                task.setAmount(task.getAmount() + amountChange);

                Status status = taskGraph.getTaskStatus(task);
                if (prevStatus != status && status.isComplete()) {
                    it.remove();
                    EntityRef client = charEntity.getOwner();
                    client.send(new TaskCompletedEvent(entry.getValue(), task, status.isSuccess()));
                }
            }
        }

    }
}
