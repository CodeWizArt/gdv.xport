/*
 * Copyright (c) 2011, 2012 by Oli B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 23.03.2011 by Oli B. (ob@aosd.de)
 */

package gdv.xport.satz.feld.sparte10.wagnisart6;

import gdv.xport.annotation.FeldInfo;
import gdv.xport.annotation.FelderInfo;
import gdv.xport.feld.AlphaNumFeld;
import gdv.xport.feld.Betrag;
import gdv.xport.feld.Zeichen;
import gdv.xport.satz.feld.common.Feld1bis7;

/**
 * Diese Enum-Klasse repraesentiert die Felder fuer Satzart 221, Sparte 10
 * "Leben - Unfall = Wagnisart 6 - Zukünftige Summenänderung" (Satzart 0221).
 *
 * @author ralfklemmer
 * @since 19.01.2013
 * @deprecated Enums mit Annotationen werden ab v6 nicht mehr unterstuetzt
 */
@Deprecated
public enum Feld221Wagnis6 {

 // /// Teildatensatz 1 /////////////////////////////////////////////////

    /** Feld 1 - 7 sind fuer jeden (Teil-)Datensatz identisch. */
    @FelderInfo(sparte = 10, teildatensatz = 1, type = Feld1bis7.class)
    INTRO1,

    /**
     * Laufende Nummer der versicherten Person (VP).
     * lfd. Nr., die im VU geführt wird
     */
    @FeldInfo(teildatensatz = 1, nr = 8, type = AlphaNumFeld.class, anzahlBytes = 17, byteAdresse = 43)
    LFD_NUMMER_VP_PERSONENGRUPPE,

  /**
   * Wagnisart. 6 = Unfallzusatzversicherung
   */
  @FeldInfo(teildatensatz = 1, nr = 9, type = Zeichen.class, anzahlBytes = 1, byteAdresse = 60, value = "6")
  WAGNISART,

    /**
     * Lfd Nummer zur Wagnisart.
     */
    @FeldInfo(teildatensatz = 1, nr = 10, type = Zeichen.class, anzahlBytes = 1, byteAdresse = 61)
    LFD_NUMMER_ZUR_WAGNISART,

    /**
     * Unfallsumme in Währungseinheiten.
     * (12,2 Stellen)
     */
    @FeldInfo(teildatensatz = 1, nr = 12, type = Betrag.class, anzahlBytes = 14, byteAdresse = 62)
    UNFALLSUMME_IN_WAEHRUNGSEINHEITEN,

    /**
     * Leerstellen.
     */
    @FeldInfo(teildatensatz = 1, nr = 13, type = AlphaNumFeld.class, anzahlBytes = 180, byteAdresse = 76)
    LEERSTELLEN,

    // /// Teildatensatz 2 /////////////////////////////////////////////////

    /** Feld 1 - 7 sind fuer jeden (Teil-)Datensatz identisch. */
    @FelderInfo(sparte = 10, teildatensatz = 2, type = Feld1bis7.class)
    INTRO2,

    /**
     * Laufende Nummer der versicherten Person (VP).
     * lfd. Nr., die im VU geführt wird
     */
    @FeldInfo(teildatensatz = 2, nr = 8, type = AlphaNumFeld.class, anzahlBytes = 17, byteAdresse = 43)
    LFD_NUMMER_VP_PERSONENGRUPPE2,

    /**
     * Wagnisart.
     * 6 = Unfallzusatzversicherung
     */
    @FeldInfo(teildatensatz = 2, nr = 9, type = Zeichen.class, anzahlBytes = 1, byteAdresse = 60)
    WAGNISART2,

    /**
     * Lfd Nummer zur Wagnisart.
     */
    @FeldInfo(teildatensatz = 2, nr = 10, type = Zeichen.class, anzahlBytes = 1, byteAdresse = 61)
    LFD_NUMMER_ZUR_WAGNISART2,

    /**
     * Absoluter Dynamikerhöhungsbetrag in Währungseinheiten in Währungseinheiten.
     * (10,2 Stellen)
     */
    @FeldInfo(teildatensatz = 2, nr = 11, type = Betrag.class, anzahlBytes = 12, byteAdresse = 62)
    ABSOLUTER_DYNAMIKERHOEHUNGSBETRAG_IN_WAEHRUNGSEINHEITEN,

    /**
     * Leerstellen.
     */
    @FeldInfo(teildatensatz = 2, nr = 12, type = AlphaNumFeld.class, anzahlBytes = 182, byteAdresse = 74)
    LEERSTELLEN2,

}
