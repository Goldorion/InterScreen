package net.goldorion.interscreen.network.actions;

public record KeyBoardAction(int keycode, boolean isPressed, boolean isReleased) implements IAction {
}
