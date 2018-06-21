import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.*;
import com.amazonaws.services.lexmodelbuilding.model.Statement;
import org.neo4j.driver.v1.*;


import java.util.*;


public class CreateBot
{
    public static void main(String[] args) throws Exception
    {
        UserConfigureInput userConfigureInput=new UserConfigureInput();
        JsonDataHandler jsonDataHandler=userConfigureInput.getJsonFileInput();
        CreateBot createBot=new CreateBot();
        createBot.putBot(jsonDataHandler);


    }
    public void putAllIntents(JsonDataHandler jsonDataHandler)
    {
        List<JsonIntentData> intents=jsonDataHandler.getIntents();
        Iterator<JsonIntentData> jsonIntentDataIterator=intents.iterator();
        while(jsonIntentDataIterator.hasNext())
        {
            JsonIntentData jsonIntentData=jsonIntentDataIterator.next();
            createIntent(jsonIntentData.getIntentname(),jsonIntentData.getUtterences(),jsonIntentData.getBaseurl(),jsonIntentData.getMethod(),jsonIntentData.getRequestbody(),jsonIntentData.getRequestparameters());
        }
    }
    public void createIntent(String intentName,List<String>utterences,String uri,String methodType,Map<String,Boolean> slots,List<String> requestparam)
    {
        Iterator<String> iterator;
        List<Slot> slotsObject=new LinkedList<>();
        int i=1;


        iterator=requestparam.iterator();
        while(iterator.hasNext())
        {
            String slotval=iterator.next();
            slotsObject.add(new Slot().withName(slotval)
                    .withSlotType("AMAZON.Person")
                    .withPriority(i)
                    .withSlotConstraint("Required")
                    .withValueElicitationPrompt(new Prompt().withMaxAttempts(3).withMessages(new Message().withContentType("PlainText").withContent("mention the "+slotval))));
            i++;
        }

        iterator=slots.keySet().iterator();
        while(iterator.hasNext())
        {
            String slotval=iterator.next();
            Boolean flag=slots.get(slotval);
            String constraint="Required";
            if(flag.booleanValue()==false)
                constraint="Optional";

            slotsObject.add(new Slot().withName(slotval)
                    .withSlotType("AMAZON.Person")
                    .withPriority(i)
                    .withSlotConstraint(constraint)
                    .withValueElicitationPrompt(new Prompt().withMaxAttempts(3).withMessages(new Message().withContentType("PlainText").withContent("mention the "+slotval))));
            i++;
       }


        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().withRegion(Regions.US_EAST_1)
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

        PutIntentRequest request2 = new PutIntentRequest()
                .withName(intentName)
                .withSampleUtterances(utterences)
                .withSlots(slotsObject)
                .withFulfillmentActivity(new FulfillmentActivity().withType("ReturnIntent"));


        PutIntentResult response2 = client.putIntent(request2);


    }
    public void deletebot(JsonDataHandler jsonDataHandler)
    {

        try {


            //delete botalias
            AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().withRegion(Regions.US_EAST_1)
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
            DeleteBotAliasRequest deleteBotAliasRequest=new DeleteBotAliasRequest().withBotName(jsonDataHandler.getBotname()).withName("abc");
            DeleteBotAliasResult deleteBotAliasResult=client.deleteBotAlias(deleteBotAliasRequest);

            //delete bot
            client = AmazonLexModelBuildingClientBuilder.standard().withRegion(Regions.US_EAST_1)
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
            DeleteBotRequest deleteBotRequest=new DeleteBotRequest().withName(jsonDataHandler.getBotname());
            DeleteBotResult deleteBotResult=client.deleteBot(deleteBotRequest);




        }
        catch(Exception e)
        {

        }

    }
    public void deleteIntents(JsonDataHandler jsonDataHandler)
    {
        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().withRegion(Regions.US_EAST_1)
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
        Iterator<JsonIntentData> iterator=jsonDataHandler.getIntents().iterator();
        while(iterator.hasNext())
        {
            JsonIntentData jsonIntentData=iterator.next();
            String name=jsonIntentData.getIntentname();
            try {

                    DeleteIntentRequest deleteIntentRequest=new DeleteIntentRequest().withName(name);
                    DeleteIntentResult deleteIntentResult=client.deleteIntent(deleteIntentRequest);

            } catch (Exception e)
            {
               // e.printStackTrace();
            }
        }
    }
    public void putBot(JsonDataHandler jsonDataHandler)
    {
        List<Intent> intents=new ArrayList<Intent>();
        //check if alredy exist and delete if exists...
        deletebot(jsonDataHandler);
        deleteIntents(jsonDataHandler);

        //create bot...
        putAllIntents(jsonDataHandler);
        Iterator<JsonIntentData> jsonIntentDataIterator=jsonDataHandler.getIntents().iterator();
        while(jsonIntentDataIterator.hasNext())
        {
            JsonIntentData jsonIntentData=jsonIntentDataIterator.next();
            intents.add(new Intent().withIntentName(jsonIntentData.getIntentname()).withIntentVersion("$LATEST"));
        }

        //Adding prebuilt intents...
        intents.add(new Intent().withIntentName("greeting").withIntentVersion("$LATEST"));
        intents.add(new Intent().withIntentName("goodbye").withIntentVersion("$LATEST"));
        //intents.add(new Intent().withIntentName("queries").withIntentVersion("$LATEST"));
       // intents.add(new Intent().withIntentName("questions").withIntentVersion("$LATEST"));
        intents.add(new Intent().withIntentName("Botinfo").withIntentVersion("$LATEST"));
        intents.add(new Intent().withIntentName("irrelevant").withIntentVersion("$LATEST"));

        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().withRegion(Regions.US_EAST_1)
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
        PutBotRequest request = new PutBotRequest()
                .withName(jsonDataHandler.getBotname())
                .withIntents(intents)
                .withClarificationPrompt(
                        new Prompt().withMessages(
                                new Message().withContentType("PlainText").withContent("I'm sorry, I didn't hear that. Can you repeate what you just said?"),
                                new Message().withContentType("PlainText").withContent("Can you say that again?")).withMaxAttempts(1))
                .withAbortStatement(
                        new Statement().withMessages(new Message().withContentType("PlainText").withContent("I don't understand. Can you try again?"),
                                new Message().withContentType("PlainText").withContent("I'm sorry, I don't understand."))).withIdleSessionTTLInSeconds(300)
                .withProcessBehavior("BUILD").withLocale("en-US").withChildDirected(true);
        PutBotResult response = client.putBot(request);
        System.out.println("Bot created...");

        //creating  Bot Alias...
        AmazonLexModelBuilding client1 = AmazonLexModelBuildingClientBuilder.standard().withRegion(Regions.US_EAST_1)
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
        PutBotAliasRequest putBotAliasRequest=new PutBotAliasRequest()
                .withBotName(jsonDataHandler.getBotname())
                .withName("abc")
                .withBotVersion("$LATEST");
        PutBotAliasResult putBotAliasResult=client1.putBotAlias(putBotAliasRequest);

        System.out.println("Bot Alias created...");

        //Updaing in DynamoDB database...
        updateInDatabse(jsonDataHandler);

        //Updating in Neo4J...
        updateInNeo(jsonDataHandler);


    }
    public void updateInDatabse(JsonDataHandler jsonDataHandler)
    {
        List<JsonIntentData> intents=jsonDataHandler.getIntents();
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

        Table table = dynamoDB.getTable("IntentInfo");

        try{
            Iterator<JsonIntentData> iterator=intents.iterator();
            while(iterator.hasNext())
            {
                JsonIntentData jsonIntentData=iterator.next();
                List<String> requestbody=new ArrayList<String>();
                requestbody.addAll(jsonIntentData.getRequestbody().keySet());


                PutItemOutcome outcome = table
                        .putItem(new Item().withPrimaryKey("intentname", jsonIntentData.getIntentname())
                                .with("botname",jsonDataHandler.getBotname())
                                .with("description",jsonIntentData.getDescription())
                                .with("baseurl",jsonIntentData.getBaseurl())
                                .with("method",jsonIntentData.getMethod())
                                .withList("requestparameteres",jsonIntentData.getRequestparameters())
                                .withList("requestbody",requestbody));

            }
            System.out.println("Successfully updated in DynamoDB..");
        }
        catch(Exception e)
        {
            System.out.println("Error while updating in dynamodb..");
            e.printStackTrace();
        }

    }
    public void updateInNeo(JsonDataHandler jsonDataHandler)
    {
        Driver driver = GraphDatabase.driver( "bolt://hobby-holocknhlicigbkeebkgcfal.dbs.graphenedb.com:24786", AuthTokens.basic( "walmik", "b.MB5SXqHTU7i3.ZDklj2e2aMQiIKF1" ) );

        Session session = driver.session();

        List<JsonIntentData> intents=jsonDataHandler.getIntents();
        Iterator<JsonIntentData> intentDataIterator=intents.iterator();
        while(intentDataIterator.hasNext())
        {
            JsonIntentData tmp=intentDataIterator.next();
            String intentname=tmp.getIntentname();
            try{
                session.run("CREATE (n:intent {value:'"+intentname+"'})");
            }
            catch (Exception e)
            {

            }
            Iterator<String> utterences=tmp.getUtterences().iterator();
            while(utterences.hasNext())
            {
                String tmp2=utterences.next();
                StatementResult  statementResult=session.run("match(n:utterence) where lower(n.value)=lower('"+tmp2+"') return n");
                System.out.println(statementResult.hasNext());
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


        }
       session.run("merge (n:bot{name:'"+jsonDataHandler.getBotname()+"'}) set n.desc='"+jsonDataHandler.getBotinfo()+"'");
        session.close();
        driver.close();
    }
}
