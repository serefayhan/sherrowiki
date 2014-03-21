package com.serefayhan.sherrowiki;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
/**
 *
 * @author Seref AYHAN <ben@serefayhan.com>
 */
public class SherroWikiBusiness {
    
    public SherroWikiBusiness(){
        
    }
    
    public SherroWikiBusiness initDatabase(){
        
        try{
            new SherroWikiDB().CreateTables(MainForm.dbConnection);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        
        return this;
    }
    
    public SherroWikiBusiness refreshWikiTree(SherroWikiModel wikiModel){
        ResultSet wikis = null;
        
        try{
            wikis = new SherroWikiDB().GetWikiList(MainForm.dbConnection);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        
        while(((SherroWikiNode)wikiModel.getRoot()).getChildCount() > 0){
            wikiModel.removeNodeFromParent((DefaultMutableTreeNode)((SherroWikiNode)wikiModel.getRoot()).getChildAt(0));
        }
        
        try{
            while(wikis!=null && wikis.next()){
                wikiModel.insertNodeInto(
                    new SherroWikiNode(wikis.getLong("ID"), wikis.getString("TITLE")), 
                    (SherroWikiNode)wikiModel.getRoot(), 
                    wikiModel.getChildCount(wikiModel.getRoot())
                );
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        
        return this;
    }
    
    public SherroWikiBusiness createBlankWikiPage(String wikiName){
        try{
            new SherroWikiDB().CreateWiki(MainForm.dbConnection, wikiName);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        
        return this;
    }
    
    public SherroWikiBusiness deleteWikiPage(long pageID){
        try{
            new SherroWikiDB().DeleteWiki(MainForm.dbConnection, pageID);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        
        return this;
    }
    
    public String renderWikiPage(Long pageID){
        String wikiText = null, render;
        
        try{
            wikiText = new SherroWikiDB().ReadWiki(MainForm.dbConnection, pageID);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        
        if(wikiText != null)
        {
            StringWriter writer = new StringWriter();
            HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
            builder.setEmitAsDocument(false);
            builder.lineBreak();
            MarkupLanguage language = new TextileLanguage();
            
            MarkupParser parser = new MarkupParser(language, builder);
            
            parser.parse(wikiText);
            render = writer.toString();
            
            writer.flush();
            try {
                writer.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }else{
            render = " ";
        }
        
        return render;
    }
    
    public boolean exportWikiPage(Long pageID, String fileName){
        String wikiText = null, render;
        boolean response = true;
        
        try{
            wikiText = new SherroWikiDB().ReadWiki(MainForm.dbConnection, pageID);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        
        if(wikiText != null)
        {
            StringWriter writer = new StringWriter();
            HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
            MarkupLanguage language = new TextileLanguage();
            
            MarkupParser parser = new MarkupParser(language, builder);
            
            parser.parse(wikiText);
            render = writer.toString();
            
            writer.flush();
            try {
                writer.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
            
            try{
                try (FileWriter f = new FileWriter(fileName, false)) {
                    f.write(render);
                    f.flush();
                    f.close();
                }
            }catch(Exception ex){
                JOptionPane.showMessageDialog(null, ex);
                response = false;
            }
            
        }else{
            response = false;
        }
        
        return response;
    }    
    
    public String getWikiSource(Long pageID){
        String wikiText = null;
        try{
            wikiText = new SherroWikiDB().ReadWiki(MainForm.dbConnection, pageID);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        
        return wikiText;
    }
    
    public void setWikiSource(Long pageID, String pageSource){
               
        try{
            new SherroWikiDB().UpdateWikiContext(MainForm.dbConnection, pageID, new StringReader(pageSource));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
}
