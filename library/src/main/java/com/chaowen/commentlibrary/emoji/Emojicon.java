/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chaowen.commentlibrary.emoji;

import java.io.Serializable;


public class Emojicon implements Serializable {
    private static final long serialVersionUID = 1L;
    private int icon;
    private char value;
    private String emoji;

    private Emojicon() {
    }

    public Emojicon(String emoji) {
        this.emoji = emoji;
    }

    public static Emojicon fromResource(int icon, int value) {
        Emojicon emoji = new Emojicon();
        emoji.icon = icon;
        emoji.value = (char) value;
        return emoji;
    }

    public static Emojicon fromCodePoint(int codePoint) {
        Emojicon emoji = new Emojicon();
        emoji.emoji = newString(codePoint);
        return emoji;
    }

    public static Emojicon fromChar(char ch) {
        Emojicon emoji = new Emojicon();
        emoji.emoji = Character.toString(ch);
        return emoji;
    }

    public static Emojicon fromChars(String chars) {
        Emojicon emoji = new Emojicon();
        emoji.emoji = chars;
        return emoji;
    }

    public static final String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }

    public char getValue() {
        return value;
    }

    public int getIcon() {
        return icon;
    }

    public String getEmoji() {
        return emoji;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Emojicon && emoji.equals(((Emojicon) o).emoji);
    }

    @Override
    public int hashCode() {
        return emoji.hashCode();
    }
}
