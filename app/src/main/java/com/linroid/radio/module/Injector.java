package com.linroid.radio.module;

import java.util.List;

import dagger.ObjectGraph;

public abstract interface Injector{
  public abstract List<Object> getModules();

  public abstract void inject(Object target);

  public abstract ObjectGraph plus(Object[] modules);
}