/*
 * Copyright 2013 MovingBlocks
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

import java.io.File;
import java.io.FileReader;

import org.junit.Test;
import org.terasology.tasks.components.QuestComponent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;

/**
 *
 */
public class GsonListTest {

    @Test
    public void testSerialization() throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TypeAdapter<QuestComponent> qa = gson.getAdapter(QuestComponent.class);
        try (FileReader reader = new FileReader(new File("assets/prefabs/QuestCard.prefab"))) {
            JsonElement root = gson.fromJson(reader, JsonElement.class);
            QuestComponent quest = qa.fromJsonTree(root.getAsJsonObject().get("Quest"));
            System.out.println(gson.toJson(quest));
        }
    }
}
