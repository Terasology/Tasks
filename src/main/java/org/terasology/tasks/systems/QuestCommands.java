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

import static org.terasology.logic.permission.PermissionManager.CHEAT_PERMISSION;
import static org.terasology.logic.permission.PermissionManager.DEBUG_PERMISSION;
import static org.terasology.logic.permission.PermissionManager.NO_PERMISSION;

import java.util.Optional;

import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.registry.In;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Task;

/**
 *
 */
@RegisterSystem
public class QuestCommands extends BaseComponentSystem {

    @In
    private QuestSystem questSystem;

    @Command(shortDescription = "List all known quests", requiredPermission = DEBUG_PERMISSION)
    public String listQuests() {
        String result = "Quests:\n";
        for (Quest quest : questSystem.getQuests()) {
            result += quest.getShortName() + " (" + quest.getStatus() + ")\n";
            for (Task task : quest.getTaskGraph()) {
                result += "    " + task.toString() + '\n';
            }
        }
        return result;
    }

    @Command(shortDescription = "Abort the specified quest", requiredPermission = NO_PERMISSION)
    public String abortQuest(@CommandParam("shortName") String shortName) {
        return finishQuest(shortName, false) ? "Quest aborted" : "No such quest found";
    }

    @Command(shortDescription = "Finish the specified quest sucessfully", requiredPermission = CHEAT_PERMISSION)
    public String completeQuest(@CommandParam("shortName") String shortName) {
        return finishQuest(shortName, true) ? "Quest successfully completed" : "No such quest found";
    }

    private boolean finishQuest(String shortName, boolean success) {
        Optional<Quest> opt = questSystem.findQuest(shortName);
        if (opt.isPresent()) {
            questSystem.removeQuest(opt.get(), success);
            return true;
        }
        return false;
    }
}
