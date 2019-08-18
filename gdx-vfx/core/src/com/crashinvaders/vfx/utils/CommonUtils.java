/*******************************************************************************
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import static com.badlogic.gdx.utils.Align.*;

public class CommonUtils {
    private static final Color tmpColor = new Color();

    public static <T> T random(T[] array) {
        int idx = MathUtils.random(array.length - 1);
        return array[idx];
    }

    /**
     * <p>Null safe comparison of Comparables.</p>
     *
     * @param <T> type of the values processed by this method
     * @param c1  the first comparable, may be null
     * @param c2  the second comparable, may be null
     * @param nullGreater if true {@code null} is considered greater
     *  than a non-{@code null} value or if false {@code null} is
     *  considered less than a Non-{@code null} value
     * @return a negative value if c1 &lt; c2, zero if c1 = c2
     *  and a positive value if c1 &gt; c2
     * @see java.util.Comparator#compare(Object, Object)
     */
    public static <T extends Comparable<? super T>> int compare(final T c1, final T c2, final boolean nullGreater) {
        if (c1 == c2) {
            return 0;
        } else if (c1 == null) {
            return nullGreater ? 1 : -1;
        } else if (c2 == null) {
            return nullGreater ? -1 : 1;
        }
        return c1.compareTo(c2);
    }

    /** This method actually replicate Integer.compare() to support Android API less than 19 */
    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /** This method actually replicate Float.compare() to support Android API less than 19 */
    public static int compare(float x, float y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /** This method actually replicate Boolean.compare() to support Android API less than 19 */
    public static int compare(boolean x, boolean y) {
        return (x == y) ? 0 : (x ? 1 : -1);
    }

    /** @return 0..1 depending on align value. */
    public static float getAlignFactorX(int align) {
        if ((align & left) != 0) return 0f;
        if ((align & right) != 0) return 1f;
        return 0.5f;
    }
    /** @return 0..1 depending on align value. */
    public static float getAlignFactorY(int align) {
        if ((align & bottom) != 0) return 0f;
        if ((align & top) != 0) return 1f;
        return 0.5f;
    }

    public static boolean stringEquals(String s0, String s1) {
        if (s0 == null && s1 == null) return true;
        return s0 != null && s0.equals(s1);
    }

    public static Color parseHexColor(String hexCode) {
        switch (hexCode.length()) {
            case 3:
                return parseHexColor3(hexCode);
            case 4:
                return parseHexColor4(hexCode);
            case 6:
                return parseHexColor6(hexCode);
            case 8:
                return parseHexColor8(hexCode);
            default:
                throw new IllegalArgumentException("Wrong format, HEX string value should be either 3, 4, 6 or 8 letters long.");
        }
    }

    public static Color parseHexColor3(String hex) {
        if (hex.length() != 3) throw new IllegalArgumentException("HEX string value should be exact 3 letters long.");
        int r = Integer.valueOf(hex.substring(0, 1), 16);
        int g = Integer.valueOf(hex.substring(1, 2), 16);
        int b = Integer.valueOf(hex.substring(2, 3), 16);
        return tmpColor.set(r / 15f, g / 15f, b / 15f, 1f);
    }

    public static Color parseHexColor4(String hex) {
        if (hex.length() != 4) throw new IllegalArgumentException("HEX string value should be exact 4 letters long.");
        int r = Integer.valueOf(hex.substring(0, 1), 16);
        int g = Integer.valueOf(hex.substring(1, 2), 16);
        int b = Integer.valueOf(hex.substring(2, 3), 16);
        int a = Integer.valueOf(hex.substring(3, 4), 16);
        return tmpColor.set(r / 15f, g / 15f, b / 15f, a / 15f);
    }

    public static Color parseHexColor6(String hex) {
        if (hex.length() != 6) throw new IllegalArgumentException("HEX string value should be exact 6 letters long.");
        int r = Integer.valueOf(hex.substring(0, 2), 16);
        int g = Integer.valueOf(hex.substring(2, 4), 16);
        int b = Integer.valueOf(hex.substring(4, 6), 16);
        return tmpColor.set(r / 255f, g / 255f, b / 255f, 1f);
    }

    public static Color parseHexColor8(String hex) {
        if (hex.length() != 8) throw new IllegalArgumentException("HEX string value should be exact 8 letters long.");
        int r = Integer.valueOf(hex.substring(0, 2), 16);
        int g = Integer.valueOf(hex.substring(2, 4), 16);
        int b = Integer.valueOf(hex.substring(4, 6), 16);
        int a = Integer.valueOf(hex.substring(6, 8), 16);
        return tmpColor.set(r / 255f, g / 255f, b / 255f, a / 255f);
    }
}
