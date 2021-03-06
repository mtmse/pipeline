package org.daisy.pipeline.nlp.lexing.omni.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.daisy.pipeline.nlp.lexing.LexResultPrettyPrinter;
import org.daisy.pipeline.nlp.lexing.LexService;
import org.daisy.pipeline.nlp.lexing.LexService.LexerInitException;
import org.daisy.pipeline.nlp.lexing.LexService.LexerToken;
import org.daisy.pipeline.nlp.lexing.LexService.Sentence;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class OmnilangTest {

	LexResultPrettyPrinter mPrinter;
	LexerToken mLexerToken;
	LexService mLexer;

	static Locale SPANISH;
	static Locale CHINESE;
	static Locale ARABIC;

	static {
		if (System.getProperty("java.version").startsWith("1.7.")) {
			SPANISH = new Locale("spa");
			CHINESE = new Locale("zho");
			ARABIC = new Locale("ara");
		} else {
			SPANISH = new Locale("es");
			CHINESE = new Locale("zh");
			ARABIC = new Locale("ar");
		}
	}

	@Before
	public void setUp() throws LexerInitException {
		mPrinter = new LexResultPrettyPrinter();
		mLexer = new OmnilangLexer();
		mLexer.globalInit();
		mLexerToken = mLexer.newToken();
		mLexerToken.addLang(Locale.ENGLISH);
		mLexerToken.addLang(Locale.FRENCH);
		mLexerToken.addLang(SPANISH);
		mLexerToken.addLang(CHINESE);
		mLexerToken.addLang(ARABIC);
	}

	@After
	public void shutDown() {
		mLexer.globalRelease();
	}

	@Test
	public void twoSentences() throws LexerInitException {
		String ref = "first sentence! Second sentence";
		List<Sentence> sentences = mLexerToken.split(ref, Locale.ENGLISH,
		        new ArrayList<String>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/first/ /sentence/! }{/Second/ /sentence/}", text);
	}

	@Test
	public void dontSplitURLsIntoSentences() throws LexerInitException {
		String ref = "dsadas http://www.kkrva.se/?page_id=715";
		List<Sentence> sentences = mLexerToken.split(ref, Locale.ENGLISH, new ArrayList<>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/dsadas/ /http/:///www.kkrva.se//?/page_id//=//715/}", text);
	}

	@Test
	public void dontCrashOnURLsEndingWithQuestionMark() throws LexerInitException {
		String ref = "dsadas http://www.kkrva.se/?";
		List<Sentence> sentences = mLexerToken.split(ref, Locale.ENGLISH, new ArrayList<>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/dsadas/ /http/:///www.kkrva.se//?}", text);
	}

	@Test
	public void mixed() throws LexerInitException {
		String ref = "first sentence !!... second sentence";
		List<Sentence> sentences = mLexerToken.split(ref, Locale.ENGLISH,
		        new ArrayList<String>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/first/ /sentence/ !!... }{/second/ /sentence/}", text);
	}

	@Ignore
	@Test
	public void whitespaces1() throws LexerInitException {
		String ref = "first sentence !!  !! second sentence";
		List<Sentence> sentences = mLexerToken.split(ref, Locale.ENGLISH,
		        new ArrayList<String>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/first/ /sentence/ !! !! }{/second/ /sentence/}", text);
	}

	@Test
	public void spanish1() throws LexerInitException {
		String ref = "first sentence. ¿Second sentence?";
		List<Sentence> sentences = mLexerToken.split(ref, SPANISH, new ArrayList<String>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/first/ /sentence/. }{¿/Second/ /sentence/?}", text);
	}

	@Ignore
	@Test
	public void spanish2() throws LexerInitException {
		String ref = "first sentence. ¿ Second sentence ?";
		List<Sentence> sentences = mLexerToken.split(ref, SPANISH, new ArrayList<String>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/first/ /sentence/. }{¿ /Second/ /sentence/ ?}", text);
	}

	@Test
	public void chinese() throws LexerInitException {
		String ref = "我喜欢中国。我喜欢英语了。";
		List<Sentence> sentences = mLexerToken.split(ref, CHINESE, new ArrayList<String>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/我喜欢中国/。}{/我喜欢英语了/。}", text);
	}

	@Test
	public void newline() throws LexerInitException {
		String ref = "They do like\nJames.";
		List<Sentence> sentences = mLexerToken.split(ref, Locale.ENGLISH,
		        new ArrayList<String>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/They/ /do/ /like/\n/James/.}", text);
	}

	@Test
	public void abbr1() throws LexerInitException {
		String ref = "J.J.R. Tolkien";
		List<Sentence> sentences = mLexerToken.split(ref, Locale.ENGLISH,
		        new ArrayList<String>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals("{/J.J.R./ /Tolkien/}", text);
	}

	@Test
	public void brackets1() throws LexerInitException {
		String ref = "Bracket example (this is not a sentence!), after.";
		List<Sentence> sentences = mLexerToken.split(ref, Locale.ENGLISH,
		        new ArrayList<String>());
		String text = mPrinter.convert(sentences, ref);
		Assert.assertEquals(
		        "{/Bracket/ /example/ (/this/ /is/ /not/ /a/ /sentence/!), /after/.}", text);
	}

	@Test
	public void testThatTitlesWontSplitASentence()  {
		String ref_mr = "Hej Mr. Walinder, hur mår du idag.";
		List<Sentence> sentences = mLexerToken.split(ref_mr, Locale.ENGLISH, new ArrayList<>());
		String text = mPrinter.convert(sentences, ref_mr);
		Assert.assertEquals("{/Hej/ /Mr./ /Walinder/, /hur/ /mår/ /du/ /idag/.}", text);
	}

	@Test
	public void testThatMultipleTitlesWontSplitASentence()  {
		String ref_mr = "Mr. Swanson and Mrs. Swanson did not visit the lake";
		List<Sentence> sentences = mLexerToken.split(ref_mr, Locale.ENGLISH, new ArrayList<>());
		String text = mPrinter.convert(sentences, ref_mr);
		Assert.assertEquals("{/Mr./ /Swanson/ /and/ /Mrs./ /Swanson/ /did/ /not/ /visit/ /the/ /lake/}", text);
	}

	@Test
	public void testThatTitlesWontSplitASentenceInTheBeginning()  {
		String ref_mr = "Dr. Jekyll did not attend the meeting";
		List<Sentence> sentences = mLexerToken.split(ref_mr, Locale.ENGLISH, new ArrayList<>());
		String text = mPrinter.convert(sentences, ref_mr);
		Assert.assertEquals("{/Dr./ /Jekyll/ /did/ /not/ /attend/ /the/ /meeting/}", text);
	}

	@Test
	public void testThatAWordContainingATitleWontMatch()  {
		String ref_mr = "Professor Snape has black hair";
		List<Sentence> sentences = mLexerToken.split(ref_mr, Locale.ENGLISH, new ArrayList<>());
		String text = mPrinter.convert(sentences, ref_mr);
		Assert.assertEquals("{/Professor/ /Snape/ /has/ /black/ /hair/}", text);
	}
}
