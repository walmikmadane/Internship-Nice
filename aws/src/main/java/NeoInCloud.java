import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.summary.ResultSummary;

public class NeoInCloud
{
    public static void main(String[] args)
    {
        Driver driver = GraphDatabase.driver( "bolt://hobby-holocknhlicigbkeebkgcfal.dbs.graphenedb.com:24786", AuthTokens.basic( "walmik", "b.MB5SXqHTU7i3.ZDklj2e2aMQiIKF1" ) );

        Session session = driver.session();
        String tmp="walmik";

       /* session.run("CREATE (n:Person {name:'"+tmp+"'})");
        StatementResult result = session.run("MATCH (n:Person) RETURN n.name AS name");

        while ( result.hasNext() )
        {
            Record record = result.next();
            System.out.println( record.get("name").asString() );
        }*/
        String intentname="goodbye";
        String tmp2="Bye";
        StatementResult  statementResult=session.run("match(n:utterence) where lower(n.value)=lower('"+tmp2+"') return n");
        System.out.println(statementResult.hasNext());
        if(statementResult.hasNext())
        {
            System.out.println("utterence already present");
            session.run("match(n:intent),(b:utterence) where n.value='"+intentname+"'"+" and b.value='"+tmp2+"' merge(b)-[:utterenceOf]->(n)");

        }
        else
        {
            System.out.println("utterence created first...");
            session.run("match(n:intent) where n.value='"+intentname+"' create(a:utterence{value:'"+tmp2+"'})-[:utterenceOf]->(n)");

        }

         session.close();
        driver.close();

    }
}
