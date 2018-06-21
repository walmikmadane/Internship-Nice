
import com.amazonaws.regions.Regions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.*;


import com.squareup.okhttp.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.json.simple.parser.JSONParser;



import java.util.*;


import static org.apache.http.util.TextUtils.isEmpty;

public class TextClientApplication {

    public static void main(String[] args) throws Exception
    {
        AmazonLexRuntime client = AmazonLexRuntimeClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();
        PostTextRequest textRequest = new PostTextRequest();
        textRequest.setBotName("Telecom");
        textRequest.setBotAlias("abc");
        textRequest.setUserId("Lex_User_ChatBot");
        Scanner scanner = new Scanner(System.in);
        while (true)
        {


            String requestText = scanner.nextLine().trim();
            if (isEmpty(requestText))
                break;

            textRequest.setInputText(requestText);
            PostTextResult textResult = client.postText(textRequest);
            String message = textResult.getMessage();
            String format = textResult.getMessageFormat();
            String intentName=textResult.getIntentName();
            String dialogstate=textResult.getDialogState();

            if(dialogstate.equals("ElicitIntent"))
            {
                System.out.println("Learning..");
                ResponseCard responseCard=learningMechanism(requestText);
                System.out.println(responseCard.getGenericAttachments().get(0));

            }
            else if(dialogstate.equals("ReadyForFulfillment"))
            {
                try {
                    System.out.println(fulfillIntent(textResult));
                }catch(Exception e)
                {
                    System.out.println(intentName);
                }
            }
            else
                {


                if (message != null) {


                    if (format.equals("PlainText")) System.out.println(message);
                    else {

                        JSONParser parser = new JSONParser();
                        Object obj = parser.parse(message);
                        //JSONArray array=(JSONArray)obj;
                        JSONObject jsonObject = (JSONObject) obj;
                        JSONArray jsonArray = (JSONArray) jsonObject.get("messages");
                        int size = jsonArray.size();
                        for (int i = 0; i < size; i++) {
                            Object object = jsonArray.get(i);
                            JSONObject jsonObject1 = (JSONObject) object;
                            System.out.println(jsonObject1.get("value"));
                        }
                    }

                }


            }

        }
        System.out.println("Bye.");
    }
    public static  ResponseCard learningMechanism(String requestText)
    {
        ResponseCard responseCard=new ResponseCard().withGenericAttachments(new GenericAttachment().withButtons(new Button().withValue("Walmik")));
        return responseCard;

    }

    public  static String fulfillIntent(PostTextResult textResult) throws Exception
    {
        String m="";
        System.out.println("Executing API call..");
        String intent=textResult.getIntentName();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("IntentInfo");
        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#yr", "intentname");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":yyyy", intent);

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#yr = :yyyy").withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;
        items = table.query(querySpec);

        String baseurl=null;
        String method=null;
        List<String> requestparameteres=null;
        List<String> requestbody=null;

        iterator = items.iterator();
        while (iterator.hasNext())
        {
            item = iterator.next();
            baseurl=item.getString("baseurl");
            method=item.getString("method");
            requestparameteres=item.getList("requestparameteres");
            requestbody=item.getList("requestbody");

        }
        if(method.equals("GET"))
        {
            //format url..
            Map<String,String> slots=textResult.getSlots();
            String str="";
            Iterator<String> iterator1=requestparameteres.iterator();
            String many="";
            int g=0;
            while(iterator1.hasNext())
            {
                if(g==0)
                {
                    str="?";
                    g=1;
                }
                String key=iterator1.next();
                String val=slots.get(key);
                str=str+many+key+"="+val;
                many="&";



            }


            //Execute the call..
            OkHttpClient client3 = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(baseurl+str)
                    .get()
                    .addHeader("content-type", "application/json")
                    .addHeader("charset", "UTF-8")
                    .addHeader("x-response-control", "minified")
                    .addHeader("cache-control", "no-cache")
                    // .addHeader("postman-token", "b2d4040f-5917-fead-1b1a-bb4372d61af1")
                    .build();

            Response response = client3.newCall(request).execute();
            if(response.isSuccessful())
            {
                m=response.body().string();

            }
            else
            {
                System.out.println("Invalid required data.. Try again");
                System.out.println(response.body().string());
                System.out.println("aSasAAsAS");
            }
        }
        else if(method.equals("POST"))
        {

            //format url..
            Map<String,String> slots=textResult.getSlots();
            String str="";
            Iterator<String> iterator1=requestparameteres.iterator();
            String many="";
            int g=0;
            while(iterator1.hasNext())
            {
                if(g==0)
                {
                    str="?";
                    g=1;
                }
                String key=iterator1.next();
                String val=slots.get(key);
                str=str+many+key+"="+val;
                many="&";



            }

            //format json requestbody..

            String requestbodystr="{";
            iterator1=requestbody.iterator();
            while(iterator1.hasNext())
            {
                String key=iterator1.next();
                if(iterator1.hasNext())
                {
                    requestbodystr=requestbodystr+"\n\t\""+key+"\":\""+slots.get(key)+"\",";
                }
                else
                {
                    requestbodystr=requestbodystr+"\n\t\""+key+"\":\""+slots.get(key)+"\"\n\n\t\n";
                }

            }
            requestbodystr=requestbodystr+"}";

            //Execute the call..
            OkHttpClient client4 = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestbodystr);
            Request request = new Request.Builder()
                    .url(baseurl+str)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    //.addHeader("cache-control", "no-cache")
                    //.addHeader("postman-token", "b3973b53-2e49-49f9-76f4-eba9a8152f09")
                    .build();

            Response response = client4.newCall(request).execute();
            if(response.isSuccessful())
            {
                m=response.body().string();

            }
            else
            {
                System.out.println("Invalid required data.. Try again");
                // System.out.println(response.body().string());
            }


        }
        else if(method.equals("PUT"))
        {

            //format url..
            Map<String,String> slots=textResult.getSlots();
            String str="";
            Iterator<String> iterator1=requestparameteres.iterator();
            String many="";
            int g=0;
            while(iterator1.hasNext())
            {
                if(g==0)
                {
                    str="?";
                    g=1;
                }
                String key=iterator1.next();
                String val=slots.get(key);
                str=str+many+key+"="+val;
                many="&";



            }

            //format json requestbody..

            String requestbodystr="{";
            iterator1=requestbody.iterator();
            while(iterator1.hasNext())
            {
                String key=iterator1.next();
                if(iterator1.hasNext())
                {
                    requestbodystr=requestbodystr+"\n\t\""+key+"\":\""+slots.get(key)+"\",";
                }
                else
                {
                    requestbodystr=requestbodystr+"\n\t\""+key+"\":\""+slots.get(key)+"\"\n\n\t\n";
                }

            }
            requestbodystr=requestbodystr+"}";

            //Execute the call..
            OkHttpClient client4 = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestbodystr);
            Request request = new Request.Builder()
                    .url(baseurl+str)
                    .put(body)
                    .addHeader("content-type", "application/json")
                    //.addHeader("cache-control", "no-cache")
                    //.addHeader("postman-token", "b3973b53-2e49-49f9-76f4-eba9a8152f09")
                    .build();

            Response response = client4.newCall(request).execute();
            if(response.isSuccessful())
            {
                m=response.body().string();

            }
            else
            {
                System.out.println("Invalid required data.. Try again");
                // System.out.println(response.body().string());
            }

        }
        else if(method.equals("DELETE"))
        {
            //format url..
            Map<String,String> slots=textResult.getSlots();
            String str="";
            Iterator<String> iterator1=requestparameteres.iterator();
            String many="";
            int g=0;
            while(iterator1.hasNext())
            {
                if(g==0)
                {
                    str="?";
                    g=1;
                }
                String key=iterator1.next();
                String val=slots.get(key);
                str=str+many+key+"="+val;
                many="&";



            }

            //format json requestbody..

            String requestbodystr="{";
            iterator1=requestbody.iterator();
            while(iterator1.hasNext())
            {
                String key=iterator1.next();
                if(iterator1.hasNext())
                {
                    requestbodystr=requestbodystr+"\n\t\""+key+"\":\""+slots.get(key)+"\",";
                }
                else
                {
                    requestbodystr=requestbodystr+"\n\t\""+key+"\":\""+slots.get(key)+"\"\n\n\t\n";
                }

            }
            requestbodystr=requestbodystr+"}";

            //Execute the call..
            OkHttpClient client4 = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestbodystr);
            Request request = new Request.Builder()
                    .url(baseurl+str)
                    .delete(body)
                    .addHeader("content-type", "application/json")
                    //.addHeader("cache-control", "no-cache")
                    //.addHeader("postman-token", "b3973b53-2e49-49f9-76f4-eba9a8152f09")
                    .build();

            Response response = client4.newCall(request).execute();
            if(response.isSuccessful())
            {
                m=response.body().string();

            }
            else
            {
                System.out.println("Invalid required data.. Try again");
                //System.out.println(requestbodystr);
                //System.out.println(baseurl+str);
                //System.out.println(response.body().string());
                // System.out.println(response.body().string());
            }
        }
        else
        {
            System.out.println("Invalid method type..");
        }

        return m;

    }
}

