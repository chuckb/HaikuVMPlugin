import haiku.bcm.lib.rpi.*;
import haiku.bcm.*;
import haiku.vm.NativeCBody;
import haiku.vm.NativeCFunction;

@NativeCBody(cImpl = "#include <platforms/pi/rpi-armtimer.h>\n" +
"volatile int lit = 0;\n" +
"extern \"C\" void __attribute__((interrupt(\"IRQ\"))) interrupt_vector(void) {\n" +
"    RPI_GetArmTimer()->IRQClear = 1;\n" +
"    if( lit )\n" +
"    {\n" +
"        lit = 0;\n" +
"    }\n" +
"    else\n" + 
"    {\n" +
"        lit = 1;\n" +
"    }\n" +
"}")

public class Main {
  @NativeCFunction(cImpl = "return lit;")
  public static native int getLit();

  /**
   * Main function entry point for app
   */
  public static void main(String[] args) {
    Rpi.init();
    
    // Enable the timer interrupt IRQ
    Interrupts.setEnable_Basic_IRQs(Interrupts.RPI_BASIC_ARM_TIMER_IRQ);

    // Setup the system timer interrupt
    // Timer frequency = Clk/256 * 0x400
    ArmTimer.setLoad(0x200);

    // Setup the ARM timer
    ArmTimer.setControl(
      ArmTimer.RPI_ARMTIMER_CTRL_23BIT |
      ArmTimer.RPI_ARMTIMER_CTRL_ENABLE |
      ArmTimer.RPI_ARMTIMER_CTRL_INT_ENABLE |
      ArmTimer.RPI_ARMTIMER_CTRL_PRESCALE_256
    );

    Rpi._enable_interrupts();

    System.out.println("Interrupt driven blinky test started.");

    while(true) {
      // Respond to bit flip, set in the interrupt service routine.
      if (getLit() == 0) {
        /* Set the LED GPIO pin low ( Turn OK LED on for original Pi, and off for plus models ) */
        Rpi.ACTLEDOn();
      } else {
        /* Set the LED GPIO pin high ( Turn OK LED off for original Pi, and on for plus models ) */
        Rpi.ACTLEDOff();
      }
    }
  }
}