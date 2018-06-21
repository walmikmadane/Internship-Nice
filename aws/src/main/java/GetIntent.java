import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuilding;
import com.amazonaws.services.lexmodelbuilding.AmazonLexModelBuildingClientBuilder;
import com.amazonaws.services.lexmodelbuilding.model.GetIntentRequest;
import com.amazonaws.services.lexmodelbuilding.model.GetIntentResult;
import com.amazonaws.services.lexmodelbuilding.model.Slot;

import java.util.List;

public class GetIntent
{
    String intentName;
    GetIntent(String intent)
    {
        this.intentName=intent;
    }
    public String getChecksum()
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
        GetIntentRequest request = new GetIntentRequest().withName(intentName).withVersion("$LATEST");
        GetIntentResult response = client.getIntent(request);
        return response.getChecksum();
    }

    public List<Slot> getSlots()
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
        GetIntentRequest request = new GetIntentRequest().withName(intentName).withVersion("$LATEST");
        GetIntentResult response = client.getIntent(request);
        return response.getSlots();
    }
}
