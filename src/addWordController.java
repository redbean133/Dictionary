import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Collections;
import java.util.ResourceBundle;

public class addWordController implements Initializable {
    @FXML
    private Button saveNewWordButton;
    @FXML
    private TextArea english;
    @FXML
    private TextArea vietnamese;

    @Override
    public void initialize(URL location, ResourceBundle resource) {

    }

    @FXML
    public void saveWord(ActionEvent actionEvent) throws ClassNotFoundException, SQLException {
        String e = this.english.getText().trim().toLowerCase();
        String v = this.vietnamese.getText().trim();
        AlertType alertAlertType;
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        if (e.equals("") || v.equals("")) {
            alert.setContentText("Không được để trống.");
        } else {
            Word find = DictionaryManagement.findWordInWordsArray(e);
            if (find.getWordTarget().equals(e)) {
                alert.setContentText("Từ " + e + " đã có sẵn trong từ điển.");
            } else {
                Word newWord = new Word(e, v);
                //Dictionary.EnglishWords.add(e);
                Dictionary.words.add(newWord);
                Collections.sort(Dictionary.words, new WordComparator());
                english.setText("");
                vietnamese.setText("");
                alert.setContentText("Đã thêm từ " + e + " vào kho dữ liệu thành công!");

                //Them tu vao file tu dien
                Class.forName("org.sqlite.JDBC");
                Connection connection = DriverManager.getConnection("jdbc:sqlite:src/anh_viet.db");
                connection.setAutoCommit(false);
                String sql = "INSERT INTO anh_viet(word,content) VALUES(?, ?)";
                PreparedStatement p = connection.prepareStatement(sql);
                p.setString(1, e);
                p.setString(2, v);
                p.executeUpdate();
                connection.commit();
            }
        }
        alert.showAndWait();
    }
}
