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

package org.terasology.questing;

import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.In;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.console.Command;
import org.terasology.logic.console.Console;

@RegisterSystem
public class QuestingCommands implements ComponentSystem {

    @In
    private Console console;

    @Command(shortDescription = "Shows the active quest.")
    public void showActiveQuest() {
        if (QuestingCardFetchSystem.friendlyGoal != null) {
            console.addMessage("The goal of this quest is " + QuestingCardFetchSystem.friendlyGoal);
        } else {
            console.addMessage("No active quests! Get a card first.");
        }
    }

    @Override
    public void initialise() {
        // nothing to do
    }

    @Override
    public void shutdown() {
        // nothing to do
    }

}
