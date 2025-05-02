package com.innov4africa.api_gateway.model;

import java.util.List;

public class IShopAddressResponse {
    private String message;
    private String status;
    private int code;
    private List<Adresse> adresse;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public List<Adresse> getAdresse() { return adresse; }
    public void setAdresse(List<Adresse> adresse) { this.adresse = adresse; }

    public static class Adresse {
        private Long id;
        private int contact_number;
        private String email;
        private String adresse;
        private double latitude;
        private double longitude;
        private String name;
        private String address_type;
        private User user;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public int getContact_number() { return contact_number; }
        public void setContact_number(int contact_number) { this.contact_number = contact_number; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getAdresse() { return adresse; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAddress_type() { return address_type; }
        public void setAddress_type(String address_type) { this.address_type = address_type; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }

        public static class User {
            private Long id;
            private String nom;
            private String prenom;
            private String imageprofil;
            private long mobile_no;
            private City city;

            public Long getId() { return id; }
            public void setId(Long id) { this.id = id; }
            public String getNom() { return nom; }
            public void setNom(String nom) { this.nom = nom; }
            public String getPrenom() { return prenom; }
            public void setPrenom(String prenom) { this.prenom = prenom; }
            public String getImageprofil() { return imageprofil; }
            public void setImageprofil(String imageprofil) { this.imageprofil = imageprofil; }
            public long getMobile_no() { return mobile_no; }
            public void setMobile_no(long mobile_no) { this.mobile_no = mobile_no; }
            public City getCity() { return city; }
            public void setCity(City city) { this.city = city; }

            public static class City {
                private Long city_id;
                private String name;
                private String identifiant;
                private Country country;
                private boolean supprime;

                public Long getCity_id() { return city_id; }
                public void setCity_id(Long city_id) { this.city_id = city_id; }
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
                public String getIdentifiant() { return identifiant; }
                public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }
                public Country getCountry() { return country; }
                public void setCountry(Country country) { this.country = country; }
                public boolean isSupprime() { return supprime; }
                public void setSupprime(boolean supprime) { this.supprime = supprime; }

                public static class Country {
                    private Long country_id;
                    private String name;
                    private boolean supprime;
                    private String indicateur;

                    public Long getCountry_id() { return country_id; }
                    public void setCountry_id(Long country_id) { this.country_id = country_id; }
                    public String getName() { return name; }
                    public void setName(String name) { this.name = name; }
                    public boolean isSupprime() { return supprime; }
                    public void setSupprime(boolean supprime) { this.supprime = supprime; }
                    public String getIndicateur() { return indicateur; }
                    public void setIndicateur(String indicateur) { this.indicateur = indicateur; }
                }
            }
        }
    }
}