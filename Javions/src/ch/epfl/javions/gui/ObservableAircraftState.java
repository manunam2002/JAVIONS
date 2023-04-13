package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;

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
    private ObservableList<AirbornePos> observableTrajectory = FXCollections.observableArrayList();
    private ObservableList<AirbornePos> unmodifiableTrajectory =
            FXCollections.unmodifiableObservableList(observableTrajectory);
    private long lastPositionMessageTimeStampNs;

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData){
        this.icaoAddress = icaoAddress;
        this.aircraftData = aircraftData;
    }

    public IcaoAddress getIcaoAddress(){
        return icaoAddress;
    }

    public AircraftData getAircraftData() {
        return aircraftData;
    }

    public record AirbornePos(GeoPos position, double altitude){}

    public ReadOnlyLongProperty lastMessageTimeStampNsProperty(){
        return lastMessageTimeStampNs;
    }

    public long getLastMessageTimeStampNs(){
        return lastMessageTimeStampNs.get();
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.set(timeStampNs);
    }

    public ReadOnlyIntegerProperty categoryProperty(){
        return category;
    }

    public int getCategory(){
        return category.get();
    }

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    public ReadOnlyObjectProperty<CallSign> callSignProperty(){
        return callSign;
    }

    public CallSign getCallSign(){
        return callSign.get();
    }

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    public ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return position;
    }

    public GeoPos getPosition(){
        return position.get();
    }

    @Override
    public void setPosition(GeoPos position) {
        if (position != null){
            this.position.set(position);
            if (observableTrajectory.isEmpty() ||
                    position.latitude() != observableTrajectory.get(observableTrajectory.size()-1).position.latitude()
                    || position.longitude() !=
                    observableTrajectory.get(observableTrajectory.size()-1).position.longitude()){
                observableTrajectory.add(new AirbornePos(position,getAltitude()));
                lastPositionMessageTimeStampNs = getLastMessageTimeStampNs();
            }
        }
    }

    public ReadOnlyDoubleProperty altitudeProperty(){
        return altitude;
    }

    public double getAltitude(){
        return altitude.get();
    }

    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        if (lastPositionMessageTimeStampNs == getLastMessageTimeStampNs()){
            observableTrajectory.set(observableTrajectory.size()-1,new AirbornePos(getPosition(),altitude));
        }
    }

    public ReadOnlyDoubleProperty velocityProperty(){
        return velocity;
    }

    public double getVelocity(){
        return velocity.get();
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty(){
        return trackOrHeading;
    }

    public double getTrackOrHeading(){
        return trackOrHeading.get();
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    public List<AirbornePos> getTrajectory(){
        return unmodifiableTrajectory;
    }
}