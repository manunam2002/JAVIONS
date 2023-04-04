package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public final class ObservableAircraftState implements AircraftStateSetter {

    private final ObjectProperty<IcaoAddress> icaoAddress = new SimpleObjectProperty<>();
    private final ObjectProperty<AircraftData> aircraftData = new SimpleObjectProperty<>();
    private final LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private final IntegerProperty category = new SimpleIntegerProperty();
    private final ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private final ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private final DoubleProperty altitude = new SimpleDoubleProperty();
    private final DoubleProperty velocity = new SimpleDoubleProperty();
    private final DoubleProperty trackOrHeading = new SimpleDoubleProperty();
    private final ListProperty<AirbornePos> trajectory = new SimpleListProperty<>();
    private ObservableList<AirbornePos> observableTrajectory = FXCollections.observableArrayList();
    private ObservableList<AirbornePos> unmodifiableTrajectory =
            FXCollections.unmodifiableObservableList(observableTrajectory);
    private long lastPositionMessageTimeStampNs;

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData aircraftData){
        this.icaoAddress.set(icaoAddress);
        this.aircraftData.set(aircraftData);
    }

    public ReadOnlyObjectProperty<IcaoAddress> icaoAddressProperty(){
        return icaoAddress;
    }

    public IcaoAddress getIcaoAddress(){
        return icaoAddress.get();
    }

    public void setIcaoAddress(IcaoAddress icaoAddress) {
        this.icaoAddress.set(icaoAddress);
    }

    public ReadOnlyObjectProperty<AircraftData> aircraftDataProperty(){
        return aircraftData;
    }

    public AircraftData getAircraftData() {
        return aircraftData.get();
    }

    public void setAircraftData(AircraftData aircraftData) {
        this.aircraftData.set(aircraftData);
    }

    public record AirbornePos(GeoPos position, double altitude){

    }

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
        this.position.set(position);
        if (getLastMessageTimeStampNs() == lastPositionMessageTimeStampNs){
            AirbornePos airbornePos = new AirbornePos(position,getAltitude());
            trajectory.set(trajectory.size()-1,airbornePos);
        }
        if (observableTrajectory.isEmpty() || position != observableTrajectory.get(trajectory.size()-1).position){
            AirbornePos airbornePos = new AirbornePos(position,getAltitude());
            trajectory.add(airbornePos);
            lastPositionMessageTimeStampNs = getLastMessageTimeStampNs();
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

    public ReadOnlyListProperty<AirbornePos> trajectoryProperty(){
        return trajectory;
    }

    public List<AirbornePos> getTrajectory(){
        return unmodifiableTrajectory;
    }
}