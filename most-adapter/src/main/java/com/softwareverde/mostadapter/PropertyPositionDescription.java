package com.softwareverde.mostadapter;

public class PropertyPositionDescription {
    public static final String DEFAULT_POSITION_Y = "0";

    private String positionX;
    private String positionY = DEFAULT_POSITION_Y;

    public String getPositionX() {
        return positionX;
    }

    public void setPositionX(String positionX) {
        this.positionX = positionX;
    }

    public String getPositionY() {
        return positionY;
    }

    public void setPositionY(String positionY) {
        this.positionY = positionY;
    }
}
