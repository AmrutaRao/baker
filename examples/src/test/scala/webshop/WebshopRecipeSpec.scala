package webshop

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.ing.baker.compiler.RecipeCompiler
import com.ing.baker.runtime.scaladsl.Baker
import org.scalatest.FlatSpec

class WebshopRecipeSpec extends FlatSpec {

  val system: ActorSystem = ActorSystem("baker-webshop-system")

  val materializer: Materializer = ActorMaterializer()(system)

  val baker: Baker = Baker.akkaLocalDefault(system, materializer)

  "The WebshopRecipe" should "compile the recipe without errors" in {
    RecipeCompiler.compileRecipe(WebshopRecipe.recipe)
  }

  it should "visualize the recipe" in {
    val compiled = RecipeCompiler.compileRecipe(WebshopRecipe.recipe)
    val viz: String = compiled.getRecipeVisualization
    println(Console.GREEN + s"Recipe visualization, paste this into webgraphviz.com:")
    println(viz + Console.RESET)
  }

  "The WebshopRecipeReflection" should "compile the recipe without errors" in {
    RecipeCompiler.compileRecipe(WebshopRecipeReflection.recipe)
  }

  it should "visualize the recipe" in {
    val compiled = RecipeCompiler.compileRecipe(WebshopRecipeReflection.recipe)
    val viz: String = compiled.getRecipeVisualization
    println(Console.GREEN + s"Recipe visualization, paste this into webgraphviz.com:")
    println(viz + Console.RESET)
  }
}
