package dannypx.foe.handler.fetch;

import dannypx.foe.handler.Handler;
import dannypx.foe.handler.logic.CatchingHandler;
import dannypx.foe.handler.logic.PlaceholderHandler;
import dannypx.foe.type.tuple.Pair;
import dannypx.foe.type.custom_text.PlaceholderValue;
import dannypx.foe.type.custom_text.ComponentValue;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TitleHandler extends Handler {
    private static TitleHandler INSTANCE = new TitleHandler();

    public static TitleHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new TitleHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private MutableComponent title = Component.empty();
    private MutableComponent subTitle = Component.empty();

    public MutableComponent getTitle() {
        return title;
    }

    public MutableComponent getSubTitle() {
        return subTitle;
    }

    public Pair<Boolean, PlaceholderValue> getTitle(String[] params) {
        if(params.length > 0) {
            Pattern fieldPattern = Pattern.compile("^(title|sub_title)$");

            if(fieldPattern.matcher(params[0]).matches()
                    && params.length == 1
            ) {
                return switch(params[0]) {
                    case "title" -> PlaceholderHandler.getPlaceholderValue(new ComponentValue(getTitle()));
                    case "sub_title" -> PlaceholderHandler.getPlaceholderValue(new ComponentValue(getSubTitle()));
                    default -> PlaceholderHandler.noResult();
                };
            }
        }
        return PlaceholderHandler.noResult();
    }
    //endregion

    //region Methods
    public void setTitle(Component title) {
        this.title = title.copy();
        this.forwardTitleEvent(title);
    }

    public void setSubTitle(Component subTitle) {
        this.subTitle = subTitle.copy();
        this.forwardSubTitleEvent(subTitle);
    }

    private void forwardTitleEvent(Component title) {
        CatchingHandler.instance().scanFishListener();
    }

    private void forwardSubTitleEvent(Component subTitle) {
        CatchingHandler.instance().scanFishNameListener();
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableComponent, MutableComponent>> _getFields() {
        return Map.of(
                "title", Pair.of(getTitle(), Component.empty()),
                "subTitle", Pair.of(getSubTitle(), Component.empty())
        );
    }
    //endregion
}
