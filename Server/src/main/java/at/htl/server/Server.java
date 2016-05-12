package at.htl.server;

import at.htl.server.entity.Student;
import at.htl.common.fx.StudentView;
import at.htl.common.io.FileUtils;
import at.htl.common.io.ScreenShot;
import at.htl.common.trasfer.LoginPackage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.logging.log4j.Level;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 * Die Hauptklasse ist der TeacherServer. Wenn ein Schüler sich mit ihm verbindet,
 * schickt er ein LoginPacket. Sobald dieses Packet verarbeitet wurde, wird der
 * SocketReaderThread und der SocketWriterThread erzeugt, mit denen dann die
 * Netzwerkkommunikation ermöglicht ist.
 *
 * @timeline .
 * 21.10.2015: PHI 020  Einfügen der "saveImage()"-Methode zum Speichern der Screenshots
 * 26.10.2015: MET 010  Verbesserung der Methode saveImage()
 * 27.10.2015: PHI 080  Live ÜberwachungsBild wird gesetzt
 * 28.10.2015: PHI 015  Live ÜberwachungsBild wird NUR für den ausgewählten Benutzer gesetzt
 * 29.11.2015: PHI 060  Umänderung auf TextField-liste für die farbige Studentenausgabe
 * 12.12.2015: PHI 010  Kommentieren von Methoden
 * 22.12.2015: PHI 001  Ändern von "Hinzufügen" von Schülern zu "Einloggen" von Schülern.
 * 06.01.2016: PHI 025  Fehler gefunden und geändert bei der Anmeldung eines Schülers der schon gespeichert ist.
 */
public class Server {

    public static int PORT = 50555;

    private final SocketWriterThread writer;
    private final SocketReaderThread reader;

    public Socket socket;

    public Server(Socket socket) throws IOException, ClassNotFoundException {

        this.socket = socket;
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(
                        socket.getInputStream()));
        FileUtils.log(this, Level.INFO, "waiting for client name ...");

        LoginPackage packet = (LoginPackage) in.readObject();

        Student student;
        if (Settings.getInstance().findStudentByName(packet.getLastname()) != null) {
            student = Settings.getInstance().findStudentByName(packet.getLastname());
            if (student.getPathOfWatch() == null) {
                student.setPathOfWatch(packet.getDirOfWatch());
            }
        } else {
            student = new Student(packet.getLastname());
            student.setPathOfWatch(packet.getDirOfWatch());
            student.setPathOfImages(Settings.getInstance().getPathOfImages());
            student.setCatalogNumber(packet.getCatalogNr());
            student.setEnrolmentID(packet.getEnrolmentID());
            student.setFirstName(packet.getFirstname());
            Settings.getInstance().addStudent(student);
        }
        FileUtils.log(this, Level.INFO, "I got the Package: " + packet.getDirOfWatch());
        Settings.getInstance().loginStudent(student);

        reader = new SocketReaderThread(student, in, this);
        writer = new SocketWriterThread(student, out);

        reader.setDaemon(true);
        writer.setDaemon(true);

        writer.handOut();

        reader.start();
        writer.start();

        student.setServer(this);
        student.setFilter(Settings.getInstance().getEndings());
        student.setInterval(Settings.getInstance().getIntervalObject());

        FileUtils.log(this, Level.INFO, "finished connecting to " + socket);
        Settings.getInstance().printErrorLine(Level.INFO, student.getName() + " logged in!", true, "CONNECT");
    }

    public static int getPORT() {
        return PORT;
    }

    public static void setPORT(int PORT) {
        Server.PORT = PORT;
    }

    /**
     * It redirects to save and show the screenshot.
     *
     * @param image   Specifies the image which should be saved.
     * @param student Specifies the client from which the screenshot is.
     */
    public void saveImage(byte[] image, Student student) {
        String path = String.format("%s/%s-%s.jpg",
                Settings.getInstance().getPathOfImages() + "/" + student.getName(),
                student.getName(),
                LocalDateTime.now());

        ScreenShot.save(image, path);

        showImage(path, student);
    }

    /**
     * It shows the Image on the Teacher-GUI.
     *
     * @param fileName Specifies the path of the file (screenshot).
     * @param student  Specifies the client from which the screenshot is.
     */
    public void showImage(final String fileName, final Student student) {
        Platform.runLater(() -> {
            Button selected = (Button) StudentView.getInstance().getLv()
                    .getSelectionModel().getSelectedItem();
            if (selected != null && !Settings.getInstance().isLooksAtScreenshots()) {
                //ist der Screenshot vom ausgewählten Studenten?
                if (student.getName().equals(selected.getText())) {
                    (StudentView.getInstance().getIv())
                            .setImage(new javafx.scene.image.Image("file:" + fileName));
                    Settings.getInstance().addScreenshot("file:" + fileName);
                    Settings.getInstance().setActualScreenshot("file" + fileName);
                }
            }
        });
    }

    /**
     * Close the Socket-Reader and the Socket-Writer.
     */
    public void shutdown() {
        writer.interrupt();
        reader.close();
    }

}