import textio.TextIO;

public class NewCheckSheet{

  private static String[][] sheet = new String[1][1];
  private static String[] review;
  private static String[] done;
  private static String[] rework;
  private static String[][] codes = new String[1][1];
  private static String jcal = "";
  private static int ingPos;
  private static ReadSheet readSheet = new ReadSheet();
  private static CheckName checkName = new CheckName();
  private static CheckAllergies checkAllergies = new CheckAllergies();
  private static CheckAdditives checkAdditives = new CheckAdditives();
  private static String[] colTitles = {
    "Product Name", "QUID", "Allerg",
    "Additiv", "Deposit Type", "Country", "Net weight", "Net volume",
    "gross", "Nutrition"
  };

  private static String[][] nlist = {
    {"Energie", "Energie"}, {"Brennwert", "Energie"}, {"nergi", "Energie"}, {"Fett", "Fett"},
    {"gesätt" , "-davon gesättigte Fettsäuren"}, {"Fettsäuren", "- davon gesättigte Fettsäuren"},
    {"Kohlenhydrat", "Kohlenhydrate"},
    {"Zucker", "- davon Zucker"}, {"Ballast", "Ballaststoffe"}, {"Faser", "Ballaststoffe"},
    {"Eiweiß", "Eiweiß"}, {"Protein", "Eiweiß"}, {"Salz", "Salz"}
  };


  public static void main(String[] args){
    TextIO.putln("Enter the path of the text file : ");
    String pathMod = TextIO.getln();
    String path = "Sheets\\" + pathMod;
    String deposit = "";
    double weight = 0;
    String unit = "";
    String line = "";
    sheet = readSheet.initializeList(path);
    review = new String[sheet.length];
    done = new String[sheet.length];
    rework = new String[sheet.length];
    int reviewCounter = 0;
    int doneCounter = 0;
    int catPos = 0;
    int noPics = 0;
    int filledLines = 0;
    int ABVpos = 0;
    codes = readSheet.getCodes();


    for(int n = 0; n < sheet[0].length; n++){
      if(doesExist("Category", sheet[0][n])){
        catPos = n;
        System.out.println("" + catPos);
        break;
      }
    }


    for(int k = 0; k < codes.length; k++){
      if(doesExist("ABV", codes[k][0])){
        if(sheet[0][k] != null){
          ABVpos = k;
        }
      }
    }


    for(int n = 0; n < sheet.length; n++){
      line = "";
      review[reviewCounter] = "";

      for(int i = 0; i < sheet[n].length; i++){
        if(sheet[n][i] == null){
          sheet[n][i] = "";
        }

        if((sheet[n][i].startsWith("\"")) || (sheet[n][i].startsWith("\'"))){
          sheet[n][i] = sheet[n][i].substring(1, sheet[n][i].length());
        }

        if((i < codes.length) && (codes[i][0] != null)){
          if(doesExist(colTitles[0], codes[i][0])){       // Product Name
            sheet[n][i] = checkName.checkProductName(sheet[n][i]);
            weight = checkName.getProductWeightInDouble();
            unit = checkName.getUnit();
            if((sheet[n][i].equals("") == true) && (checkName.getProductName().equals("") == false)){
              review[reviewCounter] = review[reviewCounter] + "Missing product name\t";
            }

            if(checkName.getProductName().equals("") == false){
              if(sheet[n][catPos].trim().equals("")){
                review[reviewCounter] = review[reviewCounter] + "Missing Category\t";
              }

              filledLines++;
            }
          }
          else if(doesExist("Product Image URL", codes[i][0])){ // Product Image
            if(checkName.getResult().trim().equals("") == false){
              if(sheet[n][i].trim().equals("")){
                noPics++;
              }
            }
          }
          else if(doesExist("Deposit Amount", codes[i][0])){  // Deposit Amount

            if((sheet[n][i].trim().equals("") == false) && (checkName.getDepositType().trim().equals("") == false)){
              double am;

              for(int k = 0; k < sheet[n][i].length(); k++){
                if((sheet[n][i].charAt(k) == ',') && (k < sheet[n][i].length())){
                  sheet[n][i] = sheet[n][i].substring(0, k) + "." + sheet[n][i].substring(k+1, (sheet[n][i].length()));
                }
              }

              try{
                if(checkName.getAmount().trim().equals("") == false){
                  am = (Double.parseDouble(sheet[n][i].trim()) / Double.parseDouble(checkName.getAmount()));
                }
                else{
                  am = Double.parseDouble(sheet[n][i].trim());
                }
              }
              catch(Exception errDepositAmount){
                am = -1;
              }

              boolean discrepency = true;

              if(doesExist("25", (""+am))){
                if(doesExist("Einweg", checkName.getDepositType())){
                  discrepency = false;
                }
              }
              else if(doesExist("08", (""+am))){
                if(doesExist("Mehrweg", checkName.getDepositType())){
                  discrepency = false;
                }
              }
              else if(doesExist("15", (""+am))){
                if(doesExist("Mehrweg", checkName.getDepositType())){
                  discrepency = false;
                }
              }


              if(discrepency == true){
                review[reviewCounter] = review[reviewCounter] + "Discrepency found: Deposit Type and Deposit Amount.\t";
              }
              else{
                if(checkName.getAmount().trim().equals("") == false){
                  sheet[n][i] = "" + (Double.parseDouble(checkName.getAmount()) * am);
                }
                else{
                  sheet[n][i] = "" + am;
                }
              }

            }
            else if((sheet[n][i].trim().equals("") == false) ^ (checkName.getDepositType().trim().equals("") == false)){
              review[reviewCounter] = review[reviewCounter] + "Discrepency found: Deposit Type and Deposit Amount.\t";
            }
          }
          else if(doesExist(colTitles[6], codes[i][0])){  // Net Weight
            try{
              if(checkName.getProductName().equals("") == false){ // Product Name does exit.
                sheet[n][i] = sheet[n][i].trim();
                if(sheet[n][i].equals("")){ // Weight cell is empty.
                  if(checkName.getUnit().equals("g")){  // Weight in gramms must be given in this cell.
                    review[reviewCounter] = review[reviewCounter] + "Net weight cell is empty\t";
                  }
                }
                else if(sheet[n][i].equals("") == false){ // Weight cell is not empty
                  if(checkName.getUnit().equals("l")){ // Weight in liters must not be given in this cell.
                    review[reviewCounter] = review[reviewCounter] + "For the net weight cell the unit must be \"g\"\t";
                  }
                  else if(checkName.getUnit().equals("")){
                    review[reviewCounter] = review[reviewCounter] + "There exists no unit of weight in product name\t";
                  }
                  else{
                    String temp = "";
                    for(int k = 0; k < sheet[n][i].length(); k++){
                      if((Character.isDigit(sheet[n][i].charAt(k))) || (sheet[n][i].charAt(k) == ',') || (sheet[n][i].charAt(k) == '.')){
                        temp = temp + sheet[n][i].charAt(k);
                      }
                    }
                    if((double) weight != Double.parseDouble(temp)){
                      review[reviewCounter] = review[reviewCounter] + "Weight given in net weight cell does not match the weight given in product name\t";
                    }
                  }
                }
              }
              else{
                if(sheet[n][i].equals("") == false){
                  review[reviewCounter] = review[reviewCounter] + "Product name does not exist, see if it was deleted by accident\t";
                }
              }
            }
            catch(Exception e){
              review[reviewCounter] = "A non numeric value was detected in Net weight cell.\t";
            }
          }
          else if(doesExist(colTitles[7], codes[i][0])){  // Net Volume
            try{
              if(checkName.getProductName().equals("") == false){ // Product Name does exit.
                sheet[n][i] = sheet[n][i].trim();
                if(sheet[n][i].equals("")){ // Weight cell is empty.
                  if(checkName.getUnit().equals("l")){  // Weight in liters must be given in this cell.
                    review[reviewCounter] = review[reviewCounter] + "Net volume cell is empty\t";
                  }
                }
                else if(sheet[n][i].equals("") == false){ // Weight cell is not empty
                  if(checkName.getUnit().equals("g")){ // Weight in gramms must not be given in this cell.
                    review[reviewCounter] = review[reviewCounter] + "For net Volume cell the unit must be \"l\"\t";
                  }
                  else if(checkName.getUnit().equals("")){
                    review[reviewCounter] = review[reviewCounter] + "There exists no unit of weight in product name\t";
                  }
                  else{
                    String temp = "";
                    for(int k = 0; k < sheet[n][i].length(); k++){
                      if((Character.isDigit(sheet[n][i].charAt(k))) || (sheet[n][i].charAt(k) == ',') || (sheet[n][i].charAt(k) == '.')){
                        temp = temp + sheet[n][i].charAt(k);
                      }
                    }
                    if((double) weight != Double.parseDouble(temp)){
                      review[reviewCounter] = review[reviewCounter] + "Weight given in net volume cell does not match the weight given in product name\t";
                    }
                  }
                }
              }
              else{
                if(sheet[n][i].equals("") == false){
                  review[reviewCounter] = review[reviewCounter] + "Product name does not exist, see if it was deleted by accident\t";
                }
              }
            }
            catch(Exception e){
              review[reviewCounter] = "A non numeric value was detected in Net Volume cell\t";
            }
          }
          else if(doesExist(colTitles[8], codes[i][0])){  // Gross Weight
            if((sheet[n][i].trim().equals("")) && (checkName.getProductName().trim().equals("") == false)){
              review[reviewCounter] = review[reviewCounter] + "missing gross weight\t";
            }
            else if(checkName.getProductName().trim().equals("") == false){
              try{
                double gWeight = Double.parseDouble(sheet[n][i]);
                if(gWeight < weight){
                  review[reviewCounter] = review[reviewCounter] + "Gross weight smaller than weight in product name\t";
                }
              }
              catch(Exception e){
                review[reviewCounter] = review[reviewCounter] + "not a valid value in gross weight\t";
              }
            }
          }
          else if(doesExist(colTitles[1], codes[i][0])){  // Ingredient for allergies, and additives
            ingPos = Integer.parseInt(codes[i][1]);

            if((sheet[n][ABVpos] != null) && (ABVpos != 0)){
              if(sheet[n][ABVpos].trim().equals("") == false){

                String abv = "";

                for(int k = 0; k < sheet[n][ABVpos].length(); k++){
                  if(Character.isDigit(sheet[n][ABVpos].charAt(k))){
                    abv = abv + sheet[n][ABVpos].charAt(k);
                  }
                  else if((sheet[n][ABVpos].charAt(k) == ',') || (sheet[n][ABVpos].charAt(k) == '.')){
                    abv = abv + ".";
                  }
                }

                sheet[n][ABVpos] = abv;
                abv = "";

                try{
                  if(Double.parseDouble(sheet[n][ABVpos]) >= 10.0){
                    if(doesExist("Sulfit", sheet[n][i]) == false){
                      sheet[n][i] = "Enthält Sulfite. " + sheet[n][i];
                    }
                  }
                }
                catch(Exception e){
                  e.printStackTrace();
                }
              }
            }
          }
          else if(doesExist(colTitles[9], codes[i][0])){  // Nutritional declaration

            if((sheet[n][ingPos].trim().equals("")) && (sheet[n][i].trim().equals("") == false)){
              review[reviewCounter] = review[reviewCounter] + "Empty ingredient cell.\t";
            }


            if((sheet[n][i].trim().equals("") == false) && (sheet[n][i].length() > 5)){
              String nutrition = "";
              try{
                nutrition = checkNutrition(sheet[n][i]);
                if(jcal.trim().equals("") == false){
                  review[reviewCounter] = review[reviewCounter] + "Check nutritional declaration. " + jcal + "\t";
                }
              }
              catch(IllegalArgumentException e){
                review[reviewCounter] = review[reviewCounter] + "Check nutritional declaration\t";
              }
              finally{
                if(nutrition.trim().equals("") == false){
                  sheet[n][i] = nutrition;
                }
              }
            }


          }
          else if(doesExist(colTitles[2], codes[i][0])){  // Allergies
            sheet[n][i] = checkAllergies.check(sheet[n][i]);
            if(ingPos != 0){
              sheet[n][i] = checkAllergies.readIngredients(sheet[n][ingPos]);
            }
          }
          else if(doesExist(colTitles[3], codes[i][0])){  // Additives
            sheet[n][i] = checkAdditives.check(sheet[n][i]);
            if(ingPos != 0){
              sheet[n][i] = checkAdditives.readIngredients(sheet[n][ingPos]);
            }
          }
          else if(doesExist(colTitles[5], codes[i][0])){  // Manufacturer Information
            if(checkName.getProductName().equals("") == false){
              if((sheet[n][i].trim().equals("")) || ((sheet[n][i-1].trim().equals("")) && sheet[n][i-1] != null) ||(sheet[n][i-2].trim().equals("") && sheet[n][i-2] != null)){
                review[reviewCounter] = review[reviewCounter] + "missing manufacturer information\t";
              }
            }
          }
          else if(doesExist("ABV", codes[i][0])){         // ABV
            if(sheet[n][i].trim().equals("") == false){
              try{
                if((Double.parseDouble(sheet[n][i]) > 90.0) || (Double.parseDouble(sheet[n][i]) < 0.0)){
                  review[reviewCounter] = review[reviewCounter] + "The value in ABV(%) is not valid\t";
                }
              }
              catch(Exception e){
                review[reviewCounter] = review[reviewCounter] + "Not a valid value in ABV(%) cell\t";
              }
            }
          }
        }


        line = line + sheet[n][i] + "\t";
      }

      if(review[reviewCounter].equals("") == false){
        rework[reviewCounter] = line;
        reviewCounter++;
      }
      else{
        done[doneCounter] = line;
        doneCounter++;
      }
    }


    System.out.println("Lines : " + filledLines);
    System.out.println("No Pictures : " + noPics);
    double proportion = (double)(noPics)/(double)(filledLines);
    System.out.println("Missing Pictures : " + 100 * proportion + "%");


    TextIO.writeFile("Special_5\\Special_5.txt");
    for(int n = 0; n < done.length; n++){
      if(done[n] != null){
        if(done[n].trim().equals("") == false){
          TextIO.putln(done[n]);
        }
      }
    }


    TextIO.writeFile("done\\R_Library\\done_"+pathMod);
    for(int n = 0; n < done.length; n++){
      if(done[n] != null){
        if(done[n].trim().equals("") == false){
          TextIO.putln(done[n]);
        }
      }
    }


    TextIO.writeFile("Special_5\\Review_Special_5.txt");
    for(int n = 0; n < rework.length; n++){
      int pos = 0;
      if(rework[n] != null){
        if(rework[n].trim().equals("") == false){
          TextIO.putln(rework[n]);
          for(int i = 0; i < review[n].length(); i++){
            if((review[n].charAt(i) == '\t') || (i == review[n].length()-1)){
              if(review[n].substring(pos, i).trim().equals("") == false){
                TextIO.putln(review[n].substring(pos, i+1));
              }
              pos = i+1;
            }
          }
          TextIO.putln("\n");
        }
      }
    }


  }






  private static boolean doesExist(String small, String big){
    boolean found = false;
    char c = ' ';

    if((small != null) && (big != null)){
      small = small.trim();
      big = big.trim();
    }
    else{
      return false;
    }

    String runner = "";
    String back = "";

    for(int n = big.length()-1; n >= 0; n--){
      back = big.charAt(n) + runner;
      runner = back;

      if(runner.length() >= small.length()){
        if(runner.substring(0, small.length()).equalsIgnoreCase(small)){
          found = true;
        }
      }
    }

    return found;
  }





  /*
  *   This subroutine is responsible for correcting any minor mistakes of cells belonging to the
  *   "Nutritional decleration" column. It does this by reading contents of the cell, if there are
  *   any, backwards until it reaches one of the key words, here reading the contents
  *   of the cell backwards becomes important, because as we reach the keyword we must, ideally,
  *   have already read the value for that keyword. So after having detected the keyword we move on
  *   with to a different loop that is responsible for reading the values, which are in the same
  *   variable that the keyword was in.
  *   There are things, as always, to take into account. Since we are dealing with many different
  *   people filling the cells, we must also implement a certain degree of flexibility within the
  *   structure of this subroutine. This means that it must be capable of recognizing a variety of
  *   mistakes it might encounter, and deal with properly. There are some problems that it will
  *   not be able to solve, hence it will throw an exception which will be called and used to
  *   create the review file.
  */
  public static String checkNutrition(String nutr) throws IllegalArgumentException{
    jcal = "";
    String eUnit1 = "";
    String eUnit2 = "";
    String runner = "";
    String back = "";
    String tempRes = "";
    String tempVal = "";
    String result = "";
    String eval1 = "";
    String eval1Runner = "";
    String eval2 = "";
    String e = "";
    char c = ' ';
    boolean found = false;
    boolean dotted = false;


    //  Read the contents of the cell backwards, until we reach a keyword.
    //  We compare the keywords only to the very beginning of the runner variable.
    for(int n = nutr.length()-1; n >= 0; n--){
      c = nutr.charAt(n);
      back = c + runner;
      runner = back;
      back = "";

      nutritionLoop : for(int i = nlist.length-1; i >= 0; i--){
        if(runner.length() >= nlist[i][0].length()){
          if(runner.substring(0, nlist[i][0].length()).equalsIgnoreCase(nlist[i][0])){
            tempRes = nlist[i][1] + " ";
            runner = runner.substring(nlist[i][0].length(), runner.length());
            found = true;
            break nutritionLoop;
          }
        }
      }


      //  Upon finding a keyword within the contents of the cell boolean value changes from false
      //  to true, this serves as a signal for the subroutine that it must now start looking for any
      //  possible numberical values for this keyword.
      //  We must take into account that for the keyword "Energy" there must exist two different
      //  values, and this section will be capable of recognizing this as well.
      //  The subroutine reads all that is left in the "runner" variable, and it looks,
      //  first and foremost, for a number, and it will take any ',' or '.' given that a numerical
      //  must proceed and precede them. As long as these conditions are met, characters are taken
      //  and are stored in a variable called "tempVal", and once these conditions are no longer
      //  true, we will take the keyword and append the value to it and add the unit.
      if(found == true){
        if(tempRes.trim().equalsIgnoreCase("energie")){   //  Keyword is "Energy"

          if(e.equals("") == true){
            e = tempRes;
          }

          valueLoop : for(int i = 0; i < runner.length(); i++){ // Read numbers and ',' and '.'
            char c2 = runner.charAt(i);
            if(Character.isDigit(c2)){
              tempVal = tempVal + c2;
            }
            else if((c2 == '.') || (c2 == ',')){
              if(((i > 0) && (i < runner.length()-1)) && (dotted == false)){ // Only one dot can exit in a number.
                if((Character.isDigit(runner.charAt(i-1))) && (Character.isDigit(runner.charAt(i+1)))){
                  if(c2 == ','){
                    c2 = '.';
                  }
                  tempVal = tempVal + c2;
                }
              }
            }
            else{
              if((eval1.equals("")) && (tempVal.equals("") == false)){
                eval1 = tempVal;      // First make sure that eval1 is not empty, make sure that
                tempVal = "";         // entering this section is only possible if the two
                found = false;        // conditions in the "if()" statement are met.
                dotted = false;


                A_unitLoop : for(int k = i; k < runner.length(); k++){
                  if(Character.isLetter(runner.charAt(k))){
                    tempVal = tempVal + runner.charAt(k);
                    if(doesExist("kilojoule", eUnit1) || doesExist("kJ", tempVal)){
                      eUnit1 = "kJ";
                      break A_unitLoop;
                    }
                    else if(doesExist("Kilokalorie", eUnit1) || doesExist("kcal", tempVal)){
                      eUnit1 = "kcal";
                      break A_unitLoop;
                    }
                  }
                  else if(Character.isDigit(runner.charAt(k)) || k == (runner.length()-1)){
                    break A_unitLoop;
                  }
                }
              }
              else if((tempVal.equals("") == false) && (eval1.equals("") == false)){
                eval2 = tempVal;      // After having initialized eval1, we can initialize eval2.
                tempVal = "";         // If eval1 is empty then we cannot enter this section.
                found = false;
                dotted = false;

                B_unitLoop : for(int m = i; m < runner.length(); m++){
                  if(Character.isLetter(runner.charAt(m))){
                    tempVal = tempVal + runner.charAt(m);
                    if(doesExist("kilojoule", tempVal) || doesExist("kJ", tempVal)){
                      eUnit2 = "kJ";
                      break B_unitLoop;
                    }
                    else if(doesExist("Kilokalorie", eUnit2) || doesExist("kcal", tempVal)){
                      eUnit2 = "kcal";
                      break B_unitLoop;
                    }
                  }
                  else if(Character.isDigit(runner.charAt(m)) || m == (runner.length()-1)){
                    break B_unitLoop;
                  }
                }

                runner = "";
                break valueLoop;
              }
              tempVal = "";
            }
          }

          runner = "";
        }
        else{
          valueLoop : for(int i = 0; i < runner.length(); i++){ // Take numerical value for all other keywords.
            char c2 = runner.charAt(i);
            if(Character.isDigit(c2)){
              tempVal = tempVal + c2;
            }
            else if((c2 == '.') || (c2 == ',')){    //  These are the conditions.
              if((i > 0) && (i < runner.length()-1) && (dotted == false)){
                if((Character.isDigit(runner.charAt(i-1))) && (Character.isDigit(runner.charAt(i+1)))){
                  if(c2 == ','){
                    c2 = '.';
                  }
                  tempVal = tempVal + c2;
                }
              }
            }
            else{
              if(tempVal.equals("") == false){
                break valueLoop;
              }
            }
          }

          if(tempVal.equals("") == false){
            if(Double.parseDouble(tempVal) != 0){
              tempRes = tempRes + tempVal + "g, ";
              tempRes = tempRes + result;
              result = tempRes;
              dotted = false;
            }
          }
          tempRes = "";
          tempVal = "";
          runner = "";
          found = false;
          dotted = false;
        }
      }
    }




    // For the keyword "Energy" we can now put everything together.
    // If we do not possess two valid numerical values then we will not be able to do so.
    if(((eval1.trim().equals("") == false) || (eval2.trim().equals("") == false))){
      double val1;
      double val2;

      if((eUnit1.equals("") == false) || (eUnit2.equals("") == false)){
        try{
          if(eUnit1.equals("") == false){
            val1 = Double.parseDouble(eval1);
          }
          else{
            throw new NumberFormatException();
          }
        }
        catch(Exception err1){
          if(eUnit1.equalsIgnoreCase("kj") && eUnit2.equalsIgnoreCase("kcal")){
            eval1 = "" + ((int)(Double.parseDouble(eval2)*4.184));
          }
          else if(eUnit1.equalsIgnoreCase("kcal") && eUnit2.equalsIgnoreCase("kj")){
            eval1 = "" + ((int)(Double.parseDouble(eval2)/4.184));
          }
          else if(eUnit2.equalsIgnoreCase("kcal")){
            eval1 = "" + ((int)(Double.parseDouble(eval2)*4.184));
            eUnit1 = "kJ";
          }
          else if(eUnit2.equalsIgnoreCase("kJ")){
            eval1 = "" + ((int)(Double.parseDouble(eval2)/4.184));
            eUnit1 = "kcal";
          }

          val1 = Double.parseDouble(eval1);
        }


        try{
          if(eUnit2.equals("") == false){
            val2 = Double.parseDouble(eval2);
          }
          else{
            throw new NumberFormatException();
          }
        }
        catch(Exception err2){
          if(eUnit2.equalsIgnoreCase("kj") && eUnit1.equalsIgnoreCase("kcal")){
            eval2 = "" + ((int)(Double.parseDouble(eval1)*4.184));
          }
          else if(eUnit2.equalsIgnoreCase("kcal") && eUnit1.equalsIgnoreCase("kj")){
            eval2 = "" + ((int)(Double.parseDouble(eval1)/4.184));
          }
          else if(eUnit1.equalsIgnoreCase("kcal")){
            eval2 = "" + ((int)(Double.parseDouble(eval1)*4.184));
            eUnit2 = "kJ";
          }
          else if(eUnit1.equalsIgnoreCase("kJ")){
            eval2 = "" + ((int)(Double.parseDouble(eval1)/4.184));
            eUnit2 = "kcal";
          }

          val2 = Double.parseDouble(eval2);
        }
      }
      else if(eUnit1.equals("") && eUnit2.equals("")){
        if(Double.parseDouble(eval1) > Double.parseDouble(eval2)){
          eUnit1 = "kJ";
          eUnit2 = "kcal";
        }
        else if(Double.parseDouble(eval1) < Double.parseDouble(eval2)){
          eUnit1 = "kcal";
          eUnit2 = "kJ";
        }
      }

      e = e + eval1 + " " + eUnit1 + " / " + eval2 + " " + eUnit2;
    }
    else if(eval1.trim().equals("") && eval2.trim().equals("")){
      throw new NumberFormatException();
    }




    result = e + ", " + result;
    if(result.length() > 2){
      result = result.substring(0, result.length()-2);
    }
    return result;
  }
}
