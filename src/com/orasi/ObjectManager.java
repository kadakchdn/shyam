
package com.orasi;

import java.util.*;
import org.openqa.selenium.By;
import org.openqa.selenium.By.*;
import org.openqa.selenium.*;
import com.orasi.datasource.*;


public class ObjectManager {
  private static final ObjectManager singleton = new ObjectManager();
  
  public static final ObjectManager instance() {
    return singleton;
  }
  
  private final Map<String,ByFactory> objectMap = new HashMap<>( 10 );
  
  private ObjectManager() {
    ByFactoryCollection bC = null;
    /*
    Site: omayo.blogspot.com
    Add a description of omayo.blogspot.com
    */
    /* Page: omayo (QAFox.com) 
    
    */

    

bC = new ByFactoryCollection("INPUT", "31358.3128", "");
bC.add( new ByFactory( ByXPath.class, "//form[@name=\"form1\"]/input[1]", "ParentRule[NameRule]", "31358.3130", "" ) );
bC.add( new ByFactory( ByXPath.class, "//*[@name=\"form1\"]/input[1]", "ParentRule[NameRule]", "31358.3132", "" ) );
bC.add( new ByFactory( ByXPath.class, "(//input[@type='text'])[1]", "IndexedAttributeRule", "31358.3134", "" ) );
bC.add( new ByFactory( ByXPath.class, "/body[1]/div[4]/div[2]/div[2]/div[2]/div[2]/div[2]/div[2]/div[1]/div[4]/div[1]/div[1]/div[1]/div[5]/div[1]/form[1]/input[1]", "AbsoluteRule", "31358.3136", "" ) );


objectMap.put( "31358.3128", bC );


bC = new ByFactoryCollection("INPUT1", "31358.3140", "");
bC.add( new ByFactory( ByXPath.class, "//form[@name=\"form1\"]/input[2]", "ParentRule[NameRule]", "31358.3142", "" ) );
bC.add( new ByFactory( ByXPath.class, "//*[@name=\"form1\"]/input[2]", "ParentRule[NameRule]", "31358.3144", "" ) );
bC.add( new ByFactory( ByXPath.class, "(//input[@type='text'])[1]/following-sibling::input[1]", "SiblingRule", "31358.3146", "" ) );
bC.add( new ByFactory( ByXPath.class, "(//input[@type='password'])[1]", "IndexedAttributeRule", "31358.3148", "" ) );
bC.add( new ByFactory( ByXPath.class, "/body[1]/div[4]/div[2]/div[2]/div[2]/div[2]/div[2]/div[2]/div[1]/div[4]/div[1]/div[1]/div[1]/div[5]/div[1]/form[1]/input[2]", "AbsoluteRule", "31358.3150", "" ) );


objectMap.put( "31358.3140", bC );


bC = new ByFactoryCollection("LogIn", "31358.3154", "");
bC.add( new ByFactory( ByXPath.class, "//button[@value='LogIn']", "AttributeRule", "31358.3156", "" ) );
bC.add( new ByFactory( ByXPath.class, "//*[@value='LogIn']", "AttributeRule", "31358.3158", "" ) );
bC.add( new ByFactory( ByXPath.class, "//form[@name=\"form1\"]/button[1]", "ParentRule[NameRule]", "31358.3160", "" ) );
bC.add( new ByFactory( ByXPath.class, "//*[@name=\"form1\"]/button[1]", "ParentRule[NameRule]", "31358.3162", "" ) );
bC.add( new ByFactory( ByXPath.class, "//button[contains( text(), \"LogIn\")][1]", "TextRule", "31358.3164", "" ) );
bC.add( new ByFactory( ByXPath.class, "(//input[@type='password'])[1]/following-sibling::button[1]", "SiblingRule", "31358.3166", "" ) );
bC.add( new ByFactory( ByXPath.class, "(//button[@type='button'])[1]", "IndexedAttributeRule", "31358.3168", "" ) );
bC.add( new ByFactory( ByXPath.class, "/body[1]/div[4]/div[2]/div[2]/div[2]/div[2]/div[2]/div[2]/div[1]/div[4]/div[1]/div[1]/div[1]/div[5]/div[1]/form[1]/button[1]", "AbsoluteRule", "31358.3170", "" ) );


objectMap.put( "31358.3154", bC );




  }
  
  public By getObject( Object alchemyIdentifier, Map<String,Object> contextMap, DataSourceProvider dM ) {
    
    if ( alchemyIdentifier instanceof By ) {
      return (By) alchemyIdentifier;
    }
    
    ByFactory by = objectMap.get( alchemyIdentifier + "" );
    if ( by == null ) {
      return new By() {
        @Override
        public List<WebElement> findElements(SearchContext sc) {
          throw new RuntimeException( "Could not find and object using [" + alchemyIdentifier + "]" );
        }
      };
    }
    return by.create(contextMap, dM);
  }

  public ByFactory getObject( String alchemyIdentifier ) {
   
    return objectMap.get( alchemyIdentifier );
  }
}
