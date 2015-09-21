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
package org.terasology.tasks;

import java.util.List;

import org.terasology.math.Border;
import org.terasology.math.geom.Vector2i;
import org.terasology.registry.In;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.ControlWidget;
import org.terasology.rendering.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.hud.CoreHudWidget;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.rendering.nui.widgets.TooltipLine;
import org.terasology.rendering.nui.widgets.UIBox;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIList;
import org.terasology.tasks.systems.QuestingCardFetchSystem;

public class QuestHud extends CoreHudWidget {
    private UIBox tooltipContainer;
    private UILabel blockName;
    private UILabel blockUri;
    private UIList<Quest> questList;
    private ItemIcon icon;

    @In
    private QuestingCardFetchSystem questSystem;

    @Override
    protected void initialise() {

        questList = find("questList", UIList.class);
        if (questList != null) {
            questList.setItemRenderer(new QuestRenderer());
            questList.bindList(
                    new ReadOnlyBinding<List<Quest>>() {
                        @Override
                        public List<Quest> get() {
                            return questSystem.getQuests();
                        }
                    });
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        Border border = canvas.getCurrentStyle().getBackgroundBorder();
        Vector2i size = getPreferredContentSize();
        int width = size.x + border.getTotalWidth();
        int height = size.y + border.getTotalHeight();
        return new Vector2i(width, height);
    }

    public Vector2i getPreferredContentSize() {
        return new Vector2i(320, 200);
    }
}

