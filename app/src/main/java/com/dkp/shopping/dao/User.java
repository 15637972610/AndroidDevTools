package com.dkp.shopping.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/10/25.
 */
@Entity
public class User {
    @Id(autoincrement = true)
    Long id;
    @Property
    String userName;//用户名
    @Property
    String passWord;//密码
    @Property
    String Sex;//性别
    @Property
    String phone;//号码
    @Generated(hash = 538195005)
    public User(Long id, String userName, String passWord, String Sex,
            String phone) {
        this.id = id;
        this.userName = userName;
        this.passWord = passWord;
        this.Sex = Sex;
        this.phone = phone;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassWord() {
        return this.passWord;
    }
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    public String getSex() {
        return this.Sex;
    }
    public void setSex(String Sex) {
        this.Sex = Sex;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
