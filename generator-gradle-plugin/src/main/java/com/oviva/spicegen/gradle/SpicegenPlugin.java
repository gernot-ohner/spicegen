package com.oviva.spicegen.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;

/**
 * Gradle plugin for generating type-safe SpiceDB client code.
 *
 * <p>Applies to Java projects and registers:
 *
 * <ul>
 *   <li>A 'spicegen' extension for configuration
 *   <li>A 'generateSpiceDbSources' task that runs before compilation
 *   <li>Generated sources added to the main source set
 * </ul>
 */
public class SpicegenPlugin implements Plugin<Project> {

  public static final String EXTENSION_NAME = "spicegen";
  public static final String TASK_NAME = "generateSpiceDbSources";

  @Override
  public void apply(Project project) {
    // Ensure java plugin is applied
    project.getPluginManager().apply(JavaPlugin.class);

    // Create extension
    var extension = project.getExtensions().create(EXTENSION_NAME, SpicegenExtension.class);

    // Set conventions (defaults)
    extension
        .getSchemaFile()
        .convention(project.getLayout().getProjectDirectory().file("src/main/resources/schema.zed"));

    extension
        .getOutputDirectory()
        .convention(project.getLayout().getBuildDirectory().dir("generated/sources/spicegen/java/main"));

    extension
        .getPackageName()
        .convention(
            project.provider(
                () -> {
                  var group = project.getGroup().toString();
                  return group.isEmpty() ? "permissions" : group + ".permissions";
                }));

    // Register the task
    var generateTask =
        project
            .getTasks()
            .register(
                TASK_NAME,
                SpicegenTask.class,
                task -> {
                  task.setGroup("build");
                  task.setDescription("Generates type-safe SpiceDB client code from schema");

                  task.getSchemaFile().set(extension.getSchemaFile());
                  task.getOutputDirectory().set(extension.getOutputDirectory());
                  task.getPackageName().set(extension.getPackageName());
                });

    // Register the task output as a source directory. Using the task provider (instead of the
    // raw output directory) gives every consumer of the main Java sources, including
    // compileKotlin, sourcesJar and IDE sync, an implicit dependency on the generator task.
    var javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
    javaExtension
        .getSourceSets()
        .named(SourceSet.MAIN_SOURCE_SET_NAME)
        .configure(
            main -> main.getJava().srcDir(generateTask.flatMap(SpicegenTask::getOutputDirectory)));
  }
}
