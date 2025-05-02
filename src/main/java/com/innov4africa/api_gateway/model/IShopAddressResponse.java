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

        public static class User {
            private Long id;
            private String nom;
            private String prenom;
            private String imageprofil;
            private long mobile_no;
            private City city;

            public static class City {
                private Long city_id;
                private String name;
                private String identifiant;
                private Country country;
                private boolean supprime;

                public static class Country {
                    private Long country_id;
                    private String name;
                    private boolean supprime;
                    private String indicateur;
                    // getters and setters
                }
                // getters and setters
            }
            // getters and setters
        }
        // getters and setters
    }
    // getters and setters
}