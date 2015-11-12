package at.htl.remotecontrol.gui.controller;

import at.htl.remotecontrol.entity.Interval;
import at.htl.remotecontrol.entity.Session;
import at.htl.remotecontrol.entity.Student;
import at.htl.remotecontrol.entity.StudentView;
import at.htl.remotecontrol.gui.Threader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 15.10.2015:  Philipp     Zeiteingabe für die Screenshot-Verzögerung durch Gui ermöglicht
 * 19.10.2015:  Patrick     Liste der verbundenen Studenten
 * 24.10.2015:  Patrick     DirectoryChooser für die Screenshots
 * 26.10.2015:  Philipp     Methode für Meldungen, starten und stoppen des Servers und Zeitauswahl(+random)
 * 05.11.2015:  Patrick     Auswahl der Angaben-Datei. Die File wird in Session gespeichert
 */
public class ControllerTeacher implements Initializable {

    @FXML
    public TextField tfTimeSS, tfPassword; // SS ... Screenshot

    @FXML
    public ListView<Student> lvStudents;

    @FXML
    public Label lbAlert;

    @FXML
    public ToggleButton TB_SS_rnd;  //TB...ToggleButton   SS...Screenshot  rnd...Random

    @FXML
    public ImageView ivLiveView;

    private Thread server;
    private Threader threader;

    public ControllerTeacher() {
        //Session.getInstance().setTime(3000);
    }

    public void initialize(URL location, ResourceBundle resources) {
        lvStudents.setItems(Session.getInstance().getObservableList());
        StudentView.getInstance().setIv(ivLiveView);
        StudentView.getInstance().setLv(lvStudents);

        // Text in Textfeld mittig setzen
        lbAlert.setTextAlignment(TextAlignment.CENTER);
        lbAlert.setAlignment(Pos.CENTER);
    }

    public void startServer(ActionEvent actionEvent) {
        String path = Session.getInstance().getPathOfImages();
        Session.getInstance().setHandOutFile(new File(String.format("%s/angabe.zip",
                Session.getInstance().getPath())));
        String ssTime = tfTimeSS.getText();
        boolean isRnd = TB_SS_rnd.isSelected();

        if (path != null && (isRnd || (!isRnd && ssTime.length() > 0))) {
            if (isRnd) {
                Session.getInstance().setInterval(new Interval(1000, 30000)); //  wert kleiner, gleich 0 bedeutet Random
            } else {
                Session.getInstance().setInterval(new Interval(Integer.parseInt(tfTimeSS.getText())));
            }

            threader = new Threader();
            server = new Thread(threader);
            server.start();
            setMsg(false, "Server lauft");
        } else if (path == null) {
            setMsg(true, "Bitte gib ein Verzeichnis an");
        } else if (!isRnd && ssTime.length() < 1) {
            setMsg(true, "Bitte gib einen Zeitwert(in ms) an");
        }
    }

    public void stopServer(ActionEvent actionEvent) {
        if (server != null) {
            if (!server.isInterrupted()) {
                threader.stop();
                server.interrupt();
                setMsg(false, "Server gestoppt");
            } else {
                setMsg(true, "Server wurde bereits gestoppt");
            }
        } else {
            setMsg(true, "Server wurde noch nie gestartet");
        }
    }

    private void setMsg(boolean error, String msg) {
        String color = (error ? "red" : "limegreen");   //bei Fehlermeldung rot, sonst grün
        lbAlert.setText(msg);
        lbAlert.setStyle("-fx-background-color: " + color);
    }

    public void chooseDirectory(ActionEvent actionEvent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        dc.setTitle("Wähle dein Ziel-Verzeichnis");
        File choosedFile = dc.showDialog(new Stage());
        if (choosedFile != null)
            Session.getInstance().setPath(choosedFile.getPath());
    }

    public void changeSomeOptions(ActionEvent actionEvent) {
        if (TB_SS_rnd.isSelected()) {
            tfTimeSS.setDisable(true);
            tfTimeSS.setEditable(false);
            TB_SS_rnd.setText("EIN");
        } else {
            tfTimeSS.setDisable(false);
            tfTimeSS.setEditable(true);
            TB_SS_rnd.setText("AUS");
        }
    }

    public void chooseHandoutFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.setTitle("Wähle dein Ziel-Verzeichnis");
        File choosedFile = fc.showOpenDialog(new Stage());
        if (choosedFile != null)
            Session.getInstance().setHandOutFile(new File(choosedFile.getPath()));

    }


}
