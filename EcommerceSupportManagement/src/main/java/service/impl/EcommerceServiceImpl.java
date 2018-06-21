package service.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import domain.OrderDetail;
import domain.entity.Products;
import example.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import repository.ProductsRepository;
import service.EcommerceService;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EcommerceServiceImpl implements EcommerceService
{

    public static void main(String[] args) {
        EcommerceServiceImpl ecommerceService=new EcommerceServiceImpl();
    String list=  ecommerceService.placeorder(new OrderDetail("walmikmadane@gmail.com","1","82564",1));
       // ecommerceService.getOrders("walmikmadane@gmail.com","825645");
        System.out.println(list);
    }

    @Override
    public List<String> getAllProducts(String category)
    {
        List<String> list=new ArrayList<String>();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("products");


        Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
        expressionAttributeValues.put(":category", category);
        ItemCollection<ScanOutcome> items = table.scan("categoryname = :category", // FilterExpression
                "productid, description,pname,price", // ProjectionExpression
                null, // ExpressionAttributeNames - not used in this example
                expressionAttributeValues);
        Iterator<Item> iterator = items.iterator();
        if(iterator.hasNext())
        {

            while (iterator.hasNext()) {
                Item item=iterator.next();
                String  str="productId:"+item.getString("productid")+"\nName:"+item.getString("pname")+"\nprice:"+item.getInt("price")+"\n"+item.getString("description")+"\n------------------------------------------------------------\n";
                list.add(str);
                System.out.println(str);

            }
        }
        else
        {
            list.add("No Products Available right now!!");
        }

        return  list;

    }

    @Override
    public List<String> getOrders(String userid, String password)
    {
        List<String> list=new ArrayList<String>();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("User");
        Item item=table.getItem("userid",userid);
        if(item!=null)
        {

            if (item.getString("password").equals(password))
            {
                table=dynamoDB.getTable("orders");

                Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
                expressionAttributeValues.put(":user", userid);
                ItemCollection<ScanOutcome> items = table.scan("userid = :user", // FilterExpression
                        "orderid, productid,amount,cost,orderday,ordermonth,orderyear", // ProjectionExpression
                        null, // ExpressionAttributeNames - not used in this example
                        expressionAttributeValues);

                Iterator<Item> iterator = items.iterator();
                if(iterator.hasNext())
                {
                   // System.out.println("walmik");

                    while (iterator.hasNext()) {
                        item=iterator.next();
                        String  str="OrderId:"+item.getString("orderid")
                                +"\nProductId:"+item.getString("productid")
                                +"\nAmount:"+item.getString("amount")
                                +"\nPrice:"+item.getString("cost")
                                +"\nOrder Date:"+item.getInt("orderday")+"/"+item.getInt("ordermonth")+"/"+item.getInt("orderyear")
                                +"\n------------------------------------------------------------\n";
                        list.add(str);
                        System.out.println(str);

                    }
                }
                else
                {
                    list.add("No order place till now!!");
                }





            }
            else {
                list.add("Invalid  password..");
            }
        }
        else
        {
            list.add("No such user exists..");
        }

        return  list;
    }

    @Override
    public String placeorder(OrderDetail orderDetail)
    {

       // String orderid= UUID.randomUUID().toString().replaceAll("-","");

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);

        //retreive current orderid and increment it...
        Table table2=dynamoDB.getTable("Counters");
        Item item2=table2.getItem("idtype","ecommerceorderid");
        int currid=item2.getInt("value");
        String orderid= toString().valueOf(currid);
        table2.putItem(new Item().withPrimaryKey("idtype","ecommerceorderid")
        .with("value",++currid));

        //........
        Table table = dynamoDB.getTable("User");
        Item item=table.getItem("userid",orderDetail.getUserid());
        if(item!=null)
        {
            if (item.getString("password").equals(orderDetail.getPassword()))
            {
                table = dynamoDB.getTable("products");
                item= table.getItem("productid",orderDetail.getProductid());
                int price=item.getInt("price");
                int cost=orderDetail.getAmount()*price;

                LocalDateTime now = LocalDateTime.now();
                int year = now.getYear();
                int month = now.getMonthValue();
                int day = now.getDayOfMonth();

                table=dynamoDB.getTable("orders");
                table.putItem(new Item().withPrimaryKey("orderid",orderid)
                        .with("userid",orderDetail.getUserid())
                        .with("productid",orderDetail.getProductid())
                        .with("amount",orderDetail.getAmount()).with("cost",cost).with("orderday",day)
                        .with("ordermonth",month).with("orderyear",year));

                return "Order placed Succesfully...";

            }
            else {
              return "Invalid  password..";
            }
        }
        else
        {
            return "No such user exists..";
        }

    }

    @Override
    public String updateorder(String orderid, OrderDetail orderDetail)
    {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("User");
        Item item=table.getItem("userid",orderDetail.getUserid());
        if(item!=null)
        {
            if (item.getString("password").equals(orderDetail.getPassword()))
            {
                table = dynamoDB.getTable("products");
                item= table.getItem("productid",orderDetail.getProductid());
                int price=item.getInt("price");
                int cost=orderDetail.getAmount()*price;

               table=dynamoDB.getTable("orders");
               item=table.getItem("orderid",orderid);
               if(item!=null)
               {
                   LocalDateTime now = LocalDateTime.now();
                   int year = now.getYear();
                   int month = now.getMonthValue();
                   int day = now.getDayOfMonth();
                   table.putItem(new Item().withPrimaryKey("orderid",orderid)
                           .with("userid",orderDetail.getUserid())
                           .with("productid",orderDetail.getProductid())
                           .with("amount",orderDetail.getAmount())
                           .with("cost",cost)
                           .with("orderday",day)
                           .with("ordermonth",month)
                           .with("orderyear",year));
                   return "Order updated succesfully..";
               }
               else
               {
                   return "no such order placed by you..";
               }

            }
            else {
                return "Invalid  password..";
            }
        }
        else
        {
            return "No such user exists..";
        }
    }

    @Override
    public String deleteorder(String orderid, OrderDetail orderDetail)
    {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("User");
        Item item=table.getItem("userid",orderDetail.getUserid());
        if(item!=null)
        {
            if (item.getString("password").equals(orderDetail.getPassword()))
            {
                table=dynamoDB.getTable("orders");
                item=table.getItem("orderid",orderid);
                if(item!=null)
                {
                    table.deleteItem("orderid",orderid);
                    return "Order cancelled Succesfully..";
                }
                else
                {
                    return "no such order place till now..";
                }

            }
            else {
                return "Invalid  password..";
            }
        }
        else
        {
            return "No such user exists..";
        }

    }
}
