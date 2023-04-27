/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package termproject;

/**
 *
 * @author micahvranyes
 */
public class searchResult {
    private boolean found;
    private TFNode node;
    private int index;
    
    public searchResult(boolean fnd, int idx, TFNode n) {
        found = fnd;
        node = n;
        index = idx;
    }
    
    public boolean wasFound() {
        return found;
    }
    
    public TFNode getNode() {
        return node;
    }
    
    public int getIndex() {
        return index;
    }
}
