package com.postmage.plugins

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.postmage.util.Constants
import io.ktor.server.application.*

fun Application.configureSecurity() {
    
    authentication {
            jwt {
                val jwtAudience = this@configureSecurity.environment.config.property("jwt.audience").getString()
                realm = this@configureSecurity.environment.config.property("jwt.realm").getString()


                val algorithm = Algorithm.HMAC256(Constants.SERVER_TOKEN_SECRET_KEY)
                val verifier: JWTVerifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build()

                verifier(
                    verifier
                    /*
                    JWT
                        .require(Algorithm.HMAC256(Constants.secretKey))
                        .withAudience(jwtAudience)
                        .withIssuer(this@configureSecurity.environment.config.property("jwt.domain").getString())
                        .build()

                     */
                )
                validate { credential ->
                    if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
                }
            }
        }

}
