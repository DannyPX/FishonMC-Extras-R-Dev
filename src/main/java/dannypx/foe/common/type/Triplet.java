package dannypx.foe.common.type;

public record Triplet<V1, V2, V3>(V1 v1, V2 v2, V3 v3) {
    public static <V1, V2, V3> Triplet<V1, V2, V3> of(V1 v1, V2 v2, V3 v3) {
        return new Triplet<>(v1, v2, v3);
    }
}
