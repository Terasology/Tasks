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

import java.util.ArrayList;
import java.util.List;

public abstract class ModifiableTask implements Task {

    private final List<Task> dependencies = new ArrayList<>();

    public void addDependency(Task task) {
        this.dependencies.add(task);
    }

    protected Status getDependencyStatus() {
        boolean stillPending = false;
        for (Task task : dependencies) {
            switch (task.getStatus()) {
                case FAILED:
                    return Status.FAILED;
                case ACTIVE:
                case PENDING:
                    stillPending = true;
                    break;
                case SUCCEEDED:
                    break;
            }
        }
        if (stillPending) {
            return Status.PENDING;
        }
        return Status.SUCCEEDED;
    }

    protected boolean dependenciesSuccess() {
        for (Task task : dependencies) {
            if (task.getStatus() != Status.SUCCEEDED) {
                return false;
            }
        }
        return true;
    }
}
