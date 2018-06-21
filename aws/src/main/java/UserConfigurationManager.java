import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.*;
import com.amazonaws.services.lexmodelbuilding.model.Statement;
import org.neo4j.driver.v1.*;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static org.apache.http.util.TextUtils.isEmpty;

public class UserConfigurationManager
{

    private static final String botName="botname";
    private static UserBotInfo userBotInfo=new UserBotInfo();

    public static void main(String[] args)
    {
        UserConfigurationManager userConfigurationManager = new UserConfigurationManager();
        Scanner scan=new Scanner(System.in);

        while(true)
        {
            System.out.println("Do you want to create a intent....");
            String str=scan.nextLine();
            if(str.equalsIgnoreCase("yes"))
            {
                    System.out.println("Enter Intent name...");
                    String intentName=scan.nextLine();

                    System.out.println("Enter Sample Utterences....");
                    List<String> utterences=new ArrayList<String>();
                    while(true)
                    {
                        String utterence=scan.nextLine();
                        if(isEmpty(utterence))
                            break;
                        utterences.add(utterence);


                    }

                System.out.println("Enter Required Parameteres...");
                List<String> slots=new ArrayList<String>();
                while(true)
                {
                    String slot=scan.nextLine();
                    if(isEmpty(slot))
                        break;
                    slots.add(slot);
                }

                System.out.println("Enter Fullfillment Info....");
                System.out.println("Enter base url..");
                String uri=scan.nextLine();
                System.out.println("Enter method type..");
                String methodType=scan.nextLine();

                    userConfigurationManager.createIntent(intentName,utterences,uri,methodType,slots);
                    userBotInfo.addIntent(new IntentInfo(intentName,uri,methodType));


            }
            else
                break;
        }
        System.out.println("Do you want to create bot with these intents...");
        if(scan.nextLine().equalsIgnoreCase("yes"))
        {
            System.out.println("Enter Bot Name..");
            String botName=scan.nextLine();
            System.out.println("Creating bot....");
            userBotInfo.setBotName(botName);
            userConfigurationManager.createBot(botName);
            System.out.println("Bot "+botName+" Created..");
        }

    }
    public void createBot(String botName)
    {
        List<Intent> intents=new ArrayList<Intent>();
       // List<String> intentnames=new ArrayList<String>();

        List<IntentInfo> intentnames=userBotInfo.getIntents();
        Iterator<IntentInfo>iterator=intentnames.iterator();
        while(iterator.hasNext())
        {
            IntentInfo str=iterator.next();
            intents.add(new Intent().withIntentName(str.getIntentName()).withIntentVersion("$LATEST"));
        }
        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        PutBotRequest request = new PutBotRequest()
                .withName(botName)
                .withIntents(intents)
                .withClarificationPrompt(
                        new Prompt().withMessages(
                                new Message().withContentType("PlainText").withContent("I'm sorry, I didn't hear that. Can you repeate what you just said?"),
                                new Message().withContentType("PlainText").withContent("Can you say that again?")).withMaxAttempts(1))
                .withAbortStatement(
                        new Statement().withMessages(new Message().withContentType("PlainText").withContent("I don't understand. Can you try again?"),
                                new Message().withContentType("PlainText").withContent("I'm sorry, I don't understand."))).withIdleSessionTTLInSeconds(300)
                .withProcessBehavior("SAVE").withLocale("en-US").withChildDirected(true);
        PutBotResult response = client.putBot(request);
        //update in database...
        addIntentInDb();
    }
    public void createIntent(String intentName,List<String>utterences,String uri,String methodType,List<String>slots)
    {
        Iterator<String> iterator=slots.iterator();

        List<Slot> slotsObject=new ArrayList<Slot>();

        while(iterator.hasNext())
        {
            String slotval=iterator.next();
            slotsObject.add(new Slot().withName(slotval).withSlotType("AMAZON.Person").withSlotConstraint("Required").withValueElicitationPrompt(new Prompt().withMaxAttempts(3).withMessages(new Message().withContentType("PlainText").withContent("mention the "+slotval))));
        }

        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        PutIntentRequest request2 = new PutIntentRequest()
                .withName(intentName)
                .withSampleUtterances(utterences)
                .withSlots(slotsObject)
                .withFulfillmentActivity(new FulfillmentActivity().withType("ReturnIntent"));


        PutIntentResult response2 = client.putIntent(request2);


    }
    public void addIntentInDb()
    {
        String uri="bolt://localhost:7687";
        String user="neo4j";
        String password="825645";

       Driver driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
        String str="create ";
       Iterator<IntentInfo> intentInfoIterator=userBotInfo.getIntents().iterator();
       int i=0;
       String ch="";
       while(intentInfoIterator.hasNext())
       {
           if(i==1)
               ch=",";
           IntentInfo intentInfo=intentInfoIterator.next();
           str=str+ch+"("+intentInfo.getIntentName()+":"+userBotInfo.getBotName()+":"+intentInfo.getIntentName()+"{bot:\""+userBotInfo.getBotName()+"\",intent:\""+intentInfo.getIntentName()+"\",baseurl:\""+intentInfo.getBaseurl()+"\",method:\""+intentInfo.getMethodType()+"\"})";
            i=1;


       }
       final String str2=str;
        try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {


                    StatementResult result2=tx.run(str2);

                    return "";

                }
            } );

        }
    }

}
