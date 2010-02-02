/*
 * Copyright (C) 2007-2010 Siemens AG
 *
 * This program and its interfaces are free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.siemens.ct.exi.datatype;

import java.util.Set;

import junit.framework.TestCase;

import org.apache.xerces.impl.xpath.regex.EXIRegularExpression;
import org.junit.Test;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.charset.CodePointCharacterSet;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.exceptions.EXIException;

public class RegularExpressionTest extends TestCase {

	static final int codePoint(char c) {
		assert (!Character.isHighSurrogate(c));
		return (int) c;
	}

	@Test
	public void testRange1() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression("[A-C]");
		assertTrue(re.getCodePoints().size() == 3);
	}

	public void testRange2() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression("[A-Z][B-Z][C-Z]");
		assertTrue(re.getCodePoints().size() == 26);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('A') == 0);
		assertTrue(rcs.getCode('L') == 11);
		assertTrue(rcs.getCode('Z') == 25);
		assertTrue(rcs.getCodingLength() == 5);
	}

	public void testRange3() throws Exception {
		String regex = "[A-Z][A-Z][A-Z]";// e.g. "ABC"
		EXIRegularExpression re = new EXIRegularExpression(regex);
		assertTrue(re.getCodePoints().size() == 26);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('A') == 0);
		assertTrue(rcs.getCode('L') == 11);
		assertTrue(rcs.getCode('Z') == 25);
		assertTrue(rcs.getCodePoint(1) == 'B');
		assertTrue(rcs.getCodingLength() == 5);
	}

	public void testRange4() throws EXIException {
		String regex = "[0-9][0-9][0-9][0-9][0-9]"; // e.g. "12345"
		EXIRegularExpression re = new EXIRegularExpression(regex);
		assertTrue(re.getCodePoints().size() == 10);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('0') == 0);
		assertTrue(rcs.getCode('5') == 5);
		assertTrue(rcs.getCode('9') == 9);
		assertTrue(rcs.getCodingLength() == 4);
	}

	public void testRange5() throws EXIException {
		// THREE of the LOWERCASE OR UPPERCASE letters from a to z
		String regex = "[a-zA-Z][a-zA-Z][a-zA-Z]"; // e.g. "aXy"
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());

		assertTrue(rcs.getCode('0') == Constants.NOT_FOUND);
		assertTrue(rcs.getCode('A') == 0);
		assertTrue(rcs.getCode('a') == 26);
		assertTrue(rcs.getCodingLength() == 6);
	}

	public void testRange6() throws EXIException {
		// THREE of the LOWERCASE OR UPPERCASE letters from a to z
		String regex = "[\\-a-zA-Z]"; // e.g. "-" "B"
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('0') == Constants.NOT_FOUND);
		assertTrue(rcs.getCode('-') == 0);
		assertTrue(rcs.getCode('A') == 1);
		assertTrue(rcs.getCode('a') == 27);
		assertTrue(rcs.getCode('z') == 52);
		assertTrue(rcs.getCodingLength() == 6);
	}

	public void testRange7() throws EXIException {
		// ONE of the following letters: x, y, OR z:
		String regex = "[xyz]"; // e.g. "x" "y" "z"
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == 'x');
		assertTrue(rcs.getCodePoint(1) == 'y');
		assertTrue(rcs.getCodePoint(2) == 'z');
		assertTrue(rcs.getCodingLength() == 2);
	}

	public void testRange8() throws EXIException {
		// zero or more occurrences of lowercase letters from a to z:
		String regex = "([a-z])*";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == 'a');
		assertTrue(rcs.getCode('c') == 2);
		assertTrue(rcs.getCodePoint(25) == 'z');
		assertTrue(rcs.getCodingLength() == 5);
	}

	public void testRange9() throws EXIException {
		// For example, "sToP" will be validated by this pattern, but not "Stop"
		// or "STOP" or "stop":
		String regex = "([a-z][A-Z])+";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == 'A');
		assertTrue(rcs.getCodePoint(25) == 'Z');
		assertTrue(rcs.getCode('c') == 28);
		assertTrue(rcs.getCodingLength() == 6);
	}

	// vowel
	public void testRange10() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression("[b-df-hj-np-tv-z]");
		Set<Integer> codePoints = re.getCodePoints();
		assertTrue(codePoints.size() == 21);
		assertFalse(codePoints.contains(codePoint('a')));
		assertTrue(codePoints.contains(codePoint('b')));

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('b') == 0);
		assertTrue(rcs.getCodePoint(2) == 'd');
		assertTrue(rcs.getCodingLength() == 5);
	}

	public void testRange11() throws Exception {
		// Matching strings: 1z, 2z, pz, rz
		// Non-matching strings: cz,dz, 0sz
		EXIRegularExpression re = new EXIRegularExpression("[0-9pqr]z");
		Set<Integer> codePoints = re.getCodePoints();
		assertTrue(codePoints.size() == (10 + 4));
		assertTrue(codePoints.contains(codePoint('0')));
		assertTrue(codePoints.contains(codePoint('9')));
		assertTrue(codePoints.contains(codePoint('p')));
		assertTrue(codePoints.contains(codePoint('q')));
		assertTrue(codePoints.contains(codePoint('r')));
		assertTrue(codePoints.contains(codePoint('z')));

		assertFalse(codePoints.contains(codePoint('h')));

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('0') == 0);
		assertTrue(rcs.getCodePoint(3) == '3');
		assertTrue(rcs.getCodePoint(13) == 'z');
		assertTrue(rcs.getCodingLength() == 4);
	}

	public void testPattern9() throws EXIException {
		// "password" with a restriction. There must be exactly eight characters
		// in a row and those characters must be lowercase or uppercase letters
		// from a to z, or a number from 0 to 9:
		String regex = "[a-zA-Z0-9]{8}";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == '0');
		assertTrue(rcs.getCodePoint(25) == 'P');
		assertTrue(rcs.getCode('c') == 38);
		assertTrue(rcs.getCodingLength() == 6);
	}

	public void testPattern10() throws EXIException {
		// 8 restricted digits
		String regex = "[2-4]{8}";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == '2');
		assertTrue(rcs.getCode('4') == 2);
		assertTrue(rcs.getCodingLength() == 2);
	}

	public void testPattern11() throws EXIException {
		// 111 restricted digits
		String regex = "[3-9]{111}";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(1) == '4');
		assertTrue(rcs.getCode('4') == 1);
		assertTrue(rcs.getCodingLength() == 3);
	}

	public void testPattern12() throws EXIException {
		// zzzzz...
		String regex = "z*";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == 'z');
		assertTrue(rcs.getCode('z') == 0);
		assertTrue(rcs.getCode('?') == Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength() == 1);
	}

	public void testPattern13() throws EXIException {
		//	
		String regex = "\\s";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == '\t');
		assertTrue(rcs.getCode(' ') == 3);
		assertTrue(rcs.getCode('?') == Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength() == 3);
	}

	public void testPattern14() throws EXIException {
		// Number decimal digit
		String regex = "\\d";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == '0');
		assertTrue(rcs.getCode('3') == 3);
		assertTrue(rcs.getCode('?') == Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength() == 8); // plenty of other stuff
	}

	public void testPattern15() throws EXIException {
		// Number decimal digit
		String regex = "\\p{Nd}";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(1) == '1');
		assertTrue(rcs.getCode('6') == 6);
		assertTrue(rcs.getCode('?') == Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength() == 8); // plenty of other stuff
	}

	public void testPattern16() throws EXIException {
		//	
		String regex = "\\i\\c*";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		assertTrue(re.isEntireSetOfXMLCharacters());
	}

	public void testPattern17() throws EXIException {
		// complexEsc \P{ L }
		String regex = "abc\\P{L}def";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		assertTrue(re.isEntireSetOfXMLCharacters());
	}

	public void testPattern18() throws EXIException {
		// language --> ([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*
		String regex = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == '-');
		assertTrue(rcs.getCodePoint(1) == '0');
		assertTrue(rcs.getCode('8') == 9);
		assertTrue(rcs.getCode('?') == Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength() == 6);
	}

	public void testPattern19() throws EXIException {
		// Social Security Number v1
		// Matches: 078-05-1120
		String regex = "[0-9]{3}-[0-9]{2}-[0-9]{4}";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == '-');
		assertTrue(rcs.getCodePoint(1) == '0');
		assertTrue(rcs.getCode('8') == 9);
		assertTrue(rcs.getCode('?') == Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength() == 4);
	}

	public void testPattern20() throws EXIException {
		// Social Security Number v2
		// Matches: 078-05-1120 | 078 05 1120 | 078051120
		String regex = "[0-9]{3}(-| )?[0-9]{2}(-| )?[0-9]{4}";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == ' ');
		assertTrue(rcs.getCodePoint(1) == '-');
		assertTrue(rcs.getCodePoint(2) == '0');
		assertTrue(rcs.getCode('8') == 10);
		assertTrue(rcs.getCode('?') == Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength() == 4);
	}

	public void testPattern21() throws EXIException {
		// Partial Pattern in OpenOffice
		// ([$]?([^\. ']+|'[^']+'))
		String regex = "([$]?([^\\. ']+|'[^']+'))";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		// TODO.. what is it ?
		assertTrue(re.isEntireSetOfXMLCharacters());
	}

	public void testPattern22() throws EXIException {
		// Pattern in OpenOffice
		// ([$]?([^\. ']+|'[^']+'))?\.[$]?[A-Z]+[$]?[0-9]+
		String regex = "([$]?([^\\. ']+|'[^']+'))?\\.[$]?[A-Z]+[$]?[0-9]+";
		EXIRegularExpression re = new EXIRegularExpression(regex);
		assertTrue(re.isEntireSetOfXMLCharacters());
	}

	public void testMaleFemale() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression("male|female");
		// aeflm
		assertTrue(re.getCodePoints().size() == 5);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('a') == 0);
		assertTrue(rcs.getCodePoint(3) == 'l');
		assertTrue(rcs.getCodingLength() == 3);
	}

	public void testAorBorCorMinus() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression("A|B|C|-");
		assertTrue(re.getCodePoints().size() == 4);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('C') == 3);
		assertTrue(rcs.getCodePoint(0) == '-');
		assertTrue(rcs.getCodingLength() == 3);
	}

	public void testSSN() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression(
				"[0-9]{3}-[0-9]{2}-[0-9]{4}");
		assertTrue(re.getCodePoints().size() == 11);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('5') == 6);
		assertTrue(rcs.getCodePoint(3) == '2');
		assertTrue(rcs.getCodingLength() == 4);
	}

	public void testProdNumType() throws Exception {
		// \d{3}-[A-Z]{2}|\d{7}
		EXIRegularExpression re = new EXIRegularExpression(
				"\\d{3}-[A-Z]{2}|\\d{7}");
		Set<Integer> codePoints = re.getCodePoints();

		// Note: \d stands for any decimal digit BUT contains more than just
		// digits

		assertTrue(codePoints.contains(codePoint('0')));
		assertTrue(codePoints.contains(codePoint('3')));
		assertTrue(codePoints.contains(codePoint('9')));
		assertTrue(codePoints.contains(codePoint('-')));
		assertTrue(codePoints.contains(codePoint('X')));
		assertFalse(codePoints.contains(codePoint('a')));

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('1') == 2);
		assertTrue(rcs.getCodePoint(12) == 'B');
		assertTrue(rcs.getCodingLength() == 8);
	}

	public void testBasicLatin() throws Exception {
		// \p{IsBasicLatin}
		EXIRegularExpression re = new EXIRegularExpression("\\p{IsBasicLatin}");
		Set<Integer> codePoints = re.getCodePoints();
		// System.out.println("IsBasicLatin Size = " + charSet.size());
		assertTrue(codePoints.size() < 256);
		assertTrue(codePoints.contains(codePoint('a')));
		assertTrue(codePoints.contains(codePoint('n')));
		assertTrue(codePoints.contains(codePoint('Z')));
		assertFalse(codePoints.contains(codePoint('�')));
		assertFalse(codePoints.contains(codePoint('�')));

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('C') == 67);
		assertTrue(rcs.getCodingLength() == 8);
	}

	public void testNonBasicLatin() throws Exception {
		// \P{IsBasicLatin}
		EXIRegularExpression re = new EXIRegularExpression("\\P{IsBasicLatin}");
		assertTrue(re.isEntireSetOfXMLCharacters());

		// Set<Integer> codePoints = re.getCodePoints();
		// // System.out.println("NonBasicLatin Size = " + charSet.size());
		// assertFalse(codePoints.contains(codePoint('a')));
		// assertFalse(codePoints.contains(codePoint('n')));
		// assertFalse(codePoints.contains(codePoint('Z')));
		// assertTrue(codePoints.contains(codePoint('�')));
		// assertTrue(codePoints.contains(codePoint('�')));
	}

	public void testUnrestricted1() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression(".*");
		assertTrue(re.isEntireSetOfXMLCharacters());
	}

	public void testHuge1() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression("\\p{L}");
		assertTrue(re.isEntireSetOfXMLCharacters());
		// Set<Integer> codePoints = re.getCodePoints();
		// assertTrue(codePoints.size() > 45000);
		// assertTrue(re.isEntireSetOfXMLCharacters());
	}

	public void testNeg() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression("[^A-Z]");
		assertTrue(re.isEntireSetOfXMLCharacters());

		// Set<Integer> codePoints = re.getCodePoints();
		// assertTrue(codePoints.size() > 999999);
		//
		// assertTrue(codePoints.contains(codePoint('�')));
		//
		// assertFalse(codePoints.contains(codePoint('B')));
		// assertFalse(codePoints.contains(codePoint('X')));
		//		
		// assertTrue(re.isEntireSetOfXMLCharacters());
	}

	// vowel, identical to "[b-df-hj-np-tv-z]" without subtraction
	public void testSubtraction1() throws Exception {
		EXIRegularExpression re = new EXIRegularExpression("[a-z-[aeiuo]]");
		Set<Integer> codePoints = re.getCodePoints();
		assertTrue(codePoints.size() == 21);

		assertTrue(codePoints.contains(codePoint('b')));
		assertTrue(codePoints.contains(codePoint('d')));

		assertFalse(codePoints.contains(codePoint('e')));
		assertFalse(codePoints.contains(codePoint('u')));

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('d') == 2);
		assertTrue(rcs.getCodePoint(6) == 'j');
		assertTrue(rcs.getCodingLength() == 5);
	}

	public void testSubtraction2() throws Exception {
		// matches any character in the string 0123789
		EXIRegularExpression re = new EXIRegularExpression("[0-9-[0-6-[0-3]]]");
		Set<Integer> codePoints = re.getCodePoints();
		assertTrue(codePoints.size() == 7);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('7') == 4);
		assertTrue(rcs.getCodePoint(5) == '8');
		assertTrue(rcs.getCodingLength() == 3);
	}

	public void testSubtraction3() throws Exception {
		// [\p{Ll}\p{Lu}-[\p{IsBasicLatin}]] matches all uppercase and lowercase
		// Unicode letters, except any ASCII letters
		EXIRegularExpression re = new EXIRegularExpression(
				"[\\p{Ll}\\p{Lu}-[\\p{IsBasicLatin}]]");
		Set<Integer> codePoints = re.getCodePoints();
		// System.out.println("Size = " + charSet.size());
		assertTrue(codePoints.size() > 256);
	}

	public void testSubtraction4() throws EXIException {
		// [A-Z-[C-X-[M-N]]]*
		// means: A,B,M,N,Y,Z
		String regex = "[A-Z-[C-X-[M-N]]]*";
		EXIRegularExpression re = new EXIRegularExpression(regex);
		Set<Integer> codePoints = re.getCodePoints();

		assertTrue(codePoints.size() == 6);

		assertTrue(codePoints.contains(codePoint('A')));
		assertTrue(codePoints.contains(codePoint('B')));
		assertTrue(codePoints.contains(codePoint('M')));
		assertTrue(codePoints.contains(codePoint('N')));
		assertTrue(codePoints.contains(codePoint('Y')));
		assertTrue(codePoints.contains(codePoint('Z')));

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCode('M') == 2);
		assertTrue(rcs.getCodePoint(5) == 'Z');
		assertTrue(rcs.getCodingLength() == 3);
	}

	public void testSubtraction5() throws EXIException {
		// [A-Z-[C-X]]
		// means: A, B, Y, Z
		String regex = "[A-Z-[C-X]]";
		EXIRegularExpression re = new EXIRegularExpression(regex);

		RestrictedCharacterSet rcs = new CodePointCharacterSet(re
				.getCodePoints());
		assertTrue(rcs.getCodePoint(0) == 'A');
		assertTrue(rcs.getCodePoint(1) == 'B');
		assertTrue(rcs.size() == 4);
		assertTrue(rcs.getCodePoint(2) == 'Y');
		assertTrue(rcs.getCodePoint(3) == 'Z');
		assertTrue(rcs.getCode('?') == Constants.NOT_FOUND);
		assertTrue(rcs.getCodingLength() == 3);
	}

	public void testPatternInvalid1() throws EXIException {
		try {
			// non-sense
			String regex = "[bla{4}";
			EXIRegularExpression re = new EXIRegularExpression(regex);
			@SuppressWarnings("unused")
			RestrictedCharacterSet rcs = new CodePointCharacterSet(re
					.getCodePoints());
			fail();
		} catch (RuntimeException e) {
			// an exception for invalid regex is expected
		}
	}

}