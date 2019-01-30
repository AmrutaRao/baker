package com.ing.baker.baas

import java.util.UUID

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.ing.baker.baas.BaasSpec.{InteractionOne, _}
import com.ing.baker.baas.server.BaasServer
import com.ing.baker.compiler.RecipeCompiler
import com.ing.baker.il.CompiledRecipe
import com.ing.baker.recipe.scaladsl
import com.ing.baker.recipe.scaladsl._
import com.ing.baker.runtime.core.{AkkaBaker, Baker, BakerProvider, BakerResponse, ProcessState, RuntimeEvent, SensoryEventStatus}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class BaasSpec extends TestKit(ActorSystem("BAASSpec")) with WordSpecLike with Matchers with BeforeAndAfterAll {
  def actorSystemName: String = "BAASSpec"

  val baasHost = "localhost"
  val baasPort = 8081

  // Startup a empty BAAS cluster
  val baker = new AkkaBaker()(system)
  val baasAPI: BaasServer = new BaasServer(baker, baasHost, baasPort)(system)

  // Start a BAAS API
  val baasBaker: Baker = BakerProvider()

  // implementations
  val localImplementations: Seq[AnyRef] = Seq(InteractionOne(), InteractionTwo())


  override def beforeAll() {
    Await.result(baasAPI.start(), 10 seconds)
    baasBaker.addImplementations(localImplementations)
  }

  override def afterAll() {
    Await.result(baasAPI.stop(), 10 seconds)
  }

  "Happy flow simple recipe BAAS" in {

    val recipeName = "simpleRecipe" + UUID.randomUUID().toString
    val recipe: Recipe = setupSimpleRecipe(recipeName)
    val compiledRecipe: CompiledRecipe = RecipeCompiler.compileRecipe(recipe)
    val recipeId = baasBaker.addRecipe(compiledRecipe)

    val requestId = UUID.randomUUID().toString

    baasBaker.bake(recipeId, requestId)

    val sensoryEventStatusResponse: SensoryEventStatus =
      baasBaker.processEvent(requestId, InitialEvent("initialIngredient"))
    sensoryEventStatusResponse shouldBe SensoryEventStatus.Completed

    val processState: ProcessState = baasBaker.getProcessState(requestId)

    processState.ingredients.keys should contain("initialIngredient")
    processState.ingredients.keys should contain("interactionOneIngredient")

    val events: Seq[RuntimeEvent] = baasBaker.events(requestId)

//    println(s"events: $events")
//    println(s"procesState : ${processState.ingredients}")

    val visualState = baasBaker.getVisualState(requestId)

    println(visualState)

    val response: BakerResponse = baasBaker.processEventAsync(requestId, InitialEvent("initialIngredient"))

    println(response.confirmAllEvents(5.second))

  }
}

object BaasSpec {

  val initialIngredient = Ingredient[String]("initialIngredient")
  val interactionOneIngredient = Ingredient[String]("interactionOneIngredient")

  val initialEvent = Event("InitialEvent", initialIngredient)
  case class InitialEvent(initialIngredient: String)
  case class InteractionOneEvent(interactionOneIngredient: String)

  val interactionOne =
    Interaction(
      name = "InteractionOne",
      inputIngredients = Seq(processId, initialIngredient),
      output = Seq(Event[InteractionOneEvent]))

  case class InteractionOne() {
    def name: String = "InteractionOne"
    def apply(processId: String, initialIngredient: String): InteractionOneEvent = {
      println("Executing interactionOne")
      InteractionOneEvent(initialIngredient)
    }
  }

  case class InteractionTwo() {
    def name: String = "InteractionTwo"
    def apply(processId: String, initialIngredient: String): String = {
      println("Executing InteractionTwo")
      initialIngredient
    }
  }

  def setupSimpleRecipe(name: String): scaladsl.Recipe = {
    scaladsl.Recipe(name)
      .withInteraction(interactionOne)
      .withSensoryEvent(initialEvent)
  }

}
