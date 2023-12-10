package pl.pollub.android.myapplication;

public class PasswordValidation {
    public String password;
    public boolean isPasswordValid(String password) {
        // Tutaj umieść kod sprawdzający, czy hasło spełnia warunki
        // Zwróć true, jeśli spełnia, false w przeciwnym razie
        return isLengthValid(password) && hasUpperCaseLetter(password) && hasDigit(password) && hasSpecialChar(password);
    }

    public boolean isLengthValid(String password) {
        return password.length() >= 8;
    }

    public boolean hasUpperCaseLetter(String password) {
        return !password.equals(password.toLowerCase());
    }

    public boolean hasDigit(String password) {
        return password.matches(".*\\d.*");
    }

    public boolean hasSpecialChar(String password) {
        return !password.matches("[A-Za-z0-9 ]*");
    }

}
