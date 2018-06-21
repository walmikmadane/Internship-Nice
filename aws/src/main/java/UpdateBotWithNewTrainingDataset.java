import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.*;
import com.amazonaws.services.lexmodelbuilding.model.Statement;
import org.neo4j.driver.v1.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UpdateBotWithNewTrainingDataset
{
    public static void main(String[] args)
    {

        Driver driver = GraphDatabase.driver( "bolt://hobby-holocknhlicigbkeebkgcfal.dbs.graphenedb.com:24786", AuthTokens.basic( "walmik", "b.MB5SXqHTU7i3.ZDklj2e2aMQiIKF1" ) );

        Session session = driver.session();
        String tmp="walmik";

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

        String botname="Telecom";
        GetBotRequest request = new GetBotRequest().withName(botname).withVersionOrAlias("$LATEST");
        GetBotResult response = client.getBot(request);
        List<Intent> intents= response.getIntents();
        Iterator<Intent> intentIterator=intents.iterator();
        HashMap<String,ArrayList<String>> intentmap = new HashMap<String,ArrayList<String>>();
        while(intentIterator.hasNext())
        {
            Intent tmpintent=intentIterator.next();
            String name=tmpintent.getIntentName();
            if(name.equals("greeting")||name.equals("goodbye")||name.equals("Botinfo"))
            {
                System.out.println("Prebuilt Intents..");
            }
            else
            {
                StatementResult statemeResult= session.run("match(n:utterence) -[r:utterenceOf]-> (a:intent) where a.value='"+name+"' return n.value");
                ArrayList<String> tmp2=new ArrayList<String>();
                while(statemeResult.hasNext())
                {
                    tmp2.add(statemeResult.next().get(0).asString());
                }
                intentmap.put(name,tmp2);
            }


        }
        session.close();
        driver.close();

        List<Intent> updatedintents=new ArrayList<Intent>();
        intentIterator=intents.iterator();
        while(intentIterator.hasNext())
        {
            Intent tmp3=intentIterator.next();
            if(tmp3.getIntentName().equals("greeting")||tmp3.getIntentName().equals("goodbye")||tmp3.getIntentName().equals("Botinfo"))
            {
                System.out.println("Prebuilt intents..");
            }
            else {
                PutIntentRequest putIntentRequest = new PutIntentRequest().withName(tmp3.getIntentName()).withChecksum(new GetIntent(tmp3.getIntentName()).getChecksum()).withSampleUtterances(intentmap.get(tmp3.getIntentName())).withSlots(new GetIntent(tmp3.getIntentName()).getSlots()).withFulfillmentActivity(new FulfillmentActivity().withType("ReturnIntent"));
                PutIntentResult response2 = client.putIntent(putIntentRequest);
                System.out.println(tmp3.getIntentName() + " updated..");
            }
        }

        intentIterator=intents.iterator();
        while(intentIterator.hasNext())
        {
            updatedintents.add(new Intent().withIntentName(intentIterator.next().getIntentName()).withIntentVersion("$LATEST"));
        }

            PutBotRequest request3 = new PutBotRequest()
                .withName(botname)
                .withChecksum(new GetBot(botname).getChecksum())
                .withIntents(updatedintents)
                .withClarificationPrompt(
                        new Prompt().withMessages(
                                new Message().withContentType("PlainText").withContent("I'm sorry, I didn't hear that. Can you repeate what you just said?"),
                                new Message().withContentType("PlainText").withContent("Can you say that again?")).withMaxAttempts(1))
                .withAbortStatement(
                        new Statement().withMessages(new Message().withContentType("PlainText").withContent("I don't understand. Can you try again?"),
                                new Message().withContentType("PlainText").withContent("I'm sorry, I don't understand."))).withIdleSessionTTLInSeconds(300)
                .withProcessBehavior("BUILD").withLocale("en-US").withChildDirected(true);
        PutBotResult response3 = client.putBot(request3);
        System.out.println("Bot updated..");

    }

}
