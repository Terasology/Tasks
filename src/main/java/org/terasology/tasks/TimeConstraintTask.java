// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks;

import org.terasology.inventory.rendering.nui.layers.ingame.ItemIcon;
import org.terasology.math.TeraMath;

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

