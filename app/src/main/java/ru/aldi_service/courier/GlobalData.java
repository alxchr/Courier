package ru.aldi_service.courier;

import android.app.Application;

/**
 * Created by alx on 01.11.15.
 * 03.11.15. add employee name
 */
public class GlobalData extends Application {
    static private String login = "", password = "", employeeName = "";
    static private int employee = 0;

    static public int getEmployee() {
        return employee;
    }

    static public void setEmployee(int e) {
        employee = e;
    }

    static public String getLogin() {
        return login;
    }

    static public void setLogin(String l) {
        login = l;
    }

    static public String getPassword() {
        return password;
    }

    static public void setPassword(String p) {
        password = p;
    }

    static public String getEmployeeName() {
        return employeeName;
    }

    static public void setEmployeeName(String n) {
        employeeName = n;
    }

}
