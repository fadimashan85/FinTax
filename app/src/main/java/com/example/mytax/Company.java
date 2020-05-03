

//Model class for company where person works



package com.example.mytax;

public class Company {
    private String companyName ;
    private String expectedTax;
    private String actualTax;
    private String salary;
    private String date;


    public Company(String companyName, String salary, String expectedTax, String actualTax,String date) {
        this.date=date;
        this.companyName = companyName;
        this.expectedTax = expectedTax;
        this.actualTax = actualTax;
        this.salary = salary;
    }




    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getExpectedTax() {
        return this.expectedTax;
    }

    public void setExpectedTax(String expectedTax) {
        this.expectedTax = expectedTax;
    }

    public String getActualTax() {
        return this.actualTax;
    }

    public void setActualTax(String actualTax) {
        this.actualTax = actualTax;
    }

    public String getSalary() {
        return this.salary;
    }

    public void setSalary(String salary) {
        this.salary= salary;
    }



}
