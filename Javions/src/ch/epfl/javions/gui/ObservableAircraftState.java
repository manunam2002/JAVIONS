package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static java.lang.Double.NaN;

/**
 * représente l'état observable d'un aéronef
 *
 * @author Manu Cristini (358484)
 * @author Youssef Esseddik (346488)
 */
public final class ObservableAircraftState implements AircraftStateSetter {

    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;
    private final LongProperty lastMessageTimeStampNs = new SimpleLongProperty(0);
    private final IntegerProperty category = new SimpleIntegerProperty(0);
    private final ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>(null);
    private final ObjectProperty<GeoPos> position = new SimpleObjectProperty<>(null);
    private final DoubleProperty altitude = new SimpleDoubleProperty(NaN);
    private final DoubleProperty velocity = new SimpleDoubleProperty(NaN);
    private final DoubleProperty trackOrHeading = new SimpleDoubleProperty(NaN);
    private final ObservableList<AirbornePos> observableTrajectory = FXCollections.observableArrayList();
    private final ObservableList<AirbornePos> unmodifiableTrajectory =
            FXCollections.unmodifiableObservableList(observableTrajectory);
    private long lastPositionMessageTimeStampNs;

    /**
     * constructeur public
     *
     * @param icaoAddress  l'adresse OACI de l'aéronef
     * @param aircraftData les caractéristiques fixes de cet aéronef
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData) {
        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;
    }

    /**
     * retourne l'adresse OACI de l'aéronef
     *
     * @return l'adresse OACI de l'aéronef
     */
    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }

    /**
     * retourne les caractéristiques fixes de cet aéronef
     *
     * @return les caractéristiques fixes de cet aéronef
     */
    public AircraftData getAircraftData() {
        return aircraftData;
    }

    /**
     * enregistrement public qui représénte des positions dans l'éspace
     *
     * @param position la position de l'aéronef
     * @param altitude l'altitude de l'aéronef
     */
    public record AirbornePos(GeoPos position, double altitude) {
    }

    /**
     * retourne la propriété en lecture seule de l'horodatage du dernier message reçu
     *
     * @return la propriété en lecture seule de l'horodatage du dernier message reçu
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    /**
     * retourne la valeur de l'horodatage du dernier message reçu
     *
     * @return la valeur de l'horodatage du dernier message reçu
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    /**
     * change la valeur de l'horodatage du dernier message reçu
     *
     * @param timeStampNs la valeur de l'horodatage du dernier message reçu
     */
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.set(timeStampNs);
    }

    /**
     * retourne la propriété en lecture seule de la catégorie de l'aéronef
     *
     * @return la propriété en lecture seule de la catégorie de l'aéronef
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    /**
     * retourne la valeur de la catégorie de l'aéronef
     *
     * @return la valeur de la catégorie de l'aéronef
     */
    public int getCategory() {
        return category.get();
    }

    /**
     * change la valeur de la catégorie de l'aéronef
     *
     * @param category la valeur de la catégorie de l'aéronef
     */
    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    /**
     * retourne la propriété en lecture seule de l'indicatif de l'aéronef
     *
     * @return la propriété en lecture seule de l'indicatif de l'aéronef
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    /**
     * retourne la valeur de l'indicatif de l'aéronef
     *
     * @return la valeur de l'indicatif de l'aéronef
     */
    public CallSign getCallSign() {
        return callSign.get();
    }

    /**
     * change la valeur de l'indicatif de l'aéronef
     *
     * @param callSign la valeur de l'indicatif de l'aéronef
     */
    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    /**
     * retourne la propriété en lecture seule de la position de l'aéronef à la surfeace de la Terre
     *
     * @return la propriété en lecture seule de la position de l'aéronef à la surfeace de la Terre
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    /**
     * retourne la valeur de la position de l'aéronef à la surfeace de la Terre
     *
     * @return la valeur de la position de l'aéronef à la surfeace de la Terre
     */
    public GeoPos getPosition() {
        return position.get();
    }

    /**
     * change la valeur de la position de l'aéronef à la surfeace de la Terre et l'ajoute à la trajectoire
     * si elle n'y est pas présente
     *
     * @param position la valeur de la position de l'aéronef à la surfeace de la Terre
     */
    @Override
    public void setPosition(GeoPos position) {
        if (position != null) {
            this.position.set(position);

            if (observableTrajectory.isEmpty() ||
                    position.latitude() != observableTrajectory.get(observableTrajectory.size() - 1).position.latitude()
                    || position.longitude() !=
                    observableTrajectory.get(observableTrajectory.size() - 1).position.longitude()) {
                observableTrajectory.add(new AirbornePos(position, getAltitude()));
                lastPositionMessageTimeStampNs = getLastMessageTimeStampNs();
            }
        }
    }

    /**
     * retourne la propriété en lecture seule de l'altitude de l'aéronef
     *
     * @return la propriété en lecture seule de l'altitude de l'aéronef
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * retourne la valeur de l'altitude de l'aéronef
     *
     * @return la valeur de l'altitude de l'aéronef
     */
    public double getAltitude() {
        return altitude.get();
    }

    /**
     * change la valeur de l'altitude de l'aéronef si le dernier message reçu a le même horodatage
     * du dernier message qui s'est ajouté à la trajectoire
     *
     * @param altitude la valeur de l'altitude de l'aéronef
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);

        if (lastPositionMessageTimeStampNs == getLastMessageTimeStampNs()) {
            observableTrajectory.set(observableTrajectory.size() - 1, new AirbornePos(getPosition(), altitude));
        }
    }

    /**
     * retourne la propriété en lecture seule de la vitesse de l'aéronef
     *
     * @return la propriété en lecture seule de la vitesse de l'aéronef
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    /**
     * retourne la valeur de la vitesse de l'aéronef
     *
     * @return la valeur de la vitesse de l'aéronef
     */
    public double getVelocity() {
        return velocity.get();
    }

    /**
     * change la valeur de la vitesse de l'aéronef
     *
     * @param velocity la valeur de la vitesse de l'aéronef
     */
    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    /**
     * retourne la propriété en lecture seule de la route de l'aéronef
     *
     * @return la propriété en lecture seule de la route de l'aéronef
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    /**
     * retourne la valeur de la route de l'aéronef
     *
     * @return la valeur de la route de l'aéronef
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    /**
     * change la valeur de la route de l'aéronef
     *
     * @param trackOrHeading la valeur de la route de l'aéronef
     */
    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    /**
     * retourne la trajectoire de l'aéronef
     *
     * @return la trajectoire de l'aéronef
     */
    public ObservableList<AirbornePos> getTrajectory() {
        return unmodifiableTrajectory;
    }
}