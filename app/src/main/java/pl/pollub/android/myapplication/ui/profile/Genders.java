package pl.pollub.android.myapplication.ui.profile;

public enum Genders {
    MALE("male", "Mężczyzna"),
    FEMALE("female", "Kobieta"),
    RATHERNOTSAY("ratherNotSay", "Wolę nie podawać");

    private final String value;
    private final String description;

    Genders(String value, String description) {
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

