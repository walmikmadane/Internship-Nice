import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager
{
    Properties prop = new Properties();
    InputStream input = null;

    PropertiesManager() throws Exception
    {
        input = getClass().getClassLoader().getResourceAsStream("config.properties");
        prop.load(input);
    }
    public void setProperty(String key,String value)
    {


        prop.setProperty(key,value);

    }
    public String getProperty(String key)
    {
        return prop.getProperty(key);
    }
}
