// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tasks.persistence;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.persistence.typeHandling.PersistedData;
import org.terasology.persistence.typeHandling.PersistedDataMap;
import org.terasology.persistence.typeHandling.PersistedDataSerializer;
import org.terasology.persistence.typeHandling.TypeHandler;
import org.terasology.tasks.Task;
import org.terasology.tasks.TaskGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskGraphTypeHandler extends TypeHandler<TaskGraph> {
    private static final String DEPENDENCY_FIELD = "dependsOn";
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskGraphTypeHandler.class);

    private TypeHandler<Task> taskTypeHandler;
    private TypeHandler<List<String>> stringListHandler;

    public TaskGraphTypeHandler(TypeHandler<Task> taskTypeHandler, TypeHandler<List<String>> stringListHandler) {
        this.taskTypeHandler = taskTypeHandler;
        this.stringListHandler = stringListHandler;
    }

    @Override
    protected PersistedData serializeNonNull(TaskGraph taskGraph, PersistedDataSerializer serializer) {
        List<PersistedData> tasks = new ArrayList<>();

        for (Task task : taskGraph) {
            List<String> dependencies = taskGraph.getDependencies(task).stream()
                                            .map(Task::getId)
                                            .collect(Collectors.toList());

            PersistedData taskData = taskTypeHandler.serialize(task, serializer);

            Map<String, PersistedData> fullData = Maps.newLinkedHashMap();

            for (Map.Entry<String, PersistedData> entry : taskData.getAsValueMap().entrySet()) {
                fullData.put(entry.getKey(), entry.getValue());
            }

            fullData.put(DEPENDENCY_FIELD, stringListHandler.serialize(dependencies, serializer));

            tasks.add(serializer.serialize(fullData));
        }

        return serializer.serialize(tasks);
    }

    @Override
    public Optional<TaskGraph> deserialize(PersistedData data) {
        if (!data.isArray()) {
            return Optional.empty();
        }

        TaskGraph taskGraph = new TaskGraph();

        Map<String, Task> tasksByIds = Maps.newHashMap();
        Map<Task, List<String>> taskDependencyIds = Maps.newHashMap();

        // Deserialize tasks
        for (PersistedData fullTaskData : data.getAsArray()) {
            Map<String, PersistedData> taskData = Maps.newHashMap();

            for (Map.Entry<String, PersistedData> entry : fullTaskData.getAsValueMap().entrySet()) {
                taskData.put(entry.getKey(), entry.getValue());
            }

            List<String> dependencies =
                    Optional.ofNullable(taskData.remove(DEPENDENCY_FIELD))
                    .flatMap(stringListHandler::deserialize)
                    .orElse(Collections.emptyList());

            Optional<Task> taskOptional = taskTypeHandler.deserialize(PersistedDataMap.of(taskData));

            if (!taskOptional.isPresent()) {
                LOGGER.error("Could not deserialize a Task from {}", fullTaskData);
                continue;
            }

            Task task = taskOptional.get();

            taskGraph.add(task);
            tasksByIds.put(task.getId(), task);
            taskDependencyIds.put(task, dependencies);
        }

        // Resolve task dependencies
        for (Task task : taskGraph) {
            for (String dependencyId : taskDependencyIds.get(task)) {
                Task dependency = tasksByIds.get(dependencyId);

                if (dependency == null) {
                    LOGGER.error("Could not find dependency with ID {} in Task graph",
                        dependencyId);
                    continue;
                }

                taskGraph.addDependency(task, dependency);
            }
        }

        return Optional.of(taskGraph);
    }
}
