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

import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;

public interface Task {

    String getDescription();

    /**
     * An optional icon
     * @return the icon or <code>null</code>
     */
    ItemIcon getIcon();

    /**
     * Returns the {@link Status} of this {@link Task}, not including its dependencies.
     * If you want the {@link Status} of this {@link Task} along with its dependencies,
     * see {@link TaskGraph#getTaskStatus(Task)}.
     */
    Status getStatus();

    /**
     * @return
     */
    String getId();
}
