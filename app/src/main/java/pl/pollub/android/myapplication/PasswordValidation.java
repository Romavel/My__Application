package pl.pollub.android.myapplication;

public class PasswordValidation {
    private String password;
    public PasswordValidation(String password){
        this.password = password;
    }
    public boolean isLengthValid() {
        return password.length() >= 8;
    }

    public boolean hasUpperCaseLetter() {
        return !password.equals(password.toLowerCase());
    }

    public boolean hasDigit() {
        return password.matches(".*\\d.*");
    }

    public boolean hasSpecialChar() {
        return !password.matches("[A-Za-z0-9 ]*");
    }
    public boolean isPasswordValid() {
        // Tutaj umieść kod sprawdzający, czy hasło spełnia warunki
        // Zwróć true, jeśli spełnia, false w przeciwnym razie
        return isLengthValid() && hasUpperCaseLetter() && hasDigit() && hasSpecialChar();
    }
}