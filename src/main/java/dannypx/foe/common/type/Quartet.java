package dannypx.foe.common.type;

public record Quartet<V1, V2, V3, V4>(V1 v1, V2 v2, V3 v3, V4 v4) {
    public static <V1, V2, V3, V4> Quartet<V1, V2, V3, V4> of(V1 v1, V2 v2, V3 v3, V4 v4) {
        return new Quartet<>(v1, v2, v3, v4);
    }
}
