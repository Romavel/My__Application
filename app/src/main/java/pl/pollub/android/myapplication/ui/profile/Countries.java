package pl.pollub.android.myapplication.ui.profile;

public enum Countries {
    PL("pl", "Polska"),
    GB("gb", "Wielka Brytania"),
    US("us", "Stany Zjednoczone"),
    AUS("aus", "Australia"),
    CD("cd", "Kanada"),
    NZ("nz", "Nowa Zelandia"),
    RATHERNOTSAY("ratherNotSay", "Wolę nie podawać");

    private final String value;
    private final String description;

    Countries(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}

