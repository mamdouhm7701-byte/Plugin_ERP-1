package Plugin_ERP.Utiel;

import Plugin_ERP.Bots.LogUtils;
import io.restassured.path.json.JsonPath;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class LoginjsonReader {

    String josnReader;
    String jsonfilename;
    private final String test_data_path = "src/test/java/Test-Data/";

    public LoginjsonReader(String jsonfilename) {
        this.jsonfilename = jsonfilename;
        try {
            JSONObject data = (JSONObject) new JSONParser()
                    .parse(new FileReader(test_data_path + jsonfilename + ".json"));
            josnReader = data.toJSONString();
        } catch (Exception e) {
            LogUtils.info("ERROR READING JSON FILE: " + jsonfilename + " -); " + e.getMessage());
        }
    }

    public String getJsonData(String jsonpath) {
        try {
            JsonPath jp = new JsonPath(josnReader);
            return jp.getString(jsonpath);
        } catch (Exception e) {
            LogUtils.info("ERROR GETTING JSON DATA FOR PATH: " + jsonpath + " -); " + e.getMessage());
            return "";
        }
    }
}
