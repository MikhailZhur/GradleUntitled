package listener;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class RetryListener implements TestExecutionExceptionHandler, AfterTestExecutionCallback {

    private static final int MAX_RETRIES = 3;
    private static final Set<String> failedTestNames = new HashSet<>();


    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                extensionContext.getRequiredTestMethod().invoke(extensionContext.getRequiredTestInstance());
                return;
            } catch (Throwable ex) {
                throwable = ex;
            }
        }
        throw throwable;
    }


    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        Method method = extensionContext.getRequiredTestMethod();
        String testClass = extensionContext.getRequiredTestClass().getName();
        String testName = method.getName();
        String testToWrite = String.format("--tests %s.%s*", testClass, testName);
        extensionContext.getExecutionException().ifPresent(x -> failedTestNames.add(testToWrite));
    }

    @SneakyThrows
    public static void saveFailedTests() {
        String output = System.getProperty("user.dir") + "/src/test/resources/FailedTests.txt";
        String result = String.join(" ", failedTestNames);
        FileUtils.writeStringToFile(new File(output), result);

    }
}
