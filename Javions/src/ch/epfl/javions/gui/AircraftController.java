package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
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

import java.util.Iterator;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * gère la vue des aéronefs
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class AircraftController {

    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> states;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;
    private final Pane pane;

    /**
     * constructeur public
     * @param mapParameters les paramètres de la portion de la carte visible à l'écran
     * @param states l'ensemble des états des aéronefs qui doivent apparaître sur la vue
     * @param selectedAircraft une propriété JavaFX contenant l'état de l'aéronef sélectionné
     */
    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> states,
                              ObjectProperty<ObservableAircraftState> selectedAircraft){
        this.mapParameters = mapParameters;
        this.states = states;
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
                // plus optimale ?
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
     * @return le panneau JavaFX sur lequel les aéronefs sont affichés
     */
    public Pane pane(){
        return pane;
    }

    /**
     * ajoute le groupe de l'aéronef au panneau
     * @param aircraftState l'état de l'aéronef
     */
    private void addGroup(ObservableAircraftState aircraftState){
        //à contôler
        if (aircraftState.getAircraftData() == null) return;

        Group aircraftGroup = new Group();
        pane.getChildren().add(aircraftGroup);
        aircraftGroup.setId(aircraftState.getIcaoAddress().string());
        aircraftGroup.viewOrderProperty().bind(aircraftState.altitudeProperty().negate());

        addTrajectory(aircraftState, aircraftGroup);

        addIconAndLabel(aircraftState, aircraftGroup);
    }

    /**
     * ajoute le groupe de la trajectoire au groupe de l'aéronef
     * @param aircraftState l'état de l'aéronef
     * @param aircraftGroup le groupe de l'aéronef
     */
    private void addTrajectory(ObservableAircraftState aircraftState, Group aircraftGroup){
        Group trajectory = new Group();
        trajectory.getStyleClass().add("trajectory");
        aircraftGroup.getChildren().add(trajectory);
        trajectory.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                        selectedAircraft.get() != null &&
                                selectedAircraft.get().equals(aircraftState),
                selectedAircraft));
        trajectory.visibleProperty().addListener((p, o, n) -> {
            if (!o && n) createTrajectory(aircraftState,trajectory);
            if (o && !n) trajectory.getChildren().clear();
        });
        aircraftState.getTrajectory().addListener((ListChangeListener<ObservableAircraftState.AirbornePos>) change -> {
            if (selectedAircraft.get() != null &&
                    change.next() && change.wasAdded() &&
                    selectedAircraft.get().equals(aircraftState)) {
                int index = aircraftState.getTrajectory().size() - 1;
                ObservableAircraftState.AirbornePos start = aircraftState.getTrajectory().get(index -1);
                ObservableAircraftState.AirbornePos end = aircraftState.getTrajectory().get(index);
                addLineToTrajectory(start, end, trajectory);
            }
        });
        mapParameters.zoomProperty().addListener((p, o, n) -> {
            if (selectedAircraft.get() != null && o.intValue() != n.intValue()
                    && selectedAircraft.get().equals(aircraftState))
                createTrajectory(aircraftState,trajectory);
        });
    }

    /**
     * crée toutes les lignes de la trajectoire
     * @param aircraftState l'état de l'aéronef
     * @param trajectory le groupe de la trajectoire
     */
    private void createTrajectory(ObservableAircraftState aircraftState, Group trajectory){
        Iterator<ObservableAircraftState.AirbornePos> it = aircraftState.getTrajectory().iterator();
        ObservableAircraftState.AirbornePos start = it.next();
        while (it.hasNext()){
            ObservableAircraftState.AirbornePos end = it.next();
            addLineToTrajectory(start, end, trajectory);
            start = end;
        }
    }

    /**
     * ajoute une ligne à la trajectoire
     * @param start la pasition du début
     * @param end la position de la fin
     * @param trajectory le groupe de la trajectoire
     */
    private void addLineToTrajectory(ObservableAircraftState.AirbornePos start,
                                     ObservableAircraftState.AirbornePos end,
                                     Group trajectory){
        double startX = WebMercator.x(mapParameters.zoom(), start.position().longitude());
        double startY = WebMercator.y(mapParameters.zoom(), start.position().latitude());
        double endX = WebMercator.x(mapParameters.zoom(), end.position().longitude());
        double endY = WebMercator.y(mapParameters.zoom(), end.position().latitude());
        Line line = new Line(startX, startY, endX, endY);
        line.layoutXProperty().bind(mapParameters.minXProperty().negate());
        line.layoutYProperty().bind(mapParameters.minYProperty().negate());
        if (start.altitude() == end.altitude()){
            line.setStroke(ColorRamp.PLASMA.at(Math.pow(end.altitude()/12000, 1.0/3)));
        } else {
            Color startColor = ColorRamp.PLASMA.at(Math.pow(start.altitude()/12000, 1.0/3));
            Color endColor = ColorRamp.PLASMA.at(Math.pow(end.altitude()/12000, 1.0/3));
            Stop s1 = new Stop(0, startColor);
            Stop s2 = new Stop(1, endColor);
            line.setStroke(new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, s1, s2));
        }
        trajectory.getChildren().add(line);
    }

    /**
     * ajoute le groupe de l'icone et de l'étiquette au groupe de l'aéronef
     * @param aircraftState l'état de l'aéronef
     * @param aircraftGroup le groupe de l'aéronef
     */
    private void addIconAndLabel(ObservableAircraftState aircraftState, Group aircraftGroup){
        Group iconAndLabelGroup = new Group();
        aircraftGroup.getChildren().add(iconAndLabelGroup);
        iconAndLabelGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.x(mapParameters.zoom(), aircraftState.getPosition().longitude()) - mapParameters.minX(),
                mapParameters.zoomProperty(),
                aircraftState.positionProperty(),
                mapParameters.minXProperty()));
        iconAndLabelGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.y(mapParameters.zoom(), aircraftState.getPosition().latitude()) - mapParameters.minY(),
                mapParameters.zoomProperty(),
                aircraftState.positionProperty(),
                mapParameters.minYProperty()));

        addLabel(aircraftState, iconAndLabelGroup);
        addIcon(aircraftState, iconAndLabelGroup);
    }

    /**
     * crée le groupe de l'étiquette et l'ajoute au groupe de l'icone et de l'étiquette
     * @param aircraftState l'état de l'aéronef
     * @param iconAndLabelGroup le groupe de l'icone et de l'étiquette
     */
    private void addLabel(ObservableAircraftState aircraftState, Group iconAndLabelGroup){
        Group label = new Group();
        label.getStyleClass().add("label");
        iconAndLabelGroup.getChildren().add(label);
        label.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                        mapParameters.zoom() >= 11 ||
                                (selectedAircraft.get() != null && selectedAircraft.get().equals(aircraftState)),
                mapParameters.zoomProperty(), selectedAircraft));
        Text text = new Text();
        StringBinding line0 = Bindings.createStringBinding(() -> {
            if (aircraftState.getAircraftData().registration().string().isEmpty()){
                if (aircraftState.getCallSign().string().isEmpty()){
                    return aircraftState.getIcaoAddress().string();
                } else {
                    return aircraftState.getCallSign().string();
                }
            } else {
                return aircraftState.getAircraftData().registration().string();
            }}, aircraftState.callSignProperty());
        StringBinding line1 = Bindings.createStringBinding(() -> {
            String velocity = (Double.isNaN(aircraftState.getVelocity())) ? "?" :
                    String.valueOf((int) Units.convert(aircraftState.getVelocity(),
                            Units.Speed.METER_PER_SECOND,Units.Speed.KILOMETER_PER_HOUR));
            String altitude = (Double.isNaN(aircraftState.getAltitude())) ? "?" :
                    String.valueOf((int) aircraftState.getAltitude());
            return String.format("%s km/h\u2002%s mètres", velocity, altitude);
        }, aircraftState.velocityProperty(), aircraftState.altitudeProperty());
        text.textProperty().bind(Bindings.format("%s\n%s", line0, line1));
        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(text.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rectangle.heightProperty().bind(text.layoutBoundsProperty().map(b -> b.getHeight() + 4));
        label.getChildren().addAll(rectangle, text);
    }

    /**
     * crée le groupe de l'icone et l'ajoute au groupe de l'étiquette et de l'icone
     * @param aircraftState l'état de l'aéronef
     * @param iconAndLabelGroup le groupe de l'étiquette et de l'icone
     */
    private void addIcon(ObservableAircraftState aircraftState, Group iconAndLabelGroup){
        SVGPath icon = new SVGPath();
        icon.getStyleClass().add("aircraft");
        iconAndLabelGroup.getChildren().add(icon);
        ObjectBinding<AircraftIcon> aircraftIcon = Bindings.createObjectBinding(() ->
                        AircraftIcon.iconFor(aircraftState.getAircraftData().typeDesignator(),
                                aircraftState.getAircraftData().description(),
                                aircraftState.getCategory(),
                                aircraftState.getAircraftData().wakeTurbulenceCategory()),
                aircraftState.categoryProperty());
        icon.contentProperty().bind(Bindings.createStringBinding(() ->
                aircraftIcon.get().svgPath()));
        icon.rotateProperty().bind(Bindings.createDoubleBinding(() ->
                        (aircraftIcon.get().canRotate()) ?
                                Units.convertTo(aircraftState.getTrackOrHeading(),Units.Angle.DEGREE) : 0,
                aircraftState.trackOrHeadingProperty()));
        icon.fillProperty().bind(Bindings.createObjectBinding(() ->
                        ColorRamp.PLASMA.at(Math.pow(aircraftState.getAltitude()/12000, 1.0/3)),
                aircraftState.altitudeProperty()));
        icon.setOnMouseClicked(e -> selectedAircraft.set(aircraftState));
    }
}