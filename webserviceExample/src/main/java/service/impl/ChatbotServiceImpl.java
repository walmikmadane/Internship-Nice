package service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.*;
import com.squareup.okhttp.*;
import entity.BotResponse;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.neo4j.driver.v1.*;
import org.springframework.stereotype.Service;
import service.ChatbotService;
import com.amazonaws.regions.Regions;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.lexruntime.AmazonLexRuntime;
import com.amazonaws.services.lexruntime.AmazonLexRuntimeClientBuilder;
import com.amazonaws.services.lexruntime.model.PostTextRequest;
import com.amazonaws.services.lexruntime.model.PostTextResult;


import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.json.simple.parser.JSONParser;



import java.util.*;


import static org.apache.http.util.TextUtils.isEmpty;

@Service
public class ChatbotServiceImpl implements ChatbotService
{

    @Override
    public BotResponse chat(String msg2, String botname,boolean learning)
    {
        try {

            String msg="";
            AmazonLexRuntime client = AmazonLexRuntimeClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1).withCredentials(new AWSCredentialsProvider() {
                        @Override
                        public AWSCredentials getCredentials() {
                            return new AWSCredentials() {
                                @Override
                                public String getAWSAccessKeyId() {
                                    return "";
                                }

                                @Override
                                public String getAWSSecretKey() {
                                    return "";
                                }
                            };
                        }

                        @Override
                        public void refresh() {

                        }
                    }).build();
            PostTextRequest textRequest = new PostTextRequest();
            textRequest.setBotName(botname);
            textRequest.setBotAlias("abc");
            textRequest.setUserId("Lex_User_ChatBot");
            Scanner scanner = new Scanner(System.in);
            System.out.println("Start Chatting...");

                String requestText = msg2;
                if (isEmpty(requestText))
                    return new BotResponse("Byeee....",false);

                textRequest.setInputText(requestText);
                PostTextResult textResult = client.postText(textRequest);
                String message = textResult.getMessage();
                String format = textResult.getMessageFormat();
                String intentName = textResult.getIntentName();
                String dialogstate = textResult.getDialogState();
                String fulfiimentresponse = "";
                if(learning==true)
                {
                    System.out.println("update training data set..");
                    //get last training utterence in neo4j....
                    Driver driver = GraphDatabase.driver( "bolt://hobby-holocknhlicigbkeebkgcfal.dbs.graphenedb.com:24786", AuthTokens.basic( "", "" ) );

                    Session session = driver.session();

                   StatementResult statementResult= session.run("match(n:bot) where n.name='"+botname+"' return n.value");
                   if(statementResult.hasNext()) {
                       //Record record = statementResult.next();
                       //Value value = record.get(0);

                       updateTrainingDatasetInS3(statementResult.next().get(0).asString(),intentName);
                   }


                    learning=false;
                }
                if (dialogstate.equals("ReadyForFulfillment"))
                {
                    learning=false;
                    fulfiimentresponse = fulfillIntent(textResult,botname);
                    System.out.println("abc");
                    System.out.println(fulfiimentresponse);
                    msg=msg+fulfiimentresponse;
                }
                else if(dialogstate.equals("ElicitIntent"))
                {
                    System.out.println("Learning....");
                    learning=true;

                    //get all intents....
                    AmazonLexModelBuilding client2 = AmazonLexModelBuildingClientBuilder.standard().withRegion(Regions.US_EAST_1)
                            .withCredentials(new AWSCredentialsProvider() {
                                @Override
                                public AWSCredentials getCredentials() {
                                    return new AWSCredentials() {
                                        @Override
                                        public String getAWSAccessKeyId() {
                                            return "";
                                        }

                                        @Override
                                        public String getAWSSecretKey() {
                                            return "";
                                        }
                                    };
                                }

                                @Override
                                public void refresh() {

                                }
                            }).build();
                    GetBotRequest request = new GetBotRequest().withName(botname).withVersionOrAlias("$LATEST");
                    GetBotResult response = client2.getBot(request);
                    List<Intent> intentMetadata=response.getIntents();
                    Iterator<Intent> intents=intentMetadata.iterator();
                    AmazonDynamoDB client3 = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1)
                            .withCredentials(new AWSCredentialsProvider() {
                                @Override
                                public AWSCredentials getCredentials() {
                                    return new AWSCredentials() {
                                        @Override
                                        public String getAWSAccessKeyId() {
                                            return "";
                                        }

                                        @Override
                                        public String getAWSSecretKey() {
                                            return "";
                                        }
                                    };
                                }

                                @Override
                                public void refresh() {

                                }
                            })
                            .build();
                    DynamoDB dynamoDB = new DynamoDB(client3);
                    Table table = dynamoDB.getTable("IntentInfo");



                    while(intents.hasNext())
                    {
                        String tmpintent=intents.next().getIntentName();
                        System.out.println(tmpintent);
                        try{
                            Item item=table.getItem("intentname",tmpintent);
                            msg=msg+item.getString("description")+"&";
                        }
                        catch (Exception e)
                        {
                            //msg=msg+tmpintent+"&";
                        }


                    }

                    //set last training utterence in neo4j....
                    Driver driver = GraphDatabase.driver( "bolt://hobby-holocknhlicigbkeebkgcfal.dbs.graphenedb.com:24786", AuthTokens.basic( "", "" ) );

                    Session session = driver.session();

                    session.run("merge (n:bot{name:'"+botname+"'}) set n.value='"+msg2+"'");

                }
                else {
                    learning=false;
                    if (message != null) {


                        if (format.equals("PlainText")) {
                            System.out.println(message);
                            msg = msg + "\n" + message;
                        } else {

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
                                msg = msg + "\n" + jsonObject1.get("value");
                            }
                        }

                    }

                }
            return new BotResponse(msg,learning);
        }
        catch (Exception e)
        {
            return new BotResponse(e.getMessage(),false);
        }


    }

    public static void updateTrainingDatasetInS3(String tmp2,String intentname)
    {
        Driver driver = GraphDatabase.driver( "bolt://hobby-holocknhlicigbkeebkgcfal.dbs.graphenedb.com:24786", AuthTokens.basic( "", "" ) );

        Session session = driver.session();

        StatementResult statementResult=session.run("match(n:utterence) where lower(n.value)=lower('"+tmp2+"') return n");
        //System.out.println(statementResult.hasNext());
        if(statementResult.hasNext())
        {
            System.out.println("utterence already present");
            session.run("match(n:intent),(b:utterence) where n.value='"+intentname+"'"+" and b.value='"+tmp2+"' merge(b)-[:utterenceOf]->(n)");

        }
        else
        {
            System.out.println("utterence created first...");
            session.run("match(n:intent) where n.value='"+intentname+"' create(a:utterence{value:'"+tmp2+"'})-[:utterenceOf]->(n)");

        }

    }
    public  static String fulfillIntent(PostTextResult textResult,String botname) throws Exception
    {
        String m="";
        System.out.println("Executing API call..");
        String intent=textResult.getIntentName();
        if(intent.equals("Botinfo")||intent.equals("irrelevant"))
        {
            Driver driver = GraphDatabase.driver( "bolt://hobby-holocknhlicigbkeebkgcfal.dbs.graphenedb.com:24786", AuthTokens.basic( "", "" ) );

            Session session = driver.session();

           StatementResult statementResult= session.run("match (n:bot{name:'"+botname+"'}) return n.desc");
           if(statementResult.hasNext())
           {
               m=statementResult.next().get(0).asString();
           }

        }
        else {
            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSCredentialsProvider() {
                @Override
                public AWSCredentials getCredentials() {
                    return new AWSCredentials() {
                        @Override
                        public String getAWSAccessKeyId() {
                            return "";
                        }

                        @Override
                        public String getAWSSecretKey() {
                            return "";
                        }
                    };
                }

                @Override
                public void refresh() {

                }
            }).build();

            DynamoDB dynamoDB = new DynamoDB(client);

            Table table = dynamoDB.getTable("IntentInfo");
            HashMap<String, String> nameMap = new HashMap<String, String>();
            nameMap.put("#yr", "intentname");

            HashMap<String, Object> valueMap = new HashMap<String, Object>();
            valueMap.put(":yyyy", intent);

            QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#yr = :yyyy").withNameMap(nameMap).withValueMap(valueMap);

            ItemCollection<QueryOutcome> items = null;
            Iterator<Item> iterator = null;
            Item item = null;
            items = table.query(querySpec);

            String baseurl = null;
            String method = null;
            List<String> requestparameteres = null;
            List<String> requestbody = null;

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                baseurl = item.getString("baseurl");
                method = item.getString("method");
                requestparameteres = item.getList("requestparameteres");
                requestbody = item.getList("requestbody");

            }
            if (method.equals("GET")) {
                //format url..
                Map<String, String> slots = textResult.getSlots();
                String str = "";
                Iterator<String> iterator1 = requestparameteres.iterator();
                String many = "";
                int g = 0;
                while (iterator1.hasNext()) {
                    if (g == 0) {
                        str = "?";
                        g = 1;
                    }
                    String key = iterator1.next();
                    String val = slots.get(key);
                    str = str + many + key + "=" + val;
                    many = "&";


                }


                //Execute the call..
                OkHttpClient client3 = new OkHttpClient();

                Request request = new Request.Builder().url(baseurl + str).get().addHeader("content-type", "application/json").addHeader("charset", "UTF-8").addHeader("x-response-control", "minified").addHeader("cache-control", "no-cache")
                        // .addHeader("postman-token", "b2d4040f-5917-fead-1b1a-bb4372d61af1")
                        .build();

                Response response = client3.newCall(request).execute();
                if (response.isSuccessful()) {
                    m = response.body().string();

                } else {
                    System.out.println("Invalid required data.. Try again");
                    System.out.println(response.body().string());
                }
            } else if (method.equals("POST")) {

                //format url..
                Map<String, String> slots = textResult.getSlots();
                String str = "";
                Iterator<String> iterator1 = requestparameteres.iterator();
                String many = "";
                int g = 0;
                while (iterator1.hasNext()) {
                    if (g == 0) {
                        str = "?";
                        g = 1;
                    }
                    String key = iterator1.next();
                    String val = slots.get(key);
                    str = str + many + key + "=" + val;
                    many = "&";


                }

                //format json requestbody..

                String requestbodystr = "{";
                iterator1 = requestbody.iterator();
                while (iterator1.hasNext()) {
                    String key = iterator1.next();
                    if (iterator1.hasNext()) {
                        requestbodystr = requestbodystr + "\n\t\"" + key + "\":\"" + slots.get(key) + "\",";
                    } else {
                        requestbodystr = requestbodystr + "\n\t\"" + key + "\":\"" + slots.get(key) + "\"\n\n\t\n";
                    }

                }
                requestbodystr = requestbodystr + "}";

                //Execute the call..
                OkHttpClient client3 = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, requestbodystr);
                Request request = new Request.Builder().url(baseurl + str).post(body).addHeader("content-type", "application/json")
                        //.addHeader("cache-control", "no-cache")
                        //.addHeader("postman-token", "b3973b53-2e49-49f9-76f4-eba9a8152f09")
                        .build();

                Response response = client3.newCall(request).execute();
                if (response.isSuccessful()) {
                    m = response.body().string();

                } else {
                    System.out.println("Invalid required data.. Try again");
                    // System.out.println(response.body().string());
                }


            } else if (method.equals("PUT")) {

                //format url..
                Map<String, String> slots = textResult.getSlots();
                String str = "";
                Iterator<String> iterator1 = requestparameteres.iterator();
                String many = "";
                int g = 0;
                while (iterator1.hasNext()) {
                    if (g == 0) {
                        str = "?";
                        g = 1;
                    }
                    String key = iterator1.next();
                    String val = slots.get(key);
                    str = str + many + key + "=" + val;
                    many = "&";


                }

                //format json requestbody..

                String requestbodystr = "{";
                iterator1 = requestbody.iterator();
                while (iterator1.hasNext()) {
                    String key = iterator1.next();
                    if (iterator1.hasNext()) {
                        requestbodystr = requestbodystr + "\n\t\"" + key + "\":\"" + slots.get(key) + "\",";
                    } else {
                        requestbodystr = requestbodystr + "\n\t\"" + key + "\":\"" + slots.get(key) + "\"\n\n\t\n";
                    }

                }
                requestbodystr = requestbodystr + "}";

                //Execute the call..
                OkHttpClient client3 = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, requestbodystr);
                Request request = new Request.Builder().url(baseurl + str).put(body).addHeader("content-type", "application/json")
                        //.addHeader("cache-control", "no-cache")
                        //.addHeader("postman-token", "b3973b53-2e49-49f9-76f4-eba9a8152f09")
                        .build();

                Response response = client3.newCall(request).execute();
                if (response.isSuccessful()) {
                    m = response.body().string();

                } else {
                    System.out.println("Invalid required data.. Try again");
                    // System.out.println(response.body().string());
                }

            } else if (method.equals("DELETE")) {
                //format url..
                Map<String, String> slots = textResult.getSlots();
                String str = "";
                Iterator<String> iterator1 = requestparameteres.iterator();
                String many = "";
                int g = 0;
                while (iterator1.hasNext()) {
                    if (g == 0) {
                        str = "?";
                        g = 1;
                    }
                    String key = iterator1.next();
                    String val = slots.get(key);
                    str = str + many + key + "=" + val;
                    many = "&";


                }

                //format json requestbody..

                String requestbodystr = "{";
                iterator1 = requestbody.iterator();
                while (iterator1.hasNext()) {
                    String key = iterator1.next();
                    if (iterator1.hasNext()) {
                        requestbodystr = requestbodystr + "\n\t\"" + key + "\":\"" + slots.get(key) + "\",";
                    } else {
                        requestbodystr = requestbodystr + "\n\t\"" + key + "\":\"" + slots.get(key) + "\"\n\n\t\n";
                    }

                }
                requestbodystr = requestbodystr + "}";

                //Execute the call..
                OkHttpClient client3 = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, requestbodystr);
                Request request = new Request.Builder().url(baseurl + str).delete(body).addHeader("content-type", "application/json")
                        //.addHeader("cache-control", "no-cache")
                        //.addHeader("postman-token", "b3973b53-2e49-49f9-76f4-eba9a8152f09")
                        .build();

                Response response = client3.newCall(request).execute();
                if (response.isSuccessful()) {
                    m = response.body().string();

                } else {
                    System.out.println("Invalid required data.. Try again");
                    // System.out.println(response.body().string());
                }
            } else {
                System.out.println("Invalid method type..");
            }
        }
        return m;

    }
}
