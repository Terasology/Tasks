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

import org.terasology.tasks.components.QuestSourceComponent;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.physics.events.CollideEvent;
import org.terasology.engine.registry.In;
import org.terasology.tasks.components.QuestComponent;
import org.terasology.tasks.components.QuestListComponent;

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
