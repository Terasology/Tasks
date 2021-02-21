// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks;

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
