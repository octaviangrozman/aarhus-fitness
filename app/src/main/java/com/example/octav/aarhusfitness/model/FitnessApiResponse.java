package com.example.octav.aarhusfitness.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Octav on 1/3/2018.
 */

public class FitnessApiResponse {

    @Override
    public String toString() {
        return "FitnessApiResponse{" +
                "type='" + type + '\'' +
                ", crs=" + crs +
                ", features=" + features +
                '}';
    }

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("crs")
    @Expose
    private FitnessApiResponse crs;
    @SerializedName("features")
    @Expose
    private List<Feature> features = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FitnessApiResponse getCrs() {
        return crs;
    }

    public void setCrs(FitnessApiResponse crs) {
        this.crs = crs;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public class Feature {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("properties")
        @Expose
        private Properties_ properties;
        @SerializedName("geometry")
        @Expose
        private Geometry geometry;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Properties_ getProperties() {
            return properties;
        }

        public void setProperties(Properties_ properties) {
            this.properties = properties;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        @Override
        public String toString() {
            return "Feature{" +
                    "type='" + type + '\'' +
                    ", properties=" + properties +
                    ", geometry=" + geometry +
                    '}';
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

    }

    public class Geometry {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("coordinates")
        @Expose
        private List<Double> coordinates = null;

        @Override
        public String toString() {
            return "Geometry{" +
                    "type='" + type + '\'' +
                    ", coordinates=" + coordinates +
                    '}';
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }

    }

    public class Properties {

        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Properties{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public class Properties_ {

        @SerializedName("Bookbar")
        @Expose
        private String bookbar;
        @SerializedName("Type")
        @Expose
        private String type;
        @SerializedName("Navn")
        @Expose
        private String navn;

        public String getBookbar() {
            return bookbar;
        }

        public void setBookbar(String bookbar) {
            this.bookbar = bookbar;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNavn() {
            return navn;
        }

        public void setNavn(String navn) {
            this.navn = navn;
        }

        @Override
        public String toString() {
            return "Properties_{" +
                    "bookbar='" + bookbar + '\'' +
                    ", type='" + type + '\'' +
                    ", navn='" + navn + '\'' +
                    '}';
        }
    }

}