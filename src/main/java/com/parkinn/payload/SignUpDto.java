package com.parkinn.payload;


public class SignUpDto {
    private String name;
    private String email;
    private String password;
    private String confirm;
    private String phone;
    private String surname;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public String getConfirm() {
        return confirm;
    }
    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    
}