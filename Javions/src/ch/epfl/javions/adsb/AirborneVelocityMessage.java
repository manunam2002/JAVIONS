package ch.epfl.javions.adsb;

import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading)
        implements Message{

    public AirborneVelocityMessage{
        if (icaoAddress == null) throw new NullPointerException();
        if (timeStampNs < 0 || speed < 0 || trackOrHeading < 0) throw new IllegalArgumentException();
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage){
        int subType = (rawMessage.bytes().byteAt(4) & 0b111);
        if (subType == 1 || subType == 2){
            long NUC = (rawMessage.bytes().bytesInRange(5,9)>>>5)&0b1111111111111111111111;
            int Vns = (int) (NUC & 0b1111111111);
            int Dns = (int) ((NUC >>> 10) & 0b1);
            int Vew = (int) ((NUC >>> 11) & 0b1111111111);
            int Dew = (int) ((NUC >>> 21) & 0b1);
            if (Vns == 0 || Vew == 0) return null;
            --Vns;
            --Vew;
            double velocity = Math.hypot(Vns,Vew);
            if (Dns == 1) Vns = -Vns;
            if (Dew == 1) Vew = -Vew;
            double angle = Math.atan2(Vew,Vns);
            if (angle < 0) angle = 2*Math.PI + angle;
            if (subType == 1) return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                    Units.convert(velocity,Units.Speed.KNOT,Units.Speed.METER_PER_SECOND), angle);
            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                    Units.convert(velocity*4,Units.Speed.KNOT,Units.Speed.METER_PER_SECOND), angle);
        }
        if (subType == 3 || subType == 4){
            long NUC = (rawMessage.bytes().bytesInRange(5,9)>>>5)&0b1111111111111111111111;
            int AS = (int) (NUC & 0b1111111111);
            int HDG = (int) ((NUC >>> 11) & 0b1111111111);
            int SH = (int) ((NUC >>> 21) & 0b1);
            if (SH == 0) return null;
            double angle = Units.convertFrom(Math.scalb(HDG,-10),Units.Angle.TURN);
            if (subType == 3) return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                    Units.convert(AS,Units.Speed.KNOT,Units.Speed.METER_PER_SECOND), angle);
            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                    Units.convert(AS*4,Units.Speed.KNOT,Units.Speed.METER_PER_SECOND), angle);
        }
        return null;
    }

    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }
}
