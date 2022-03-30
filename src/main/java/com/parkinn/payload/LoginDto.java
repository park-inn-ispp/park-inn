package com.parkinn.payload;


public class LoginDto {
    private String nameOrEmail;
    private String password;
    public String getnameOrEmail() {
        return nameOrEmail;
    }
    public void setnameOrEmail(String nameOrEmail) {
        this.nameOrEmail = nameOrEmail;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    
}


