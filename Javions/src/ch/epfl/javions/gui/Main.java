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
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

public final class Main extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL u = getClass().getResource("/aircraft.zip");
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());

        Path tileCache = Path.of("tile-cache");

        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");

        MapParameters mp = new MapParameters(8,33_530,23_070);

        BaseMapController bmc = new BaseMapController(tm, mp);

        AircraftStateManager asm = new AircraftStateManager(db);

        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();

        AircraftController ac = new AircraftController(mp, asm.states(), sap);

        AircraftTableController atc = new AircraftTableController(asm.states(), sap);

        StatusLineController slc = new StatusLineController();
        slc.aircraftCountProperty().bind(Bindings.size(asm.states()));
        LongProperty mcp = slc.messageCountProperty();

        StackPane stackPane = new StackPane(bmc.pane(), ac.pane());

        BorderPane borderPane = new BorderPane();
        borderPane.centerProperty().set(atc.pane());
        borderPane.topProperty().set(slc.pane());

        SplitPane splitPane = new SplitPane(stackPane, borderPane);

        primaryStage.setTitle("Javions");
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        Thread thread;

        ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();

        if (getParameters().getRaw().isEmpty()){
            AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
            thread = new Thread(() -> {
                try {
                    Message m = MessageParser.parse(demodulator.nextMessage());
                    if (m != null){
                        queue.add(m);
                        mcp.set(mcp.get() + 1);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            String file = getParameters().getRaw().get(0);
            URL url = getClass().getResource(file);
            assert url != null;
            Path path = Path.of(u.toURI());

            thread = new Thread(() -> {
                try (DataInputStream s = new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(path.toString())))) {
                    byte[] bytes = new byte[RawMessage.LENGTH];
                    while (true) {
                        long timeStampNs = s.readLong();
                        int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                        assert bytesRead == RawMessage.LENGTH;
                        ByteString message = new ByteString(bytes);
                        RawMessage rawMessage = new RawMessage(timeStampNs, message);
                        Message m = MessageParser.parse(rawMessage);
                        if (m != null){
                            if (System.nanoTime() < m.timeStampNs()){
                                sleep(m.timeStampNs() - System.nanoTime());
                            }
                            queue.add(m);
                            mcp.set(mcp.get() + 1);
                        }
                    }
                } catch (IOException e){
                    return;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        thread.start();


        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (queue.isEmpty()) return;
                try {
                    asm.updateWithMessage(queue.remove());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}