package SuiteSupport;

import Plugin_ERP.BranchProfiles.*;
import java.nio.file.*;
import java.util.*;
import org.testng.xml.XmlSuite;

/** Separate native TestNG runner: prepares policies before TestNG starts fixture lifecycles. */
public class BusinessRunListener extends NativeInteractiveSuiteListener {
    private final Map<String, String> previous = new LinkedHashMap<>();

    private void set(String key, String value) {
        previous.putIfAbsent(key, System.getProperty(key));
        System.setProperty(key, value);
    }

    BusinessRunDialog.Selection selectBusiness() throws Exception {
        return BusinessRunDialog.choose();
    }

    BranchProfile prepare(BusinessRunRequest request, Path project, Path output) throws Exception {
        return BusinessBranchPreparation.prepare(request, project, output);
    }

    List<XmlSuite> plan(Path project, BusinessRunDialog.Selection selection, Path output)
            throws Exception {
        return BusinessRunSuites.plan(project, selection.business(), selection.level(), output);
    }

    @Override
    public void alter(List<XmlSuite> suites) {
        try {
            var choice = selectBusiness();
            if (choice == null) {
                suites.clear();
                System.out.println("Canceled. No business settings or tests changed.");
                return;
            }
            Path project = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
            acquire(project);
            set("ExecutionType", choice.execution());
            if (choice.demo()) {
                set("Environment", "Cloud");
                set("Tenant", "Demo");
            }
            Path output = project.resolve("target/validation/business-" + UUID.randomUUID());
            var profile = prepare(choice.business(), project, output.resolve("preparation"));
            set("branch.profile", profile.name());
            set("branch.profile.fingerprint", profile.fingerprint());
            set("branch.profile.select", "true");
            set("business.additionalTax", Boolean.toString(choice.business().additionalTax()));
            BranchProfileCommand.write(
                    profile.name(),
                    project.resolve("config/branch-profiles"),
                    output.resolve("branch-profile.json"),
                    profile.fingerprint());
            var planned = plan(project, choice, output);
            suites.clear();
            suites.addAll(planned);
            System.out.println("Business verified: " + choice.business().fields());
            System.out.println(
                    "Branch: " + profile.expected().branchId() + "; profile: " + profile.name());
            System.out.println("Native TestNG run; evidence and coverage limits: " + output);
        } catch (Exception | AssertionError failure) {
            onExecutionFinish();
            throw new IllegalStateException(
                    "Business run stopped before tests: " + failure.getMessage(), failure);
        }
    }

    @Override
    public void onExecutionFinish() {
        try {
            super.onExecutionFinish();
        } finally {
            previous.forEach(
                    (key, value) -> {
                        if (value == null) System.clearProperty(key);
                        else System.setProperty(key, value);
                    });
            previous.clear();
        }
    }
}
