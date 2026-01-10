package dannypx.foe.common.type;

public record Triplet<V1, V2, V3>(V1 v1, V2 v2, V3 v3) {
    public static <K, V1, V2> Triplet<K, V1, V2> of(K k, V1 v1, V2 v2) {
        return new Triplet<>(k, v1, v2);
    }
}
