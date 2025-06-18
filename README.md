# dag-cbor
A Java dag-cbor implementation

Example usage on a custom type:
```java
public record CustomType(String name, long time, Multihash ref) implements Cborable {

    @Override
    public CborObject toCbor() {
        SortedMap<String, Cborable> state = new TreeMap<>();
        state.put("n", new CborObject.CborString(name));
        state.put("t", new CborObject.CborLong(time));
        state.put("r", new CborObject.CborMerkleLink(ref));
        return CborObject.CborMap.build(state);
    }

    public static CustomType fromCbor(Cborable cbor) {
        if (! (cbor instanceof CborObject.CborMap))
            throw new IllegalStateException("Invalid cbor for CustomType! " + cbor);
        CborObject.CborMap m = (CborObject.CborMap) cbor;
        return new CustomType(m.getString("n"), m.getLong("t"), m.getMerkleLink("r"));
    }
}

CustomType example = new CustomType("Hey", 12345L, new Cid(1, Cid.Codec.DagCbor, Multihash.Type.sha2_256, new byte[32]));
byte[] raw = example.serialize();
CustomType deserialized = CustomType.fromCbor(CborObject.fromByteArray(raw));
```
