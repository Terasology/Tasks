/*
 * Copyright 2015 MovingBlocks
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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.world.block.BlockUri;

public class FetchQuest implements Quest {

    private final int amount;
    private final BlockUri blockType;
    private final EntityRef target;

    public FetchQuest(int amount, BlockUri blockType, EntityRef target) {
        this.amount = amount;
        this.blockType = blockType;
        this.target = target;
    }

    @Override
    public String getShortName() {
        return "Fetch Blocks";
    }

    @Override
    public String getDescription() {
        return String.format("Fetch %d blocks of %s and return them to %s", amount, blockType, target);
    }

    @Override
    public QuestStatus getStatus() {
        return QuestStatus.PENDING;
    }
}

