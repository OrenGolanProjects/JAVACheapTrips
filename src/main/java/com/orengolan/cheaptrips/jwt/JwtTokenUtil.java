package com.orengolan.cheaptrips.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for handling JWT (JSON Web Token) generation, validation, and parsing.
 * This class is responsible for creating JWT tokens, extracting information from tokens,
 * and validating the integrity and expiration of tokens.
 */
@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;

    // The secret key used for signing and validating JWT tokens
    private final String secret = "AppSecrettttt1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

    // The validity period of JWT tokens in seconds (5 hours in this case)
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    /**
     * Extracts the username from a given JWT token.
     *
     * @param token The JWT token from which to extract the username.
     * @return The username parsed from the token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Retrieves the expiration date from a given JWT token.
     *
     * @param token The JWT token from which to retrieve the expiration date.
     * @return The expiration date parsed from the token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Retrieves a specific claim from a given JWT token using a provided claims resolver function.
     *
     * @param token          The JWT token from which to retrieve the claim.
     * @param claimsResolver The function to resolve the desired claim from the token's payload.
     * @param <T>            The type of the claim.
     * @return The resolved claim from the token.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT token to obtain all claims from its payload.
     *
     * @param token The JWT token to parse.
     * @return All claims from the token's payload.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * Checks whether a given JWT token has expired.
     *
     * @param token The JWT token to check for expiration.
     * @return True if the token has expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generates a new JWT token based on the provided user details.
     *
     * @param userDetails The user details used to generate the token.
     * @return The newly generated JWT token.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }
    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    /**
     * Generates a new JWT token with the specified claims and subject (username).
     *
     * @param claims  The claims to include in the token's payload.
     * @param subject The subject (username) for whom the token is generated.
     * @return The newly generated JWT token.
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setSubject(subject).setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).setId(subject).compact();
    }

    /**
     * Validates a JWT token by checking its expiration and matching the username against the provided user details.
     *
     * @param token        The JWT token to validate.
     * @param userDetails The user details against which to validate the token.
     * @return True if the token is valid, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
