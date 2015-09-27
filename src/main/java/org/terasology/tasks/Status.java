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
     * Pending, not complete, not successful
     */
    PENDING(true, false, false),

    /**
     * Not queued, not complete, not successful
     */
    ACTIVE(false, false, false),

    /**
     * Not queued, complete and successful
     */
    SUCCEEDED(false, true, true),

    /**
     * Not queued, complete and not successful
     */
    FAILED(false, true, false);

    private final boolean complete;
    private final boolean success;
    private final boolean pending;

    Status(boolean pending, boolean complete, boolean success) {
        this.pending = pending;
        this.complete = complete;
        this.success = success;
    }

    /**
     * @return true, if the task is waiting for other tasks to complete
     */
    public boolean isPending() {
        return pending;
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
