import haiku.vm.NativeCBody;
import haiku.vm.NativeCFunction;

@NativeCBody(cImpl ="volatile unsigned int* gpio = (unsigned int*)0x20200000UL;")

public class Main {
  @NativeCFunction(cImpl = "gpio[arg1] = arg2;")
  private static native void setGPIO(int offset, int value);  
        
  @NativeCFunction(cImpl = "return gpio[arg1];")
  private static native int getGPIO(int offset);

  public static final int GPIO_GPFSEL4 = 4;           // GPIO Function Select 4
  public static final int LED_GPFSEL = GPIO_GPFSEL4;
  public static final int GPIO_GPSET1 = 8;            // GPIO Pin Output Set 1
  public static final int GPIO_GPCLR1 = 11;           // GPIO Pin Output Clear 1
  public static final int LED_GPFBIT = 21;
  public static final int LED_GPSET = GPIO_GPSET1;
  public static final int LED_GPCLR = GPIO_GPCLR1;
  public static final int LED_GPIO_BIT = 15;
    
  /**
   * Main function entry point for app
   */
  public static void main(String[] args) {
    /* Write 1 to the GPIO init nibble in the Function Select GPIO peripheral register to set
        the GPIO pin for the ACT LED as an output */
    setGPIO(LED_GPFSEL, (1 << LED_GPFBIT) | getGPIO(LED_GPFSEL)&0xFFFFFFFF);
    
    while(true) {
      // Test the millisecond timer
      long seconds = System.currentTimeMillis() / 1000;

      // Toggle every second
      if (seconds % 2 == 0) {
        /* Set the LED GPIO pin high ( Turn OK LED off for original Pi, and on for plus models ) */
        setGPIO(LED_GPSET, (1 << LED_GPIO_BIT));
      } else {
        /* Set the LED GPIO pin low ( Turn OK LED on for original Pi, and off for plus models ) */
        setGPIO(LED_GPCLR, (1 << LED_GPIO_BIT));
      }
    }
  }
}