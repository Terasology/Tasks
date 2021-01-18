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

import org.terasology.joml.geom.Rectanglei;
import org.joml.Vector2i;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.FontColor;
import org.terasology.nui.SubRegion;
import org.terasology.nui.TextLineBuilder;
import org.terasology.nui.asset.font.Font;
import org.terasology.nui.itemRendering.AbstractItemRenderer;
import org.terasology.nui.util.RectUtility;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.tasks.Quest;
import org.terasology.tasks.Status;
import org.terasology.tasks.Task;
import org.terasology.tasks.TaskGraph;
import org.terasology.utilities.Assets;

import java.util.List;

/**
 * Renders quest entries as part of a list item.
 */
public class QuestRenderer extends AbstractItemRenderer<Quest> {

    private TextureRegion questPending = Assets.getTextureRegion("Tasks:icons#QuestionMark").get();
    private TextureRegion questActive = Assets.getTextureRegion("Tasks:icons#ExclamationMark").get();
    private TextureRegion questSuccess = Assets.getTextureRegion("Tasks:icons#CheckMark").get();
    private TextureRegion questFailed = Assets.getTextureRegion("Tasks:icons#CrossMark").get();

    @Override
    public void draw(Quest quest, Canvas canvas) {
        Font font = canvas.getCurrentStyle().getFont();
        int lineHeight = font.getLineHeight();

        String title = getTitle(quest);
        int width = font.getWidth(title);
        canvas.drawText(title);


        Rectanglei questIconRect = RectUtility.createFromMinAndSize(width + 4, 0, lineHeight, lineHeight);
        TextureRegion questIcon = getIcon(quest.getStatus());
        canvas.drawTexture(questIcon, questIconRect);

        if (quest.getStatus() != Status.ACTIVE) {
            return;
        }

        // draw quest tasks only for active quests
        int maxWidth = canvas.getRegion().lengthX();
        int maxHeight = canvas.getRegion().lengthY();

        int y = lineHeight;
        TaskGraph taskGraph = quest.getTaskGraph();
        for (Task task : taskGraph) {
            // draw task text first
            String taskText = getTaskText(task);
            List<String> lines = TextLineBuilder.getLines(font, taskText, maxWidth);
            Rectanglei taskTextRect = new Rectanglei(20, y, maxWidth, maxHeight);

            Status taskStatus = taskGraph.getTaskStatus(task);

            if (taskStatus == Status.PENDING) {
                // TODO: add methods Canvas.drawText(String, Color)
                taskText = FontColor.getColored(taskText, Color.GREY);
            }
            canvas.drawText(taskText, taskTextRect);

            // draw status icon
            Rectanglei statusIconRect = RectUtility.expand(RectUtility.createFromMinAndSize(0, y, lineHeight, lineHeight), -2, -2);
            canvas.drawTexture(getIcon(taskStatus), statusIconRect);

            // draw task icon, if available
            int lastIdx = lines.size() - 1;
            String last = lines.get(lastIdx);
            y += lineHeight * lastIdx;
            Rectanglei taskIconRect = RectUtility.createFromMinAndSize(20 + font.getWidth(last) + 4, y, lineHeight, lineHeight);
            if (task.getIcon() != null) {
                try (SubRegion ignored = canvas.subRegion(taskIconRect, false)) {
                    task.getIcon().onDraw(canvas);
                }
            }
            y += lineHeight;
        }
    }

    @Override
    public Vector2i getPreferredSize(Quest value, Canvas canvas) {
        Font font = canvas.getCurrentStyle().getFont();
        String text = getTitle(value);

        // only tasks for active quests are explicitly listed
        if (value.getStatus() == Status.ACTIVE) {
            for (Task task : value.getTaskGraph()) {
                text += '\n';
                text += getTaskText(task);
            }
        }
        List<String> lines = TextLineBuilder.getLines(font, text, canvas.getRegion().lengthX());
        return font.getSize(lines);
    }


    private String getTitle(Quest quest) {
        return String.format("%s: %s", quest.getShortName(), quest.getDescription());
    }

    private String getTaskText(Task task) {
        return task.getDescription();
    }

    private TextureRegion getIcon(Status status) {
        switch (status) {
        case PENDING:
            return questPending;
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


