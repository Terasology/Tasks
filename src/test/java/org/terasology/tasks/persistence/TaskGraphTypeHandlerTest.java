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
package org.terasology.tasks.persistence;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Test;
import org.terasology.moduletestingenvironment.ModuleTestingEnvironment;
import org.terasology.persistence.typeHandling.TypeHandler;
import org.terasology.persistence.typeHandling.TypeHandlerLibrary;
import org.terasology.persistence.typeHandling.gson.GsonPersistedData;
import org.terasology.persistence.typeHandling.gson.GsonPersistedDataSerializer;
import org.terasology.tasks.CollectBlocksTask;
import org.terasology.tasks.GoToBeaconTask;
import org.terasology.tasks.Task;
import org.terasology.tasks.TaskGraph;
import org.terasology.tasks.TimeConstraintTask;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskGraphTypeHandlerTest extends ModuleTestingEnvironment {
    private static final GsonPersistedDataSerializer SERIALIZER = new GsonPersistedDataSerializer();
    private static final Gson GSON = new GsonBuilder().create();

    private static final String JSON =
        "[{\"class\":\"Tasks:CollectBlocksTask\",\"targetAmount\":50,\"itemId\":\"someOtherItem\",\"id\":\"collectB\",\"dependsOn\":[\"time\"]},{\"class\":\"Tasks:TimeConstraintTask\",\"targetTime\":15.0,\"id\":\"time\",\"dependsOn\":[\"collectA\",\"goTo\"]},{\"class\":\"Tasks:CollectBlocksTask\",\"targetAmount\":100,\"itemId\":\"someItem\",\"id\":\"collectA\",\"dependsOn\":[]},{\"class\":\"Tasks:GoToBeaconTask\",\"targetBeaconId\":\"beacon\",\"id\":\"goTo\",\"dependsOn\":[]}]";

    @Override
    public Set<String> getDependencies() {
        return Sets.newHashSet("Tasks");
    }

    private String serialize(TaskGraph action) {
        TypeHandler<TaskGraph> taskGraphTypeHandler = getHostContext().get(TypeHandlerLibrary.class)
                                                          .getTypeHandler(TaskGraph.class).get();

        GsonPersistedData serialized =
            (GsonPersistedData) taskGraphTypeHandler.serialize(action, SERIALIZER);
        return GSON.toJson(serialized.getElement());
    }

    private TaskGraph deserialize(String json) {
        TypeHandler<TaskGraph> taskGraphTypeHandler = getHostContext().get(TypeHandlerLibrary.class)
                                                          .getTypeHandler(TaskGraph.class).get();

        GsonPersistedData data = new GsonPersistedData(GSON.fromJson(json, JsonElement.class));
        return taskGraphTypeHandler.deserializeOrThrow(data);
    }

    @Test
    public void testSerialize() {
        TaskGraph graph = new TaskGraph();

        CollectBlocksTask collectBlocksTaskA =
            new CollectBlocksTask("collectA", 100, "someItem");

        CollectBlocksTask collectBlocksTaskB =
            new CollectBlocksTask("collectB", 50, "someOtherItem");

        TimeConstraintTask timeConstraintTask = new TimeConstraintTask("time", 15.0f);

        GoToBeaconTask goToBeaconTask = new GoToBeaconTask("goTo", "beacon");

        graph.add(collectBlocksTaskB);

        graph.addDependency(collectBlocksTaskB, timeConstraintTask);
        graph.addDependency(timeConstraintTask, collectBlocksTaskA);
        graph.addDependency(timeConstraintTask, goToBeaconTask);

        assertEquals(JSON, serialize(graph));
    }

    @Test
    public void testDeserialize() {
        TaskGraph graph = deserialize(JSON);

        assertEquals(Sets.newHashSet("goTo", "time", "collectA", "collectB"),
            tasksToIds(graph.getTasks()));

        Map<String, Set<String>> dependencyMap = Maps.newHashMap();

        for (Task task : graph) {
            dependencyMap.put(task.getId(), tasksToIds(graph.getDependencies(task)));
        }

        assertEquals(Sets.newHashSet("time"), dependencyMap.get("collectB"));
        assertEquals(Sets.newHashSet("collectA", "goTo"), dependencyMap.get("time"));
    }

    private Set<String> tasksToIds(Collection<Task> tasks) {
        return tasks.stream().map(Task::getId).collect(Collectors.toSet());
    }
}
