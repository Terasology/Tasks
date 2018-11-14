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
import java.util.*;
import java.util.Map.Entry;
import org.terasology.engine.bootstrap.ClassMetaLibrary;
import org.terasology.persistence.typeHandling.*;
import org.terasology.rendering.nui.layers.ingame.inventory.ItemIcon;
import org.terasology.tasks.ModifiableTask;
import org.terasology.tasks.Status;
import org.terasology.tasks.Task;
import com.google.common.collect.Lists;
@RegisterTypeHandler
public class TaskTypeHandler extends TypeHandler<Task> {
    private final ClassMetaLibrary classLibrary;
    public TaskTypeHandler(ClassMetaLibrary classLibrary) {
        this.classLibrary = classLibrary;
    }
    @Override
    public PersistedData serializeNonNull(Task value, PersistedDataSerializer context) {
        throw new SerializationException("No type handler found for " + value);
    }
    @Override
    public Optional<ModifiableTask> deserialize(PersistedData data) {
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
        return Optional.of(type);
    }
    @Override
    public PersistedData serializeCollection(Collection<Task> collection, PersistedDataSerializer context) {
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
            root.put("id", context.serialize(task.getId()));
            root.put("dependsOn", context.serialize(depIds));
            root.put("type", context.serialize(type));
            // enrich with task-specific data
            PersistedData custom = context.serialize(type);
            for (Entry<String, PersistedData> entry : custom.getAsValueMap().entrySet()) {
                root.put(entry.getKey(), entry.getValue());
            }
            rawList.add(context.serialize(root));
        }
        return context.serialize(rawList);
    }
    @Override
    public List<Task> deserializeCollection(PersistedData data) {
        if (data.isArray()) {
            PersistedDataArray array = data.getAsArray();
            Map<String, ModifiableTask> result = new LinkedHashMap<>(array.size());
            // first deserialize the actual tasks based on their type
            for (PersistedData value : array) {
                String id = value.getAsValueMap().getAsString("id");
                result.put(id, deserialize(value));
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