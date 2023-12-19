package net.goldorion.interscreen.network.actions;

import java.awt.*;

public record MouseAction(Point position, int mouseButtons, int modifiers, int wheelAmt, boolean isPressed, boolean isReleased) implements IAction {
}