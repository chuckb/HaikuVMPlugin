# HaikuVMPlugin Project

This is a Gradle plugin which aspires to simplify building and deploying Java applications on to bare metal
micros, such as Arduino compatibles and the Raspberry Pis. It uses the [HaikuVM](http://haiku-vm.sourceforge.net/) project to generate a deployable virtual machine embedded with the target application's bytecode. It also couples a serial bootloader and boot image bundlng (in the Raspberry Pi case) to reduce embedded deployment friction.

This project is in active development and more hosts, boards, and documentation are planned for the future. The ./samples projects contain simple Java applications that demonstrate basic hello world LED blinking applications using delays, timers, and interrupts on the the Raspberry Pi Zero. They were tested out cross-compiling on a Mac host.

## Prerequisites
1. [Java](https://www.oracle.com/java/technologies/) 1.8 or better
2. Max OS/X or Windows 10 with WSL
3. [ARM cross compiler](https://developer.arm.com/open-source/gnu-toolchain/gnu-rm/downloads)
4. [Pi Zero](https://www.adafruit.com/product/2885)
5. USB to TTL Serial Cable like [this one from Adafruit](https://www.adafruit.com/product/954)

## Running The Delayblink Sample
The [delayblink](samples/delayblink) Java project will blink the green ACT LED on the Rpi Zero.
1. Clone this project.
2. Change directory to samples/delayblink.
3. `./gradlew buildPiImage`
4. `cd build/firmware/pi`
5. Transfer the built pi boot image file to an empty micro SD card. Use `sudo dd if=boot.img of=/dev/diskx bs=512`. Use `sudo diskutil list` to find your SD card. Use `sudo diskutil unmountDisk /dev/diskx` to make the card available for dd writing.
6. Plug card into Pi. Plug in USB serial adapter to workstation.
7. Change directory back to samples/delayblink.
8. `./gradlew deploy` A console window should launch with host bootloader ready to transfer kernel to Rpi.
9. Connect serial cable pins to Pi.
10. Marvel at the blinking active LED on the Pi Zero board using [bare metal Java code](samples/delayblink/src/main/java/Main.java)
<img src="https://github.com/chuckb/HaikuVMPlugin/blob/master/resources/images/RPiSerial.jpg" alt="RPi Serial Connection" width="250">

## Build A Standalone Project Using The Plugin
See [Embedded Java Hello World On Raspberry Pi Zero](https://blog.chuckstechtalk.com/software/2020/03/28/embedded-java-hello-world-on-raspberry-pi-zero.html). For Windows setup considerations, see [Embedded Java For Raspberry Pi Zero Setup On Windows 10 Hosts](https://blog.chuckstechtalk.com/software/2020/04/02/embedded-java-raspberry-pi-windows.html)

## Attribution
This plugin is my own original work. My contribution was to add the missing bare-metal bootstrapping for boards like the Raspberry Pi and bundling/streamlining of the build/deploy operations into something familiar to Java developers, like Gradle tasks.

The plugin makes use of several projects and many educational sources:
- [HaikuVM](http://haiku-vm.sourceforge.net/)
  - HaikuVM brought Java to Arduino platforms by taking compiled Java bytecode, the Arduino bootstrap, and the leJOS VM and combining them into a native C application that can be cross-compiled to a target.
  - HaikuVM is based on work from [leJOS](http://www.lejos.org). leJOS is published under the Mozilla Public License 1.0. leJOS was developed for the Lego Mindstorms &trade;.
  - I forked HaikuVM version 1.4.3 and published it [here](https://github.com/chuckb/haikuVM), adding Raspberry Pi bootstrapping and the beginnings of BCM Java wrapper classes.
- The ARM® Architecture Reference Manual, obtainable free with registered account from https://developer.arm.com
- The [ARM1176JZF-S Technical Reference Manual](http://infocenter.arm.com/help/topic/com.arm.doc.ddi0301h/DDI0301H_arm1176jzfs_r0p7_trm.pdf)
- The [Broadcom BCM2835 ARM Peripherals Manual](https://www.raspberrypi.org/app/uploads/2012/02/BCM2835-ARM-Peripherals.pdf)
- The [ELinux BCM2835 Datasheet Eratta](https://elinux.org/BCM2835_datasheet_errata#p90)
- A [serial bootloader](https://github.com/mrvn/raspbootin) from which I patched pieces from several forks and published into my own fork [here](https://github.com/chuckb/raspbootin).
- Learnings from [Bare metal Raspberry Pi 3 tutorials](https://github.com/bztsrc/raspi3-tutorial)
- More learnings from [Raspberry-Pi Bare Metal Tutorial](https://github.com/BrianSidebotham/arm-tutorial-rpi). I can't give enough kudos. If you want to learn about bare-metal bring-ups, read this.
- A sample [build.gradle](https://gist.github.com/Stephen-Seo/2466754204909435a160) demonstrating how to build native apps with Gradle.
- [A Java library for manipulating FAT file systems](http://waldheinz.github.io/fat32-lib/). I fixed compatibility problems found in Raspberry Pi first and second stage bootloaders and fixed in [this fork](https://github.com/chuckb/fat32-lib). You read more in my blog article [Programmatically Building A FAT File System In Java](https://blog.chuckstechtalk.com/software/2020/03/26/programatic-fat-java-library.html)