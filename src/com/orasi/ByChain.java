package com.orasi;

import java.io.Serializable;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 * The By chain provides an order list of locators by each executing in succession if they parent 
 * contained a shadow root
 *
 */
public class ByChain extends By implements Serializable, ByIdentifier, ByTimer {

  private static final long serialVersionUID = 4573668832699497306L;

  private final By[] byChain;
  private final String name;
  private final String alchemyId;
  private long startTime;
  private long searchTime;

  public ByChain(String name, String alchemyId, By... byChain) {
    this.name = name;
    this.alchemyId = alchemyId;
    this.byChain = byChain;
  }

  /**
   * @see org.openqa.selenium.By
   * @param context
   * @return
   */
  @Override
  public WebElement findElement(SearchContext context) {

    if (byChain == null && byChain.length < 1) {
      return null;
    }

    WebElement currentElement = null;

    for (By by : byChain) {
      if (currentElement == null) {
        currentElement = by.findElement(context);
      } else {

        try {
          currentElement = currentElement.getShadowRoot().findElement(by);
        } catch (Exception e) {
          throw new IllegalArgumentException("Could not locate a shadow root!  Was this shadow root closed?");
        }

      }
    }

    return currentElement;
  }

  /**
   * @see org.openqa.selenium.By
   * @param context
   * @return
   */
  @Override
  public List<WebElement> findElements(SearchContext context) {
    if (byChain == null && byChain.length < 1) {
      return null;
    }

    List<WebElement> elementList = null;

    for (By by : byChain) {
      if (elementList == null) {
        elementList = by.findElements(context);
      } else {

        if (!elementList.isEmpty()) {
          try {
            elementList = elementList.get(0).getShadowRoot().findElements(by);
          } catch (Exception e) {
            throw new IllegalArgumentException("Could not locate a shadow root!  Was this shadow root closed?");
          }

        } else {
          throw new java.util.NoSuchElementException("Cannot locate an element using " + by.toString() + " in chain " + toString());
        }
      }
    }

    return elementList;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("By.chain(");
    stringBuilder.append("{");

    boolean first = true;
    for (By by : byChain) {
      stringBuilder.append((first ? "" : ",")).append(by);
      first = false;
    }
    stringBuilder.append("})");
    return stringBuilder.toString();
  }

  /**
   * @return the name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * @return the alchemyId
   */
  @Override
  public String getAlchemyId() {
    return alchemyId;
  }

  /**
   * @return the startTime
   */
  @Override
  public long getStartTime() {
    return startTime;
  }

  /**
   * @return the searchTime
   */
  @Override
  public long getSearchTime() {
    return searchTime;
  }
}
