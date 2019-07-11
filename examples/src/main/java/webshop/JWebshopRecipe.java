package webshop;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;
import com.ing.baker.recipe.javadsl.Recipe;

import static com.ing.baker.recipe.javadsl.InteractionDescriptor.of;

public class JWebshopRecipe {

    public static class OrderPlaced {

        public final String orderId;
        public final String[] items;

        public OrderPlaced(String orderId, String[] items) {
            this.orderId = orderId;
            this.items = items;
        }
    }

    public interface ReserveItems extends Interaction {

        interface ReserveItemsOutcome {
        }

        class OrderHadUnavailableItems implements ReserveItemsOutcome {

            public final String[] unavailableItems;

            public OrderHadUnavailableItems(String[] unavailableItems) {
                this.unavailableItems = unavailableItems;
            }
        }

        class ItemsReserved implements ReserveItemsOutcome {

            public final String[] reservedItems;

            public ItemsReserved(String[] reservedItems) {
                this.reservedItems = reservedItems;
            }
        }

        @FiresEvent(oneOf = {OrderHadUnavailableItems.class, ItemsReserved.class})
        ReserveItemsOutcome apply(@RequiresIngredient("orderId") String id, @RequiresIngredient("items") String[] items);
    }

    public final static Recipe recipe = new Recipe("WebshopRecipe")
            .withSensoryEvents(OrderPlaced.class)
            .withInteractions(of(ReserveItems.class));
}
