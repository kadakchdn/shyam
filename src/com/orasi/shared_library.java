/**
 * @version 2
 *
 */
 package com.orasi;
 
 import java.util.*;
 import java.util.function.*;
 import java.io.*;
 import java.lang.reflect.*;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.openqa.selenium.*;
 import org.openqa.selenium.interactions.*;
 import org.openqa.selenium.remote.*;
 import org.openqa.selenium.support.ui.*;
 import java.util.regex.*;
 import com.orasi.datasource.*;
 import com.orasi.alchemy.mediation.execution.*;
 import java.util.concurrent.atomic.AtomicInteger;
 import com.orasi.event.*;
 import com.orasi.event.chain.*;
 import com.orasi.event.handler.EventHandler;
 import com.orasi.model.*;
 import java.time.*;
 
 public class shared_library
 {
    private static Logger log = LoggerFactory.getLogger(shared_library.class );
 
    private static final Pattern CONTEXT_REGEX = Pattern.compile( "\\$\\{([^}]*)\\}" );
 

    private static final AtomicInteger testCounter = new AtomicInteger();
    private static final AtomicInteger stepCounter = new AtomicInteger();

    public static final int getTestCounter() {
      return testCounter.incrementAndGet();
    }

    public static final int getStepCounter() {
      return stepCounter.incrementAndGet();
    }
 

 private static EventChain eventChain = new EventChain();

 public static void addEventHandler( EventHandler eventHandler ) {
   eventHandler.setEventChain(eventChain);
   eventChain.addEventHandler(eventHandler);
 }

 public static void notifyListeners( Event e ) {
   
   eventChain.handle( e );
 }

  private static List<Throwable> getThrowableList(Throwable throwable) {
    final List<Throwable> list = new ArrayList<>();
    while (throwable != null && !list.contains(throwable)) {
      list.add(throwable);
      throwable = throwable.getCause();
    }
    return list;
  }

  private static Throwable getRootCause(final Throwable throwable) {
    final List<Throwable> list = getThrowableList(throwable);
    return list.isEmpty() ? throwable : list.get(list.size() - 1);
  }

 
 
 
 public static void Click_v7( int executionId, int stepIdentifier, int testExecutionId, WebDriver webDriver, Map<String,Object> variableMap, Map<String,Object> contextMap, String contextName, Stack<String> callStack, Stack<Integer> stepStack )
 {
 
   By targetLocator = (By) variableMap.get( "targetLocator" );
 
   String method = (String) variableMap.get( "method" );
 
   Long x = null;
   Number _x = (Number) variableMap.get( "x" );
   if ( _x != null ) x = _x.longValue();
 
   Long y = null;
   Number _y = (Number) variableMap.get( "y" );
   if ( _y != null ) y = _y.longValue();
 

 

    if (method != null && !method.isEmpty()) {

      try {
        switch (method) {
          case "Default":
            try {
              WebDriverWait wait =
                  new WebDriverWait(webDriver, Duration.ofSeconds(8), Duration.ofMillis(250));
              wait.until((_wD) -> ExpectedConditions.elementToBeClickable(targetLocator).apply(_wD))
                  .click();
            } catch (Exception e) {
              try {
                if (webDriver instanceof JavascriptExecutor) {
                  WebElement wE = webDriver.findElement(targetLocator);
                  ((JavascriptExecutor) webDriver).executeScript("arguments[ 0 ].click();", wE);
                }
              } catch (Exception e2) {
                throw e;
              }
            }
            break;
          case "No Wait":
            try {
              webDriver.findElement(targetLocator).click();
            } catch (Exception e) {
              try {
                if (webDriver instanceof JavascriptExecutor) {
                  WebElement wE = webDriver.findElement(targetLocator);
                  ((JavascriptExecutor) webDriver).executeScript("arguments[ 0 ].click();", wE);
                }
              } catch (Exception e2) {
                throw e;
              }
            }
            break;
          case "Simple":
            webDriver.findElement(targetLocator).click();
            break;
          case "Right":
            Actions rC = new Actions(webDriver);
            rC.contextClick(webDriver.findElement(targetLocator)).perform();
            break;
          case "Double":
            Actions dC = new Actions(webDriver);
            dC.doubleClick(webDriver.findElement(targetLocator)).perform();
            break;
          case "Offset":
            Actions oC = new Actions(webDriver);
            oC.moveToElement(webDriver.findElement(targetLocator)).moveByOffset( x.intValue(), y.intValue() ).perform();
            break;
          case "Absolute":
            Actions aC = new Actions(webDriver);
            aC.moveToLocation( x.intValue(), y.intValue() ).perform();
            break;
          default:
            throw new IllegalArgumentException("Invalid method specified as " + method);
        }
      } catch (Exception e) {
        throw new IllegalArgumentException("Could not locate element defined by targetLocator");
      }

    } else {
      throw new IllegalArgumentException("The METHOD parameter must be specified");
    }

    
 if ( variableMap != null ) {
   variableMap.clear();
 }
}

 
 public static void Navigate_v2( int executionId, int stepIdentifier, int testExecutionId, WebDriver webDriver, Map<String,Object> variableMap, Map<String,Object> contextMap, String contextName, Stack<String> callStack, Stack<Integer> stepStack )
 {
 
   String url = (String) variableMap.get( "url" );
 
   Long timeout = null;
   Number _timeout = (Number) variableMap.get( "timeout" );
   if ( _timeout != null ) timeout = _timeout.longValue();
 

 webDriver.manage().timeouts().pageLoadTimeout( timeout.longValue(), java.util.concurrent.TimeUnit.MILLISECONDS ); webDriver.get( url ); 
 if ( variableMap != null ) {
   variableMap.clear();
 }
}

 /**
 * This version implemented Maximize, Minimize, Full Screen, Resize and Close
 */
 public static void Browser_v2( int executionId, int stepIdentifier, int testExecutionId, WebDriver webDriver, Map<String,Object> variableMap, Map<String,Object> contextMap, String contextName, Stack<String> callStack, Stack<Integer> stepStack )
 {
 
   String action = (String) variableMap.get( "action" );
 
   Long height = null;
   Number _height = (Number) variableMap.get( "height" );
   if ( _height != null ) height = _height.longValue();
 
   Long width = null;
   Number _width = (Number) variableMap.get( "width" );
   if ( _width != null ) width = _width.longValue();
 

 switch( action ) {
  case "Back":
    webDriver.navigate().back();
    break;
  case "Forward":
    webDriver.navigate().forward();
    break;
  case "Refresh":
    webDriver.navigate().refresh();
    break;
  case "Maximize":
    webDriver.manage().window().maximize();
    break;
  case "Minimize":
    webDriver.manage().window().minimize();
    break;
  case "Full Screen":
    webDriver.manage().window().fullscreen();
    break;
  case "Resize":
    webDriver.manage().window().setSize( new Dimension( width.intValue(), height.intValue() ) );
    break;
  case "Close":
   String windowHandle = webDriver.getWindowHandle();
   String baseWindowHandle = webDriver.getWindowHandles().toArray()[ 0 ] + "";
   
   if ( !windowHandle.equals(baseWindowHandle) ) {
     webDriver.switchTo().window( windowHandle );
     webDriver.close();
     webDriver.switchTo().window( baseWindowHandle );
   } else {
     throw new IllegalArgumentException( "You can not close the default window" );
   }
}

if ( contextName != null ) {
  contextMap.put( contextName, webDriver.getCurrentUrl() );
}
 if ( variableMap != null ) {
   variableMap.clear();
 }
}

 /**
 * This version will check after the clear to verify that the input is empty.  If not, it will send backspace keys for each character in the input
 */
 public static void Type_v2( int executionId, int stepIdentifier, int testExecutionId, WebDriver webDriver, Map<String,Object> variableMap, Map<String,Object> contextMap, String contextName, Stack<String> callStack, Stack<Integer> stepStack )
 {
 
   String value = (String) variableMap.get( "value" );
 
   Boolean clearFirst = Boolean.parseBoolean( variableMap.get( "clearFirst" ) + "" );
 
   By targetLocator = (By) variableMap.get( "targetLocator" );
 

 WebElement targetElement;
targetElement = webDriver.findElement(targetLocator);

if ( targetElement == null ) {
  throw new IllegalArgumentException( "Unable to locate " + targetLocator );
}

if ( clearFirst ) {   
  targetElement.clear(); 
  String text = targetElement.getAttribute( "value" );
  if ( text != null ) {
    for ( int i=0; i<text.length(); i++ ) {
      targetElement.sendKeys(Keys.BACK_SPACE);
    } 
  }
}  

targetElement.sendKeys( value );


 if ( variableMap != null ) {
   variableMap.clear();
 }
}


 }
