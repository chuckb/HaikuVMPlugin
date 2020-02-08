# HaikuVMPlugin Project

This is the code for a Gradle plugin which aids in building and deploying Java applications on to bare metal
micros, such as Arduino compatibles and the Raspberry Pis. It uses the [HaikuVM](http://haiku-vm.sourceforge.net/) project to generate a
deployable virtual machine embedded with the target application's bytecode contained within c structs as a 
standalone .img file, which serves as the target's kernel.

This project is in active development and more documentation is planned for the future. The manualtest project
contains a simple Java application that demonstrates building a bare metal Java application for the Raspberry Pi
Zero using the task mainExecutable.

## Usage
At this time, I have not bundled up the plugin and deployed to the Gradle repo. The plugin is contained as a build source dependency for the manualtest project as a proof of concept that this can be made to work.
1. Clone the project.
2. Change directory to manualtest.
3. `./gradlew mainExecutable`
4. Marvel at the main.elf file built in ./build/exe/main