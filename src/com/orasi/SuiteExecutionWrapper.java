/**
 * @version 2
 */
  
package com.orasi;
  
import java.util.*;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.orasi.event.spi.*;
import static com.orasi.shared_library.*;
import com.orasi.integration.*;
import com.orasi.event.Event;
import com.orasi.event.action.EventAction;
import com.orasi.event.handler.AbstractEventHandler;
import com.orasi.event.hook.EventHook;
import com.orasi.model.*;
import org.apache.commons.cli.*;
import com.orasi.datasource.*;
import java.io.InputStream;
import java.util.logging.LogManager;
import com.google.gson.Gson;

  
  
public class SuiteExecutionWrapper {

  static {
    try ( InputStream iS = SuiteExecutionWrapper.class.getClassLoader().getResourceAsStream( "logging.properties") ) {
      LogManager.getLogManager().readConfiguration(iS);
    } catch( Exception e ) {
      e.printStackTrace();
    }
  }

  private static final Random numberGenerator = new Random();
  public static final String generateCharacters(int characterCount) {
    StringBuilder sB = new StringBuilder();
    for (int i = 0; i < characterCount; i++) {
      int rV = numberGenerator.nextInt(26);

      rV += numberGenerator.nextBoolean() ? 65 : 97;

      sB.append((char) rV);

    }

    return sB.toString();
  }

  private static int failureCount = 0;
  private static int failureLevel = 0;

  private static class TestFailureHandler extends AbstractEventHandler<TestPayload> {

      public TestFailureHandler() {
        super( EventAction.ALL, EventHook.FAILURE.getId(), TestEvent.EVENT_TYPE );
      }
    
      @Override
      protected void _handleEvent(Event<TestPayload> e) {
        if ( e.getPayload().getFailureType() > failureLevel ) {
          failureLevel = e.getPayload().getFailureType();
        }
        failureCount++;
      }

    }
  
    private final Options cliOptions = new Options();
  private static CommandLine cli;
  private int threadCount = 30;
  private static final Logger log = LoggerFactory.getLogger(SuiteExecutionWrapper.class);
  private static final SuiteExecutionWrapper singleton = new SuiteExecutionWrapper();
  private int executionId;
  private int checkFailureLevel = 2;

  private String name;
  private String description;
  private int id;
  private String userName;
  private List<String> testList;
  private List<String> targetList;
  private List<Router> routerList = new ArrayList<>(5);

  private static final Map<String, Map<String, String>> environmentMap = new HashMap<>(10);
  
    private SuiteExecutionWrapper() {
  
    }

    public CommandLine getCommandLine() {
      return cli;
    }

    public Options getOptions() {
      return cliOptions;
    }

    public void addOptions(Option[] options) {
      for (Option option : options) {
        cliOptions.addOption(option);
      }
    }

    public String getOption( String name, String defaultValue ) {
      if ( cli.hasOption(name) ) {
        if ( SuiteExecutionWrapper.instance().getOptions().getOption(name).hasArg() ) {
          return cli.getOptionValue(name);
        } else {
          return "true";
        }
      } else {
        return defaultValue;
      }
    }
  
    public String getOption( String key, String name, String defaultValue ) {
      String keyName = key + "_" + name;
      if ( cli.hasOption(keyName) ) {
        if ( SuiteExecutionWrapper.instance().getOptions().getOption(keyName).hasArg() ) {
          return cli.getOptionValue(keyName);
        } else {
          return "true";
        }
      } else {
        return defaultValue;
      }
    }

  private static boolean hasValue(String option, String value) {
    String[] oV = cli.getOptionValues(option);
    if (oV != null) {
      for (String v : oV) {
        if (v.equals(value)) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
    
  }

   
    public static void main(String[] args) {

            System.out.println( "                                                                                \n" +
"                                                                                \n" +
"                                                                                \n" +
"                                                                        &##     \n" +
"                                                               &######&  %%     \n" +
"                              &##################%              &&#& &%#& ####% \n" +
"                             &#####################&         %#% %#######& %#   \n" +
"                            ########################&      #####%##&  %####%    \n" +
"                          &###########################    %##########%%###%     \n" +
"                         ##############################&%##%  #######%          \n" +
"                       &#####################################%#####%%##%        \n" +
"                      #################  &#####################%%#####          \n" +
"                    &################      ###################%  ####           \n" +
"                   ################%         ######################             \n" +
"                  ################%           ####################              \n" +
"                %################&             &################&               \n" +
"               ###################            %###################              \n" +
"             &#####################%         ######################             \n" +
"           &#########################      &########################%           \n" +
"           ###########################%   ############################&         \n" +
"         ############################### ##############################         \n" +
"        ################&##############################&#################       \n" +
"      #################   &###########################&  &################      \n" +
"    &################      &########################%      ################%    \n" +
"   &################         %#####################&        &################   \n" +
"  ################&           %###################            ################% \n" +
" ################&              %%%%%%%%%%%%%%%%&              &################");
      
      System.out.println( "\n\nAlchemy by Orasi");
      System.out.println( "Copyright Orasi, Inc. | All Rights Reserved.\n\n");

      DataSourceProviderFactory.instance().setDataSourceProvider( DataManager.instance() );

      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("h", "help", false, "Print this message"));
      Option o = new Option("t", "tags", true, "A space separated list of test tags.  Each test that contains any of the supplied tags will be executed.");
      o.setArgs(Option.UNLIMITED_VALUES);
      SuiteExecutionWrapper.instance().getOptions().addOption(o);

      o = new Option("it", "includetests", true, "A space separated list of test identifiers that will be executed");
      o.setArgs(Option.UNLIMITED_VALUES);
      SuiteExecutionWrapper.instance().getOptions().addOption(o);

      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("e", "environment", true, "The environment variable set to use during execution"));
      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("p", "threadcount", true, "The maximum amount of threads to run in parellel"));
      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("n", "noreturn", false, "Allow the process to complete without forcing it to exit with a return code"));
 
      o = new Option("ir", "includerouters", true, "A space separated list of router identifiers.  Only targets belonging to these routers will run");
      o.setArgs(Option.UNLIMITED_VALUES);
      SuiteExecutionWrapper.instance().getOptions().addOption(o);

      o = new Option("iw", "includeintegrations", true, "A space separated list of integration identifiers to use");
      o.setArgs(Option.UNLIMITED_VALUES);
      SuiteExecutionWrapper.instance().getOptions().addOption(o);

      o = new Option("ie", "includetargets", true, "A space separated list of execution target identifiers.  Only the names targets will run");
      o.setArgs(Option.UNLIMITED_VALUES);
      SuiteExecutionWrapper.instance().getOptions().addOption(o);
      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("a", "name", true, "Override the default name of this test suite"));
      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("u", "user", true, "Override the name of the executing user"));
      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("d", "description", true, "Override the description of this test suite"));
      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("q", "queryids", false, "List the ID's of all elements that make up this suite"));
      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("rd", "rundevelopment", false, "Indicates that tests in the development state should run"));
      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("rq", "runquarantine", false, "Indicates that tests in the quarantine state should run"));
      SuiteExecutionWrapper.instance().getOptions().addOption(new Option("f", "failurelevel", true, "What error level fails a test suite.  1: Infrastructure, 2: Automation Failure (default), 3: Application Failure, 4: Unknown Error"));

      SuiteExecutionWrapper.instance().addOptions( new com.orasi.integration.cloud.ExecutionCloud().getOptions() );
      SuiteExecutionWrapper.instance().addOptions( new com.orasi.integration.html.HTMLSerializer().getOptions() );
      SuiteExecutionWrapper.instance().addOptions( new com.orasi.integration.console.ExecutionConsole().getOptions() );
      

      CommandLineParser cP = new DefaultParser();
      try {
        cli = cP.parse(SuiteExecutionWrapper.instance().getOptions(), args);
      } catch( ParseException pE ) {
        System.err.println("Failed to parse command line: " + pE.getMessage());
      }

      // Default Environment Properties 
      DataManager.instance().addDefaultEnvironmentProperty( "url", DataManager.instance().replaceValues( "https://omayo.blogspot.com" ) + "" );



      Map<String,String> envMap;
      //* A production configuration containing a Production URL */
      envMap = new HashMap<>( 10 );
      
      environmentMap.put( "Production", envMap );
//* A staging configuration containing a Staging URL */
      envMap = new HashMap<>( 10 );
      
      environmentMap.put( "Staging", envMap );

      

      if ( cli.hasOption( "e" ) ) {
        envMap = environmentMap.get( cli.getOptionValue( "e" ) );
        if ( envMap == null ) {
          System.err.println( "Specified Environment " + cli.getOptionValue( "e" ) + " does not exist" );
        } else {
          DataManager.instance().addEnvironmentProperties( envMap );
        }
      }

      



      if ( cli.hasOption( "h" ) ) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar zczxc", SuiteExecutionWrapper.instance().getOptions() );
        System.exit( 0 );
      }

      if ( cli.hasOption( "q" ) ) {
        System.out.println( "Suite" );
        System.out.println( "zczxc (Suite): 31358.3110" ); 
        System.out.println( "\r\nTests" );
        System.out.println( "Test1 (Test): 31358.3120" );
        
        System.out.println( "\r\nFunctions" );
        
        
        System.out.println( "\r\nExecution Targets" );
	System.out.println( "zczxc router (Router): 31358.3113" );
	System.out.println( "Chrome (Execution Target): 31358.3114" );
	System.out.println( "Firefox (Execution Target): 31358.3115" );
	System.out.println( "Microsoft Edge (Execution Target): 31358.3116" );
	
        System.out.println( "\r\nSites and Pages" );
        System.out.println( "omayo.blogspot.com (Site): 31358.3122" );
        System.out.println( "omayo (QAFox.com) (Page): 31358.3126" );
        
        System.out.println( "\r\nData Sources" );
        
        System.out.println( "\r\nPlugins" );
        System.out.println( "Alchemy GridWorks (Plugin): 31358.3111" );
        System.out.println( "HTML Generator (Plugin): 31358.3117" );
        System.out.println( "Alchemy Execution Console (Plugin): 31358.3118" );
        
        
        System.out.println( "\r\n\r\nImported Suites" ); 
	
        System.exit( 0 );
      }

      DataSource<String,DataTable> _dS = null;
      DataTable<String,DataSource,DataField,DataRow> _dT = null;
      DataField<String> _dF = null; 
      

      

      if ( cli.hasOption( "a" ) ) {
        SuiteExecutionWrapper.instance().setName(cli.getOptionValue( "a" ));
      } else {
        SuiteExecutionWrapper.instance().setName("zczxc");
      }
      if ( cli.hasOption( "u" ) ) {
        SuiteExecutionWrapper.instance().setUserName(cli.getOptionValue( "u" ));
      } else {
        SuiteExecutionWrapper.instance().setUserName("");
      }
      if ( cli.hasOption( "d" ) ) {
        SuiteExecutionWrapper.instance().setDescription(cli.getOptionValue( "d" ));
      } else {
        SuiteExecutionWrapper.instance().setDescription("No description was added for zczxc");
      }

      //
      // Configure the test level information
      //
      List<String> testList = new ArrayList(5);
      if ( hasValue( "it", "31358.3120" ) ) {
        if ( 0 == 0 || ( 0 == 1 && cli.hasOption( "rd" ) ) || ( 0 == 2 && cli.hasOption( "rq" ) ) ) {
          TestManager.instance().registerTest( new org.org_1.zczxc.test1() );
          testList.add("Test1");
        }
      }
      
      

      
      



      
      //
      // Register the execution routers here
      //
      Router r;
      /*
      Routers from zczxc Execution Package
      Add a description of zczxc Execution Package
      */
      if ( hasValue( "ir", "31358.3113" ) ) { 
        r = new Router( 8, "zczxc router", "31358.3113" ,"{\"name\":\"zczxc router\",\"description\":\"Add a description of zczxc router\",\"alchemyId\":3113,\"alchemySeed\":31358,\"organizationId\":0,\"changed\":false,\"routerId\":8,\"status\":0,\"userId\":0,\"propertyList\":[{\"name\":\"URL\",\"value\":\"#{alchemy.cloud.url}\"}],\"createDate\":\"Mar 20, 2025, 1:37:20 PM\",\"modifyDate\":\"Mar 20, 2025, 1:37:20 PM\"}" );
      
        r.addProperty( "URL", DataManager.instance().replaceValues( "#{alchemy.cloud.url}" ) + "" );
        SuiteExecutionWrapper.instance().routerList.add(r);
      }
      
 
      //
      // Configure the endpoint details here
      //
      List<ExecutionTarget> targetList = new ArrayList<>(10);
      ExecutionTarget eT;
      /*
      Targets from zczxc Execution Package
      Add a description of zczxc Execution Package
      */
      if ( hasValue( "ie", "31358.3114" ) ) {
        eT = new ExecutionTarget( "Chrome", "31358.3114", "31358.3113", 2 ,"{\"name\":\"Chrome\",\"description\":\"Add a description of Chrome\",\"alchemyId\":3114,\"alchemySeed\":31358,\"organizationId\":0,\"changed\":false,\"maximumAvailable\":2,\"executionRouterID\":{\"alchemyId\":3113,\"alchemySeed\":31358},\"propertyList\":[{\"name\":\"browserName\",\"value\":\"chrome\"}],\"status\":0,\"userId\":0,\"createDate\":\"Mar 20, 2025, 1:37:20 PM\",\"modifyDate\":\"Mar 20, 2025, 1:37:20 PM\",\"referenceSuiteID\":0}" );
        if ( eT.getMaximumAvailable() > 0 ) {
          eT.addProperty( "browserName", DataManager.instance().replaceValues( "chrome" ) + "" );
          targetList.add(eT);
        }
      }
      if ( hasValue( "ie", "31358.3115" ) ) {
        eT = new ExecutionTarget( "Firefox", "31358.3115", "31358.3113", 2 ,"{\"name\":\"Firefox\",\"description\":\"Add a description of Firefox\",\"alchemyId\":3115,\"alchemySeed\":31358,\"organizationId\":0,\"changed\":false,\"maximumAvailable\":2,\"executionRouterID\":{\"alchemyId\":3113,\"alchemySeed\":31358},\"propertyList\":[{\"name\":\"browserName\",\"value\":\"firefox\"}],\"status\":0,\"userId\":0,\"createDate\":\"Mar 20, 2025, 1:37:20 PM\",\"modifyDate\":\"Mar 20, 2025, 1:37:20 PM\",\"referenceSuiteID\":0}" );
        if ( eT.getMaximumAvailable() > 0 ) {
          eT.addProperty( "browserName", DataManager.instance().replaceValues( "firefox" ) + "" );
          targetList.add(eT);
        }
      }
      if ( hasValue( "ie", "31358.3116" ) ) {
        eT = new ExecutionTarget( "Microsoft Edge", "31358.3116", "31358.3113", 2 ,"{\"name\":\"Microsoft Edge\",\"description\":\"Add a description of Microsoft Edge\",\"alchemyId\":3116,\"alchemySeed\":31358,\"organizationId\":0,\"changed\":false,\"maximumAvailable\":2,\"executionRouterID\":{\"alchemyId\":3113,\"alchemySeed\":31358},\"propertyList\":[{\"name\":\"browserName\",\"value\":\"MicrosoftEdge\"}],\"status\":0,\"userId\":0,\"createDate\":\"Mar 20, 2025, 1:37:20 PM\",\"modifyDate\":\"Mar 20, 2025, 1:37:20 PM\",\"referenceSuiteID\":0}" );
        if ( eT.getMaximumAvailable() > 0 ) {
          eT.addProperty( "browserName", DataManager.instance().replaceValues( "MicrosoftEdge" ) + "" );
          targetList.add(eT);
        }
      }
      

      //
      // This will add all targets to the correct router
      //
      targetList.forEach((t) -> {
        SuiteExecutionWrapper.instance().routerList.forEach((_t) -> {
          _t.addTarget(t);
        });
      });


      if ( cli.hasOption( "t" ) ) {
        TestManager.instance().setTags( cli.getOptionValues( "t" ));
      }

      if (testList.isEmpty() || targetList.isEmpty()) {
        if (testList.isEmpty()) {
          throw new IllegalArgumentException("No tests were added - nothing to do");
        }

        if (targetList.isEmpty()) {
          throw new IllegalArgumentException("No Execution Targets were defined - nowhere to run your tests");
        }
      } else {


      //
      // Configuration Integrations
      //
      Integration cI = null;
      if ( hasValue( "iw", "31358.3111" ) ) { 
        cI = new com.orasi.integration.cloud.ExecutionCloud();
        log.warn( "Enabling Integration: " + cI.getName() );
        cI.setProperty( "cloudUrl", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "cloudUrl", DataManager.instance().replaceValues( "https://alchemytesting.cloud" ) + "" ) );
        cI.setProperty( "apiKey", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "apiKey", DataManager.instance().replaceValues( "6bbd2a68-b5f8-4f8d-b8f2-8da98f87189a" ) + "" ) );
        cI.setProperty( "secretKey", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "secretKey", DataManager.instance().replaceValues( "ff586a18-2d6f-4612-9bdf-3d8ee482ac03" ) + "" ) );
        cI.setProperty( "launchUI", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "launchUI", DataManager.instance().replaceValues( "true" ) + "" ) );
        cI.setProperty( "gridInactivityTimeout", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "gridInactivityTimeout", DataManager.instance().replaceValues( "60" ) + "" ) );
        cI.setProperty( "nodeInactivityTimeout", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "nodeInactivityTimeout", DataManager.instance().replaceValues( "30" ) + "" ) );
        cI.setProperty( "nodeInactivityStartupDelay", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "nodeInactivityStartupDelay", DataManager.instance().replaceValues( "30" ) + "" ) );
        cI.setProperty( "gridId", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "gridId", DataManager.instance().replaceValues( "" ) + "" ) );
        cI.setProperty( "localTesting", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "localTesting", DataManager.instance().replaceValues( "false" ) + "" ) );
      
        cI.initialize();
        cI.getHandlers().forEach((t) -> {
          addEventHandler(t);
        });
      }if ( hasValue( "iw", "31358.3117" ) ) { 
        cI = new com.orasi.integration.html.HTMLSerializer();
        log.warn( "Enabling Integration: " + cI.getName() );
        cI.setProperty( "outputFolder", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "outputFolder", DataManager.instance().replaceValues( "." ) + "" ) );
        cI.setProperty( "launchUi", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "launchUi", DataManager.instance().replaceValues( "false" ) + "" ) );
        cI.setProperty( "suiteTemplate", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "suiteTemplate", DataManager.instance().replaceValues( "" ) + "" ) );
        cI.setProperty( "testTemplate", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "testTemplate", DataManager.instance().replaceValues( "" ) + "" ) );
        cI.setProperty( "sourceTemplate", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "sourceTemplate", DataManager.instance().replaceValues( "" ) + "" ) );
      
        cI.initialize();
        cI.getHandlers().forEach((t) -> {
          addEventHandler(t);
        });
      }if ( hasValue( "iw", "31358.3118" ) ) { 
        cI = new com.orasi.integration.console.ExecutionConsole();
        log.warn( "Enabling Integration: " + cI.getName() );
        cI.setProperty( "color", SuiteExecutionWrapper.instance().getOption( cI.getKey(), "color", DataManager.instance().replaceValues( "false" ) + "" ) );
      
        cI.initialize();
        cI.getHandlers().forEach((t) -> {
          addEventHandler(t);
        });
      }

        //
      // Get all active targets
      //
      List<String> targetNames = new ArrayList<>(10);
      List<String> localTargetNames = new ArrayList<>(10);
      SuiteExecutionWrapper.instance().routerList.forEach((_t) -> {
        _t.getTargetList().forEach((t) -> {
          
          String useName = t.getName() + " from " + _t.getName();
          useName = useName.toLowerCase();
          
          if ( localTargetNames.contains( useName ) ) {
            t.setName( t.getName() + "_" + generateCharacters( 5 ) );
            targetNames.add(t.getName() + " from " + _t.getName());
          } else {
            localTargetNames.add( useName );
            targetNames.add(t.getName() + " from " + _t.getName());
          }
        });
      });

      SuiteExecutionWrapper.instance().targetList = targetNames;
      SuiteExecutionWrapper.instance().testList = TestManager.instance().getTestNames();
      SuiteExecutionWrapper.instance().start();
      }  
    }
    
    public int getExecutionId() {
      return executionId;
    }
  
    public static SuiteExecutionWrapper instance() {
      return singleton;
    }
  
    private ExecutorService eS;
  
    public void start() {
      
      if ( cli.hasOption( "p" ) ) {
        try { threadCount = Integer.parseInt( cli.getOptionValue( "p" ) ); } catch( Exception e ) {}
      }

      if ( cli.hasOption( "f" ) ) {
        try { checkFailureLevel = Integer.parseInt( cli.getOptionValue( "f" ) ); } catch( Exception e ) {}
      }

      executionId = 1;

      int totalTargets = 0;
      for ( Router _t : SuiteExecutionWrapper.instance().routerList ) {
        for ( ExecutionTarget t : _t.getTargetList() ){
          totalTargets++;
        }
      }

      Map<String, String> cliMap = new HashMap<>(5);
      for (Option o : cli.getOptions()) {
        if (o.getLongOpt() != null && !o.getLongOpt().trim().isEmpty()) {
          cliMap.put(o.getLongOpt() + " (-" + o.getOpt() + ")", String.join(",", o.getValuesList()));
        } else {
          cliMap.put("-" + o.getOpt(), String.join(",", o.getValuesList()));
        }
      }

      if ( totalTargets == 0 ) {
        throw new IllegalArgumentException( "No targets were defined therefore there was nowhere to run your tests" );
      }

      if ( TestManager.instance().getSize() == 0 ) {
        throw new IllegalArgumentException( "There were no tests to run" );
      }
      

      int totalTasks = TestManager.instance().getSize() * totalTargets;

      ExecutionQueue eQ = new ExecutionQueue(totalTasks > threadCount ? threadCount : totalTasks, 3, 3);
      if ( threadCount > totalTasks ) {
        threadCount = totalTasks;
      }

      SuitePayload suitePayload = new SuitePayload();
      suitePayload.setExecutionIdentifier( executionId );
      suitePayload.setName(name);
      suitePayload.setDescription(description);
      suitePayload.setUserName(userName);
      suitePayload.setSuiteDetail(  "{\"id\":6989,\"name\":\"zczxc\",\"description\":\"No description was added for zczxc\",\"userId\":12,\"userConfidence\":0,\"organizationId\":1,\"organizationConfidence\":0,\"status\":1,\"endpointId\":1,\"endpointStyleId\":1,\"targetId\":7157,\"targetConfigurationId\":0,\"targetVersionId\":0,\"reviewFlag\":0,\"importTests\":0,\"importFunctions\":0,\"importSites\":0,\"importTargets\":0,\"importData\":0,\"shareCount\":0,\"conductorList\":[{\"reviewFlag\":0,\"id\":6235,\"name\":\"zczxc Execution Package\",\"description\":\"Add a description of zczxc Execution Package\",\"alchemyId\":3112,\"alchemySeed\":31358,\"organizationId\":1,\"userId\":12,\"version\":0,\"targetDetail\":\"[{\\\"name\\\": \\\"Chrome\\\", \\\"status\\\": 0, \\\"userId\\\": 0, \\\"changed\\\": false, \\\"alchemyId\\\": 3114, \\\"createDate\\\": \\\"Mar 20, 2025, 1:37:20 PM\\\", \\\"modifyDate\\\": \\\"Mar 20, 2025, 1:37:20 PM\\\", \\\"alchemySeed\\\": 31358, \\\"description\\\": \\\"Add a description of Chrome\\\", \\\"propertyList\\\": [{\\\"name\\\": \\\"browserName\\\", \\\"value\\\": \\\"chrome\\\"}], \\\"organizationId\\\": 0, \\\"maximumAvailable\\\": 2, \\\"referenceSuiteID\\\": 0, \\\"executionRouterID\\\": {\\\"alchemyId\\\": 3113, \\\"alchemySeed\\\": 31358}}, {\\\"name\\\": \\\"Firefox\\\", \\\"status\\\": 0, \\\"userId\\\": 0, \\\"changed\\\": false, \\\"alchemyId\\\": 3115, \\\"createDate\\\": \\\"Mar 20, 2025, 1:37:20 PM\\\", \\\"modifyDate\\\": \\\"Mar 20, 2025, 1:37:20 PM\\\", \\\"alchemySeed\\\": 31358, \\\"description\\\": \\\"Add a description of Firefox\\\", \\\"propertyList\\\": [{\\\"name\\\": \\\"browserName\\\", \\\"value\\\": \\\"firefox\\\"}], \\\"organizationId\\\": 0, \\\"maximumAvailable\\\": 2, \\\"referenceSuiteID\\\": 0, \\\"executionRouterID\\\": {\\\"alchemyId\\\": 3113, \\\"alchemySeed\\\": 31358}}, {\\\"name\\\": \\\"Microsoft Edge\\\", \\\"status\\\": 0, \\\"userId\\\": 0, \\\"changed\\\": false, \\\"alchemyId\\\": 3116, \\\"createDate\\\": \\\"Mar 20, 2025, 1:37:20 PM\\\", \\\"modifyDate\\\": \\\"Mar 20, 2025, 1:37:20 PM\\\", \\\"alchemySeed\\\": 31358, \\\"description\\\": \\\"Add a description of Microsoft Edge\\\", \\\"propertyList\\\": [{\\\"name\\\": \\\"browserName\\\", \\\"value\\\": \\\"MicrosoftEdge\\\"}], \\\"organizationId\\\": 0, \\\"maximumAvailable\\\": 2, \\\"referenceSuiteID\\\": 0, \\\"executionRouterID\\\": {\\\"alchemyId\\\": 3113, \\\"alchemySeed\\\": 31358}}]\",\"routerDetail\":\"[{\\\"name\\\": \\\"zczxc router\\\", \\\"status\\\": 0, \\\"userId\\\": 0, \\\"changed\\\": false, \\\"routerId\\\": 8, \\\"alchemyId\\\": 3113, \\\"createDate\\\": \\\"Mar 20, 2025, 1:37:20 PM\\\", \\\"modifyDate\\\": \\\"Mar 20, 2025, 1:37:20 PM\\\", \\\"alchemySeed\\\": 31358, \\\"description\\\": \\\"Add a description of zczxc router\\\", \\\"propertyList\\\": [{\\\"name\\\": \\\"URL\\\", \\\"value\\\": \\\"#{alchemy.cloud.url}\\\"}], \\\"organizationId\\\": 0}]\",\"status\":1,\"lockUserId\":0,\"changed\":false,\"createDate\":\"Mar 20, 2025, 1:37:41 PM\",\"modifyDate\":\"Mar 20, 2025, 1:37:41 PM\",\"targetList\":[{\"name\":\"Chrome\",\"description\":\"Add a description of Chrome\",\"alchemyId\":3114,\"alchemySeed\":31358,\"organizationId\":0,\"changed\":false,\"maximumAvailable\":2,\"executionRouterID\":{\"alchemyId\":3113,\"alchemySeed\":31358},\"propertyList\":[{\"name\":\"browserName\",\"value\":\"chrome\"}],\"status\":0,\"userId\":0,\"createDate\":\"Mar 20, 2025, 1:37:20 PM\",\"modifyDate\":\"Mar 20, 2025, 1:37:20 PM\",\"referenceSuiteID\":0},{\"name\":\"Firefox\",\"description\":\"Add a description of Firefox\",\"alchemyId\":3115,\"alchemySeed\":31358,\"organizationId\":0,\"changed\":false,\"maximumAvailable\":2,\"executionRouterID\":{\"alchemyId\":3113,\"alchemySeed\":31358},\"propertyList\":[{\"name\":\"browserName\",\"value\":\"firefox\"}],\"status\":0,\"userId\":0,\"createDate\":\"Mar 20, 2025, 1:37:20 PM\",\"modifyDate\":\"Mar 20, 2025, 1:37:20 PM\",\"referenceSuiteID\":0},{\"name\":\"Microsoft Edge\",\"description\":\"Add a description of Microsoft Edge\",\"alchemyId\":3116,\"alchemySeed\":31358,\"organizationId\":0,\"changed\":false,\"maximumAvailable\":2,\"executionRouterID\":{\"alchemyId\":3113,\"alchemySeed\":31358},\"propertyList\":[{\"name\":\"browserName\",\"value\":\"MicrosoftEdge\"}],\"status\":0,\"userId\":0,\"createDate\":\"Mar 20, 2025, 1:37:20 PM\",\"modifyDate\":\"Mar 20, 2025, 1:37:20 PM\",\"referenceSuiteID\":0}],\"routerList\":[{\"name\":\"zczxc router\",\"description\":\"Add a description of zczxc router\",\"alchemyId\":3113,\"alchemySeed\":31358,\"organizationId\":0,\"changed\":false,\"routerId\":8,\"status\":0,\"userId\":0,\"propertyList\":[{\"name\":\"URL\",\"value\":\"#{alchemy.cloud.url}\"}],\"createDate\":\"Mar 20, 2025, 1:37:20 PM\",\"modifyDate\":\"Mar 20, 2025, 1:37:20 PM\"}],\"acls\":[]}],\"version\":2,\"lockUserId\":0,\"testDisplay\":0,\"alchemyId\":3110,\"alchemySeed\":31358,\"referenceSuiteID\":0,\"changed\":false}" );
      suitePayload.setTestList(testList);
      suitePayload.setTargetList(targetList);
      suitePayload.setTotalTests(totalTasks);
      suitePayload.setOtherValue( "threadCount", threadCount );
      suitePayload.setOtherValue( "requestedThreadCount", totalTasks );
      suitePayload.setOtherValue( "cliMap", cliMap );
      suitePayload.setOtherValue( "routers", new Gson().toJson( SuiteExecutionWrapper.instance().routerList ) );
      notifyListeners( new SuiteEvent( suitePayload, name, 1 ) );

      
      log.warn(TestManager.instance().getSize() + " Tests executing across " + totalTargets + " devices ");
      log.warn("Submitting " + totalTasks + " tasks for execution");
      addEventHandler(new TestFailureHandler());

      TestManager.instance().getTests().forEach((t) -> {
        SuiteExecutionWrapper.instance().routerList.forEach((r) -> {
          r.getTargetList().forEach((_t) -> {
            eQ.addExecution(new EndpointDevice(r, _t), t);
          });
        });
      });

      eQ.run();

    boolean suiteFailed = (failureCount > 0 && failureLevel >= checkFailureLevel) || failureCount >= totalTasks;

    if (suiteFailed) {
      log.atWarn().log("Notifying Integration of Suite Failure");
      try {
        notifyListeners(new SuiteEvent(suitePayload, name, EventHook.FAILURE.getId()));
      } catch (Exception e) {
        log.atError().log("Failed to notify suites of failure");
      }
    } else {

      log.atWarn().log("Notifying Integration of Suite Success");
      try {
        notifyListeners(new SuiteEvent(suitePayload, name, EventHook.SUCCESS.getId()));
      } catch (Exception e) {
        log.atError().log("Failed to notify suites of success");
      }
    }

    log.atWarn().log("Notifying Integration of Suite Complete");
    try {
      notifyListeners(new SuiteEvent(suitePayload, name, EventHook.AFTER.getId()));
    } catch (Exception e) {
      log.atError().log("Failed to notify suites of success");
    }

    if (!cli.hasOption("n")) {
      if (suiteFailed) {
        System.exit(-1);
      } else {
        System.exit(0);
      }
    }
  }
  
  
    /**
     * @return the name
     */
    public String getName() {
      return name;
    }
  
    /**
     * @param name the name to set
     */
    private void setName(String name) {
      this.name = name;
    }
  
    /**
     * @return the description
     */
    public String getDescription() {
      return description;
    }
  
    /**
     * @param description the description to set
     */
    private void setDescription(String description) {
      this.description = description;
    }
  
    /**
     * @return the id
     */
    public int getId() {
      return id;
    }
  
    /**
     * @param id the id to set
     */
    private void setId(int id) {
      this.id = id;
    }
  
    /**
     * @return the userName
     */
    public String getUserName() {
      return userName;
    }
  
    /**
     * @param userName the userName to set
     */
    private void setUserName(String userName) {
      this.userName = userName;
    }
  
  }
  