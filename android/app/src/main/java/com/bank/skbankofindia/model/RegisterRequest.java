package com.bank.skbankofindia.model;

public class RegisterRequest {
    private String username;
    private String email;
    private String phone;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String gender;
    private String pan;
    private String aadhaar;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String pincode;

    public RegisterRequest(String username, String email, String phone, String password,
                           String firstName, String middleName, String lastName, String dob,
                           String gender, String pan, String aadhaar, String address1,
                           String address2, String city, String state, String pincode) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName != null ? middleName : "";
        this.lastName = lastName;
        this.dob = dob != null && !dob.isEmpty() ? dob : "1998-05-15";
        this.gender = gender != null ? gender : "MALE";
        this.pan = pan != null ? pan : "";
        this.aadhaar = aadhaar;
        this.address1 = address1;
        this.address2 = address2 != null ? address2 : "";
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

    // Getters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getPan() { return pan; }
    public String getAadhaar() { return aadhaar; }
    public String getAddress1() { return address1; }
    public String getAddress2() { return address2; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }
}
