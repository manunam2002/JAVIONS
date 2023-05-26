package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

/**
 * contient le programme principal
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class Main extends Application {

    private final static long SECOND = Duration.ofSeconds(1).toNanos();
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final String DATABASE = "/aircraft.zip";
    private static final String TILE_CACHE = "tile-cache";
    private static final String SERVER_NAME = "tile.openstreetmap.org";
    private static final String JAVIONS = "Javions";
    private long lastPurge;

    /**
     * appelle launch avec les arguments du programme
     *
     * @param args les arguments du programme
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * démarre l'application en construisant le graphe de scène correspondant à l'interface graphique,
     * démarrant le fil d'exécution chargé d'obtenir les messages, et enfin démarrant le minuteur d'animation
     * chargé de mettre à jour les états d'aéronefs en fonction des messages reçus
     *
     * @param primaryStage la scène principale de cette application
     *
     * @throws RuntimeException en cas d'erreur d'entrée/sortie ou en cas d'interruption du fil d'execution
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL u = getClass().getResource(DATABASE);
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());

        Path tileCache = Path.of(TILE_CACHE);

        TileManager tm = new TileManager(tileCache, SERVER_NAME);

        MapParameters mp = new MapParameters(8, 33_530, 23_070);

        BaseMapController bmc = new BaseMapController(tm, mp);

        AircraftStateManager asm = new AircraftStateManager(db);

        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();

        AircraftController ac = new AircraftController(mp, asm.states(), sap);

        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        atc.setOnDoubleClick(s -> bmc.centerOn(s.getPosition()));

        StatusLineController slc = new StatusLineController();
        slc.aircraftCountProperty().bind(Bindings.size(asm.states()));
        LongProperty mcp = slc.messageCountProperty();

        StackPane stackPane = new StackPane(bmc.pane(), ac.pane());

        BorderPane borderPane = new BorderPane(atc.pane(), slc.pane(), null, null, null);

        SplitPane splitPane = new SplitPane(stackPane, borderPane);
        splitPane.orientationProperty().set(Orientation.VERTICAL);

        primaryStage.setTitle(JAVIONS);
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.show();

        ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();

        Thread thread = (getParameters().getRaw().isEmpty()) ?
                new Thread(() -> {
                    try {
                        AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
                        RawMessage rm;
                        while ((rm = demodulator.nextMessage()) != null) {
                            Message m = MessageParser.parse(rm);
                            if (m != null) {
                                queue.add(m);
                            }
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }) :
                new Thread(() -> {
                    long start = System.nanoTime();
                    String file = getParameters().getRaw().get(0);
                    try {
                        long time;
                        for (Message m : readAllMessages(file)) {
                            time = System.nanoTime();
                            if ((time - start) < m.timeStampNs()) {
                                sleep(Duration.ofNanos(m.timeStampNs() + start - time).toMillis());
                            }
                            queue.add(m);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        thread.setDaemon(true);
        thread.start();

        lastPurge = 0;

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                while (!queue.isEmpty()) {
                    try {
                        asm.updateWithMessage(queue.remove());
                        mcp.set(mcp.get() + 1);
                        if (now - lastPurge >= SECOND){
                            asm.purge();
                            lastPurge = now;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();
    }

    /**
     * lit tous les messages depuis un fichier et les retourne dans une liste
     *
     * @param fileName le nom du fichier
     * @return la liste des messages
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    private static List<Message> readAllMessages(String fileName) throws IOException {
        List<Message> l = new ArrayList<>();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                RawMessage rawMessage = new RawMessage(timeStampNs, message);
                Message m = MessageParser.parse(rawMessage);
                if (m != null) {
                    l.add(m);
                }
            }
        } catch (EOFException e) {
            return l;
        }
    }
}