package com.wepindia.pos.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RichaA on 10/23/2017.
 */

public class GSTINValidation {

    private static final int CHECK_INTEGER_VALUE = 0;
    private static final int CHECK_DOUBLE_VALUE = 1;
    private static final int CHECK_STRING_VALUE = 2;

    public static boolean checkGSTINValidation(String str )
    {
        boolean mFlag = false;
        try {
            if(str.trim().length() == 0)
            {mFlag = true;}
            else if (str.trim().length() > 0 && str.length() == 15) {
                String[] part = str.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                if ((part.length == 7 ||part.length == 6)
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[0], "Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[1],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[2],"Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[3],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[4],"Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(str.substring(13,14),"String")
                        //&& (((int)(str.charAt(13))) >=65 && ((int)(str.charAt(13))) <=90)
                        && (CHECK_INTEGER_VALUE == checkDataypeValue(str.substring(14),"Int") ||
                        CHECK_STRING_VALUE == checkDataypeValue(str.substring(14),"String"))) {

                    mFlag = true;
                } else {
                    mFlag = false;
                }
            } else {
                mFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFlag = false;
        }

        finally {
            return mFlag;
        }
    }
    public static  int checkDataypeValue(String value, String type) {
        int flag =-1;
        try {
            switch(type) {
                case "Int":
                    Integer.parseInt(value);
                    flag = 0;
                    break;
                case "Double" : Double.parseDouble(value);
                    flag = 1;
                    break;
                default : flag = getSpecialCharacterCount(value);
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            flag = -1;
        }
        return flag;
    }

    private  static int getSpecialCharacterCount(String s) {
        int result = -1;
        try{
            if (s == null || s.trim().isEmpty()) {
                System.out.println("Incorrect format of string");
                return result;
            }
            Pattern p = Pattern.compile("[^A-Za-z0-9]");
            Matcher m = p.matcher(s);
            // boolean b = m.matches();
            boolean b = m.find();
            if (b == true) {
                System.out.println("There is a special character in my string ");
            }
            else
            {
                System.out.println("There is no special char.");
                result = 2;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            result = -1;
        }
        finally{
            return  result;
        }

    }

}
