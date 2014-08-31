package com.darylteo.gradle.javassist.transformers;

import groovy.lang.Closure;
import javassist.CtClass;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;

/**
 * Created by dteo on 28/05/2014.
 */
public class GroovyClassTransformation implements IClassTransformer {
  private Closure transform;
  private Closure filter;

  public GroovyClassTransformation(Closure transform) {
    this.transform = transform;
    this.filter = null;
  }

  public GroovyClassTransformation(Closure transform, Closure filter) {
    this.transform = transform;
    this.filter = filter;
  }

  @Override
  public void applyTransformations(CtClass clazz) {
    this.transform.call(clazz);
  }

  @Override
  public boolean shouldTransform(CtClass clazz) throws JavassistBuildException {
    return this.filter == null || (Boolean) this.filter.call(clazz);
  }

  public void transform(Closure transform) {
    this.transform = transform;
  }

  public void where(Closure filter) {
    this.filter = filter;
  }
}
