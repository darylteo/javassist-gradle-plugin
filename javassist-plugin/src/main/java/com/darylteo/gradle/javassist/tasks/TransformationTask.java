package com.darylteo.gradle.javassist.tasks;

import com.darylteo.gradle.javassist.transformers.ClassTransformer;
import com.darylteo.gradle.javassist.transformers.GroovyClassTransformation;
import groovy.lang.Closure;
import org.gradle.api.internal.file.copy.CopyAction;
import org.gradle.api.internal.file.copy.CopyActionProcessingStream;
import org.gradle.api.internal.tasks.SimpleWorkResult;
import org.gradle.api.tasks.AbstractCopyTask;
import org.gradle.api.tasks.WorkResult;

public class TransformationTask extends AbstractCopyTask {

  private String destinationDir;

  public String getDestinationDir() {
    return destinationDir;
  }

  public void setDestinationDir(String destinationDir) {
    this.destinationDir = destinationDir;
  }

  private ClassTransformer transformation;

  public ClassTransformer getTransformation() {
    return transformation;
  }

  public void setTransformation(ClassTransformer transformation) {
    this.transformation = transformation;
  }

  public ClassTransformer transform(Closure closure) {
    this.transformation = new GroovyClassTransformation(closure);
    return this.transformation;
  }

  public ClassTransformer where(Closure closure) {
    this.transformation = new GroovyClassTransformation(null, closure);
    return this.transformation;
  }

  public TransformationTask() {
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

    String dir = this.destinationDir;
    if (dir == null) {
      dir = String.format("%s/transformations/%s", this.getProject().getBuildDir(), this.getName());
    }

    return new TransformationAction(dir, this.getSource().getFiles(), this.transformation);
  }

}
