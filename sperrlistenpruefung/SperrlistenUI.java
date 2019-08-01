package sperrlistenpruefung;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class SperrlistenUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Öffne Mail- und Sperrlistentabelle");
        fileChooser.getExtensionFilters().
                add(new FileChooser.
                        ExtensionFilter("XLSX Dateien"
                        , "*.xlsx"));

        File listendatei;
        listendatei = fileChooser.showOpenDialog(primaryStage);

        if (listendatei != null) {
            Sperrlistenpruefer sperrlistenpruefer =
                    new Sperrlistenpruefer(listendatei.getAbsolutePath());

            sperrlistenpruefer.pruefe();

            Stage speicherOrt = new Stage();

            speicherOrt.initModality(Modality.APPLICATION_MODAL);
            speicherOrt.initOwner(primaryStage);
            VBox speicherOrtVbox = new VBox(20);
            speicherOrtVbox.getChildren().add(new Text("MaillisteNeu wurde " +
                    "gespeichert unter: " + sperrlistenpruefer.speicherPfad));
            Scene speicherOrtScene = new Scene(speicherOrtVbox, 800, 20);
            speicherOrt.setScene(speicherOrtScene);
            speicherOrt.show();
        } else {
            System.exit(1);
        }


    }
}
