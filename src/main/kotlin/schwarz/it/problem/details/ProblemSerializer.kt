package schwarz.it.problem.details

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import kotlin.reflect.full.starProjectedType

/**
 * Serializer for the problem object
 */
class ProblemSerializer : KSerializer<Problem> {
   override val descriptor: SerialDescriptor = buildClassSerialDescriptor(Problem::class.simpleName!!)

   /**
    * No real deserializer implemented since it is only necessary to serialize the problem object
    */
   override fun deserialize(decoder: Decoder): Problem = problem { }

   /**
    * Custom serializer in order to handle the extensions of the problem object and deliver them as real properties
    */
   override fun serialize(
      encoder: Encoder,
      value: Problem,
   ) {
      val output =
         encoder as? JsonEncoder ?: throw SerializationException("Encoder could not be parsed to JsonEncoder")
      val problem = mapProblemToJsonObject(value)
      output.encodeJsonElement(problem)
   }

   private fun mapProblemToJsonObject(value: Problem): JsonObject {
      val elements = mutableMapOf<String, JsonElement>()
      elements[Problem::type.name] = JsonPrimitive(value.type)
      if (value.status != null) {
         elements[Problem::status.name] = JsonPrimitive(value.status)
      }
      if (!value.title.isNullOrBlank()) {
         elements[Problem::title.name] = JsonPrimitive(value.title)
      }
      if (!value.detail.isNullOrBlank()) {
         elements[Problem::detail.name] = JsonPrimitive(value.detail)
      }
      if (!value.instance.isNullOrBlank()) {
         elements[Problem::instance.name] = JsonPrimitive(value.instance)
      }
      if (!value.extensions.isNullOrEmpty()) {
         val extensions =
            value.extensions.map { (key, value) ->
               key to mapAnyToJsonElement(value)
            }
         elements += extensions
      }
      return JsonObject(elements)
   }

   private fun mapAnyToJsonElement(value: Any): JsonElement =
      if (value is Iterable<*>) {
         JsonArray(value.map { mapAnyToJsonElement(it!!) })
      } else {
         Json.encodeToJsonElement(serializer(value::class.starProjectedType), value)
      }
}
