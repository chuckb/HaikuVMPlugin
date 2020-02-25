import haiku.bcm.lib.rpi.*;

public class Main {
  /**
   * Main function entry point for Hello World console printing
   * application. The haiku.bcm.lib.HaikuMicroKernelWithConsole micro kernel
   * sets up the serial port and shims up the System class to route output
   * steam to the mini UART.
   */
  public static void main(String[] args) {
    System.out.println("Hello World!");
    while(true) {}
  }
}