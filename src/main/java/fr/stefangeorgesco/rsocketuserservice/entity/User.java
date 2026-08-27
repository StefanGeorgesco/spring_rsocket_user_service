package fr.stefangeorgesco.rsocketuserservice.entity;

import org.springframework.data.annotation.Id;

public class User {

    @Id
    private String id;
    private String name;
    private int balance;

    public User() {
    }

    public User(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
