import textio.TextIO;
import java.math.BigInteger;

public class ReadRef{

  private static class TreeNode{
    BigInteger ean;
    String number;

    public TreeNode(BigInteger val){
      ean = val;
      number = ean.toString();
    }

    TreeNode left;
    TreeNode right;
  }


  private static ReadSheet readSheet;
  private static String[][] refSheet;
  private static String[][] done;
  private static String[][] empty;
  private static String[][] dupEANS;
  private static int dupCounter = 0;
  private static int doneCounter = 0;
  private static int emptyCounter = 0;
  private static TreeNode root = null;



  public static void main(String[] args){
    System.out.println("Enter the path of the empty sheet.");
    String path = "done\\" + TextIO.getln();
    String line = "";


    readRef();
    readEAN(path);
    traverseTree(root);


    TextIO.writeFile("done\\done.txt");
    for(int n = 0; n < done.length; n++){
      for(int i = 0; i < done[n].length; i++){
        if((done[n][i] == null) || (done[n][i].trim().equalsIgnoreCase("null"))){
          done[n][i] = "";
        }
        line = line + done[n][i] + "\t";
      }
      TextIO.putln(line);
      line = "";
    }


    line = "";
    TextIO.writeFile("done\\empty.txt");
    for(int n = 0; n < empty.length; n++){
      for(int i = 0; i < empty[n].length; i++){
        if((empty[n][i] == null) || (empty[n][i].trim().equalsIgnoreCase("null"))){
          empty[n][i] = "";
        }
        line = line + empty[n][i] + "\t";
      }
      TextIO.putln(line);
      line = "";
    }


    line = "";
    TextIO.writeFile("done\\dupEANS.txt");
    for(int n = 0; n < dupEANS.length; n++){
      if((dupEANS[n][0] == null) || (dupEANS[n][0].trim().equalsIgnoreCase("null"))){
        dupEANS[n][0] = "";
      }
      line = dupEANS[n][0];
      TextIO.putln(line);
    }
  }






  public static void readRef(){
    readSheet = new ReadSheet();
    refSheet = readSheet.initializeList("done\\Reference.txt");
  }






  public static void readEAN(String path){
    readSheet = new ReadSheet();
    readSheet.makeList(path);
    done = readSheet.getSheet();
    empty = new String[done.length][1];
    dupEANS = new String[done.length][1];
    String line = "";
    String runner = "";
    char c = ' ';
    TextIO.readFile(path);

    readLoop : while(true){
      try{
        line = TextIO.getln();
      }
      catch(IllegalArgumentException e){
        break readLoop;
      }

      line = line.trim();

      checkLine : for(int n = 0; n < line.length(); n++){
        c = line.charAt(n);
        if(Character.isDigit(c) == false){
          break checkLine;
        }
        else{
          runner = runner + c;
        }
      }

      try{
        BigInteger value = new BigInteger(runner);
        insertTree(root, value);
      }
      catch(Exception e){
        e.printStackTrace();
      }

      runner = "";
    }
  }






  public static void insertTree(TreeNode node, BigInteger number){
    if(root == null){
      root = new TreeNode(number);
    }
    else{
      int val = node.ean.compareTo(number);
      if(val == 1){
        if(node.left != null){
          insertTree(node.left, number);
        }
        else{
          node.left = new TreeNode(number);
          return;
        }
      }
      else if(val == -1){
        if(node.right != null){
          insertTree(node.right, number);
        }
        else{
          node.right = new TreeNode(number);
          return;
        }
      }
      else if(val == 0){
        dupEANS[dupCounter][0] = number.toString();
        dupCounter++;
      }
    }
  }






  public static void traverseTree(TreeNode root){
    if(root != null){
      if(root.left != null){
        traverseTree(root.left);
      }


      findEAN(root.ean);


      if(root.right != null){
        traverseTree(root.right);
      }
    }
  }





  public static void findEAN(BigInteger number){
    int start = 0;
    int end = refSheet.length;
    int pos = start + ((int)((end - start)/2));
    boolean found = false;
    BigInteger refEAN;

    while(true){

      refEAN = new BigInteger(refSheet[pos][0]);

      if(number.compareTo(refEAN) == 1){
        start = pos+1;
      }
      else if(number.compareTo(refEAN) == -1){
        end = pos-1;
      }
      else if(number.compareTo(refEAN) == 0){
        done[doneCounter] = refSheet[pos];
        doneCounter++;
        return;
      }


      if(start <= end){
        pos = start + ((int)((end - start)/2));
      }
      else if(start > end){
        empty[emptyCounter][0] = number.toString();
        emptyCounter++;
        return;
      }
    }
  }

}
