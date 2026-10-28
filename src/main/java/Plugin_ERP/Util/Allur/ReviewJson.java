/* شرح الملف: قراءة وكتابة بيانات ملخص الفشل وسجل المتابعة بالمكتبة الموجودة في المشروع.
 * فائدته: يمنع إضافة احتياجات تشغيل جديدة من أجل التقرير فقط.
 */
package Plugin_ERP.Util.Allur;

import java.util.*;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

final class ReviewJson {
    private ReviewJson() {}

    private static Object parse(String text) {
        try {
            return new JSONParser().parse(text);
        } catch (ParseException failure) {
            throw new IllegalArgumentException("Invalid failure-review JSON", failure);
        }
    }

    private static Object raw(Object value) {
        if (value instanceof JSONObject object) return object.values;
        if (value instanceof JSONArray array) return array.values;
        return value;
    }

    static final class JSONObject {
        private final Map<String, Object> values;

        JSONObject() {
            values = new LinkedHashMap<>();
        }

        JSONObject(String text) {
            this(parse(text));
        }

        private JSONObject(Object source) {
            if (!(source instanceof Map<?, ?> map))
                throw new IllegalArgumentException("Expected JSON object");
            values = new LinkedHashMap<>();
            map.forEach((key, value) -> values.put(key.toString(), value));
        }

        JSONObject put(String name, Object value) {
            values.put(name, raw(value));
            return this;
        }

        void remove(String name) {
            values.remove(name);
        }

        String optString(String name) {
            return optString(name, "");
        }

        String optString(String name, String fallback) {
            return Objects.toString(values.get(name), fallback);
        }

        String getString(String name) {
            if (!values.containsKey(name))
                throw new IllegalArgumentException("Missing review field: " + name);
            return optString(name);
        }

        int optInt(String name) {
            Object value = values.get(name);
            return value instanceof Number number ? number.intValue() : 0;
        }

        int getInt(String name) {
            return optInt(name);
        }

        boolean optBoolean(String name) {
            return Boolean.TRUE.equals(values.get(name));
        }

        boolean getBoolean(String name) {
            return optBoolean(name);
        }

        JSONObject optJSONObject(String name) {
            return values.get(name) instanceof Map<?, ?> ? new JSONObject(values.get(name)) : null;
        }

        JSONObject getJSONObject(String name) {
            return Objects.requireNonNull(optJSONObject(name), "Missing review object: " + name);
        }

        JSONArray optJSONArray(String name) {
            return values.get(name) instanceof List<?> ? new JSONArray(values.get(name)) : null;
        }

        JSONArray getJSONArray(String name) {
            return Objects.requireNonNull(optJSONArray(name), "Missing review array: " + name);
        }

        @Override
        public String toString() {
            return JSONValue.toJSONString(values);
        }
    }

    static final class JSONArray {
        private final List<Object> values;

        JSONArray() {
            values = new ArrayList<>();
        }

        JSONArray(String text) {
            this(parse(text));
        }

        private JSONArray(Object source) {
            if (!(source instanceof List<?> list))
                throw new IllegalArgumentException("Expected JSON array");
            values = new ArrayList<>(list);
        }

        JSONArray put(Object value) {
            values.add(raw(value));
            return this;
        }

        int length() {
            return values.size();
        }

        boolean isEmpty() {
            return values.isEmpty();
        }

        Object get(int index) {
            return values.get(index);
        }

        JSONObject getJSONObject(int index) {
            return new JSONObject(values.get(index));
        }

        String getString(int index) {
            return Objects.toString(values.get(index), "");
        }

        @Override
        public String toString() {
            return JSONValue.toJSONString(values);
        }
    }
}
