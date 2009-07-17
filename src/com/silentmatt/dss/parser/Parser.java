package com.silentmatt.dss.parser;

import java.util.*;
import com.silentmatt.dss.*;

public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _integer = 2;
	public static final int _decimal = 3;
	public static final int _s = 4;
	public static final int _stringLit = 5;
	public static final int maxT = 57;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	Token t;    // last recognized token
	Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public ErrorReporter errors;

	public CSSDocument CSSDoc;

		boolean PartOfHex(String value) {
			if (value.length() == 7) { return false; }
			if (value.length() + la.val.length() > 7) { return false; }
			List<String> hexes = Arrays.asList(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "a", "b", "c", "d", "e", "f" });
			for (int i = 0; i < la.val.length(); i++) {
				char c = la.val.charAt(i);
				if (!hexes.contains(String.valueOf(c))) {
					return false;
				}
			}
			return true;
		}
		boolean IsUnit() {
			if (la.kind != 1) { return false; }
			List<String> units = Arrays.asList(new String[] { "em", "ex", "px", "gd", "rem", "vw", "vh", "vm", "ch", "mm", "cm", "in", "pt", "pc", "deg", "grad", "rad", "turn", "ms", "s", "hz", "khz" });
			return units.contains(la.val);
		}

/*------------------------------------------------------------------------*
 *----- SCANNER DESCRIPTION ----------------------------------------------*
 *------------------------------------------------------------------------*/



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new ListErrorReporter();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void CSS3() {
		CSSDoc = new CSSDocument();
		String cset = null;
		RuleSet rset = null;
		Directive dir = null;
		
		while (la.kind == 4 || la.kind == 6 || la.kind == 7) {
			if (la.kind == 6) {
				Get();
			} else if (la.kind == 7) {
				Get();
			} else {
				Get();
			}
		}
		while (StartOf(1)) {
			if (StartOf(2)) {
				rset = ruleset();
				CSSDoc.addRuleSet(rset); 
			} else {
				dir = directive();
				CSSDoc.addDirective(dir); 
			}
			while (la.kind == 4 || la.kind == 6 || la.kind == 7) {
				if (la.kind == 6) {
					Get();
				} else if (la.kind == 7) {
					Get();
				} else {
					Get();
				}
			}
		}
	}

	RuleSet  ruleset() {
		RuleSet  rset;
		rset = new RuleSet();
		Selector sel = null;
		Declaration dec = null;
		
		sel = selector();
		rset.getSelectors().add(sel); 
		while (la.kind == 36) {
			Get();
			sel = selector();
			rset.getSelectors().add(sel); 
		}
		Expect(23);
		while (StartOf(3)) {
			if (StartOf(4)) {
				dec = declaration();
				Expect(26);
				rset.addDeclaration(dec); 
			} else if (la.kind == 25) {
				DirectiveBuilder dir = classDirective();
				rset.addDirective(dir.build()); 
			} else {
				DirectiveBuilder dir = defineDirective();
				rset.addDirective(dir.build()); 
			}
		}
		Expect(24);
		return rset;
	}

	Directive  directive() {
		Directive  dir;
		dir = null; //new GenericDirective();
		Declaration dec = null;
		RuleSet rset = null;
		Expression exp = null;
		Directive dr = null;
		String ident = null;
		Medium m;
		                           DirectiveBuilder dirb = new DirectiveBuilder();
		
		switch (la.kind) {
		case 22: {
			dirb = mediaDirective();
			break;
		}
		case 25: {
			dirb = classDirective();
			break;
		}
		case 27: {
			dirb = defineDirective();
			break;
		}
		case 29: {
			dirb = fontFaceDirective();
			break;
		}
		case 31: {
			dirb = importDirective();
			break;
		}
		case 32: {
			dirb = includeDirective();
			break;
		}
		case 33: {
			dirb = charsetDirective();
			break;
		}
		case 30: {
			dirb = pageDirective();
			break;
		}
		case 34: {
			dirb = namespaceDirective();
			break;
		}
		case 35: {
			Get();
			ident = identity();
			dirb.setName("@" + ident);
			String lcName = ident.toLowerCase();
			dirb.setType(DirectiveType.Other);
			
			if (StartOf(5)) {
				if (StartOf(5)) {
					exp = expr();
					dirb.setExpression(exp); 
				} else {
					m = medium();
					dirb.getMediums().add(m); 
				}
			}
			if (la.kind == 23) {
				Get();
				while (StartOf(1)) {
					if (StartOf(4)) {
						dec = declaration();
						Expect(26);
						dirb.addDeclaration(dec); 
					} else if (StartOf(2)) {
						rset = ruleset();
						dirb.addRuleSet(rset); 
					} else {
						dr = directive();
						dirb.addDirective(dr); 
					}
				}
				Expect(24);
			} else if (la.kind == 26) {
				Get();
			} else SynErr(58);
			break;
		}
		default: SynErr(59); break;
		}
		dir = dirb.build(); 
		return dir;
	}

	String  QuotedString() {
		String  qs;
		Expect(5);
		qs = t.val; 
		return qs;
	}

	String  URI() {
		String  url;
		url = ""; 
		Expect(8);
		if (la.kind == 9) {
			Get();
		}
		if (la.kind == 5) {
			url = QuotedString();
		} else if (StartOf(6)) {
			while (StartOf(7)) {
				Get();
				url += t.val; 
				if (la.val.equals(")")) { break; } 
			}
		} else SynErr(60);
		if (la.kind == 10) {
			Get();
		}
		return url;
	}

	Medium  medium() {
		Medium  m;
		m = Medium.all; 
		switch (la.kind) {
		case 11: {
			Get();
			m = Medium.all; 
			break;
		}
		case 12: {
			Get();
			m = Medium.aural; 
			break;
		}
		case 13: {
			Get();
			m = Medium.braille; 
			break;
		}
		case 14: {
			Get();
			m = Medium.embossed; 
			break;
		}
		case 15: {
			Get();
			m = Medium.handheld; 
			break;
		}
		case 16: {
			Get();
			m = Medium.print; 
			break;
		}
		case 17: {
			Get();
			m = Medium.projection; 
			break;
		}
		case 18: {
			Get();
			m = Medium.screen; 
			break;
		}
		case 19: {
			Get();
			m = Medium.tty; 
			break;
		}
		case 20: {
			Get();
			m = Medium.tv; 
			break;
		}
		default: SynErr(61); break;
		}
		return m;
	}

	String  identity() {
		String  ident;
		ident = null; 
		switch (la.kind) {
		case 1: {
			Get();
			break;
		}
		case 21: {
			Get();
			break;
		}
		case 8: {
			Get();
			break;
		}
		case 11: {
			Get();
			break;
		}
		case 12: {
			Get();
			break;
		}
		case 13: {
			Get();
			break;
		}
		case 14: {
			Get();
			break;
		}
		case 15: {
			Get();
			break;
		}
		case 16: {
			Get();
			break;
		}
		case 17: {
			Get();
			break;
		}
		case 18: {
			Get();
			break;
		}
		case 19: {
			Get();
			break;
		}
		case 20: {
			Get();
			break;
		}
		default: SynErr(62); break;
		}
		ident = t.val; 
		return ident;
	}

	DirectiveBuilder  mediaDirective() {
		DirectiveBuilder  dirb;
		dirb = new DirectiveBuilder();
		Medium m;
		RuleSet rset = new RuleSet();
		dirb.setName("@media");
		dirb.setType(DirectiveType.Media);
		
		Expect(22);
		m = medium();
		dirb.getMediums().add(m); 
		Expect(23);
		while (StartOf(8)) {
			if (StartOf(2)) {
				rset = ruleset();
				dirb.addRuleSet(rset); 
			} else if (la.kind == 25) {
				DirectiveBuilder dir = classDirective();
				dirb.addDirective(dir.build()); 
			} else if (la.kind == 27) {
				DirectiveBuilder dir = defineDirective();
				dirb.addDirective(dir.build()); 
			} else {
				DirectiveBuilder dir = includeDirective();
				dirb.addDirective(dir.build()); 
			}
		}
		Expect(24);
		return dirb;
	}

	DirectiveBuilder  classDirective() {
		DirectiveBuilder  dirb;
		dirb = new DirectiveBuilder();
		Declaration dec = null;
		String ident;
		RuleSet rset = new RuleSet();
		dirb.setType(DirectiveType.Class);
		
		Expect(25);
		ident = identity();
		dirb.setID(ident); 
		Expect(23);
		if (StartOf(4)) {
			dec = declaration();
			dirb.addDeclaration(dec); 
			while (la.kind == 26) {
				Get();
				if (la.val.equals("}")) { Get(); return dirb; } 
				dec = declaration();
				dirb.addDeclaration(dec); 
			}
			if (la.kind == 26) {
				Get();
			}
		}
		Expect(24);
		return dirb;
	}

	DirectiveBuilder  defineDirective() {
		DirectiveBuilder  dirb;
		dirb = new DirectiveBuilder();
		Declaration dec = null;
		RuleSet rset = new RuleSet();
		dirb.setType(DirectiveType.Define);
		
		Expect(27);
		if (la.kind == 28) {
			Get();
			dirb.setID("global"); 
		}
		Expect(23);
		while (StartOf(4)) {
			dec = declaration();
			Expect(26);
			dirb.addDeclaration(dec); 
		}
		Expect(24);
		return dirb;
	}

	DirectiveBuilder  includeDirective() {
		DirectiveBuilder  dirb;
		dirb = new DirectiveBuilder();
		Declaration dec = null;
		RuleSet rset = new RuleSet();
		Term trm = new Term();
		Expression expr = new Expression();
		dirb.setType(DirectiveType.Include);
		String url;
		
		Expect(32);
		url = URI();
		trm.setValue(url);
		trm.setType(TermType.Url);
		expr.getTerms().add(trm);
		dirb.setExpression(expr); 
		Expect(26);
		return dirb;
	}

	Declaration  declaration() {
		Declaration  dec;
		dec = new Declaration();
		Expression exp = null;
		String ident = null;
		
		ident = identity();
		dec.setName(ident); 
		Expect(51);
		exp = expr();
		dec.setExpression(exp); 
		if (la.kind == 52) {
			Get();
			dec.setImportant(true); 
		}
		return dec;
	}

	DirectiveBuilder  fontFaceDirective() {
		DirectiveBuilder  dirb;
		dirb = new DirectiveBuilder();
		Declaration dec = null;
		RuleSet rset = new RuleSet();
		dirb.setType(DirectiveType.FontFace);
		
		Expect(29);
		Expect(23);
		if (StartOf(4)) {
			dec = declaration();
			dirb.addDeclaration(dec); 
			while (la.kind == 26) {
				Get();
				if (la.val.equals("}")) { Get(); return dirb; } 
				dec = declaration();
				dirb.addDeclaration(dec); 
			}
			if (la.kind == 26) {
				Get();
			}
		}
		Expect(24);
		return dirb;
	}

	DirectiveBuilder  pageDirective() {
		DirectiveBuilder  dirb;
		dirb = new DirectiveBuilder();
		Declaration dec = null;
		SimpleSelector ss;
		String psd;
		dirb.setType(DirectiveType.Page);
		
		Expect(30);
		if (la.kind == 51) {
			psd = pseudo();
			ss = new SimpleSelector();
			ss.setPseudo(psd);
			dirb.setSimpleSelector(ss);
			
		}
		Expect(23);
		if (StartOf(4)) {
			dec = declaration();
			dirb.addDeclaration(dec); 
			while (la.kind == 26) {
				Get();
				if (la.val.equals("}")) { Get(); return dirb; } 
				dec = declaration();
				dirb.addDeclaration(dec); 
			}
			if (la.kind == 26) {
				Get();
			}
		}
		Expect(24);
		return dirb;
	}

	String  pseudo() {
		String  pseudo;
		pseudo = "";
		Expression exp = null;
		String ident = null;
		
		Expect(51);
		if (la.kind == 51) {
			Get();
		}
		ident = identity();
		pseudo = ident; 
		if (la.kind == 9) {
			Get();
			pseudo += t.val; 
			exp = expr();
			pseudo += exp.toString(); 
			Expect(10);
			pseudo += t.val; 
		}
		return pseudo;
	}

	DirectiveBuilder  importDirective() {
		DirectiveBuilder  dirb;
		dirb = new DirectiveBuilder();
		Declaration dec = null;
		RuleSet rset = new RuleSet();
		Term trm = new Term();
		Expression expr = new Expression();
		dirb.setType(DirectiveType.Import);
		Medium m;
		String url;
		
		Expect(31);
		url = URI();
		trm.setValue(url);
		trm.setType(TermType.Url);
		expr.getTerms().add(trm);
		dirb.setExpression(expr); 
		if (StartOf(9)) {
			m = medium();
			dirb.getMediums().add(m); 
		}
		Expect(26);
		return dirb;
	}

	DirectiveBuilder  charsetDirective() {
		DirectiveBuilder  dirb;
		dirb = new DirectiveBuilder();
		Declaration dec = null;
		RuleSet rset = new RuleSet();
		Term trm;
		Expression expr = new Expression();
		dirb.setType(DirectiveType.Charset);
		
		Expect(33);
		trm = term();
		expr.getTerms().add(trm);
		dirb.setExpression(expr); 
		Expect(26);
		return dirb;
	}

	Term  term() {
		Term  trm;
		trm = new Term();
		String val = "";
		Expression exp = null;
		String ident = null;
		
		if (la.kind == 5) {
			val = QuotedString();
			trm.setValue(val); trm.setType(TermType.String); 
		} else if (la.kind == 8) {
			val = URI();
			trm.setValue(val); trm.setType(TermType.Url); 
		} else if (la.kind == 54) {
			Get();
			ident = identity();
			trm.setValue("U\\" + ident); trm.setType(TermType.Unicode); 
		} else if (la.kind == 41) {
			val = HexValue();
			trm.setValue(val); trm.setType(TermType.Hex); 
		} else if (StartOf(4)) {
			ident = identity();
			trm.setValue(ident); trm.setType(TermType.String); 
			if (StartOf(10)) {
				while (la.kind == 42 || la.kind == 44 || la.kind == 51) {
					if (la.kind == 51) {
						Get();
						trm.setValue(trm.getValue() + t.val); 
						if (la.kind == 51) {
							Get();
							trm.setValue(trm.getValue() + t.val); 
						}
						ident = identity();
						trm.setValue(trm.getValue() + ident); 
					} else if (la.kind == 42) {
						Get();
						trm.setValue(trm.getValue() + t.val); 
						ident = identity();
						trm.setValue(trm.getValue() + ident); 
					} else {
						Get();
						trm.setValue(trm.getValue() + t.val); 
						if (StartOf(4)) {
							ident = identity();
							trm.setValue(trm.getValue() + ident); 
						} else if (la.kind == 2) {
							Get();
							trm.setValue(trm.getValue() + t.val); 
						} else SynErr(63);
					}
				}
			}
			if (la.kind == 9) {
				Get();
				exp = expr();
				Function func = new Function();
				func.setName(trm.getValue());
				func.setExpression(exp);
				trm.setValue(null);
				trm.setFunction(func);
				trm.setType(TermType.Function);
				
				Expect(10);
			}
		} else if (StartOf(11)) {
			if (la.kind == 37 || la.kind == 55) {
				if (la.kind == 55) {
					Get();
					trm.setSign('-'); 
				} else {
					Get();
					trm.setSign('+'); 
				}
			}
			if (la.kind == 2) {
				Get();
			} else if (la.kind == 3) {
				Get();
			} else SynErr(64);
			val = t.val; 
			if (StartOf(12)) {
				if (la.val.toLowerCase().equals("n")) {
					Expect(21);
					val += t.val; 
					if (la.kind == 37 || la.kind == 55) {
						if (la.kind == 37) {
							Get();
							val += t.val; 
						} else {
							Get();
							val += t.val; 
						}
						Expect(2);
						val += t.val; 
					}
				} else if (la.kind == 56) {
					Get();
					trm.setUnit(Unit.Percent); 
				} else {
					if (IsUnit()) {
						ident = identity();
						try {
						trm.setUnit(Unit.parse(ident));
						} catch (Exception ex) {
							errors.SemErr(t.line, t.col, "Unrecognized unit '" + ident + "'");
						}
						
					}
				}
			}
			trm.setValue(val); trm.setType(TermType.Number); 
		} else SynErr(65);
		return trm;
	}

	DirectiveBuilder  namespaceDirective() {
		DirectiveBuilder  dirb;
		dirb = new DirectiveBuilder();
		Declaration dec = null;
		RuleSet rset = new RuleSet();
		Term trm = new Term();
		Expression expr = new Expression();
		dirb.setType(DirectiveType.Namespace);
		String ident;
		String url;
		
		Expect(34);
		if (StartOf(4)) {
			ident = identity();
			dirb.setID(ident); 
		}
		if (la.kind == 8) {
			url = URI();
			trm.setValue(url);
			trm.setType(TermType.Url);
			expr.getTerms().add(trm);
			dirb.setExpression(expr);
			
		} else if (la.kind == 5) {
			url = QuotedString();
			trm.setValue(url);
			trm.setType(TermType.Url);
			expr.getTerms().add(trm);
			dirb.setExpression(expr);
			
		} else SynErr(66);
		Expect(26);
		return dirb;
	}

	Expression  expr() {
		Expression  exp;
		exp = new Expression();
		Character sep = null;
		Term trm = null;
		
		trm = term();
		exp.getTerms().add(trm); 
		while (StartOf(13)) {
			if (la.kind == 36 || la.kind == 53) {
				if (la.kind == 53) {
					Get();
					sep = '/'; 
				} else {
					Get();
					sep = ','; 
				}
			}
			trm = term();
			if (sep != null) { trm.setSeperator(sep); }
			exp.getTerms().add(trm);
			sep = null;
			
		}
		return exp;
	}

	Selector  selector() {
		Selector  sel;
		sel = new Selector();
		SimpleSelector ss = null;
		Combinator cb = null;
		
		ss = simpleselector();
		sel.getSimpleSelectors().add(ss); 
		while (StartOf(14)) {
			if (la.kind == 37 || la.kind == 38 || la.kind == 39) {
				if (la.kind == 37) {
					Get();
					cb = Combinator.PrecededImmediatelyBy; 
				} else if (la.kind == 38) {
					Get();
					cb = Combinator.ChildOf; 
				} else {
					Get();
					cb = Combinator.PrecededBy; 
				}
			}
			ss = simpleselector();
			if (cb != null) { ss.setCombinator(cb); }
			sel.getSimpleSelectors().add(ss);
			
			cb = null; 
		}
		return sel;
	}

	SimpleSelector  simpleselector() {
		SimpleSelector  ss;
		ss = new SimpleSelector();
		String psd = null;
		Attribute atb = null;
		SimpleSelector parent = ss;
		String ident = null;
		
		if (StartOf(4)) {
			ident = identity();
			ss.setElementName(ident); 
		} else if (la.kind == 40) {
			Get();
			ss.setElementName("*"); 
		} else if (StartOf(15)) {
			if (la.kind == 41) {
				Get();
				ident = identity();
				ss.setID(ident); 
			} else if (la.kind == 42) {
				Get();
				ident = identity();
				ss.setClassName(ident); 
			} else if (la.kind == 43) {
				atb = attrib();
				ss.setAttribute(atb); 
			} else {
				psd = pseudo();
				ss.setPseudo(psd); 
			}
		} else SynErr(67);
		while (StartOf(15)) {
			SimpleSelector child = new SimpleSelector(); 
			if (la.kind == 41) {
				Get();
				ident = identity();
				child.setID(ident); 
			} else if (la.kind == 42) {
				Get();
				ident = identity();
				child.setClassName(ident); 
			} else if (la.kind == 43) {
				atb = attrib();
				child.setAttribute(atb); 
			} else {
				psd = pseudo();
				child.setPseudo(psd); 
			}
			parent.setChild(child);
			parent = child;
			
		}
		return ss;
	}

	Attribute  attrib() {
		Attribute  atb;
		atb = new Attribute();
		String quote = null;
		String ident = null;
		
		Expect(43);
		ident = identity();
		atb.setOperand(ident); 
		if (StartOf(16)) {
			switch (la.kind) {
			case 44: {
				Get();
				atb.setOperator(AttributeOperator.Equals); 
				break;
			}
			case 45: {
				Get();
				atb.setOperator(AttributeOperator.InList); 
				break;
			}
			case 46: {
				Get();
				atb.setOperator(AttributeOperator.Hyphenated); 
				break;
			}
			case 47: {
				Get();
				atb.setOperator(AttributeOperator.EndsWith); 
				break;
			}
			case 48: {
				Get();
				atb.setOperator(AttributeOperator.BeginsWith); 
				break;
			}
			case 49: {
				Get();
				atb.setOperator(AttributeOperator.Contains); 
				break;
			}
			}
			if (StartOf(4)) {
				ident = identity();
				atb.setValue(ident); 
			} else if (la.kind == 5) {
				quote = QuotedString();
				atb.setValue(quote); 
			} else SynErr(68);
		}
		Expect(50);
		return atb;
	}

	String  HexValue() {
		String  val;
		val = "";
		boolean found = false;
		
		Expect(41);
		val += t.val; 
		if (la.kind == 2) {
			Get();
			val += t.val; 
		} else if (PartOfHex(val)) {
			Expect(1);
			val += t.val; found = true; 
		} else SynErr(69);
		if (!found && PartOfHex(val)) {
			Expect(1);
			val += t.val; 
		}
		return val;
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		CSS3();

		Expect(0);
	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,T,x,x, x,x,x,x, T,x,x,T, T,T,T,T, T,T,T,T, T,T,T,x, x,T,x,T, x,T,T,T, T,T,T,T, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x},
		{x,T,x,x, x,x,x,x, T,x,x,T, T,T,T,T, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x},
		{x,T,x,x, x,x,x,x, T,x,x,T, T,T,T,T, T,T,T,T, T,T,x,x, x,T,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,T,x,x, x,x,x,x, T,x,x,T, T,T,T,T, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,T,T,T, x,T,x,x, T,x,x,T, T,T,T,T, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,x,x},
		{x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
		{x,T,T,T, T,x,T,T, T,T,x,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,x},
		{x,T,x,x, x,x,x,x, T,x,x,T, T,T,T,T, T,T,T,T, T,T,x,x, x,T,x,T, x,x,x,x, T,x,x,x, x,x,x,x, T,T,T,T, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,x,x,x, x,x,x,T, x,x,x,x, x,x,x},
		{x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x},
		{x,T,x,x, x,x,x,x, T,x,x,T, T,T,T,T, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,x,x},
		{x,T,T,T, x,T,x,x, T,x,x,T, T,T,T,T, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,T,T,T, x,x,x},
		{x,T,x,x, x,x,x,x, T,x,x,T, T,T,T,T, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,T,T,T, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,T, T,T,x,x, x,x,x,x, x,x,x}

	};

	public static final String getErrorMessage(int n) {
		String s = null;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "ident expected"; break;
			case 2: s = "integer expected"; break;
			case 3: s = "decimal expected"; break;
			case 4: s = "s expected"; break;
			case 5: s = "stringLit expected"; break;
			case 6: s = "\"<!--\" expected"; break;
			case 7: s = "\"-->\" expected"; break;
			case 8: s = "\"url\" expected"; break;
			case 9: s = "\"(\" expected"; break;
			case 10: s = "\")\" expected"; break;
			case 11: s = "\"all\" expected"; break;
			case 12: s = "\"aural\" expected"; break;
			case 13: s = "\"braille\" expected"; break;
			case 14: s = "\"embossed\" expected"; break;
			case 15: s = "\"handheld\" expected"; break;
			case 16: s = "\"print\" expected"; break;
			case 17: s = "\"projection\" expected"; break;
			case 18: s = "\"screen\" expected"; break;
			case 19: s = "\"tty\" expected"; break;
			case 20: s = "\"tv\" expected"; break;
			case 21: s = "\"n\" expected"; break;
			case 22: s = "\"@media\" expected"; break;
			case 23: s = "\"{\" expected"; break;
			case 24: s = "\"}\" expected"; break;
			case 25: s = "\"@class\" expected"; break;
			case 26: s = "\";\" expected"; break;
			case 27: s = "\"@define\" expected"; break;
			case 28: s = "\"global\" expected"; break;
			case 29: s = "\"@font-face\" expected"; break;
			case 30: s = "\"@page\" expected"; break;
			case 31: s = "\"@import\" expected"; break;
			case 32: s = "\"@include\" expected"; break;
			case 33: s = "\"@charset\" expected"; break;
			case 34: s = "\"@namespace\" expected"; break;
			case 35: s = "\"@\" expected"; break;
			case 36: s = "\",\" expected"; break;
			case 37: s = "\"+\" expected"; break;
			case 38: s = "\">\" expected"; break;
			case 39: s = "\"~\" expected"; break;
			case 40: s = "\"*\" expected"; break;
			case 41: s = "\"#\" expected"; break;
			case 42: s = "\".\" expected"; break;
			case 43: s = "\"[\" expected"; break;
			case 44: s = "\"=\" expected"; break;
			case 45: s = "\"~=\" expected"; break;
			case 46: s = "\"|=\" expected"; break;
			case 47: s = "\"$=\" expected"; break;
			case 48: s = "\"^=\" expected"; break;
			case 49: s = "\"*=\" expected"; break;
			case 50: s = "\"]\" expected"; break;
			case 51: s = "\":\" expected"; break;
			case 52: s = "\"!important\" expected"; break;
			case 53: s = "\"/\" expected"; break;
			case 54: s = "\"U\\\\\" expected"; break;
			case 55: s = "\"-\" expected"; break;
			case 56: s = "\"%\" expected"; break;
			case 57: s = "??? expected"; break;
			case 58: s = "invalid directive"; break;
			case 59: s = "invalid directive"; break;
			case 60: s = "invalid URI"; break;
			case 61: s = "invalid medium"; break;
			case 62: s = "invalid identity"; break;
			case 63: s = "invalid term"; break;
			case 64: s = "invalid term"; break;
			case 65: s = "invalid term"; break;
			case 66: s = "invalid namespaceDirective"; break;
			case 67: s = "invalid simpleselector"; break;
			case 68: s = "invalid attrib"; break;
			case 69: s = "invalid HexValue"; break;
			default: s = "error " + n; break;
		}
		return s;
	}
} // end Parser


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}

