package at.htl.remotecontrol.gui;

import at.htl.remotecontrol.server.TeacherServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 15.10.2015:  Philipp     ??? Akzeptieren von Students durch Thread implementiert
 * 26.10.2015:  Tobias      ??? Verbesserung des Codes
 * 27.10.2015:  Philipp     ??? Socketproblem und Portproblem gelöst (continue)
 * 31.10.2015:  Tobias      ??? statt continue interrupt()
 * 01.12.2015:  Philipp     ??? Umstrukturierung für bessere Testfreundlichkeit (mocking)
 */
public class Threader implements Runnable {

    private ServerSocket ss = null;
    private boolean isContinue = true;

    public Threader() {
    }

    public void run() {
        try {
            ss = createServerSocket();
            while (isContinue) {
                Socket socket = ss.accept();
                System.out.println("Connection from " + socket);
                createTeacherServer(socket);
            }
        } catch (IOException e) {
            System.out.println("socket closed");
        }
    }

    /**
     * creates a socket which will work as an server.
     *
     * @return  the server-socket.
     */
    public ServerSocket createServerSocket() {
        try {
            return new ServerSocket(TeacherServer.PORT);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * creates a new server for a student which will work as a teacher.
     *
     * @param socket    the socket which will communicate with the students.
     * @return          the success of it.
     */
    public boolean createTeacherServer(Socket socket) {
        try {
            new TeacherServer(socket);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * stops the server and closes it.
     */
    public void stop() {
        isContinue = false;
        if (ss != null) {
            try {
                ss.close();
            } catch (IOException e) {
                System.out.println("can't close ServerSocket");
            }
        }
    }
    
}
