/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaproject;

/**
 *
 * @author Lenovo
 */
public class PatientBean {
    private int id;
    private String name;
    private String dob;
    private byte[] photo;
    private int pressure;
    private int glucose;
    private int temperature;
    private int saturation;
    private String docadv;
    private String reminder;
    
    public PatientBean(int id,String name,String dob,byte[] photo,int pressure,int glucose,int temperature,int saturation,String docadv, String reminder){
        this.id=id;
        this.name=name;
        this.dob=dob;
        this.photo=photo;
        this.pressure=pressure;
        this.glucose=glucose;
        this.temperature=temperature;
        this.saturation=saturation;
        this.docadv=docadv;
        this.reminder=reminder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getGlucose() {
        return glucose;
    }

    public void setGlucose(int glucose) {
        this.glucose = glucose;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public String getDocadv() {
        return docadv;
    }

    public void setDocadv(String docadv) {
        this.docadv = docadv;
    }
    
    public String getReminder(){
        return reminder;
    }
    
    public void setReminder(String reminder){
        this.reminder=reminder;
    }
    
}
