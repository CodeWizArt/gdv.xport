/*
 * Copyright 2021 Optica Abrechnungszentrum Dr. Gueldener GmbH
 */
package gdv.xport.satz.rule;

import gdv.xport.io.PushbackLineNumberReader;
import gdv.xport.util.SatzRegistry;
import gdv.xport.util.SatzTyp;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testRule() {
        // define facts
        Facts facts = new Facts();
        facts.put("satzart", "1");

        // define rules
        Rule vorsatzRule = new RuleBuilder()
                .name("Vorsatz-Regel")
                .description("wenn Satzart 1 ist, ist es ein Vorsatz")
                .when(f -> f.get("satzart").equals("1"))
                .then(f -> System.out.println("Juhu - es ist ein Vorsatz"))
                .build();
        Rule simpleRule = new RuleBuilder()
                .name("Einfach-Regel")
                .description("Ermittlung einer Satzart ohne Sparte")
                .when(f -> isSatzart(f))
                .then(f -> System.out.println("isch ne einfache Satzart"))
                .build();
        Rules rules = new Rules();
        rules.register(vorsatzRule);
        rules.register(simpleRule);

        // fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);
    }

    private boolean isSatzart(Facts f) {
        return f.get("satzart").equals("1");
    }

}