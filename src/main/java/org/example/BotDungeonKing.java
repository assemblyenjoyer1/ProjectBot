package org.example;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BotDungeonKing {
    private static Robot robot;
    private final static int startGameColor = -13684423;
    private final static int chooseDifficultyColor = -11581584;
    private final static int startRunColor = -9540470;
    private final static int alternativeStartRunColor = -9540470;
    private final static int startAutoBattleColor = -15939281;
    private final static int finishRunColor = -1327543;
    private final static int startGameX = 1818;
    private final static int startGameY = 967;
    private final static int startGameAvailableX = 1783;
    private final static int startGameAvailableY = 1007;
    private final static int chooseDifficultyX = 1460;
    private final static int chooseDifficultyY = 263;
    private final static int startRunX = 1584;
    private final static int startRunY = 189;
    private final static int finishRunX = 1546;
    private final static int finishRunY = 708;
    private final static int autoBattleX = 1847;
    private final static int autoBattleY = 820;

    public enum BottingState {
        IDLE, OPEN_LOBBY_MENU, CHOOSE_DIFFICULTY, START_RUN, AUTO_BATTLING, UNKNOWN
    }

    private static String wmic() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("WMIC", "path", "win32_process", "get", "Caption,Processid,Commandline");
        builder.redirectErrorStream(true);
        Process process = builder.start();
        try (InputStream stream = process.getInputStream()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = stream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        }
    }

    private static void click(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static void printCoordinates() {
        try {
            if (robot == null) robot = new Robot();
            while (true) {
                Point point = MouseInfo.getPointerInfo().getLocation();
                System.out.println("X: " + point.x + ", Y: " + point.y);
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                Rectangle rectangle = new Rectangle(0, 0, dimension.width, dimension.height);
                BufferedImage image = robot.createScreenCapture(rectangle);
                System.out.println("RGB:" + image.getRGB(point.x, point.y));
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BottingState checkState(int x, int y, int color, BottingState desiredState) {
        try {
            if (robot == null) robot = new Robot();
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle rectangle = new Rectangle(0, 0, dimension.width, dimension.height);
            BufferedImage image = robot.createScreenCapture(rectangle);
            if (image.getRGB(x, y) == color) {
                return desiredState;
            } else {
                return BottingState.UNKNOWN;
            }
        } catch (Exception e) {
            return BottingState.UNKNOWN;
        }
    }

    private static void snooze(){
        try {
            Thread.sleep(1000); // Sleep for 1 second (1000 milliseconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void hardSnooze(){
        try {
            Thread.sleep(2400000); // Sleep for 1 second (1000 milliseconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private int attempts = 10;

    public static void main(String[] args) {
        boolean yes = true;
        while (yes) {
            BottingState state;

            printCoordinates();
            System.out.println("Trying to start up Lobby Menu");
            do {
                state = checkState(startGameAvailableX, startGameAvailableY, startGameColor, BottingState.OPEN_LOBBY_MENU);
            } while (state != BottingState.OPEN_LOBBY_MENU);
            click(startGameX, startGameY);
            System.out.println("Lobby Menu opened");
            snooze();
            System.out.println("Trying to select difficulty");
            do {
                state = checkState(chooseDifficultyX, chooseDifficultyY, chooseDifficultyColor, BottingState.CHOOSE_DIFFICULTY);
            } while (state != BottingState.CHOOSE_DIFFICULTY);
            click(chooseDifficultyX, chooseDifficultyY);
            System.out.println("Selected Difficulty");
            snooze();
            System.out.println("Trying to start new Run in Difficulty");
            do {
                state = checkState(startRunX, startRunY, startRunColor, BottingState.START_RUN);
            } while (state != BottingState.START_RUN);
            click(startRunX, startRunY);
            System.out.println("Started Run");
            snooze();
            System.out.println("Trying to enable auto battle");
            do {
                state = checkState(autoBattleX, autoBattleY, startAutoBattleColor, BottingState.AUTO_BATTLING);
            } while (state != BottingState.AUTO_BATTLING);
            click(autoBattleX, autoBattleY);
            System.out.println("Started auto battle");
            snooze();
            System.out.println("Waiting to finish run...");
            hardSnooze();

            do {
                state = checkState(finishRunX, finishRunY, finishRunColor, BottingState.IDLE);
                snooze();
            } while (state != BottingState.IDLE);
            click(finishRunX, finishRunY);
            System.out.println("Finished Run! Repeating");
        }
    }
}
