//Carson Lutterloh
//cjl150530

package BSTree;

public class Node implements Comparable <Node> {
    //Initialize variables
    String title;
    int available;
    int rented;
    Node left = null, right = null;
    
    //Constructors (Any default node without all required info would cause problems so we don't have one)
    public Node(String title, int available, int rented) {
        this.title = title;
        this.available = available;
        this.rented = rented;
    }
    
    //Accessors
    public String getTitle()
    { 
        return title;   
    }
    public int getAvailable()
    { 
        return available;   
    }
    public int getRented()
    { 
        return rented;   
    }
    public Node getLeft()
    { 
        return left;  
    }
    public Node getRight()
    { 
        return right;  
    }
    
    //Mutators
    public void setTitle(String title)
    {  
        this.title = title; 
    }
    public void setAvailable(int available)
    {  
        this.available = available; 
    }
    public void setRented(int rented)
    {  
        this.rented = rented; 
    }
    public void setLeft(Node n)
    {  
        left = n;    
    }
    public void setRight(Node n)
    {  
        right = n;    
    }
    
    //Other methods
    //"Use the DVD title to determine node placement in the tree" Thus, our compare sorts by title
    @Override
    public int compareTo(Node n)    //THIS ONLY APPLIES IF WE'RE COMPARING TWO NODES
    {
        return this.title.compareTo(n.title);
    }
}
