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

import org.terasology.engine.bootstrap.ClassMetaLibrary;
import org.terasology.persistence.typeHandling.DeserializationContext;
import org.terasology.persistence.typeHandling.DeserializationException;
import org.terasology.persistence.typeHandling.PersistedData;
import org.terasology.persistence.typeHandling.RegisterTypeHandler;
import org.terasology.persistence.typeHandling.SerializationContext;
import org.terasology.persistence.typeHandling.SerializationException;
import org.terasology.persistence.typeHandling.TypeHandler;
import org.terasology.tasks.ModifiableTask;
import org.terasology.tasks.Task;

import java.util.Iterator;

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
}
