package dannypx.foe.common.handler.io;

import java.util.UUID;

public class DataModels {
    public static final String PROFILE_DATA_MODEL_VERSION = "0";

    public static abstract class DataModel {
        String version;
        public UUID uuid;

        DataModel(String version, UUID uuid) {
            this.version = version;
            this.uuid = uuid;
        }
    }

    public static class ProfileDataModel extends DataModel {
        public ProfileDataModel() {
            super(PROFILE_DATA_MODEL_VERSION, null);
        }
    }

    public enum DataModelType {
        PROFILE_DATA("profile");

        public final String FILENAME;

        DataModelType(String fileName) {
            FILENAME = fileName;
        }
    }
}
