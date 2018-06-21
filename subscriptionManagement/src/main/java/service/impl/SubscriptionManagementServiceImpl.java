package service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import org.springframework.stereotype.Service;
import service.SubscriptionManagementService;


@Service
public class SubscriptionManagementServiceImpl implements SubscriptionManagementService
{
    public static void main(String[] args) {
        SubscriptionManagementServiceImpl subscriptionManagementService=new SubscriptionManagementServiceImpl();
        String list=subscriptionManagementService.deactivateScheme("8600043230","1");
    System.out.println(list);
    }

    public List<String> getAllSchemes()
    {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSCredentialsProvider() {
                    @Override
                    public AWSCredentials getCredentials() {
                        return new AWSCredentials() {
                            @Override
                            public String getAWSAccessKeyId() {
                                return "AKIAJ3ZQUIJVNUD7UKZA";
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return "EKUBWJ4eDnWklcEkkv1HBDYXX7Uzt0hD3GDsMCy2";
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                })
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("schemes");
        ItemCollection<ScanOutcome> items = table.scan (
                null,                                  //FilterExpression
                "schemeid, description",     //ProjectionExpression
                null,                                           //No ExpressionAttributeNames
               null);
        Iterator<Item> iterator = items.iterator();
        List<String> list=new ArrayList<String>();
        while (iterator.hasNext())
        {
            Item item=iterator.next();
            //System.out.println(item.get("schemeid")+"  "+item.get("description"));
            list.add("Scheme Id:"+item.get("schemeid")+"\nSchemeDetails:\n"+item.get("description"));
            list.add("\n----------------\n");
        }

        return list;
    }

    public List<String> getAllSchemes(String mobno)
    {
        List<String> list=new ArrayList<String>();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSCredentialsProvider() {
                    @Override
                    public AWSCredentials getCredentials() {
                        return new AWSCredentials() {
                            @Override
                            public String getAWSAccessKeyId() {
                                return "AKIAJ3ZQUIJVNUD7UKZA";
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return "EKUBWJ4eDnWklcEkkv1HBDYXX7Uzt0hD3GDsMCy2";
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                })
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("subscription");
        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#mob", "mobno");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":yyyy", mobno);

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#mob = :yyyy").withNameMap(nameMap)
                .withValueMap(valueMap);
        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        List<String>schemes=new LinkedList<String>();
        List<Calendar>scheme_date=new LinkedList<Calendar>();
        try {

            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
               // System.out.println(item.getNumber("year") + ": " + item.getString("title"));
                schemes.add(item.getString("schemeid"));
                int day=item.getInt("activationday");
                int month=item.getInt("activationmonth");
                int year=item.getInt("activationyear");
                Calendar c = Calendar.getInstance();
                c.set(year, month - 1, day, 0, 0);
                scheme_date.add(c);

            }

        }
        catch (Exception e) {
            list.add("No Subscription added to this mobile no.");
            return list;
        }

        table=dynamoDB.getTable("schemes");
        Iterator<String> stringIterator=schemes.iterator();
        Iterator<Calendar> calendarIterator=scheme_date.iterator();
        if(stringIterator.hasNext()==false)
            list.add("No Subscription added to this mobile no.");
        while(stringIterator.hasNext())
        {
            String schemeid=stringIterator.next();
            Calendar activationdate=calendarIterator.next();
            Item item1=table.getItem("schemeid",schemeid);
            int validity=item1.getInt("validitydays");
            Calendar deactivatedate=activationdate;
           // deactivatedate.add(Calendar.DATE,validity);
            if(item1!=null)
            {
                list.add("Scheme Id:"+item1.get("schemeid")+"\n"+item1.get("description"));
                list.add("\nActivated Date:"+activationdate.get(Calendar.DATE)+"/"+(activationdate.get(Calendar.MONTH)+1)%12+"/"+activationdate.get(Calendar.YEAR));
                activationdate.add(Calendar.DATE,validity);
                list.add("\nValid Till:"+activationdate.get(Calendar.DATE)+"/"+(activationdate.get(Calendar.MONTH)+1)%12+"/"+activationdate.get(Calendar.YEAR));
                list.add("\n----------------\n");

            }

            //System.out.println(schemeid);


        }
        return list;
    }

    public String getScheme(String schemeid,String element)
    {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSCredentialsProvider() {
                    @Override
                    public AWSCredentials getCredentials() {
                        return new AWSCredentials() {
                            @Override
                            public String getAWSAccessKeyId() {
                                return "AKIAJ3ZQUIJVNUD7UKZA";
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return "EKUBWJ4eDnWklcEkkv1HBDYXX7Uzt0hD3GDsMCy2";
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                })
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("schemes");


        Item item=table.getItem("schemeid",schemeid);
        if(item!=null)
        {
            String str="";
           // str=str+"------------------------------------------------------------------------\n";

            if(element.equals("everything"))
                 str=str+"SchemeId:"+schemeid+" \nPlanType:"+item.getString("plantype")+" \nvalidity:"+item.getInt("validitydays")+" days\n"+"price: Rs."+item.getInt("cost")+"\n\n"+item.getString("description");
           else
               str=str+"SchemeId:"+schemeid+" \n"+element+":"+item.get(element);
           // str=str+"\n---------------------------------------------------------------------\n";
            return  str;
        }
        else
        {
            return "No such Scheme with id "+schemeid+" available right now!!";
        }

    }

    public String activateScheme(String mobno, String schemeid)
    {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSCredentialsProvider() {
                    @Override
                    public AWSCredentials getCredentials() {
                        return new AWSCredentials() {
                            @Override
                            public String getAWSAccessKeyId() {
                                return "AKIAJ3ZQUIJVNUD7UKZA";
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return "EKUBWJ4eDnWklcEkkv1HBDYXX7Uzt0hD3GDsMCy2";
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                })
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("schemes");
        Item item=table.getItem("schemeid",schemeid);
        if(item!=null)
        {
            table = dynamoDB.getTable("subscription");
            LocalDateTime now = LocalDateTime.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            table.putItem(new Item().withPrimaryKey("mobno",mobno,"schemeid",schemeid).with("activationyear",year).with("activationmonth",month).with("activationday",day));
            return "Scheme Activated Succesfully...";

        }
        else
        {
            return "No such Scheme with id "+schemeid+" available right now!!";
        }



    }

    public String deactivateScheme(String mobno, String schemeid)
    {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSCredentialsProvider() {
                    @Override
                    public AWSCredentials getCredentials() {
                        return new AWSCredentials() {
                            @Override
                            public String getAWSAccessKeyId() {
                                return "AKIAJ3ZQUIJVNUD7UKZA";
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return "EKUBWJ4eDnWklcEkkv1HBDYXX7Uzt0hD3GDsMCy2";
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                })
                .build();
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("subscription");
        Item item=table.getItem("mobno",mobno,"schemeid",schemeid);
        if(item!=null)
        {
            table.deleteItem("mobno",mobno,"schemeid",schemeid);
            return "Scheme Deactivated Succesfully..";
        }
        else
        {
            return "No such Scheme with id "+schemeid+" subscribe to Mobile No "+mobno;
        }



    }
}