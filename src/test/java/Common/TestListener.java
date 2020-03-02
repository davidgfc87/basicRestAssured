package Common;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class TestListener extends Base implements ITestListener {

    private Logger LOG =  LogManager.getLogger(getClass());

    @Override
    public void onTestStart(ITestResult result) {
        LOG.debug("----->   [TEST STARTED]: [" + splitCamelCase(result.getMethod().getMethodName()).toUpperCase() + "]");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("----->   [TEST PASSED ]: [" + splitCamelCase(result.getMethod().getMethodName()).toUpperCase() + "]");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.fatal("----->   [TEST FAILED ]: [" + splitCamelCase(result.getMethod().getMethodName()).toUpperCase() + "]");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("----->   [TEST SKIPPED]: [" + splitCamelCase(result.getMethod().getMethodName()).toUpperCase() + "]");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}

}
