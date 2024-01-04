import textio.TextIO;



/*
*   Use the method initializeList(String path), which initializes the sheet[][] variable.
*   Use the getSheet() method to get the initialized sheet[][].
*   The codes[][] array stores two kinds of information, one written in codes[n][0],
*   is the title for columns we might be interested in.
*   Second is the position of the said columns, stored in codes[n][1].
*   Use getCodes() to get the codes[][] array.
*/

public class ReadSheet{


  private int rowCount = 0;
  private int colCount = 0;
  private String[][] codes;


  private String[][] sheet;


  private String[] varHeader = {
    "EAN", "GTIN", "EAN/GTIN", "Product Name", "Product Image", "Image", "Deposit Type",
    "Amount", "Net Weight", "Net Volume", "Gross Weight", "Quantitative declaration of ingredients",
    "QUID", "(QUID)", "Nutritional declaration", "Allergen Information",
    "Additive information", "Additive", "ABV", "Name of Manufacturer",
    "Manufacturer", "Address of Manufacturer", "Address", "Country of origin", "Origin", "Country",
    "Deposit Amount"
  };






  public String[][] initializeList(String path){
    determine(path);
    makeList(path);
    TextIO.readFile(path);
    String runner = "";
    String line = "";
    char c = ' ';
    int countC = 0;

    try{
      //  There are two loops, one for taking lines and the other for processing the taken line,
      //  seperating it into columns.
      for(int i = 0; i < rowCount; i++){
        line = TextIO.getln();

        for(int n = 0; n < line.length(); n++){
          c = line.charAt(n);

          // A tab character signifies that another cell in the next column exists. So in the
          // absence of this character we are either dealing with end of the line, or simple
          // contents of a cell.
          if((c != '\t') && (c != '\n') && (c != '\r')){
            runner = runner + c;
          }
          else if((c == '\n') || (c == '\r')){
            if(sheet[i][countC] != null){
              sheet[i][countC] = sheet[i][countC] + runner;
            }
            else{
              sheet[i][countC] = runner;
            }
          }
          else{
            if(countC < colCount-1){
              if(n < line.length()-1){
                sheet[i][countC] = runner;
                runner = "";
                countC++;
              }
            }
          }
        }

        countC = 0;
        runner = "";

      }
    }
    catch(Exception e){
      System.out.println("End of the file");
      e.printStackTrace();
    }

    return sheet;
  }








  public String[] readLine(String line, int number){
    String[] newLine = new String[number];
    String runner = "";
    char c = ' ';
    int count = 0;

    for(int n = 0; n < line.length(); n++){
      c = line.charAt(n);
      if((c != '\t') && (c != '\n') && (c != '\r')){
        runner = runner + c;
      }
      else if((c == '\n') || (c == '\r')){
        newLine[count] = runner;
      }
      else if((c == '\t') && (count < number)){
        newLine[count] = runner;
        runner = "";
        count++;
      }
    }

    return newLine;
  }








  public void determine(String path){
    TextIO.readFile(path);
    String line = "";

    try{
      line = TextIO.getln();
    }
    catch(Exception e){
      System.out.println("");
    }

    String result = "";
    char c = ' ';
    int size = 1;
    boolean found = false;
    String runner = "";
    String corrCells = "";

    //  This loop is responsible for finding the header for each column.
    //  It will take all characters until a tab character is reached.
    //  But each time it takes a character, after runner has length greater than 1,
    //  it checks for the runners validity agains the elements within the header array.
    for(int n = 0; n < line.length(); n++){
      c = line.charAt(n);

      //  After having read all prior characters and having stored them, we need to
      //  check for the validity of the columns header, this will have been already done at this
      //  point, and a signal will have been created.
      //  The signal will ensure that if we are not interested in the column header, then we
      //  need not store its information.
      if(c == '\t'){
        if(found == true){
          if(result.equals("")){
            result = runner + "\t";
            corrCells = "" + colCount + "\t";
          }
          else{
            result = result + runner + "\t";  // We save the column titles here.
            corrCells = corrCells + colCount + "\t";  // And save the position here.
          }

          // The size of the array that stores the column titles and their positions
          // should grow each time we find the columns we are interested in.
        }
        else{
          result = result + "\t";
          corrCells = corrCells + "\t";
        }


        size++;
        colCount++;
        runner = "";
        found = false;
      }
      else{
        runner = runner + c;
      }


      //  Some columns have headers that can cause confusion for the loop. These should
      //  be recognized and a signal should be given that this column is not one we are
      //  interested in an thus should not be stored.
      if(runner.length() >= 2){
        if(runner.equalsIgnoreCase("Product Name - Translation") || runner.equalsIgnoreCase("Product Name Translation")){
          found = false;
        }
        else{
          for(int i = 0; i < varHeader.length; i++){
            if(runner.length() >= varHeader[i].length()){
              if((runner.substring((runner.length() - varHeader[i].length()), runner.length()).equalsIgnoreCase(varHeader[i])) && (found == false)){
                found = true;
              }
            }
          }
        }
      }
    }




    colCount = 0;


    codes = new String[size][2];  //  create the codes array
    size = 0;
    runner = "";

    //  After creating the array, we start by reading the column titles.
    for(int m = 0; m < result.length(); m++){
      c = result.charAt(m);
      if(c != '\t'){
        runner = runner + c;
      }
      else{
        codes[size][0] = runner;
        size++;
        runner = "";
      }
    }


    size = 0;
    runner = "";

    //  And here we store the position of the columns in the codes array.
    for(int k = 0; k < corrCells.length(); k++){
      c = corrCells.charAt(k);
      if(c != '\t'){
        runner = runner + c;
      }
      else{
        codes[size][1] = runner;
        size++;
        runner = "";
      }
    }
  }








  /*
  *   This method will go thorugh every line in the txt document and count the number of columns
  *   and the number of rows.
  */
  public void makeList(String path) throws IllegalArgumentException{
    TextIO.readFile(path);
    int empty = 0;
    char c = ' ';
    int prevCol = 0;
    String line = "";


    thisLoop : while(line != null){
      try{
        line = TextIO.getln();
      }
      catch(Exception e){
        System.out.println("The end of this file has been reached");
        break thisLoop;
      }

      // For this line, count all columns
      for(int m = 0; m < line.length(); m++){
        c = line.charAt(m);
        if(c == '\t'){
          colCount++;
        }
      }

      //  We might encounter a larger number of columns as we process the document.
      //  We must ensure that the sheet[][] dimensions we are going to create are based
      //  on the largest of the numbers we find here.
      //  The following does exactly that.
      if(colCount < prevCol){
        colCount = prevCol;
      }
      else{
        prevCol = colCount;
      }




      //  Now we want to simply read each line in the document and save the number of lines in
      //  a variable, this will be the number of rows.
      //  It will also terminate the counting process if it finds over 200 empty lines in a row.
      line = line.trim();
      if(line.equals("")){
        empty++;
        if(empty == 200){
          break;
        }
      }
      else{
        empty = 0;
      }

      colCount = 0;
      rowCount++;
    }

    colCount = prevCol;
    System.out.println("There exit "+rowCount+" rows, and the largest number of "+
    "columns detected is "+colCount );


    sheet = new String[rowCount+1][colCount+1];
  }








  public int getColCount(){
    return colCount;
  }


  public String[][] getCodes(){
    return codes;
  }

  public String[][] getSheet(){
    return sheet;
  }

}
