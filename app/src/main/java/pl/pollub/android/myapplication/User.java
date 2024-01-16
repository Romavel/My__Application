package pl.pollub.android.myapplication;

import com.google.firebase.Timestamp;

public class User {

    private String first_name;
    private String last_name;
    private String username;
    private String email;
    private String birth_date;
    private String country;
    private String illness;
    private String role;
    private String medication;
    private String phone_number;
    private String gender;
    //private String userId; // UID z Firebase Authentication
    private Timestamp registration_date; // Aktualna data i czas rejestracji

    // Konstruktor bezargumentowy (domyślny)
    public User() {
        // Pusty konstruktor potrzebny do używania Firebase Firestore
    }



    // Konstruktor z argumentami
    public User(String first_name, String last_name, String username, String email,
                String birth_date, String country, String phone_number, String gender,
                String medication, String role, String illness) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
        this.birth_date = birth_date;
        this.country = country;
        this.phone_number = phone_number;
        this.gender = gender;
        this.illness = illness;
        this.role = role;
        this.medication = medication;
    }

    // Getter i setter dla firstName
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    // Getter i setter dla lastName
    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
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
    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    // Getter i setter dla country
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    // Getter i setter dla phoneNumber
    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    // Getter i setter dla gender
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Getter i setter dla registrationDate
    public Timestamp getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(Timestamp registration_date) {
        this.registration_date = registration_date;
    }

    // Getter i setter dla illness
    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
    }

    // Getter i setter dla role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Getter i setter dla medication
    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }
}
