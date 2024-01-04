import textio.TextIO;


/**
  * This class is responsible for checking all additives and making sure that they are
  * written correctly. This class will also include a function that will check the list of
  * ingredients for any product and compare it to the list of declared additives. This function
  * is also responsible for any needed correction to the additives list.
  */

public class CheckAdditives{

  public static String[] allCodes = {"1", "1.1", "2", "2.1", "2.2", "2.3", "3", "4", "5", "6", "7", "8", "9",
                              "9.1", "9.2", "10", "10.1", "10.2", "11", "12", "13", "14", "15",
                              "15.1", "15.2", "15.3"};



  public static String[][] additiveList = {{"Farbstoff", "1"},
    {"E 102", "1.1"}, {"E 110", "1.1"}, {"E 122", "1.1"}, {"E 124", "1.1"}, {"E 129", "1.1"},
    {"E 104", "1.1"}, {"Tartrazin", "1.1"}, {"Gelborange", "1.1"}, {"Azorubin", "1.1"}, {"Cochenillerot", "1.1"},
    {"Allurarot", "1.1"}, {"E102", "1.1"}, {"E110", "1.1"}, {"E122", "1.1"}, {"E124", "1.1"}, {"E129", "1.1"},
    {"Konservierungsstoff", "2"}, {"Nitritpökelsalz", "2.1"}, {"Nitrat", "2.2"}, {"Antioxidationsmittel", "3"},
    {"Geschmacksverstärker", "4"}, {"Sulfite", "5"}, {"Geschwärzt", "6"}, {"Gewachst", "7"}, {"Überzugsmittel", "7"}, {"Wachs", "7"}, {"Phosphat", "8"},
    {"Süßungsmittel", "9"}, {"Zucker", "9"}, {"Phenylalanin", "9.1"}, {"Abführend", "9.2"}, {"Abführ", "9.2"}, {"Koffein", "10"}, {"Chinin", "11"},
    {"Gentechnisch verändert", "12"}, {"Gentechnisch", "12"}, {"Säuerungsmittel", "13"}, {"Säure", "13"},
    {"Säurergulator", "13"}, {"Stabilisator", "14"}, {"Eiweiß", "15"}, {"Protein", "15"}, {"Stärke", "15.2"},
  };


  public static String[][] additiveGroups = {
    {"1", "1.1"},
    {"2", "2.1", "2.2", "2.3"},
    {"9", "9.1", "9.2"},
    {"10", "10.1", "10.2"},
    {"15", "15.1", "15.2", "15.3"}
  };


  public static String result = "";










  /**
  * The main() method is only here for testing. All subroutines and global variable are declared
  * as static for the purpose of testing within the same class. Once this class is complete
  * I will use it for the creation of objects.
  */

  public static void main(String[] args){
    thisLoop : while(true){
      String message = TextIO.getln();
      if(message.equalsIgnoreCase("exit")){
        break thisLoop;
      }
      message = check(message);
      message = duplicates(message);
      System.out.println(message);
      TextIO.readFile("Special.txt");
      message = TextIO.getln();
      readIngredients(message);
      System.out.println(result);
      TextIO.readStandardInput();
    }
  }













  /**
  * The check() methode will only correct that which is already written within the additives cell
  * for a given product. It contains a set of rules that it follows to achieve this goal.
  */

  public static String check(String str){
    result = "";
    str = str + ", ";
    boolean containsDot = false;
    String runner = "";
    int length;

    if(str.length() == 0){
      return "";
    }
    else{
      length = str.length();
    }


    //  We have read one line and now we will process the first line, by taking each of the
    //  characters that it is comprised of, each time a '\t' is dected we know that the next
    // "data type" is not the same as the one we have been dealing with currently.
    for(int n = 0; n < length; n++){
      boolean found = false;
      char c = str.charAt(n);

      if(Character.isDigit(c)){
        runner = runner + c;
      }

      for(int i = 0; i < allCodes.length; i++){
        if(allCodes[i].equals(runner) ){
          found = true;
        }
      }

      //  Here the set of rules start.
      //  If the code is found in allCodes list then we will enter the first if clause.
      if(found == true){
        if(runner.length() == 4){           //  If the length of the detected code is 4 then
          result = result + runner + ", ";  //  we do not need to do much. simply add it to the
          runner = "";                      //  result and reset all other variables.
        }
        else if(runner.length() < 4){     // From here on we see what happens if the code length
          if(runner.length() == 1){       // is less than 4.
            if(Character.isDigit(str.charAt(n+1)) == false){
              if(str.charAt(n+1) != '.'){
                result = result + runner + ", ";
                runner = "";
              }
              else if((str.charAt(n+1) == '.') && (Character.isDigit(str.charAt(n+2)))){
                if(Character.isDigit(str.charAt(n+3)) == false){
                  runner = runner + '.';
                  n++;
                  containsDot = true;
                }
                else{
                  result = result + runner + ", ";
                  runner = "";
                }
              }
            }
          }
          else if(runner.length() == 2){
            if(str.charAt(n+1) != '.'){
              result = result + runner + ", ";
              runner = "";
            }
            else if((str.charAt(n+1) == '.') && (Character.isDigit(str.charAt(n+2)))){
              if(Character.isDigit(str.charAt(n+3)) == false){
                runner = runner + '.';
                n++;
                containsDot = true;
              }
              else{
                result = result + runner + ", ";
              }
            }
          }
          else if(runner.length() == 3){
            if(Character.isDigit(str.charAt(n+1)) == false){
              result = result + runner + ", ";
              runner = "";
            }
          }
        }
      }
      else if((found == false) && (runner != "")){
        /**
        * If the code detected thus far, cannot be found in the allCodes list, then we need to
        * make sure that the mistake is corrected. If the code is 52 for example then the code is
        * simply invalid and should not be considered. This mistake can also be rectified, but not
        * in this subroutine.
        * Instead if the the detected code is 5.2 for example, then ofcourse this will not be found
        * within the allCodes list, but it may be that the "." is used as seperator, such mistakes
        * will be corrected here.
        */
        if(containsDot == true){
          result = result + runner.substring(0, runner.length()-2)+ ", ";
          n--;
        }
        runner = "";
      }
    }

    if(result.length() > 2){
      result = result.substring(0, result.length()-2);
    }
    return result;
  }














  public static void readIngredients(String line){
    String runner = "";
    int length = line.length();
    char c;


    forwardsRead : for(int n = 0; n < length; n++){
      c = line.charAt(n);

      if((Character.isLetter(c)) || (Character.isDigit(c))){
        runner = runner + c;
        if(((c == 'E') && (line.charAt(n+1) == ' ')) && (Character.isDigit(line.charAt(n+2)))){
          runner = runner + " ";
          n++;
        }

        if(runner.length() >= 4){
          for(int i = 0; i < additiveList.length; i++){
            if(runner.equalsIgnoreCase(additiveList[i][0])){
              if(result.equals("") != true){
                result = result + ", "+additiveList[i][1];
              }
              else{
                result = additiveList[i][1];
              }
              System.out.println(additiveList[i][0]);
              runner = "";
            }
          }
        }
      }
      else{
        runner = "";
      }
    }

    runner = "";
    String holder = "";

    backwardsRead : for(int n = length-1; n > -1; n--){
      c = line.charAt(n);
      if((Character.isLetter(c)) || (Character.isDigit(c))){
        holder = ""+c;
        if(runner == ""){
          runner = holder;
        }
        else{
          holder = holder + runner;
          runner = holder;
          holder = "";
        }


        if(runner.length() >= 4){
          for(int i = 0; i < additiveList.length; i++){
            if(runner.equalsIgnoreCase(additiveList[i][0])){
              if(result.equals("") != true){
                result = result + ", "+additiveList[i][1];
              }
              else{
                result = additiveList[i][1];
              }
              System.out.println(additiveList[i][0]);
              runner = "";
            }
          }
        }
      }
      else{
        runner = "";
      }

    }


    grouping(result);
    result = duplicates(result);
  }













  public static String duplicates(String line){

    if(line == null || line.equals("") || line.equals("\n") || line.equals("\t")){
      return "";
    }


    int length = line.length();
    int pos = 0;
    char c;
    String runner = "";
    String word = "";


    //  Takes the first set of characters, within an ordered String, which comes right before a
    // comma (',').
    firstLoop : for(int n = 0; n < length; n++){
      c = line.charAt(n);
      if(c != ','){
        word = word + c;
      }
      else{
        pos = n+2;
        break firstLoop;
      }

      if(n == length-1){
        return word;
      }
    }


    // The firs "word" we have taken from the line, is now compared to all other words within
    // the line and if a duplicate is detected, the current word is equalized to "".
    secondLoop : for(int i = pos; i < length; i++){
      c = line.charAt(i);
      if((Character.isDigit(c)) || (c == '.')){
        runner = runner + c;
        if(i == length-1){
          if(word.equals(runner)){
            word = "";
          }
        }
      }
      else{
        if(c == ','){
          if(word.equals(runner) == true){
            word = "";
            break secondLoop;
          }
          i++;
          runner = "";
        }
      }
    }


    //  Now repeat this process with a substring of the original string, such that the first
    //  word is not taken into account in any further processes.
    if(word.equals("")){
      word = duplicates(line.substring(pos, length));
    }
    else{
      word = word +", "+ duplicates(line.substring(pos, length));
    }
    return word;
  }






  public static void grouping(String line){
    if(line == null || line.equals("") || line.equals("\n") || line.equals("\t")){
      return;
    }
    line = line.trim() + ",";
    int length = line.length();
    char c = ' ';
    String word = "";

    for(int n = 0; n < length; n++){
      c = line.charAt(n);
      if((Character.isDigit(c)) || (c == '.')){
        word = word + c;
      }
      else{
        if(word != ""){
          thisLoop : for(int i = 0; i < additiveGroups.length; i++){
            for(int m = 1; m < additiveGroups[i].length; m++){
              if(word.equals(additiveGroups[i][m])){
                result = result + ", " + additiveGroups[i][0];
                break thisLoop;
              }
            }
          }
        }
        word = "";
      }
    }
  }

}
