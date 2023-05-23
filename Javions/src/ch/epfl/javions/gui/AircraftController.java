package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.*;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * gère la vue des aéronefs
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class AircraftController {

    private final MapParameters mapParameters;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;
    private final Pane pane;

    /**
     * constructeur public
     *
     * @param mapParameters    les paramètres de la portion de la carte visible à l'écran
     * @param states           l'ensemble des états des aéronefs qui doivent apparaître sur la vue
     * @param selectedAircraft une propriété JavaFX contenant l'état de l'aéronef sélectionné
     */
    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> states,
                              ObjectProperty<ObservableAircraftState> selectedAircraft) {
        this.mapParameters = mapParameters;
        this.selectedAircraft = selectedAircraft;

        pane = new Pane();
        pane.setPickOnBounds(false);
        pane.getStylesheets().add("aircraft.css");

        states.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if (change.wasAdded()) {
                        addGroup(change.getElementAdded());
                    }
                    if (change.wasRemoved()) {
                        String id = change.getElementRemoved().getIcaoAddress().string();
                        pane.getChildren().removeIf(group -> group.getId().equals(id));
                    }
                });

        for (ObservableAircraftState aircraftState : states) {
            addGroup(aircraftState);
        }
    }

    /**
     * retourne le panneau JavaFX sur lequel les aéronefs sont affichés
     *
     * @return le panneau JavaFX sur lequel les aéronefs sont affichés
     */
    public Pane pane() {
        return pane;
    }

    /**
     * ajoute le groupe de l'aéronef au panneau
     *
     * @param aircraftState l'état de l'aéronef
     */
    private void addGroup(ObservableAircraftState aircraftState) {
        Group aircraftGroup = new Group();
        pane.getChildren().add(aircraftGroup);
        aircraftGroup.setId(aircraftState.getIcaoAddress().string());
        aircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());

        addTrajectory(aircraftState, aircraftGroup);

        addIconAndLabel(aircraftState, aircraftGroup);
    }

    /**
     * ajoute le groupe de la trajectoire au groupe de l'aéronef
     *
     * @param aircraftState l'état de l'aéronef
     * @param aircraftGroup le groupe de l'aéronef
     */
    private void addTrajectory(ObservableAircraftState aircraftState, Group aircraftGroup) {
        Group trajectory = new Group();
        trajectory.getStyleClass().add("trajectory");
        aircraftGroup.getChildren().add(trajectory);

        trajectory.layoutXProperty().bind(mapParameters.minXProperty().negate());
        trajectory.layoutYProperty().bind(mapParameters.minYProperty().negate());

        trajectory.visibleProperty().bind(Bindings.equal(aircraftState, selectedAircraft));
        trajectory.visibleProperty().addListener((p, o, n) -> {
            if (n) createTrajectory(aircraftState, trajectory);
            if (!n) trajectory.getChildren().clear();
        });

        aircraftState.getTrajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>) change -> {
            if (aircraftState.equals(selectedAircraft.get()))
                createTrajectory(aircraftState, trajectory);
        });

        mapParameters.zoomProperty().addListener((p, o, n) -> {
            if (aircraftState.equals(selectedAircraft.get()))
                createTrajectory(aircraftState, trajectory);
        });
    }

    /**
     * crée toutes les lignes de la trajectoire
     *
     * @param aircraftState l'état de l'aéronef
     * @param trajectory    le groupe de la trajectoire
     */
    private void createTrajectory(ObservableAircraftState aircraftState, Group trajectory) {
        ArrayList<Line> trajectoryLines = new ArrayList<>();
        List<ObservableAircraftState.AirbornePos> t = aircraftState.getTrajectory();
        double startX = WebMercator.x(mapParameters.zoom(), t.get(0).position().longitude());
        double startY = WebMercator.y(mapParameters.zoom(), t.get(0).position().latitude());
        double startAlt = t.get(0).altitude();
        for (int i = 1; i < t.size(); i++) {
            double endX = WebMercator.x(mapParameters.zoom(), t.get(i).position().longitude());
            double endY = WebMercator.y(mapParameters.zoom(), t.get(i).position().latitude());
            double endAlt = t.get(i).altitude();

            Line line = new Line(startX, startY, endX, endY);

            Stop s1 = new Stop(0, plasmaAt(startAlt));
            Stop s2 = new Stop(1, plasmaAt(endAlt));
            line.setStroke(
                        new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, s1, s2));

            trajectoryLines.add(line);

            startX = endX;
            startY = endY;
            startAlt = endAlt;
        }

        trajectory.getChildren().setAll(trajectoryLines);
    }

    /**
     * ajoute le groupe de l'icone et de l'étiquette au groupe de l'aéronef
     *
     * @param aircraftState l'état de l'aéronef
     * @param aircraftGroup le groupe de l'aéronef
     */
    private void addIconAndLabel(ObservableAircraftState aircraftState, Group aircraftGroup) {
        Group iconAndLabelGroup = new Group();
        aircraftGroup.getChildren().add(iconAndLabelGroup);

        iconAndLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.x(mapParameters.zoom(),
                                aircraftState.getPosition().longitude()) - mapParameters.minX(),
                mapParameters.zoomProperty(),
                aircraftState.positionProperty(),
                mapParameters.minXProperty()));
        iconAndLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.y(mapParameters.zoom(),
                                aircraftState.getPosition().latitude()) - mapParameters.minY(),
                mapParameters.zoomProperty(),
                aircraftState.positionProperty(),
                mapParameters.minYProperty()));

        addLabel(aircraftState, iconAndLabelGroup);
        addIcon(aircraftState, iconAndLabelGroup);
    }

    /**
     * crée le groupe de l'étiquette et l'ajoute au groupe de l'icone et de l'étiquette
     *
     * @param aircraftState     l'état de l'aéronef
     * @param iconAndLabelGroup le groupe de l'icone et de l'étiquette
     */
    private void addLabel(ObservableAircraftState aircraftState, Group iconAndLabelGroup) {
        Group label = new Group();
        label.getStyleClass().add("label");
        iconAndLabelGroup.getChildren().add(label);

        label.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                        mapParameters.zoom() >= 11 || (aircraftState.equals(selectedAircraft.get())),
                mapParameters.zoomProperty(), selectedAircraft));

        Object line0 = (Objects.nonNull(aircraftState.getAircraftData()) &&
                !aircraftState.getAircraftData().registration().string().isEmpty()) ?
            aircraftState.getAircraftData().registration().string() :
                Bindings.when(aircraftState.callSignProperty().isNotNull()).
                        then(Bindings.convert(aircraftState.callSignProperty().map(CallSign::string))).
                        otherwise(aircraftState.getIcaoAddress().string());

        StringBinding line1 = Bindings.createStringBinding(() -> {
            String velocity = (Double.isNaN(aircraftState.getVelocity())) ? "?" :
                    String.format("%.0f", Units.convert(aircraftState.getVelocity(),
                            Units.Speed.METER_PER_SECOND, Units.Speed.KILOMETER_PER_HOUR));
            String altitude = (Double.isNaN(aircraftState.getAltitude())) ? "?" :
                    String.format("%.0f", aircraftState.getAltitude());
            return String.format("%s km/h\u2002%s m", velocity, altitude);
        }, aircraftState.velocityProperty(), aircraftState.altitudeProperty());

        Text text = new Text();
        text.textProperty().bind(Bindings.format("%s\n%s", line0, line1));

        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(text.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rectangle.heightProperty().bind(text.layoutBoundsProperty().map(b -> b.getHeight() + 4));

        label.getChildren().addAll(rectangle, text);
    }

    /**
     * crée le groupe de l'icone et l'ajoute au groupe de l'étiquette et de l'icone
     *
     * @param aircraftState     l'état de l'aéronef
     * @param iconAndLabelGroup le groupe de l'étiquette et de l'icone
     */
    private void addIcon(ObservableAircraftState aircraftState, Group iconAndLabelGroup) {
        SVGPath icon = new SVGPath();
        icon.getStyleClass().add("aircraft");
        iconAndLabelGroup.getChildren().add(icon);

        ObjectBinding<AircraftIcon> aircraftIcon = Bindings.createObjectBinding(() -> {
                    if (Objects.isNull(aircraftState.getAircraftData())) return AircraftIcon.iconFor(
                            new AircraftTypeDesignator(""), new AircraftDescription(""),
                            aircraftState.getCategory(), WakeTurbulenceCategory.of(""));
                    return AircraftIcon.iconFor(aircraftState.getAircraftData().typeDesignator(),
                            aircraftState.getAircraftData().description(),
                            aircraftState.getCategory(),
                            aircraftState.getAircraftData().wakeTurbulenceCategory());
                },
                aircraftState.categoryProperty());

        icon.contentProperty().bind(Bindings.createStringBinding(() -> aircraftIcon.get().svgPath()));
        icon.rotateProperty().bind(Bindings.createDoubleBinding(() -> (aircraftIcon.get().canRotate()) ?
                                Units.convertTo(aircraftState.getTrackOrHeading(), Units.Angle.DEGREE) : 0,
                aircraftState.trackOrHeadingProperty()));
        icon.fillProperty().bind(Bindings.createObjectBinding(() -> plasmaAt(aircraftState.getAltitude()),
                aircraftState.altitudeProperty()));

        icon.setOnMouseClicked(e -> selectedAircraft.set(aircraftState));
    }

    /**
     * retourne la couleur correspondante à une valeur selon la formule donnée
     *
     * @param value la valeur donnée
     * @return la couleur correspondante
     */
    private Color plasmaAt(double value) {
        return ColorRamp.PLASMA.at(Math.cbrt(value / 12000));
    }
}