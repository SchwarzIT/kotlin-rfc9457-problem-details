# Kotlin-RFC9457-Problem-Details

[![SIT](https://img.shields.io/badge/SIT-awesome-blueviolet.svg)](https://jobs.schwarz)
![GitHub kotlin version](https://img.shields.io/badge/kotlin-2.0.20-green)
![License](https://img.shields.io/github/license/SchwarzIT/kotlin-rfc9457-problem-details)
![GitHub last commit](https://img.shields.io/github/last-commit/SchwarzIT/kotlin-rfc9457-problem-details)


The `Kotlin-RFC9457-Problem-Details` library implements `application/problem+json` according to [RFC 9457](https://datatracker.ietf.org/doc/rfc9457/) in Kotlin. Problem-Details provides all necessary interfaces and builders to comply with the RFC.

## Features
  - provides all necessary interfaces and serialisers to implement [RFC 9457](https://datatracker.ietf.org/doc/rfc9457/)
  - provides a centralised approach to error objects to normalise REST APIs
  - conforms to `application/problem+json`

## Installation

```kotlin
dependencies {
    implementation("io.github.schwarzit:kotlin-rfc9457-problem-details:$problemDetailsVersion")
}
```


## Usage

The library offers several ways to use the problem object. There are some static constructors and a builder that makes it really easy to use.

We recommend using the library in combination with the `io.ktor:ktor-server-status-pages` when using Ktor.

### Status Pages

The following example uses the problem library in order to handle an `IllegalArgumentException` thrown by your application within the status pages handler.

`respondProblem` is used in order to set the content type to `application/problem+json`.

```kotlin
install(StatusPages) {
    exception<IllegalArgumentException> { call, cause ->
        val httpStatus = HttpStatusCode.BadRequest
        val problem = Problem.of(
            httpStatus,
            "problem/illegal-argument-error.html",
            cause.message,
            "https://example.org"
        )
        call.respondProblem(httpStatus, problem)
    }
}
```


#### Serving Problem Types
Serving problem types can be easily done using the [static resources feature of ktor](https://ktor.io/docs/server-static-content.html#resources).
```kotlin
routing {
    staticResources("/problem", "problem-details")
}
```

### Static constructors
The library delivers two static constructors for easy use.

If you don't have defined a type yet, but want to conform to the standard the following constructor can be used:
```kotlin
Problem.of(HttpStatusCode.BadRequest)
```
This will produce in the following json
```json
{
  "type": "about:blank",
  "status": 400,
  "title": "Bad Request"
}
```

If you want to add some more information and a problem type the following can be used:
```kotlin
Problem.of(
    HttpStatusCode.BadRequest,
    "problem/illegal-argument-error.html",
    "Parameter is mandatory",
    "https://example.org"
)
```
This will produce in the following json
```json
{
  "type": "https://example.org/problem/illegal-argument-error.html",
  "status": 400,
  "title": "Bad Request",
  "detail": "Parameter is mandatory"
}
```

### Builder
If you need more flexibility this library also offers a builder that makes it possible to set all properties of the standard and add more if needed (also like the standard defines).

The builder can be used as follows:
```kotlin
val problem = problem {
    type("problem/illegal-argument-error.html", "https://example.org")
    status = 400
    title = "Bad Request"
    detail = "Invalid parameters were handed over"
    instance = "Instance reference"
    extension("errors", listOf("Name parameter is mandatory", "First name contains invalid characters"))
}
```
This will produce in the following json
```json
{
  "type": "https://example.org/problem/illegal-argument-error.html",
  "status": 400,
  "title": "Bad Request",
  "detail": "Invalid parameters were handed over",
  "instance": "Instance reference",
  "errors": [
    "Name parameter is mandatory",
    "First name contains invalid characters"
  ]
}
```



## Dependencies

- kotlin
- ktor serialization kotlinx json
- ktor server core
- kotest
- kotlin serialization plugin
