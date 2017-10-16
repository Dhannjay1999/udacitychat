package com.example.lenovo.xyz;

/**
 * Created by Lenovo on 8/14/2017.
 */

public class Artist {
    private  String id;
    private String name;
    private String genere;
private String imageuri;
    public Artist(){

    }
    public Artist(String id,String name,String genere,String imageuri){
        this.id=id;
        this.name=name;
        this.genere=genere;
        this.imageuri=imageuri;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGenere() {
        return genere;
    }

    public String getImageuri() {
        return imageuri;
    }
}
