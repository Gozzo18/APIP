package com.example.myapplication;

import android.app.Application;

public class GlobalVariables extends Application {

    //Define the variables to store for the whole application
    private boolean isBlind;
    private boolean isAlmostBlind;
    private boolean isColorBlind;
    private boolean isDeaf;
    private boolean isMute;
    private boolean isPhysicallyDisabled;

    //region Getters and Setters
    public boolean getBlind() {
        return isBlind;
    }

    public void setBlind(boolean blind) {
        isBlind = blind;
    }

    public boolean getAlmostBlind() {
        return isAlmostBlind;
    }

    public void setAlmostBlind(boolean almostBlind) {
        isAlmostBlind = almostBlind;
    }

    public boolean getColorBlind() {
        return isColorBlind;
    }

    public void setColorBlind(boolean colorBlind) {
        isColorBlind = colorBlind;
    }

    public boolean getDeaf() {
        return isDeaf;
    }

    public void setDeaf(boolean deaf) {
        isDeaf = deaf;
    }

    public boolean getMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public boolean getPhysicallyDisabled() {
        return isPhysicallyDisabled;
    }

    public void setPhysicallyDisabled(boolean physicallyDisabled) {
        isPhysicallyDisabled = physicallyDisabled;
    }
    //endregion
}
