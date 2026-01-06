package dannypx.foe.common.type;

public class Triplet<V1, V2, V3> {
    public final V1 v1;
    public final V2 v2;
    public final V3 v3;

    public Triplet(V1 v1, V2 v2, V3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public static <K, V1, V2> Triplet<K, V1, V2> of(K k, V1 v1, V2 v2) {
        return new Triplet<>(k, v1, v2);
    }
}
