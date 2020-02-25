import haiku.bcm.lib.rpi.*;

public class Main {
  /**
   * Main function entry point for app
   */
  public static void main(String[] args) {
    int tim;
    
    Rpi.init();
    
    while(true) {
      // Delay
      for(tim = 0; tim < 50000; tim++);

      /* Set the LED GPIO pin low ( Turn OK LED on for original Pi, and off for plus models ) */
      Rpi.ACTLEDOn();

      for(tim = 0; tim < 50000; tim++);

      /* Set the LED GPIO pin high ( Turn OK LED off for original Pi, and on for plus models ) */
      Rpi.ACTLEDOff();
    }
  }
}