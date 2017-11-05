package encoder;

import java.io.*;
import java.util.ArrayList;

/**
 * Класс Data работает с данными файлов
 */
public class Data {
    
    /**
     * Записывает данные в файл
     * @param ArrayList lines <p>Массив строк</p>
     * @return void <p>Ничего не возвращает</p>
     */
    public static void writeToFile(ArrayList lines){
        
        File myFile = new File(System.getProperty("user.home", "") + "\\" + "encoder.txt");
        try{
            PrintWriter writer = new PrintWriter(new FileWriter(myFile, true));
            for(int i = 0; i < lines.size(); i++){
                writer.println(lines.get(i));
            }
            writer.close();
        }catch(IOException e){
           e.printStackTrace();
        }
    }
    
    /**
     * Читает данные из файла
     * @return ArrayList <p>Массив прочетаных строк</p>
     */
     public static ArrayList readToFile() {
         
        ArrayList<String> lines = new ArrayList<String>();
        
        try{
            FileInputStream fstream = new FileInputStream(System.getProperty("user.home", "") + "\\" + "encoder.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null){
                lines.add(strLine);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        
        return lines;    
    } 
     
    /**
     * Очищает файл
     * @return void <p>Ничего не возвращает</p>
     */
    public static void clearFile() {
        
        File myFile = new File(System.getProperty("user.home", "") + "\\" + "encoder.txt");
         try{
            FileWriter fwOb = new FileWriter(myFile, false); 
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
}
