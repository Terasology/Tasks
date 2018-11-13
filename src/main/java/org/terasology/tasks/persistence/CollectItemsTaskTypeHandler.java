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

import java.util.Map;
import java.util.Optional;

import org.terasology.persistence.typeHandling.*;
import org.terasology.tasks.CollectBlocksTask;

import com.google.common.collect.ImmutableMap;

@RegisterTypeHandler
public class CollectItemsTaskTypeHandler extends TypeHandler<CollectBlocksTask> {

    @Override
    public PersistedData serializeNonNull(CollectBlocksTask task, PersistedDataSerializer context) {
        Map<String, PersistedData> data = ImmutableMap.of(
                "amount", context.serialize(task.getTargetAmount()),
                "itemId", context.serialize(task.getItemId()));

        return context.serialize(ImmutableMap.of(
                "data", context.serialize(data),
                "id", context.serialize(task.getId())));
    }

    @Override
    public Optional<CollectBlocksTask> deserialize(PersistedData data) {
        if (!data.isString()) {
            return Optional.empty();
        }
        PersistedDataMap root = data.getAsValueMap();
        String id = root.get("id").getAsString();
        PersistedDataMap taskData = root.get("data").getAsValueMap();
        return Optional.of(new CollectBlocksTask(id,
                taskData.get("amount").getAsInteger(),
                taskData.get("itemId").getAsString()));
    }

}
