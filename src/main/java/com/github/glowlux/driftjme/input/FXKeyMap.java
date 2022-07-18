/*
This project incorporates work covered by the following copyright and permission notice:

       Copyright (c) 2009-2021 jMonkeyEngine
       All rights reserved.

       Redistribution and use in source and binary forms, with or without
       modification, are permitted provided that the following conditions are
       met:

       * Redistributions of source code must retain the above copyright
         notice, this list of conditions and the following disclaimer.

       * Redistributions in binary form must reproduce the above copyright
         notice, this list of conditions and the following disclaimer in the
         documentation and/or other materials provided with the distribution.

       * Neither the name of 'jMonkeyEngine' nor the names of its contributors
         may be used to endorse or promote products derived from this software
         without specific prior written permission.

       THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
       "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
       TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
       PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
       CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
       EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
       PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
       PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
       LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
       NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
       SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package com.github.glowlux.driftjme.input;

import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

import static com.jme3.input.KeyInput.*;

/**
 * <p>JavaFX Keycode mapping to JME keys.</p>
 * @implNote <b>JavaFX does not differentiate left and right keys. That is, LSHIFT and RSHIFT are both called when either shift button is pressed! (same for control)</b>
 *
 * @author glowlux
 */
public class FXKeyMap {

    private static final Map<KeyCode, Integer[]> JFX_TO_JME_KEY_MAP = new HashMap<>();

    private FXKeyMap(){}

    static {
        JFX_TO_JME_KEY_MAP.put(KeyCode.UNDEFINED, new Integer[]{KEY_UNKNOWN});
        JFX_TO_JME_KEY_MAP.put(KeyCode.ESCAPE, new Integer[]{KEY_ESCAPE});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT1, new Integer[]{KEY_1});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT2, new Integer[]{KEY_2});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT3, new Integer[]{KEY_3});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT4, new Integer[]{KEY_4});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT5, new Integer[]{KEY_5});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT6, new Integer[]{KEY_6});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT7, new Integer[]{KEY_7});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT8, new Integer[]{KEY_8});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT9, new Integer[]{KEY_9});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIGIT0, new Integer[]{KEY_0});
        JFX_TO_JME_KEY_MAP.put(KeyCode.MINUS, new Integer[]{KEY_MINUS});
        JFX_TO_JME_KEY_MAP.put(KeyCode.EQUALS, new Integer[]{KEY_EQUALS, KEY_NUMPADEQUALS});
        JFX_TO_JME_KEY_MAP.put(KeyCode.BACK_SPACE, new Integer[]{KEY_BACK});
        JFX_TO_JME_KEY_MAP.put(KeyCode.TAB, new Integer[]{KEY_TAB});
        JFX_TO_JME_KEY_MAP.put(KeyCode.Q, new Integer[]{KEY_Q});
        JFX_TO_JME_KEY_MAP.put(KeyCode.W, new Integer[]{KEY_W});
        JFX_TO_JME_KEY_MAP.put(KeyCode.E, new Integer[]{KEY_E});
        JFX_TO_JME_KEY_MAP.put(KeyCode.R, new Integer[]{KEY_R});
        JFX_TO_JME_KEY_MAP.put(KeyCode.T, new Integer[]{KEY_T});
        JFX_TO_JME_KEY_MAP.put(KeyCode.Y, new Integer[]{KEY_Y});
        JFX_TO_JME_KEY_MAP.put(KeyCode.U, new Integer[]{KEY_U});
        JFX_TO_JME_KEY_MAP.put(KeyCode.I, new Integer[]{KEY_I});
        JFX_TO_JME_KEY_MAP.put(KeyCode.O, new Integer[]{KEY_O});
        JFX_TO_JME_KEY_MAP.put(KeyCode.P, new Integer[]{KEY_P});
        JFX_TO_JME_KEY_MAP.put(KeyCode.OPEN_BRACKET, new Integer[]{KEY_LBRACKET});
        JFX_TO_JME_KEY_MAP.put(KeyCode.CLOSE_BRACKET, new Integer[]{KEY_RBRACKET});
        JFX_TO_JME_KEY_MAP.put(KeyCode.CONTROL, new Integer[]{KEY_LCONTROL, KEY_RCONTROL});
        JFX_TO_JME_KEY_MAP.put(KeyCode.A, new Integer[]{KEY_A});
        JFX_TO_JME_KEY_MAP.put(KeyCode.S, new Integer[]{KEY_S});
        JFX_TO_JME_KEY_MAP.put(KeyCode.D, new Integer[]{KEY_D});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F, new Integer[]{KEY_F});
        JFX_TO_JME_KEY_MAP.put(KeyCode.G, new Integer[]{KEY_G});
        JFX_TO_JME_KEY_MAP.put(KeyCode.H, new Integer[]{KEY_H});
        JFX_TO_JME_KEY_MAP.put(KeyCode.J, new Integer[]{KEY_J});
        JFX_TO_JME_KEY_MAP.put(KeyCode.K, new Integer[]{KEY_K});
        JFX_TO_JME_KEY_MAP.put(KeyCode.L, new Integer[]{KEY_L});
        JFX_TO_JME_KEY_MAP.put(KeyCode.SEMICOLON, new Integer[]{KEY_SEMICOLON});
        JFX_TO_JME_KEY_MAP.put(KeyCode.QUOTE, new Integer[]{KEY_APOSTROPHE});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DEAD_GRAVE, new Integer[]{KEY_GRAVE});
        JFX_TO_JME_KEY_MAP.put(KeyCode.SHIFT, new Integer[]{KEY_LSHIFT, KEY_RSHIFT});
        JFX_TO_JME_KEY_MAP.put(KeyCode.BACK_SLASH, new Integer[]{KEY_BACKSLASH});
        JFX_TO_JME_KEY_MAP.put(KeyCode.Z, new Integer[]{KEY_Z});
        JFX_TO_JME_KEY_MAP.put(KeyCode.X, new Integer[]{KEY_X});
        JFX_TO_JME_KEY_MAP.put(KeyCode.C, new Integer[]{KEY_C});
        JFX_TO_JME_KEY_MAP.put(KeyCode.V, new Integer[]{KEY_V});
        JFX_TO_JME_KEY_MAP.put(KeyCode.B, new Integer[]{KEY_B});
        JFX_TO_JME_KEY_MAP.put(KeyCode.N, new Integer[]{KEY_N});
        JFX_TO_JME_KEY_MAP.put(KeyCode.M, new Integer[]{KEY_M});
        JFX_TO_JME_KEY_MAP.put(KeyCode.PERIOD, new Integer[]{KEY_PERIOD});
        JFX_TO_JME_KEY_MAP.put(KeyCode.SLASH, new Integer[]{KEY_SLASH});
        JFX_TO_JME_KEY_MAP.put(KeyCode.MULTIPLY, new Integer[]{KEY_MULTIPLY});
        JFX_TO_JME_KEY_MAP.put(KeyCode.CONTEXT_MENU, new Integer[]{KEY_LMENU, KEY_RMENU});
        JFX_TO_JME_KEY_MAP.put(KeyCode.SPACE, new Integer[]{KEY_SPACE});
        JFX_TO_JME_KEY_MAP.put(KeyCode.CAPS, new Integer[]{KEY_CAPITAL});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F1, new Integer[]{KEY_F1});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F2, new Integer[]{KEY_F2});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F3, new Integer[]{KEY_F3});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F4, new Integer[]{KEY_F4});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F5, new Integer[]{KEY_F5});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F6, new Integer[]{KEY_F6});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F7, new Integer[]{KEY_F7});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F8, new Integer[]{KEY_F8});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F9, new Integer[]{KEY_F9});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F10, new Integer[]{KEY_F10});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUM_LOCK, new Integer[]{KEY_NUMLOCK});
        JFX_TO_JME_KEY_MAP.put(KeyCode.SCROLL_LOCK, new Integer[]{KEY_SCROLL});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD7, new Integer[]{KEY_NUMPAD7});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD8, new Integer[]{KEY_NUMPAD8});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD9, new Integer[]{KEY_NUMPAD9});
        JFX_TO_JME_KEY_MAP.put(KeyCode.SUBTRACT, new Integer[]{KEY_SUBTRACT});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD4, new Integer[]{KEY_NUMPAD4});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD5, new Integer[]{KEY_NUMPAD5});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD6, new Integer[]{KEY_NUMPAD6});
        JFX_TO_JME_KEY_MAP.put(KeyCode.ADD, new Integer[]{KEY_ADD});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD1, new Integer[]{KEY_NUMPAD1});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD2, new Integer[]{KEY_NUMPAD2});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD3, new Integer[]{KEY_NUMPAD3});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NUMPAD0, new Integer[]{KEY_NUMPAD0});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DECIMAL, new Integer[]{KEY_DECIMAL});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F11, new Integer[]{KEY_F11});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F12, new Integer[]{KEY_F12});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F13, new Integer[]{KEY_F13});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F14, new Integer[]{KEY_F14});
        JFX_TO_JME_KEY_MAP.put(KeyCode.F15, new Integer[]{KEY_F15});
        JFX_TO_JME_KEY_MAP.put(KeyCode.KANA, new Integer[]{KEY_KANA});
        JFX_TO_JME_KEY_MAP.put(KeyCode.CONVERT, new Integer[]{KEY_CONVERT});
        JFX_TO_JME_KEY_MAP.put(KeyCode.NONCONVERT, new Integer[]{KEY_NOCONVERT});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DOLLAR, new Integer[]{KEY_YEN});
        JFX_TO_JME_KEY_MAP.put(KeyCode.CIRCUMFLEX, new Integer[]{KEY_CIRCUMFLEX});
        JFX_TO_JME_KEY_MAP.put(KeyCode.AT, new Integer[]{KEY_AT});
        JFX_TO_JME_KEY_MAP.put(KeyCode.COLON, new Integer[]{KEY_COLON});
        JFX_TO_JME_KEY_MAP.put(KeyCode.UNDERSCORE, new Integer[]{KEY_UNDERLINE});
        JFX_TO_JME_KEY_MAP.put(KeyCode.KANJI, new Integer[]{KEY_KANJI});
        JFX_TO_JME_KEY_MAP.put(KeyCode.STOP, new Integer[]{KEY_STOP});
        JFX_TO_JME_KEY_MAP.put(KeyCode.PRINTSCREEN, new Integer[]{KEY_PRTSCR});
        JFX_TO_JME_KEY_MAP.put(KeyCode.ENTER, new Integer[]{KEY_NUMPADENTER, KEY_RETURN});
        JFX_TO_JME_KEY_MAP.put(KeyCode.COMMA, new Integer[]{KEY_NUMPADCOMMA, KEY_COMMA});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DIVIDE, new Integer[]{KEY_DIVIDE});
        JFX_TO_JME_KEY_MAP.put(KeyCode.PAUSE, new Integer[]{KEY_PAUSE});
        JFX_TO_JME_KEY_MAP.put(KeyCode.HOME, new Integer[]{KEY_HOME});
        JFX_TO_JME_KEY_MAP.put(KeyCode.UP, new Integer[]{KEY_UP});
        JFX_TO_JME_KEY_MAP.put(KeyCode.PAGE_UP, new Integer[]{KEY_PGUP});
        JFX_TO_JME_KEY_MAP.put(KeyCode.LEFT, new Integer[]{KEY_LEFT});
        JFX_TO_JME_KEY_MAP.put(KeyCode.RIGHT, new Integer[]{KEY_RIGHT});
        JFX_TO_JME_KEY_MAP.put(KeyCode.END, new Integer[]{KEY_END});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DOWN, new Integer[]{KEY_DOWN});
        JFX_TO_JME_KEY_MAP.put(KeyCode.PAGE_DOWN, new Integer[]{KEY_PGDN});
        JFX_TO_JME_KEY_MAP.put(KeyCode.INSERT, new Integer[]{KEY_INSERT});
        JFX_TO_JME_KEY_MAP.put(KeyCode.DELETE, new Integer[]{KEY_DELETE});
        JFX_TO_JME_KEY_MAP.put(KeyCode.META, new Integer[]{KEY_LMETA, KEY_RMETA});
        JFX_TO_JME_KEY_MAP.put(KeyCode.POWER, new Integer[]{KEY_POWER});
    }

    /**
     * @param jfxKey the javafx keycode
     * @return a list of the jme keycodes that match the javafx keycode.
     * @implNote JavaFX does not differentiate between left and right keys, thus {@link KeyCode#SHIFT} matches {KEY_LSHIFT, KEY_RSHIFT}.
     */
    public static Integer[] toJmeKeyCodes(final KeyCode jfxKey) {
        Integer[] keyCodes = JFX_TO_JME_KEY_MAP.get(jfxKey);
        return (keyCodes == null) ? new Integer[]{} : keyCodes;
    }

    public static KeyCode fromJmeKeyCode(final int searchKey) {
        for (Map.Entry<KeyCode, Integer[]> mapping : JFX_TO_JME_KEY_MAP.entrySet()) {
            for (int jmeKey : mapping.getValue()) {
                if (jmeKey == searchKey) return mapping.getKey();
            }
        }
        return KeyCode.UNDEFINED;
    }
}
