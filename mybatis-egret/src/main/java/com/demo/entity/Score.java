package com.demo.entity;

/**
 * @author：rkc
 * @date：Created in 2021/6/10 16:03
 * @description：
 */
public class Score {

    private int id;
    private int mybatis;
    private int spring;
    private int springmvc;
    private int userId;

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", mybatis=" + mybatis +
                ", spring=" + spring +
                ", springmvc=" + springmvc +
                ", userId=" + userId +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMybatis() {
        return mybatis;
    }

    public void setMybatis(int mybatis) {
        this.mybatis = mybatis;
    }

    public int getSpring() {
        return spring;
    }

    public void setSpring(int spring) {
        this.spring = spring;
    }

    public int getSpringmvc() {
        return springmvc;
    }

    public void setSpringmvc(int springmvc) {
        this.springmvc = springmvc;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
