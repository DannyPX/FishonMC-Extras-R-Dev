package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.store.Stat;
import dannypx.foe.common.helper.TextHelper;
import dannypx.foe.common.item.FishNbtObject;
import dannypx.foe.common.item.NbtObject;
import dannypx.foe.common.item.PetNbtObject;
import dannypx.foe.common.type.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class NotifierHandler {
    private static NotifierHandler INSTANCE = new NotifierHandler();

    public static NotifierHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new NotifierHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private final List<Notification> notifications = new ArrayList<>();
    private final List<UUID> removeQueue = new ArrayList<>();

    public List<Notification> getNotifications() {
        return notifications;
    }
    //endregion

    //region Methods
    public void tick() {
        boolean isRemoved = notifications.removeIf(notification -> removeQueue.contains(notification.uuid));
        if(isRemoved) removeQueue.clear();

        notifications.forEach(notification -> {
            if(System.currentTimeMillis() > notification.startTime + notification.notificationTime * 1000L) {
                this.removeNotification(notification.uuid);
            }
        });
    }

    public UUID addNotification(Notification notification) {
        notifications.add(notification);
        return notification.uuid;
    }

    public void removeNotification(UUID uuid) {
        removeQueue.add(uuid);
    }

    public void notifyFish(
            FishNbtObject fish,
            Pair<String, Integer> rarityDrystreak,
            Pair<String, Integer> variantDrystreak,
            Pair<String, Integer> sizeDryStreak
    ) {
        Text tagText = !Objects.equals(fish.getVariant(), "normal")
                ? TextHelper.concat(fish.getVariantText(), fish.getRarityText())
                : TextHelper.concat(fish.getRarityText());
        Text rarityText = fish.getRarityText();
        Text variantText = fish.getVariantText();
        Text sizeText = fish.getFishSizeText();

        Text lengthText = TextHelper.concat(
                Text.literal(TextHelper.floatToString(fish.getLength(), 2)).formatted(Formatting.GRAY),
                Text.literal("in ").formatted(Formatting.GRAY)
        );

        Text weightText = TextHelper.concat(
                Text.literal(TextHelper.floatToString(fish.getWeight(), 2)).formatted(Formatting.GRAY),
                Text.literal("lb ").formatted(Formatting.GRAY)
        );

        List<Text> notifTextList = Arrays.asList(
                tagText,
                fish.getName(),
                TextHelper.concat(fish.getFishSizeText(), Text.literal(" "), lengthText, Text.literal(" "), weightText),
                Text.empty(),
                Text.literal(" - Drystreaks before catch").formatted(Formatting.GRAY),
                TextHelper.concat(rarityText, TextHelper.literal(rarityDrystreak.v2())),
                TextHelper.concat(sizeText, TextHelper.literal(sizeDryStreak.v2()))
        );

        if(!Objects.equals(fish.getVariant(), "normal")) {
            notifTextList.add(TextHelper.concat(variantText, TextHelper.literal(variantDrystreak.v2())));
            notifTextList.add(Text.empty());
            notifTextList.add(Text.empty());
        }

        this.addNotification(
                new NotifierHandler.Notification(fish.getItemStack(),
                        Objects.equals(fish.getVariant(), "normal") ? 7 : 8, 1,
                        Configs.hudConfig.fishDismissalTime.get(),
                        notifTextList
                )
        );
    }

    public void notifyPet(PetNbtObject pet, Pair<String, Integer> rarityDrystreak, Pair<String, Integer> ratingDrystreak) {
        Text petText = TextHelper.concat(pet.getRarityText(), pet.getName());

        List<Text> notifTextList = Arrays.asList(
                petText,
                pet.getRatingText(),
                Text.empty(),
                Text.literal(" - Drystreaks before catch").formatted(Formatting.GRAY),
                TextHelper.concat(pet.getRarityText(), TextHelper.literal(rarityDrystreak.v2())),
                TextHelper.concat(pet.getRatingText(), TextHelper.literal(ratingDrystreak.v2()))
        );

        this.addNotification(
                new Notification(pet.getItemStack(),
                        6, 1,
                        Configs.hudConfig.petDismissalTime.get(),
                        notifTextList
                )
        );
    }

    public void notifyItem(NbtObject item, int count, Pair<String, Integer> itemDrystreak) {
        Text itemText = TextHelper.concat(item.getName(), Text.literal(" "), TextHelper.literal(count), Text.literal("x").formatted(Formatting.GRAY));
        Text typeText = Text.literal(TextHelper.convertField(itemDrystreak.v1()));


        List<Text> notifTextList = Arrays.asList(
                itemText,
                Text.empty(),
                Text.literal(" - Drystreak before catch").formatted(Formatting.GRAY),
                TextHelper.concat(typeText, Text.literal(" ") ,TextHelper.literal(itemDrystreak.v2()))
        );

        this.addNotification(
                new Notification(item.getItemStack(),
                        4, 1,
                        Configs.hudConfig.petDismissalTime.get(),
                        notifTextList
                )
        );
    }
    //endregion

    //region NotifyObject
    public static class Notification {
        public final ItemStack item;
        public final int rows;
        public final int columns;
        public final List<Text> textList;
        protected final long startTime;
        protected final int notificationTime;
        protected final UUID uuid;

        public Notification(
                ItemStack item,
                int rows,
                int columns,
                int notificationTime,
                List<Text> texts
        ) {
            this.item = item;
            this.rows = rows;
            this.columns = columns;
            this.startTime = System.currentTimeMillis();
            this.notificationTime = notificationTime;
            this.textList = texts;
            this.uuid = UUID.randomUUID();
        }

        public Notification(
                int rows,
                int columns,
                int notificationTime,
                List<Text> texts
        ) {
            this.item = ItemStack.EMPTY;
            this.rows = rows;
            this.columns = columns;
            this.startTime = System.currentTimeMillis();
            this.notificationTime = notificationTime;
            this.textList = texts;
            this.uuid = UUID.randomUUID();
        }

        public Notification(
                ItemStack item,
                int rows,
                int columns,
                List<Text> texts
        ) {
            this.item = item;
            this.rows = rows;
            this.columns = columns;
            this.startTime = System.currentTimeMillis();
            this.notificationTime = Integer.MAX_VALUE;
            this.textList = texts;
            this.uuid = UUID.randomUUID();
        }

        public Notification(
                int rows,
                int columns,
                List<Text> texts
        ) {
            this.item = ItemStack.EMPTY;
            this.rows = rows;
            this.columns = columns;
            this.startTime = System.currentTimeMillis();
            this.notificationTime = Integer.MAX_VALUE;
            this.textList = texts;
            this.uuid = UUID.randomUUID();
        }
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
