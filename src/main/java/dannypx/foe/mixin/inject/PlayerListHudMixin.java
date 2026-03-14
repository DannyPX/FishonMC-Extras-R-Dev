package dannypx.foe.mixin.inject;

import com.llamalad7.mixinextras.sugar.Local;
import dannypx.foe.common.handler.fetch.ScoreboardHandler;
import dannypx.foe.common.handler.logic.ConnectionHandler;
import dannypx.foe.common.handler.logic.CrewHandler;
import dannypx.foe.common.handler.renderer.TabRendererHandler;
import dannypx.foe.common.handler.store.ProfileDataHandler;
import dannypx.foe.common.type.tuple.Pair;
import dannypx.foe.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract List<PlayerListEntry> collectPlayerEntries();

    @Unique
    private int indexPlayerEntry;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private void redirectRender(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.playerListHudRedirectRender.get()
                && color == this.client.options.getTextBackgroundColor(553648127)) {
            TabRendererHandler.instance().renderCrewTab(context, x1, y1, x2, y2, color, indexPlayerEntry, this.collectPlayerEntries());
        }

        context.fill(x1, y1, x2, y2, color);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private void injectRender(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci, @Local(ordinal = 13) int w) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.mixinConfig.playerListHudInjectRender.get()
        ) {
            indexPlayerEntry = w;
        }
    }

    @Inject(method = "collectPlayerEntries", at = @At("RETURN"), cancellable = true)
    private void injectCollectPlayerEntries(@NotNull CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        if(ConnectionHandler.instance().isOnServer()
                && Configs.mainConfig.enableMod.get()
                && Configs.rendererConfig.showOnlineCrewMembers.get()
                && Configs.mixinConfig.playerListHudCollectPlayerEntries.get()
                && ProfileDataHandler.instance().getProfileData().hasImportedCrew
                && !ScoreboardHandler.instance().getCrew().getString().isBlank()
        ) {
            Set<UUID> priorityUUIDs = CrewHandler.instance().getOnlineMembers().stream()
                    .map(Pair::value1)
                    .collect(Collectors.toSet());

            List<PlayerListEntry> sorted = new ArrayList<>(cir.getReturnValue());

            sorted.sort(Comparator.comparing(
                    e -> !priorityUUIDs.contains(e.getProfile().getId())
            ));

            cir.setReturnValue(sorted);
        } else {
            cir.setReturnValue(cir.getReturnValue());
        }
    }
}
