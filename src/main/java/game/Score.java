package game;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Score {
    private int score;
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score += score;
    }
//    public static String readFile(String filename){
//        String result = "";
//        try{
//            BufferedReader br = new BufferedReader(new FileReader(filename));
//            StringBuilder sb = new StringBuilder();
//            String line = br.readLine();
//            while(line!=null){
//                sb.append(line);
//                line = br.readLine();
//            }
//            result = sb.toString();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return result;
//    }
//    public void saveScore(){
//        String jsonData = readFile("scores.json");
//        JSONObject highscore= new JSONObject(jsonData);
//        String name = highscore.getString("title");
//        String pScore = highscore.getString("name");
//        System.out.println(name +" "+ pScore);
//    }
}