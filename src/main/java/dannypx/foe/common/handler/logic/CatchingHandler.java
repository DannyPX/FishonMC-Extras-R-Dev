package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.fetch.TitleHandler;
import dannypx.foe.common.handler.store.StatsDataHandler;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CatchingHandler {
    private static CatchingHandler INSTANCE = new CatchingHandler();

    public static CatchingHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new CatchingHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    private long startScanTime = 0L;
    private boolean scanDone = true;
    private String fishNameToFind = "";
    //endregion

    //region Methods
    public void tick() {
        this.scanFish();
    }

    private void scanFish() {
        if(!scanDone && System.currentTimeMillis() < startScanTime + (Configs.handlerConfig.catchingStatusCooldown.get() * 1000L)) {
            Pair<Boolean, FishNbtObject> foundFish = this.findFish();
            if(foundFish.v1()) {
                this.scanDone = true;
                InventoryHandler.instance().addToTrackedFish(foundFish.v2().getID());

                // Store to Stats
                StatsDataHandler.instance().setFish(foundFish.v2());
                LoggerHandler.info("Found Fish: " + foundFish.v2().getName().getString());

                this.checkForCaughtItems();
            }
        } else if (!scanDone && System.currentTimeMillis() > startScanTime + (Configs.handlerConfig.catchingStatusCooldown.get() * 1000L)){
            this.scanDone = true;
            LoggerHandler.info("Did not find fish");
        }
    }

    private void checkForCaughtItems() {
        LoggerHandler.info("Start finding items");
        if(minecraftClient.player != null) {
            DefaultedList<ItemStack> oldInventory = InventoryHandler.instance().getSnapshotInventory();
            DefaultedList<ItemStack> newInventory = minecraftClient.player.getInventory().main;

            for(int i = 0; i < newInventory.size(); i++) {
                ItemStack oldStack = oldInventory.get(i);
                ItemStack newStack = newInventory.get(i);

                // New item in slot
                if (oldStack.isEmpty() && !newStack.isEmpty()) {
                    scanItem(newStack, newStack.getCount());
                }

                // Same item, stack size changed
                if (!newStack.isEmpty()
                        && !oldStack.isEmpty()
                        && oldStack.getCount() != newStack.getCount()) {

                    int delta = newStack.getCount() - oldStack.getCount();
                    scanItem(newStack, delta);
                }
            }
        }
    }

    private void scanItem(ItemStack itemStack, int count) {
        Pair<Boolean, NbtObject> validatedItem = ValidateItem.isType(itemStack);

        if(validatedItem.v1()) {
            // Store to Stats
            StatsDataHandler.instance().setItem(validatedItem.v2(), count);

            LoggerHandler.info("Found Item: " + itemStack.getName().getString());
        }
    }

    private Pair<Boolean, FishNbtObject> findFish() {
        FishNbtObject inventoryFish = this.findFishInInventory();
        if(inventoryFish != null) return Pair.of(true, inventoryFish);

        FishNbtObject worldFish = this.findFishInWorld();
        if(worldFish != null) return Pair.of(true, worldFish);

        return Pair.ofFalse();
    }

    private FishNbtObject findFishInInventory() {
        AtomicReference<FishNbtObject> foundItemStack = new AtomicReference<>();

        if(minecraftClient.player != null) {
            minecraftClient.player.getInventory().main.forEach(itemStack -> {
                FishNbtObject validatedFish = validateFish(itemStack);
                if(validatedFish != null && foundItemStack.get() == null) foundItemStack.set(validatedFish);
            });
        }
        return foundItemStack.get();
    }

    private FishNbtObject findFishInWorld() {
        AtomicReference<FishNbtObject> foundItemStack = new AtomicReference<>();

        if(minecraftClient.player != null) {
            ClientWorld world = minecraftClient.player.clientWorld;
            Box searchBox = minecraftClient.player.getBoundingBox().expand(10d);

            List<ItemEntity> itemEntities = world.getEntitiesByClass(
                    ItemEntity.class,
                    searchBox,
                    itemEntity -> {
                        ItemStack itemStack = itemEntity.getStack();
                        FishNbtObject validatedFish = validateFish(itemStack);
                        return validatedFish != null;
                    }
            );

            if(!itemEntities.isEmpty() && foundItemStack.get() == null) {
                foundItemStack.set(ValidateItem.isFish(itemEntities.getFirst().getStack()).v2());
            }
        }
        return foundItemStack.get();
    }

    private FishNbtObject validateFish(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            Pair<Boolean, FishNbtObject> validatedFish = ValidateItem.isFish(itemStack);
            if(validatedFish.v1()
                    && validatedFish.v2().isOwn()
                    && !InventoryHandler.instance().getTrackedFish().contains(validatedFish.v2().getID())
                    && fishNameToFind.contains(itemStack.getName().getString())
            ) {
                return validatedFish.v2();
            }
        }
        return null;
    }

    public void scanFishListener() {
        Text title = TitleHandler.instance().getTitle();

        if(title.getString().length() != 1 || title.equals(Text.empty())) {
            return;
        }

        if(title.getString().charAt(0) > 0xE000 && title.getString().charAt(0) < 0xE999) {
            this.startScan();
            LoggerHandler.info("Start finding fish");
        }
    }

    public void scanFishNameListener() {
        Text subTitle = TitleHandler.instance().getSubTitle();

        if(subTitle.equals(Text.empty()) || subTitle.getString().isBlank()) {
            return;
        }

        if(subTitle.getString().charAt(0) > 0xF000 && subTitle.getString().charAt(0) < 0xF999) {
            fishNameToFind = subTitle.getString();
        }
    }

    private void startScan() {
        startScanTime = System.currentTimeMillis();
        scanDone = false;
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
