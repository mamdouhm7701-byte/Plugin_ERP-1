package SuiteSupport;

import org.testng.annotations.Test;

/** Local event source used only by NativeInteractiveSuiteListenerTest; no ERP access. */
public final class NativeRunnerEventProbe {
    @Test
    public void firstCase() {}

    @Test
    public void secondCase() {}
}
