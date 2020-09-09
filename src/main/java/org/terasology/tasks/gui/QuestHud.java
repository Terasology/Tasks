// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tasks.gui;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.players.LocalPlayer;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.nui.widgets.UIList;
import org.terasology.tasks.Quest;
import org.terasology.tasks.systems.QuestSystem;

import java.util.ArrayList;
import java.util.List;

public class QuestHud extends CoreHudWidget {
    private UIList<Quest> questList;

    @In
    private QuestSystem questSystem;

    @In
    private LocalPlayer localPlayer;

    @Override
    public void initialise() {

//        UILabel title = find("listTitle", UILabel.class);

        questList = find("questList", UIList.class);
        if (questList != null) {
            questList.setItemRenderer(new QuestRenderer());
            questList.bindList(
                    new ReadOnlyBinding<List<Quest>>() {
                        @Override
                        public List<Quest> get() {
                            EntityRef clientEntity = localPlayer.getClientEntity();
                            List<Quest> quests = questSystem.getQuestsFor(clientEntity);
                            return new ArrayList<>(quests);
                        }
                    });
        }
    }
}

