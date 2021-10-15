/*
 * Copyright 2021 Optica Abrechnungszentrum Dr. Gueldener GmbH
 */
package gdv.xport.satz.rule;

import gdv.xport.io.ImportException;
import gdv.xport.util.SatzTyp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;

import java.io.IOException;
import java.io.PushbackReader;

/**
 * Die Klasse ImportRule ...
 *
 * @author oboehm
 * @since 5.2 (17.09.21)
 */
public class ImportRule {

    private static final Logger LOG = LogManager.getLogger();
    private final PushbackReader reader;
    private final Rules rules = new Rules();
    private final Facts facts = new Facts();

    public static void main(String[] args) {
        // define facts
        Facts facts = new Facts();
        facts.put("rain", true);

        // define rules
        Rule weatherRule = defineRule();
        Rules rules = new Rules();
        rules.register(weatherRule);

        // fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);
    }

    private static Rule defineRule() {
        Rule weatherRule = new RuleBuilder()
                .name("weather rule")
                .description("if it rains then take an umbrella")
                .when(facts -> facts.get("rain").equals(true))
                .then(facts -> System.out.println("It rains, take an umbrella!"))
                .build();
        return weatherRule;
    }

    public ImportRule(PushbackReader reader) {
        this.reader = reader;
    }

    public static ImportRule of(PushbackReader reader) {
        ImportRule rule = new ImportRule(reader);
        rule.defineRules();
        return rule;
    }

    private void defineRules() {
        rules.register(ruleBausparen());
    }

    private void fireRules() {
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);
    }

    public SatzTyp getSatzTyp() {
        facts.put("sparte", "580");
        fireRules();
        throw new UnsupportedOperationException("not yet implemented");
    }

    private Rule ruleBausparen() {
        return new RuleBuilder()
                .name("0220.580")
                .description("Bausparen")
                .when(facts -> facts.get("sparte").equals("580"))
                //.when(facts -> (isSatzart(220) && facts.get("sparte").equals("580")))
                //.when(facts -> true)
                .then(facts -> System.out.println("0220.0580.x\n"))
                .build();
    }

    private boolean isSatzart(int satzart) {
        try {
            int x = readInt(0, 4);
            return x == satzart;
        } catch (IOException ex) {
            LOG.warn("Kann Satzart nicht bestimmen:", ex);
            return false;
        }
    }

    private int readInt(int from, int length) throws IOException {
        char[] cbuf = new char[length];
        if (reader.read(cbuf) == -1) {
            throw new IOException("can't read 14 bytes (" + new String(cbuf) + ") from " + reader);
        }
        reader.unread(cbuf);
        String s = new String(cbuf);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            throw new ImportException(String.format("cannot read %d digits from byte %d (%s)", length, from, s));
        }
    }

}
