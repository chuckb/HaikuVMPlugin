# HaikuVMPlugin Project

This is the code for a Gradle plugin which aids in building and deploying Java applications on to bare metal
micros, such as Arduino compatibles and the Raspberry Pis. It uses the [HaikuVM](http://haiku-vm.sourceforge.net/) project to generate a
deployable virtual machine embedded with the target application's bytecode contained within c structs as a 
standalone .img file, which serves as the target's kernel.

This project is in active development and more documentation is planned for the future. The samples/delayblink project
contains a simple Java application that demonstrates building and running a bare metal Java application on the Raspberry Pi
Zero using the gradle task deploy.

## Prerequisites
1. Java 1.8 or better
2. Max OS/X (Windows will be tested shortly...maybe)
3. [ARM cross compiler](https://developer.arm.com/open-source/gnu-toolchain/gnu-rm/downloads)
4. [Pi Zero](https://www.adafruit.com/product/2885)
5. A [serial bootloader](https://github.com/chuckb/raspbootin/tree/master/raspbootin2) and barebones Pi image installed on a micro SD
card.  TODO: This will be a gradle task.
6. USB to TTL Serial Cable like [this one from Adafruit](https://www.adafruit.com/product/954)

## Usage
At this time, I have not bundled up the plugin and deployed to the Gradle repo. The plugin is contained as a build source dependency for the samples projects as a proof of concept that this can be made to work.
1. Clone the project.
2. Change directory to samples/manualtest.
3. Plug in USB serial adapter to workstation.
4. `./gradlew deploy`
5. Connect serial cable pins to Pi.  TODO: Show pic here.
6. Marvel at the blinking active LED on the Pi Zero board.
