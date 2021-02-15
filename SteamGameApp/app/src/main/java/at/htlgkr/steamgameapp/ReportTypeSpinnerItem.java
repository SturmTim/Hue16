package at.htlgkr.steamgameapp;

import at.htlgkr.steam.ReportType;

public class ReportTypeSpinnerItem {
    private final ReportType type;
    private final String displayText;

    public ReportTypeSpinnerItem(ReportType type, String displayText) {
        this.type = type;
        this.displayText = displayText;
    }

    public ReportType getType(){
        return type;
    }

    public String getDisplayText() {
        return displayText;
    }

    @Override
    public String toString() {
        return displayText;
    }
}