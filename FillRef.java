import textio.TextIO;
import java.math.BigInteger;



public class FillRef{



  private static class TreeNode{
    String[] line;
    BigInteger ean;

    public TreeNode(String[] par){
      try{
        line = par;
        ean = new BigInteger(line[0]);
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }

    TreeNode left;
    TreeNode right;
  }


  private static ReadSheet read = new ReadSheet();
  private static String[][] doneSheet = new String[0][0];
  private static String[][] refSheet = new String[0][0];
  private static String[][] duplicates;
  private static BigInteger refNum;
  private static BigInteger doneNum;
  private static int doneCount = 0;
  private static int dupCounter = 0;
  private static int prevRefLength = 0;
  private static TreeNode root = null;









  public static void main(String[] args){
    String path = TextIO.getln();
    if(path.trim().equals("")){
      path = "Special_5\\special_5.txt";
    }
    else{
      path = "done\\R_Library\\done_" + path;
    }


    readDone(path);
    treeFill(root);
    refSheet = readRef();
    refSheet = insertRef();
    String line = "";
    int refCounter = 0;

    TextIO.writeFile("done\\Reference.txt");
    for(int n = 0; n < refSheet.length; n++){
      if(refSheet[n] != null){
        if(refSheet[n][0] != null){
          for(int i = 0; i < refSheet[n].length; i++){
            line = line + refSheet[n][i] + "\t";
          }
          TextIO.putln(line);
          refCounter++;
          line = "";
        }
      }
    }

    line = "";
    dupCounter = 0;
    TextIO.writeFile("done\\Duplicates.txt");
    for(int n = 0; n < duplicates.length; n++){
      if((duplicates[n] != null) && (duplicates[n].length > 1)){
        for(int i = 0; i < duplicates[n].length; i++){
          line = line + duplicates[n][i] + "\t";
        }

        if(line != null){
          TextIO.putln(line);
          dupCounter++;
        }

        line = "";
      }
    }

    System.out.println("Done sheet length : " + doneSheet.length +
    "\nDuplicates sheet length : " + dupCounter +
    "\nShould be the difference : " + (doneSheet.length - dupCounter) +
    "\nAdded to reference sheet : " + (refCounter - prevRefLength) +
    "\n" + refCounter);
  }









  /*
  *   This routine will read the sheet (doneSheet) that is to be inserted into the reference sheet
  *   (refSheet).
  *   doneSheet is read line by line, each line that is taken will be split into its proper columns,
  *   Then we call for a function that takes that line and inserts it into a binary tree list.
  *   The choice for binary tree should be obvious, doneSheet is not a permanent sheet we have to
  *   work with and it will only be read and inserted, so wether the tree is balaced is irrelevant.
  *   Having made this disadvatage of using a binary tree list irrelevant, what remains are
  *   its advantages. Sorting is made very easy, the worst scenatio is the tree becoming a linked list.
  *   But this does not matter because we only want to insert these items into the reference sheet
  *   and have reference sheet remain ordered.
  */
  public static void readDone(String donePath){
    read.determine(donePath);  // codes[] array is initialized.
    read.makeList(donePath);    // an empty sheet is made, which we shall make use of later.

    doneSheet = read.getSheet();  // The empty sheet is now initialized in this class as well.
    duplicates = new String[doneSheet.length][1];  // Here we will also create a sheet for duplicates.

    int columnCount = read.getColCount(); // get the number of columns.

    TextIO.readFile(donePath);  // Start reading from this source
    String line = TextIO.getln(); // Read the first line.
    String runner = "";
    String[] line2;

    try{
      for(int n = 1; n < doneSheet.length; n++){
        line = TextIO.getln();
        line2 = read.readLine(line, columnCount);

        for(int k = 0; k < line2[0].length(); k++){
          if(Character.isDigit(line2[0].charAt(k))){
            runner = runner + line2[0].charAt(k);
          }
        }

        if(runner.trim().equals("") == false){
          line2[0] = runner;
          runner = "";
          insertTree(root, line2);
        }
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }








  public static void insertTree(TreeNode node, String[] line){
    if(root == null){
      root = new TreeNode(line);
      System.out.println(root.ean.toString());
    }
    else{
      try{
        int val = node.ean.compareTo(new BigInteger(line[0]));
        if(val == 1){
          if(node.left != null){
            insertTree(node.left, line);
          }
          else{
            node.left = new TreeNode(line);
            return;
          }
        }
        else if(val == -1){
          if(node.right != null){
            insertTree(node.right, line);
          }
          else{
            node.right = new TreeNode(line);
            return;
          }
        }
        else if(val == 0){
          duplicates[dupCounter] = line;
          dupCounter++;
        }
      }
      catch(Exception e){
        e.printStackTrace();
        System.out.println("This line could not be read.");
      }
    }

  }









  public static void treeFill(TreeNode root){
    if(root.left != null){
      treeFill(root.left);
    }


    doneSheet[doneCount] = root.line;
    doneCount++;


    if(root.right != null){
      treeFill(root.right);
    }
  }








  public static String[][] readRef(){
    try{
      read = new ReadSheet();
      refSheet = read.initializeList("done\\Reference.txt");
    }
    catch (Exception e){
      System.out.println("The reference sheet could not be read.");
    }
    System.out.println("Reference sheet prior to insertion length is : " + refSheet.length);
    prevRefLength = refSheet.length;
    return refSheet;
  }







  public static String[][] insertRef(){
    if(doneSheet.length == 0){
      return refSheet;
    }
    else if(refSheet.length == 0){
      return doneSheet;
    }
    else{
      String[][] result = new String[doneSheet.length + refSheet.length][];
      int dpos = 0;
      int rpos = 0;
      boolean dposChange = true;
      boolean rposChange = true;
      int resCounter = 0;



      while((dpos < doneSheet.length) || (rpos < refSheet.length)){

        if(rposChange == true){
          try{
            refNum = new BigInteger(refSheet[rpos][0]);
            rposChange = false;
          }
          catch(Exception e){
            refNum = null;
          }
        }


        if(dposChange == true){
          try{
            doneNum = new BigInteger(doneSheet[dpos][0]);
            dposChange = false;
          }
          catch(Exception e){
            doneNum = null;
          }
        }


        if(((refNum != null) || (rpos == refSheet.length)) && ((doneNum != null) || (dpos == doneSheet.length))){
          if((dpos < doneSheet.length) && (rpos < refSheet.length)){
            if(refNum.compareTo(doneNum) == 1){
              result[resCounter] = doneSheet[dpos];
              resCounter++;
              dpos++;
              dposChange = true;
            }
            else if(refNum.compareTo(doneNum) == -1){
              result[resCounter] = refSheet[rpos];
              resCounter++;
              rpos++;
              rposChange = true;
            }
            else if(refNum.compareTo(doneNum) == 0){
              duplicates[dupCounter] = refSheet[rpos];
              result[resCounter] = doneSheet[dpos];
              resCounter++;
              dupCounter++;
              rpos++;
              dpos++;
              rposChange = true;
              dposChange = true;
            }
          }
          else if((dpos == doneSheet.length) && (rpos < refSheet.length)){
            result[resCounter] = refSheet[rpos];
            resCounter++;
            rpos++;
            rposChange = true;
          }
          else if((rpos == refSheet.length) && (dpos < doneSheet.length)){
            result[resCounter] = doneSheet[dpos];
            resCounter++;
            dpos++;
            dposChange = true;
          }
        }
        else{
          if(((refNum == null) && (rpos < refSheet.length))){
            rpos++;
            rposChange = true;
          }
          else if((doneNum == null) && (dpos < doneSheet.length)){
            dpos++;
            dposChange = true;
          }
        }
      }

      return result;
    }
  }
}
