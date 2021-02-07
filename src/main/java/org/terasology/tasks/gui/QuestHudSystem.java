// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tasks.gui;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.joml.geom.Rectanglef;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;

@RegisterSystem(RegisterMode.CLIENT)
public class QuestHudSystem extends BaseComponentSystem {

    public static final String HUD_ELEMENT_ID = "Tasks:QuestHud";

    @In
    private NUIManager nuiManager;

    private QuestHud questHud;

    @Override
    public void initialise() {
        Rectanglef rc = new Rectanglef(0, 0, 1, 1);
        questHud = nuiManager.getHUD().addHUDElement(HUD_ELEMENT_ID, QuestHud.class, rc);
    }

    @ReceiveEvent
    public void onToggleMinimapButton(ToggleQuestsButton event, EntityRef entity) {
        if (event.isDown()) {
            questHud.setVisible(!questHud.isVisible());
            event.consume();
        }
    }
}
