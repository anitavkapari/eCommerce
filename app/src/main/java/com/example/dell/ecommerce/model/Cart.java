package com.example.dell.ecommerce.model;

public class Cart {
    private String pname, description, price,quantity,discount,pid;

    public Cart() {
    }

    public Cart(String pname, String description, String price,String pid, String quantity, String discount) {
        this.pname = pname;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.pid = pid;
        this.discount = discount;

    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
