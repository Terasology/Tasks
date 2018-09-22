/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.tasks.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.terasology.engine.bootstrap.ClassMetaLibrary;
import org.terasology.persistence.typeHandling.DeserializationContext;
import org.terasology.persistence.typeHandling.DeserializationException;
import org.terasology.persistence.typeHandling.PersistedData;
import org.terasology.persistence.typeHandling.PersistedDataArray;
import org.terasology.persistence.typeHandling.PersistedDataMap;
import org.terasology.persistence.typeHandling.RegisterTypeHandler;
import org.terasology.persistence.typeHandling.SerializationContext;
import org.terasology.persistence.typeHandling.SerializationException;
import org.terasology.persistence.typeHandling.TypeHandler;
import org.terasology.tasks.ModifiableTask;
import org.terasology.tasks.Task;

import com.google.common.collect.Lists;

@RegisterTypeHandler
public class TaskTypeHandler implements TypeHandler<Task> {

    private final ClassMetaLibrary classLibrary;

    public TaskTypeHandler(ClassMetaLibrary classLibrary) {
        this.classLibrary = classLibrary;
    }

    @Override
    public PersistedData serialize(Task value, SerializationContext context) {
        throw new SerializationException("No type handler found for " + value);
    }

    @Override
    public ModifiableTask deserialize(PersistedData data, DeserializationContext context) {
        String typeId = data.getAsValueMap().getAsString("type");
        Iterable<Class<? extends ModifiableTask>> types = classLibrary.getSubtypesOf(ModifiableTask.class, typeId);
        Iterator<Class<? extends ModifiableTask>> it = types.iterator();
        if (!it.hasNext()) {
            throw new DeserializationException("Could not find class metadata for '" + typeId + "'");
        }
        Class<? extends ModifiableTask> type = it.next();
        if (it.hasNext()) {
            throw new DeserializationException("Ambiguous type: '" + typeId + "' - found " + types);
        }
        return context.deserializeAs(data, type);
    }

    @Override
    public PersistedData serializeCollection(Collection<Task> collection, SerializationContext context) {
        List<PersistedData> rawList = Lists.newArrayList();
        for (Task task : collection) {
            String type = task.getClass().getSimpleName();
            List<Task> deps = task.getDependencies();
            String[] depIds = new String[deps.size()];
            for (int i = 0; i < deps.size(); i++) {
                depIds[i] = deps.get(i).getId();
            }

            // create a map with general information
            Map<String, PersistedData> root = new HashMap<>();
            root.put("id", context.create(task.getId()));
            root.put("dependsOn", context.create(depIds));
            root.put("type", context.create(type));

            // enrich with task-specific data
            PersistedData custom = context.create(task, task.getClass());
            for (Entry<String, PersistedData> entry : custom.getAsValueMap().entrySet()) {
                root.put(entry.getKey(), entry.getValue());
            }
            rawList.add(context.create(root));
        }
        return context.create(rawList);
    }

    @Override
    public List<Task> deserializeCollection(PersistedData data, DeserializationContext context) {
        if (data.isArray()) {
            PersistedDataArray array = data.getAsArray();
            Map<String, ModifiableTask> result = new LinkedHashMap<>(array.size());

            // first deserialize the actual tasks based on their type
            for (PersistedData value : array) {
                String id = value.getAsValueMap().getAsString("id");
                result.put(id, deserialize(value, context));
            }

            // then deserialize the task dependencies and map the ids to instances
            for (PersistedData value : array) {
                PersistedDataMap valueMap = value.getAsValueMap();
                PersistedData depData = valueMap.get("dependsOn");
                if (depData != null) {
                    if (depData.isArray()) {
                        Iterable<PersistedData> deps = valueMap.getAsArray("dependsOn");
                        String id = valueMap.getAsString("id");
                        ModifiableTask task = result.get(id);
                        for (PersistedData dep : deps) {
                            ModifiableTask depTask = result.get(dep.getAsString());
                            if (depTask != null) {
                                task.addDependency(depTask);
                            }
                        }
                    } else {
                        throw new DeserializationException("'dependsOn' must be either a list or a single entry");
                    }
                }
            }
            return new ArrayList<>(result.values());
        }
        return new ArrayList<>();
    }

}
