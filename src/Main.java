//cjlutterloh
//CS 2336

import BSTree.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException{
        //Introduce and validate necessary files
        File inventoryFile = new File("inventory.dat");
        //Stops program if the file doesn't exist
        checkFile(inventoryFile);
        File transactionFile = new File("transaction.log");
        checkFile(transactionFile);
        PrintWriter errorFile = new PrintWriter("error.log");
        PrintWriter outputFile = new PrintWriter("redbox_kiosk.txt");
        
        //Read inventory to tree
        BinaryTree mainTree = readInventoryToTree(inventoryFile);
        //Process transactions
        readTransactionToTree(transactionFile, errorFile, mainTree);
        //Print results to file
        printReport(mainTree, outputFile);
        
        //Close all output files
        errorFile.close();
        outputFile.close();
    }
    
    public static void checkFile (File filename) {
        if (!filename.exists()) {
                System.out.println("Error! The file " + filename + " does not exist in the directory, but is necessary to continue. Exiting...");
                System.exit(-1);
            }
    }
    
    public static BinaryTree readInventoryToTree (File fileName) throws IOException, NumberFormatException{
        String line;
        int count = 0;
        BinaryTree tempTree = new BinaryTree();
        try (
            Scanner lineReader = new Scanner(fileName);
        ) {
            //Reads in each line of the file
            while(lineReader.hasNext()) {
                line = lineReader.nextLine();
                if (line.length() > 0) {    //Ensures a blank new line is not looked at
                    //This separates the line into words excluding whitespace and commas. It also keeps anything inside double quotes (DOES NOT REMOVE QUOTES)
                    String[] words = line.split("(\\s*(\\s|,)\\s*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); //(\\s*(\\s|,)\\s*) removes any whitespace or commas
                    //Ideally, words[0] = title, words[1] = available, words[2] = rented
                    count += 1;
                    //The following validates/inserts each line 
                    if (words.length != 3) {
                        System.out.println("Line " + count + " of inventory.dat should have 3 items separated by spaces or 1 comma. This line has been skipped.");
                    }
                    else {
                        //Only inserts a node if the movie has at least 1 available or rented copy
                        if (Integer.parseInt(words[1]) > 0 || Integer.parseInt(words[2]) > 0)
                            tempTree.insert(words[0], Integer.parseInt(words[1]), Integer.parseInt(words[2]));
                        //words[0].replace("\"", "") stores movies without quotes, in case we want to change that
                        else 
                            System.out.println("Error... Line " + count + " has a zero for both amounts and was not added");
                    }
                }
            }
        }
        catch (NumberFormatException ex)
            {
                System.out.println("Exiting... Your " + fileName + " file (line " + count + ") contains a line that is not: String, int, int");
                System.exit(-1);
            }
        return tempTree;
    }
    
    public static void readTransactionToTree (File fileName, PrintWriter errorFile, BinaryTree mainTree) throws IOException {
        String line = "";
        int count = 0;
        try (
            Scanner lineReader = new Scanner(fileName);
        ) {
            //Reads in each line of the file
            while(lineReader.hasNext()) {
                line = lineReader.nextLine();
                if (line.length() > 0) {    //Ensures a blank new line is not looked at
                    //This separates the line into words excluding whitespace and commas. It also keeps anything inside double quotes (DOES NOT REMOVE QUOTES)
                    String[] words = line.split("(\\s*(\\s|,)\\s*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); //(\\s*(\\s|,)\\s*) removes any whitespace or commas
                    //Ideally, words[0] = add/rent/remove/return, words[1] = title, and for add/remove words[2] = # of copies
                    count += 1;
                    try {
                        //Checks the first word
                        if (!words[0].equals("add") && !words[0].equals("remove") && !words[0].equals("rent")&& !words[0].equals("return")) 
                            errorFile.println(line);
                        //Checks that there's the correct number of arguments
                        else if ((words[0].equals("add") || words[0].equals("remove")) && (!(words.length == 3)))
                            errorFile.println(line);
                        //Checks that there's the correct number of arguments
                        else if ((words[0].equals("rent") || words[0].equals("return")) && (!(words.length == 2)))
                            errorFile.println(line);
                        //Ensures there are quotes around the movie title
                        else if (words[1].charAt(0) != '"' || words[1].charAt(words[1].length() - 1) != '"')
                            errorFile.println(line);
                        //Checks that the third argument (if there is one) is an int
                        else if ((words[0].equals("add") || words[0].equals("remove")) && !(Integer.parseInt(words[2]) >= 0))
                            errorFile.println(line);
                        //Checks to see if the string has a comma
                        else if ((words[0].equals("add") || words[0].equals("remove")) && !(line.contains(",")))   
                            errorFile.println(line);
                        else {
                            //Stores the node if it exists in the tree (So we can use setAvailable(), etc. to manipulate values, rather than create another recursive method
                            Node temp = mainTree.search(words[1]);
                            switch(words[0]) {
                                case "add":
                                    //If the title is already listed in the tree, it updates that info. Otherwise, it adds the movie title
                                    if (temp != null) {
                                        temp.setAvailable(temp.getAvailable() + Integer.parseInt(words[2]));
                                    }
                                    else {
                                        mainTree.insert(words[1], Integer.parseInt(words[2]), 0);
                                    }
                                    break;
                                case "remove":
                                    if (temp != null) {
                                        temp.setAvailable(temp.getAvailable() - Integer.parseInt(words[2]));
                                        //Deletes the movie from the tree if it has 0 available and rented copies
                                        if (temp.getAvailable() <= 0 && temp.getRented() <= 0)
                                            mainTree.delete(temp.getTitle());
                                    }
                                    else
                                        System.out.println("The movie on line " + count + " of " + fileName + " was skipped for not exisiting in the tree. Skipped removal...");
                                    break;
                                case "rent":
                                    if (temp != null) {
                                        //Only rents a movie if there is an available movie
                                        if (temp.getAvailable() > 0) {
                                            temp.setAvailable(temp.getAvailable() - 1);
                                            temp.setRented(temp.getRented() + 1);
                                        }
                                        else
                                            System.out.println("Rental procedure on line " + count + " of " + fileName + " failed. No available copies.");
                                    }
                                    else
                                        System.out.println("The movie on line " + count + " of " + fileName + " was skipped for not exisiting in the tree. Skipped renting...");
                                    break;
                                case "return":
                                    if (temp != null) {
                                        //If no copies are rented out, but one is returned, this picks that up
                                        if (temp.getRented() > 0) {
                                            temp.setAvailable(temp.getAvailable() + 1);
                                            temp.setRented(temp.getRented() - 1);
                                        }
                                        else
                                            System.out.println("Return procedure on line " + count + " of " + fileName + " failed. No rented copies.");
                                    }
                                    else
                                        System.out.println("The movie on line " + count + " of " + fileName + " was skipped for not exisiting in the tree. Skipped returning...");
                                    break;
                            }
                        }
                        //If the third argument for add/remove is not an int, this catches it and makes it an error. Ex. add "Passenger",a
                    } catch (NumberFormatException ex){
                            errorFile.println(line);
                    }
                }
            }
        }
    }
    
    public static void printReport(BinaryTree mainTree, PrintWriter outputFile) {
        mainTree.inorder(outputFile);
    }
}
