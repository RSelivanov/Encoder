package encoder;

import java.io.*;
import java.util.ArrayList;

public class Data {

    private static final File myFile = new File(System.getProperty("user.home", "") + File.separator + "encoder.txt");

    /**
     * Записывает строки в файл, перезаписывая его содержимое.
     * Если список пустой, файл НЕ трогается.
     */
    public static void writeToFile(ArrayList<String> lines) {
        if (lines == null || lines.isEmpty()) {
            System.out.println("Error file was not update. List is empty.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(myFile, false))) {
            for (String line : lines) {
                if (line != null && !line.trim().isEmpty()) {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Читает строки из файла и возвращает их как список.
     */
    public static ArrayList<String> readToFile() {
        ArrayList<String> lines = new ArrayList<>();
        if (!myFile.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(myFile)))) {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (!strLine.trim().isEmpty()) {
                    lines.add(strLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }
}
