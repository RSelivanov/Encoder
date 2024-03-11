package encoder;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Класс Main главынй класс + графический интерфейс
 */
public class Main extends Application {
    
    private int margin = 5;
    private int indentY = 75;
    private int spaseY = 0 + margin + margin;
    
    private ArrayList<String> lines = new ArrayList<String>();
    
    private Pane layout = new Pane();
    private ScrollPane scrollpane = new ScrollPane(layout);
    private Scene scene = new Scene(scrollpane);
        
    private PasswordField fildKey = new PasswordField();
    
    private TextField fildName = new TextField();
    private TextField fildLogin = new TextField();
    private PasswordField fildPassword = new PasswordField();
    private Button addBtn = new Button("Add");
    
    private Label labelKey = new Label("Key:");
    private Label labelName = new Label("Name");
    private Label labelLogin = new Label("Login");
    private Label labelPassword = new Label("Password");
    
    private CheckBox checkBoxSimplePassword = new CheckBox("Simple Password");
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {

        lines = Data.readToFile();
        
        Collections.sort(lines, String.CASE_INSENSITIVE_ORDER);
        
        render(layout);
        
        addBtn.setOnAction(event -> {
            
            if(!fildName.getText().trim().isEmpty() && !fildLogin.getText().trim().isEmpty() && !fildKey.getText().trim().isEmpty()){
                
                spaseY += 25; 
                layout.getChildren().clear();
                String cryptPass = null; 
                
                try {
                    if(fildPassword.getText().trim().isEmpty()){
                        cryptPass = Encoder.encrypt(Encoder.generatePassword(checkBoxSimplePassword.isSelected()), fildKey.getText());
                    }else{
                        cryptPass = Encoder.encrypt(fildPassword.getText(), fildKey.getText());
                    }
                } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
                
                lines.add(fildName.getText()+"<::>"+fildLogin.getText()+"<::>"+cryptPass);
                fildName.setText("");
                fildLogin.setText("");
                fildPassword.setText("");
                Data.clearFile();
                Data.writeToFile(lines);     
                render(layout);
            }
        });
        
        layout.setPrefSize(510, indentY + spaseY);
        stage.setScene(scene);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(0);
        stage.setY(0);
        //stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        stage.show();
    }

    private void render(Pane layout){
        indentY = 75;
        spaseY = 0 + margin + margin;
        
        labelKey.setTranslateX(0 + margin);
        labelKey.setTranslateY(0 + margin + 3);
        layout.getChildren().add(labelKey);
        
        fildKey.setTranslateX(25 + margin);
        fildKey.setTranslateY(0 + margin);
        layout.getChildren().add(fildKey);
        
        checkBoxSimplePassword.setTranslateX(190 + margin);
        checkBoxSimplePassword.setTranslateY(4 + margin);
        layout.getChildren().add(checkBoxSimplePassword);
                
        labelName.setTranslateY(25 + margin  + 3);
        labelName.setTranslateX(0 + margin);
        layout.getChildren().add(labelName);
            
        labelLogin.setTranslateY(25 + margin  + 3);
        labelLogin.setTranslateX(150 + margin);
        layout.getChildren().add(labelLogin);
            
        labelPassword.setTranslateY(25 + margin  + 3);
        labelPassword.setTranslateX(300 + margin);
        layout.getChildren().add(labelPassword);
            
        fildName.setTranslateX(0 + margin);
        fildName.setTranslateY(50 + margin);
        layout.getChildren().add(fildName);
            
        fildLogin.setTranslateX(150 + margin);
        fildLogin.setTranslateY(50 + margin);
        layout.getChildren().add(fildLogin);
            
        fildPassword.setTranslateX(300 + margin);
        fildPassword.setTranslateY(50 + margin);
        layout.getChildren().add(fildPassword);
            
        addBtn.setTranslateX(450 + margin);
        addBtn.setTranslateY(50 + margin);
        addBtn.setId("add");
        layout.getChildren().add(addBtn);
        
        for(int i = 0; i < lines.size(); i++){
            
            String line = lines.get(i);
            String[] collums = line.split("<::>");
            
            spaseY += 25;
            
            TextField name = new TextField(collums[0]);
            name.setTranslateX(0 + margin);
            name.setTranslateY(indentY + i*25 + margin);
            layout.getChildren().add(name);

            
            TextField login = new TextField(collums[1]);
            login.setTranslateX(150 + margin);
            login.setTranslateY(indentY + i*25 + margin);
            layout.getChildren().add(login);

            
            Button getBtn = new Button("Get Password");
            getBtn.setTranslateX(300 + margin);
            getBtn.setTranslateY(indentY + i*25 + margin);
            getBtn.setPrefWidth(149);
            getBtn.setId(String.valueOf(i));
            layout.getChildren().add(getBtn);
            
            Button btnDel = new Button("Del");
            btnDel.setTranslateX(450 + margin);
            btnDel.setTranslateY(indentY + i*25 + margin);
            btnDel.setPrefWidth(38);
            btnDel.setId(String.valueOf(i));
            layout.getChildren().add(btnDel);
            
            
            getBtn.setOnAction(event -> {
                String string = lines.get(Integer.valueOf(getBtn.getId()));
                String[] arr = string.split("<::>");

                try {
                    //Закидываем в буфер
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection stringselection = new StringSelection(Encoder.decrypt(arr[2], fildKey.getText()));
                    clipboard.setContents(stringselection, null);
                   
                    /* 
                    
                    String cryptPassNew = null; 
                    try 
                    {
                        String decryptedPassword = Encoder.decrypt(arr[2], oldKey);
                        System.out.println("1");
                        cryptPassNew = Encoder.encryptNew(decryptedPassword, newKey);
                        System.out.println("2");

                    } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
                    
                    string = string + "<::>" + cryptPassNew;
                    
                    lines = Data.readToFile();
                    Collections.sort(lines, String.CASE_INSENSITIVE_ORDER);
                    
                    lines.set(Integer.valueOf(getBtn.getId()), string);
                    
                    Data.clearFile();
                    Data.writeToFile(lines);   
                    */
                    
               } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
            });
            
            btnDel.setOnAction(event -> {
                spaseY -= 25;
                lines.remove(Integer.parseInt(btnDel.getId()));
                Data.clearFile();
                Data.writeToFile(lines);
                layout.getChildren().clear();
                render(layout);
            });
            
        }
        layout.setPrefSize(510, indentY + spaseY);

    }
    
}