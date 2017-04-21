package com.test.feiyun.demo.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 飞云 on 2016/11/12.
 */

public class Data {
    private static boolean isWear = false;
    private static String username = "";
    private static boolean isFirst = true;
    private static String phone;
    private static int userID;
    private static int height;
    private static int userPetID;
    private static int weight;
    private static float BMI;
    private static Date birth;
    private static int currentUserShape;//用户体型，数字越大越胖1,2,3
    private static int money;
    private static int gender; //女生为0，男生为1
    private static Date registerDate;
    private static String petPath;




    public static boolean isWear() {
        return isWear;
    }
    public static void setIsWear(boolean isWear) {
        Data.isWear = isWear;
    }
    public static String getPetPath() {
        return petPath;
    }
    public static void setPetPath(String petPath) {
        Data.petPath = petPath;
    }
    public static int getUserPetID() {
        return userPetID;
    }
    public static void setUserPetID(int userPetID) {
        Data.userPetID = userPetID;
    }
    public static int getCurrentUserShape() {
        return currentUserShape;
    }
    public static void setCurrentUserShape(int currentUserShape) {
        Data.currentUserShape = currentUserShape;
    }
    public static Date getRegisterDate() {
        return registerDate;
    }
    public static void setRegisterDate(Date registerDate) {
        Data.registerDate = registerDate;
    }
    public static int getMoney() {
        return money;

    }
    public static void setMoney(int money) {
        Data.money = money;
    }
    public static float getBMI() {
        return BMI;
    }
    public static void setBMI(float BMI) {
        Data.BMI = BMI;
    }
    public static String getPhone() {
        return phone;
    }
    public static void setPhone(String phone) {
        Data.phone = phone;
    }
    public static boolean isFirst() {
        return isFirst;
    }
    public static int getUserID() {
        return userID;
    }
    public static void setUserID(int userID) {
        Data.userID = userID;
    }
    public static int getHeight() {
        return height;
    }
    public static void setHeight(int height) {
        Data.height = height;
    }
    public static int getWeight() {
        return weight;
    }
    public static void setWeight(int weight) {
        Data.weight = weight;
    }
    public static Date getBirth() {
        return birth;
    }
    public static void setBirth(Date birth) {
        Data.birth = birth;
    }
    public static int getGender() {
        return gender;
    }
    public static void setGender(int gender) {
        Data.gender = gender;
    }
    public static void setUsername(String username) {
        Data.username = username;
    }
    public static String getUsername() {
        return username;
    }
    public static void setIsFirst(boolean isCollect){
        Data.isFirst = isCollect;
    }
    public static boolean getIsCollect() {
        return isFirst;
    }
}
