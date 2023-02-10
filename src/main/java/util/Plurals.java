package util;

import simplenlgde.features.DiscourseFunction;
import simplenlgde.features.InternalFeature;
import simplenlgde.framework.NLGFactory;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.phrasespec.NPPhraseSpec;
import simplenlgde.realiser.Realiser;

public class Plurals {

    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    private static final String[][] UMLAUT_REPLACEMENTS = {{"Ä", "Ae"}, {"Ü", "Ue"}, {"Ö", "Oe"}, {"ä", "ae"},
            {"ü", "ue"}, {"ö", "oe"}, {"ß", "ss"}};

    public static String getPlural(String noun) {
        NPPhraseSpec pluralSentence = nlgFactory.createNounPhrase(noun);
        pluralSentence.setPlural(true);
        pluralSentence.setFeature(InternalFeature.CASE, DiscourseFunction.SUBJECT);

        String pluralNoun = realiser.realise(pluralSentence).toString();

        return removeUmlauts(pluralNoun);
    }

    private static String removeUmlauts(String word) {
        for (String[] umlautReplacement : UMLAUT_REPLACEMENTS) {
            word = word.replace(umlautReplacement[0], umlautReplacement[1]);
        }
        return word;
    }
}
