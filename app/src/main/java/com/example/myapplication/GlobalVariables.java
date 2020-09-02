package com.example.myapplication;

import java.io.Serializable;

public class GlobalVariables implements Serializable {
    private boolean isBlind;
    private boolean isDeaf;
    private boolean isMute;
    private boolean isColorBlind;
    private boolean isVisuallyImpaired;

    public GlobalVariables() {
        isBlind = false;
        isVisuallyImpaired = false;
        isColorBlind = false;
        isDeaf = false;
        isMute = false;
    }

    // Get and set methods
    public boolean getBlind() { return isBlind; }

    public void setBlind(boolean blind) { isBlind = blind; }

    public boolean getVisuallyImpaired() { return isVisuallyImpaired; }

    public void setVisuallyImpaired(boolean almostBlind) { isVisuallyImpaired = almostBlind; }

    public boolean getColorBlind() { return isColorBlind; }

    public void setColorBlind(boolean colorBlind) { isColorBlind = colorBlind; }

    public boolean getDeaf() { return isDeaf; }

    public void setDeaf(boolean deaf) { isDeaf = deaf; }

    public boolean getMute() { return isMute; }

    public void setMute(boolean mute) { isMute = mute; }

}
