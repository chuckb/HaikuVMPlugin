package com.chuckbenedict.gradle.plugin;

import java.io.File;
import java.util.List;

import com.chuckbenedict.gradle.plugin.extensions.NativeBuildExtension;
import com.chuckbenedict.gradle.plugin.tasks.ShellDeployScript;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ExternalDependency;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Exec;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.language.c.CSourceSet;
import org.gradle.language.c.plugins.CPlugin;
import org.gradle.language.cpp.CppSourceSet;
import org.gradle.language.cpp.plugins.CppPlugin;
import org.gradle.model.ModelMap;
import org.gradle.model.Mutate;
import org.gradle.model.Path;
import org.gradle.model.RuleSource;
import org.gradle.nativeplatform.Flavor;
import org.gradle.nativeplatform.FlavorContainer;
import org.gradle.nativeplatform.NativeExecutableBinarySpec;
import org.gradle.nativeplatform.NativeExecutableSpec;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.toolchain.Gcc;
import org.gradle.nativeplatform.toolchain.GccPlatformToolChain;
import org.gradle.nativeplatform.toolchain.NativeToolChainRegistry;
import org.gradle.platform.base.ComponentSpecContainer;
import org.gradle.platform.base.Platform;
import org.gradle.platform.base.PlatformContainer;

/**
 * Configure the c and cpp plugins to build the native application.
 * Certain project values are needed, which are not provided via the
 * model infrastructure. They have been provided via a custom extension.
 * See https://mrhaki.blogspot.com/search/label/Gradle%3AGoodness for the
 * technique.
 */
public class BuildNative implements Plugin<Project> {
  @Override
  public void apply(final Project project) {
    // Apply the c and cpp plugins to the project
    project.getPlugins().apply(CppPlugin.class);
    project.getPlugins().apply(CPlugin.class);
    //TODO: Don't assume this is created already...in fact, it should be created here.
    final NativeBuildExtension nativeBuildExtension = project.getExtensions().getByType(NativeBuildExtension.class);

    project.getConfigurations().create("deployment", deployment -> {
      // Add the host side serial boot loader sender utility
      ExternalDependency raspbootcom = (ExternalDependency)project.getDependencies().create("com.github.chuckb.raspbootin:raspbootcom");
      raspbootcom.setTargetConfiguration("executable");
      //TODO: Make configurable
      raspbootcom.version(v -> {
        v.setBranch("master");
      });
      deployment.getDependencies().add(raspbootcom);

      // Add the Raspberry Pi side serial boot loader kernel
      ExternalDependency raspbootin2 = (ExternalDependency)project.getDependencies().create("com.github.chuckb.raspbootin:raspbootin2");
      raspbootin2.setTargetConfiguration("executable");
      //TODO: Make configurable
      raspbootin2.version(v -> {
        v.setBranch("master");
      });
      deployment.getDependencies().add(raspbootin2);

      nativeBuildExtension.setDeploymentConfiguration(deployment);
    });
  }

  public static class Rpi extends RuleSource {
    @Mutate
    void addPlatforms(PlatformContainer platforms) {
      Platform platform = platforms.create("none_arm", NativePlatform.class, new Action<NativePlatform>() {
        public void execute(NativePlatform nativePlatform) {
          nativePlatform.operatingSystem("none");
          nativePlatform.architecture("arm");
        }
      });
      platforms.add(platform);
    }
  
    @Mutate
    void addFlavors(FlavorContainer flavors) {
      Flavor flavor = flavors.create("rpi");
      flavors.add(flavor);
    }
  
    @Mutate
    void configureToolChains(NativeToolChainRegistry toolChains, final ExtensionContainer extensionContainer) {
      final NativeBuildExtension nativeBuildExtension = extensionContainer.getByType(NativeBuildExtension.class);
      File projectDir = nativeBuildExtension.getProjectDir();
      toolChains.create("gcc", Gcc.class, new Action<Gcc>() {
        public void execute(Gcc gcc) {
          // Set the executable names for gcc cross compiler
          gcc.eachPlatform(new Action<GccPlatformToolChain>() {
            public void execute(GccPlatformToolChain toolChain) {
              toolChain.getcCompiler().setExecutable("arm-none-eabi-gcc");
              toolChain.getCppCompiler().setExecutable("arm-none-eabi-g++");
              toolChain.getLinker().setExecutable("arm-none-eabi-gcc");
              toolChain.getAssembler().setExecutable("arm-none-eabi-as");
              toolChain.getStaticLibArchiver().setExecutable("arm-none-eabi-ar");
            }
          });
          // Configure the RPI target build
          gcc.target("none_arm", new Action<GccPlatformToolChain>() {
            public void execute(GccPlatformToolChain toolChain) {
              // Configure GCC compiler flags for cross-compiling targeting the RPI
              toolChain.getcCompiler().withArguments(new Action<List<String>>() {
                public void execute(List<String> args) {
                  args.add("-DRPI0");
//                  args.add("-D__arm1176jzf-s__");
                  args.add("-c");
                  args.add("-O0");
                  args.add("-fno-builtin");
                  args.add("-funsigned-char");
                  args.add("-funsigned-bitfields"); 
                  args.add("-fomit-frame-pointer");
                  args.add("-fshort-enums");
                  args.add("-std=gnu99");
                  args.add("-Wall");
                  args.add("-nostartfiles");
                  args.add("-march=armv6zk");
                  args.add("-mtune=arm1176jzf-s");
                  args.add("-mfloat-abi=hard");
                  args.add("-mfpu=vfp");
                }
              });
              toolChain.getCppCompiler().withArguments(new Action<List<String>>() {
                public void execute(List<String> args) {
                  args.add("-DRPI0");
//                  args.add("-D__arm1176jzf-s__");
                  args.add("-c");
                  args.add("-O0");
                  args.add("-fno-builtin");
                  args.add("-funsigned-char");
                  args.add("-funsigned-bitfields");
                  args.add("-fomit-frame-pointer");
                  args.add("-fshort-enums");
                  args.add("-fno-exceptions");
                  args.add("-felide-constructors");
                  args.add("-nostartfiles");
                  args.add("-std=gnu++14");
                  args.add("-Wno-error=narrowing");
                  args.add("-fno-rtti");
                  args.add("-march=armv6zk");
                  args.add("-mtune=arm1176jzf-s");
                  args.add("-mfloat-abi=hard");
                  args.add("-mfpu=vfp");
                  args.add("-fsingle-precision-constant");
                }
              });
              // Configure GCC linker flags for linking the RPI target
              toolChain.getLinker().withArguments(new Action<List<String>>() {
                public void execute(List<String> args) {
                  args.add("-O0");
                  args.add("-nostartfiles");
                  args.add("-lstdc++");
                  args.add("-Wl,-T," + projectDir.getAbsolutePath() + "/build/HaikuVM/src/lib/ldscripts/rpi.x" + ",--gc-sections,--relax,--defsym=__rtc_localtime=0");
                  args.add("-lm");
                  args.add("-march=armv6zk");
                  args.add("-mtune=arm1176jzf-s");
                  args.add("-mfloat-abi=hard");
                  args.add("-mfpu=vfp");
                  args.add("-fsingle-precision-constant");
                }
              });
            }
          });
        }
      });
    }

    @Mutate
    public void configureComponents(ComponentSpecContainer components, final ExtensionContainer extensionContainer) {
      final NativeBuildExtension nativeBuildExtension = extensionContainer.getByType(NativeBuildExtension.class);
      File projectDir = nativeBuildExtension.getProjectDir();
      Task haikulinkTask = nativeBuildExtension.getHaikulinkTask();

      components.create("main", NativeExecutableSpec.class, new Action<NativeExecutableSpec>() {
        public void execute(NativeExecutableSpec main) {
          main.setBaseName("main.elf");
          main.targetPlatform("none_arm");
          main.getSources().named("cpp", new Action<LanguageSourceSet>() {
            public void execute(LanguageSourceSet languageSourceSet) {
              CppSourceSet sourceSet = (CppSourceSet)languageSourceSet;
              sourceSet.builtBy(haikulinkTask);
              //TODO: Make into constant
              sourceSet.getSource().srcDir(projectDir.getAbsolutePath() + "/build/HaikuVM/src");
              sourceSet.getSource().include("**/*.cpp");
              sourceSet.getExportedHeaders().srcDirs(
                  projectDir.getAbsolutePath() + "/build/HaikuVM/src", 
                  projectDir.getAbsolutePath() + "/build/HaikuVM/src/utility");
            }
          });
          main.getSources().named("c", new Action<LanguageSourceSet>() {
            public void execute(LanguageSourceSet languageSourceSet) {
              CSourceSet sourceSet = (CSourceSet)languageSourceSet;
              sourceSet.builtBy(haikulinkTask);
              sourceSet.getSource().srcDir(projectDir.getAbsolutePath() + "/build/HaikuVM/src");
              sourceSet.getSource().include("**/*.c");
              sourceSet.getExportedHeaders().srcDirs(
                projectDir.getAbsolutePath() + "/build/HaikuVM/src", 
                projectDir.getAbsolutePath() + "/build/HaikuVM/src/utility");
            }
          });
          }
      });
    }

    @Mutate
    public void createGetRawImageTask(ModelMap<Task> tasks, @Path("components.main.binaries.executable") NativeExecutableBinarySpec spec) {
      File elfFile = spec.getExecutable().getFile();
      File imgFile = getImageFile(elfFile);
      tasks.create("getRawImage", Exec.class, new Action<Exec>() {
        public void execute(Exec execTask) {
          execTask.setDescription("Get raw binary image out of linker-generated elf container.");
          execTask.dependsOn("mainExecutable");
          execTask.getInputs().files(elfFile);
          execTask.getOutputs().files(imgFile);
          execTask.setWorkingDir(elfFile.getParentFile());
          execTask.commandLine("arm-none-eabi-objcopy", elfFile.getName(), "-O", "binary", imgFile.getName());
        }
      });  
    }

    @Mutate
    void createShellDeployScriptTask(ModelMap<Task> tasks, 
        @Path("components.main.binaries.executable") NativeExecutableBinarySpec spec,
        final ExtensionContainer extensionContainer) {
      final NativeBuildExtension nativeBuildExtension = extensionContainer.getByType(NativeBuildExtension.class);
      File elfFile = spec.getExecutable().getFile();
      File imgFile = getImageFile(elfFile);
      File deployFile = getDeployScriptFile(elfFile);
      // TODO: Make OS aware
      tasks.create("createShellDeployScript", ShellDeployScript.class, t -> {
        t.setDescription("Create local Mac script to launch Rasbootin in a separate process.");
        t.script.set(deployFile);
        t.image.set(imgFile);
        t.bootLoader.set(nativeBuildExtension.getDeploymentConfiguration().filter(new Spec<File>() {
          @Override
          public boolean isSatisfiedBy(File file) {
            return file.getName().contains("raspbootcom");
          }
        }).getSingleFile());
        t.port.set(nativeBuildExtension.getPort());
        t.dependsOn("getRawImage");
      });
    }

    @Mutate
    public void createRasbootinDeployTask(ModelMap<Task> tasks, 
        @Path("components.main.binaries.executable") NativeExecutableBinarySpec spec,
        final ExtensionContainer extensionContainer) {
      File elfFile = spec.getExecutable().getFile();
      File deployFile = getDeployScriptFile(elfFile);
      tasks.create("deploy", Exec.class, t -> {
        t.setDescription("Run raspberry pi com port boot loader to load raw image.");
        t.dependsOn("createShellDeployScript");
        t.dependsOn(extensionContainer.getByType(NativeBuildExtension.class).getDeploymentConfiguration());
        //TODO: Make OS independent
        t.commandLine("open", "-a", "Terminal", deployFile.getAbsolutePath());
      });
    }

    private File getImageFile(File elfFile) {
      File imgFile;
      if (elfFile.getName().endsWith(".elf")) {
        imgFile = new File(elfFile.getParentFile(), elfFile.getName().replace(".elf", ".img"));
      } else {
        imgFile = new File(elfFile.getAbsolutePath() + ".img");
      }  
      return imgFile;
    }

    private File getDeployScriptFile(File elfFile) {
      return new File(elfFile.getParentFile(), "deploy.sh");
    }
  }  
}