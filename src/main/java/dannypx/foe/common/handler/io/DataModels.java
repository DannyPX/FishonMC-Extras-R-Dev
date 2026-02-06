package dannypx.foe.common.handler.io;

import java.util.UUID;

public class DataModels {

    public static abstract class DataModel {
        public final String version;
        public UUID uuid;

        protected DataModel(String version, UUID uuid) {
            this.version = version;
            this.uuid = uuid;
        }
    }

    public enum DataModelType {
        PROFILE_DATA("profile"),
        STATS_DATA("stats"),
        CONSTANT_DATA("constant");

        public final String FILENAME;

        DataModelType(String fileName) {
            FILENAME = fileName;
        }
    }
}
