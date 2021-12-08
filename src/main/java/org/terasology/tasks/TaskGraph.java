// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
