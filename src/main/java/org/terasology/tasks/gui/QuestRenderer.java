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
package org.terasology.tasks.gui;

import java.util.List;

import org.terasology.asset.Assets;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.TextLineBuilder;
import org.terasology.rendering.nui.itemRendering.AbstractItemRenderer;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Status;
import org.terasology.tasks.Task;

/**
 * Renders quest entries as part of a list item.
 */
public class QuestRenderer extends AbstractItemRenderer<Quest> {

    private TextureRegion questActive = Assets.getTextureRegion("Tasks:icons#QuestionMark").get();
    private TextureRegion questSuccess = Assets.getTextureRegion("Tasks:icons#CheckMark").get();
    private TextureRegion questFailed = Assets.getTextureRegion("Tasks:icons#CrossMark").get();

    private int maxWidth = 280;

    @Override
    public void draw(Quest quest, Canvas canvas) {
        Font font = canvas.getCurrentStyle().getFont();
        int lineHeight = font.getLineHeight();

        String title = getTitle(quest);
        int width = font.getWidth(title);
        canvas.drawText(title);

        Rect2i questIconRect = Rect2i.createFromMinAndSize(width + 4, 0, lineHeight, lineHeight);
        TextureRegion questIcon = getIcon(quest.getStatus());
        canvas.drawTexture(questIcon, questIconRect);

        int y = lineHeight;
        for (Task task : quest.getAllTasks()) {
            String taskText = "+ " + getTaskText(task);
            List<String> lines = TextLineBuilder.getLines(font, taskText, maxWidth);
            Rect2i taskTextRect = Rect2i.createFromMinAndSize(0, y, maxWidth, canvas.getRegion().height() - y);
            canvas.drawText(taskText, taskTextRect);

            String ll = lines.get(lines.size() - 1);
            y += lineHeight * (lines.size() - 1);
            Rect2i taskIconRect = Rect2i.createFromMinAndSize(font.getWidth(ll) + 4, y, lineHeight, lineHeight);
            canvas.drawTexture(getIcon(task.getStatus()), taskIconRect);
            y += lineHeight;
        }
    }

    @Override
    public Vector2i getPreferredSize(Quest quest, Canvas canvas) {
        Font font = canvas.getCurrentStyle().getFont();
        String text = getTitle(quest);
        for (Task t : quest.getAllTasks()) {
            text += getTaskText(t) + '\n';
        }
        List<String> lines = TextLineBuilder.getLines(font, text, maxWidth);
        return font.getSize(lines).add(40, 0);
    }

    private String getTitle(Quest quest) {
        return String.format("%s: %s\n", quest.getShortName(), quest.getDescription());
    }

    private String getTaskText(Task task) {
        return task.getDescription();
    }

    private TextureRegion getIcon(Status status) {
        switch (status) {
        case ACTIVE:
            return questActive;
        case FAILED:
            return questFailed;
        case SUCCEEDED:
            return questSuccess;
        default:
            return Assets.getTextureRegion("engine:items#questionMark").get();
        }
    }
}


