package com.sam.dbus;

public class ReturnPickupPoints {

    private String Name;
    private String City;

    public ReturnPickupPoints() {

    }

    public ReturnPickupPoints(String Name) {

        this.Name = Name;
    }

    public void setName(String Name) {

        this.Name = Name;
    }

    public String getName() {
        return this.Name;
    }
    public void setCity(String City){
        this.City = City;
    }
    public String getCity()
    {
        return this.City;
    }

}
