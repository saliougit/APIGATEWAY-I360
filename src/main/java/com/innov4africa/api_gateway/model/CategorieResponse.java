package com.innov4africa.api_gateway.model;

public class CategorieResponse {
    private Integer id;
    private Integer code;
    private String libelle;
    private String description;
    private Integer id_parent;
    private Integer poids;
    private Integer id_super;
    private Boolean supprime;
    private Integer niveau;
    private Integer feature;
    private String image;
    private DomaineResponse domaine;

    // Constructeur par défaut
    public CategorieResponse() {}

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId_parent() {
        return id_parent;
    }

    public void setId_parent(Integer id_parent) {
        this.id_parent = id_parent;
    }

    public Integer getPoids() {
        return poids;
    }

    public void setPoids(Integer poids) {
        this.poids = poids;
    }

    public Integer getId_super() {
        return id_super;
    }

    public void setId_super(Integer id_super) {
        this.id_super = id_super;
    }

    public Boolean getSupprime() {
        return supprime;
    }

    public void setSupprime(Boolean supprime) {
        this.supprime = supprime;
    }

    public Integer getNiveau() {
        return niveau;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }

    public Integer getFeature() {
        return feature;
    }

    public void setFeature(Integer feature) {
        this.feature = feature;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public DomaineResponse getDomaine() {
        return domaine;
    }

    public void setDomaine(DomaineResponse domaine) {
        this.domaine = domaine;
    }
}
