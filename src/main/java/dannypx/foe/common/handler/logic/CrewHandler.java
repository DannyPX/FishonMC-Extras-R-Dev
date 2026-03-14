package dannypx.foe.common.handler.logic;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.fetch.ScoreboardHandler;
import dannypx.foe.common.handler.store.CrewDataHandler;
import dannypx.foe.common.type.custom_text.CustomTextValue;
import dannypx.foe.common.type.custom_text.StringValue;
import dannypx.foe.common.type.tuple.Pair;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class CrewHandler extends Handler {
    private static CrewHandler INSTANCE = new CrewHandler();

    public static CrewHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new CrewHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private List<UUID> pendingLeavesList = new ArrayList<>();

    List<Pair<UUID, String>> crewListOrdered = new ArrayList<>();
    List<Pair<UUID, String>> onlineMembers = new ArrayList<>();
    List<Pair<UUID, String>> offlineMembers = new ArrayList<>();

    public List<Pair<UUID, String>> getCrewListOrdered() {
        return crewListOrdered;
    }

    public List<Pair<UUID, String>> getOnlineMembers() {
        return onlineMembers;
    }

    public List<Pair<UUID, String>> getOfflineMembers() {
        return offlineMembers;
    }

    public Pair<Boolean, CustomTextValue> getCrew(String[] params) {
        if(params.length > 0) {
            Pattern crewListPattern = Pattern.compile("^(online|offline)$");
            Pattern intPattern = Pattern.compile("^-?\\d+$");
            Pattern crewPattern = Pattern.compile("^(id|name)$");

            if(crewListPattern.matcher(params[0]).matches()) {
                List<Pair<UUID, String>> list = switch (params[0]) {
                    case "online" -> onlineMembers;
                    case "offline" -> offlineMembers;
                    default -> new ArrayList<>();
                };

                if(params.length == 3
                        && intPattern.matcher(params[1]).matches()
                        && crewPattern.matcher(params[2]).matches()
                ) {
                    int index = Integer.parseInt(params[1]);
                    if(list.size() > index) {
                        Pair<UUID, String> crew = list.get(index);
                        return switch (params[2]) {
                            case "id" -> PlaceholderHandler.getTextValue(new StringValue(String.valueOf(crew.value1())));
                            case "name" -> PlaceholderHandler.getTextValue(new StringValue(crew.value2()));
                            default -> PlaceholderHandler.noResult();
                        };
                    }
                }
            }
        }
        return PlaceholderHandler.noResult();
    }

    //endregion

    //region Methods
    public void tick() {
        if(!ScoreboardHandler.instance().getCrew().getString().isBlank()) {
            if(crewListOrdered.isEmpty()) this.updateCrewOrderedList(CrewDataHandler.instance().getCrewData().crewList);
        }
    }

    public void updateCrewOrderedList(Map<UUID, Pair<String, ItemStack>> crewMap) {
        this.crewListOrdered.clear();
        crewMap.forEach(((uuid, s) -> this.crewListOrdered.add(Pair.of(uuid, s.value1()))));


        this.fetchCrewMemberStatus();
    }

    private void fetchCrewMemberStatus() {
        onlineMembers.clear();
        offlineMembers.clear();
        crewListOrdered.forEach(crew -> {
            PlayerListEntry playerListEntry = minecraftClient.getNetworkHandler().getPlayerListEntry(crew.value1());
            if(playerListEntry != null) {
                onlineMembers.add(crew);
            } else {
                offlineMembers.add(crew);
            }
        });
    }

    public void updatePlayerToOffline(UUID id) {
        AtomicReference<Pair<UUID, String>> updatedMember = new AtomicReference<>();

        onlineMembers.removeIf(crew -> {
            if(crew.value1().equals(id)) {
                updatedMember.set(crew);
                return true;
            }
            return false;
        });

        if(updatedMember.get() != null) {
            Pair<String, ItemStack> crewMember = CrewDataHandler.instance().getCrewData().crewList.get(updatedMember.get().value1());

            LoggerHandler._debug("player " + crewMember.value1() + " left");

            offlineMembers.add(updatedMember.get());
            NotifierHandler.instance().notifyPlayerStatus(false, crewMember);
        }
    }

    public void updatePlayerToOnline(UUID id) {
        AtomicReference<Pair<UUID, String>> updatedMember = new AtomicReference<>();

        offlineMembers.removeIf(crew -> {
            if(crew.value1().equals(id)) {
                updatedMember.set(crew);
                return true;
            }
            return false;
        });

        if(updatedMember.get() != null) {
            Pair<String, ItemStack> crewMember = CrewDataHandler.instance().getCrewData().crewList.get(updatedMember.get().value1());

            LoggerHandler._debug("player " + crewMember.value1() + " joined");

            onlineMembers.add(updatedMember.get());
            NotifierHandler.instance().notifyPlayerStatus(true, crewMember);
        }
    }

    public void onPlayerJoin(UUID uuid) {
        if(LoadingHandler.instance().isLoadingDone()
                && !ScoreboardHandler.instance().getCrew().getString().isBlank()
                && CrewDataHandler.instance().getCrewData().crewList.containsKey(uuid)
        ) {

            if(onlineMembers.stream().noneMatch(m -> m.value1().equals(uuid))) {
                pendingLeavesList.remove(uuid);

                updatePlayerToOnline(uuid);
            }
        }
    }

    public void onPlayerLeave(UUID uuid) {
        if(LoadingHandler.instance().isLoadingDone()
                && !ScoreboardHandler.instance().getCrew().getString().isBlank()
                && CrewDataHandler.instance().getCrewData().crewList.containsKey(uuid)
        ) {

            this.pendingLeavesList.add(uuid);

            CodeExecuterHandler.runLater(10, () -> {
                if(this.pendingLeavesList.contains(uuid)) {
                    this.pendingLeavesList.remove(uuid);
                    updatePlayerToOffline(uuid);
                }
            });
        }
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    @Override
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "key", Pair.of(Text.literal("value"), Text.empty())
        );
    }
    //endregion
}
