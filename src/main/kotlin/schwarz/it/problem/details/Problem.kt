package schwarz.it.problem.details

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

/**
 * After RFC 9457 the type is expected to be 'about:blank' if not present
 */
private const val TYPE_DEFAULT = "about:blank"

/**
 * Construct a Problem Detail after RFC 9457
 */
public fun problem(block: ProblemBuilder.() -> Unit): Problem = ProblemBuilder().apply(block).build()

/**
 * Builder for a problem detail including all mutable components
 * Problem implements RFC 9457 (https://datatracker.ietf.org/doc/rfc9457/)
 *
 * @property status (optional) HTTP status code
 * @property title (optional) human-readable summary of the problem type
 * @property detail (optional) explanation specific to this occurrence
 * @property instance (optional) identifies the specific occurrence
 */
class ProblemBuilder(
   private var type: String = TYPE_DEFAULT,
   var status: Int? = null,
   var title: String? = null,
   var detail: String? = null,
   var instance: String? = null,
   private var extensions: MutableMap<String, Any> = mutableMapOf(),
) {
   private val reservedKeys =
      listOf(
         Problem::type.name,
         Problem::status.name,
         Problem::title.name,
         Problem::detail.name,
         Problem::instance.name,
      )

   /**
    * Sets the type of the problem
    * String containing a URI reference that identifies the problem type. 'about:blank' if not present
    * @param type relative path reference to type
    * @param baseUrl (optional) given for absolute path to type (recommended but not mandatory)
    */
   fun type(
      type: String,
      baseUrl: String? = "",
   ) = apply {
      this.type = "$baseUrl/$type"
   }

   /**
    * Adds extensions to the base problem-detail
    * @param key represents the property name that will later be used for the value from the serializer
    * @param value any kind of type can be used here (also lists), custom objects need to be serializable
    */
   fun extension(
      key: String,
      value: Any,
   ) = apply {
      require(!reservedKeys.contains(key)) { "$key is reserved by existing attributes in the problem class" }
      extensions[key] = value
   }

   /**
    * Builds a Problem object out of given values
    */
   fun build(): Problem =
      Problem(
         type,
         status,
         title,
         detail,
         instance,
         extensions,
      )
}

/**
 * Problem object after RFC 9457
 * @property type
 * @property status (optional) HTTP status code
 * @property title (optional) human-readable summary of the problem type
 * @property detail (optional) explanation specific to this occurrence
 * @property instance (optional) identifies the specific occurrence
 * @property extensions (optional) adds additional properties to the Problem object
 */
class Problem(
   val type: String,
   val status: Int? = null,
   val title: String? = null,
   val detail: String? = null,
   val instance: String? = null,
   val extensions: Map<String, Any>? = null,
) {
   /**
    * Serializes the Problem object to a JSON string
    */
   fun toJson(): String = Json.encodeToString(ProblemSerializer(), this)

   companion object {
      /**
       * Creates a problem detail just with an HTTP status code (not a full problem detail since type will be 'about:blank')
       * @param httpStatusCode HTTP code of the problem
       */
      fun of(httpStatusCode: HttpStatusCode): Problem =
         problem {
            status = httpStatusCode.value
            title = httpStatusCode.description
         }

      /**
       * Creates a problem detail with an HTTP status code, type (with absolute path) and detail
       * @param httpStatusCode HTTP code of the problem
       * @param problemType relative path to the problem type
       * @param problemDetail detail description of the problem
       * @param baseUrl url to make type route to absolute path
       */
      fun of(
         httpStatusCode: HttpStatusCode,
         problemType: String,
         problemDetail: String? = null,
         baseUrl: String? = "",
      ) = problem {
         status = httpStatusCode.value
         title = httpStatusCode.description
         detail = problemDetail
         type(problemType, baseUrl)
      }
   }
}
