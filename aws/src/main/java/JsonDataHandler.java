import java.io.File;
import java.io.FileReader;
import java.util.*;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;

import javax.swing.text.StyledEditorKit;


public class JsonDataHandler
{
    FileReader jsondata;
    String botname;
    String botinfo;
    List<JsonIntentData> intents=new ArrayList<JsonIntentData>();

    JsonDataHandler(FileReader file) throws Exception
    {
        this.jsondata=file;

        //Parsing json string into java object..

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(jsondata);
        JSONObject jsonObject=(JSONObject)obj;
        botname=(String)jsonObject.get("botname");
        botinfo=(String)jsonObject.get("botinfo");
       // System.out.println(botname);
        JSONArray jsonArray=(JSONArray) jsonObject.get("intents");
        for(int i=0;i<jsonArray.size();i++)
        {
            JSONObject jsonObject1=(JSONObject) jsonArray.get(i);
            JsonIntentData jsonIntentData=new JsonIntentData();
            jsonIntentData.setIntentname((String) jsonObject1.get("intentname"));
            jsonIntentData.setDescription((String)jsonObject1.get("description"));

            JSONArray jsonArray1=(JSONArray) jsonObject1.get("utterences");
            List<String> tmp=new ArrayList<String>();
            for(int j=0;j<jsonArray1.size();j++)
            {
                tmp.add((String) jsonArray1.get(j));
            }
            tmp.add(jsonIntentData.getDescription());
            jsonIntentData.setUtterences(tmp);

            JSONObject jsonObject2=(JSONObject) jsonObject1.get("fulfillment");


            jsonIntentData.setBaseurl((String)jsonObject2.get("baseurl"));
            jsonIntentData.setMethod((String)jsonObject2.get("method"));
            JSONArray jsonArray2=(JSONArray) jsonObject2.get("requestbody");

            Map<String,Boolean> tmp2=new LinkedHashMap<String,Boolean>();
            for(int k=0;k<jsonArray2.size();k++)
            {
                JSONObject jsonObject3=(JSONObject) jsonArray2.get(k);
                boolean flg=false;
                if(((String)jsonObject3.get("constraint")).equals("required"))
                    flg=true;
                tmp2.put((String)jsonObject3.get("name"),new Boolean(flg));

            }
            jsonIntentData.setRequestbody(tmp2);

            JSONArray jsonArray3=(JSONArray)jsonObject2.get("requestparameters");
            List<String> list=new LinkedList<>();
            for(int m=0;m<jsonArray3.size();m++)
            {
                list.add((String) jsonArray3.get(m));
            }
            jsonIntentData.setRequestparameters(list);

            intents.add(jsonIntentData);



        }




    }
    public String toString()
    {
        System.out.println("Bot:"+botname);
        System.out.println("Intents..");
        Iterator<JsonIntentData> iterator=intents.iterator();
        while(iterator.hasNext())
        {
            JsonIntentData jsonIntentData=iterator.next();
            System.out.println("Intent name:"+jsonIntentData.getIntentname());
            System.out.println("utterences..");
            List<String> utterences=jsonIntentData.getUtterences();
            Iterator<String> stringIterator=utterences.iterator();

            while(stringIterator.hasNext())
            {
                System.out.println(stringIterator.next());
            }

            System.out.println("baseurl:"+jsonIntentData.getBaseurl());
            System.out.println("method:"+jsonIntentData.getMethod());

            System.out.println("Request body params..");
            Map<String,Boolean> stringBooleanMap=jsonIntentData.getRequestbody();
            Iterator<String> keys=stringBooleanMap.keySet().iterator();

            while(keys.hasNext())
            {
                System.out.println(keys.next());
            }

        }
        return  "";
    }

    public String getBotinfo() {
        return botinfo;
    }

    public void setBotinfo(String botinfo) {
        this.botinfo = botinfo;
    }

    public FileReader getJsondata() {
        return jsondata;
    }

    public void setJsondata(FileReader jsondata) {
        this.jsondata = jsondata;
    }

    public String getBotname() {
        return botname;
    }

    public void setBotname(String botname) {
        this.botname = botname;
    }

    public List<JsonIntentData> getIntents() {
        return intents;
    }

    public void setIntents(List<JsonIntentData> intents) {
        this.intents = intents;
    }
}
