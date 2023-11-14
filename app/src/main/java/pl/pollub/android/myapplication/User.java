package pl.pollub.android.myapplication;
public class User {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String birthDate;
    private String country;
    private String phoneNumber;
    private String gender;
    private String userId; // UID z Firebase Authentication
    private String registrationDate; // Aktualna data i czas rejestracji

    // Konstruktor bezargumentowy (domyślny)
    public User() {
        // Pusty konstruktor potrzebny do używania Firebase Firestore
    }

    // Konstruktor z argumentami
    public User(String firstName, String lastName, String username, String email,
                String birthDate, String country, String phoneNumber, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.birthDate = birthDate;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    // Getter i setter dla firstName
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Getter i setter dla lastName
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Getter i setter dla username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter i setter dla email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter i setter dla birthDate
    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    // Getter i setter dla country
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    // Getter i setter dla phoneNumber
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Getter i setter dla gender
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Getter i setter dla userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter i setter dla registrationDate
    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
}
