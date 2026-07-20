package dannypx.foe.handler.logic;

import dannypx.foe.config.Configs;
import dannypx.foe.handler.Handler;
import dannypx.foe.handler.fetch.NetworkHandler;
import dannypx.foe.handler.store.ProfileDataHandler;
import dannypx.foe.helper.ItemStackHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.item.*;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.tuple.Triplet;
import dannypx.foe.type.placeholder.PlaceholderValue;
import dannypx.foe.type.placeholder.StringValue;
import dannypx.foe.type.placeholder.ComponentValue;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class InventoryHandler extends Handler {
    private static InventoryHandler INSTANCE = new InventoryHandler();

    public static InventoryHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new InventoryHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final List<UUID> trackedFish = new ArrayList<>();
    private NonNullList<ItemStack> snapshotInventory = NonNullList.createWithCapacity(0);
    private List<Triplet<Long, ItemStack, Integer>> snapshottedItems = new ArrayList<>();
    private List<Triplet<Long, ItemStack, Integer>> snapshottedRemovedItems = new ArrayList<>();
    private FishingRodTagObject currentFishingRod = FishingRodTagObject.empty();
    private PetTagObject currentPet = PetTagObject.empty();

    private boolean currentlyLoading = false;

    private int currentEmptySlots = 27;

    public List<UUID> getTrackedFish() {
        return trackedFish;
    }

    public NonNullList<ItemStack> getSnapshotInventory() {
        return snapshotInventory.isEmpty() ? NonNullList.createWithCapacity(0) : snapshotInventory;
    }

    public List<Triplet<Long, ItemStack, Integer>> getSnapshottedItems() {
        return snapshottedItems;
    }

    public List<ItemStack> getSnapshottedItemstacks() {
        return this.snapshottedItems.stream().map(Triplet::value2).toList();
    }

    protected void setCurrentFishingRod(FishingRodTagObject currentFishingRod) {
        this.currentFishingRod = currentFishingRod;
    }

    public FishingRodTagObject getCurrentFishingRod() {
        return this.currentFishingRod;
    }

    protected  void setCurrentPet(PetTagObject currentPet) {
        this.currentPet = currentPet;
    }

    public PetTagObject getCurrentPet() {
        return this.currentPet;
    }

    public boolean hasPet() {
        return this.currentPet.getItemStack() != ItemStack.EMPTY;
    }

    public int getCurrentEmptySlots() {
        return currentEmptySlots;
    }

    public Pair<Boolean, PlaceholderValue> getInventory(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(fishing_rod|pet|armor|empty_slots|held_item|slot)$");

            if(fieldPattern.matcher(params[0]).matches()) {
                return switch(params[0]) {
                    case "fishing_rod" -> {
                        if(params.length >= 2) {
                            yield switch(params[1]) {
                                case "name" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(getCurrentFishingRod().getName()));
                                case "line" -> {
                                    List<TagObject> list = getCurrentFishingRod().getLineItem();
                                    if(!list.isEmpty()) {
                                        yield switch(params[2]) {
                                            case "name" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(list.getFirst().getName()));
                                            case "lore" -> params.length > 3 ? PlaceholderHandler.getLoreValue(list.getFirst(), params[3]) : PlaceholderHandler.noResult();
                                            default -> PlaceholderHandler.getNbtValue(list.getFirst(), Arrays.copyOfRange(params, 2, params.length));
                                        };
                                    }
                                    yield PlaceholderHandler.noResult();
                                }
                                case "reel" -> {
                                    List<TagObject> list = getCurrentFishingRod().getReelItem();
                                    if(!list.isEmpty()) {
                                        yield switch(params[2]) {
                                            case "name" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(list.getFirst().getName()));
                                            case "lore" -> params.length > 3 ? PlaceholderHandler.getLoreValue(list.getFirst(), params[3]) : PlaceholderHandler.noResult();
                                            default -> PlaceholderHandler.getNbtValue(list.getFirst(), Arrays.copyOfRange(params, 2, params.length));
                                        };
                                    }
                                    yield PlaceholderHandler.noResult();
                                }
                                case "pole" -> {
                                    List<TagObject> list = getCurrentFishingRod().getPoleItem();
                                    if(!list.isEmpty()) {
                                        yield switch(params[2]) {
                                            case "name" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(list.getFirst().getName()));
                                            case "lore" -> params.length > 3 ? PlaceholderHandler.getLoreValue(list.getFirst(), params[3]) : PlaceholderHandler.noResult();
                                            default -> PlaceholderHandler.getNbtValue(list.getFirst(), Arrays.copyOfRange(params, 2, params.length));
                                        };
                                    }
                                    yield PlaceholderHandler.noResult();
                                }
                                case "lore" -> params.length > 2 ? PlaceholderHandler.getLoreValue(getCurrentFishingRod(), params[2]) : PlaceholderHandler.noResult();
                                default -> PlaceholderHandler.getNbtValue(getCurrentFishingRod(), Arrays.copyOfRange(params, 1, params.length));
                            };
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "pet" -> {
                        if(params.length >= 2
                                && hasPet()
                        ) {
                            yield switch(params[1]) {
                                case "name" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(getCurrentPet().getName()));
                                case "level" -> PlaceholderHandler.getPlaceholderValue(StringValue.valueOf(getCurrentPet().getLevel()));
                                case "level_progress" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getProgress() * 100, 2)));
                                case "rating" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(getCurrentPet().getRatingComponent()));
                                case "rating_percent" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getTotalPercent() * 100, 2)));
                                case "rarity" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(getCurrentPet().getRarityComponent()));
                                case "location_luck_percent" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getLocationPercentMaxLuck() * 100, 2)));
                                case "location_scale_percent" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getLocationPercentMaxScale() * 100, 2)));
                                case "climate_luck_percent" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getClimatePercentMaxLuck() * 100, 2)));
                                case "climate_scale_percent" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getClimatePercentMaxScale() * 100, 2)));
                                case "location_luck" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getLocationMaxLuck(), 0)));
                                case "location_scale" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getLocationMaxScale(), 0)));
                                case "climate_luck" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getClimateMaxLuck(), 0)));
                                case "climate_scale" -> PlaceholderHandler.getPlaceholderValue(StringValue.of(TextHelper.floatToString(getCurrentPet().getClimateMaxScale(), 0)));
                                case "lore" -> params.length > 2 ? PlaceholderHandler.getLoreValue(getCurrentPet(), params[2]) : PlaceholderHandler.noResult();
                                default -> PlaceholderHandler.getNbtValue(getCurrentPet(), Arrays.copyOfRange(params, 1, params.length));
                            };
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "armor" -> {
                        if(params.length >= 3
                                && minecraft.player != null
                        ) {
                            ItemStack stack = ItemStack.EMPTY;

                            switch(params[1]) {
                                case "chestplate" -> stack = minecraft.player.getItemBySlot(EquipmentSlot.CHEST);
                                case "leggings" -> stack = minecraft.player.getItemBySlot(EquipmentSlot.LEGS);
                                case "boots" -> stack = minecraft.player.getItemBySlot(EquipmentSlot.FEET);
                            }

                            Pair<Boolean, TagObject> validatedItem = ValidateItem.isServerItem(stack, true);

                            if(validatedItem.value1()) {
                                yield switch(params[2]) {
                                    case "name" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(validatedItem.value2().getName()));
                                    case "lore" -> params.length > 3 ? PlaceholderHandler.getLoreValue(validatedItem.value2(), params[3]) : PlaceholderHandler.noResult();
                                    default -> PlaceholderHandler.getNbtValue(validatedItem.value2(), Arrays.copyOfRange(params, 2, params.length));
                                };
                            }
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "empty_slots" -> PlaceholderHandler.getPlaceholderValue(StringValue.valueOf(getCurrentEmptySlots()));
                    case "held_item" -> {
                        if(params.length >= 2
                                && minecraft.player != null
                        ) {
                            ItemStack heldItem = minecraft.player.getInventory().getSelectedItem();
                            if(!heldItem.isEmpty()) {
                                yield switch (params[1]) {
                                    case "name" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(heldItem.getHoverName()));
                                    case "lore" -> params.length > 2 ? PlaceholderHandler.getLoreValue(heldItem, params[2]) : PlaceholderHandler.noResult();
                                    default -> PlaceholderHandler.getNbtValue(heldItem, Arrays.copyOfRange(params, 1, params.length));
                                };
                            }
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    case "slot" -> {
                        if(params.length >= 3
                                && minecraft.player != null
                        ) {
                            try {
                                int slot = Integer.parseInt(params[1]);
                                ItemStack stack = minecraft.player.getInventory().getItem(slot);

                                if(!stack.isEmpty()) {
                                    yield switch (params[2]) {
                                        case "name" -> PlaceholderHandler.getPlaceholderValue(ComponentValue.of(stack.getHoverName()));
                                        case "lore" -> params.length > 3 ? PlaceholderHandler.getLoreValue(stack, params[3]) : PlaceholderHandler.noResult();
                                        default -> PlaceholderHandler.getNbtValue(stack, Arrays.copyOfRange(params, 2, params.length));
                                    };
                                }
                            } catch (Exception ignored) {}
                        }
                        yield PlaceholderHandler.noResult();
                    }
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void tick() {
        if(minecraft.player != null) {
            this.tickInventory();
            this.snapshotFishingRod();
            this.snapshotPet();
            this.snapshotEmptySlots();
            this.checkSnapshottedItems();
        }
    }

    private void tickInventory() {
        if(!snapshotInventory.isEmpty()) {
            NonNullList<ItemStack> oldInventory = snapshotInventory;
            NonNullList<ItemStack> newInventory = minecraft.player.getInventory().getNonEquipmentItems();

            for(int i = 0; i < newInventory.size(); i++) {
                ItemStack oldStack = oldInventory.get(i);
                ItemStack newStack = newInventory.get(i);

                // New item in slot
                if ((oldStack.isEmpty() && !newStack.isEmpty())
                        || (newStack.isEmpty() && !oldStack.isEmpty())
                ) {
                    this.snapshotInventory();
                    int finalI = i;
                    if(!newStack.isEmpty() &&
                            snapshottedRemovedItems.stream()
                                    .noneMatch(
                                            removedItem -> removedItem.value3() == finalI
                                            && ItemStack.isSameItemSameComponents(removedItem.value2(), newStack)
                                    )
                    ) this.addToSnapshotItems(newStack, newStack.getCount());
                    if(newStack.isEmpty()) this.addToRemovedSnapshotItems(oldStack, i);
                }

                // Same item, stack size changed
                if (!newStack.isEmpty()
                        && !oldStack.isEmpty()
                        && oldStack.getCount() != newStack.getCount()) {
                    this.snapshotInventory();
                    this.addToSnapshotItems(newStack, newStack.getCount() - oldStack.getCount());
                }
            }
        } else {
            if(!currentlyLoading) {
                currentlyLoading = true;
                CodeExecuterHandler.runLater(100, this::snapshotInventory);
            }
        }
    }

    public void onLeave() {
        this.reset();
    }

    public void reset() {
        this.currentlyLoading = false;
        this.snapshotInventory.clear();
        this.snapshottedItems.clear();
    }

    private void checkSnapshottedItems() {
        snapshottedItems.removeIf(item -> item.value1() > System.currentTimeMillis() + 1000L);
        snapshottedRemovedItems.removeIf(item -> item.value1() > System.currentTimeMillis() + 60L + NetworkHandler.instance().getPing());
    }

    private void addToSnapshotItems(ItemStack newStack, int count) {
        LoggerHandler._debug("Snapshotted Item: " + newStack.getHoverName().getString() + " at " + System.currentTimeMillis());
        snapshottedItems.add(Triplet.of(System.currentTimeMillis(), newStack, count));
    }

    private void addToRemovedSnapshotItems(ItemStack oldStack, int slot) {
        snapshottedRemovedItems.add(Triplet.of(System.currentTimeMillis(), oldStack, slot));
    }

    private void snapshotEmptySlots() {
        int empty = 0;

        for (ItemStack stack : minecraft.player.getInventory().getNonEquipmentItems()) {
            if (stack.isEmpty()) {
                empty++;
            }
        }

        if(currentEmptySlots != empty) {
            currentEmptySlots = empty;
            NotifierHandler.instance().notifyEmptySlots(currentEmptySlots);

            //Event Trigger
            if(currentEmptySlots <= Configs.hudConfig.showNotificationAtEmptySlots.get()) {
                if(currentEmptySlots == 0) {
                    EventHandler.instance().onFullInventory();
                } else {
                    EventHandler.instance().onNearFullInventory();
                }
            }
        }
    }

    private void snapshotPet() {
        if(ProfileDataHandler.instance().getProfileData().activePetSlot != -1) {
            ItemStack pet = minecraft.player.getInventory().getNonEquipmentItems().get(ProfileDataHandler.instance().getProfileData().activePetSlot);
            if(!pet.isEmpty() && !ItemStack.isSameItemSameComponents(currentPet.getItemStack(), pet)) {
                Pair<Boolean, @Nullable PetTagObject> validatedPet = ValidateItem.isPet(pet);
                if(validatedPet.value1()) {
                    this.setCurrentPet(validatedPet.value2());
                }
            }
        } else if(ProfileDataHandler.instance().getProfileData().activePetSlot == -1
                && currentPet.getItemStack() != ItemStack.EMPTY
        ) {
            currentPet = PetTagObject.empty();
        }
    }

    private void snapshotFishingRod() {
        ItemStack fishingRod = minecraft.player.getInventory().getNonEquipmentItems().getFirst();
        if(!fishingRod.isEmpty() && !ItemStack.isSameItemSameComponents(currentFishingRod.getItemStack(), fishingRod)) {
            Pair<Boolean, @Nullable FishingRodTagObject> validatedFishingRod = ValidateItem.isFishingRod(fishingRod);
            if(validatedFishingRod.value1()) {
                this.setCurrentFishingRod(validatedFishingRod.value2());
            }
        }
    }

    public void trackFishOffSide() {
        if(minecraft.player != null
                && CatchingHandler.instance().isScanDone()
        ) {
            this.trackAllFish();
        }
    }

    public void snapshotInventory() {
        if(minecraft.player != null) {
            snapshotInventory = ItemStackHelper.deepCopy(
                    minecraft.player.getInventory().getNonEquipmentItems(),
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
        if(minecraft.player != null) {
            trackedFish.clear();
            minecraft.player.getInventory().getNonEquipmentItems().forEach(itemStack -> {
                Pair<Boolean, FishTagObject> validatedItem = ValidateItem.isFish(itemStack);
                if(validatedItem.value1() && validatedItem.value2().isOwn()) {
                    this.addToTrackedFish(validatedItem.value2().getID());
                }
            });
            LoggerHandler._debug("Tracked Fish: " + trackedFish.size());
            return true;
        }
        return false;
    }

    public TagObject getCurrentHeldItem() {
        if(minecraft.player != null) {
            ItemStack heldItem = minecraft.player.getMainHandItem();
            Pair<Boolean, TagObject> validatedItem = ValidateItem.isServerItem(heldItem, true);
            if(validatedItem.value1()) {
                return validatedItem.value2();
            }
        }
        return TagObject.empty();
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "trackedFish", Pair.of(Component.literal("[trackedFish]"), TextHelper.literal(getTrackedFish())),
                "snapshotInventory", Pair.of(Component.literal("[snapshotInventory]"), TextHelper.literal(
                        ItemStackHelper.itemStackListToJson(getSnapshotInventory())
                )),
                "currentFishingRod", Pair.of(Component.literal("[currentFishingRod]"), TextHelper.literal(getCurrentFishingRod().getItemStack())),
                "currentPet", Pair.of(Component.literal("[currentPet]"), TextHelper.literal(getCurrentPet().getItemStack())),
                "currentHeldItem", Pair.of(Component.literal("[currentHeldItem]"), TextHelper.literal(getCurrentHeldItem())),
                "snapshottedItems", Pair.of(Component.literal("[snapshottedItems]"), TextHelper.literal(getSnapshottedItemstacks()))
        );
    }
    //endregion
}
