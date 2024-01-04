import textio.TextIO;

public class NewCheckSheetTester{

  private static ReadSheet readSheet = new ReadSheet();
  private static CheckName checkName = new CheckName();
  private static CheckAdditives checkAdditives = new CheckAdditives();
  private static CheckAllergies checkAllergies = new CheckAllergies();


  private static String[][] sheet = new String[1][1];
  private static String[][] codes = new String[1][1];


  private static String[] review;
  private static String[] rework;
  private static String[] done;
  private static int reviewCounter = 0;
  private static int ingPos = 0;
  private static int filledLines = 0;
  private static int noPics = 0;
  private static double weight = 0;
  private static int ABVpos = 0;
  private static String unit = "";
  private static String deposit = "";


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
    String line = "";

    sheet = readSheet.initializeList(path);
    done = new String[sheet.length];
    review = new String[sheet.length];
    rework = new String[sheet.length];

    int doneCounter = 0;
    int catPos = 0;
    codes = readSheet.getCodes();


    for(int n = 0; n < sheet[0].length; n++){
      if(doesExist("Category", sheet[0][n])){
        catPos = n;
        break;
      }
    }


    for(int k = 0; k < codes.length; k++){
      if(doesExist("ABV", codes[k][0])){
        if(sheet[0][k] != null){
          ABVpos = k;
          break;
        }
      }
    }


    for(int k = 0; k < codes.length; k++){
      if(doesExist("QUID", codes[k][0])){
        ingPos = k;
        break;
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
            sheet[n][i] = productName(sheet[n][i], sheet[n][catPos]);
          }
          else if(doesExist("Product Image URL", codes[i][0])){ // Product Image
            productImage(sheet[n][i]);
          }
          else if(doesExist("Deposit Amount", codes[i][0])){  // Deposit Amount
            sheet[n][i] = depositAmount(sheet[n][i]);
          }
          else if(doesExist(colTitles[6], codes[i][0])){  // Net Weight
            netWeight(sheet[n][i]);
          }
          else if(doesExist(colTitles[7], codes[i][0])){  // Net Volume
            netVolume(sheet[n][i]);
          }
          else if(doesExist(colTitles[8], codes[i][0])){  // Gross Weight
            grossWeight(sheet[n][i]);
          }
          else if(doesExist(colTitles[1], codes[i][0])){  // Ingredient and ABV
            sheet[n][ABVpos] = checkABV(sheet[n][ABVpos]);
            sheet[n][i] = ingredients(sheet[n][i], sheet[n][ABVpos]);
          }
          else if(doesExist(colTitles[9], codes[i][0])){  // Nutritional declaration
            sheet[n][i] = nutritionalDeclaration(sheet[n][i], sheet[n][ingPos]);
          }
          else if(doesExist(colTitles[2], codes[i][0])){  // Allergies
            sheet[n][i] = allergies(sheet[n][i], sheet[n][ingPos]);
          }
          else if(doesExist(colTitles[3], codes[i][0])){  // Additives
            sheet[n][i] = additives(sheet[n][i], sheet[n][ingPos]);
          }
          else if(doesExist(colTitles[5], codes[i][0])){  // Manufacturer Information
            manufacturer(sheet[n][i], sheet[n][i-1], sheet[n][i-2]);
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









  private static String productName(String name, String category){
    name = checkName.checkProductName(name);
    weight = checkName.getProductWeightInDouble();
    unit = checkName.getUnit();

    if((name.equals("") == true) && (checkName.getProductName().equals("") == false)){
      review[reviewCounter] = review[reviewCounter] + "Missing product name\t";
    }

    if(checkName.getProductName().equals("") == false){
      if(category.trim().equals("")){
        review[reviewCounter] = review[reviewCounter] + "Missing category\t";
      }

      filledLines++;
    }

    return name;
  }









  private static void productImage(String image){
    if(checkName.getResult().trim().equals("") == false){
      if(image.trim().equals("")){
        noPics++;
      }
    }
  }









  private static String depositAmount(String depoAmount){
    if((depoAmount.trim().equals("") == false) && (checkName.getDepositType().trim().equals("") == false)){
      boolean discrepency = true;
      double am;

      for(int k = 0; k < depoAmount.length(); k++){
        if((depoAmount.charAt(k) == ',')){
          if(k < depoAmount.length()-1){
            depoAmount = depoAmount.substring(0, k) + "." + depoAmount.substring(k+1, depoAmount.length());
          }
          else if(k == depoAmount.length()-1){
            depoAmount = depoAmount.substring(0, k);
          }
        }
      }

      try{
        if(checkName.getAmount().trim().equals("") == false){
          am = (Double.parseDouble(depoAmount.trim()) / Double.parseDouble(checkName.getAmount()));
        }
        else{
          am = Double.parseDouble(depoAmount.trim());
        }
      }
      catch(Exception depoErr){
        am = -1;
      }


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
          depoAmount = "" + (Double.parseDouble(checkName.getAmount()) * am);
        }
        else{
          depoAmount = "" + am;
        }
      }
    }
    else if((depoAmount.trim().equals("") == false) ^ (checkName.getDepositType().trim().equals("") == false)){
      review[reviewCounter] = review[reviewCounter] + "Discrepency found: Deposit Type and Deposit Amount.\t";
    }

    return depoAmount;
  }









  private static void netWeight(String nettoWeight){
    try{
      if(checkName.getProductWeight().equals("") == false){
        nettoWeight = nettoWeight.trim();
        if(nettoWeight.equals("")){
          if(checkName.getUnit().equals("g")){
            review[reviewCounter] = review[reviewCounter] + "Net weight cell is empty\t";
          }
        }
        else if(nettoWeight.equals("") == false){
          if(checkName.getUnit().equals("l")){
            review[reviewCounter] = review[reviewCounter] + "Wrong weight cell for " + checkName.getProductWeight() + checkName.getUnit() + "\t";
          }
          else if(checkName.getUnit().equals("")){
            review[reviewCounter] = review[reviewCounter] + "Missing unit of weight\t";
          }
          else{
            String temp = "";
            for(int k = 0; k < nettoWeight.length(); k++){
              if((Character.isDigit(nettoWeight.charAt(k)) || (nettoWeight.charAt(k) == '.') || (nettoWeight.charAt(k) == ','))){
                temp = temp + nettoWeight.charAt(k);
              }
            }

            if(weight != Double.parseDouble(temp)){
              review[reviewCounter] = review[reviewCounter] + "Discrepency : product weigh and net weight\t";
            }
          }
        }
      }
      else{
        if(nettoWeight.equals("") == false){
          review[reviewCounter] = review[reviewCounter] + "Missing product weight in name\t";
        }
      }
    }
    catch(Exception netWeightErr){
      review[reviewCounter] = review[reviewCounter] + "Not a valid number in net weight\t";
    }
  }










  private static void netVolume(String nettoVolume){
    try{
      if(checkName.getProductWeight().equals("") == false){
        nettoVolume = nettoVolume.trim();
        if(nettoVolume.equals("")){
          if(checkName.getUnit().equals("l")){
            review[reviewCounter] = review[reviewCounter] + "Net volume cell is empty\t";
          }
        }
        else if(nettoVolume.equals("") == false){
          if(checkName.getUnit().equals("g")){
            review[reviewCounter] = review[reviewCounter] + "Wrong weight cell for " + checkName.getProductWeight() + checkName.getUnit() + "\t";
          }
          else if(checkName.getUnit().equals("")){
            review[reviewCounter] = review[reviewCounter] + "Missing unit of weight\t";
          }
          else{
            String temp = "";
            for(int k = 0; k < nettoVolume.length(); k++){
              if((Character.isDigit(nettoVolume.charAt(k)) || (nettoVolume.charAt(k) == '.') || (nettoVolume.charAt(k) == ','))){
                temp = temp + nettoVolume.charAt(k);
              }
            }

            if(weight != Double.parseDouble(temp)){
              review[reviewCounter] = review[reviewCounter] + "Discrepency : product weigh and net volume\t";
            }
          }
        }
      }
      else{
        if(nettoVolume.equals("") == false){
          review[reviewCounter] = review[reviewCounter] + "Missing product weight in name\t";
        }
      }
    }
    catch(Exception netVolumeErr){
      review[reviewCounter] = review[reviewCounter] + "Not a valid number in net volume\t";
    }
  }









  private static void grossWeight(String gWeight){
    if((gWeight.trim().equals("")) && (checkName.getProductName().trim().equals("") == false)){
      review[reviewCounter] = review[reviewCounter] + "Gross weight missing\t";
    }
    else if(checkName.getProductName().trim().equals("") == false){
      try{
        double val;

        if(checkName.getUnit().equalsIgnoreCase("l")){
          val = (1000 * Double.parseDouble(gWeight));
        }
        else{
          val = Double.parseDouble(gWeight);
        }

        if(val < weight){
          review[reviewCounter] = review[reviewCounter] + "Gross weight smaller than product weight\t";
        }
      }
      catch(Exception grossWeightErr){
        review[reviewCounter] = review[reviewCounter] + "Not a valid number in gross weight\t";
      }
    }
  }









  public static String ingredients(String ingL, String ABVcell){
    try{
      if((ABVcell != null) && (ABVcell.trim().equals("") == false)){
        if(Double.parseDouble(ABVcell) >= 10.0){
          if(doesExist("Sulfit", ingL) == false){
            ingL = "Enthält Sulfite. " + ingL;
          }
        }
      }
    }
    catch(Exception ingredientsErr){
      ingredientsErr.printStackTrace();
    }

    return ingL;
  }









  public static String checkABV(String ABVcell){
    if((ABVcell != null) && (ABVpos != 0)){
      if(ABVcell.trim().equals("") == false){
        String abv = "";
        for(int k = 0; k < ABVcell.length(); k++){
          if(Character.isDigit(ABVcell.charAt(k))){
            abv = abv + ABVcell.charAt(k);
          }
          else if((ABVcell.charAt(k) == ',') || (ABVcell.charAt(k) == '.')){
            abv = abv + ".";
          }
        }

        ABVcell = abv;

        try{
          if((Double.parseDouble(ABVcell) > 90.0) || (Double.parseDouble(ABVcell) < 0.0)){
            review[reviewCounter] = review[reviewCounter] + "Invalid ABV(%) value\t";
          }
        }
        catch(Exception abvErr){
          review[reviewCounter] = review[reviewCounter] + "Invalid ABV(%) value\t";
        }
      }
    }


    return ABVcell;
  }









  public static String nutritionalDeclaration(String nd, String ingredients){
    if((ingredients.trim().equals("")) && (nd.trim().equals("") == false)){
      review[reviewCounter] = review[reviewCounter] + "Empty ingredient cell\t";
    }
    else{
      if((nd.trim().equals("") == false) && (nd.length() > 5)){
        try{
          nd = checkNutrition(nd);
        }
        catch(IllegalArgumentException nutritionErr){
          review[reviewCounter] = review[reviewCounter] + "Nutrition could not be checked\t";
        }
      }
    }

    return nd;
  }









  private static String allergies(String al, String ingredients){
    al = checkAllergies.check(al);

    if(ingredients.trim().equals("") == false){
      al = checkAllergies.readIngredients(ingredients);
    }

    return al;
  }









  private static String additives(String ad, String ingredients){
    ad = checkAdditives.check(ad);

    if(ingredients.trim().equals("") == false){
      ad = checkAdditives.readIngredients(ingredients);
    }

    return ad;
  }









  private static void manufacturer(String man1, String man2, String man3){
    if(checkName.getProductName().equals("") == false){
      try{
        if((man1.trim().equals("")) || (man2.trim().equals("")) || (man3.trim().equals(""))){
          review[reviewCounter] = review[reviewCounter] + "Manufacturer information missing\t";
        }
      }
      catch(NullPointerException manufacturerErr){
        review[reviewCounter] = review[reviewCounter] + "Manufacturer information missing\t";
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
        if(tempRes.trim().equalsIgnoreCase("energie") || (tempRes.trim().equalsIgnoreCase("energy"))){   //  Keyword is "Energy"
          if(e.equals("") == true){
            tempRes = "Energie ";
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
