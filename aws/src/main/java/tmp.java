

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.DeleteBotRequest;
import com.amazonaws.services.lexmodelbuilding.model.DeleteBotResult;
import com.amazonaws.services.lexmodelbuilding.model.DeleteIntentRequest;
import com.amazonaws.services.lexmodelbuilding.model.DeleteIntentResult;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.Iterator;


public class tmp
{
    public static void main(String[] args) throws Exception {
        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        DeleteBotRequest deleteBotRequest=new DeleteBotRequest().withName("Telecom");
        DeleteBotResult result=client.deleteBot(deleteBotRequest);
    }
}
