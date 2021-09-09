// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tasks.gui;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.joml.geom.Rectanglef;

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
