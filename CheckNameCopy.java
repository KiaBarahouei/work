import textio.TextIO;

public class CheckName{



  private static String[] deposit = {"Einweg", "Mehrweg", "(Einweg)", "(Mehrweg)"};
  private static String[][] weightUnits = {
    {"kg", "1000g"}, {"g", "g"}, {"G", "g"}, {"Gramm", "g"}, {"Kilogramm", "1000g"}, {"cl", "0.01l"},
    {"ml", "0.001l"}, {"L", "l"}, {"Liter", "l"}, {"dl", "0.1l"}
  };



  private static String productName = "";
  private static String productWeight = "";
  private static String depositType = "";
  private static String result = "";
  private static String amount = "";





  public static void main(String[] args){
    thisLoop : while(true){
      String name = TextIO.getln();
      if(name.equalsIgnoreCase("exit")){
        break thisLoop;
      }
      name = checkProductName(name);
      TextIO.putln(name);
    }
  }




  private static String checkProductName(String name){

    char c = ' ';
    String runner = "";
    String temp = "";
    String unit = "";
    String result = "";
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
            runner = runner + c;

          }
          else{
            checkLoop : for(int i = n+1; i < name.length(); i++){ // See if after number, a unit
              c = name.charAt(i);                                 // of wieght follows.

              if((Character.isDigit(c) == false) && ((c != 'x') && (c != 'X') && (c != '.') && (c != ' '))){
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

            temp = "";
            runner = runner + name.charAt(n);   // If we did not recognize a weight unit then
          }                                     // this number must be part of the name.
        }
      }
    }

    productName = runner;
    runner = "";






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
        if(((Character.isDigit(c)) || (c == '.')) && ((c != ' ') && (c != 'x') && (c != 'X'))){
          runner = runner + c;    // Take any valid number.
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
                    double n2 = Double.parseDouble(weightUnits[m][1].substring(0, (weightUnits[m][1].length()-1)));
                    unit = weightUnits[m][1].substring((weightUnits[m][1].length()-1), (weightUnits[m][1].length()));
                    n1 = n1 * n2;
                    runner = "" + n1 + "" + unit;
                  }
                  else{   // But if the unit is correct, then nothing needs changing.
                    runner = n1 + weightUnits[m][1];
                  }
                  pos = m;
                  if(temp != ""){   // temp contains the number of pieces.
                    productWeight = temp + " x " + runner;
                  }
                  else{
                    productWeight = runner;
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


    amount = temp;





    /*
    *   If the name contains a deposit type specification then it should after the weight,
    *   hence if there does exit any other letters, read them and check and see if it matches
    *   the items in the list.
    */
    if(pos < name.length()){
      depositLoop : for(int k = pos; k < name.length(); k++){
        c = name.charAt(k);
        if(Character.isLetter(c)){
          runner = runner + c;
        }
        else if((Character.isLetter(c) == false) || (k == name.length()-1)){
          for(int a = 0; a < deposit.length; a++){
            if(runner.equalsIgnoreCase(deposit[a])){
              if(a < 2){
                depositType = deposit[a+2];
              }
              else{
                depositType = deposit[a];
              }
            }
          }
        }
      }
      runner = "";
    }


    //  Finally, enter everything into the result variable and return it.
    result = productName + productWeight + unit + " " + depositType;

    return result;
  }




  public static String getProductName(){
    return productName;
  }

  public static String getProductWeight(){
    return productWeight;
  }

  public static String getDepositType(){
    return depositType;
  }

  public static String getAmount(){
    return amount;
  }



}
