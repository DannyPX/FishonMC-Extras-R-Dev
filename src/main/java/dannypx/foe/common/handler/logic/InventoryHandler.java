package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.helper.ItemStackHelper;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.item.FishingRodNbtObject;
import dannypx.foe.common.item.PetNbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryHandler {
    private static InventoryHandler INSTANCE = new InventoryHandler();

    public static InventoryHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new InventoryHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private final List<UUID> trackedFish = new ArrayList<>();
    private DefaultedList<ItemStack> snapshotInventory = DefaultedList.ofSize(0);
    private FishingRodNbtObject currentFishingRod = FishingRodNbtObject.empty();
    private PetNbtObject currentPet = PetNbtObject.empty();

    public List<UUID> getTrackedFish() {
        return trackedFish;
    }

    public DefaultedList<ItemStack> getSnapshotInventory() {
        return snapshotInventory;
    }

    protected void setCurrentFishingRod(FishingRodNbtObject currentFishingRod) {
        this.currentFishingRod = currentFishingRod;
    }

    public FishingRodNbtObject getCurrentFishingRod() {
        return this.currentFishingRod;
    }

    protected  void setCurrentPet(PetNbtObject currentPet) {
        this.currentPet = currentPet;
    }

    public PetNbtObject getCurrentPet() {
        return this.currentPet;
    }

    public boolean hasPet() {
        return this.currentPet.getItemStack() != ItemStack.EMPTY;
    }
    //endregion

    //region Methods
    public void tick() {
        if(minecraftClient.player != null) {
            ItemStack fishingRod = minecraftClient.player.getInventory().main.getFirst();
            if(!fishingRod.isEmpty() && !ItemStack.areItemsAndComponentsEqual(currentFishingRod.getItemStack(), fishingRod)) {
                Pair<Boolean, @Nullable FishingRodNbtObject> validatedFishingRod = ValidateItem.isFishingRod(fishingRod);
                if(validatedFishingRod.v1()) {
                    this.setCurrentFishingRod(validatedFishingRod.v2());
                }
            }

            if(ProfileDataHandler.instance().getProfileData().activePetSlot != -1) {
                ItemStack pet = minecraftClient.player.getInventory().main.get(ProfileDataHandler.instance().getProfileData().activePetSlot);
                if(!pet.isEmpty() && !ItemStack.areItemsAndComponentsEqual(currentPet.getItemStack(), pet)) {
                    Pair<Boolean, @Nullable PetNbtObject> validatedPet = ValidateItem.isPet(pet);
                    if(validatedPet.v1()) {
                        this.setCurrentPet(validatedPet.v2());
                    }
                }
            } else if(ProfileDataHandler.instance().getProfileData().activePetSlot == -1
                    && currentPet.getItemStack() != ItemStack.EMPTY
            ) {
                currentPet = PetNbtObject.empty();
            }
        }
    }

    public void onUseItem(Hand hand) {

        if(minecraftClient.player != null && ValidateItem.isFishingRod(minecraftClient.player.getStackInHand(hand)).v1()) {
            this.snapshotInventory();
        }
    }

    public void snapshotInventory() {
        if(minecraftClient.player != null) {

            snapshotInventory = ItemStackHelper.deepCopy(
                    minecraftClient.player.getInventory().main,
                    ItemStack.EMPTY,
                    stack -> stack.isEmpty() ? ItemStack.EMPTY : stack.copy()
            );
        }
    }

    public void addToTrackedFish(UUID uuid) {
        if (!trackedFish.contains(uuid)) {
            trackedFish.add(uuid);
        }
    }

    public boolean trackAllFish() {
        if(minecraftClient.player != null) {
            trackedFish.clear();
            minecraftClient.player.getInventory().main.forEach(itemStack -> {
                Pair<Boolean, FishNbtObject> validatedItem = ValidateItem.isFish(itemStack);
                if(validatedItem.v1() && validatedItem.v2().isOwn()) {
                    this.addToTrackedFish(validatedItem.v2().getID());
                }
            });
            LoggerHandler.info("Tracked Fish: " + trackedFish.size());
            return true;
        }
        return false;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "trackedFish", Pair.of(Text.literal("[trackedFish]"), TextHelper.literal(getTrackedFish())),
                "snapshotInventory", Pair.of(Text.literal("[snapshotInventory]"), TextHelper.literal(
                        ItemStackHelper.itemStackListToJson(getSnapshotInventory())
                )),
                "currentFishingRod", Pair.of(Text.literal("[currentFishingRod]"), TextHelper.literal(getCurrentFishingRod().getItemStack())),
                "currentPet", Pair.of(Text.literal("[currentPet]"), TextHelper.literal(getCurrentPet().getItemStack()))

        );
    }
    //endregion
}
