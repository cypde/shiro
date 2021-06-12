package com.cyp.shiro.pojo;


import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String username;
    private String password;
    private String roles;
    private String permissions;
    private String salt;

    public User(String username,String password){
        this.username = username;
        this.password = password;
    }

}
