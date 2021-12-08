// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.systems;

import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.physics.events.CollideEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.Assets;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.tasks.components.QuestComponent;
import org.terasology.tasks.components.QuestListComponent;
import org.terasology.tasks.components.QuestSourceComponent;

/**
 * TODO Type description
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class QuestPointSystem extends BaseComponentSystem {

    @In
    private InventoryManager inventoryManager;

    @In
    private EntityManager entityManager;

    @ReceiveEvent(components = QuestListComponent.class)
    public void onCollision(CollideEvent event, EntityRef questPoint) {

        EntityRef charEnt = event.getOtherEntity();
        InventoryComponent inventory = charEnt.getComponent(InventoryComponent.class);
        if (inventory != null) {
            for (EntityRef itemRef : inventory.itemSlots) {
                QuestComponent quest = itemRef.getComponent(QuestComponent.class);
                if (quest != null) {
                    return;
                }
            }
        }

        QuestListComponent comp = questPoint.getComponent(QuestListComponent.class);
        for (String questItem : comp.questItems) {
            Prefab prefab = Assets.getPrefab(questItem).get();
            if (prefab.getComponent(ItemComponent.class) != null) {
                EntityRef item = entityManager.create(prefab);
                item.addComponent(new QuestSourceComponent(questPoint));
                inventoryManager.giveItem(charEnt, questPoint, item);
            }
        }
    }
}
