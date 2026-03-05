package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.fetch.TitleHandler;
import dannypx.foe.common.handler.store.QuestDataHandler;
import dannypx.foe.common.handler.store.StatsDataHandler;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CatchingHandler extends Handler {
    private static CatchingHandler INSTANCE = new CatchingHandler();

    public static CatchingHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new CatchingHandler();
        }
        return INSTANCE;
    }

    //region Fields
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
            if(foundFish.value1()) {
                InventoryHandler.instance().addToTrackedFish(foundFish.value2().getID());

                // Store to Stats
                StatsDataHandler.instance().setFish(foundFish.value2());
                QuestDataHandler.instance().setFish(foundFish.value2());
                LoggerHandler._debug("Found Fish: " + foundFish.value2().getName().getString());

                CodeExecuterHandler.runLater(Configs.handlerConfig.catchingItemsDelayCheck.get(), this::checkForCaughtItems);

                this.scanDone = true;
            }
        } else if (!scanDone && System.currentTimeMillis() > startScanTime + (Configs.handlerConfig.catchingStatusCooldown.get() * 1000L)){
            this.scanDone = true;
            LoggerHandler._debug("Did not find fish");
        }
    }

    private void checkForCaughtItems() {
        LoggerHandler._debug("Start finding items");
        LoggerHandler._debug("Start Time: " + System.currentTimeMillis());
        LoggerHandler._debug("Search Window: " + (Configs.handlerConfig.catchingItemsCheckWindow.get() + (System.currentTimeMillis() - startScanTime)));
        if(minecraftClient.player != null) {
            InventoryHandler.instance().getSnapshottedItems().stream()
                    .filter(item -> System.currentTimeMillis() - item.value1()
                            < Configs.handlerConfig.catchingItemsCheckWindow.get() + (System.currentTimeMillis() - startScanTime))
                    .toList().forEach(item -> scanItem(item.value2(), item.value3()));
        }
    }

    private void scanItem(ItemStack itemStack, int count) {
        Pair<Boolean, NbtObject> validatedItem = ValidateItem.isType(itemStack);

        if(validatedItem.value1()) {
            // Store to Stats
            StatsDataHandler.instance().setItem(validatedItem.value2(), count);

            LoggerHandler._debug("Found Item: " + itemStack.getName().getString(), itemStack);
        }
    }

    private Pair<Boolean, FishNbtObject> findFish() {
        FishNbtObject inventoryFish = this.findFishInInventory();
        if(inventoryFish != null) return Pair.of(inventoryFish);

        FishNbtObject worldFish = this.findFishInWorld();
        if(worldFish != null) return Pair.of(worldFish);

        return Pair.ofFalse(FishNbtObject.empty());
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
                foundItemStack.set(ValidateItem.isFish(itemEntities.getFirst().getStack()).value2());
            }
        }
        return foundItemStack.get();
    }

    private FishNbtObject validateFish(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            Pair<Boolean, FishNbtObject> validatedFish = ValidateItem.isFish(itemStack);
            if(validatedFish.value1()
                    && validatedFish.value2().isOwn()
                    && !InventoryHandler.instance().getTrackedFish().contains(validatedFish.value2().getID())
                    && fishNameToFind.contains(itemStack.getName().getString())
            ) {
                return validatedFish.value2();
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
            LoggerHandler._debug("Start finding fish");
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
