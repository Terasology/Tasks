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

public enum Status {

    /**
     * The task is waiting for other tasks to complete first - not complete, not successful
     */
    PENDING(false, false),

    /**
     * Not complete, not successful
     */
    ACTIVE(false, false),

    /**
     * Complete and successful
     */
    SUCCEEDED(true, true),

    /**
     * Not queued, complete and not successful
     */
    FAILED(true, false);

    private final boolean complete;
    private final boolean success;

    Status(boolean complete, boolean success) {
        this.complete = complete;
        this.success = success;
    }

    /**
     * @return if the entire quest is complete (successful or not)
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * @return if the quest was successfully completed (implies isComplete() == true)
     */
    public boolean isSuccess() {
        return success;
    }
}
