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

import org.terasology.math.TeraMath;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;

/**
 * This task is successful until the given time runs out.
 */
public class TimeConstraintTask extends ModifiableTask {

    private final float targetTime;
    private transient final String targetTimeText;

    private transient float startTime = Float.NEGATIVE_INFINITY;
    private transient float currentTime;

    /**
     * @param targetTime the game time, in seconds.
     */
    public TimeConstraintTask(String id, float targetTime) {
        super(id);
        this.targetTime = targetTime;
        this.targetTimeText = DurationFormat.SHORT.formatCompact(TeraMath.floorToInt(targetTime));
    }

    @Override
    public ItemIcon getIcon() {
        return null;
    }

    /**
     * @param time the game time in seconds
     */
    public void startTimer(float time) {
        startTime = time;
    }

    /**
     * @param time the new game time in seconds
     */
    public void setTime(float time) {
        currentTime = time;
    }

    @Override
    public String getDescription() {
        float timeLeft = startTime + targetTime - currentTime;

        if (timeLeft <= 0) {
            return String.format("Complete within %s", targetTimeText);
        } else {
            String timeText = DurationFormat.SHORT.formatFull(TeraMath.floorToInt(timeLeft));
            return String.format("Complete within %s (%s)", timeText, targetTimeText);
        }
    }

    @Override
    public Status getStatus() {
        return currentTime < startTime + targetTime ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    public String toString() {
        return String.format("TimeConstraintTask [%s]", targetTimeText);
    }

    /**
     *
     */
    public float getTargetTime() {
        return targetTime;
    }
}

