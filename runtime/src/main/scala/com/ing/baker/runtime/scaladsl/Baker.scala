package com.ing.baker.runtime.scaladsl

import akka.actor.{ActorSystem, Address}
import akka.stream.Materializer
import cats.data.NonEmptyList
import com.ing.baker.runtime.akka.{AkkaBaker, AkkaBakerConfig}
import com.ing.baker.runtime.common
import com.ing.baker.runtime.common.LanguageDataStructures.ScalaApi
import com.ing.baker.runtime.common.SensoryEventStatus
import com.typesafe.config.Config

import scala.concurrent.Future

object Baker {

  def akkaLocalDefault(actorSystem: ActorSystem, materializer: Materializer): AkkaBaker =
    new AkkaBaker(AkkaBakerConfig.localDefault(actorSystem, materializer))

  def akkaClusterDefault(seedNodes: NonEmptyList[Address], actorSystem: ActorSystem, materializer: Materializer): AkkaBaker =
    new AkkaBaker(AkkaBakerConfig.clusterDefault(seedNodes, actorSystem, materializer))

  def akka(config: AkkaBakerConfig): AkkaBaker =
    new AkkaBaker(config)

  def akka(config: Config, actorSystem: ActorSystem, materializer: Materializer): AkkaBaker =
    new AkkaBaker(AkkaBakerConfig.from(config, actorSystem, materializer))

}

/**
  * The Baker is the component of the Baker library that runs one or multiples recipes.
  * For each recipe a new instance can be baked, sensory events can be send and state can be inquired upon
  */
trait Baker extends common.Baker[Future] with ScalaApi {

  override type SensoryEventResultType = SensoryEventResult

  override type SensoryEventMomentsType = SensoryEventMoments

  override type RuntimeEventType = RuntimeEvent

  override type ProcessStateType = ProcessState

  override type InteractionImplementationType = InteractionImplementation

  override type BakerEventType = BakerEvent

  override type ProcessMetadataType = ProcessMetadata

  override type RecipeInformationType = RecipeInformation

  def fireSensoryEventReceived(processId: String, event: RuntimeEvent): Future[SensoryEventStatus] =
    fireSensoryEventReceived(processId, event, None)

  def fireSensoryEventCompleted(processId: String, event: RuntimeEvent): Future[SensoryEventResultType] =
    fireSensoryEventCompleted(processId, event, None)

  def fireSensoryEvent(processId: String, event: RuntimeEvent): SensoryEventMomentsType =
    fireSensoryEvent(processId, event, None)

  def fireSensoryEventReceived(processId: String, event: RuntimeEvent, correlationId: String): Future[SensoryEventStatus] =
    fireSensoryEventReceived(processId, event, Some(correlationId))

  def fireSensoryEventCompleted(processId: String, event: RuntimeEvent, correlationId: String): Future[SensoryEventResultType] =
    fireSensoryEventCompleted(processId, event, Some(correlationId))

  def fireSensoryEvent(processId: String, event: RuntimeEvent, correlationId: String): SensoryEventMomentsType =
    fireSensoryEvent(processId, event, Some(correlationId))

}
