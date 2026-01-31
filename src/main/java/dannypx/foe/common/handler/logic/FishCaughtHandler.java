package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.fetch.TitleHandler;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.item.ValidateItem;
import dannypx.foe.common.type.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FishCaughtHandler {
    private static FishCaughtHandler INSTANCE = new FishCaughtHandler();

    public static FishCaughtHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new FishCaughtHandler();
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
        if(!scanDone && System.currentTimeMillis() < startScanTime + (Configs.handlerConfig.fishCaughtStatusCooldown.get() * 1000L)) {
            Pair<Boolean, FishNbtObject> foundFish = this.findFish();
            if(foundFish.v1()) {
                this.scanDone = true;
                InventoryHandler.instance().addToTrackedFish(foundFish.v2().getUUID());
                LoggerHandler.info("Found: " + foundFish.v2().getName().getString() + " (" + foundFish.v2().getWeight() + ")");
            }
        } else if (!scanDone && System.currentTimeMillis() > startScanTime + (Configs.handlerConfig.fishCaughtStatusCooldown.get() * 1000L)){
            this.scanDone = true;
            LoggerHandler.info("Did not find fish");
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
                    && !InventoryHandler.instance().getTrackedFish().contains(validatedFish.v2().getUUID())
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

        if(subTitle.equals(Text.empty())) {
            return;
        }

        if(subTitle.getString().charAt(0) > 0xF000 && subTitle.getString().charAt(0) < 0xF999) {
            fishNameToFind = subTitle.getString();
            LoggerHandler.info("Fish to find: " + fishNameToFind);
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
