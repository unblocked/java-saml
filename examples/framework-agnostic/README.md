# Framework-Agnostic SAML Usage Examples

This directory contains examples showing how to use the java-saml library with different HTTP frameworks using the new framework-agnostic API introduced in version 2.11.0.

## Overview

Starting with version 2.11.0, java-saml provides a framework-agnostic API that allows you to use SAML authentication with any HTTP framework, not just Jakarta Servlets. This is achieved through the `HttpContext` abstraction layer.

## Key Interfaces

- **`HttpRequestContext`**: Framework-agnostic representation of HTTP request data
- **`HttpResponseContext`**: Framework-agnostic representation of HTTP response functionality  
- **`HttpContext`**: Combines request and response contexts
- **`HttpContextFactory`**: Factory for creating contexts from framework-specific objects

## Usage Patterns

### 1. Direct HttpContext Usage

```java
// Create your own HttpContext implementation or use mock for testing
MockHttpContext context = new MockHttpContext("http://localhost:8080/saml/login", Map.of());
Saml2Settings settings = new SettingsBuilder().fromFile("saml.properties").build();

Auth auth = new Auth(settings, context);
auth.login("/dashboard");

// Check if redirect was sent
String redirectUrl = context.getMockResponse().getRedirectLocation();
```

### 2. Factory-Based Usage

```java
// Use a factory to convert framework-specific objects
HttpContextFactory factory = new ServletHttpContextFactory();
Saml2Settings settings = new SettingsBuilder().fromFile("saml.properties").build();

// For servlet-based frameworks
Auth auth = new Auth(settings, factory, httpServletRequest, httpServletResponse);

// For single-object frameworks like Ktor
Auth auth = new Auth(settings, factory, ktorApplicationCall);
```

## Framework Examples

### Jakarta Servlets (Backward Compatible)

```java
// Old way (still works)
Auth auth = new Auth(request, response);

// New way (recommended)
ServletHttpContextFactory factory = new ServletHttpContextFactory();
Auth auth = new Auth(settings, factory, request, response);
```

### Ktor 3.x

```java
// In your Ktor route handler
KtorHttpContextFactory factory = new KtorHttpContextFactory();
Saml2Settings settings = new SettingsBuilder().fromFile("saml.properties").build();

routing {
    get("/saml/login") {
        Auth auth = new Auth(settings, factory, call);
        auth.login("/dashboard");
    }
    
    post("/saml/acs") {
        Auth auth = new Auth(settings, factory, call);
        auth.processResponse();
        if (auth.isAuthenticated()) {
            // Handle successful authentication
            call.respondText("Welcome " + auth.getNameId());
        }
    }
}
```

### Spring WebFlux

```java
// Custom WebFlux adapter (user-implemented)
WebFluxHttpContextFactory factory = new WebFluxHttpContextFactory();
Saml2Settings settings = new SettingsBuilder().fromFile("saml.properties").build();

@GetMapping("/saml/login")
public Mono<Void> login(ServerHttpRequest request, ServerHttpResponse response) {
    Auth auth = new Auth(settings, factory, request, response);
    auth.login("/dashboard");
    return Mono.empty();
}
```

### Testing

```java
@Test
public void testSamlLogin() {
    // Create mock context for testing
    MockHttpContext context = new MockHttpContext("http://localhost/login", Map.of());
    Saml2Settings settings = new SettingsBuilder().fromFile("test-saml.properties").build();
    
    Auth auth = new Auth(settings, context);
    auth.login("/dashboard");
    
    // Verify redirect was sent
    assertTrue(context.getMockResponse().wasRedirectSent());
    String redirectUrl = context.getMockResponse().getRedirectLocation();
    assertTrue(redirectUrl.contains("SAMLRequest="));
}
```

## Migration Guide

### From Servlet-Only to Framework-Agnostic

1. **Keep existing code working**: All existing servlet-based constructors continue to work
2. **Gradually migrate**: Use new constructors for new code
3. **Add framework support**: Implement `HttpContextFactory` for your framework
4. **Test thoroughly**: Use mock implementations for unit testing

### Creating Custom Adapters

To support a new framework, implement the three core interfaces:

```java
public class MyFrameworkHttpContextFactory implements HttpContextFactory {
    @Override
    public HttpContext createContext(Object request, Object response) {
        return new MyFrameworkHttpContext((MyFrameworkRequest) request, (MyFrameworkResponse) response);
    }
}

public class MyFrameworkHttpContext implements HttpContext {
    // Implement getRequest() and getResponse()
}

public class MyFrameworkHttpRequestContext implements HttpRequestContext {
    // Implement all request methods
}

public class MyFrameworkHttpResponseContext implements HttpResponseContext {
    // Implement all response methods
}
```

## Benefits

1. **Framework Independence**: Use SAML with any HTTP framework
2. **Easy Testing**: Mock implementations for unit testing
3. **Backward Compatibility**: Existing servlet code continues to work
4. **Clean Architecture**: Separation between SAML logic and HTTP framework
5. **Future Proof**: Easy to add support for new frameworks

## See Also

- [Servlet Example](servlet-example/)
- [Ktor Example](ktor-example/)  
- [Testing Example](testing-example/)
- [API Documentation](../../docs/api/)
