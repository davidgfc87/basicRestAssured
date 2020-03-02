package Common;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;

public class Utils {

    private static final String LAST_SECOND = "T23:59:59";
    private static final String EMPTY_STRING = "";

    public static final String ORDER_ID = "order_id";
    public static final String FOO = "foo";
    public static final String FOO_VALUE = "bar";
    public static final String STATUS = "status";
    public static final String PROFILE_ID = "profile_id";

    public static String tomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, 1);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    public static String getDraftOrderRequest(String reservationId) {
        JSONObject jo;
        try(FileReader jsonReader = new FileReader("./src/test/resources/OrderBody.json")) {
            jo = (JSONObject) new JSONParser().parse(jsonReader);
            JSONObject subOrders = (JSONObject)((JSONArray)jo.get("subOrders")).get(0);
            JSONObject reservationInfo = (JSONObject) subOrders.get("reservationInfo");
            reservationInfo.put("expirationDate", tomorrow() + LAST_SECOND);
            reservationInfo.put("pickupDate", tomorrow());
            reservationInfo.put("reservationId", reservationId);

        } catch(FileNotFoundException fnfe) {
            return EMPTY_STRING;
        }
        catch(ParseException pe) {
            return EMPTY_STRING;
        }
        catch(IOException io) {
            return EMPTY_STRING;
        }
        return jo.toJSONString();
    }
}
