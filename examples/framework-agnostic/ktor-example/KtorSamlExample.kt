package com.onelogin.saml2.examples.ktor

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.onelogin.saml2.Auth
import com.onelogin.saml2.ktor.KtorHttpContextFactory
import com.onelogin.saml2.settings.SettingsBuilder

/**
 * Example showing how to use java-saml with Ktor 3.x using the framework-agnostic API.
 * 
 * This example demonstrates:
 * 1. SAML SSO initiation
 * 2. SAML response processing
 * 3. Single Logout (SLO)
 * 
 * Prerequisites:
 * - Configure your SAML settings in saml.properties
 * - Set up your Identity Provider (IdP)
 * - Add the java-saml-ktor adapter to your dependencies
 */

fun main() {
    embeddedServer(Netty, port = 8080) {
        configureSaml()
    }.start(wait = true)
}

fun Application.configureSaml() {
    val factory = KtorHttpContextFactory()
    val settings = SettingsBuilder().fromFile("saml.properties").build()
    
    routing {
        // Home page
        get("/") {
            call.respondText("""
                <html>
                <body>
                    <h1>Ktor SAML Example</h1>
                    <p><a href="/saml/login">Login with SAML</a></p>
                    <p><a href="/saml/logout">Logout</a></p>
                </body>
                </html>
            """.trimIndent(), io.ktor.http.ContentType.Text.Html)
        }
        
        // SAML SSO initiation
        get("/saml/login") {
            try {
                val auth = Auth(settings, factory, call)
                
                // Optional: specify return URL
                val returnTo = call.request.queryParameters["returnTo"] ?: "/dashboard"
                
                // Initiate SAML login
                auth.login(returnTo)
                
                // The auth.login() method will automatically redirect to the IdP
                // No additional response needed here
                
            } catch (e: Exception) {
                call.respondText("SAML login error: ${e.message}", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }
        
        // SAML Assertion Consumer Service (ACS) - where IdP sends the response
        post("/saml/acs") {
            try {
                val auth = Auth(settings, factory, call)
                
                // Process the SAML response from IdP
                auth.processResponse()
                
                if (auth.isAuthenticated) {
                    // User is authenticated, get user info
                    val nameId = auth.nameId
                    val attributes = auth.attributes
                    
                    // Store user session (implement your session management here)
                    // For example, using Ktor sessions:
                    // call.sessions.set(UserSession(nameId, attributes))
                    
                    // Redirect to dashboard or return URL
                    val returnTo = auth.getLastRelayState() ?: "/dashboard"
                    call.respondRedirect(returnTo)
                    
                } else {
                    // Authentication failed
                    val errors = auth.errors
                    call.respondText(
                        "SAML authentication failed: ${errors.joinToString(", ")}", 
                        status = io.ktor.http.HttpStatusCode.Unauthorized
                    )
                }
                
            } catch (e: Exception) {
                call.respondText("SAML ACS error: ${e.message}", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }
        
        // Dashboard (protected page)
        get("/dashboard") {
            // In a real application, check if user is authenticated
            // For this example, we'll just show a simple page
            call.respondText("""
                <html>
                <body>
                    <h1>Dashboard</h1>
                    <p>Welcome! You are logged in via SAML.</p>
                    <p><a href="/saml/logout">Logout</a></p>
                </body>
                </html>
            """.trimIndent(), io.ktor.http.ContentType.Text.Html)
        }
        
        // SAML Single Logout (SLO) initiation
        get("/saml/logout") {
            try {
                val auth = Auth(settings, factory, call)
                
                // Optional: specify return URL after logout
                val returnTo = call.request.queryParameters["returnTo"] ?: "/"
                
                // Initiate SAML logout
                auth.logout(returnTo)
                
                // The auth.logout() method will automatically redirect to the IdP
                // No additional response needed here
                
            } catch (e: Exception) {
                call.respondText("SAML logout error: ${e.message}", status = io.ktor.http.HttpStatusCode.InternalServerError)
            }
        }
        
        // SAML Single Logout Service (SLS) - where IdP sends logout requests/responses
        route("/saml/sls") {
            get {
                handleSls(factory, settings, call)
            }
            post {
                handleSls(factory, settings, call)
            }
        }
    }
}

suspend fun handleSls(factory: KtorHttpContextFactory, settings: com.onelogin.saml2.settings.Saml2Settings, call: ApplicationCall) {
    try {
        val auth = Auth(settings, factory, call)
        
        // Process logout request or response
        val redirectUrl = auth.processSLO(true, null, false)
        
        if (redirectUrl != null) {
            // If there's a redirect URL, redirect to it
            call.respondRedirect(redirectUrl)
        } else {
            // Logout completed, clear session and redirect to home
            // call.sessions.clear<UserSession>()
            call.respondRedirect("/")
        }
        
    } catch (e: Exception) {
        call.respondText("SAML SLS error: ${e.message}", status = io.ktor.http.HttpStatusCode.InternalServerError)
    }
}

/**
 * Example user session data class
 * In a real application, you would use Ktor's session management
 */
data class UserSession(
    val nameId: String,
    val attributes: Map<String, List<String>>
)
