package com.darylteo.gradle.javassist.transformers;

import javassist.CtClass;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;

/**
 * Created by dteo on 28/05/2014.
 */
public class ClassTransformer implements IClassTransformer {
  @Override
  public void applyTransformations(CtClass clazz) throws JavassistBuildException {
    return;
  }

  @Override
  public boolean shouldTransform(CtClass clazz) throws JavassistBuildException {
    return true;
  }
}
