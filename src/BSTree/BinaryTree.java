//cjlutterloh
//CS 2336

package BSTree;

import java.io.*;

public class BinaryTree {
    //Initialize Variables
    Node root = null;
    
    //Constructors
    public BinaryTree() {
        root = null;
    }
    public BinaryTree(Node n) {
        root = n;
    }
    
    //Accessor
    public Node getRoot()
    {   return root;    }
    
    //Mutator
    public void setRoot(Node n)
    {   root = n;   }
    
    //Other methods
    //Helper function
    public Node search(String title) {
        return search(root, title);
    }
    private Node search(Node root, String title1) { //(We don't want people to use this directly - aka, use helper function instead)
        if (root == null) 
            return null;
        else {
            //If there's still a node and one matches, return true!
            if (title1.compareToIgnoreCase(root.title) == 0)
                return root;
            else if (title1.compareToIgnoreCase(root.title) < 0)
                return search(root.left, title1);
            else
                return search(root.right, title1);
        }
    }
    
    //Double helper function
    public void insert(String title, int available, int rented)
    {
        Node temp = new Node(title, available, rented);
        insert(temp);
    }
    //Helper function
    public void insert(Node n)
    {
        insert(root, n);
    }
    private void insert(Node n1, Node n2) //(We don't want people to use this directly - aka, use helper function instead)
    {
        if (root != null)
	{
            //Checks if the node should go left, and continues until it finds an open space (null space)
            if(n2.compareTo(n1) < 0)    {
                if (n1.left != null) 
                    insert(n1.left, n2);
                else 
                    n1.left = n2;
            }
            //Checks if the node should go right, and continues until it finds an open space (null space)
            else if(n2.compareTo(n1) > 0) {
                if (n1.right != null)
                    insert(n1.right, n2);
                else
                    n1.right = n2;
            }
        }
        //If this is the first insert of the list, root will be null, so this initializes it
        else
            root = n2;
    }
    
    //Helper function(s)
    public Node delete(String title)
    {
        //Takes care of a tree with one node
        if (title.compareToIgnoreCase(root.title) == 0 && root.left == null && root.right == null)  {
            Node hold = root;
            root = null;
            return hold;
        }
        return delete(root, title);
    }
    private Node delete(Node n, String title) {
        if (n == null) {
            System.out.println("Unsuccessful deletion! " + title + " does not exist in the tree");
        }
        else if (title.compareToIgnoreCase(n.title) < 0)
            n.left = delete(n.left, title);
        else if (title.compareToIgnoreCase(n.title) > 0)
            n.right = delete(n.right, title);
        //Hitting this else means we found a matching node
        else {
            //n has one child (or none) on the right
            if (n.left == null) 
                n = n.right;    //Will change the node or make it null
            else {
                Node replacement = maxNode(n.left);
                //Swap out values (This invalidates any name corresponding to a specific node - which we don't need)
                n.title = replacement.title;
                n.available = replacement.available;
                n.rented = replacement.rented;
                //Re-call function to get rid of replacement
                n.left = delete(n.left, replacement.title);
            }
        }
        return n;   //Returns node of root
    }
    //This finds the greatest node on the left to replace the current node (It will be greater than all left nodes, and less than all right nodes)
    private Node maxNode(Node n) { //(Private because it's only meant to be used with delete())
        if(n.right == null)
            return n;
        else {
            return maxNode(n.right);
        }
    }
    
    //Recursively prints the tree in order of least to greatest (One prints to system, and the other to a file
    //Helper function
    public void inorder() {
        inorder(root);
    }
    public void inorder(PrintWriter output) {
        inorder(root, output);
    }
    private void inorder(Node root) { //(We don't want people to use this directly - aka, use helper function instead)
        if (root == null) {
            return;
        }
        inorder(root.left);
        System.out.println(root.title + " - Available: " + root.available + " - Rented: " + root.rented);
        inorder(root.right);
    }
    private void inorder(Node root, PrintWriter output) { //(We don't want people to use this directly - aka, use helper function instead)
        if (root == null) {
            return;
        }
        inorder(root.left, output);
        output.printf("%-40s%-8d%-8d%n", root.title.replace("\"", ""), root.available, root.rented);
        inorder(root.right, output);
    }
    
    //Checks for an empty tree
    public boolean isEmpty() {
        return root == null;
    }
}
