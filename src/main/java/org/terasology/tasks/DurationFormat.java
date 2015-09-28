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

/**
 *
 */
public enum DurationFormat {

    SHORT("h", "m", "s");

    private String hs;
    private String ms;
    private String ss;

    private DurationFormat(String hs, String ms, String ss) {
        this.hs = hs;
        this.ms = ms;
        this.ss = ss;
    }

    /**
     * @param seconds the number of seconds
     * @return the text representation in the format HH MM SS
     */
    public String formatFull(int seconds) {
        StringBuilder sb = new StringBuilder();
        int rest = seconds;
        int hours = rest / 3600;
        rest = rest % 3600;
        if (hours > 0) {
            sb.append(hours);
            sb.append(hs);
            sb.append(' ');
        }
        int mins = rest / 60;
        rest = rest % 60;
        if (hours > 0 || mins > 0) {
            sb.append(mins);
            sb.append(ms);
            sb.append(' ');
        }

        sb.append(rest);
        sb.append(ss);

        return sb.toString();
    }

    /**
     * @param seconds the number of seconds
     * @return the text representation in the shortest format
     */
    public String formatCompact(int seconds) {
        StringBuilder sb = new StringBuilder();
        int rest = seconds;
        int hours = rest / 3600;
        rest = rest % 3600;
        if (hours > 0) {
            sb.append(hours);
            sb.append(hs);
        }
        int mins = rest / 60;
        rest = rest % 60;
        if (mins > 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(mins);
            sb.append(ms);
        }

        if (rest > 0 || sb.length() == 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(rest);
            sb.append(ss);
        }

        return sb.toString();
    }

}
