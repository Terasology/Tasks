/*
 * Copyright 2019 MovingBlocks
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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores a list of {@link Task Tasks} and their dependencies. The class implements
 * {@link Iterable} and iterates over all the tasks.
 */
public class TaskGraph implements Iterable<Task> {
    private final Map<Task, List<Task>> taskDependencies = new LinkedHashMap<>();

    public void add(Task task) {
        taskDependencies.put(task, new ArrayList<>());
    }

    /**
     * Add a {@link Task} as a dependency to {@code task}. If {@code dependency} is
     * not present in the {@link TaskGraph}, it is added.
     */
    public boolean addDependency(Task task, Task dependency) {
        List<Task> dependencies = getDependencies(task);

        if (dependencies == null) {
            return false;
        }

        if (!taskDependencies.containsKey(dependency)) {
            add(dependency);
        }

        return dependencies.add(dependency);
    }

    public List<Task> getDependencies(Task task) {
        return taskDependencies.get(task);
    }

    private Status getDependenciesStatus(Task task) {
        boolean stillPending = false;
        for (Task dependency : getDependencies(task)) {
            switch (getTaskStatus(dependency)) {
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

    /**
     * Gets the {@link Status} of a {@link Task} including the {@link Status} of its dependencies.
     */
    public Status getTaskStatus(Task task) {
        Status dependenciesStatus = getDependenciesStatus(task);
        if (dependenciesStatus == Status.SUCCEEDED) {
            return task.getStatus();
        }
        return dependenciesStatus;
    }

    /**
     * Returns an unmodifiable {@link Set} of all {@link Task Tasks} in this {@link TaskGraph}.
     */
    public Set<Task> getTasks() {
        return Collections.unmodifiableSet(taskDependencies.keySet());
    }

    @Override
    public Iterator<Task> iterator() {
        return getTasks().iterator();
    }
}
