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

import org.terasology.engine.Time;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;

/**
 * This task is successful until the given time runs out.
 */
public class TimeConstraintTask implements Task {

    private final float targetTime;
    private final Time time;

    /**
     * @param time the time provider
     * @param targetTime the game time, in seconds.
     */
    public TimeConstraintTask(Time time, float targetTime) {
        this.time = time;
        this.targetTime = targetTime;
    }

    @Override
    public String getShortName() {
        return "Time Constraint";
    }

    @Override
    public ItemIcon getIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        return String.format("Complete all tasks within %.0f seconds.", targetTime - time.getGameTime());
    }

    @Override
    public Status getStatus() {
        return time.getGameTime() < targetTime ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    public String toString() {
        return String.format("TimeConstraintTask [%.2f]", targetTime);
    }
}

