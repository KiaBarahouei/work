import textio.TextIO;

public class CheckAllergies{

  private static String[] allAllergies = {"A", "Wh", "Ry", "Ba", "Oa", "Sp", "Ka", "B", "C", "D", "E", "F",
   "G", "H", "Al", "Ha", "Wa", "Ca", "Pe", "Br", "Pi", "Ma", "Qu", "L", "M", "N", "O", "P", "R"};
  private static String[][] allergyList = {
    {"Getreide", "A"}, {"Glutenhaltig", "A"}, {"Weizen", "Wh"}, {"Roggen", "Ry"}, {"Gerste", "Ba"},
    {"Hafer", "Oa"}, {"Dinkel", "Sp"}, {"Kamut", "Ka"}, {"Krebstiere", "B"}, {"Kreb", "B"},
    {"Krebse", "B"}, {"Langusten", "B"}, {"Hummer", "B"}, {"Garnelen", "B"}, {"Krabben", "B"},
    {"Eier", "C"}, {"Hühnerei", "C"}, {"Fisch", "D"}, {"Erdnüsse", "E"}, {"Erdnuss", "E"}, {"Soja", "F"}, {"E322", "F"},
    {"Milch", "G"}, {"Molken", "G"}, {"Nüsse", "H"}, {"Schalenfrüchte", "H"}, {"Nuss", "H"},
    {"Mandeln", "Al"}, {"Mandel", "Al"}, {"Haselnüsse", "Ha"}, {"Haselnuss", "Ha"}, {"Walnüsse", "Wa"},
    {"Walnuss", "Wa"}, {"Caschew", "Ca"}, {"Cashew", "Ca"}, {"Pekann", "Pe"}, {"Pistaz", "Pi"},
    {"Macadamian", "Ma"}, {"Queensland", "Qu"}, {"Sellerie", "L"}, {"Senf", "M"}, {"Sesam", "N"},
    {"Schwefeloxid", "O"}, {"Sulfit", "O"}, {"Lupin", "P"}, {"Weichtiere", "R"}, {"Schnecken", "R"},
    {"Muscheln", "R"}, {"Kopffüßer", "R"}, {"Kopffuß", "R"}, {"Tintenfisch", "R"}, {"Mollusken", "R"},
    {"Schalentiere", "R"}, {"Austern", "R"}
  };


  private static String[][] allergyGroups = {
    {"A", "Wh", "Ry", "Ba", "Oa", "Sp", "Ka"},
    {"H", "Al", "Ha", "Wa", "Ca", "Pe", "Br", "Pi", "Ma", "Qu"}
  };




  public static String result = "";







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







  public static String check(String str){
    int length = str.length();
    result = "";

    if(length == 0){
      return "";
    }

    String runner = "";
    char c;

    str = str+".";

    for(int n = 0; n < length+1; n++){
      c = str.charAt(n);

      if(Character.isLetter(c) == true){
        runner = runner+c;
      }
      else{
        if((runner.length() == 2) || (runner.length() == 1)){
          if(runner.equals("AI")){
            runner = "Al";
          }
          for(int i = 0; i < allAllergies.length; i++){
            if(runner.equalsIgnoreCase(allAllergies[i])){
              result = result+allAllergies[i]+", ";
            }
          }
        }
        runner = "";
      }
    }
    if(result == ""){
      return "";
    }
    result = result.substring(0, result.length()-2);
    return result;
  }






  public static void readIngredients(String line){
    int length = line.length();
    String runner = "";
    char c = ' ';


    for(int n = 0; n < length; n++){
      c = line.charAt(n);
      if((Character.isLetter(c)) || (Character.isDigit(c))){
        runner = runner + c;
        if(((c == 'E') && (line.charAt(n+1) == ' ')) && (Character.isDigit(line.charAt(n+2)))){
          runner = runner + " ";
        }

        if(runner.length() >= 2){
          for(int i = 0; i < allergyList.length; i++){
            if(runner.equalsIgnoreCase(allergyList[i][0])){
              if(result.equals("")){
                result = allergyList[i][1];
              }
              else{
                result = result + ", " + allergyList[i][1];
              }
              System.out.println(allergyList[i][0]);
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
          for(int i = 0; i < allergyList.length; i++){
            if(runner.equalsIgnoreCase(allergyList[i][0])){
              if(result.equals("") != true){
                result = result + ", "+allergyList[i][1];
              }
              else{
                result = allergyList[i][1];
              }
              System.out.println(allergyList[i][0]);
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
      if(Character.isLetter(c)){
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
      if(Character.isLetter(c)){
        runner = runner + c;
        if((i == length-1)){
          if(word.equals(runner)){
            word = "";
          }
        }
      }
      else if(c == ','){
        if(word.equals(runner) == true){
          word = "";
          break secondLoop;
        }
        i++;
        runner = "";
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
      if((Character.isLetter(c))){
        word = word + c;
      }
      else{
        if(word != ""){
          thisLoop : for(int i = 0; i < allergyGroups.length; i++){
            for(int m = 1; m < allergyGroups[i].length; m++){
              if(word.equals(allergyGroups[i][m])){
                result = result + ", " + allergyGroups[i][0];
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
