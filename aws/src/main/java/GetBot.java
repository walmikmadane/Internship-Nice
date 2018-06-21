import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.GetBotRequest;
import com.amazonaws.services.lexmodelbuilding.model.GetBotResult;
import com.amazonaws.services.lexmodelbuilding.model.Intent;

import java.util.List;

public class GetBot
{
    String botName;
    GetBot(String botName)
    {
        this.botName=botName;
    }
    public boolean isExist(String botName)
    {
        boolean flg;
        try{
            AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
            GetBotRequest request = new GetBotRequest().withName(botName).withVersionOrAlias("$LATEST");
            GetBotResult response = client.getBot(request);
            flg=true;
        }
        catch(Exception e)
        {
            flg=false;
        }
        return  flg;
    }
    public  String getChecksum()
    {
        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        GetBotRequest request = new GetBotRequest().withName(botName).withVersionOrAlias("$LATEST");
        GetBotResult response = client.getBot(request);
        return response.getChecksum();
    }
    public List<Intent> getIntents()
    {
        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        GetBotRequest request = new GetBotRequest().withName(botName).withVersionOrAlias("$LATEST");
        GetBotResult response = client.getBot(request);
        return response.getIntents();
    }
    public String getStatus()
    {
        AmazonLexModelBuilding client = AmazonLexModelBuildingClientBuilder.standard().build();
        GetBotRequest request = new GetBotRequest().withName(botName).withVersionOrAlias("$LATEST");
        GetBotResult response = client.getBot(request);
        return response.getStatus();
    }
}

