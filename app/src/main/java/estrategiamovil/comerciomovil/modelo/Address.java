package estrategiamovil.comerciomovil.modelo;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by administrator on 22/06/2016.
 */
public class Address implements Serializable {//agregar atributos de la tabla ubicacion para ir guardando los objetos direccions...
    private String idAddress;
    private String street;
    private String pc;
    private String outDoorNumber;
    private String interiorNumber;
    private String town;
    private String state;
    private String country;
    private String reference;
    private String shortName;
    private String lat;
    private String lng;
    private String googleAddress;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(String idAddress) {
        this.idAddress = idAddress;
    }


    public String getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setOutDoorNumber(String outDoorNumber) {
        this.outDoorNumber = outDoorNumber;
    }

    public void setInteriorNumber(String interiorNumber) {
        this.interiorNumber = interiorNumber;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }


    public String getReference() {
        return reference;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getTown() {
        return town;
    }

    public String getInteriorNumber() {
        return interiorNumber;
    }

    public String getOutDoorNumber() {
        return outDoorNumber;
    }

    public String getStreet() {
        return street;
    }



    public Address(String street, String outDoorNumber, String interiorNumber, String town, String state, String country, String reference, String lat,String lng, String googleAddress) {
        this.street = street;
        this.outDoorNumber = outDoorNumber;
        this.interiorNumber = interiorNumber;
        this.town = town;
        this.state = state;
        this.country = country;
        this.reference = reference;
        this.lat = lat;
        this.lng = lng;
        this.googleAddress = googleAddress;
    }
    @Override
    public String toString(){

       StringBuffer number=new StringBuffer();
       if (!getOutDoorNumber().trim().equalsIgnoreCase("SN")) {
           number.append(getOutDoorNumber());
           if (!getInteriorNumber().trim().equalsIgnoreCase("SN"))
               number.append(" interior " + getInteriorNumber());
       }else{
           number.append(" Sin número");
       }

       return   getStreet()+" "+
               ((getOutDoorNumber()==null || getOutDoorNumber().trim().equals(""))?"SN":getOutDoorNumber())+ " " +
               ((getInteriorNumber()==null || getInteriorNumber().trim().equals(""))?"SN":getInteriorNumber())+", "+
               ((getTown()==null || getTown().trim().equals(""))?"":getTown()) +
                "Estado: "+getState() + ","+getCountry()+" ,"+getPc()+". "+getReference();
    }
    public Address(){}
}
