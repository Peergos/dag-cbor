package org.peergos.cbor;

import org.junit.Assert;
import org.junit.Test;

public class CborTest {

    @Test
    public void basicTypes() {
        CborObject.CborString orig = new CborObject.CborString("G'day mate!");
        byte[] raw = orig.serialize();
        Assert.assertArrayEquals(raw, HexUtil.hexToBytes("6b4727646179206d61746521"));
    }
}
