/*
 * Copyright (c) 2011-2021 by Oli B.
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
 * (c)reated 09.03.2011 by Oli B. (ob@aosd.de)
 */

package gdv.xport.satz.model;

import gdv.xport.annotation.FeldInfo;
import gdv.xport.annotation.FelderInfo;
import gdv.xport.feld.Feld;
import gdv.xport.feld.NumFeld;
import gdv.xport.io.PushbackLineNumberReader;
import gdv.xport.satz.Datensatz;
import gdv.xport.satz.Teildatensatz;
import gdv.xport.satz.feld.FeldX;
import gdv.xport.satz.feld.MetaFeldInfo;
import gdv.xport.satz.feld.common.Kopffelder1bis7;
import gdv.xport.util.SatzTyp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.PushbackReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Dies ist die gemeinsame Oberklasse aller Saetze in diesem Package, die nach
 * dem SOP-Muster aufgebaut sind. Eventuell wird diese Klasse mit der Oberklasse
 * vereinigt.
 *
 * @author oliver (ob@aosd.de)
 * @since 0.6 (09.03.2011)
 * @deprecated Enums mit Annotationen werden ab v6 nicht mehr unterstuetzt
 */
@Deprecated
public class SatzX extends Datensatz {

	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Instantiiert einen neuen Datensatz.
	 *
	 * @param satzTyp Satzart
	 * @param tdsList Teildatensaetze
	 * @since 5.0
	 */
	public SatzX(SatzTyp satzTyp, List<Teildatensatz> tdsList) {
		super(satzTyp);
		if (tdsList.get(0).hasSparte()) {
			setSparte(tdsList.get(0).getSparte());
		}
		this.completeTeildatensaetze();
	}

	/**
	 * Instantiiert einen neuen Datensatz.
	 *
	 * @param satzart z.B. 100
	 * @param felder mit allen Elementen des Datensatzes
	 * @deprecated bitte {@link SatzX#SatzX(SatzTyp, List)} verwenden
	 */
	@Deprecated
	public SatzX(final int satzart, final Enum[] felder) {
		this(SatzTyp.of(satzart), getTeildatensaetzeFor(satzart, felder));
	}

	/**
	 * Instantiiert einen neuen Datensatz.
	 *
	 * @param satzart z.B. 100
	 * @param enumClass Enumerationen-Klasse mit den Feldbeschreibungen
	 * @deprecated bitte {@link SatzX#SatzX(SatzTyp, List)} verwenden
	 */
	@Deprecated
	public SatzX(final int satzart, final Class<? extends Enum> enumClass) {
		this(SatzTyp.of(satzart), getTeildatensaetzeFor(SatzTyp.of(satzart), enumClass));
	}

	/**
	 * Instantiiert einen neuen Datensatz.
	 *
	 * @param satzart z.B. 100
	 * @param sparte Sparte
	 * @param felder mit allen Elementen des Datensatzes
	 * @deprecated bitte {@link SatzX#SatzX(SatzTyp, List)} verwenden
	 */
	@Deprecated
	public SatzX(final int satzart, final int sparte, final Enum[] felder) {
		this(SatzTyp.of(satzart), complete(getTeildatensaetzeFor(satzart, felder), sparte));
	}

	/**
	 * Instantiiert einen neuen Datensatz.
	 *
	 * @param satzart z.B. 100
	 * @param sparte Sparte
	 * @param enumClass Enumerationen-Klasse mit den Feldbeschreibungen
	 * @deprecated bitte {@link SatzX#SatzX(SatzTyp, List)} verwenden
	 */
	@Deprecated
	public SatzX(final int satzart, final int sparte, final Class<? extends Enum> enumClass) {
		this(SatzTyp.of(satzart), complete(getTeildatensaetzeFor(SatzTyp.of(satzart), enumClass), sparte));
	}

	/**
	 * Instantiiert einen allgemeinen Datensatz fuer die angegebene Satzart und
	 * Sparte. Dieser Konstruktor ist hauptsaechlich als Fallback fuer
	 * Satzarten/Sparten gedacht, die noch nicht unterstuetzt werden.
	 *
	 * @param satzart z.B. 100
	 * @param sparte Sparte
	 */
	public SatzX(final int satzart, final int sparte) {
		this(satzart, sparte, FeldX.values());
	}

	/**
	 * Instantiiert einen allgemeinen Datensatz fuer die angegebene Satznummer.
	 *
	 * @param satzNr Satznummer mit Satzart, Sparte, Wagnisart, lfd. Nummer
	 * @param enumClass Enum-Klasse, die den Datensatz beschreibt
	 * @since 0.9
	 */
  public SatzX(final SatzTyp satzNr, final Class<? extends Enum> enumClass)
  {
    this(satzNr, getTeildatensaetzeFor(satzNr, enumClass));

    this.setGdvSatzartName(satzNr.toString());
    if (satzNr.hasSparte())
      this.setSparte(satzNr.getSparteAsString());
    if (satzNr.hasGdvSatzartNummer())
      this.setGdvSatzartNummer(String.valueOf(satzNr.getGdvSatzartNummer()));
  }

  private static List<Teildatensatz> complete(List<Teildatensatz> teildatensaetze, int sparte) {
    NumFeld sparteFeld = new NumFeld(Kopffelder1bis7.SPARTE);
    sparteFeld.setInhalt(sparte);
    for (Teildatensatz tds : teildatensaetze) {
      setUpTeildatensatz(tds, sparteFeld);
    }
    return teildatensaetze;
	}

  /**
   * hierdurch werden Teildatensaetze erzeugt, die ihren GdvSatzartNamen kennen
   */
  private static List<Teildatensatz> getTeildatensaetzeFor(final SatzTyp satzNr,
      final Class<? extends Enum> enumClass)
  {
    Enum[] constants = enumClass.getEnumConstants();
    return getTeildatensaetzeFor(satzNr, constants);
  }

    /**
     * Legt das gewuenschte Feld an, das sich aus der uebergebenen Annotation
     * ergibt (Factory-Methode). Der Name wird dabei aus dem uebergebenen
     * Enum-Feld abgeleitet.
     *
     * @param feldX Enum fuer das erzeugte Feld
     * @param info die FeldInfo-Annotation mit dem gewuenschten Datentyp
     * @return das erzeugte Feld
     * @deprecated Enums werden ab v6 nicht mehr unterstuetzt
     */
    @Deprecated
    public static Feld createFeld(final Enum feldX, final FeldInfo info) {
        try {
            Constructor<? extends Feld> ctor = info.type().getConstructor(Enum.class, FeldInfo.class);
            return ctor.newInstance(feldX, info);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("no constructor " + info.type().getSimpleName()
                    + "(String, FeldInfo) found", ex);
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException("can't instantiate " + info.type(), ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("can't access ctor for " + info.type(), ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalArgumentException("error invoking ctor for " + info.type() + " ("
                    + ex.getTargetException() + ")", ex);
        }
    }

    /**
	 * Setzt die Teildatensaetze mit den angegebenen Feldern auf.
	 *
	 * @param felder Felder fuer die Teildatensaetze.
	 */
	protected void setUpTeildatensaetze(final Enum[] felder) {
		createTeildatensaetze(getTeildatensaetzeFor(SatzTyp.of(this.getSatzart()), felder));
		super.completeTeildatensaetze();
	}

	private void createTeildatensaetze(final List<? extends Teildatensatz> tdsList) {
		for (int i = 0; i < tdsList.size(); i++) {
			add(new Teildatensatz(tdsList.get(i)));
		}
	}

	/**
	 * Unterklassen (wie Datensatz) sind dafuer verantwortlich, dass auch noch
	 * die Wagnisart und die TeildatensatzNummer ueberprueft wird, ob sie noch
	 * richtig ist oder ob da schon der naechste Satz beginnt.
	 *
	 * @param reader ein Reader
	 * @param lastFeld1To7 Feld1..7 als Char-Array (42 Zeichen) der letzten Zeile oder {@code null} für ersten Teildatensatz
	 * @return true (Default-Implementierung)
	 * @throws IOException bei I/O-Fehlern
	 * @since 0.9
	 * @see gdv.xport.satz.Satz#matchesNextTeildatensatz(PushbackLineNumberReader, char[], Character)
	 */
	@Override
	protected boolean matchesNextTeildatensatz(final PushbackLineNumberReader reader, char[] lastFeld1To7, Character satznummer) throws IOException {
		if (super.matchesNextTeildatensatz(reader, lastFeld1To7, satznummer)) {
            return !(this.hasWagnisart() && this.getWagnisart().trim().length() > 0);
		}
		return false;
	}

	// /// Enum-Behandlung und Auswertung der Meta-Infos ///////////////////

	/**
	 * Hier passiert die Magie: die Annotationen der uebergebenen Enum werden
	 * ausgelesen und in eine Liste mit den Teildatensaetzen gepackt.
	 *
	 * @param satzart the satzart
	 * @param felder the felder
	 * @return eine Liste mit Teildatensaetzen
	 */
	protected static List<Teildatensatz> getTeildatensaetzeFor(final int satzart,
															   final Enum[] felder) {
		SortedMap<Integer, Teildatensatz> tdsMap = new TreeMap<>();
		List<MetaFeldInfo> metaFeldInfos = getMetaFeldInfos(felder);
		for (MetaFeldInfo metaFeldInfo : metaFeldInfos) {
			int n = metaFeldInfo.getTeildatensatzNr();
			Teildatensatz tds = tdsMap.get(n);
			if (tds == null) {
       // tds = new TeildatensatzEnum(satzart, n);
        tds = new Teildatensatz(SatzTyp.of(satzart), n);
				tdsMap.put(n, tds);
			}
			add(metaFeldInfo.getFeldEnum(), tds);

		}
		List<Teildatensatz> teildatensaetze = new ArrayList<>(tdsMap.values());
		setSparteFor(teildatensaetze, metaFeldInfos);
		return teildatensaetze;
	}

	/**
	 * Durch die Uebergabe eines SatzTyp kann der GdvSatzartName im
	 *       Teildatensatz besetzt werden. Bei den SatzXml ist alles eleganter..
	 *
	 *       Hier passiert die Magie: die Annotationen der uebergebenen Enum
	 *       werden ausgelesen und in eine Liste mit den Teildatensaetzen gepackt.
	 *
	 * @param satzTyp the satzTyp
	 * @param felder the felder
	 * @return eine Liste mit Teildatensaetzen
	 */
	protected static List<Teildatensatz> getTeildatensaetzeFor(
			final SatzTyp satzTyp, final Enum[] felder)
	{
		SortedMap<Integer, Teildatensatz> tdsMap = new TreeMap<>();
		List<MetaFeldInfo> metaFeldInfos = getMetaFeldInfos(felder);
		for (MetaFeldInfo metaFeldInfo : metaFeldInfos)
		{
			int n = metaFeldInfo.getTeildatensatzNr();
			Teildatensatz tds = tdsMap.get(n);
			if (tds == null)
			{
				tds = new Teildatensatz(satzTyp, n);
				tdsMap.put(n, tds);
			}
			add(metaFeldInfo.getFeldEnum(), tds);

		}
		List<Teildatensatz> teildatensaetze = new ArrayList<>(tdsMap.values());
		setSparteFor(teildatensaetze, metaFeldInfos);
		return teildatensaetze;
	}

	private static void setSparteFor(final List<Teildatensatz> teildatensaetze,
									   final List<MetaFeldInfo> metaFeldInfos) {
		int sparte = getSparte(metaFeldInfos);
		if (sparte > 0) {
			setSparteFor(teildatensaetze, sparte);
		}
	}

	private static int getSparte(final List<MetaFeldInfo> metaFeldInfos) {
		for (MetaFeldInfo info : metaFeldInfos) {
			if (info.hasSparte()) {
				return info.getSparte();
			}
		}
		return -1;
	}

	private static void setSparteFor(final List<Teildatensatz> teildatensaetze, final int sparte) {
		for (Teildatensatz teildatensatz : teildatensaetze) {
			setSparteFor(teildatensatz, sparte);
		}
	}

  private static void setSparteFor(final Teildatensatz tds, final int sparte)
  {
    if (!tds.hasFeld(Kopffelder1bis7.SPARTE))
    {
      Feld spartenFeld = new NumFeld(Kopffelder1bis7.SPARTE);
			tds.add(spartenFeld);
		}
    tds.getFeld(Kopffelder1bis7.SPARTE.getBezeichner())
        .setInhalt(sparte);
	}

	/**
	 * Wandelt das uebergebene Array in eine Liste mit MetaFeldInfos. Seit 0.7.1
	 * duerfen Feld-Enums wie gdv.xport.satz.feld.Feld100 auch
	 * FelderInfo-Annotationen enthalten, die wiederum auf einen Enum verweisen.
	 *
	 * @param felder the felder
	 * @return the meta feld infos
	 */
	public static List<MetaFeldInfo> getMetaFeldInfos(final Enum[] felder) {
		List<MetaFeldInfo> metaFeldInfos = new ArrayList<>(felder.length);
		for (Enum f : felder) {
			String name = f.name();
			try {
				Field field = f.getClass().getField(name);
				FelderInfo info = field.getAnnotation(FelderInfo.class);
				if (info == null) {
					metaFeldInfos.add(new MetaFeldInfo(f));
				} else {
					metaFeldInfos.addAll(getMetaFeldInfos(info));
				}
			} catch (NoSuchFieldException nsfe) {
				throw new InternalError("no field " + name + " (" + nsfe + ")");
			}
		}
		return metaFeldInfos;
	}

	private static List<MetaFeldInfo> getMetaFeldInfos(final FelderInfo info) {
		Collection<? extends Enum> enums = getAsList(info);
		List<MetaFeldInfo> metaFeldInfos = new ArrayList<>(enums.size());
		for (Enum enumX : enums) {
			metaFeldInfos.add(new MetaFeldInfo(enumX, info));
		}
		return metaFeldInfos;
	}

	private static Collection<? extends Enum> getAsList(final FelderInfo info) {
		Class<? extends Enum> enumClass = info.type();
		return getAsList(enumClass.getEnumConstants());
	}

	/**
	 * Wandelt das uebergebene Array in eine Liste mit Felder. Seit 0.7.1
	 * duerfen Feld-Enums wie gdv.xport.satz.feld.Feld100 auch
	 * FelderInfo-Annotationen enthalten, die wiederum auf einen Enum verweisen.
	 *
	 * @param felder the felder
	 * @return the feld info list
	 */
	private static List<Enum> getAsList(final Enum[] felder) {
		List<Enum> feldList = new ArrayList<>(felder.length);
		for (Enum f : felder) {
			String name = f.name();
			try {
				Field field = f.getClass().getField(name);
				FelderInfo info = field.getAnnotation(FelderInfo.class);
				if (info == null) {
					feldList.add(f);
				} else {
					feldList.addAll(getAsList(info));
				}
			} catch (NoSuchFieldException nsfe) {
				throw new InternalError("no field " + name + " (" + nsfe + ")");
			}
		}
		return feldList;
	}

	/**
	 * Leitet aus dem uebergebenen Feldelement die Parameter ab, um daraus ein
	 * Feld anzulegen und im jeweiligen Teildatensatz einzuhaengen. Zusaetzlich
	 * wird das Feld "Satznummer" vorbelegt, falls es in den uebergebenen
	 * Feldern vorhanden ist.
	 *
	 * @param feldX das Feld-Element
	 * @param tds der entsprechende Teildatensatz
	 */
	private static void add(final Enum feldX, final Teildatensatz tds) {
		FeldInfo info = MetaFeldInfo.getFeldInfo(feldX);
		Feld feld = createFeld(feldX, info);
		if (info.nr() < 7) {
			LOG.debug("using default settings for " + feld);
		} else {
			tds.add(feld);
			if (isSatznummer(feldX)) {
				feld.setInhalt(info.teildatensatz());
			}
		}
	}

	private static boolean isSatznummer(final Enum feldX) {return (feldX.name().length() <= 11) && feldX.name().startsWith("SATZNUMMER");
	}

	@Override
	public SatzX importFrom(final String s) throws IOException {
        int satzlength = getSatzlength(s);
        SortedSet<Integer> importedTeilsatzIndexes = new TreeSet<>();
		List<Teildatensatz> teildatensaetze = getTeildatensaetze();
		for (int i = 0; i < teildatensaetze.size(); i++) {
            String input = s.substring(i * satzlength);
            if (input.trim().isEmpty()) {
                LOG.info("mehr Daten fuer Satz " + this.getSatzart() + " erwartet, aber nur " + i
                        + " Teildatensaetze vorgefunden");
                removeUnusedTeildatensaetze(importedTeilsatzIndexes);
                break;
            }
            int satznummer = readSatznummer(input.toCharArray()) - '0';
            int teildatensatzIndex = getTeildatensatzIndex(i, satznummer);
            teildatensaetze.get(teildatensatzIndex).importFrom(input);
            importedTeilsatzIndexes.add(teildatensatzIndex);
        }
		return this;
	}

	protected int getTeildatensatzIndex(int index, int satznummer) {
		if (satznummer < 1) {
			return index;
		}
		List<Teildatensatz> teildatensaetze = getTeildatensaetze();
		for (int i = 0; i < teildatensaetze.size(); i++) {
			if (teildatensaetze.get(i).getSatznummer().toInt() == satznummer) {
				return i;
			}
		}
		return index;
	}

	/**
	 * Liest die Satznummer.
	 *
	 * @param reader den Reader
	 * @return Teildatensatz-Nummer
	 * @throws IOException bei Lesefehler
	 */
	public static Character readSatznummer(final PushbackReader reader) throws IOException {
		char[] cbuf = new char[256];
		if (reader.read(cbuf) == -1) {
			throw new EOFException("can't read 1 bytes (" + new String(cbuf) + ") from " + reader);
		}
		reader.unread(cbuf);
		return readSatznummer(cbuf);
	}

	/**
	 * Liest die Satznummer.
	 *
	 * @param cbuf der eingelesene Satz in char array
	 * @return Teildatensatz -Nummer
	 */
	public static char readSatznummer(char[] cbuf) {
		if (cbuf.length < 256) {
			return 0;
		}
		String satz = new String(cbuf);
		String satzartString = satz.substring(0, 4)
								   .trim();
		int satzart = isNumber(satzartString) ? Integer.parseInt(satzartString)
				: -1;
		String sparteString = satz.substring(10, 13)
								  .trim();
		int sparte = isNumber(sparteString) ? Integer.parseInt(sparteString) : -1;

		int satznummerIndex = 255;
		switch (satzart) {
			case 210:
				satznummerIndex = getSatznummerIndexOfSatz210(sparte);
				break;
			case 211:
				switch (sparte) {
					case 0:
					case 80:
					case 170:
					case 190:
						satznummerIndex = 42;
						break;
					default:
						break;
				}
				break;
			case 220:
			case 221:
				satznummerIndex = getSatznummerIndexOf(satz, sparte);
				break;
			case 250:
			case 251:
				satznummerIndex = 50;
				break;
			case 270:
			case 280:
			case 291:
			case 292:
			case 293:
			case 294:
			case 295:
				satznummerIndex = 42;
				break;
			case 410:
			case 420:
			case 450:
				satznummerIndex = 50;
				break;
			case 550:
				satznummerIndex = 65;
				break;
			default:
				break;
		}
		return satz.charAt(satznummerIndex);
	}

	private static int getSatznummerIndexOfSatz210(int sparte) {
		switch (sparte) {
			case 0:
			case 80:
			case 170:
			case 190:
			case 550:
			case 560:
			case 570:
			case 580:
				return 42;
			case 130:
				return 250;
			default:
				return 255;
		}
	}

	private static int getSatznummerIndexOf(String satz, int sparte) {
		switch (sparte) {
			case 0:
				return 46;
			case 30:
				if ((satz.charAt(48) == '2' && satz.charAt(255) == 'X') || (satz.charAt(
						48) == '1' || satz.charAt(48) == '4')) {
					return 48;
				} else if (Character.isDigit(satz.charAt(255)) && satz.charAt(255) != '0'
						&& satz.charAt(255) != '2') {
					return 249;
				} else if (satz.charAt(42) == '3') {
					return 42;
				} else {
					return 59;
				}
			case 40:
			case 140:
				return 50;
			case 70:
				return 52;
			case 80:
			case 190:
				return 48;
			case 170:
				return 49;
			case 550:
			case 560:
			case 570:
			case 580:
				return 42;
			default:
				return 255;
		}
	}

	public static boolean isNumber(String string) {
		return string.matches("-?\\d+");
	}

}
