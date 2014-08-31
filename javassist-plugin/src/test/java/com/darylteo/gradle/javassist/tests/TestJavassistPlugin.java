package com.darylteo.gradle.javassist.tests;

import com.darylteo.gradle.javassist.tasks.TransformationTask;
import javassist.CtClass;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by dteo on 30/08/2014.
 */
public class TestJavassistPlugin {

  private Project project;
  private TransformationTask task;

  @Before
  public void setup() {
    this.project = ProjectBuilder.builder().withProjectDir(new File("test-project")).build();
    this.project.getPlugins().apply(JavaPlugin.class);

    System.out.println(this.project.getProjectDir());

    ((DefaultTask) project.getTasks().getByPath("clean")).execute();
    ((DefaultTask) project.getTasks().getByPath("compileJava")).execute();

    this.task = project.getTasks().create("transform", TransformationTask.class);

  }

  @Test
  public void testNoConfigTask() {
    this.task.execute();
  }

  @Test
  public void testNoTransformerTask() {
    String srcDir = "build/classes";

    this.task.from(this.project.file(srcDir));
    this.task.execute();

    assert this.task.getDidWork() == false;
  }


  @Test
  public void testBasicTaskWithBuildApi() throws Exception {
    IClassTransformer transformer = new BasicTransformer();
    String destDir = "build/transformations/transform";
    String srcDir = "build/classes";

    this.task.from(this.project.file(srcDir));
    this.task.setTransformation(transformer);

    this.task.execute();


    assert this.project.file(destDir).exists();
    assert this.project.file(destDir + "/TransformedSomeClass.class").exists();

    URLClassLoader loader = new URLClassLoader(new URL[]{
      this.project.file(destDir).toURI().toURL()
    });

    Class<?> clazz = loader.loadClass("TransformedSomeClass");
    System.out.println(clazz.getName());
  }

  private class BasicTransformer implements IClassTransformer {
    @Override
    public void applyTransformations(CtClass ctClass) throws JavassistBuildException {
      String name = ctClass.getName();
      name = name.substring(0, 1).toUpperCase() + name.substring(1);

      ctClass.setName("Transformed" + name);
    }

    @Override
    public boolean shouldTransform(CtClass ctClass) throws JavassistBuildException {
      return true;
    }
  }
}
