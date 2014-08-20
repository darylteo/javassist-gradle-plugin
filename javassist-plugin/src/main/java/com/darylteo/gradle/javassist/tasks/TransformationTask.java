package com.darylteo.gradle.javassist.tasks;

import com.darylteo.gradle.javassist.transformers.GroovyClassTransformation;
import groovy.lang.Closure;
import java.io.File;
import javassist.build.IClassTransformer;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.copy.CopyAction;
import org.gradle.api.internal.file.copy.CopyActionProcessingStream;
import org.gradle.api.internal.tasks.SimpleWorkResult;
import org.gradle.api.tasks.AbstractCopyTask;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.WorkResult;

import java.io.File;
import java.nio.file.Paths;

public class TransformationTask extends AbstractCopyTask {

  private File destinationDir;

  @OutputDirectory
  public File getDestinationDir() {
    return destinationDir;
  }

  public void setDestinationDir(File destinationDir) {
    this.destinationDir = destinationDir;
  }

  private IClassTransformer transformation;

  public IClassTransformer getTransformation() {
    return transformation;
  }

  public void setTransformation(IClassTransformer transformation) {
    this.transformation = transformation;
  }

  private FileCollection classpath;

  @InputFiles
  public FileCollection getClasspath() {
    return this.classpath;
  }

  public void setClasspath(FileCollection classpath) {
    this.classpath = classpath;
  }

  public IClassTransformer transform(Closure closure) {
    this.transformation = new GroovyClassTransformation(closure);
    return this.transformation;
  }

  public IClassTransformer where(Closure closure) {
    this.transformation = new GroovyClassTransformation(null, closure);
    return this.transformation;
  }

  public TransformationTask() {
    // empty classpath
    this.classpath = this.getProject().files();
    this.destinationDir = Paths.get(this.getProject().getBuildDir().toString(), "transformations", this.getName()).toFile();
  }

  @Override
  protected CopyAction createCopyAction() {
    // no op if no transformation defined
    if (this.transformation == null) {
      return new CopyAction() {
        @Override
        public WorkResult execute(CopyActionProcessingStream copyActionProcessingStream) {
          System.out.println("No transformation defined for this task");
          return new SimpleWorkResult(false);
        }
      };
    }

    return new TransformationAction(this.destinationDir, this.getSource().getFiles(), this.classpath.getFiles(), this.transformation);
  }

}
