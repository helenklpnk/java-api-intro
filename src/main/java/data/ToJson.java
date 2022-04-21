package data;

import entities.Account;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;

public class ToJson{
    private String val1;
    private int val2;

    public ToJson(String val1, int val2){
        this.val1 = val1;
        this.val2 = val2;
    }

    public String convert(){
       StringBuffer sBuffer = new StringBuffer("{");
       sBuffer.append("\"val1\": \"" + this.val1 + "\",");
       sBuffer.append("\"val2\": " + String.valueOf(this.val2) + "");
       sBuffer.append("}");
       return sBuffer.toString();
    }
}