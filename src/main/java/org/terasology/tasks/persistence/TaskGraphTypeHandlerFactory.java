// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tasks.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.persistence.typeHandling.RegisterTypeHandlerFactory;
import org.terasology.persistence.typeHandling.TypeHandler;
import org.terasology.persistence.typeHandling.TypeHandlerContext;
import org.terasology.persistence.typeHandling.TypeHandlerFactory;
import org.terasology.persistence.typeHandling.TypeHandlerLibrary;
import org.terasology.reflection.TypeInfo;
import org.terasology.tasks.Task;
import org.terasology.tasks.TaskGraph;

import java.util.List;
import java.util.Optional;

@RegisterTypeHandlerFactory
public class TaskGraphTypeHandlerFactory implements TypeHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskGraphTypeHandlerFactory.class);

    @Override
    public <T> Optional<TypeHandler<T>> create(TypeInfo<T> typeInfo, TypeHandlerContext context) {
        if (!typeInfo.getRawType().equals(TaskGraph.class)) {
            return Optional.empty();
        }

        TypeHandlerLibrary library = context.getTypeHandlerLibrary();

        Optional<TypeHandler<List<String>>> stringListHandler =
            library.getTypeHandler(new TypeInfo<List<String>>() { });

        if (!stringListHandler.isPresent()) {
            LOGGER.error("No List<String> handler found");
            return Optional.empty();
        }

        TypeHandler<Task> taskTypeHandler = library.getBaseTypeHandler(TypeInfo.of(Task.class));

        TaskGraphTypeHandler typeHandler = new TaskGraphTypeHandler(taskTypeHandler,
            stringListHandler.get());

        return Optional.of((TypeHandler<T>) typeHandler);
    }
}
