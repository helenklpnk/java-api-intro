package endpoint;

import com.sun.net.httpserver.*;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import db.JDBCUtils;
import entities.Account;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Endpoint2 extends BaseEndpoint implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestParamValue = null;
        String requestURI = httpExchange.getRequestURI().toString();
        System.out.println(requestURI);
        System.out.println(httpExchange.getRequestMethod());
        if ("GET".equals(httpExchange.getRequestMethod())) {
            System.out.println("Endpoint2: GET handled");
            requestParamValue = "Val";
        } else if ("POST".equals(httpExchange.getRequestMethod()) || "OPTIONS".equals(httpExchange.getRequestMethod())) {
            System.out.println("Endpoint2: Post handled");
            requestParamValue = handlePostRequest(httpExchange);
        } else {
            System.out.println("Endpoint2: Nothing handled");
        }
        System.out.println("URI: " + requestURI);
        handleResponse(httpExchange, requestParamValue);
    }

    private String handleGetRequest(HttpExchange httpExchange) {
        return httpExchange.
                getRequestURI()
                .toString()
                .split("\\?")[1]
                .split("=")[1];
    }
    
    private String handlePostRequest(HttpExchange httpExchange) throws IOException {
        BufferedReader httpInput = new BufferedReader(new InputStreamReader(
			  httpExchange.getRequestBody(), "UTF-8"));
        StringBuilder in = new StringBuilder();
        String input;
        while ((input = httpInput.readLine()) != null) {
            System.out.println("input: " + input);
            in.append(input).append(" ");
        }
        System.out.println("Entire body: " + in.toString().trim());
        httpInput.close();
        return in.toString().trim();
    }
    
    private void handleResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank",
                "root",
                "kitty")) {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(requestParamValue);
            int proof;
            switch (detectAction(object)) {
                /**
                 * Authentication
                 */ 
                case 1: boolean res = JDBCUtils.checkForAccount(conn, (String) object.get("login"),
                        (String) object.get("password"));
                requestParamValue = String.valueOf(res);
                /*
                JSONObject obj = new JSONObject();
                if (account != null) {
                    obj.put("login", account.getLogin());
                    obj.put("password", account.getPasswd());
                    obj.put("first_name", account.getFname());
                    obj.put("last_name", account.getLname());
                    obj.put("city", account.getCity());
                    obj.put("balance", account.getBalance());
                    requestParamValue = obj.toString();
                } else
                    requestParamValue = null; */
                break;
                /**
                 * Deposit
                 */
                case 2: JDBCUtils.makeDeposit(conn, (String) object.get("login"), Double.parseDouble((String) object.get("debit")));
                requestParamValue = String.valueOf(JDBCUtils.showBalance(conn, (String) object.get("login")));
                break;
                /** 
                 * Withdrawal
                 */
                case 3: proof = JDBCUtils.withdrawMoney(conn, (String) object.get("login"), Double.parseDouble((String) object.get("credit")));
                requestParamValue = (proof > 0) ?
                        String.valueOf(JDBCUtils.showBalance(conn, (String) object.get("login"))) : String.valueOf(proof);
                break;
                /**
                 * Opening account
                 */ 
                case 4:
                JDBCUtils.openAccount(conn, (String) object.get("login"), (String) object.get("password"),
                        (String) object.get("first_name"), (String) object.get("last_name"),
                        (String) object.get("city"), Double.parseDouble((String) object.get("balance")));
                requestParamValue = String.valueOf(JDBCUtils.showBalance(conn, (String) object.get("login")));
                break;
                /** 
                 * Deleting account
                 */
                case 5: proof = JDBCUtils.closeAccount(conn, (String) object.get("login"));
                requestParamValue = String.valueOf(proof);
                break;
                case 6: double balance = JDBCUtils.showBalance(conn, (String) object.get("login"));
                requestParamValue = String.valueOf(balance);
                break;
                default: break;
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        String htmlResponse = "{\"key\": \"" + requestParamValue + "\"}";
        System.out.println(htmlResponse);
        super.setHttpExchangeResponseHeaders(httpExchange);
        httpExchange.sendResponseHeaders(200, htmlResponse.length());

        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(htmlResponse.getBytes());
        }
    }

    private int detectAction(JSONObject object) throws ParseException {
        if (object.containsKey("password") && !object.containsKey("first_name"))
            return 1;       // authentication
        else if (object.containsKey("debit"))
            return 2;       // deposit
        else if (object.containsKey("credit"))
            return 3;       // withdrawal
        else if (object.containsKey("first_name"))
            return 4;       // opening account
        else if (object.keySet().size() == 2 && object.containsKey("login")) {
            if (object.get("command").equals("delete"))
                return 5;   // deleting account
            else
                return 6;   // show balance
        }
        return -1;
    }
}