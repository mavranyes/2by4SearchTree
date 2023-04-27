package termproject;

import java.util.Random;

/**
 * Title:        Term Project 2-4 Trees
 * Description:  An abstract data structure that takes in a key and object
 *               for insertion and a key for removal.
 * @author       Micah Vranyes
 */
public class TwoFourTree implements Dictionary {

    private Comparator treeComp;
    private int size = 0;
    private TFNode treeRoot = null;

    public TwoFourTree(Comparator comp) {
        treeComp = comp;
    }

    private TFNode root() {
        return treeRoot;
    }

    private void setRoot(TFNode root) {
        treeRoot = root;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Searches dictionary to determine if key is present
     * @param key to be searched for
     * @return object corresponding to key; null if not found
     */
    @Override
    public Object findElement(Object key) {
        searchResult rslts = this.findNodeWith(key);
        if(!rslts.wasFound()) {
            return null;
        }
        return rslts.getNode().getItem(rslts.getIndex()).element();
    }
    
    /**
     * Determines the index position where the key belongs in the node.
     * @param node to search through
     * @param key that the index is found for
     * @return the index where the key belongs in that node
     */
    private int findFirstGreaterThanOrEqual(TFNode node, Object key) {
        int i;
        for(i = 0; i < node.getNumItems(); i++) {
            if(treeComp.isGreaterThanOrEqualTo(node.getItem(i).key(), key)) {
                break;
            }
        }
        return i;
    }
    
    /**
     * Finds the parent element index of the inputted node.
     * @param node the node which you want to find the parent element of
     * @return the parent element index of the node
     */
    private int whatChildIsThis(TFNode node) {
        TFNode parent = node.getParent();
        int i;
        for(i = 0; i < parent.getNumItems() + 1; i++) {
            if(parent.getChild(i) == node) {
                break;
            }
        }
        return i;
    }
    
    /**
     * Finds the node containing the given object key.
     * @param key the element to find
     * @return a data object that contains if the key was found,
     * the index of it, and the node it is in/should be in
     */
    private searchResult findNodeWith(Object key) {
        TFNode node = treeRoot;
        int idx = this.findFirstGreaterThanOrEqual(node, key);
        while(node != null) {
            //Checks if the element matches the key
            if(idx != node.getNumItems() && treeComp.isEqual(node.getItem(idx).key(), key)) {
                return new searchResult(true, idx, node);
            }
            //Checks to see if it should not iterate down
            if(node.getChild(idx) == null) {
                break;
            }
            //Iterates down
            node = node.getChild(idx);
            idx = this.findFirstGreaterThanOrEqual(node, key);
        }
        return new searchResult(false, idx, node);
    }

    /**
     * Finds the in order successor node of the inputted element.
     * @param node the node which contains the element
     * @param idx the index of the element to find the in order successor
     * @return the node where the in order successor lies
     */
    private TFNode getInOrderSuccessor(TFNode node, int idx) {
        idx++;
        if(node.getChild(idx) != null) {
            //Goes right one
            node = node.getChild(idx);
            idx = 0;
            //Loops through left child
            while(node.getChild(idx) != null) {
                node = node.getChild(idx);
            }
        }
        return node;
    }
    
    /**
     * Inserts provided element into the Dictionary
     * @param key of object to be inserted
     * @param element to be inserted
     */
    @Override
    public void insertElement(Object key, Object element) throws InvalidObjectException {
        if(!treeComp.isComparable(key)) {
            throw new InvalidObjectException("Error: Invalid key inputted");
        }
        Item itm = new Item(key, element);
        //Special case: first insert
        if(size == 0) {
            treeRoot = new TFNode();
            treeRoot.addItem(0, itm);
            size++;
            return;
        }
        //Finds and initalizes objects
        searchResult rslts = this.findNodeWith(key);
        TFNode node = rslts.getNode();
        int idx = rslts.getIndex();
        //Checks if the element is already present and properly inserts it
        if(!rslts.wasFound()) {
            node.insertItem(idx, itm);
        }
        else{
            node = this.getInOrderSuccessor(node, idx);
            if(node.getChild(0) == null) {
                idx = this.findFirstGreaterThanOrEqual(node, key);
                node.insertItem(idx, itm);
            }
        }
        size++;
        //Check for overflow
        while(node.getNumItems() > node.getMaxItems()) {
            //Creates second node
            TFNode scnd = new TFNode();
            scnd.addItem(0, node.getItem(3));
            //Connect children to second node
            TFNode a = node.getChild(3);
            if(a != null) {
                scnd.setChild(0, a);
                a.setParent(scnd);
            }
            TFNode b = node.getChild(4);
            if(b != null) {
                scnd.setChild(1, b);
                b.setParent(scnd);
            }
            
            Item data = node.getItem(2);            
            TFNode parent = node.getParent();
            //Push third element up
            if(parent == null) {
                //Creates new root and inserts the overflown element
                idx = 0;
                parent = new TFNode();
                parent.addItem(idx, data);
                this.setRoot(parent);
            } 
            else{
                //Inserts element into parent
                idx = this.whatChildIsThis(node);
                parent.insertItem(idx, data);
            }
            //Fixes connections with the parent node
            node.setParent(parent);
            scnd.setParent(parent);
            parent.setChild(idx,node);
            parent.setChild(idx + 1, scnd);
            //Fixes the overflown node's elements and children
            node.deleteItem(3);
            node.deleteItem(2);
            node.setChild(3, null);
            node.setChild(4, null);
            //Iterate upward
            node = node.getParent();
        }
    }

    /**
     * Searches dictionary to determine if key is present, then
     * removes and returns corresponding object
     * @param key of data to be removed
     * @return object corresponding to key
     * @exception ElementNotFoundException if the key is not in dictionary
     */
    @Override
    public Object removeElement(Object key) throws ElementNotFoundException, InvalidObjectException {
        if(!treeComp.isComparable(key)) {
            throw new InvalidObjectException("Error: Invalid key inputted");
        }
        //Finds and initalizes objects
        Object obj;
        searchResult rslt = this.findNodeWith(key);
        TFNode node = rslt.getNode();
        int idx = rslt.getIndex();
        TFNode leafNode = this.getInOrderSuccessor(node, idx);
        
        if(!rslt.wasFound()) {
            this.printAllElements();
            throw new ElementNotFoundException("Error: Element not found");
        }
        //Checks if the node is internal and properly removes the element
        obj = node.getItem(idx).element();
        if(node.getChild(0) == null) {
            node.removeItem(idx);
        }
        else {
            //Swaps internal and leaf elements then removes leaf one
            node.deleteItem(idx);
            node.addItem(idx, leafNode.getItem(0));
            leafNode.removeItem(0);
        }
        size--;
        //Check for underflow
        while(leafNode.getNumItems() == 0 && leafNode.getNumItems() == 0) {//Has underflow
            //Checks if it underflowed in the root
            if(leafNode == treeRoot) {
                TFNode nRoot = leafNode.getChild(0);
                if(nRoot != null) {
                    nRoot.setParent(null);
                }
                treeRoot = nRoot;
                break;
            }
            //Initalizes objects used across all cases
            int prntIdx = this.whatChildIsThis(leafNode);
            TFNode parent = leafNode.getParent();
            //Left Transfer
            if(prntIdx != 0) {
                TFNode lftSib = leafNode.getParent().getChild(prntIdx - 1);
                if(lftSib.getNumItems() > 1) {
                    int lftItemPos = lftSib.getNumItems() - 1;
                    //Saves and removes from parent
                    Item temp = leafNode.getParent().getItem(prntIdx - 1);
                    parent.deleteItem(prntIdx - 1);
                    parent.addItem(prntIdx - 1, lftSib.getItem(lftItemPos));
                    //Fixes underflowed node
                    leafNode.insertItem(0, temp);
                    leafNode.setChild(1,leafNode.getChild(0));
                    leafNode.setChild(0, lftSib.getChild(lftItemPos + 1));
                    if(leafNode.getChild(0) != null) {
                        leafNode.getChild(0).setParent(leafNode);
                    }
                    //Fixes sibling node
                    lftSib.deleteItem(lftItemPos);
                    lftSib.setChild(lftItemPos + 1, null);
                    break;
                }
            }
            //Right Transfer
            if(prntIdx < leafNode.getParent().getNumItems() && leafNode.getParent().getChild(prntIdx + 1).getNumItems() > 1) {
                TFNode rghtSib = leafNode.getParent().getChild(prntIdx + 1);
                if(rghtSib.getNumItems() > 1) {
                    int rghtItemPos = 0;
                    //Saves and removes from parent
                    Item temp = leafNode.getParent().getItem(prntIdx);
                    parent.deleteItem(prntIdx);
                    parent.addItem(prntIdx, rghtSib.getItem(rghtItemPos));
                    //Fixes underflowed node
                    leafNode.insertItem(0, temp);
                    leafNode.setChild(1, rghtSib.getChild(rghtItemPos));
                    if(leafNode.getChild(1) != null) {
                        leafNode.getChild(1).setParent(leafNode);
                    }
                    //Fixes sibling node
                    rghtSib.removeItem(rghtItemPos);
                    break;
                }
            }
            //Left Fusion
            if(prntIdx != 0) {
                prntIdx--;
                TFNode lftSib = leafNode.getParent().getChild(prntIdx);
                //Adds to and fixes childrn of the left sibling
                lftSib.addItem(1, parent.getItem(prntIdx));
                lftSib.setChild(2, leafNode.getChild(0));
                if(lftSib.getChild(2) != null) {
                    lftSib.getChild(2).setParent(lftSib);
                }
                //Removes from and fixes children of the parent
                parent.removeItem(prntIdx);
                parent.setChild(prntIdx, lftSib);
                leafNode = leafNode.getParent();
                continue;
            }
            //Right Fusion
            TFNode rghtSib = leafNode.getParent().getChild(prntIdx + 1);
            //Adds to and fixes childrn of the right sibling
            rghtSib.insertItem(0, parent.getItem(prntIdx));
            rghtSib.setChild(0, leafNode.getChild(0));
            if(rghtSib.getChild(0) != null) {
                rghtSib.getChild(0).setParent(rghtSib);
            }
            //Removes from parent
            parent.removeItem(prntIdx);
            //Iterates upward
            leafNode = leafNode.getParent();
        }
        return obj;
    }
    
    /**
     * Creates, populates, and removes elements from search tree 
     * with two data sets, linear and second random, 
     * while checking for accuracy.
     * @param args 
     */
    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);
        
        myTree = new TwoFourTree(myComp);

        if(false) {
            final int TEST_SIZE = 10000;
            for (int i = 0; i < TEST_SIZE; i++) {
                myTree.insertElement(i, i);
            }
            System.out.println("removing");
            for (int i = 0; i < TEST_SIZE; i++) {
                int out = (Integer) myTree.removeElement(i);
                if (out != i) {
                    throw new TwoFourTreeException("main: wrong element removed");
                }
                if (i > TEST_SIZE - 15) {
                    myTree.printAllElements();
                    System.out.println("----------------");
                }
            }
        }
        
        if(true) {//Tests tree with random numbers
            Random randGen = new Random();
            final int INSERTS = 50000;//Manipulate number of elements HERE
            final int numLength = 4;//Manipulate length (between 1 and 10) of elements HERE
            int[] list = new int[INSERTS];
            for (int i = 0; i < INSERTS; i++) {
                int rand = randGen.nextInt()/(1000000000 / (int) Math.pow(10, numLength - 1));
                myTree.insertElement(rand,rand);
                list[i] = rand;
            }
            myTree.printAllElements();
            System.out.println("removing");
            for (int i = 0; i < INSERTS; i++) {
                    int out = (Integer) myTree.removeElement(list[i]);
                if (out != list[i]) {
                    throw new TwoFourTreeException("main: wrong element removed");
                }
                if (i > INSERTS - 15) {
                    myTree.printAllElements();
                    System.out.println("----------------");
                }
            }
        }
    }

    public void printAllElements() {
        int indent = 0;
        if (root() == null) {
            System.out.println("The tree is empty");
        }
        else {
            printTree(root(), indent);
        }
    }

    public void printTree(TFNode start, int indent) {
        if (start == null) {
            return;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        printTFNode(start);
        indent += 4;
        int numChildren = start.getNumItems() + 1;
        for (int i = 0; i < numChildren; i++) {
            printTree(start.getChild(i), indent);
        }
    }

    public void printTFNode(TFNode node) {
        int numItems = node.getNumItems();
        for (int i = 0; i < numItems; i++) {
            System.out.print(((Item) node.getItem(i)).element() + " ");
        }
        System.out.println();
    }
    
    // checks if tree is properly hooked up, i.e., children point to parents
    public void checkTree() {
        checkTreeFromNode(treeRoot);
    }
    
    private void checkTreeFromNode(TFNode start) {
        if (start == null) {
            return;
        }

        if (start.getParent() != null) {
            TFNode parent = start.getParent();
            int childIndex = 0;
            for (childIndex = 0; childIndex <= parent.getNumItems(); childIndex++) {
                if (parent.getChild(childIndex) == start) {
                    break;
                }
            }
            // if child wasn't found, print problem
            if (childIndex > parent.getNumItems()) {
                System.out.println("Child to parent confusion");
                printTFNode(start);
            }
        }

        if (start.getChild(0) != null) {
            for (int childIndex = 0; childIndex <= start.getNumItems(); childIndex++) {
                if (start.getChild(childIndex) == null) {
                    System.out.println("Mixed null and non-null children");
                    printTFNode(start);
                }
                else {
                    if (start.getChild(childIndex).getParent() != start) {
                        System.out.println("Parent to child confusion");
                        printTFNode(start);
                    }
                    for (int i = childIndex - 1; i >= 0; i--) {
                        if (start.getChild(i) == start.getChild(childIndex)) {
                            System.out.println("Duplicate children of node");
                            printTFNode(start);
                        }
                    }
                }

            }
        }

        int numChildren = start.getNumItems() + 1;
        for (int childIndex = 0; childIndex < numChildren; childIndex++) {
            checkTreeFromNode(start.getChild(childIndex));
        }

    }
}
