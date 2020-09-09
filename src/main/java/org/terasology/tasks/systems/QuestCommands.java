// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks.systems;

import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.engine.registry.In;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Task;

import java.util.Optional;

import static org.terasology.engine.logic.permission.PermissionManager.CHEAT_PERMISSION;
import static org.terasology.engine.logic.permission.PermissionManager.DEBUG_PERMISSION;
import static org.terasology.engine.logic.permission.PermissionManager.NO_PERMISSION;

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
