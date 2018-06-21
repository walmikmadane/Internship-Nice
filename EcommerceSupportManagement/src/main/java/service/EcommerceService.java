package service;

import domain.OrderDetail;
import org.springframework.validation.annotation.Validated;

import java.security.PublicKey;
import java.util.List;

@Validated
public interface EcommerceService
{
    public List<String> getAllProducts(String category);
    public List<String> getOrders(String userid,String password);

    public String placeorder(OrderDetail orderDetail);
    public String updateorder(String orderid,OrderDetail orderDetail);
    public String deleteorder(String orderid,OrderDetail orderDetail);

}
