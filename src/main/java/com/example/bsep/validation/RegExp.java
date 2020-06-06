package com.example.bsep.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp {

    public boolean isValidId(Long id){
        String number = Long.toString(id);
        String nameRegex = "^[0-9]{1,6}$";
        Pattern pattern = Pattern.compile(nameRegex);
        Matcher matcher = pattern.matcher(number);
        if(matcher.find()){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isValidEmail(String email){
        String emailRegex = "/\\S+@\\S+\\.\\S+/";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(emailRegex);
        if(matcher.find()){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isValidPassword(String pass){
        String passRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!_@$%^&*-]).{8,}$";
        Pattern pattern = Pattern.compile(passRegex);
        Matcher matcher = pattern.matcher(pass);
        if(matcher.find()){
            return true;
        }
        else{
            return false;
        }
    }

}
