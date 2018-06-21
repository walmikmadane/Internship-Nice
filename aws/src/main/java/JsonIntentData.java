import java.util.*;

public class JsonIntentData
{
    String intentname;
    String description;
    List<String> utterences=new ArrayList<String>();
    String baseurl;
    String method;
    Map<String,Boolean> requestbody=new LinkedHashMap<String, Boolean>();

    public List<String> getRequestparameters() {
        return requestparameters;
    }

    public void setRequestparameters(List<String> requestparameters) {
        this.requestparameters = requestparameters;
    }

    List<String> requestparameters=new LinkedList<>();

    public String getIntentname() {
        return intentname;
    }

    public void setIntentname(String intentname) {
        this.intentname = intentname;
    }

    public List<String> getUtterences() {
        return utterences;
    }

    public void setUtterences(List<String> utterences) {
        this.utterences = utterences;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Boolean> getRequestbody() {
        return requestbody;
    }

    public void setRequestbody(Map<String, Boolean> requestbody) {
        this.requestbody = requestbody;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
