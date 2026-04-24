package com.tty7.gl.input.event;

public record WheelEvent(double xoffset, double yoffset) implements InputEvent {
}