import java.io.FileReader;

public class UserConfigureInput
{
    JsonDataHandler jsonDataHandler;

    public JsonDataHandler getJsonDataHandler() {
        return jsonDataHandler;
    }
    public JsonDataHandler getJsonFileInput() throws Exception
    {
        jsonDataHandler=new JsonDataHandler(new FileReader("D:\\telecom.json"));
        return  jsonDataHandler;
    }
}
