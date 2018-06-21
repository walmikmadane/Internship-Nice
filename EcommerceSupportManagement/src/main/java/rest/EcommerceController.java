package rest;

import com.amazonaws.services.dynamodbv2.xspec.S;
import domain.OrderDetail;
import domain.entity.Products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.ProductsRepository;
import service.EcommerceService;
import service.impl.EcommerceServiceImpl;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/EcommerceSupport")
public class EcommerceController
{
    @Autowired
    EcommerceService ecommerceService;

    @CrossOrigin
    @GetMapping(value = "/getallproducts",produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAllProducts(@RequestParam(value = "categoryname",required = true)String categoryname)
    {
        List<String> str=ecommerceService.getAllProducts(categoryname);
        Iterator iterator=str.iterator();
        String response="";
        while(iterator.hasNext())
        {
            response=response+iterator.next();
        }

        return ResponseEntity.ok(response);
    }
    @CrossOrigin
    @GetMapping(value = "/getorders",produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String > getOrders(@RequestParam(value = "userid",required = true)String userid,@RequestParam(value="password",required = true)String password)
    {
        List<String> str=ecommerceService.getOrders(userid,password);
        Iterator iterator=str.iterator();
        String response="";
        while(iterator.hasNext())
        {
            response=response+iterator.next();
        }

        return ResponseEntity.ok(response);
    }
    @CrossOrigin
    @PostMapping(value = "/placeorder",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> placeorder(@RequestBody(required = true)OrderDetail orderDetail)
    {

        return ResponseEntity.ok( ecommerceService.placeorder(orderDetail));
    }
    @CrossOrigin
    @PutMapping(value = "/updateorder",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updateOrder(@RequestParam(value="orderid",required =true)String orderid,@RequestBody(required = true)OrderDetail orderDetail)
    {
        return ResponseEntity.ok(ecommerceService.updateorder(orderid,orderDetail));
    }
    @CrossOrigin
    @DeleteMapping(value = "/deleteorder",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deleteOrder(@RequestParam(value = "orderid",required = true)String orderid,@RequestBody(required = true)OrderDetail orderDetail)
    {
        return ResponseEntity.ok(ecommerceService.deleteorder(orderid,orderDetail));
    }

}
