package util;

import simplenlgde.features.DiscourseFunction;
import simplenlgde.features.InternalFeature;
import simplenlgde.framework.NLGFactory;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.phrasespec.NPPhraseSpec;
import simplenlgde.realiser.Realiser;

/**
 * Utility functions to create the plural of a given noun.
 *
 * @see <a href="https://github.com/sebischair/SimpleNLG-DE">SimpleNLG-DE</a>
 *
 * @author Laura Schmidt
 * @version 1.0 Initial implementation
 */
public class Plurals {

    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    private static final String[][] UMLAUT_REPLACEMENTS = {{"Ä", "Ae"}, {"Ü", "Ue"}, {"Ö", "Oe"}, {"ä", "ae"},
            {"ü", "ue"}, {"ö", "oe"}, {"ß", "ss"}};

    /**
     * Get plural of a given noun.
     *
     * @param noun noun
     * @return plural of noun
     */
    public static String getPlural(String noun) {
        NPPhraseSpec pluralSentence = nlgFactory.createNounPhrase(noun);
        pluralSentence.setPlural(true);
        pluralSentence.setFeature(InternalFeature.CASE, DiscourseFunction.SUBJECT);

        String pluralNoun = realiser.realise(pluralSentence).toString();

        return removeUmlauts(pluralNoun);
    }

    /**
     * Remove umlauts because plurals are used for names of variables.
     *
     * @param word word to remove umlauts from
     * @return word without umlauts
     */
    private static String removeUmlauts(String word) {
        for (String[] umlautReplacement : UMLAUT_REPLACEMENTS) {
            word = word.replace(umlautReplacement[0], umlautReplacement[1]);
        }
        return word;
    }
}
