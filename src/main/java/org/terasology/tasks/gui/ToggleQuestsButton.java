// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.tasks.gui;

import org.terasology.engine.input.BindButtonEvent;
import org.terasology.engine.input.DefaultBinding;
import org.terasology.engine.input.RegisterBindButton;
import org.terasology.nui.input.InputType;
import org.terasology.nui.input.Keyboard;

/**
 * Open/Close Quest HUD using this key
 */
@RegisterBindButton(id = "toggleQuests", description = "Open/Close Quest HUD")
@DefaultBinding(type = InputType.KEY, id = Keyboard.KeyId.K)
public class ToggleQuestsButton extends BindButtonEvent {
    // annotations suffice
}
