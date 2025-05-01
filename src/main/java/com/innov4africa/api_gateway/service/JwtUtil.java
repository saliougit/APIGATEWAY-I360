package com.innov4africa.api_gateway.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.innov4africa.api_gateway.model.DomaineResponse;
import com.innov4africa.api_gateway.model.IShopInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtil {

    @Value("${jwt.secret:myDefaultSecretKey12345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:3600000}") // 1 heure par défaut
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Génère un token avec des claims supplémentaires pour IPay
    public String generateIpayToken(String username, String ipayToken, String telephone, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("ipayToken", ipayToken);
        claims.put("telephone", telephone);
        claims.put("userId", userId);
        return createToken(claims, username);
    }
    
    // Génère un token JWT avec toutes les informations IPay y compris l'ID du compte
    public String generateIpayTokenWithAccount(String username, String ipayToken, String telephone, 
                                          String userId, String accountIdIPay) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("ipayToken", ipayToken);
        claims.put("telephone", telephone);
        claims.put("userId", userId);
        claims.put("accountIdIPay", accountIdIPay);
        return createToken(claims, username);
    }
    
    // Version améliorée avec nom et prénom pour les besoins du SMS Pay
    public String generateIpayTokenWithUserInfo(String username, String ipayToken, String telephone, String userId, String nom, String prenom) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("ipayToken", ipayToken);
        claims.put("telephone", telephone);
        claims.put("userId", userId);
        claims.put("nom", nom);
        claims.put("prenom", prenom);
        return createToken(claims, username);
    }

    // Version complète avec toutes les informations
    public String generateCompleteIpayToken(String username, String ipayToken, String telephone, String userId, 
                                         String accountIdIPay, String nom, String prenom) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("ipayToken", ipayToken);
        claims.put("telephone", telephone);
        claims.put("userId", userId);
        claims.put("accountIdIPay", accountIdIPay);
        claims.put("nom", nom);
        claims.put("prenom", prenom);
        return createToken(claims, username);
    }

    // Version basique (conservée pour compatibilité)
    public String generateToken(String username) {
        return createToken(new HashMap<>(), username);
    }

    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // Méthode pour générer un token JWT avec des informations iShop
    public String generateTokenWithIShopInfo(String username, String ipayToken, String telephone, 
                                        String userId, IShopInfo ishopInfo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("ipayToken", ipayToken);
        claims.put("telephone", telephone);
        claims.put("userId", userId);
        
        // Ajout des infos iShop seulement si c'est un seller
        if ("Seller".equals(ishopInfo.getUser_type())) {
            claims.put("ishopUserId", ishopInfo.getUser_id());
            claims.put("userType", ishopInfo.getUser_type());
            claims.put("sellerCredit", ishopInfo.getCredit());
            
            if (ishopInfo.getDomaineList() != null && !ishopInfo.getDomaineList().isEmpty()) {
                claims.put("primaryDomaine", ishopInfo.getDomaineList().get(0).getLibelle());
            }
        }
        
        return createToken(claims, username);
    }

    public IShopInfo extractIShopInfo(String token) {
        Claims claims = extractAllClaims(token);
        IShopInfo info = new IShopInfo();
        info.setUser_id(claims.get("ishopUserId", Integer.class));
        info.setUser_type(claims.get("userType", String.class));
        info.setCredit(claims.get("sellerCredit", Integer.class));
        
        if (claims.containsKey("primaryDomaine")) {
            DomaineResponse domaine = new DomaineResponse();
            domaine.setLibelle(claims.get("primaryDomaine", String.class));
            info.setDomaineList(List.of(domaine));
        }
        
        return info;
    }

    // Méthodes d'extraction spécifiques à IPay
    public String extractIpayToken(String token) {
        return extractClaim(token, claims -> claims.get("ipayToken", String.class));
    }

    public String extractTelephone(String token) {
        return extractClaim(token, claims -> claims.get("telephone", String.class));
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }
    
    // Méthode pour extraire l'ID du compte IPay du token JWT
    public String extractAccountIdIPay(String token) {
        return extractClaim(token, claims -> claims.get("accountIdIPay", String.class));
    }
    
    // Nouvelles méthodes pour extraire le nom et le prénom du token JWT
    public String extractNom(String token) {
        return extractClaim(token, claims -> claims.get("nom", String.class));
    }
    
    public String extractPrenom(String token) {
        return extractClaim(token, claims -> claims.get("prenom", String.class));
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    // Extract iShop information from token
    public String extractIShopUserId(String token) {
        return extractClaim(token, claims -> claims.get("ishopUserId", String.class));
    }

    public String extractUserType(String token) {
        return extractClaim(token, claims -> claims.get("userType", String.class));
    }

    public String extractSellerType(String token) {
        return extractClaim(token, claims -> claims.get("sellerType", String.class));
    }

    public String extractDomaines(String token) {
        return extractClaim(token, claims -> claims.get("domaines", String.class));
    }

    // Méthodes existantes conservées
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Récupère la date d'expiration d'un token JWT
     * @param token Le token JWT
     * @return La date d'expiration du token
     */
    public Date getExpirationDateFromToken(String token) {
        // Alias pour extractExpiration pour rester compatible avec le nom utilisé dans AuthService
        return extractExpiration(token);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}