// package com.innov4africa.api_gateway.model;

// import java.util.List;

// public class IShopNotificationResponse {
//     private String status;
//     private String message;
//     private List<Notification> result;

//     public String getStatus() { return status; }
//     public void setStatus(String status) { this.status = status; }
//     public List<Notification> getResult() { return result; }
//     public String getMessage() { return message; }
//     public void setMessage(String message) { this.message = message; }
//     public void setResult(List<Notification> result) { this.result = result; }

//     public static class Notification {
//         private String title;
//         private String notification_id;
//         private String type;
//         private boolean is_read;
//         private String create_Date;
//         private String message;
//         private String url;
//         private String product;

//         public String getTitle() { return title; }
//         public void setTitle(String title) { this.title = title; }
//         public String getNotification_id() { return notification_id; }
//         public void setNotification_id(String notification_id) { this.notification_id = notification_id; }
//         public String getType() { return type; }
//         public void setType(String type) { this.type = type; }
//         public boolean isIs_read() { return is_read; }
//         public void setIs_read(boolean is_read) { this.is_read = is_read; }
//         public String getCreate_Date() { return create_Date; }
//         public void setCreate_Date(String create_Date) { this.create_Date = create_Date; }
//         public String getMessage() { return message; }
//         public void setMessage(String message) { this.message = message; }
//         public String getUrl() { return url; }
//         public void setUrl(String url) { this.url = url; }
//         public String getProduct() { return product; }
//         public void setProduct(String product) { this.product = product; }
//     }
// }

package com.innov4africa.api_gateway.model;

import java.util.List;

public class IShopNotificationResponse {
    private String status;
    private String message;
    private String code; // Nouveau champ pour le code d'erreur
    private List<Notification> result;

    // Getters et Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public List<Notification> getResult() { return result; }
    public void setResult(List<Notification> result) { this.result = result; }

    // Méthode utilitaire pour créer une réponse d'erreur
    public static IShopNotificationResponse errorResponse(String code, String message) {
        IShopNotificationResponse response = new IShopNotificationResponse();
        response.setStatus("error");
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static class Notification {
        private String title;
        private String notification_id;
        private String type;
        private boolean is_read;
        private String create_Date;
        private String message;
        private String url;
        private String product;

        // Getters et Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getNotification_id() { return notification_id; }
        public void setNotification_id(String notification_id) { this.notification_id = notification_id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isIs_read() { return is_read; }
        public void setIs_read(boolean is_read) { this.is_read = is_read; }
        public String getCreate_Date() { return create_Date; }
        public void setCreate_Date(String create_Date) { this.create_Date = create_Date; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getProduct() { return product; }
        public void setProduct(String product) { this.product = product; }
    }
}
