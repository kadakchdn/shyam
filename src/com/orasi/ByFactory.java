package com.orasi;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.openqa.selenium.By;
import com.orasi.datasource.*;

/**
 * A class used to construct a locator compatible with Selenium that has been
 * scanned and processed replacing and alchemy specific replaceable values such
 * as data, context, etc
 */
public class ByFactory {

  private final Class byClass;
  private final String descriptor;
  private final String name;
  private final String alchemyId;
  private final String shadowHost;

  /**
   *
   * @param byClass A class compatible with By
   * @param descriptor The textual descriptor definition
   * @param name A friendly name for this locator
   * @param alchemyId A unique identifier for this locator
   */
  public ByFactory(Class byClass, String descriptor, String name, String alchemyId) {
    this(byClass, descriptor, name, alchemyId, null);
  }

  /**
   *
   * @param byClass A class compatible with By
   * @param descriptor The textual descriptor definition
   * @param name A friendly name for this locator
   * @param alchemyId A unique identifier for this locator
   * @param shadowHost An optional parameter instructing this locator is relative to a shadow root
   */
  public ByFactory(Class byClass, String descriptor, String name, String alchemyId, String shadowHost) {
    this.byClass = byClass;
    this.descriptor = descriptor;
    this.name = name;
    this.alchemyId = alchemyId;
    this.shadowHost = shadowHost;
  }

  /**
   * Given the current context map and a data source provider, this generates a
   * locator
   *
   * @param contextMap
   * @param dM
   * @return
   */
  public By create(Map<String, Object> contextMap, DataSourceProvider dM) {

    By shadowBy = null;

    if (shadowHost != null && !shadowHost.trim().isEmpty()) {
      shadowBy = ObjectManager.instance().getObject(shadowHost, contextMap, dM);
    }

    String useDescriptor = dM.replaceValues(descriptor, contextMap) + "";
    By useBy = null;

    try {
      useBy = (By) byClass.getConstructor(String.class).newInstance(useDescriptor);
    } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
      throw new IllegalArgumentException("Could not create locator for " + useDescriptor + " as " + byClass.getName());
    }

    if (shadowBy != null) {
      return new ByChain(name, alchemyId, shadowBy, useBy );
    } else {
      return useBy;
    }

  }

  /**
   *
   * @return
   */
  public String getDescriptor() {
    return descriptor;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the alchemyId
   */
  public String getAlchemyId() {
    return alchemyId;
  }

  /**
   * @return the shadowHost
   */
  public String getShadowHost() {
    return shadowHost;
  }
}
