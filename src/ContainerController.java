import com.sun.speech.freetts.VoiceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContainerController implements Initializable {
    @FXML
    private TextField searchTextField;
    @FXML
    private Label englishLabel;
    @FXML
    private Button deleteWordButton;
    @FXML
    private Button addWordButton;
    @FXML
    private Button playAudio;
    @FXML
    private Label pronunciationLabel;
    @FXML
    private ListView<String> suggestList;
    @FXML
    private StackPane editStack;
    @FXML
    private WebView webView;
    @FXML
    private TextArea editTextArea;
    @FXML
    private StackPane buttonStack;
    @FXML
    private Button saveButton;
    @FXML
    private Button editWordButton;

    @Override
    public void initialize(URL location, ResourceBundle resource) {

    }

    @FXML
    private void searchWord(KeyEvent event) {
        WebEngine webEngine = webView.getEngine();
        if (event.getCode().equals(KeyCode.ENTER)) {
            //An bang goi y
            suggestList.getItems().clear();
            suggestList.setDisable(true);
            webView.toFront();
            editWordButton.toFront();

            String english = searchTextField.getText().trim().toLowerCase();
            Word findWord = DictionaryManagement.findWordInWordsArray(english);
            if (findWord.getWordTarget().equals(english)) {
                englishLabel.setText(findWord.getWordTarget());
                String text = findWord.getWordExplain();
                webEngine.loadContent(text,"text/html");
                //Hien thi pronunciation
                String textCopy = text;
                String pronunciation = "";
                int begin = textCopy.indexOf("/");
                if (begin > -1) {
                    textCopy = text.substring(begin + 1);
                    int end = textCopy.indexOf("/");
                    if (end > -1) {
                        pronunciation += "/" + textCopy.substring(0, end + 1);
                        if (!pronunciation.equals("/span><br /")) {
                            pronunciationLabel.setText(pronunciation);
                        } else {
                            pronunciationLabel.setText("");
                        }
                    }
                }
            } else {
                englishLabel.setText("Not found");
                webEngine.loadContent("");
                pronunciationLabel.setText("");
            }
        }

        //hien thi goi y
        searchTextField.textProperty().addListener((obj, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                ArrayList<String> guess;
                guess = DictionaryManagement.findGuessList(newValue.trim().toLowerCase());
                if (guess.size() > 0) {
                    ObservableList<String> items = FXCollections.observableArrayList(guess);
                    suggestList.setItems(items);
                    suggestList.setDisable(false);
                    suggestList.getFocusModel().focus(0);
                    suggestList.getSelectionModel().select(0);
                } else {
                    suggestList.getItems().clear();
                    suggestList.setDisable(true);
                }
            } else {
                suggestList.getItems().clear();
                suggestList.setDisable(true);
            }
        });
    }

    @FXML
    private void chooseGuess(MouseEvent click) {
        String word = suggestList.getSelectionModel().getSelectedItem();
        searchTextField.setText(word);
        suggestList.getItems().clear();
        suggestList.setDisable(true);
    }

    @FXML
    private void deleteWord(ActionEvent event) throws ClassNotFoundException, SQLException {
        WebEngine webEngine = webView.getEngine();
        String deleteWord = englishLabel.getText();
        if (!deleteWord.equals("") && !deleteWord.equals("Not found")) {
            Alert.AlertType alertAlertType;
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirm delete word");
            alert.setHeaderText("Look, a confirmation dialog!");
            alert.setContentText("Bạn chắc chắn muốn xóa từ " + deleteWord + " ra khỏi từ điển?");
            Optional<ButtonType> choice = alert.showAndWait();
            if (choice.get() == ButtonType.OK) {
                DictionaryManagement.deleteWord(deleteWord);
                searchTextField.setText("");
                englishLabel.setText("");
                pronunciationLabel.setText("");
                webEngine.loadContent("Từ " + deleteWord + " đã được xóa khỏi từ điển.");
            }
        }
    }

    @FXML
    private void openEditWord(ActionEvent event) throws IOException {
        if (!englishLabel.getText().isEmpty()) {
            editTextArea.toFront();
            saveButton.toFront();
        }
    }

    @FXML
    private void saveEdit(ActionEvent event) throws ClassNotFoundException, SQLException {
        String editWord = englishLabel.getText();
        String newExplain = editTextArea.getText();
        Word word = DictionaryManagement.findWordInWordsArray(editWord);
        if (!newExplain.trim().isEmpty()) {
            Alert.AlertType alertAlertType;
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirm edit word");
            alert.setHeaderText("Look, a confirmation dialog!");
            alert.setContentText("Hãy chắc chắn bạn muốn thay đổi nghĩa của từ " + editWord + ".");
            Optional<ButtonType> choice = alert.showAndWait();
            if (choice.get() == ButtonType.OK) {
                word.setWordExplain(newExplain);
                Class.forName("org.sqlite.JDBC");
                Connection connection = DriverManager.getConnection("jdbc:sqlite:src/anh_viet.db");
                connection.setAutoCommit(false);
                String sql = "UPDATE anh_viet SET content = ? WHERE word = ?";
                PreparedStatement p = connection.prepareStatement(sql);
                p.setString(1, newExplain);
                p.setString(2, editWord);
                p.executeUpdate();
                connection.commit();

                WebEngine webEngine = webView.getEngine();
                webEngine.loadContent(newExplain, "text/html");
                webView.toFront();
                editWordButton.toFront();
            } else {
                webView.toFront();
                editWordButton.toFront();
            }
        }
    }

    @FXML
    private void openAddWordWindow(ActionEvent event) throws IOException {
        Stage window = new Stage();
        window.setTitle("Add new word");
        Parent root = FXMLLoader.load(getClass().getResource("addWordWindow.fxml"));
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.initModality(Modality.WINDOW_MODAL);
        window.initOwner(DictionaryApplication.getPriStage());
        window.show();
    }

    @FXML
    private void playAudioWord(ActionEvent event)  {
        com.sun.speech.freetts.Voice voice;
        voice=VoiceManager.getInstance().getVoice("kevin16");
        voice.allocate();
        voice.speak(englishLabel.getText());
    }
}
