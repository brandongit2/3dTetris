//// Brandon Tsang & gary ye
//// Ms. Strelkovska
//// ICS3U1-07
//// Culminating project
//
//package game;
//
//import org.json.JSONObject;
//
//import java.io.*;
//import java.util.Scanner;
//
//public class Score {
//    private static int         score = 0;
//    private static JSONObject  scores;
//    private static InputStream scoreFile;
//
//    static {
//        String jsonString = "";
//        scoreFile = Score.class.getClassLoader().getResourceAsStream("/scores.json");
//
//        Scanner       in            = new Scanner(scoreFile);
//        StringBuilder stringBuilder = new StringBuilder();
//        while (in.hasNextLine()) {
//            stringBuilder.append(in.nextLine() + "\n");
//        }
//        jsonString = stringBuilder.toString();
//        scores = new JSONObject(jsonString);
//    }
//
//    static void addScore(int newScore) {
//        score += newScore;
//    }
//
//    static int getScore() {
//        return score;
//    }
//
//    static void saveScore(String name) {
//        scores.put(name, score);
//
//        try {
//            FileWriter fileWriter = new FileWriter(scoreFile);
//            fileWriter.write(scores.toString());
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    static int readScore(String name) {
//        return (int) scores.get(name);
//    }
//}
