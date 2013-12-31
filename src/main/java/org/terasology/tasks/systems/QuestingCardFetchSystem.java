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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.CoreRegistry;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.inventory.events.ReceivedItemEvent;
import org.terasology.logic.manager.GUIManager;
import org.terasology.tasks.components.QuestingCardFetchComponent;
import org.terasology.tasks.events.ReachedBeaconEvent;
import org.terasology.tasks.gui.UIScreenQuest;
import org.terasology.tasks.utils.ModIcons;

/**
 * This controls the main logic of the quest, and defines what to do with a "quest card"
 * @author nh_99
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class QuestingCardFetchSystem implements ComponentSystem {
    public static String friendlyGoal;
    public static String questName;
    public static String returnTo;

    private static final Logger logger = LoggerFactory.getLogger(QuestingCardFetchSystem.class);

    private static String goal;
    private static String amount;
    private static Integer currentAmount = 1;
    private static boolean beaconReached;

    @Override
    public void initialise() {
        ModIcons.loadIcons();
    }

    @Override
    public void shutdown() {

    }

    @ReceiveEvent(components = {InventoryComponent.class}, priority = EventPriority.PRIORITY_HIGH)
    public void onReceiveItem(ReceivedItemEvent event, EntityRef entity) {
        ItemComponent item = event.getItem().getComponent(ItemComponent.class);

        // Make sure we have a valid item
        if (item == null) {
            logger.warn("Got an invalid item for entity {}", entity);
            return;
        }

        String stackID = item.stackId;

        if (goal != null) {
            if (stackID.equals(goal)) {
                Integer amounts = Integer.parseInt(amount);

                if (!currentAmount.equals(amounts)) { //The quest still needs some more to be complete
                    currentAmount += 1;
                } else { //The quest may be done
                    if (returnTo != null) {
                        UIScreenQuest.qGoal.setText("Return to " + returnTo);
                    } else {
                        resetQuest();

                        UIScreenQuest.qName.setText("Quest finished!");
                        UIScreenQuest.qGoal.setText(" ");

                        CoreRegistry.get(GUIManager.class).openWindow("journal");
                    }
                }
            }
        }
    }

    @ReceiveEvent
    public void onBeaconReached(ReachedBeaconEvent event, EntityRef entity) {
        if (event.getBeaconName().equals(returnTo)) {
            resetQuest();

            UIScreenQuest.qName.setText("Quest finished!");
            UIScreenQuest.qGoal.setText(" ");

            CoreRegistry.get(GUIManager.class).openWindow("journal");
        }
    }

    /**
     * This updates the quest card variables for tasks calls.
     */
    @ReceiveEvent(components = {QuestingCardFetchComponent.class})
    public void onActivate(ActivateEvent event, EntityRef entity) {
        QuestingCardFetchComponent questingCard = entity.getComponent(QuestingCardFetchComponent.class);

        createFetchQuest(questingCard.questName, questingCard.goal,
                questingCard.friendlyGoal, questingCard.amount, questingCard.returnTo);

        logger.info("Quest is now active! The quest is {}", questName);
    }

    /**
     * Run this when the quest is done. It nulls everything out so that a new quest can go in it's place.
     */
    public static void resetQuest() {
        questName = null;
        goal = null;
        friendlyGoal = null;
        amount = null;
        currentAmount = 1;
        returnTo = null;
    }

    /**
     * Create a quest
     * @param name is the name of the quest, not the one that people see though.
     * @param questGoal is the item that you need to get in the quest.
     * @param friendlyQuestGoal is the goal that people see.
     * @param amountToGet is how much to get, as a string.
     * @param playerReturnTo is the beacon that the player needs to return to for the quest to end.
     */
    public static void createFetchQuest(String name, String questGoal, String friendlyQuestGoal, String amountToGet, String playerReturnTo) {
        questName = name;
        goal = questGoal;
        friendlyGoal = friendlyQuestGoal;
        amount = amountToGet;
        returnTo = playerReturnTo;
    }
}
