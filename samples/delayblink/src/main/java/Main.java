import haiku.vm.NativeCBody;
import haiku.vm.NativeCFunction;

/**
 * Define access to GPIO registers
 */
@NativeCBody(cImpl ="uint32_t volatile * gpio = (uint32_t volatile *)0x20200000UL;")

/**
 * Make the green ACT LED on the Raspberry Pi Zero blink
 */
public class Main {
  @NativeCFunction(cImpl = "gpio[arg1] = arg2;")
  /**
   * Set the value of a GPIO register.
   * @param registerOffset  The count of the number of registers from
   *                        the beginning offset.
   * @param value           The value to set
   */
  private static native void setGPIO(int registerOffset, int value);  
        
  @NativeCFunction(cImpl = "return gpio[arg1];")
  /**
   * Fetch the value of a GPIO register.
   * @param registerOffset  The count of the number of registers from
   *                        the beginning of the offset.
   * @return                The value of the register fetched.
   */
  private static native int getGPIO(int registerOffset);

  public static final int REG_LEN = 4;                  // Registers are 4 bytes long
  public static final int GPIO_GPFSEL4 = 0x10/REG_LEN;  // GPIO Function Select 4
  public static final int GPIO_GPSET1 = 0x20/REG_LEN;   // GPIO Pin Output Set 1
  public static final int GPIO_GPCLR1 = 0x2C/REG_LEN;   // GPIO Pin Output Clear 1
  public static final int LED_GPF_OUTPUT_BIT = 21;      // GPIO Function Bit For Output, GPIO47
  public static final int LED_GPIO_BIT = 15;            // Set/Clr Bit For GPIO47
    
  /**
   * Main function entry point for app
   */
  public static void main(String[] args) {
    int tim;

    // Write 1 to the GPIO Function Select field (FSEL47) of the GPFSEL4 register to set
    // the GPIO47 pin for the ACT LED as an output. Don't trounce over data
    // that might already be in the register. This why we are reading the register
    // and then "oring" in the contents to set.
    setGPIO(GPIO_GPFSEL4, (1 << LED_GPF_OUTPUT_BIT) | getGPIO(GPIO_GPFSEL4));
    
    while(true) {
      // Delay
      for(tim = 0; tim < 50000; tim++);

      // Set the CLR47 field witin the GPCLR1 register, by setting it to 1
      // (thus asserting 0 the GPIO47 pin), which pulls the LED cathode to ground, 
      // thus lighting the LED.
      setGPIO(GPIO_GPCLR1, (1 << LED_GPIO_BIT));

      for(tim = 0; tim < 50000; tim++);

      // Set the SET47 field witin the GPSET1 register, by setting it to 1
      // (thus asserting 1 the GPIO47 pin), which pulls the LED cathode to +3.3v. 
      // With no voltage drop across the LED, it will not light.
      setGPIO(GPIO_GPSET1, (1 << LED_GPIO_BIT));
    }
  }
}