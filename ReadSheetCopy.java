import textio.TextIO;

public class ReadSheet{


  private int rowCount = 0;
  private int colCount = 0;
  private String[][] codes;


  private String[][] sheet;


  private String[] varHeader = {
    "EAN", "GTIN", "EAN/GTIN", "Product Name", "Product Image", "Image",
    "Net Weight", "Net Volume", "Gross Weight", "Quantitative declaration of ingredients",
    "QUID", "(QUID)", "Nutritional declaration", "Allergen Information",
    "Additive information", "Additive", "Name of Manufacturer",
    "Manufacturer", "Address of Manufacturer", "Address", "Country of origin", "Origin", "Country"
  };




  public static void main(String[] args){
    initializeList("Automatenheld-UA3.txt");

    TextIO.writeFile("Automatenheld-ReadSheet.txt");
    String line = "";

    for(int n = 0; n < sheet.length; n++){
      for(int i = 0; i < sheet[n].length; i++){
        if(sheet[n][i] != null){
          if(line.equals("")){
            line = sheet[n][i];
          }
          else{
            line = line + "\t" + sheet[n][i];
          }
        }
        else{
          line = line + "\t";
        }
      }
      TextIO.putln(line);
      line = "";
    }


    for(int m = 0; m < codes.length; m++){
      System.out.println(codes[m][0]+" can be found in column "+codes[m][1]);
    }


  }






  private void initializeList(String path){
    TextIO.readFile(path);
    String runner = "";
    String line = "";
    char c = ' ';
    int countR = 0;
    int countC = 0;


    while((countR < rowCount) || (countR == 0)){
      line = TextIO.getln();


      if((rowCount == 0) && (colCount == 0)){
        determine(line);
        makeList(line);
        TextIO.readFile(path);
      }

      for(int n = 0; n < line.length(); n++){
        c = line.charAt(n);
        if(c != '\t'){
          runner = runner + c;
        }
        else{
          sheet[countR][countC] = runner;

          runner = "";
          countC++;
        }
      }

      countC = 0;
      countR++;
    }
  }





  private void determine(String line){
    String result = "";
    char c = ' ';
    int size = 0;
    boolean found = false;
    String runner = "";
    String corrCells = "";


    for(int n = 0; n < line.length(); n++){
      c = line.charAt(n);
      if(c == '\t'){
        if(found == true){

          if(result.equals("")){
            result = runner;
            corrCells = "" + colCount;
          }
          else{
            result = result + "\t" + runner;
            corrCells = corrCells + "\t"+colCount;
          }

          size++;

        }
        colCount++;
        runner = "";
        found = false;
      }
      else{
        runner = runner + c;
      }


      if(runner.length() >= 2){
        if(runner.equalsIgnoreCase("Product Name - Translation") || runner.equalsIgnoreCase("Product Name Translation")){
          found = false;
        }
        for(int i = 0; i < varHeader.length; i++){
          if(runner.equalsIgnoreCase(varHeader[i]) && found == false){
            found = true;
          }
        }
      }

    }
    System.out.println(result);
    colCount = 0;


    codes = new String[size][2];
    size = 0;
    runner = "";
    result = result + "\t";
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
    corrCells = corrCells + "\t";
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






  private void makeList(String line){
    int empty = 0;
    char c = ' ';
    int prevCol = 0;


    try{
      while(line != null){
        line = TextIO.getln();

        for(int m = 0; m < line.length(); m++){
          c = line.charAt(m);
          if(c == '\t'){
            colCount++;
          }
        }

        if(colCount < prevCol){
          colCount = prevCol;
        }
        else{
          prevCol = colCount;
        }

        line = line.trim();
        if(line.equals("")){
          empty++;
          if(empty == 200){
            break;
          }
        }
        colCount = 0;
        rowCount++;
      }
    }
    catch(IllegalArgumentException e){
      e.printStackTrace();
    }


    System.out.println("There are "+(colCount+1)+" columns in this sheet.\n"+
    "And there are "+(rowCount+1)+"rows in the sheet.\n");

    sheet = new String[rowCount+1][colCount+1];
  }









  public String[][] getCodes(){
    return codes;
  }

  public String[][] getSheet(){
    return sheet;
  }

}
