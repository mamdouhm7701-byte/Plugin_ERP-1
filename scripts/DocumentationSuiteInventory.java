import java.lang.reflect.Method;
import java.util.*;
import java.nio.file.*;
import org.json.*;

public class DocumentationSuiteInventory {
    public static void main(String[] args) throws Exception {
        Method selector = Class.forName("SuiteSupport.SuiteDefinitionsTest")
                .getDeclaredMethod("selectedCases", String.class);
        selector.setAccessible(true);
        JSONArray rows = new JSONArray();
        try(var paths = Files.walk(Path.of("src/test/resources"))) {
            for(Path p : paths.filter(f -> f.getFileName().toString().endsWith("-testng.xml")).sorted().toList()) {
                String level = Path.of("src/test/resources").relativize(p).toString().replace('\\','/')
                        .replaceFirst("-testng\\.xml$", "");
                try {
                    @SuppressWarnings("unchecked") List<String> cases = (List<String>)selector.invoke(null,level);
                    rows.put(new JSONObject().put("file",p.toString().replace('\\','/'))
                            .put("selectedMethods",cases.size()).put("uniqueMethods",new HashSet<>(cases).size())
                            .put("cases",cases));
                } catch (java.lang.reflect.InvocationTargetException ex) {
                    rows.put(new JSONObject().put("file",p.toString().replace('\\','/'))
                            .put("inspectionError",ex.getCause().toString()));
                }
            }
        }
        Files.writeString(Path.of("target/documentation-suite-inventory.json"),rows.toString(2));
        for(Object row : rows) {
            JSONObject value = (JSONObject)row;
            if(Set.of("src/test/resources/smoke-testng.xml","src/test/resources/sanity-testng.xml",
                    "src/test/resources/e2e-testng.xml","src/test/resources/regression-testng.xml",
                    "src/test/resources/ci-testng.xml").contains(value.getString("file")))
                System.out.println(value.getString("file")+": "+value.optInt("selectedMethods",-1));
        }
    }
}
