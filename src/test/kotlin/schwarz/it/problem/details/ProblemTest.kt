package schwarz.it.problem.details

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

class ProblemTest :
   FunSpec({
      test("Problem.of method with httpStatus code creates valid problem object") {
         val httpStatus = HttpStatusCode.BadRequest

         val problem = Problem.of(httpStatus)

         problem.status shouldBe httpStatus.value
         problem.title shouldBe httpStatus.description
         problem.type shouldBe "about:blank"
      }

      test("Problem.of method with httpStatus code and problemType creates valid problem object") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"
         val detail = "error occurred"

         val problem = Problem.of(httpStatus, problemType, detail, baseUrl)

         problem.status shouldBe httpStatus.value
         problem.title shouldBe httpStatus.description
         problem.type shouldBe "$baseUrl/$problemType"
         problem.detail shouldBe detail
      }

      test("Problem.of method with httpStatus code and problemType but no detail creates valid problem object") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"

         val problem = Problem.of(httpStatus, problemType, null, baseUrl)

         problem.status shouldBe httpStatus.value
         problem.title shouldBe httpStatus.description
         problem.type shouldBe "$baseUrl/$problemType"
         problem.detail shouldBe null
      }

      test("Problem.of method with no base url creates valid problem object with relative problemType path") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val detail = "error occurred"

         val problem = Problem.of(httpStatus, problemType, detail)

         problem.status shouldBe httpStatus.value
         problem.title shouldBe httpStatus.description
         problem.type shouldBe "/$problemType"
         problem.detail shouldBe detail
      }

      test("Problem.of method with no details creates valid problem object") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"

         val problem = Problem.of(httpStatus, problemType, null, baseUrl)

         problem.status shouldBe httpStatus.value
         problem.title shouldBe httpStatus.description
         problem.type shouldBe "$baseUrl/$problemType"
      }

      test("Problem.Builder is building a valid problem object") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"
         val problemDetail = "error occurred"
         val problemInstance = "https://api.example.org/widget/example-instance"

         val problem =
            problem {
               status = httpStatus.value
               title = httpStatus.description
               detail = problemDetail
               instance = problemInstance
               type(problemType, baseUrl)
            }

         problem.status shouldBe httpStatus.value
         problem.title shouldBe httpStatus.description
         problem.type shouldBe "$baseUrl/$problemType"
         problem.detail shouldBe problemDetail
         problem.instance shouldBe problemInstance
      }

      test("Problem.Builder with no baseUrl is building a valid problem object with relative path problemType") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val problemDetail = "error occurred"
         val problemInstance = "https://api.example.org/widget/example-instance"

         val problem =
            problem {
               status = httpStatus.value
               title = httpStatus.description
               detail = problemDetail
               instance = problemInstance
               type(problemType)
            }

         problem.status shouldBe httpStatus.value
         problem.title shouldBe httpStatus.description
         problem.type shouldBe "/$problemType"
         problem.detail shouldBe problemDetail
         problem.instance shouldBe problemInstance
      }

      test("Problem.Builder add existing attribute throws IllegalArgumentException") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"
         val problemDetail = "error occurred"
         val problemInstance = "https://api.example.org/widget/example-instance"

         shouldThrow<IllegalArgumentException> {
            problem {
               status = httpStatus.value
               title = httpStatus.description
               detail = problemDetail
               instance = problemInstance
               type(problemType)
               extension("status", httpStatus.value)
            }
         }
      }

      test("Problem.Builder is building a valid problem object with extensions") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"
         val problemDetail = "error occurred"
         val problemInstance = "https://api.example.org/widget/example-instance"
         val errors = listOf("error1", "error2")

         val problem =
            problem {
               status = httpStatus.value
               title = httpStatus.description
               detail = problemDetail
               instance = problemInstance
               type(problemType, baseUrl)
               extension("errors", errors)
            }

         problem.status shouldBe httpStatus.value
         problem.title shouldBe httpStatus.description
         problem.type shouldBe "$baseUrl/$problemType"
         problem.detail shouldBe problemDetail
         problem.instance shouldBe problemInstance
         problem.extensions?.get("errors") shouldBe errors
      }

      test("toJson returns a valid json string") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"
         val problemDetail = "error occurred"
         val problemInstance = "https://api.example.org/widget/example-instance"

         val problem =
            problem {
               status = httpStatus.value
               title = httpStatus.description
               detail = problemDetail
               instance = problemInstance
               type(problemType, baseUrl)
            }

         val json = problem.toJson()

         json shouldBe
            "{\"type\":\"https://api.example.org/problem/test.html\",\"status\":400,\"title\":\"Bad Request\"," +
            "\"detail\":\"error occurred\",\"instance\":\"https://api.example.org/widget/example-instance\"}"
      }

      test("toJson returns a valid json string of problem object with list extension") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"
         val problemDetail = "error occurred"
         val problemInstance = "https://api.example.org/widget/example-instance"
         val errors = listOf("error1", "error2")

         val problem =
            problem {
               status = httpStatus.value
               title = httpStatus.description
               detail = problemDetail
               instance = problemInstance
               type(problemType, baseUrl)
               extension("errors", errors)
            }

         val json = problem.toJson()

         json shouldBe
            "{\"type\":\"https://api.example.org/problem/test.html\",\"status\":400,\"title\":\"Bad Request\"," +
            "\"detail\":\"error occurred\",\"instance\":\"https://api.example.org/widget/example-instance\"," +
            "\"errors\":[\"error1\",\"error2\"]}"
      }

      test("toJson returns a valid json string of problem object with object extension") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"
         val problemDetail = "error occurred"
         val problemInstance = "https://api.example.org/widget/example-instance"
         val error = ValidationError("error", "class/name")

         val problem =
            problem {
               status = httpStatus.value
               title = httpStatus.description
               detail = problemDetail
               instance = problemInstance
               type(problemType, baseUrl)
               extension("error", error)
            }

         val json = problem.toJson()

         json shouldBe
            "{\"type\":\"https://api.example.org/problem/test.html\",\"status\":400,\"title\":\"Bad Request\"," +
            "\"detail\":\"error occurred\",\"instance\":\"https://api.example.org/widget/example-instance\"," +
            "\"error\":{\"message\":\"error\",\"path\":\"class/name\"}}"
      }

      test("toJson returns a valid json string of problem object with list of object extension") {
         val httpStatus = HttpStatusCode.BadRequest
         val problemType = "test.html"
         val baseUrl = "https://api.example.org/problem"
         val problemDetail = "error occurred"
         val problemInstance = "https://api.example.org/widget/example-instance"
         val errors =
            listOf(
               ValidationError("error1", "class/name"),
               ValidationError("error2", "class/lastName"),
            )

         val problem =
            problem {
               status = httpStatus.value
               title = httpStatus.description
               detail = problemDetail
               instance = problemInstance
               type(problemType, baseUrl)
               extension("errors", errors)
            }

         val json = problem.toJson()

         json shouldBe
            "{\"type\":\"https://api.example.org/problem/test.html\",\"status\":400,\"title\":\"Bad Request\"," +
            "\"detail\":\"error occurred\",\"instance\":\"https://api.example.org/widget/example-instance\"," +
            "\"errors\":[{\"message\":\"error1\",\"path\":\"class/name\"},{\"message\":\"error2\"," +
            "\"path\":\"class/lastName\"}]}"
      }

      test("toJson returns a valid json string with only non null values") {
         val problem = Problem.of(HttpStatusCode.BadRequest)

         val json = problem.toJson()

         json shouldBe "{\"type\":\"about:blank\",\"status\":400,\"title\":\"Bad Request\"}"
      }
   })

@Serializable
data class ValidationError(
   val message: String,
   val path: String,
)
