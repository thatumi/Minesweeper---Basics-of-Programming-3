package mins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Modifies and writes the results table to a text file
 */
public class ScoreTable {
    /** This variable stores the results table in HTML format */
    String text = "<html><body>";

    public ScoreTable() {
        text = "<html><body>";
    }

    /**
     * Modifies the results table (which is a text file) based on the new data
     * @param newNickname The nickname provided by the user
     * @param newScore The score (remaining time)
     * @return The updated name array
     */
    public String[] updateScoreTable(String newNickname, int newScore) {
        Scanner scanner;
        String currentNickname;
        int currentScore;

        String[] nameArray = new String[10];
        int[] scoreArray = new int[10];
        int row = 0;

        try {
            scanner = new Scanner(new FileReader("src/resources/scoretable.txt"));
            while (scanner.hasNextLine() && row < 10) {
                System.out.println(row);
                currentNickname = scanner.next();
                currentScore = scanner.nextInt();
                if (currentScore <= newScore) {
                    nameArray[row] = newNickname;
                    scoreArray[row] = newScore;
                    newNickname = currentNickname;
                    newScore = currentScore;
                } else {
                    nameArray[row] = currentNickname;
                    scoreArray[row] = currentScore;
                }
                row++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File("src/resources/scoretable.txt");
        file.delete();

        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (int i = 0; i < 10; i++) {
                bufferedWriter.write(nameArray[i]);
                bufferedWriter.write("\t");
                bufferedWriter.write(String.valueOf(scoreArray[i]));
                bufferedWriter.write("\n");
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nameArray;
    }

    /** Reads the scoretable.txt file into the text variable in HTML format */
    public String readScoreTable() {
        int row = 1;

        try {
            Scanner scanner = new Scanner(new File("src/resources/scoretable.txt"));

            while (scanner.hasNextLine()) {
                if (row == 10) {
                    text += "</body></html>";
                    scanner.close();
                    return text;
                } else {
                    text += scanner.nextLine() + "<br>";
                }
                row++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return text;
    }
}
