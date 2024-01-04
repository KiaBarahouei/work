import textio.TextIO;




/*
* This class is responsible for reading the ingredients and completing the allergy-code-cell.
*/
public class CheckAllergies{


  private String[] allAllergies = {"A", "Wh", "Ry", "Ba", "Oa", "Sp", "Ka", "B", "C", "D", "E", "F",
   "G", "H", "Al", "Ha", "Wa", "Ca", "Pe", "Br", "Pi", "Ma", "Qu", "L", "M", "N", "O", "P", "R"};


  private String[][] allergyList = {
    {"Getreide", "A"}, {"Glutenhaltig", "A"}, {"Weizen", "Wh"}, {"Roggen", "Ry"}, {"Gerste", "Ba"},
    {"Hafer", "Oa"}, {"Dinkel", "Sp"}, {"Kamut", "Ka"}, {"Krebstiere", "B"}, {"Kreb", "B"},
    {"Krebse", "B"}, {"Langusten", "B"}, {"Hummer", "B"}, {"Garnelen", "B"}, {"Krabben", "B"},
    {"Eier", "C"}, {"Hühnerei", "C"}, {"Fisch", "D"}, {"Erdnüsse", "E"}, {"Erdnuss", "E"}, {"Soja", "F"}, {"E322", "F"},
    {"Milch", "G"}, {"Molken", "G"}, {" Nüsse", "H"}, {"Schalenfrüchte", "H"}, {" Nuss", "H"},
    {"Mandeln", "Al"}, {"Mandel", "Al"}, {"Haselnüsse", "Ha"}, {"Haselnuss", "Ha"}, {"Walnüsse", "Wa"},
    {"Walnuss", "Wa"}, {"Caschew", "Ca"}, {"Cashew", "Ca"}, {"Pekann", "Pe"}, {"Pistaz", "Pi"},
    {"Macadamian", "Ma"}, {"Queensland", "Qu"}, {"Sellerie", "L"}, {"Senf", "M"}, {"Sesam", "N"},
    {"Schwefeloxid", "O"}, {"Sulfit", "O"}, {"sulfite", "O"}, {"Lupin", "P"}, {"Weichtiere", "R"}, {"Schnecken", "R"},
    {"Muscheln", "R"}, {"Kopffüßer", "R"}, {"Kopffuß", "R"}, {"Tintenfisch", "R"}, {"Mollusken", "R"},
    {"Schalentiere", "R"}, {"Austern", "R"}, {"Vollei", "C"}, {"Eigelb", "C"}, {"Eipulver", "C"},
    {" Ei ", "C"}, {"Ei,", "C"}, {"Ei ", "C"}
  };


  private String[][] allergyGroups = {
    {"A", "Wh", "Ry", "Ba", "Oa", "Sp", "Ka"},
    {"H", "Al", "Ha", "Wa", "Ca", "Pe", "Br", "Pi", "Ma", "Qu"}
  };


  private String result = "";









  /*
  * This method will recieve the allergy codes, and make sure that everythin is written correctly.
  * The first Letters must be capitalized, each code has to be separated from the other using a comma,
  * and only the accepted codes can be used.
  */
  public String check(String str){
    int length = str.length();
    result = "";

    if(length == 0){  // If the content of the cell is empty, then return nothing.
      return "";
    }

    String runner = "";
    char c;

    str = str+".";

    // This loop ensures that only the accepted codes are used. It takes the codes one by one and
    // compares them to the items in allAllergies list.
    for(int n = 0; n < length+1; n++){
      c = str.charAt(n);

      if(Character.isLetter(c) == true){
        runner = runner+c;
      }
      else{
        if((runner.length() == 2) || (runner.length() == 1)){

          // Sometimes instead of the letter 'l', the capital letter 'I' is used. Which is a mistake
          // that must be corrected. In such cases we know that the code in question is "Al", which
          // stands for "Almond".
          if(runner.equals("AI")){
            runner = "Al";
          }

          // Here we check the other codes against the items in the list.
          for(int i = 0; i < allAllergies.length; i++){
            if(runner.equalsIgnoreCase(allAllergies[i])){
              result = result+allAllergies[i]+", ";
            }
          }
        }
        runner = "";
      }
    }
    // If result is still empty, then this means that no valid codes were found in the cell, hence
    // an empty String is returned.
    if(result == ""){
      return "";
    }
    // Make sure that the string does not end with ", ".
    result = result.substring(0, result.length()-2);
    return result;
  }









  /*
  * This method reads the cell that contains the ingredients, it will compare the items within that
  * cell against the items in allergyList, which is a 2D array, each array within this array is
  * comprised of exactly two elements the first is the ingredient and the second is the code.
  * The method will add the code to the global variable "result". Once that is done it will call two
  * other methods. One responsible for ensuring that the cell contains one of each recognized codes
  * and the second ensuring that the grouping is done also.
  * There is also another method tcalled "prepIng(String ing)", one that ensures that if it has
  * been declared that only traces of certain ingredients exits, then these should not be added
  * to result.
  */
  public String readIngredients(String line){
    line = prepIng(line);
    int length = line.length();
    String runner = "";
    char c = ' ';
    String holder = "";

    // Start from the end and take each letter, save them in a variable, once the size of the varibale
    // grows to a certain length, we take each ingredient and compare it to the first n letters of
    // the variable (n being the length of the ingredient string). Once we have gone through every
    // ingredient we take yet another letter, and repeat the process.
    backwardsRead : for(int n = length-1; n >= 0; n--){
      c = line.charAt(n);
      holder = ""+c;
      holder = holder + runner;
      runner = holder;

      for(int i = 0; i < allergyList.length; i++){
        if(allergyList[i][0].length() <= runner.length()){
          if(runner.substring(0, allergyList[i][0].length()).equalsIgnoreCase(allergyList[i][0])){

            // As of right now there exists only one exception that I am aware of, Milchsäure is
            // not a dairy product, and it should not be mistaken as such.
            if((runner.substring(0, allergyList[i][0].length()).equalsIgnoreCase("Milch")) && (runner.length() >= 10)){
              if(runner.substring(0, 10).equalsIgnoreCase("Milchsäure")){
                result = result;
              }
              else{
                if(result.equals("") != true){
                  result = result + ", " + allergyList[i][1];
                }
                else{
                  result = allergyList[i][1];
                }
              }
            }
            // All ingredients are read and if one is recognized then the code is added to "result".
            else if(result.equals("") != true){
              result = result + ", " + allergyList[i][1];
            }
            else{
              result = allergyList[i][1];
            }
          }
        }
      }

    }


    grouping(result); // Ensure that the grouping is done correctly
    result = duplicates(result); // Ensure that there exists exactly one of each recognized code.
    return result;
  }




  // Thos method looks for the word "Spuren", any ingredients following this word are ignores, until
  // the end of the sentence.
  public String prepIng(String line){
    int beginning = 0;
    int end = 0;

    if(line.length() > 6){
      intervalBegin : for(int n = 6; n < line.length(); n++){
        if(line.substring(n-6, n).equalsIgnoreCase("Spuren") || line.substring(n-5, n).equalsIgnoreCase("Kann ")){
          beginning = n;

          intervalEnd : for(int i = n+1; i < line.length(); i++){
            if(line.charAt(i) == '.'){
              end = i;
              break intervalEnd;
            }
          }

          break intervalBegin;
        }
      }
    }

    if((beginning != 0) && (end != 0)){
      line = line.substring(0, beginning) + line.substring(end, line.length());
      return line;
    }
    else{
      return line;
    }
  }









  private String duplicates(String line){
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









  private void grouping(String line){
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









  public String getResult(){
    return result;
  }

}
