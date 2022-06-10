import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DictionaryApplication extends Application {
    static Stage priStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        DictionaryManagement.insertFromFile();

        //DictionaryManagement.deleteSameWordInDictionary();

        priStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("container.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dictionary");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPriStage() {
        return priStage;
    }
}
