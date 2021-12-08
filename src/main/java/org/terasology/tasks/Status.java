// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

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
