// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationFormatTest {

    @Test
    public void testFull() {
        assertEquals("2h 3m 4s", DurationFormat.SHORT.formatFull(2 * 3600 + 3 * 60 + 4));
        assertEquals("3m 4s", DurationFormat.SHORT.formatFull(3 * 60 + 4));
        assertEquals("2h 0m 0s", DurationFormat.SHORT.formatFull(2 * 3600));
        assertEquals("5m 0s", DurationFormat.SHORT.formatFull(5 * 60));
        assertEquals("5h 0m 7s", DurationFormat.SHORT.formatFull(5 * 3600 + 7));
        assertEquals("0s", DurationFormat.SHORT.formatFull(0));
    }

    @Test
    public void testCompact() {
        assertEquals("2h 3m 4s", DurationFormat.SHORT.formatCompact(2 * 3600 + 3 * 60 + 4));
        assertEquals("3m 4s", DurationFormat.SHORT.formatCompact(3 * 60 + 4));
        assertEquals("2h", DurationFormat.SHORT.formatCompact(2 * 3600));
        assertEquals("5m", DurationFormat.SHORT.formatCompact(5 * 60));
        assertEquals("5h 7s", DurationFormat.SHORT.formatCompact(5 * 3600 + 7));
        assertEquals("0s", DurationFormat.SHORT.formatCompact(0));
    }
}
