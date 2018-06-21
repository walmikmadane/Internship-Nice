public class IntentInfo
{
    String intentName;
    String baseurl;
    String methodType;


    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }


    IntentInfo(String intentName,String baseurl,String methodType)
    {
        this.intentName=intentName;
        this.baseurl=baseurl;
       this.methodType=methodType;
    }
    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }
}
