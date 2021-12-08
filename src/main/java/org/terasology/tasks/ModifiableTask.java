// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks;

public abstract class ModifiableTask implements Task {
    private final String id;

    public ModifiableTask(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
