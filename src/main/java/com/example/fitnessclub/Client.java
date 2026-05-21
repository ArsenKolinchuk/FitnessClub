package com.example.fitnessclub;

import java.sql.Date;

public class Client {
    private int id;
    private String fullName;
    private Date dateOfBirth;
    private String sex;
    private String phoneNumber;
    private String email;
    private Date registrationDate;

    // Конструктор
    public Client(int id, String fullName, Date dateOfBirth, String sex, String phoneNumber, String email, Date registrationDate) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    // Геттери
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public String getSex() { return sex; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public Date getRegistrationDate() { return registrationDate; }
}