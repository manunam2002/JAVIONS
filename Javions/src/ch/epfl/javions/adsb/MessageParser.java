package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public class MessageParser {
     public static Message parse(RawMessage rawMessage){
         return new Message() {
             @Override
             public long timeStampNs() {
                 return 0;
             }

             @Override
             public IcaoAddress icaoAddress() {
                 return null;
             }
         };
         }
     }
