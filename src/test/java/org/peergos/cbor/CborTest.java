package org.peergos.cbor;

import io.ipfs.cid.Cid;
import io.ipfs.multihash.Multihash;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.MalformedInputException;
import java.time.ZoneOffset;
import java.util.SortedMap;
import java.util.TreeMap;

public class CborTest {

    @Test
    public void basicTypes() {
        CborObject.CborString orig = new CborObject.CborString("G'day mate!");
        byte[] raw = orig.serialize();
        Assert.assertArrayEquals(raw, HexUtil.hexToBytes("6b4727646179206d61746521"));
    }

    @Test
    public void customType() {
        CustomType orig = new CustomType("G'day!", 12345678910L, new Cid(1, Cid.Codec.DagCbor, Multihash.Type.sha2_256, new byte[32]));
        byte[] raw = orig.serialize();
        CustomType deserialized = CustomType.fromCbor(CborObject.fromByteArray(raw));
        Assert.assertEquals(deserialized, orig);
        byte[] roundTripped = deserialized.serialize();
        Assert.assertArrayEquals(roundTripped, raw);
    }

    @Test
    public void b3cid() {
        byte[] raw = HexUtil.hexToBytes("d82a58250001551e208e4c7c1b99dbfd50e7a95185fead5ee1448fa904a2fdd778eaf5f2dbfd629a99");
        CborObject cbor = CborObject.fromByteArray(raw);
        byte[] roundTripped = cbor.serialize();
        Assert.assertArrayEquals(roundTripped, raw);
    }

    @Test
    public void duplicateMapKeys() {
        byte[] raw = HexUtil.hexToBytes("a2616100616101");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void repeatedObject() {
        byte[] raw = HexUtil.hexToBytes("0000");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void invalidUtf8() {
        byte[] raw = HexUtil.hexToBytes("62c328");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (RuntimeException e) {
            if (!(e.getCause() instanceof MalformedInputException))
                throw new RuntimeException("Fail!");
        }
    }

    @Test
    public void undefinedMapLength() {
        byte[] raw = HexUtil.hexToBytes("bfff");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthByte() {
        byte[] raw = HexUtil.hexToBytes("1801");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthNegativeByte() {
        byte[] raw = HexUtil.hexToBytes("3800");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthShort() {
        byte[] raw = HexUtil.hexToBytes("190001");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthNegativeShort() {
        byte[] raw = HexUtil.hexToBytes("390000");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthInt() {
        byte[] raw = HexUtil.hexToBytes("1a00000001");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthNegativeInt() {
        byte[] raw = HexUtil.hexToBytes("3a00000000");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthLong() {
        byte[] raw = HexUtil.hexToBytes("1b0000000000000001");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthNegativeLong() {
        byte[] raw = HexUtil.hexToBytes("3b0000000000000000");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthByteString() {
        byte[] raw = HexUtil.hexToBytes("5800");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthString() {
        byte[] raw = HexUtil.hexToBytes("7800");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

    @Test
    public void unneededExtraLengthArray() {
        byte[] raw = HexUtil.hexToBytes("9800");
        try {
            CborObject.fromByteArray(raw);
            throw new RuntimeException("Should fail!");
        } catch (IllegalStateException e) {}
    }

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
}
