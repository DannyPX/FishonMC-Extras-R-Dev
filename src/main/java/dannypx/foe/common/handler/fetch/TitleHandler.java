package dannypx.foe.common.handler.fetch;

import dannypx.foe.common.handler.Handler;
import dannypx.foe.common.handler.logic.CatchingHandler;
import dannypx.foe.common.type.Pair;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Map;

public class TitleHandler extends Handler {
    private static TitleHandler INSTANCE = new TitleHandler();

    public static TitleHandler instance() {
        if (INSTANCE == null) {
            INSTANCE = new TitleHandler();
        }
        return INSTANCE;
    }

    //region Fields
    private MutableText title = Text.empty();
    private MutableText subTitle = Text.empty();

    public MutableText getTitle() {
        return title;
    }

    public MutableText getSubTitle() {
        return subTitle;
    }
    //endregion

    //region Methods
    public void setTitle(Text title) {
        this.title = title.copy();
        this.forwardTitleEvent(title);
    }

    public void setSubTitle(Text subTitle) {
        this.subTitle = subTitle.copy();
        this.forwardSubTitleEvent(subTitle);
    }

    private void forwardTitleEvent(Text title) {
        CatchingHandler.instance().scanFishListener();
    }

    private void forwardSubTitleEvent(Text subTitle) {
        CatchingHandler.instance().scanFishNameListener();
    }
    //endregion

    //region Dev
    /// Field, Pair<Value, Tooltip>
    protected Map<String, Pair<MutableText, MutableText>> _getFields() {
        return Map.of(
                "title", Pair.of(getTitle(), Text.empty()),
                "subTitle", Pair.of(getSubTitle(), Text.empty())
        );
    }
    //endregion
}
