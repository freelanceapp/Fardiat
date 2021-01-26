package com.fardiat.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultSettings implements Serializable {
    private boolean isLanguageSelected = false;
    private boolean showIntroSlider = true;
    private String ringToneUri;
    private String ringToneName;
    private List<String> recentSearchList = new ArrayList<>();

    public DefaultSettings() {
    }

    public boolean isLanguageSelected() {
        return isLanguageSelected;
    }

    public void setLanguageSelected(boolean languageSelected) {
        isLanguageSelected = languageSelected;
    }

    public boolean isShowIntroSlider() {
        return showIntroSlider;
    }

    public void setShowIntroSlider(boolean showIntroSlider) {
        this.showIntroSlider = showIntroSlider;
    }

    public List<String> getRecentSearchList() {
        return recentSearchList;
    }

    public void setRecentSearchList(List<String> recentSearchList) {
        this.recentSearchList = recentSearchList;
    }

    public String getRingToneUri() {
        return ringToneUri;
    }

    public void setRingToneUri(String ringToneUri) {
        this.ringToneUri = ringToneUri;
    }

    public String getRingToneName() {
        return ringToneName;
    }

    public void setRingToneName(String ringToneName) {
        this.ringToneName = ringToneName;
    }
}
