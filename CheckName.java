import textio.TextIO;




/*
*   Use checkProductName(String name) to correct the name of a given product.
*   This method also splits the name into different variables. These are :
*     productName
*     productWeight
*     amount
*     depositType
*
*   And there also exit a variable that contains the end result accoriding to this class,
*   this variable is called result.
*   All of these variables also have their own getter methods.
*/
public class CheckName{



  private String[][] deposit = {{"Einweg", "(EINWEG)"}, {"Mehrweg", "(MEHRWEG)"},
  {"(Einweg)", "(EINWEG)"}, {"(Mehrweg)", "(MEHRWEG)"}, {"Merhweg", "(MEHRWEG)"},
  {"Mehweg", "(MEHRWEG)"}, {"Enweg", "(EINWEG)"}, {"Eniweg", "(EINWEG)"}, {"Mherweg", "(MEHRWEG)"},
  {"Eiwneg", "(EINWEG)"}
  };

  private String[][] weightUnits = {
    {"kg", "1000g"}, {"g", "g"}, {"G", "g"}, {"Gramm", "g"}, {"Kilogramm", "1000g"}, {"cl", "0.01l"},
    {"ml", "0.001l"}, {"L", "l"}, {"Liter", "l"}, {"dl", "0.1l"}, {"gr", "g"}, {"gram", "g"}
  };



  private String productName = "";
  private String productWeight = "";
  private String depositType = "";
  private String result = "";
  private String amount = "";
  private String unit = "";





  public String checkProductName(String name){


    productName = "";
    productWeight = "";
    depositType = "";
    result = "";
    amount = "";
    unit = "";

    char c = ' ';
    String runner = "";
    String temp = "";
    int pos = 0;




    /*
    *   This cleanUpLoop, is responsible for getting rid of any unnecessary ',' (commas) characters.
    *   It will also do so for any dots, given that some special conditions are met.
    */
    cleanUpLoop : for(int n = 0; n < name.length(); n++){
      c = name.charAt(n);
      //  Unless the current character is either a '.' or ',' take that character and add it to
      //  a temporary variable.
      if((c != ',') && (c != '.')){
        runner = runner + c;
      }
      else{
        if(n < name.length()-1){
          if(Character.isDigit(name.charAt(n+1))){  //  A number occures after the unwanted character.
            if(Character.isDigit(name.charAt(n-1))){  // This one ensures that numbers are seperated
              runner = runner + ".";                  // from one another properly.
            }
            else if(Character.isLetter(name.charAt(n-1))){ // possible the end of the name is marked
              runner = runner + " ";                       // by either a dot or a comma.
            }
            else if(name.charAt(n-1) == ' '){ // possibly a forgotten zero before the dot.
              runner = runner + "0.";
            }
          }
          else{
            if(name.charAt(n+1) != ' '){ // Simply get rid of the character and if needed add space.
              runner = runner + " ";
            }
          }
        }
      }
    }

    name = runner;
    runner = "";






    /*
    *   This one is responsible for recognizing what part of what has beed written is the name
    *   of the product. It cannot reliably do this on its own, in some cases it will
    *   not take some numbers that are part of the product name, but that is handled in the
    *   loop responsible for recognizing the wight and the number of pieces.
    *   The idea is that unless a number is encountered, take whatever character this string
    *   contains. Once a number is encountered then perhaps the number is part of the name.
    *   Once a number is taken from the string, we check if the next set of valid characters
    *   could possibly be units of weight. If so then we are done with the name.
    */
    nameLoop : for(int n = 0; n < name.length(); n++){
      c = name.charAt(n);
      if((Character.isDigit(c) == false) && (c != '.')){  // Do not take numbers
        runner = runner + c;
      }
      else{
        if(n > 0){
          if(Character.isLetter(name.charAt(n-1))){ //  Number might be attached to a letter

            checkLoop : for(int i = n+1; i < name.length(); i++){ // See if after number, a unit
              c = name.charAt(i);                                 // of wieght follows.

              if((Character.isDigit(c) == false) && (((c != 'x') && (c != 'X')) && ((c != '.') && (c != ' ')))){
                temp = temp + c;  // Take the next set of characters, with the above restriction.
              }
              else if(temp != ""){
                break checkLoop;  // break only if a valid set of characters have been taken.
              }
            }

            for(int k = 0; k < weightUnits.length; k++){  // Compare temp to all recognized units.
              temp = temp.trim();
              if(temp.equalsIgnoreCase(weightUnits[k][0])){
                temp = "";        //  If temp is equal to a unit then remember the position and
                pos = n;          //  break the loop. Thus The product name is recognized.
                break nameLoop;
              }
            }

            runner = runner + c;

          }
          else{
            checkLoop : for(int i = n+1; i < name.length(); i++){ // See if after number a unit
              c = name.charAt(i);                                 // of wieght follows.

              if((Character.isDigit(c) == false) && ((c != 'x') && (c != 'X') && (c != '.') && (c != ' ') && (c != '(') && (c != ')'))){
                temp = temp + c;  // Take the next set of characters, with the above restriction.
              }
              else{
                if(temp.trim().equals("") == false){
                  break checkLoop;  // break only if a valid set of characters have been taken.
                }
              }
            }

            for(int k = 0; k < weightUnits.length; k++){  // Compare temp to all recognized units.
              temp = temp.trim();
              if(temp.equalsIgnoreCase(weightUnits[k][0])){
                temp = "";        //  If temp is equal to a unit then remember the position and
                pos = n;          //  break the loop. Thus The product name is recognized.
                break nameLoop;
              }
            }

            temp = "";
            runner = runner + name.charAt(n);   // If we did not recognize a weight unit then
          }                                     // this number must be part of the name.
        }
        else{
          runner = runner + c;
        }
      }
    }

    productName = runner;
    runner = "";




    boolean dotted = false;


    /*
    *   We pick up where the nameLoop broke off. This WeightLoop will primarily read numbers.
    *   If any other characters are encountered then there are certain tasks that it will
    *   perform. If a space character is recognized, then we need to see what the character that
    *   comes after this one is. If it is an 'x' then We know that the number we have takes
    *   thus far is the number of pieces. But if another number follows after the space then
    *   We recognize this number as being part of the name.
    *   Once it does recognize the product weight the unit is also taken and, if need be rewritten.
    *   After the unit it will also try and take any remaining characters. If the product has a
    *   deposit type then this too will be recognized.
    */
    if(pos != 0){
      weightLoop : for(int n = pos; n < name.length(); n++){
        c = name.charAt(n);
        if((Character.isDigit(c)) || (c == '.')){
          if((c == '.') && dotted != true){
            dotted = true;
            runner = runner + c;    // Take any valid number.
          }
          else if((c == '.') && (dotted == true)){
            runner = "" + c;
          }
          else{
            runner = runner + c;    // Take any valid number.
          }
        }
        else if(runner.equals("") == false){

          if(c == ' '){
            if((name.charAt(n+1) == 'x') || (name.charAt(n+1) == 'X')){
              temp = runner;  // We have recognized the number of pices.
              runner = "";
              n++;
            }
            else if(Character.isDigit(name.charAt(n+1))){
              productName = productName + runner + " ";   // The number is part of the name.
              runner = "";
            }
          }
          else if((c == 'x') || (c == 'X')){
            temp = runner;  // Again the number of pieces has been recognized.
            runner = "";
          }
          else if(Character.isLetter(c) && ((c != 'x') && (c != 'X'))){
            /*
            * This loop finds the appropriate unit in which the weight of the product
            * is supposed to be displayes in. But this should only be used after the
            * numerical value for the weight has been identified.
            */
            unitLoop : for(int i = n; i < name.length(); i++){
              c = name.charAt(i);                             // Take the unit.
              unit = unit + c;                                // Check the units validity.
              for(int m = 0; m < weightUnits.length; m++){
                if(unit.equalsIgnoreCase(weightUnits[m][0])){ // If valid then write it in the
                  double n1 = Double.parseDouble(runner);     // correct form, which also means
                  if(weightUnits[m][1].length() > 1){         // that the weight must also change.
                    unit = weightUnits[m][1].substring((weightUnits[m][1].length()-1), (weightUnits[m][1].length()));

                    switch (weightUnits[m][1].substring(0, weightUnits[m][1].length()-1)){
                      case "1000" :
                        n1 = n1 * 1000;
                        break;
                      case "0.1" :
                        n1 = n1 * 1/10;
                        break;
                      case "0.01" :
                        n1 = n1 * 1/100;
                        break;
                      case "0.001" :
                        n1 = n1 * 1/1000;
                        break;
                    }
                  }
                  else{   // But if the unit is correct, then nothing needs changing.
                    unit = weightUnits[m][1];
                  }

                  pos = m;

                  if(temp != ""){   // temp contains the number of pieces.
                    amount = temp;
                  }
                  productWeight = "" + n1;


                  runner = "";
                  pos = i+1;    //  Again remember the position.
                  break weightLoop;
                }
              }
            }

          }

        }
      }
    }





    /*
    *   If the name contains a deposit type specification then it should be after the weight,
    *   hence if there does exit any other letters, read them and check and see if it matches
    *   the items in the list.
    */
    String back = "";
    depositLoop : for(int k = name.length()-1; k >= 0; k--){
      c = name.charAt(k);
      if(Character.isLetter(c)){
        back = ""+c;
        back = back + runner;
        runner = back;
      }

      if(runner.length() > 3){
        for(int a = 0; a < deposit.length; a++){
          if(runner.equalsIgnoreCase(deposit[a][0])){
            depositType = deposit[a][1];
            break depositLoop;
          }
        }
      }
    }
    runner = "";
    back = "";


    if(productWeight.length() > 3){
      for(int n = productWeight.length()-1; n >= 0; n--){
        c = productWeight.charAt(n);
        back = c + runner;
        runner = back;
        if(runner.trim().equalsIgnoreCase(".0")){
          productWeight = productWeight.substring(0, n);
        }
      }
    }


    //  Finally, enter everything into the result variable and return it.
    if(amount.trim().equals("") == false){
      result = productName + amount + " x " + productWeight + unit + " " + depositType;
    }
    else{
      result = productName + productWeight + unit + " " + depositType;
    }

    return result;
  }







  public String getProductName(){
    return productName;
  }






  public String getProductWeight(){
    return productWeight;
  }






  public double getProductWeightInDouble(){
    double weight = 0;
    if(productWeight.equals("") == false){
      try{
        weight = Double.parseDouble(productWeight);
        double a;
        if(amount.equals("") == false){
          a = Double.parseDouble(amount);
          weight = weight * a;
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
    else{
      weight = 0;
    }
    return weight;
  }






  public String getDepositType(){
    return depositType;
  }






  public String checkDepositType(String line){
    String cor = "";
    for(int n = 0; n < deposit.length; n++){
      if(line.equalsIgnoreCase(deposit[n][0])){
        cor = deposit[n][1];
        break;
      }
    }
    return cor;
  }






  public String getAmount(){
    return amount;
  }







  public String getResult(){
    return result;
  }







  public String getUnit(){
    return unit;
  }

}
