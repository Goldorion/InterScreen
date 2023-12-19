package net.goldorion.interscreen.network;

import net.goldorion.interscreen.network.actions.IAction;
import net.goldorion.interscreen.network.actions.KeyBoardAction;
import net.goldorion.interscreen.network.actions.MouseAction;
import net.goldorion.interscreen.ui.InterScreenApp;
import net.goldorion.interscreen.network.actions.OwnerAction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client {
    private Socket socket;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    private final boolean isOwner;
    private Robot robot;
    private static final Rectangle SCREEN_SIZE = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

    private final InterScreenApp app;

    public Client(InterScreenApp app, int port, String ipAddress, boolean isOwner) {
        this.app = app;
        this.isOwner = isOwner;

        try {
            this.robot = new Robot();
            this.socket = new Socket(ipAddress, port);
            writer = new ObjectOutputStream(socket.getOutputStream());
            reader = new ObjectInputStream(socket.getInputStream());

            listenToServer();
            if (isOwner)
                ownerToServer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            System.out.println("An error occurred when creating the robot. " + e.getMessage());
        }

    }

    private void listenToServer() {
        new Thread(() -> {
            while (isConnected()) {
                try {
                    Object object = reader.readObject();
                    if (object instanceof OwnerAction action) {
                        ByteArrayInputStream baos = new ByteArrayInputStream(action.currentImage());
                        app.receiveNewImage(ImageIO.read(baos));
                        baos.close();
                    } else if (object instanceof MouseAction action) {
                        robot.mouseMove(action.position().x, action.position().y);
                        if (action.isPressed()) {
                            robot.mousePress(action.mouseButtons());
                            robot.keyPress(action.modifiers());
                        }
                        if (action.isReleased()) {
                            robot.mouseRelease(action.mouseButtons());
                            robot.keyRelease(action.modifiers());
                        }
                        robot.mouseWheel(action.wheelAmt());
                    } else if (object instanceof KeyBoardAction action) {
                        if (action.isPressed())
                            robot.keyPress(action.keycode());
                        if (action.isReleased())
                            robot.keyRelease(action.keycode());
                    }
                } catch (SocketException ignored) {
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, "S2C thread").start();
    }

    private void ownerToServer() {
        new Thread(() -> {
            while (isConnected() && writer != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(robot.createScreenCapture(SCREEN_SIZE), "png", baos);
                    sendActionToServer(new OwnerAction(baos.toByteArray()));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }, "C2S thread").start();
    }

    public void sendActionToServer(IAction action) {
        if (socket.isConnected() && !socket.isClosed() && writer != null) {
            try {
                writer.writeObject(action);
                writer.flush();
            } catch (IOException e) {
                System.out.println("An error occurred when sending an action to the server. " + e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        return !socket.isClosed();
    }

    public void disconnect() {
        try {
            socket.close();
            if (!isOwner) {
                app.receiveNewImage(null);
            }
        } catch (IOException e) {
            System.out.println("Could not close correctly the client. " + e.getMessage());
        }
    }

    public boolean isOwner() {
        return isOwner;
    }
}
