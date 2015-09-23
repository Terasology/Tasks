import org.junit.Assert;
import org.junit.Test;
import org.terasology.tasks.DurationFormat;

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

/**
 *
 */
public class DurationFormatTest {

    @Test
    public void testSimple() {
        Assert.assertEquals("2h 3m 4s", DurationFormat.SHORT.format(2 * 3600 + 3 * 60 + 4));
        Assert.assertEquals("3m 4s", DurationFormat.SHORT.format(3 * 60 + 4));
        Assert.assertEquals("2h 0m 0s", DurationFormat.SHORT.format(2 * 3600 + 0 * 60 + 0));
        Assert.assertEquals("5h 0m 7s", DurationFormat.SHORT.format(5 * 3600 + 0 * 60 + 7));
        Assert.assertEquals("0s", DurationFormat.SHORT.format(0));
    }
}
