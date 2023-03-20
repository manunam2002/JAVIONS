package ch.epfl.javions.adsb;

public class AircraftStateAccumulator<T> {
    AircraftStateAccumulator(T stateSetter){


    }
    public T stateSetter(){
        return stateSetter();
    }
     public void update(Message message){

     }



}
