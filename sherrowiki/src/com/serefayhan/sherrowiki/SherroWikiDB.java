package com.serefayhan.sherrowiki;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Seref AYHAN <ben@serefayhan.com>
 */
public class SherroWikiDB {
    
    public SherroWikiDB(){
        
    }
    
    public ResultSet GetWikiList(Connection conn) throws Exception{
        Class.forName("org.h2.Driver");
        
        Statement stat = conn.createStatement();
        ResultSet wikis = stat.executeQuery("SELECT ID,TITLE FROM WIKI");

        return wikis;
    }
    
    public void CreateWiki(Connection conn, String wikiName) throws Exception{
        Class.forName("org.h2.Driver");
        
        String q = "INSERT INTO WIKI(TITLE) VALUES(?)";
        
        PreparedStatement stat = conn.prepareStatement(q);
        stat.setString(1, wikiName);
        
        stat.executeUpdate();
    }
    
    public String ReadWiki(Connection conn, Long id) throws Exception{
        StringBuilder wikiContext = new StringBuilder();
        Class.forName("org.h2.Driver");
        
        String q = "SELECT CONTEXT FROM WIKI WHERE ID=?";
        
        PreparedStatement stat = conn.prepareStatement(q);
        stat.setLong(1, id);
        
        ResultSet context = stat.executeQuery();
        
        if(context.first() && context.getCharacterStream("CONTEXT") != null){
        
            BufferedReader wikiReader = new BufferedReader(context.getCharacterStream("CONTEXT"));
            String l;
            wikiContext.append("");

            while((l = wikiReader.readLine()) != null){
                wikiContext.append(l).append(System.lineSeparator());
            }
        }
        
        return wikiContext.toString();
    }
    
    public void UpdateWikiContext(Connection conn, Long id, Reader context) throws Exception{
        Class.forName("org.h2.Driver");
        
        String q = "UPDATE WIKI SET CONTEXT=? WHERE ID=?";
        
        PreparedStatement stat = conn.prepareStatement(q);
        stat.setCharacterStream(1, context);
        stat.setLong(2, id);
        
        stat.executeUpdate();
    }
    
    public void DeleteWiki(Connection conn, Long id) throws Exception{
        Class.forName("org.h2.Driver");
        
        String q = "DELETE WIKI WHERE ID=?";
        
        PreparedStatement stat = conn.prepareStatement(q);
        stat.setLong(1, id);
        
        stat.executeUpdate();
    }
    
    public boolean CreateTables(Connection conn) throws Exception{
        Class.forName("org.h2.Driver");
        
        Statement stat = conn.createStatement();
        stat.execute("CREATE TABLE IF NOT EXISTS WIKI(ID BIGINT AUTO_INCREMENT(1,1) PRIMARY KEY, TITLE VARCHAR(250) NOT NULL, CONTEXT CLOB NULL)");
        
        return true;
    }
    
    public Connection connectDB() throws Exception{
                
        Connection conn;
        
        conn = DriverManager.getConnection(
                "jdbc:h2:~/sherrowiki/sherrowiki;DB_CLOSE_DELAY=0;"
        );
        
        return conn;
    }
}
