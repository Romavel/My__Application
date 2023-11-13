package pl.pollub.android.myapplication;

// User.java
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String country;
    private String phoneNumber;
    private String gender;
    private String registrationDate;

    // Pusty konstruktor
    public User() {
        // Pusty konstruktor wymagany przez Firebase
    }

    // Konstruktor
    public User(String id, String firstName, String lastName, String username, String email, String password,
                String country, String phoneNumber, String gender, String registrationDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.registrationDate = registrationDate;
    }

    // Gettery i settery dla wszystkich pól

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
}
