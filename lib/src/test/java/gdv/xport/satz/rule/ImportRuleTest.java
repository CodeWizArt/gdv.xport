/*
 * Copyright 2021 Optica Abrechnungszentrum Dr. Gueldener GmbH
 */
package gdv.xport.satz.rule;

import gdv.xport.io.PushbackLineNumberReader;
import gdv.xport.util.SatzRegistry;
import gdv.xport.util.SatzTyp;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Unit-Test fuer ImportRule.
 *
 * @author oboehm
 * @since 17.09.21
 */
public class ImportRuleTest {

    @Test
    public void testGetSatzTypBausparen() throws IOException {
        SatzTyp bausparen = SatzTyp.of("0220.580.01");
        String input = SatzRegistry.getInstance().getSatz(bausparen).toLongString();
        try (StringReader stringReader = new StringReader(input);
             PushbackLineNumberReader reader = new PushbackLineNumberReader(stringReader)) {
            ImportRule rule = new ImportRule(reader);
            SatzTyp satzTyp = rule.getSatzTyp();
            assertEquals(bausparen, satzTyp);
        }
    }

}