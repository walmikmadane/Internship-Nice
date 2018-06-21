package domain;

import org.springframework.core.annotation.Order;

public class OrderDetail
{
    public String userid;
    public String productid;
    public int amount;
    public String password;

    public String getPassword() {
        return password;
    }
    OrderDetail(){}

    public OrderDetail(String userid, String productid, String pwd, int amount)
    {
        this.amount=amount;
        this.productid=productid;
        this.password=pwd;

        this.userid=userid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
