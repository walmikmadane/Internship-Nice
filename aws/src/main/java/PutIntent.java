import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.*;

public class PutIntent
{
    public static void main(String[] args)
    {
        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        PutIntentRequest request = new PutIntentRequest()
                .withName("DocOrderPizza")
                .withDescription("Order a pizza from a local pizzeria.")
                .withSampleUtterances("Order me a pizza.", "Order me  pizza.")
                .withConclusionStatement(
                        new Statement()
                                .withMessages(
                                        new Message().withContentType("PlainText").withContent(
                                                "All right, I ordered  you a {Crust} crust {Type} pizza with {Sauce} sauce.")));


        PutIntentResult response = client.putIntent(request);
        System.out.println(response);
    }
}
