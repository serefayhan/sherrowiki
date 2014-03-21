package com.serefayhan.sherrowiki;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Seref AYHAN <ben@serefayhan.com>
 */
public class SherroWikiNode extends DefaultMutableTreeNode{
    
    private final String title;
    private final Long id;
    
    public SherroWikiNode(String _title){
        this.title = _title;
        this.id = Long.parseLong("0");
    }
    
    public SherroWikiNode(Long _id, String _title){
        this.id = _id;
        this.title = _title;
    }
    
    public Long getNodeID(){
        return this.id;
    }
    
    @Override
    public String toString(){
        return this.title;
    }
}
