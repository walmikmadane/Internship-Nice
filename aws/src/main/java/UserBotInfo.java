import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.*;

import java.util.ArrayList;
import java.util.List;

public class UserBotInfo
{
    private String botName=null;
    private List<IntentInfo> intents=new ArrayList<IntentInfo>();

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public List<IntentInfo> getIntents() {
        return intents;
    }

    public void setIntents(List<IntentInfo> intents) {
        this.intents = intents;
    }
    public void addIntent(IntentInfo intentInfo)
    {
        intents.add(intentInfo);
    }
}
