package dannypx.foe.placeholder.registry;

import com.google.gson.*;
import dannypx.foe.config.Configs;
import dannypx.foe.handler.fetch.*;
import dannypx.foe.handler.logic.*;
import dannypx.foe.handler.store.*;
import dannypx.foe.helper.KeyBindHelper;
import dannypx.foe.helper.TextHelper;
import dannypx.foe.item.FishTagObject;
import dannypx.foe.item.PetTagObject;
import dannypx.foe.item.TagObject;
import dannypx.foe.item.ValidateItem;
import dannypx.foe.placeholder.functions.PlaceholderValue;
import dannypx.foe.type.custom_value.*;
import dannypx.foe.type.tuple.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static dannypx.foe.placeholder.registry.PlaceholderTreeNode.*;

public class PlaceholderRegistry {
    private static final Map<String, PlaceholderTreeNode> ROOTS = new HashMap<>();

    public static void init() {
        //region Placeholders
        register(
                node("boss_bar")
                        .branch(node("location").valueComponent(BossEventContext::getLocation))
                        .branch(node("weather").valueComponent(BossEventContext::getWeather))
                        .branch(node("time").valueComponent(BossEventContext::getTime))
                        .branch(node("temperature").valueComponent(BossEventContext::getTemperature))
                        .branch(node("sub_location").valueComponent(BossEventContext::getSubLocation).allowEmpty())
                        .branch(node("community_goal")
                                .branch(node("current").valueComponent(BossEventContext::getCommunityGoalCurrent))
                                .branch(node("max").valueComponent(BossEventContext::getCommunityGoalMax))
                        )
        );

        register(
                node("player")
                        .branch(node("name").valueString(PlayerContext::getName))
                        .branch(node("level").valueNumber(PlayerContext::getLevel))
                        .branch(node("level_progress").valueNumber(PlayerContext::getLevelProgress))
                        .branch(node("pos")
                                .branch(node("x").valueNumber(PlayerContext::getPosX))
                                .branch(node("y").valueNumber(PlayerContext::getPosY))
                                .branch(node("z").valueNumber(PlayerContext::getPosZ))
                        )
                        .branch(node("fps").valueNumber(PlayerContext::getFps))
        );

        register(
                node("scoreboard")
                        .branch(node("level").valueComponent(ScoreboardContext::getLevel))
                        .branch(node("wallet").valueNumber(ScoreboardContext::getWallet))
                        .branch(node("credits").valueNumber(ScoreboardContext::getCredits))
                        .branch(node("catches").valueNumber(ScoreboardContext::getCatches))
                        .branch(node("location_min").valueNumber(ScoreboardContext::getLocationMin))
                        .branch(node("location_max").valueNumber(ScoreboardContext::getLocationMax))
                        .branch(node("catch_rate").valueString(ScoreboardContext::getCatchRate))
                        .branch(node("crew").valueString(ScoreboardContext::getCrew))
                        .branch(node("crew_nearby").valueComponent(ScoreboardContext::getCrewNearby))
                        .branch(node("version").valueString(ScoreboardContext::getVersion))
                        .branch(node("date").valueString(ScoreboardContext::getDate))
        );

        register(
                node("tab")
                        .branch(node("player_name").valueComponent(TabOverlayContext::getPlayerName))
                        .branch(node("instance").valueString(TabOverlayContext::getInstance))
                        .branch(node("is_in_instance").valueBoolean(TabOverlayContext::getIsInInstance))
        );

        register(
                node("title")
                        .branch(node("title").valueComponent(TitleContext::getTitle))
                        .branch(node("subtitle").valueComponent(TitleContext::getSubTitle))
        );

        register(
                node("connection")
                        .branch(node("is_on_server").valueBoolean(ConnectionContext::getIsOnServer))
                        .branch(node("was_on_server").valueBoolean(ConnectionContext::getWasOnServer))
        );

        register(
                node("inventory")
                        .branch(node("empty_slots").valueNumber(InventoryContext::getEmptySlots))
                        .branch(node("fishing_rod")
                                .branch(node("line")
                                        .branch(node("name").valueComponent(InventoryContext::getFishingRodLineName))
                                        .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getFishingRodLineLore)))
                                        .branch(nodeStringArray().value(InventoryContext::getFishingRodLineNbt))
                                )
                                .branch(node("reel")
                                        .branch(node("name").valueComponent(InventoryContext::getFishingRodReelName))
                                        .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getFishingRodReelLore)))
                                        .branch(nodeStringArray().value(InventoryContext::getFishingRodReelNbt))
                                )
                                .branch(node("pole")
                                        .branch(node("name").valueComponent(InventoryContext::getFishingRodPoleName))
                                        .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getFishingRodPoleLore)))
                                        .branch(nodeStringArray().value(InventoryContext::getFishingRodPoleNbt))
                                )
                                .branch(node("name").valueComponent(InventoryContext::getFishingRodName))
                                .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getFishingRodLore)))
                                .branch(nodeStringArray().value(InventoryContext::getFishingRodNbt))
                        )
                        .branch(node("pet")
                                .branch(node("name").valueComponent(InventoryContext::getPetName))
                                .branch(node("level").valueNumber(InventoryContext::getPetLevel))
                                .branch(node("level_progress").valueNumber(InventoryContext::getPetLevelProgress))
                                .branch(node("rating").valueComponent(InventoryContext::getPetRating))
                                .branch(node("rating_percent").valueNumber(InventoryContext::getPetRatingPercent))
                                .branch(node("rarity").valueComponent(InventoryContext::getPetRarity))
                                .branch(node("location_luck_percent").valueNumber(InventoryContext::getPetLocationLuckPercent))
                                .branch(node("location_scale_percent").valueNumber(InventoryContext::getPetLocationScalePercent))
                                .branch(node("climate_luck_percent").valueNumber(InventoryContext::getPetClimateLuckPercent))
                                .branch(node("climate_scale_percent").valueNumber(InventoryContext::getPetClimateScalePercent))
                                .branch(node("location_luck").valueNumber(InventoryContext::getPetLocationLuck))
                                .branch(node("location_scale").valueNumber(InventoryContext::getPetLocationScale))
                                .branch(node("climate_luck").valueNumber(InventoryContext::getPetClimateLuck))
                                .branch(node("climate_scale").valueNumber(InventoryContext::getPetClimateScale))
                                .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getPetLore)))
                                .branch(nodeStringArray().value(InventoryContext::getPetNbt))
                        )
                        .branch(node("armor")
                                .branch(node("chestplate")
                                        .branch(node("name").valueComponent(InventoryContext::getChestplateName))
                                        .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getChestplateLore)))
                                        .branch(nodeStringArray().value(InventoryContext::getBootsNbt))
                                )
                                .branch(node("leggings")
                                        .branch(node("name").valueComponent(InventoryContext::getLeggingsName))
                                        .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getLeggingsLore)))
                                        .branch(nodeStringArray().value(InventoryContext::getLeggingsNbt))
                                )
                                .branch(node("boots")
                                        .branch(node("name").valueComponent(InventoryContext::getBootsName))
                                        .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getBootsLore)))
                                        .branch(nodeStringArray().value(InventoryContext::getBootsNbt))
                                )
                        )
                        .branch(node("held_item")
                                .branch(node("name").valueComponent(InventoryContext::getHeldItemName))
                                .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getHeldItemLore)))
                                .branch(nodeStringArray().value(InventoryContext::getHeldItemNbt))
                        )
                        .branch(node("slot")
                                .branch(nodeIndex()
                                        .branch(node("name").valueComponent(InventoryContext::getSlotName))
                                        .branch(node("lore").branch(nodeIndex().valueComponent(InventoryContext::getSlotLore)))
                                        .branch(nodeStringArray().value(InventoryContext::getSlotNbt))
                                )
                        )
        );

        register(node("key_bind")
                .branch(node("open_main").valueString(KeyBindContext::getOpenMainKeyBind))
                .branch(node("inspect").valueString(KeyBindContext::getInspectKeyBind))
        );

        register(node("loading")
                .branch(node("is_loading_done").valueBoolean(LoadingContext::getIsLoadingDone))
                .branch(node("is_error").valueBoolean(LoadingContext::getIsError))
        );

        register(node("hit_result")
                .branch(node("block")
                        .branch(node("name").valueComponent(HitResultContext::getBlockName))
                )
                .branch(node("entity")
                        .branch(node("name").valueComponent(HitResultContext::getEntityName))
                )
                .branch(node("item_frame")
                        .branch(node("name").valueComponent(HitResultContext::getItemFromItemFrameName))
                )
        );

        register(node("network")
                .branch(node("ping").valueNumber(NetworkContext::getPing))
        );

        register(node("crew")
                .branch(node("online")
                        .branch(nodeIndex()
                                .branch(node("name").valueString(CrewContext::getOnlineName))
                                .branch(node("id").valueString(CrewContext::getOnlineId))
                        )
                )
                .branch(node("offline")
                        .branch(nodeIndex()
                                .branch(node("name").valueString(CrewContext::getOfflineName))
                                .branch(node("id").valueString(CrewContext::getOfflineId))
                        )
                )
                .branch(node("is_crew_nearby").valueBoolean(CrewContext::getIsCrewNearby)) //TODO Use icons
        );

        register(node("chat")
                .branch(node("trigger").branch(nodeString().valueComponent(ChatContext::getStoredChatTrigger)))
        );

        register(node("timer")
                .branch(nodeString()
                        .branch(node("timer").valueNumber(TimerContext::getTimer))
                        .branch(node("offset").valueNumber(TimerContext::getOffset))
                        .branch(node("notification_to_trigger").valueString(TimerContext::getNotificationToTrigger))
                        .branch(node("clean_up_chat_trigger").valueString(TimerContext::getCleanUpChatTrigger))
                        .branch(node("use_timer").valueBoolean(TimerContext::getIsUseTimer))
                        .branch(node("is_period").valueBoolean(TimerContext::getIsPeriod))
                        .branch(node("off_timer").valueNumber(TimerContext::getOffTimer))
                        .branch(node("notification_to_trigger_end").valueString(TimerContext::getNotificationToTriggerEnd))
                        .branch(node("time")
                                .branch(node("second").valueNumber(TimerContext::getTimeSecond))
                                .branch(node("minute").valueNumber(TimerContext::getTimeMinute))
                                .branch(node("hour").valueNumber(TimerContext::getTimeHour))
                                .branch(node("on")
                                        .branch(node("second").valueNumber(TimerContext::getOnTimeSecond))
                                        .branch(node("minute").valueNumber(TimerContext::getOnTimeMinute))
                                        .branch(node("hour").valueNumber(TimerContext::getOnTimeHour))
                                )
                                .branch(node("off")
                                        .branch(node("second").valueNumber(TimerContext::getOffTimeSecond))
                                        .branch(node("minute").valueNumber(TimerContext::getOffTimeMinute))
                                        .branch(node("hour").valueNumber(TimerContext::getOffTimeHour))
                                )
                        )
                        .branch(node("is_on").valueBoolean(TimerContext::getIsOn))
                        .branch(node("is_off").valueBoolean(TimerContext::getIsOff))
                )
        );

        register(node("catch")
                .branch(node("last_caught")
                        .branch(node("fish")
                                .branch(node("name").valueComponent(CatchContext::getLastCaughtFishName))
                                .branch(node("rarity")
                                        .branch(node("id").valueString(CatchContext::getLastCaughtFishRarityName))
                                        .branch(node("icon").valueComponent(CatchContext::getLastCaughtFishRarityIcon))
                                        .branch(node("last_drystreak").valueNumber(CatchContext::getLastCaughtFishRarityDryStreak))
                                )
                                .branch(node("variant")
                                        .branch(node("id").valueString(CatchContext::getLastCaughtFishVariantName))
                                        .branch(node("icon").valueComponent(CatchContext::getLastCaughtFishVariantIcon))
                                        .branch(node("last_drystreak").valueNumber(CatchContext::getLastCaughtFishVariantDryStreak))
                                )
                                .branch(node("size")
                                        .branch(node("id").valueString(CatchContext::getLastCaughtFishSizeName))
                                        .branch(node("icon").valueComponent(CatchContext::getLastCaughtFishSizeIcon))
                                        .branch(node("last_drystreak").valueNumber(CatchContext::getLastCaughtFishSizeDryStreak))
                                )
                                .branch(node("lore").branch(nodeIndex().valueComponent(CatchContext::getLastCaughtFishLore)))
                                .branch(nodeStringArray().value(CatchContext::getLastCaughtFishNbt))
                        )
                        .branch(node("pet")
                                .branch(node("name").valueComponent(CatchContext::getLastCaughtPetName))
                                .branch(node("rarity")
                                        .branch(node("id").valueString(CatchContext::getLastCaughtPetRarityName))
                                        .branch(node("icon").valueComponent(CatchContext::getLastCaughtPetRarityIcon))
                                        .branch(node("last_drystreak").valueNumber(CatchContext::getLastCaughtPetRarityDryStreak))
                                ).branch(node("rating")
                                        .branch(node("id").valueString(CatchContext::getLastCaughtPetRatingName))
                                        .branch(node("icon").valueComponent(CatchContext::getLastCaughtPetRatingIcon))
                                        .branch(node("last_drystreak").valueNumber(CatchContext::getLastCaughtPetRatingDryStreak))
                                )
                                .branch(node("lore").branch(nodeIndex().valueComponent(CatchContext::getLastCaughtPetLore)))
                                .branch(nodeStringArray().value(CatchContext::getLastCaughtPetNbt))
                        )
                        .branch(node("item")
                                .branch(nodeIndex()
                                        .branch(node("name").valueComponent(CatchContext::getLastCaughtItemName))
                                        .branch(node("amount").valueNumber(CatchContext::getLastCaughtItemStackAmount))
                                        .branch(node("id").valueString(CatchContext::getLastCaughtItemId))
                                        .branch(node("last_drystreak").valueNumber(CatchContext::getLastCaughtItemDryStreak))
                                        .branch(node("lore").branch(nodeIndex().valueComponent(CatchContext::getLastCaughtItemLore)))
                                        .branch(nodeStringArray().value(CatchContext::getLastCaughtItemNbt))
                                )
                        )
                )
        );

        register(node("quest")
                .branch(node("last_rewarded")
                        .branch(node("pet")
                                .branch(node("name").valueComponent(QuestContext::getLastRewardedPetName))
                                .branch(node("level").valueNumber(QuestContext::getLastRewardedPetLevel))
                                .branch(node("level_progress").valueNumber(QuestContext::getLastRewardedPetLevelProgress))
                                .branch(node("rating").valueComponent(QuestContext::getLastRewardedPetRating))
                                .branch(node("rating_percent").valueNumber(QuestContext::getLastRewardedPetRatingPercent))
                                .branch(node("rarity").valueComponent(QuestContext::getLastRewardedPetRarity))
                                .branch(node("location_luck_percent").valueNumber(QuestContext::getLastRewardedPetLocationLuckPercent))
                                .branch(node("location_scale_percent").valueNumber(QuestContext::getLastRewardedPetLocationScalePercent))
                                .branch(node("climate_luck_percent").valueNumber(QuestContext::getLastRewardedPetClimateLuckPercent))
                                .branch(node("climate_scale_percent").valueNumber(QuestContext::getLastRewardedPetClimateScalePercent))
                                .branch(node("location_luck").valueNumber(QuestContext::getLastRewardedPetLocationLuck))
                                .branch(node("location_scale").valueNumber(QuestContext::getLastRewardedPetLocationScale))
                                .branch(node("climate_luck").valueNumber(QuestContext::getLastRewardedPetClimateLuck))
                                .branch(node("climate_scale").valueNumber(QuestContext::getLastRewardedPetClimateScale))
                                .branch(node("lore").branch(nodeIndex().valueComponent(QuestContext::getLastRewardedPetLore)))
                                .branch(nodeStringArray().value(QuestContext::getLastRewardedPetNbt))
                        )
                        .branch(node("item")
                                .branch(nodeIndex()
                                        .branch(node("name").valueComponent(QuestContext::getLastRewardedItemName))
                                        .branch(node("rarity").valueComponent(QuestContext::getLastRewardedItemRarity))
                                        .branch(node("amount").valueNumber(QuestContext::getLastRewardedItemAmount))
                                        .branch(node("lore").branch(nodeIndex().valueComponent(QuestContext::getLastRewardedItemLore)))
                                        .branch(nodeStringArray().value(QuestContext::getLastRewardedItemNbt))
                                )
                        )
                )
        );

        register(node("screen")
                .branch(node("last_screen").valueComponent(ScreenContext::getLastScreen))
        );

        register(node("constant_data")
                .branch(node("data")
                        .branch(node("fish")
                                .branch(node("variant").branch(nodeString().valueComponent(ConstantDataContext::getFishVariant)))
                                .branch(node("rarity").branch(nodeString().valueComponent(ConstantDataContext::getFishRarity)))
                                .branch(node("size").branch(nodeString().valueComponent(ConstantDataContext::getFishSize)))
                        )
                        .branch(node("pet")
                                .branch(node("rarity").branch(nodeString().valueComponent(ConstantDataContext::getPetRarity)))
                                .branch(node("rating").branch(nodeString().valueComponent(ConstantDataContext::getPetRating)))
                        )
                )
        );

        register(node("profile_data")
                .branch(node("data")
                        .branch(node("active_pet_slot").valueNumber(ProfileDataContext::getActivePetSlot))
                        .branch(node("has_imported_stats").valueBoolean(ProfileDataContext::getHasImportedStats))
                        .branch(node("has_imported_crew").valueBoolean(ProfileDataContext::getHasImportedCrew))
                        .branch(node("is_in_crew_chat").valueBoolean(ProfileDataContext::getIsInCrewChat))
                        .branch(node("tournament_contribution").valueBoolean(ProfileDataContext::getTournamentContribution))
                )
        );

        register(node("quest_data")
                .branch(node("data")
                        .branch(nodeIndex()
                                .branch(node("goal").valueComponent(QuestDataContext::getGoal))
                                .branch(node("max").valueNumber(QuestDataContext::getMax))
                                .branch(node("current").valueNumber(QuestDataContext::getCurrent))
                        )
                )
        );

        register(node("stats_data")
                .branch(node("data")
                        .branch(node("fish")
                                .branch(node("total").valueNumber(StatsDataContext::getFishTotal))
                                .branch(node("rarity")
                                        .branch(nodeString()
                                                .branch(node("count").valueNumber(StatsDataContext::getFishRarityCount))
                                                .branch(node("dry_streak").valueNumber(StatsDataContext::getFishRarityDryStreak))
                                        )
                                )
                                .branch(node("size")
                                        .branch(nodeString()
                                                .branch(node("count").valueNumber(StatsDataContext::getFishSizeCount))
                                                .branch(node("dry_streak").valueNumber(StatsDataContext::getFishSizeDryStreak))
                                        )
                                )
                                .branch(node("variant")
                                        .branch(nodeString()
                                                .branch(node("count").valueNumber(StatsDataContext::getFishVariantCount))
                                                .branch(node("dry_streak").valueNumber(StatsDataContext::getFishVariantDryStreak))
                                        )
                                )
                        )
                        .branch(node("pet")
                                .branch(node("total").valueNumber(StatsDataContext::getPetTotal))
                                .branch(node("dry_streak").valueNumber(StatsDataContext::getPetDryStreak))
                                .branch(node("rarity")
                                        .branch(nodeString()
                                                .branch(node("count").valueNumber(StatsDataContext::getPetRarityCount))
                                                .branch(node("dry_streak").valueNumber(StatsDataContext::getPetRarityDryStreak))
                                        )
                                )
                                .branch(node("rating")
                                        .branch(nodeString()
                                                .branch(node("count").valueNumber(StatsDataContext::getPetRatingCount))
                                                .branch(node("dry_streak").valueNumber(StatsDataContext::getPetRatingDryStreak))
                                        )
                                )
                        )
                        .branch(node("item")
                                .branch(nodeString()
                                        .branch(node("count").valueNumber(StatsDataContext::getItemCount))
                                        .branch(node("dry_streak").valueNumber(StatsDataContext::getItemDryStreak))
                                )
                        )
                )
        );

        register(node("crew_data")
                .branch(node("data")
                        .branch(nodeIndex()
                                .branch(node("id").valueString(CrewDataContext::getUuid))
                                .branch(node("name").valueString(CrewDataContext::getName))
                        )
                )
        );

        register(node("tracker_data")
                .branch(node("data")
                        .branch(nodeString()
                                .branch(node("value").value(TrackerDataContext::getValue))
                                .branch(node("itemstack")
                                        .branch(node("lore").branch(nodeIndex().valueComponent(TrackerDataContext::getItemLore)))
                                        .branch(nodeStringArray().value(TrackerDataContext::getItemNbt))
                                )
                        )
                )
        );
        //endregion

        //region Boolean Functions
        register(node("condition").evalBoolean(EvaluationContext::evalCondition));
        register(node("is_blank").evalBoolean(EvaluationContext::evalIsBlank));
        register(node("contains").evalBoolean(EvaluationContext::evalContains));
        register(node("ends_with").evalBoolean(EvaluationContext::evalEndsWith));
        register(node("starts_with").evalBoolean(EvaluationContext::evalStartsWith));
        register(node("or").evalBoolean(EvaluationContext::evalOr));
        register(node("and").evalBoolean(EvaluationContext::evalAnd));
        register(node("not").evalBoolean(EvaluationContext::evalNot));
        register(node("xor").evalBoolean(EvaluationContext::evalXor));
        //endregion

        //region String Manipulation Functions
        register(node("substring").evalValue(EvaluationContext::evalSubstring));
        register(node("index_of").evalNumber(EvaluationContext::evalIndexOf));
        register(node("repeat").evalValue(EvaluationContext::evalRepeat));
        register(node("uppercase").evalValue(EvaluationContext::evalUppercase));
        register(node("lowercase").evalValue(EvaluationContext::evalLowercase));
        register(node("shorten_number").evalString(EvaluationContext::evalShortenNumber));
        register(node("remove_format").evalString(EvaluationContext::evalRemoveFormat));
        register(node("format_time").evalString(EvaluationContext::evalFormatTime));
        //endregion

        //region Math Functions
        register(node("expression").evalNumber(EvaluationContext::evalExpression));
        register(node("max").evalNumber(EvaluationContext::evalMax));
        register(node("min").evalNumber(EvaluationContext::evalMin));
        register(node("abs").evalNumber(EvaluationContext::evalAbs));
        register(node("ceil").evalNumber(EvaluationContext::evalCeil));
        register(node("floor").evalNumber(EvaluationContext::evalFloor));
        register(node("round").evalNumber(EvaluationContext::evalRound));
        register(node("mod").evalNumber(EvaluationContext::evalMod));
        register(node("clamp").evalNumber(EvaluationContext::evalClamp));
        register(node("log").evalNumber(EvaluationContext::evalLog));
        register(node("pow").evalNumber(EvaluationContext::evalPow));
        //endregion

        //region Misc
        register(node("hide_line").evalValue(EvaluationContext::evalHideLine).allowEmpty());
        //endregion
    }

    public static void register(PlaceholderTreeNode root) {
        ROOTS.put(root.key(), root);
    }

    public static PlaceholderTreeNode getRoot(String key) {
        return ROOTS.get(key);
    }

    //region Placeholder Contexts

    static class BossEventContext {
        static MutableComponent getLocation() {
            return BossEventHandler.instance().getLocation();
        }

        static MutableComponent getWeather() {
            return BossEventHandler.instance().getWeather();
        }

        static MutableComponent getTime() {
            return BossEventHandler.instance().getTime();
        }

        static MutableComponent getTemperature() {
            return BossEventHandler.instance().getTemperature();
        }

        static MutableComponent getSubLocation() {
            return BossEventHandler.instance().getSubLocation();
        }

        static MutableComponent getCommunityGoalCurrent() {
            return BossEventHandler.instance().getCommunityGoalCurrent();
        }

        static MutableComponent getCommunityGoalMax() {
            return BossEventHandler.instance().getCommunityGoalMax();
        }
    }

    static class PlayerContext {
        static String getName() {
            return Minecraft.getInstance().player.getName().getString();
        }

        static Number getLevel() {
            return Minecraft.getInstance().player.experienceLevel;
        }

        static Number getLevelProgress() {
            return Minecraft.getInstance().player.experienceProgress * 100;
        }

        static Number getPosX() {
            return Minecraft.getInstance().player.position().x;
        }

        static Number getPosY() {
            return Minecraft.getInstance().player.position().y;
        }

        static Number getPosZ() {
            return Minecraft.getInstance().player.position().z;
        }

        static Number getFps() {
            return Minecraft.getInstance().getFps();
        }
    }

    static class ScoreboardContext {
        static MutableComponent getLevel() {
            return ScoreboardHandler.instance().getLevel();
        }

        static Number getWallet() {
            return TextHelper.toIntFromString(ScoreboardHandler.instance().getWallet().getString().substring(1));
        }

        static Number getCredits() {
            return TextHelper.toIntFromString(ScoreboardHandler.instance().getCredits().getString());
        }

        static Number getCatches() {
            return TextHelper.toIntFromString(ScoreboardHandler.instance().getCatches().getString().trim());
        }

        static Number getLocationMin() {
            return Integer.parseInt(ScoreboardHandler.instance().getLocationMin().getString().trim());
        }

        static Number getLocationMax() {
            return Integer.parseInt(ScoreboardHandler.instance().getLocationMax().getString().trim());
        }

        static String getCatchRate() {
            return ScoreboardHandler.instance().getCatchRate().getString();
        }

        static String getCrew() {
            return ScoreboardHandler.instance().getCrew().getString();
        }

        static MutableComponent getCrewNearby() {
            return ScoreboardHandler.instance().isCrewNearby();
        }

        static String getVersion() {
            return ScoreboardHandler.instance().getVersion().getString();
        }

        static String getDate() {
            return ScoreboardHandler.instance().getDate().getString();
        }
    }

    static class TabOverlayContext {
        static MutableComponent getPlayerName() {
            return TabOverlayHandler.instance().getPlayerName().copy();
        }

        static String getInstance() {
            return TabOverlayHandler.instance().getInstance();
        }

        static Boolean getIsInInstance() {
            return TabOverlayHandler.instance().isInInstance();
        }
    }

    static class TitleContext {
        static MutableComponent getTitle() {
            return TitleHandler.instance().getTitle();
        }

        static MutableComponent getSubTitle() {
            return TitleHandler.instance().getSubTitle();
        }
    }

    static class ConnectionContext {
        static Boolean getIsOnServer() {
            return ConnectionHandler.instance().isOnServer();
        }

        static Boolean getWasOnServer() {
            return ConnectionHandler.instance().wasOnServer();
        }
    }

    static class InventoryContext {
        static Number getEmptySlots() {
            return InventoryHandler.instance().getCurrentEmptySlots();
        }

        static MutableComponent getFishingRodName() {
            return InventoryHandler.instance().getCurrentFishingRod().getName().copy();
        }

        static MutableComponent getFishingRodLore(List<String> indices) {
            return getLoreValue(InventoryHandler.instance().getCurrentFishingRod(), indices.getFirst());
        }

        static PlaceholderValue getFishingRodNbt(List<String> indices) {
            return getNbtValue(InventoryHandler.instance().getCurrentFishingRod(), indices);
        }

        static MutableComponent getFishingRodLineName() {
            List<TagObject> lineList = InventoryHandler.instance().getCurrentFishingRod().getLineItem();

            if(!lineList.isEmpty()) {
                return lineList.getFirst().getName().copy();
            }

            return Component.empty();
        }

        static MutableComponent getFishingRodLineLore(List<String> indices) {
            List<TagObject> lineList = InventoryHandler.instance().getCurrentFishingRod().getLineItem();

            if(!lineList.isEmpty()) {
                return getLoreValue(lineList.getFirst(), indices.getFirst());
            }

            return Component.empty();
        }

        static PlaceholderValue getFishingRodLineNbt(List<String> indices) {
            List<TagObject> lineList = InventoryHandler.instance().getCurrentFishingRod().getLineItem();

            if(!lineList.isEmpty()) {
                return getNbtValue(lineList.getFirst(), indices);
            }

            return PlaceholderValue.emptyText();
        }

        static MutableComponent getFishingRodReelName() {
            List<TagObject> reelList = InventoryHandler.instance().getCurrentFishingRod().getReelItem();

            if(!reelList.isEmpty()) {
                return reelList.getFirst().getName().copy();
            }

            return Component.empty();
        }

        static MutableComponent getFishingRodReelLore(List<String> indices) {
            List<TagObject> reelList = InventoryHandler.instance().getCurrentFishingRod().getReelItem();

            if(!reelList.isEmpty()) {
                return getLoreValue(reelList.getFirst(), indices.getFirst());
            }

            return Component.empty();
        }

        static PlaceholderValue getFishingRodReelNbt(List<String> indices) {
            List<TagObject> reelList = InventoryHandler.instance().getCurrentFishingRod().getReelItem();

            if(!reelList.isEmpty()) {
                return getNbtValue(reelList.getFirst(), indices);
            }

            return PlaceholderValue.emptyText();
        }

        static MutableComponent getFishingRodPoleName() {
            List<TagObject> poleList = InventoryHandler.instance().getCurrentFishingRod().getPoleItem();

            if(!poleList.isEmpty()) {
                return poleList.getFirst().getName().copy();
            }

            return Component.empty();
        }

        static MutableComponent getFishingRodPoleLore(List<String> indices) {
            List<TagObject> poleList = InventoryHandler.instance().getCurrentFishingRod().getPoleItem();

            if(!poleList.isEmpty()) {
                return getLoreValue(poleList.getFirst(), indices.getFirst());
            }

            return Component.empty();
        }

        static PlaceholderValue getFishingRodPoleNbt(List<String> indices) {
            List<TagObject> poleList = InventoryHandler.instance().getCurrentFishingRod().getPoleItem();

            if(!poleList.isEmpty()) {
                return getNbtValue(poleList.getFirst(), indices);
            }

            return PlaceholderValue.emptyText();
        }

        static MutableComponent getPetName() {
            return InventoryHandler.instance().getCurrentPet().getName().copy();
        }

        static Number getPetLevel() {
            return InventoryHandler.instance().getCurrentPet().getLevel();
        }

        static Number getPetLevelProgress() {
            return InventoryHandler.instance().getCurrentPet().getProgress();
        }

        static MutableComponent getPetRating() {
            return InventoryHandler.instance().getCurrentPet().getRatingComponent().copy();
        }

        static Number getPetRatingPercent() {
            return InventoryHandler.instance().getCurrentPet().getTotalPercent() * 100;
        }

        static MutableComponent getPetRarity() {
            return InventoryHandler.instance().getCurrentPet().getRarityComponent().copy();
        }

        static Number getPetLocationLuckPercent() {
            return InventoryHandler.instance().getCurrentPet().getLocationPercentMaxLuck() * 100;
        }

        static Number getPetLocationScalePercent() {
            return InventoryHandler.instance().getCurrentPet().getLocationPercentMaxScale() * 100;
        }

        static Number getPetClimateLuckPercent() {
            return InventoryHandler.instance().getCurrentPet().getClimatePercentMaxLuck() * 100;
        }

        static Number getPetClimateScalePercent() {
            return InventoryHandler.instance().getCurrentPet().getClimatePercentMaxScale() * 100;
        }

        static Number getPetLocationLuck() {
            return InventoryHandler.instance().getCurrentPet().getLocationMaxLuck();
        }

        static Number getPetLocationScale() {
            return InventoryHandler.instance().getCurrentPet().getLocationMaxScale();
        }

        static Number getPetClimateLuck() {
            return InventoryHandler.instance().getCurrentPet().getClimateMaxLuck();
        }

        static Number getPetClimateScale() {
            return InventoryHandler.instance().getCurrentPet().getClimateMaxScale();
        }

        static MutableComponent getPetLore(List<String> indices) {
            return getLoreValue(InventoryHandler.instance().getCurrentPet(), indices.getFirst());
        }

        static PlaceholderValue getPetNbt(List<String> indices) {
            return getNbtValue(InventoryHandler.instance().getCurrentPet(), indices);
        }

        static MutableComponent getChestplateName() {
            ItemStack armor = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.CHEST);
            Pair<Boolean, TagObject> validatedItem = ValidateItem.isServerItem(armor, true);
            return validatedItem.value1() ? validatedItem.value2().getName().copy() : Component.empty();
        }

        static MutableComponent getChestplateLore(List<String> indices) {
            ItemStack armor = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.CHEST);
            return getLoreValue(armor, indices.getFirst());
        }

        static PlaceholderValue getChestplateNbt(List<String> indices) {
            ItemStack armor = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.CHEST);
            return getNbtValue(armor, indices);
        }

        static MutableComponent getLeggingsName() {
            ItemStack armor = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.LEGS);
            Pair<Boolean, TagObject> validatedItem = ValidateItem.isServerItem(armor, true);
            return validatedItem.value1() ? validatedItem.value2().getName().copy() : Component.empty();
        }

        static MutableComponent getLeggingsLore(List<String> indices) {
            ItemStack armor = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.LEGS);
            return getLoreValue(armor, indices.getFirst());
        }

        static PlaceholderValue getLeggingsNbt(List<String> indices) {
            ItemStack armor = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.LEGS);
            return getNbtValue(armor, indices);
        }

        static MutableComponent getBootsName() {
            ItemStack armor = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.FEET);
            Pair<Boolean, TagObject> validatedItem = ValidateItem.isServerItem(armor, true);
            return validatedItem.value1() ? validatedItem.value2().getName().copy() : Component.empty();
        }

        static MutableComponent getBootsLore(List<String> indices) {
            ItemStack armor = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.FEET);
            return getLoreValue(armor, indices.getFirst());
        }

        static PlaceholderValue getBootsNbt(List<String> indices) {
            ItemStack armor = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.FEET);
            return getNbtValue(armor, indices);
        }

        static MutableComponent getHeldItemName() {
            return InventoryHandler.instance().getCurrentHeldItem().getName().copy();
        }

        static MutableComponent getHeldItemLore(List<String> indices) {
            return getLoreValue(InventoryHandler.instance().getCurrentHeldItem(), indices.getFirst());
        }

        static PlaceholderValue getHeldItemNbt(List<String> indices) {
            return getNbtValue(InventoryHandler.instance().getCurrentHeldItem(), indices);
        }

        static MutableComponent getSlotName(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());

            if(index >= 0) {
                ItemStack item = Minecraft.getInstance().player.getInventory().getItem(index);
                return item.getHoverName().copy();
            }
            return Component.empty();
        }

        static MutableComponent getSlotLore(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());

            if(index >= 0) {
                ItemStack item = Minecraft.getInstance().player.getInventory().getItem(index);
                return getLoreValue(item, indices.get(1));
            }
            return Component.empty();
        }

        static PlaceholderValue getSlotNbt(List<String> indices) {
            if(indices.size() > 1) {
                int index = Integer.parseInt(indices.getFirst());

                if(index >= 0) {
                    ItemStack item = Minecraft.getInstance().player.getInventory().getItem(index);
                    return getNbtValue(item, indices.subList(1, indices.size()));
                }
            }
            return PlaceholderValue.emptyText();
        }
    }

    static class KeyBindContext {
        static String getOpenMainKeyBind() {
            return KeyBindHelper.getKeyString(Configs.keyBindConfig.openMainKeybind);
        }

        static String getInspectKeyBind() {
            return KeyBindHelper.getKeyString(Configs.keyBindConfig.inspectKeybind);
        }
    }

    static class LoadingContext {
        static Boolean getIsLoadingDone() {
            return LoadingHandler.instance().isLoadingDone();
        }

        static Boolean getIsError() {
            return LoadingHandler.instance().isError();
        }
    }

    static class HitResultContext {
        static MutableComponent getBlockName() {
            return HitResultHandler.instance().getBlockFromHitResult();
        }

        static MutableComponent getEntityName() {
            return HitResultHandler.instance().getEntityHitResult() != null ? HitResultHandler.instance().getEntityHitResult().getEntity().getName().copy() : Component.empty();
        }

        static MutableComponent getItemFromItemFrameName() {
            return HitResultHandler.instance().getItemFrameItem().getItem().getName().copy();
        }
    }

    static class NetworkContext {
        static Number getPing() {
            return NetworkHandler.instance().getPing();
        }
    }

    static class CrewContext {
        static String getOnlineName(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            if(index >= 0 && index < CrewHandler.instance().getOnlineMembers().size()) {
                return CrewHandler.instance().getOnlineMembers().get(index).value2();
            }
            return "";
        }

        static String getOnlineId(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            if(index >= 0 && index < CrewHandler.instance().getOnlineMembers().size()) {
                return CrewHandler.instance().getOnlineMembers().get(index).value1().toString();
            }
            return "";
        }

        static String getOfflineName(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            if(index >= 0 && index < CrewHandler.instance().getOfflineMembers().size()) {
                return CrewHandler.instance().getOnlineMembers().get(index).value2();
            }
            return "";
        }

        static String getOfflineId(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            if(index >= 0 && index < CrewHandler.instance().getOfflineMembers().size()) {
                return CrewHandler.instance().getOnlineMembers().get(index).value1().toString();
            }
            return "";
        }

        static Boolean getIsCrewNearby() {
            return CrewHandler.instance().isCrewNearby();
        }
    }

    static class ChatContext {
        static MutableComponent getStoredChatTrigger(List<String> indices) {
            return ChatHandler.instance().getStoredChatTriggerComponent().getOrDefault(indices.getFirst(), Component.empty()).copy();
        }
    }

    static class TimerContext {
        static Number getTimer(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer != null) {
                return timer.getTimer();
            }
            return null;
        }

        static Number getOffset(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer != null) {
                return timer.getOffset();
            }
            return null;
        }

        static String getNotificationToTrigger(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer != null) {
                return timer.getNotificationToTrigger();
            }
            return "";
        }

        static String getCleanUpChatTrigger(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer != null) {
                return timer.getNotificationToTrigger();
            }
            return "";
        }

        static Boolean getIsUseTimer(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer != null) {
                return timer.isUseTimer();
            }
            return false;
        }

        static Boolean getIsPeriod(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer != null) {
                return timer.isPeriod();
            }
            return false;
        }

        static Number getOffTimer(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                return timerPeriod.getOffTimer();
            }
            return null;
        }

        static String getNotificationToTriggerEnd(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                return timerPeriod.getNotificationToTriggerEnd();
            }
            return "";
        }

        static Number getTimeSecond(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer != null) {
                long timeSeconds = System.currentTimeMillis() / 1000;
                long adjusted = timeSeconds + timer.getOffset();
                long pos = adjusted % timer.getTimer();
                long remaining = timer.getTimer() - pos;

                return Math.floor(remaining % 60);
            }
            return null;
        }

        static Number getTimeMinute(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer != null) {
                long timeSeconds = System.currentTimeMillis() / 1000;
                long adjusted = timeSeconds + timer.getOffset();
                long pos = adjusted % timer.getTimer();
                long remaining = timer.getTimer() - pos;

                return Math.floor((double) (remaining % 3600) / 60);
            }
            return null;
        }

        static Number getTimeHour(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer != null) {
                long timeSeconds = System.currentTimeMillis() / 1000;
                long adjusted = timeSeconds + timer.getOffset();
                long pos = adjusted % timer.getTimer();
                long remaining = timer.getTimer() - pos;

                return Math.floor((double) remaining / 3600);
            }
            return null;
        }

        static Number getOnTimeSecond(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                long cycle = timerPeriod.getTimer() + timerPeriod.getOffTimer();
                long adjusted = System.currentTimeMillis() / 1000 + timerPeriod.getOffset();
                long pos = adjusted % cycle;
                long remaining = pos < timerPeriod.getTimer()
                        ? timerPeriod.getTimer() - pos
                        : (cycle - pos) + timerPeriod.getTimer();

                return Math.floor(remaining % 60);
            }
            return null;
        }

        static Number getOnTimeMinute(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                long cycle = timerPeriod.getTimer() + timerPeriod.getOffTimer();
                long adjusted = System.currentTimeMillis() / 1000 + timerPeriod.getOffset();
                long pos = adjusted % cycle;
                long remaining = pos < timerPeriod.getTimer()
                        ? timerPeriod.getTimer() - pos
                        : (cycle - pos) + timerPeriod.getTimer();

                return Math.floor((double) (remaining % 3600) / 60);
            }
            return null;
        }

        static Number getOnTimeHour(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                long cycle = timerPeriod.getTimer() + timerPeriod.getOffTimer();
                long adjusted = System.currentTimeMillis() / 1000 + timerPeriod.getOffset();
                long pos = adjusted % cycle;
                long remaining = pos < timerPeriod.getTimer()
                        ? timerPeriod.getTimer() - pos
                        : (cycle - pos) + timerPeriod.getTimer();

                return Math.floor((double) remaining / 3600);
            }
            return null;
        }

        static Number getOffTimeSecond(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                long cycle = timerPeriod.getTimer() + timerPeriod.getOffTimer();
                long adjusted = System.currentTimeMillis() / 1000 + timerPeriod.getOffset();
                long pos = adjusted % cycle;
                long remaining = cycle - pos;

                return Math.floor(remaining % 60);
            }
            return null;
        }

        static Number getOffTimeMinute(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                long cycle = timerPeriod.getTimer() + timerPeriod.getOffTimer();
                long adjusted = System.currentTimeMillis() / 1000 + timerPeriod.getOffset();
                long pos = adjusted % cycle;
                long remaining = cycle - pos;

                return Math.floor((double) (remaining % 3600) / 60);
            }
            return null;
        }

        static Number getOffTimeHour(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                long cycle = timerPeriod.getTimer() + timerPeriod.getOffTimer();
                long adjusted = System.currentTimeMillis() / 1000 + timerPeriod.getOffset();
                long pos = adjusted % cycle;
                long remaining = cycle - pos;

                return Math.floor((double) remaining / 3600);
            }
            return null;
        }

        static Boolean getIsOn(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                long cycle = timerPeriod.getTimer() + timerPeriod.getOffTimer();
                long adjusted = System.currentTimeMillis() / 1000 + timerPeriod.getOffset();
                long pos = adjusted % cycle;

                return pos < timerPeriod.getTimer();
            }
            return false;
        }

        static Boolean getIsOff(List<String> indices) {
            CustomTimerDataHandler.CustomTimer timer = TimerHandler.instance().getTimers().stream().filter(t -> Objects.equals(t.getName(), indices.getFirst())).findFirst().orElse(null);
            if(timer instanceof CustomTimerDataHandler.CustomTimerPeriod timerPeriod) {
                long cycle = timerPeriod.getTimer() + timerPeriod.getOffTimer();
                long adjusted = System.currentTimeMillis() / 1000 + timerPeriod.getOffset();
                long pos = adjusted % cycle;

                return !(pos < timerPeriod.getTimer());
            }
            return false;
        }
    }

    static class CatchContext {
        static MutableComponent getLastCaughtFishName() {
            if(!CatchingHandler.instance().getLastCaughtFish().getItemStack().isEmpty()) {
                return CatchingHandler.instance().getLastCaughtFish().getName().copy();
            }
            return Component.empty();
        }

        static String getLastCaughtFishRarityName() {
            if(CatchingHandler.instance().getLastDataFish() != null) {
                return CatchingHandler.instance().getLastDataFish().value1().value1();
            }
            return "";
        }

        static MutableComponent getLastCaughtFishRarityIcon() {
            if(!CatchingHandler.instance().getLastCaughtFish().getItemStack().isEmpty()) {
                return CatchingHandler.instance().getLastCaughtFish().getRarityComponent().copy();
            }
            return Component.empty();
        }

        static Number getLastCaughtFishRarityDryStreak() {
            if(CatchingHandler.instance().getLastDataFish() != null) {
                return CatchingHandler.instance().getLastDataFish().value1().value2();
            }
            return null;
        }

        static String getLastCaughtFishVariantName() {
            if(CatchingHandler.instance().getLastDataFish() != null) {
                return CatchingHandler.instance().getLastDataFish().value2().value1();
            }
            return "";
        }

        static MutableComponent getLastCaughtFishVariantIcon() {
            if(!CatchingHandler.instance().getLastCaughtFish().getItemStack().isEmpty()) {
                return CatchingHandler.instance().getLastCaughtFish().getVariantComponent().copy();
            }
            return Component.empty();

        }

        static Number getLastCaughtFishVariantDryStreak() {
            if(CatchingHandler.instance().getLastDataFish() != null) {
                return CatchingHandler.instance().getLastDataFish().value2().value2();
            }
            return null;

        }

        static String getLastCaughtFishSizeName() {
            if(CatchingHandler.instance().getLastDataFish() != null) {
                return CatchingHandler.instance().getLastDataFish().value3().value1();
            }
            return "";
        }

        static MutableComponent getLastCaughtFishSizeIcon() {
            if(!CatchingHandler.instance().getLastCaughtFish().getItemStack().isEmpty()) {
                return CatchingHandler.instance().getLastCaughtFish().getFishSizeComponent().copy();
            }
            return Component.empty();
        }

        static Number getLastCaughtFishSizeDryStreak() {
            if(CatchingHandler.instance().getLastDataFish() != null) {
                return CatchingHandler.instance().getLastDataFish().value3().value2();
            }
            return null;
        }

        static MutableComponent getLastCaughtFishLore(List<String> indices) {
            return getLoreValue(CatchingHandler.instance().getLastCaughtFish(), indices.getFirst());
        }

        static PlaceholderValue getLastCaughtFishNbt(List<String> indices) {
            return getNbtValue(CatchingHandler.instance().getLastCaughtFish(), indices);
        }

        static MutableComponent getLastCaughtItemName(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return CatchingHandler.instance().getLastCaughtItems().get(index).value1().getName().copy();
        }

        static Number getLastCaughtItemStackAmount(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return CatchingHandler.instance().getLastCaughtItems().get(index).value2();
        }

        static String getLastCaughtItemId(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return CatchingHandler.instance().getLastCaughtItems().get(index).value3().value1();
        }

        static Number getLastCaughtItemDryStreak(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return CatchingHandler.instance().getLastCaughtItems().get(index).value3().value2();
        }

        static MutableComponent getLastCaughtItemLore(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return getLoreValue(CatchingHandler.instance().getLastCaughtItems().get(index).value1(), indices.get(1));
        }

        static PlaceholderValue getLastCaughtItemNbt(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return getNbtValue(CatchingHandler.instance().getLastCaughtItems().get(index).value1(), indices.subList(1, indices.size()));
        }

        static MutableComponent getLastCaughtPetName() {
            if(!CatchingHandler.instance().getLastCaughtPet().getItemStack().isEmpty()) {
                return CatchingHandler.instance().getLastCaughtPet().getName().copy();
            }
            return Component.empty();
        }

        static String getLastCaughtPetRarityName() {
            if(CatchingHandler.instance().getLastDataPet() != null) {
                return CatchingHandler.instance().getLastDataPet().value1().value1();
            }
            return "";
        }

        static MutableComponent getLastCaughtPetRarityIcon() {
            if(!CatchingHandler.instance().getLastCaughtPet().getItemStack().isEmpty()) {
                return CatchingHandler.instance().getLastCaughtPet().getRarityComponent().copy();
            }
            return Component.empty();
        }

        static Number getLastCaughtPetRarityDryStreak() {
            if(CatchingHandler.instance().getLastDataPet() != null) {
                return CatchingHandler.instance().getLastDataPet().value1().value2();
            }
            return null;
        }

        static String getLastCaughtPetRatingName() {
            if(CatchingHandler.instance().getLastDataPet() != null) {
                return CatchingHandler.instance().getLastDataPet().value2().value1();
            }
            return "";
        }

        static MutableComponent getLastCaughtPetRatingIcon() {
            if(!CatchingHandler.instance().getLastCaughtPet().getItemStack().isEmpty()) {
                return CatchingHandler.instance().getLastCaughtPet().getRarityComponent().copy();
            }
            return Component.empty();
        }

        static Number getLastCaughtPetRatingDryStreak() {
            if(CatchingHandler.instance().getLastDataPet() != null) {
                return CatchingHandler.instance().getLastDataPet().value2().value2();
            }
            return null;
        }

        static MutableComponent getLastCaughtPetLore(List<String> indices) {
            return getLoreValue(CatchingHandler.instance().getLastCaughtPet(), indices.getFirst());
        }

        static PlaceholderValue getLastCaughtPetNbt(List<String> indices) {
            return getNbtValue(CatchingHandler.instance().getLastCaughtPet(), indices);
        }
    }

    static class QuestContext {
        static MutableComponent getLastRewardedPetName() {
            return QuestHandler.instance().getLastRewardedPet().getName().copy();
        }

        static Number getLastRewardedPetLevel() {
            return QuestHandler.instance().getLastRewardedPet().getLevel();
        }

        static Number getLastRewardedPetLevelProgress() {
            return QuestHandler.instance().getLastRewardedPet().getProgress();
        }

        static MutableComponent getLastRewardedPetRating() {
            return QuestHandler.instance().getLastRewardedPet().getRatingComponent().copy();
        }

        static Number getLastRewardedPetRatingPercent() {
            return QuestHandler.instance().getLastRewardedPet().getTotalPercent() * 100;
        }

        static MutableComponent getLastRewardedPetRarity() {
            return QuestHandler.instance().getLastRewardedPet().getRarityComponent().copy();
        }

        static Number getLastRewardedPetLocationLuckPercent() {
            return QuestHandler.instance().getLastRewardedPet().getLocationPercentMaxLuck() * 100;
        }

        static Number getLastRewardedPetLocationScalePercent() {
            return QuestHandler.instance().getLastRewardedPet().getLocationPercentMaxScale() * 100;
        }

        static Number getLastRewardedPetClimateLuckPercent() {
            return QuestHandler.instance().getLastRewardedPet().getClimatePercentMaxLuck() * 100;
        }

        static Number getLastRewardedPetClimateScalePercent() {
            return QuestHandler.instance().getLastRewardedPet().getClimatePercentMaxScale() * 100;
        }

        static Number getLastRewardedPetLocationLuck() {
            return QuestHandler.instance().getLastRewardedPet().getLocationMaxLuck();
        }

        static Number getLastRewardedPetLocationScale() {
            return QuestHandler.instance().getLastRewardedPet().getLocationMaxScale();
        }

        static Number getLastRewardedPetClimateLuck() {
            return QuestHandler.instance().getLastRewardedPet().getClimateMaxLuck();
        }

        static Number getLastRewardedPetClimateScale() {
            return QuestHandler.instance().getLastRewardedPet().getClimateMaxScale();
        }

        static MutableComponent getLastRewardedPetLore(List<String> indices) {
            return getLoreValue(QuestHandler.instance().getLastRewardedPet(), indices.getFirst());
        }

        static PlaceholderValue getLastRewardedPetNbt(List<String> indices) {
            return getNbtValue(QuestHandler.instance().getLastRewardedPet(), indices);
        }

        static MutableComponent getLastRewardedItemName(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return QuestHandler.instance().getLastRewardedItems().get(index).value1().getName().copy();
        }

        static MutableComponent getLastRewardedItemRarity(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return QuestHandler.instance().getLastRewardedItems().get(index).value1().getRarityComponent().copy();
        }

        static Number getLastRewardedItemAmount(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return QuestHandler.instance().getLastRewardedItems().get(index).value2();
        }

        static MutableComponent getLastRewardedItemLore(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return getLoreValue(QuestHandler.instance().getLastRewardedItems().get(index).value1(), indices.get(1));
        }

        static PlaceholderValue getLastRewardedItemNbt(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return getNbtValue(QuestHandler.instance().getLastRewardedItems().get(index).value1(), indices.subList(1, indices.size()));
        }
    }

    static class ScreenContext {
        static MutableComponent getLastScreen() {
            return ScreenHander.instance().getLastScreen().copy();
        }
    }

    static class ConstantDataContext {
        static MutableComponent getFishRarity(List<String> indices) {
            return ConstantDataHandler.instance().getConstantData().fishData.getOrDefault(FishTagObject.RARITY, Map.of()).getOrDefault(indices.getFirst(), Component.empty()).copy();
        }

        static MutableComponent getFishSize(List<String> indices) {
            return ConstantDataHandler.instance().getConstantData().fishData.getOrDefault(FishTagObject.FISH_SIZE, Map.of()).getOrDefault(indices.getFirst(), Component.empty()).copy();
        }

        static MutableComponent getFishVariant(List<String> indices) {
            return ConstantDataHandler.instance().getConstantData().fishData.getOrDefault(FishTagObject.VARIANT, Map.of()).getOrDefault(indices.getFirst(), Component.empty()).copy();
        }

        static MutableComponent getPetRarity(List<String> indices) {
            return ConstantDataHandler.instance().getConstantData().petData.getOrDefault(PetTagObject.RARITY, Map.of()).getOrDefault(indices.getFirst(), Component.empty()).copy();
        }

        static MutableComponent getPetRating(List<String> indices) {
            return ConstantDataHandler.instance().getConstantData().petData.getOrDefault(PetTagObject.RATING, Map.of()).getOrDefault(TextHelper.smallCaps(indices.getFirst()), Component.empty()).copy();
        }
    }

    static class ProfileDataContext {
        static Number getActivePetSlot() {
            return ProfileDataHandler.instance().getProfileData().activePetSlot;
        }

        static Boolean getHasImportedStats() {
            return ProfileDataHandler.instance().getProfileData().hasImportedStats;
        }

        static Boolean getHasImportedCrew() {
            return ProfileDataHandler.instance().getProfileData().hasImportedCrew;
        }

        static Boolean getIsInCrewChat() {
            return ProfileDataHandler.instance().getProfileData().isInCrewChat;
        }

        static Boolean getTournamentContribution() {
            return ProfileDataHandler.instance().getProfileData().tournamentContribution;
        }
    }

    static class QuestDataContext {
        static MutableComponent getGoal(List<String> indices) {
            String location = BossEventHandler.instance().getLocation().getString();
            List<QuestDataHandler.Quest> quests = QuestDataHandler.instance().getQuestData().questList.getOrDefault(location, List.of());
            int index = Integer.parseInt(indices.getFirst());
            if(!quests.isEmpty() && index >= 0 && index < quests.size()) return ConstantDataHandler.instance().getConstantFishComponent(quests.get(index).goal).copy();
            return Component.empty();
        }

        static Number getMax(List<String> indices) {
            String location = BossEventHandler.instance().getLocation().getString();
            List<QuestDataHandler.Quest> quests = QuestDataHandler.instance().getQuestData().questList.getOrDefault(location, List.of());
            int index = Integer.parseInt(indices.getFirst());
            if(!quests.isEmpty() && index >= 0 && index < quests.size()) return quests.get(index).max;
            return null;
        }

        static Number getCurrent(List<String> indices) {
            String location = BossEventHandler.instance().getLocation().getString();
            List<QuestDataHandler.Quest> quests = QuestDataHandler.instance().getQuestData().questList.getOrDefault(location, List.of());
            int index = Integer.parseInt(indices.getFirst());
            if(!quests.isEmpty() && index >= 0 && index < quests.size()) return quests.get(index).current;
            return null;
        }
    }

    static class StatsDataContext {
        static Number getFishTotal() {
            return StatsDataHandler.instance().getStatsData().fishTotal;
        }

        static Number getFishRarityCount(List<String> indices) {
            return StatsDataHandler.instance().getStatsData().fishData.getOrDefault(FishTagObject.RARITY, Map.of()).getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).amount();
        }

        static Number getFishRarityDryStreak(List<String> indices) {
            Number caughtOn = StatsDataHandler.instance().getStatsData().fishData.getOrDefault(FishTagObject.RARITY, Map.of()).getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).caughtOn();
            if(caughtOn != null) return StatsDataHandler.instance().getStatsData().fishTotal - caughtOn.intValue();
            return null;
        }

        static Number getFishSizeCount(List<String> indices) {
            return StatsDataHandler.instance().getStatsData().fishData.getOrDefault(FishTagObject.FISH_SIZE, Map.of()).getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).amount();
        }

        static Number getFishSizeDryStreak(List<String> indices) {
            Number caughtOn = StatsDataHandler.instance().getStatsData().fishData.getOrDefault(FishTagObject.FISH_SIZE, Map.of()).getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).caughtOn();
            if(caughtOn != null) return StatsDataHandler.instance().getStatsData().fishTotal - caughtOn.intValue();
            return null;
        }

        static Number getFishVariantCount(List<String> indices) {
            return StatsDataHandler.instance().getStatsData().fishData.getOrDefault(FishTagObject.VARIANT, Map.of()).getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).amount();
        }

        static Number getFishVariantDryStreak(List<String> indices) {
            Number caughtOn = StatsDataHandler.instance().getStatsData().fishData.getOrDefault(FishTagObject.VARIANT, Map.of()).getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).caughtOn();
            if(caughtOn != null) return StatsDataHandler.instance().getStatsData().fishTotal - caughtOn.intValue();
            return null;
        }

        static Number getPetTotal() {
            return StatsDataHandler.instance().getStatsData().petTotal;
        }

        static Number getPetDryStreak() {
            return StatsDataHandler.instance().getStatsData().fishTotal - StatsDataHandler.instance().getStatsData().petData.getOrDefault(PetTagObject.RARITY, new HashMap<>()).values().stream().mapToInt(StatsDataHandler.Stat::caughtOn).max().orElse(0);
        }

        static Number getPetRarityCount(List<String> indices) {
            return StatsDataHandler.instance().getStatsData().petData.getOrDefault(PetTagObject.RARITY, Map.of()).getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).amount();
        }

        static Number getPetRarityDryStreak(List<String> indices) {
            Number caughtOn = StatsDataHandler.instance().getStatsData().petData.getOrDefault(PetTagObject.RARITY, Map.of()).getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).caughtOn();
            if(caughtOn != null) return StatsDataHandler.instance().getStatsData().fishTotal - caughtOn.intValue();
            return null;
        }

        static Number getPetRatingCount(List<String> indices) {
            return StatsDataHandler.instance().getStatsData().petData.getOrDefault(PetTagObject.RATING, Map.of()).getOrDefault(TextHelper.smallCaps(indices.getFirst()), StatsDataHandler.Stat.of(null, null)).amount();
        }

        static Number getPetRatingDryStreak(List<String> indices) {
            Number caughtOn = StatsDataHandler.instance().getStatsData().petData.getOrDefault(PetTagObject.RATING, Map.of()).getOrDefault(TextHelper.smallCaps(indices.getFirst()), StatsDataHandler.Stat.of(null, null)).caughtOn();
            if(caughtOn != null) return StatsDataHandler.instance().getStatsData().fishTotal - caughtOn.intValue();
            return null;
        }

        static Number getItemCount(List<String> indices) {
            return StatsDataHandler.instance().getStatsData().itemData.getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).amount();
        }

        static Number getItemDryStreak(List<String> indices) {
            Number caughtOn = StatsDataHandler.instance().getStatsData().itemData.getOrDefault(indices.getFirst(), StatsDataHandler.Stat.of(null, null)).caughtOn();
            if(caughtOn != null) return StatsDataHandler.instance().getStatsData().fishTotal - caughtOn.intValue();
            return null;
        }
    }

    static class CrewDataContext {
        static String getUuid(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return CrewHandler.instance().getCrewListOrdered().get(index).value1().toString();
        }

        static String getName(List<String> indices) {
            int index = Integer.parseInt(indices.getFirst());
            return CrewHandler.instance().getCrewListOrdered().get(index).value2();
        }
    }

    static class TrackerDataContext {
        static PlaceholderValue getValue(List<String> indices) {
            CustomTrackerDataHandler.CustomTracker tracker = CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.getOrDefault(indices.getFirst(), null);
            if(tracker != null) {
                return switch (tracker.getValue()) {
                    case BooleanValue booleanValue -> PlaceholderValue.bool(booleanValue.value());
                    case ItemStackValue itemStackValue -> PlaceholderValue.component(itemStackValue.value().value1().getHoverName().copy());
                    case NumberValue numberValue -> PlaceholderValue.number(numberValue.value());
                    default -> PlaceholderValue.emptyText();
                };
            }
            return PlaceholderValue.emptyText();
        }

        static MutableComponent getItemLore(List<String> indices) {
            CustomTrackerDataHandler.CustomTracker tracker = CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.getOrDefault(indices.getFirst(), null);
            if(tracker != null) {
                return switch (tracker.getValue()) {
                    case ItemStackValue itemStackValue -> getLoreValue(itemStackValue.value().value1(), indices.get(1));
                    default -> Component.empty();
                };
            }
            return Component.empty();
        }

        static PlaceholderValue getItemNbt(List<String> indices) {
            CustomTrackerDataHandler.CustomTracker tracker = CustomTrackerDataHandler.instance().getCustomTrackerData().trackerList.getOrDefault(indices.getFirst(), null);
            if(tracker != null) {
                return switch (tracker.getValue()) {
                    case ItemStackValue itemStackValue -> getNbtValue(itemStackValue.value().value1(), indices.subList(1, indices.size()));
                    default -> PlaceholderValue.emptyText();
                };
            }
            return PlaceholderValue.emptyText();
        }
    }
    //endregion

    //region Functions
    static class EvaluationContext {
        /// Boolean

        static Boolean evalCondition(List<PlaceholderValue> args) {
            return !args.isEmpty() && args.getFirst().toBoolean();
        }

        static Boolean evalOr(List<PlaceholderValue> args) {
            for (PlaceholderValue arg : args) {
                if(arg.toBoolean()) return true;
            }
            return false;
        }

        static Boolean evalAnd(List<PlaceholderValue> args) {
            for (PlaceholderValue arg : args) {
                if(!arg.toBoolean()) return false;
            }
            return true;
        }

        static Boolean evalXor(List<PlaceholderValue> args) {
            boolean result = false;
            for (PlaceholderValue arg : args) {
                result ^= arg.toBoolean();
            }
            return result;
        }

        static Boolean evalNot(List<PlaceholderValue> args) {
            boolean value = !args.isEmpty() && args.getFirst().toBoolean();
            return !value;
        }

        static Boolean evalIsBlank(List<PlaceholderValue> args) {
            return args.isEmpty() || args.getFirst().toString().isBlank();
        }

        static Boolean evalContains(List<PlaceholderValue> args) {
            if(args.size() < 2) return false;
            return args.getFirst().toString().contains(args.get(1).toString());
        }

        static Boolean evalEndsWith(List<PlaceholderValue> args) {
            if(args.size() < 2) return false;
            return args.getFirst().toString().endsWith(args.get(1).toString());
        }

        static Boolean evalStartsWith(List<PlaceholderValue> args) {
            if(args.size() < 2) return false;
            return args.getFirst().toString().startsWith(args.get(1).toString());
        }

        /// Math

        static Number evalExpression(List<PlaceholderValue> args) {
            return args.isEmpty() ? 0 : args.getFirst().toDouble();
        }

        static Number evalMax(List<PlaceholderValue> args) {
            double result = Double.NEGATIVE_INFINITY;
            for (PlaceholderValue arg : args) {
                result = Math.max(result, arg.toDouble());
            }
            return args.isEmpty() ? 0 : result;
        }

        static Number evalMin(List<PlaceholderValue> args) {
            double result = Double.POSITIVE_INFINITY;
            for (PlaceholderValue arg : args) {
                result = Math.min(result, arg.toDouble());
            }
            return args.isEmpty() ? 0 : result;
        }

        static Number evalAbs(List<PlaceholderValue> args) {
            return args.isEmpty() ? 0 : Math.abs(args.getFirst().toDouble());
        }

        static Number evalCeil(List<PlaceholderValue> args) {
            return args.isEmpty() ? 0 : Math.ceil(args.getFirst().toDouble());
        }

        static Number evalFloor(List<PlaceholderValue> args) {
            return args.isEmpty() ? 0 : Math.floor(args.getFirst().toDouble());
        }

        static Number evalRound(List<PlaceholderValue> args) {
            if(args.isEmpty()) return null;

            int decimals = 0;
            if(args.size() > 1) decimals = args.get(1).toInteger();

            BigDecimal bd = new BigDecimal(args.getFirst().toDouble());
            bd = bd.setScale(decimals, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }

        static Number evalMod(List<PlaceholderValue> args) {
            if(args.size() < 2) return null;
            double a = args.getFirst().toDouble();
            double b = args.get(1).toDouble();

            return a % b;
        }

        static Number evalClamp(List<PlaceholderValue> args) {
            if(args.isEmpty()) return null;
            if(args.size() < 3) return args.getFirst().toDouble();

            double value = args.getFirst().toDouble();
            double min = args.get(1).toDouble();
            double max = args.get(2).toDouble();

            return Math.clamp(value, min, max);
        }

        static Number evalLog(List<PlaceholderValue> args) {
            if(args.isEmpty()) return null;
            return Math.log(args.getFirst().toDouble());
        }

        static Number evalPow(List<PlaceholderValue> args) {
            if(args.size() < 2) return null;
            double a = args.getFirst().toDouble();
            double b = args.get(1).toDouble();

            return Math.pow(a, b);
        }

        /// String Manipulation

        static PlaceholderValue evalSubstring(List<PlaceholderValue> args) {
            if(args.isEmpty()) return PlaceholderValue.emptyText();
            if(args.size() < 2) return args.getFirst();

            PlaceholderValue value = args.getFirst();
            int length = value.toString().length();
            int start = args.get(1).toInteger();

            int end = length;
            if(args.size() > 2) end = args.get(2).toInteger();

            if(start < 0 || end < start || end > length) return PlaceholderValue.emptyText();

            if(value.isComponent()) {
                return PlaceholderValue.component(TextHelper.substring(value.toComponent(), start, end));
            } else {
                return PlaceholderValue.text(value.toString().substring(start, end));
            }
        }

        static Number evalIndexOf(List<PlaceholderValue> args) {
            if(args.size() < 2) return -1;
            if(args.size() < 3) {
                String value = args.getFirst().toString();
                String valueToSearch = args.get(1).toString();

                return value.indexOf(valueToSearch);
            } else {
                String value = args.getFirst().toString();
                String valueToSearch = args.get(1).toString();
                int fromIndex = args.get(2).toInteger();

                return value.indexOf(valueToSearch, fromIndex);
            }
        }

        static PlaceholderValue evalRepeat(List<PlaceholderValue> args) {
            if(args.isEmpty()) return PlaceholderValue.emptyText();
            if(args.size() < 2) return args.getFirst();

            PlaceholderValue value = args.getFirst();

            int count = args.get(1).toInteger();
            if(count <= 0) return value;

            if(value.isComponent()) {
                MutableComponent repeatedComponent = Component.empty();

                for (int i = 0; i < count; i++) {
                    repeatedComponent.append(value.toComponent());
                }

                return PlaceholderValue.component(repeatedComponent);
            } else {
                return PlaceholderValue.text(value.toString().repeat(count));
            }
        }

        static PlaceholderValue evalUppercase(List<PlaceholderValue> args) {
            if(args.isEmpty()) return PlaceholderValue.emptyText();

            PlaceholderValue value = args.getFirst();

            if(value.isComponent()) {
                return PlaceholderValue.component(TextHelper.toUppercase(value.toComponent()));
            } else {
                return PlaceholderValue.text(value.toString().toUpperCase(Locale.US));
            }
        }

        static PlaceholderValue evalLowercase(List<PlaceholderValue> args) {
            if(args.isEmpty()) return PlaceholderValue.emptyText();

            PlaceholderValue value = args.getFirst();

            if(value.isComponent()) {
                return PlaceholderValue.component(TextHelper.toLowercase(value.toComponent()));
            } else {
                return PlaceholderValue.text(value.toString().toLowerCase(Locale.US));
            }
        }

        static String evalShortenNumber(List<PlaceholderValue> args) {
            if(args.isEmpty()) return "";

            Number number = args.getFirst().toDouble();

            return TextHelper.shortenNumber(number.floatValue(), 2);
        }

        static String evalRemoveFormat(List<PlaceholderValue> args) {
            if(args.isEmpty()) return "";

            return args.getFirst().toString();
        }

        static String evalFormatTime(List<PlaceholderValue> args) {
            if(args.isEmpty()) return "";

            return String.format(Locale.US, "%02d", args.getFirst().toInteger());
        }

        /// Misc

        static PlaceholderValue evalHideLine(List<PlaceholderValue> args) {
            return args.getFirst().toBoolean() ? PlaceholderValue.emptyText().markFailure() : PlaceholderValue.emptyText();
        }
    }
    //endregion

    //region JSON Schema
    public static JsonObject toJsonSchema() {
        JsonObject root = new JsonObject();

        for (Map.Entry<String, PlaceholderTreeNode> entry : ROOTS.entrySet()) {
            root.add(entry.getKey(), describeNode(entry.getValue()));
        }

        return root;
    }

    public static String toJsonSchemaString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(toJsonSchema());
    }

    private static JsonElement describeNode(PlaceholderTreeNode node) {
        boolean hasChildren = !node.getChildren().isEmpty()
                || node.getIndexChild() != null
                || node.getStringChild() != null
                || node.getStringArrayChild() != null;
        boolean hasOwnValue = node.getValueKind() != ValueKind.NONE;
        boolean hasOwnFunction = node.getEvalKind() != EvalKind.NONE;

        if(!hasChildren) {
            if(hasOwnValue && hasOwnFunction) return selfDescriptor(node);
            if(hasOwnValue) return new JsonPrimitive(valueTag(node.getValueKind()));
            if(hasOwnFunction) return new JsonPrimitive(evalTag(node.getEvalKind()));

            return new JsonPrimitive("unknown");
        }

        JsonObject object = new JsonObject();
        if(hasOwnValue && hasOwnFunction) {
            object.add("$self", selfDescriptor(node));
        } else if (hasOwnValue) {
            object.addProperty("$self", valueTag(node.getValueKind()));
        } else if (hasOwnFunction) {
            object.addProperty("$self", evalTag(node.getEvalKind()));
        }

        for (Map.Entry<String, PlaceholderTreeNode> child : node.getChildren().entrySet()) {
            object.add(child.getKey(), describeNode(child.getValue()));
        }
        if(node.getIndexChild() != null) {
            object.add("<index>", describeNode(node.getIndexChild()));
        }
        if(node.getStringChild() != null) {
            object.add("<string>", describeNode(node.getStringChild()));
        }
        if(node.getStringArrayChild() != null) {
            object.add("<string[]>", describeNode(node.getStringArrayChild()));
        }
        return object;
    }

    private static JsonObject selfDescriptor(PlaceholderTreeNode node) {
        JsonObject self = new JsonObject();
        self.addProperty("value", valueTag(node.getValueKind()));
        self.addProperty("function", evalTag(node.getEvalKind()));
        return self;
    }

    private static String valueTag(ValueKind valueKind) {
        return switch (valueKind) {
            case NONE -> "none";
            case STRING -> "string";
            case COMPONENT -> "component";
            case NUMBER -> "number";
            case BOOLEAN -> "boolean";
            case VALUE -> "dynamic";
        };
    }

    private static String evalTag(EvalKind evalKind) {
        return switch (evalKind) {
            case NONE -> "none";
            case STRING -> "function<string>";
            case COMPONENT -> "function<component>";
            case NUMBER -> "function<number>";
            case BOOLEAN -> "function<boolean>";
            case VALUE -> "function<value>";
        };
    }
    //endregion

    //region Helpers
    private static MutableComponent getLoreValue(ItemStack itemStack, String indexString) {
        Pair<Boolean, TagObject> item = ValidateItem.isServerItem(itemStack, false);
        return item.value1() ? getLoreValue(item.value2(), indexString) : Component.empty();
    }

    private static MutableComponent getLoreValue(TagObject object, String indexString) {
        try {
            int index = Integer.parseInt(indexString);
            List<Component> loreLines = object.getLore();

            if(index >= 0 && index < loreLines.size()) {
                return loreLines.get(index).copy();
            }

            return Component.empty();
        } catch (NumberFormatException e) {
            return Component.empty();
        }
    }

    public static PlaceholderValue getNbtValue(ItemStack itemStack, List<String> indices) {
        Pair<Boolean, TagObject> item = ValidateItem.isServerItem(itemStack, true);
        return item.value1() ? getNbtValue(item.value2(), indices) : PlaceholderValue.emptyText();
    }

    public static PlaceholderValue getNbtValue(TagObject object, List<String> indices) {
        if(object.contains(indices.getFirst())) {
            Tag data = object.get(indices.getFirst());
            return switch (data.getId()) {
                case 1 -> PlaceholderValue.bool(object.getBoolean(indices.getFirst()));
                case 2 -> PlaceholderValue.number(object.getShort(indices.getFirst()));
                case 3 -> PlaceholderValue.number(object.getInt(indices.getFirst()));
                case 4 -> PlaceholderValue.number(object.getLong(indices.getFirst()));
                case 5 -> PlaceholderValue.number(object.getFloat(indices.getFirst()));
                case 6 -> PlaceholderValue.number(object.getDouble(indices.getFirst()));
                case 7 -> {
                    if(indices.size() > 1) {
                        try {
                            int index = Integer.parseInt(indices.get(1));
                            yield PlaceholderValue.number(object.getByteFromArray(indices.getFirst(), index));
                        } catch (NumberFormatException e) {
                            yield PlaceholderValue.emptyText();
                        }
                    }
                    yield PlaceholderValue.emptyText();
                }
                case 8 -> PlaceholderValue.text(object.getString(indices.getFirst()));
                case 9 -> {
                    if(indices.size() > 2) {
                        try {
                            int index = Integer.parseInt(indices.get(1));
                            yield getNbtValue(TagObject.of(object.getList(indices.getFirst()).getCompound(index).orElse(new CompoundTag())),
                                    indices.subList(2, indices.size())
                            );
                        } catch (NumberFormatException e) {
                            yield PlaceholderValue.emptyText();
                        }
                    }
                    yield PlaceholderValue.emptyText();
                }
                case 10 -> getNbtValue(TagObject.of(object.getTag(indices.getFirst())), indices.subList(1, indices.size()));
                case 11 -> {
                    if(indices.size() > 1) {
                        try {
                            int index = Integer.parseInt(indices.get(1));
                            yield PlaceholderValue.number(object.getIntFromArray(indices.getFirst(), index));
                        } catch (NumberFormatException e) {
                            yield PlaceholderValue.emptyText();
                        }
                    }
                    yield PlaceholderValue.emptyText();
                }
                case 12 -> {
                    if(indices.size() > 1) {
                        try {
                            int index = Integer.parseInt(indices.get(1));
                            yield PlaceholderValue.number(object.getLongFromArray(indices.getFirst(), index));
                        } catch (NumberFormatException e) {
                            yield PlaceholderValue.emptyText();
                        }
                    }
                    yield PlaceholderValue.emptyText();
                }
                default -> PlaceholderValue.emptyText();
            };
        }
        return PlaceholderValue.emptyText();
    }
    //endregion
}
