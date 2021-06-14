package com.demo.entity;

/**
 * @author：rkc
 * @date：Created in 2021/6/14 9:46
 * @description：
 */
public class Address {

    private String home;
    private String school;
    private String company;

    @Override
    public String toString() {
        return "Address{" +
                "home='" + home + '\'' +
                ", school='" + school + '\'' +
                ", company='" + company + '\'' +
                '}';
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
