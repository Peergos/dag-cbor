# A Java dag-cbor implementation

## Usage
Add jitpack repo:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

Include with:
```xml
<dependency>
    <groupId>com.github.peergos</groupId>
    <artifactId>dag-cbor</artifactId>
    <version>0.1.2</version>
</dependency>
```

## Example usage on a custom type
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

## Java go cbrrr
There is a benchmark giving results in the hundreds of MB/s!

```shell
Decode rate 256 MB/s for canada.json.dagcbor
Decode rate 114 MB/s for citm_catalog.json.dagcbor
Decode rate 195 MB/s for twitter.json.dagcbor

Encode rate 146 MB/s for canada.json.dagcbor
Encode rate 156 MB/s for citm_catalog.json.dagcbor
Encode rate 406 MB/s for twitter.json.dagcbor
```

Benchmark data was taken from https://github.com/DavidBuchanan314/dag-cbor-benchmark