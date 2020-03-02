package Common;
//  --------------
import Apps.ProxyEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import Apps.CamundaEndpoint;
import Apps.FacadeEndpoint;

/**
 * <h1>BASE CLASS</h1>
 * <strong>Initializer class</strong> for both setting up and tearing down tests
 *
 * @author  AlanZinho
 */
public class Base {

    protected RequestSpecification specsRequest;
    protected ResponseSpecification specsResponse;
    protected Map<String, Object> environment;
    private final Logger LOG = LogManager.getLogger(getClass());
    private final long REQUEST_TIMEOUT_MS = 5000L;
    private final String PATH_TO_ENVIRONMENT_DATA = "src/test/resources/workflow.json";
    private String ENVIRONMENT;
    private String ENV_DATA_PATH;
    protected CamundaEndpoint camundaEndpoint;
    protected FacadeEndpoint facadeEndpoint;
    protected ProxyEndpoint proxyEndpoint;
    private String deploymentId;


    @BeforeClass
    @Parameters({"processName"})
    public void setupForRestAssured(String processName) {
        LOG.debug("----->   <<< STARTING REST ASSURED TEST >>>");
        ENVIRONMENT = (System.getProperty("env") == null) ? "local" : System.getProperty("env");
        LOG.debug("----->   Base setting for ENVIRONMENT: [" + ENVIRONMENT.toUpperCase() + "]");
        LOG.debug("----->   Base setting for REQUEST TIMEOUTS: [" + REQUEST_TIMEOUT_MS + "]");
        environment = readEnvironmentData();
        //specsRequest = getRequestSpecification();
        specsResponse = getResponseSpecification(REQUEST_TIMEOUT_MS);
        //starts Enpoint Objects
        camundaEndpoint = new CamundaEndpoint(environment);
        facadeEndpoint = new FacadeEndpoint(environment);
        proxyEndpoint = new ProxyEndpoint(environment);
        //deploy specific process
        deploymentId = camundaEndpoint.deployWorkflow(processName);
        camundaEndpoint.assertProcessDeployed(deploymentId);

    }

    @AfterClass
    public void teardownForRestAssured() {
        LOG.debug("----->   <<< FINISHING REST ASSURED TEST >>>");
        //removing deployment from camunda.
        camundaEndpoint.removeDeployment(deploymentId);
        camundaEndpoint.assertDeploymentRemoved(deploymentId);
    }

    /**
     * This method creates the response specification object
     * @return  The RestAssured response specification object
     */
    public ResponseSpecification getResponseSpecification(long ms) {
        ResponseSpecBuilder responseBuilder = new ResponseSpecBuilder();
        responseBuilder.expectResponseTime( lessThanOrEqualTo(ms) , TimeUnit.MILLISECONDS );
        return responseBuilder.build();
    }

    /**
     * <i><strong>Used to ADD a query parameter to the Request Specification Object passed as parameter</strong></i>
     * @param   specRequest RequestSpecification object
     * @param   key         Parameter to be added
     * @param   value       The value of the parameter
     * @return              A request specification object (RestAssured)
     */
    public RequestSpecification addQueryParameter( RequestSpecification specRequest , String key , String value ) {
        LOG.info("Adding query parameter [" + key + " = " + value + "]...");
        return specRequest.queryParam( key , value );
    }

    /**
     * This method converts any "camelCase" text into "Human Readable" text
     * @param   text    The camelCase text to convert
     * @return          The converted string
     */
    public String splitCamelCase(String text) {
        return text.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    /**
     * This method reads from EnvData.json file and returns the map related to the environment selected
     * @return  The Map of [EnvData.json] related to the environment selected through command line
     */
    private Map<String, Object> readEnvironmentData() {
        Map<String,Object> environmentData;
        Map<String,Object> environment = null;
        try {
            ENV_DATA_PATH = (System.getenv("envData").equals("")) ? PATH_TO_ENVIRONMENT_DATA : System.getenv("envData");
            FileReader fileReader = new FileReader(ENV_DATA_PATH);
            environmentData = new ObjectMapper().readValue(fileReader, HashMap.class);
            environment = (Map<String, Object>) environmentData.get(ENVIRONMENT);
        } catch (FileNotFoundException e) {
            LOG.error("Unable to fetch environment data file at [src/test/resources/EnvData.json]", e);
        } catch (Exception e) {
            LOG.error("Unexpected error while reading [Environment Data]", e);
        }
        return environment;
    }
}