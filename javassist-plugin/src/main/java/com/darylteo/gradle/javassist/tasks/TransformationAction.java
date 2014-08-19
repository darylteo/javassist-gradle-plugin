package com.darylteo.gradle.javassist.tasks;

import com.darylteo.gradle.javassist.transformers.ClassTransformer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.gradle.api.GradleException;
import org.gradle.api.internal.file.CopyActionProcessingStreamAction;
import org.gradle.api.internal.file.copy.CopyAction;
import org.gradle.api.internal.file.copy.CopyActionProcessingStream;
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal;
import org.gradle.api.internal.tasks.SimpleWorkResult;
import org.gradle.api.tasks.WorkResult;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dteo on 30/05/2014.
 */
class TransformationAction implements CopyAction {

  private File destinationDir;

  private ClassTransformer transformation;

  private List<File> sources = new LinkedList<File>();

  private List<File> classpath = new LinkedList<File>();

  public TransformationAction(File destinationDir, Collection<File> sources, Collection<File> classpath, ClassTransformer transformation) {
    this.destinationDir = destinationDir;
    this.sources.addAll(sources);
    this.classpath.addAll(classpath);

    this.transformation = transformation;
  }

  @Override
  public WorkResult execute(CopyActionProcessingStream stream) {
    try {
      final ClassPool pool = createPool(this.sources);
      final LoaderAction action = new LoaderAction(pool, destinationDir, this.transformation);

      stream.process(action);
    } catch (Exception e) {
      throw new GradleException("Could not execute transformation", e);
    }

    return new SimpleWorkResult(true);
  }

  private ClassPool createPool(List<File> classpath) throws NotFoundException {
    final ClassPool pool = new ClassPool(true);

    // set up the classpath for the classpool
    for (File f : this.classpath) {
      pool.appendClassPath(f.toString());
    }

    //add files to transform to the classpool
    for (File f : this.sources) {
      pool.appendClassPath(f.toString());
    }

    return pool;
  }

  // preloads all class files into the classpool and stores a list of class names
  private class LoaderAction implements CopyActionProcessingStreamAction {
    private final ClassPool pool;

    private ClassTransformer transformation;

    private final String destinationDir;

    public LoaderAction(ClassPool pool, File destinationDir, ClassTransformer transformation) {
      this.pool = pool;
      this.destinationDir = destinationDir.toString();

      this.transformation = transformation;
    }

    @Override
    public void processFile(FileCopyDetailsInternal details) {
      try {
        if (!details.isDirectory()) {
          CtClass clazz = loadClassFile(details.getFile());

          if (this.transformation.shouldTransform(clazz)) {
            clazz.defrost();
            this.transformation.applyTransformations(clazz);
            clazz.writeFile(this.destinationDir);
          }
        }
      } catch (Exception e) {
        throw new GradleException("An error occurred while trying to process class file ", e);
      }
    }

    private CtClass loadClassFile(File classFile) throws IOException {
      // read the file first to get the classname
      // much easier than trying to extrapolate from the filename (i.e. with anonymous classes etc.)
      InputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(classFile)));
      CtClass clazz = pool.makeClass(stream);

      stream.close();

      return clazz;
    }
  }
}
