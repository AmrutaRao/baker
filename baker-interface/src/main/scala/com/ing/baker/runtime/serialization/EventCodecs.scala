package com.ing.baker.runtime.serialization

import com.ing.baker.il.CompiledRecipe
import com.ing.baker.il.failurestrategy.ExceptionStrategyOutcome
import com.ing.baker.runtime.common.RejectReason
import com.ing.baker.runtime.scaladsl.{BakerEvent, EventInstance}
import com.ing.baker.types
import io.circe.Encoder._
import io.circe._
import io.circe.generic.semiauto._

object EventCodecs {

  implicit val valuesEncoder: Encoder[types.Value] =
    Encoder.instance {
      case types.NullValue =>
        Json.Null
      case types.ListValue(entries) =>
        encodeList(valuesEncoder)(entries)
      case types.RecordValue(entries) =>
        encodeMap(KeyEncoder.encodeKeyString, valuesEncoder)(entries)
      case types.PrimitiveValue(value) =>
        encodeString(value.toString)
    }

  implicit val eventInstanceEncoder: Encoder[EventInstance] = deriveEncoder[EventInstance]
  implicit val rejectReasonEncoder: Encoder[RejectReason] = encodeString.contramap(_.toString)
  implicit val exceptionEncoder: Encoder[ExceptionStrategyOutcome] = deriveEncoder[ExceptionStrategyOutcome]
  implicit val throwableEncoder: Encoder[Throwable] = encodeString.contramap[Throwable](_.getMessage)
  implicit val compiledRecipeEncoder: Encoder[CompiledRecipe] =
    encodeString.contramap[CompiledRecipe](_.getRecipeVisualization.toString)

  implicit val bakerEventEncoder: Encoder[BakerEvent] = deriveEncoder[BakerEvent]
}