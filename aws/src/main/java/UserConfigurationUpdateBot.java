import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.*;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static org.apache.http.util.TextUtils.isEmpty;

public class UserConfigurationUpdateBot
{


    public static void main(String[] args)
    {
        Scanner scan=new Scanner(System.in);
        UserConfigurationUpdateBot userConfigurationUpdateBot=new UserConfigurationUpdateBot();
        System.out.println("Enter bot name..");
        String botName=scan.nextLine();

        if(!new GetBot(botName).isExist(botName))
        {
            System.out.println("Invalid Bot name..");
            return;
        }
        while(true)
        {
            System.out.println("Enter option:\n1.add intent \n2. delete intent \n3.update Intent");
            int n=scan.nextInt();
            if(n>3||n<=0)
                break;
            switch(n)
            {
                case 1:
                    userConfigurationUpdateBot.addIntent(botName);
                    break;
                case 2:
                    userConfigurationUpdateBot.deleteIntent(botName);
                    break;
                case 3:
                    userConfigurationUpdateBot.updateIntent(botName);
                    break;
            }
        }

    }
    public void updateIntent(String botName)
    {
        List<Intent> intents1=new GetBot(botName).getIntents();
        System.out.println("select  from below intents..");
        Iterator<Intent> iterator=intents1.iterator();
        while(iterator.hasNext())
        {
            Intent a=iterator.next();
            System.out.println(a.getIntentName());
        }
        Scanner scan=new Scanner(System.in);
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
        System.out.println("Enter Fullfillment URI..");
        String uri=scan.nextLine();

        System.out.println("Enter Slots...");
        List<String> slots=new ArrayList<String>();
        while(true)
        {
            String slot=scan.nextLine();
            if(isEmpty(slot))
                break;
            slots.add(slot);
        }

        Iterator<String> iterator2=slots.iterator();

        List<Slot> slotsObject=new ArrayList<Slot>();

        while(iterator2.hasNext())
        {
            String slotval=iterator2.next();
            slotsObject.add(new Slot().withName(slotval).withSlotType("AMAZON.Person").withSlotConstraint("Required").withValueElicitationPrompt(new Prompt().withMaxAttempts(3).withMessages(new Message().withContentType("PlainText").withContent("mention the "+slotval))));
        }

        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        PutIntentRequest request = new PutIntentRequest()
                .withName(intentName)
                .withChecksum(new GetIntent(intentName).getChecksum())
                .withSlots(slotsObject)
                .withSampleUtterances(utterences)
               .withFulfillmentActivity(new FulfillmentActivity().withType("ReturnIntent"));


        PutIntentResult response1 = client.putIntent(request);

    }
    public void addIntent(String botName)
    {
        Scanner scan=new Scanner(System.in);
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
        System.out.println("Enter Fullfillment URI..");
        String uri=scan.nextLine();

        System.out.println("Enter Slots...");
        List<String> slots=new ArrayList<String>();
        while(true)
        {
            String slot=scan.nextLine();
            if(isEmpty(slot))
                break;
            slots.add(slot);
        }

        Iterator<String> iterator=slots.iterator();

        List<Slot> slotsObject=new ArrayList<Slot>();

        while(iterator.hasNext())
        {
            String slotval=iterator.next();
            slotsObject.add(new Slot().withName(slotval).withSlotType("AMAZON.Person").withSlotConstraint("Required").withValueElicitationPrompt(new Prompt().withMaxAttempts(3).withMessages(new Message().withContentType("PlainText").withContent("mention the "+slotval))));
        }

        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        PutIntentRequest request = new PutIntentRequest()
                .withName(intentName)
                .withSlots(slotsObject)
                .withSampleUtterances(utterences)
                .withFulfillmentActivity(new FulfillmentActivity().withType("ReturnIntent"));


        PutIntentResult response1 = client.putIntent(request);

        List<Intent> intents=new GetBot(botName).getIntents();
        intents.add(new Intent().withIntentName(intentName).withIntentVersion("$LATEST"));
        AmazonLexModelBuilding client2 = AmazonLexModelBuildingClientBuilder.standard().build();
        PutBotRequest request2 = new PutBotRequest()
                .withName(botName)
                .withChecksum(new GetBot(botName).getChecksum())
                .withIntents(intents)
                .withClarificationPrompt(
                        new Prompt().withMessages(
                                new Message().withContentType("PlainText").withContent("I'm sorry, I didn't hear that. Can you repeate what you just said?"),
                                new Message().withContentType("PlainText").withContent("Can you say that again?")).withMaxAttempts(1))
                .withAbortStatement(
                        new Statement().withMessages(new Message().withContentType("PlainText").withContent("I don't understand. Can you try again?"),
                                new Message().withContentType("PlainText").withContent("I'm sorry, I don't understand."))).withIdleSessionTTLInSeconds(300)
                .withProcessBehavior("SAVE").withLocale("en-US").withChildDirected(true);
        PutBotResult response = client2.putBot(request2);
    }

    public void deleteIntent(String botName)
    {

        Scanner scan=new Scanner(System.in);
        List<Intent> intents1=new GetBot(botName).getIntents();
        System.out.println("select  from below intents..");
        Iterator<Intent> iterator=intents1.iterator();
        while(iterator.hasNext())
        {
            Intent a=iterator.next();
            System.out.println(a.getIntentName());
        }
        String intentName=scan.nextLine();

        List<Intent> intents=new GetBot(botName).getIntents();
        Iterator<Intent> iterator2=intents.iterator();
        while(iterator2.hasNext())
        {
            Intent a=iterator2.next();
            if(a.getIntentName().equals(intentName))
            {
                iterator2.remove();
            }
        }

        //remove from bot
        AmazonLexModelBuilding client2 = AmazonLexModelBuildingClientBuilder.standard().build();
        PutBotRequest request2 = new PutBotRequest()
                .withName(botName)
                .withChecksum(new GetBot(botName).getChecksum())
                .withIntents(intents)
                .withClarificationPrompt(
                        new Prompt().withMessages(
                                new Message().withContentType("PlainText").withContent("I'm sorry, I didn't hear that. Can you repeate what you just said?"),
                                new Message().withContentType("PlainText").withContent("Can you say that again?")).withMaxAttempts(1))
                .withAbortStatement(
                        new Statement().withMessages(new Message().withContentType("PlainText").withContent("I don't understand. Can you try again?"),
                                new Message().withContentType("PlainText").withContent("I'm sorry, I don't understand."))).withIdleSessionTTLInSeconds(300)
                .withProcessBehavior("SAVE").withLocale("en-US").withChildDirected(true);
        PutBotResult response = client2.putBot(request2);

        //remove from space
        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        DeleteIntentRequest deleteIntentRequest=new DeleteIntentRequest().withName(intentName);
        DeleteIntentResult deleteIntentResult=client.deleteIntent(deleteIntentRequest);

    }
}
