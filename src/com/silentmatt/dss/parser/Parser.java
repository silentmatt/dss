package com.silentmatt.dss.parser;

import java.util.*;
import com.silentmatt.dss.*;
import com.silentmatt.dss.directive.*;
import com.silentmatt.dss.term.*;
import com.silentmatt.dss.bool.*;
import com.silentmatt.dss.calc.*;

class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _atref = 2;
	public static final int _integer = 3;
	public static final int _decimal = 4;
	public static final int _stringLit = 5;
	public static final int _url = 6;
	public static final int maxT = 72;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	Token t;    // last recognized token
	Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public ErrorReporter errors;

	public DSSDocument CSSDoc;

        boolean partOfHex(String value) {
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
        boolean isUnit() {
            if (la.kind != 1) { return false; }
            List<String> units = Arrays.asList(new String[] { "em", "ex", "px", "gd", "rem", "vw", "vh", "vm", "ch", "mm", "cm", "in", "pt", "pc", "deg", "grad", "rad", "turn", "ms", "s", "hz", "khz" });
            return units.contains(la.val);
        }
        boolean isDeclaration() {
            return la.kind == _ident && scanner.Peek().val.equals(":");
        }
        boolean isNestedSelector() {
            Token next = la;
            while (!next.val.equals(";") && !next.val.equals("}")) {
                if (next.val.equals("{")) {
                    return true;
                }
                next = scanner.Peek();
            }
            return false;
        }
        boolean endOfBlock() {
            return la.val.equals("}");
        }

/*------------------------------------------------------------------------*
 *----- SCANNER DESCRIPTION ----------------------------------------------*
 *------------------------------------------------------------------------*/



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new ListErrorReporter();
	}

	void SynErr(int n) {
		if (errDist >= minErrDist) {
            errors.SynErr(la.line, la.col, n);
        }
		errDist = 0;
	}

	public void SemErr(String msg) {
		if (errDist >= minErrDist) {
            errors.SemErr(t.line, t.col, msg);
        }
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
		if (la.kind==n) {
            Get();
        }
        else {
            SynErr(n);
        }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) {
            Get();
        }
		else {
			SynErr(n);
			while (!StartOf(follow)) {
                Get();
            }
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) { return false; }
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
		CSSDoc = new DSSDocument(); 
		while (la.kind == 7 || la.kind == 8) {
			if (la.kind == 7) {
				Get();
			} else {
				Get();
			}
		}
		while (StartOf(1)) {
			Rule rule = rule();
			CSSDoc.addRule(rule); 
			while (la.kind == 7 || la.kind == 8) {
				if (la.kind == 7) {
					Get();
				} else {
					Get();
				}
			}
		}
	}

	Rule  rule() {
		Rule  rule;
		rule = null; 
		if (StartOf(2)) {
			rule = ruleset();
		} else if (StartOf(3)) {
			rule = directive();
		} else SynErr(73);
		return rule;
	}

	RuleSet  ruleset() {
		RuleSet  rset;
		rset = new RuleSet();
		Selector sel;
		List<Declaration> decs;
		Rule dir;
		Combinator cb = Combinator.Descendant;
		
		sel = selector();
		rset.getSelectors().add(sel); 
		while (la.kind == 24) {
			Get();
			sel = selector();
			rset.getSelectors().add(sel); 
		}
		Expect(25);
		while (StartOf(4)) {
			if (!isNestedSelector()) {
				decs = multideclaration();
				rset.addDeclarations(decs);
				if (endOfBlock()) {
				    break;
				}
				
				Expect(32);
			} else if (la.kind == 30) {
				dir = classDirective();
				rset.addRule(dir); 
			} else if (la.kind == 37) {
				dir = defineDirective();
				rset.addRule(dir); 
			} else {
				cb = Combinator.Descendant; 
				if (la.kind == 34) {
					Get();
				}
				if (t.pos + t.val.length() == la.pos) {
				   cb = Combinator.None;
				}
				else {
				    cb = Combinator.Descendant;
				}
				
				if (la.kind == 33 || la.kind == 35 || la.kind == 36) {
					if (la.kind == 35) {
						Get();
						cb = Combinator.PrecededImmediatelyBy; 
					} else if (la.kind == 33) {
						Get();
						cb = Combinator.ChildOf; 
					} else {
						Get();
						cb = Combinator.PrecededBy; 
					}
				}
				RuleSet nested = ruleset();
				rset.addNestedRuleSet(cb, nested); 
			}
		}
		Expect(26);
		return rset;
	}

	Rule  directive() {
		Rule  dir;
		dir = null; 
		switch (la.kind) {
		case 27: {
			dir = ifDirective();
			break;
		}
		case 23: {
			dir = mediaDirective();
			break;
		}
		case 30: {
			dir = classDirective();
			break;
		}
		case 37: {
			dir = defineDirective();
			break;
		}
		case 38: {
			dir = fontFaceDirective();
			break;
		}
		case 40: {
			dir = importDirective();
			break;
		}
		case 41: {
			dir = includeDirective();
			break;
		}
		case 42: {
			dir = charsetDirective();
			break;
		}
		case 39: {
			dir = pageDirective();
			break;
		}
		case 43: {
			dir = namespaceDirective();
			break;
		}
		case 44: {
			dir = genericDirective();
			break;
		}
		default: SynErr(74); break;
		}
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
		Expect(6);
		url = t.val.substring(4, t.val.length() - 1); 
		return url;
	}

	Medium  medium() {
		Medium  m;
		m = Medium.all; 
		switch (la.kind) {
		case 9: {
			Get();
			m = Medium.all; 
			break;
		}
		case 10: {
			Get();
			m = Medium.aural; 
			break;
		}
		case 11: {
			Get();
			m = Medium.braille; 
			break;
		}
		case 12: {
			Get();
			m = Medium.embossed; 
			break;
		}
		case 13: {
			Get();
			m = Medium.handheld; 
			break;
		}
		case 14: {
			Get();
			m = Medium.print; 
			break;
		}
		case 15: {
			Get();
			m = Medium.projection; 
			break;
		}
		case 16: {
			Get();
			m = Medium.screen; 
			break;
		}
		case 17: {
			Get();
			m = Medium.tty; 
			break;
		}
		case 18: {
			Get();
			m = Medium.tv; 
			break;
		}
		default: SynErr(75); break;
		}
		return m;
	}

	String  identity() {
		String  ident;
		switch (la.kind) {
		case 1: {
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
		case 9: {
			Get();
			break;
		}
		case 10: {
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
		case 21: {
			Get();
			break;
		}
		case 22: {
			Get();
			break;
		}
		default: SynErr(76); break;
		}
		ident = t.val; 
		return ident;
	}

	String  atReference() {
		String  ident;
		Expect(2);
		ident = t.val.substring(1); 
		return ident;
	}

	MediaDirective  mediaDirective() {
		MediaDirective  mdir;
		List<Medium> media = new ArrayList<Medium>();
		List<Rule> rules = new ArrayList<Rule>();
		
		Expect(23);
		Medium m = medium();
		media.add(m); 
		while (la.kind == 24) {
			Get();
			m = medium();
			media.add(m); 
		}
		Expect(25);
		while (StartOf(5)) {
			if (StartOf(2)) {
				RuleSet rset = ruleset();
				rules.add(rset); 
			} else if (la.kind == 30) {
				ClassDirective cdir = classDirective();
				rules.add(cdir); 
			} else if (la.kind == 37) {
				DefineDirective ddir = defineDirective();
				rules.add(ddir); 
			} else {
				IncludeDirective idir = includeDirective();
				rules.add(idir); 
			}
		}
		Expect(26);
		mdir = new MediaDirective(media, rules); 
		return mdir;
	}

	ClassDirective  classDirective() {
		ClassDirective  dir;
		String ident;
		List<Declaration> parameters = new ArrayList<Declaration>();
		List<Declaration> declarations = new ArrayList<Declaration>();
		List<Declaration> mdecs;
		List<NestedRuleSet> nested = new ArrayList<NestedRuleSet>();
		Declaration param;
		Combinator cb;
		boolean global = false;
		
		Expect(30);
		ident = identity();
		if (la.kind == 31) {
			Get();
			if (StartOf(6)) {
				param = parameter();
				parameters.add(param); 
				while (la.kind == 32) {
					Get();
					param = parameter();
					parameters.add(param); 
				}
			}
			Expect(33);
		}
		if (la.kind == 22) {
			Get();
			global = true; 
		}
		Expect(25);
		while (StartOf(7)) {
			if (!isNestedSelector()) {
				mdecs = multideclaration();
				declarations.addAll(mdecs);
				if (endOfBlock()) {
				    break;
				}
				
				Expect(32);
			} else {
				cb = Combinator.Descendant; 
				if (la.kind == 34) {
					Get();
				}
				if (t.pos + t.val.length() == la.pos) {
				   cb = Combinator.None;
				}
				else {
				    cb = Combinator.Descendant;
				}
				
				if (la.kind == 33 || la.kind == 35 || la.kind == 36) {
					if (la.kind == 35) {
						Get();
						cb = Combinator.PrecededImmediatelyBy; 
					} else if (la.kind == 33) {
						Get();
						cb = Combinator.ChildOf; 
					} else {
						Get();
						cb = Combinator.PrecededBy; 
					}
				}
				RuleSet nest = ruleset();
				nested.add(new NestedRuleSet(cb, nest)); 
			}
		}
		Expect(26);
		dir = new ClassDirective(ident, parameters, global, declarations, nested); 
		return dir;
	}

	DefineDirective  defineDirective() {
		DefineDirective  dir;
		List<Declaration> declarations = new ArrayList<Declaration>();
		boolean global = false;
		List<Declaration> mdecs;
		
		Expect(37);
		if (la.kind == 22) {
			Get();
			global = true; 
		}
		Expect(25);
		while (StartOf(6)) {
			mdecs = multideclaration();
			declarations.addAll(mdecs);
			if (endOfBlock()) {
			    break;
			}
			
			Expect(32);
		}
		Expect(26);
		dir = new DefineDirective(declarations, global); 
		return dir;
	}

	IncludeDirective  includeDirective() {
		IncludeDirective  dir;
		dir = null; 
		Expect(41);
		if (la.kind == 6) {
			String url = URI();
			dir = new IncludeDirective(new UrlTerm(url)); 
		} else if (la.kind == 5) {
			String url = QuotedString();
			dir = new IncludeDirective(new UrlTerm(url.substring(1, url.length()-1))); 
		} else SynErr(77);
		Expect(32);
		return dir;
	}

	IfDirective  ifDirective() {
		IfDirective  idir;
		BooleanExpression expr;
		List<Rule> ifrules = new ArrayList<Rule>();
		List<Rule> elserules = null;
		
		Expect(27);
		expr = booleanExpression();
		Expect(25);
		while (StartOf(1)) {
			Rule rule = rule();
			ifrules.add(rule); 
		}
		Expect(26);
		if (la.kind == 28) {
			Get();
			elserules = new ArrayList<Rule>(); 
			Expect(25);
			while (StartOf(1)) {
				Rule rule = rule();
				elserules.add(rule); 
			}
			Expect(26);
		}
		idir = new IfDirective(expr, ifrules, elserules); 
		return idir;
	}

	BooleanExpression  booleanExpression() {
		BooleanExpression  expr;
		expr = orExpression();
		return expr;
	}

	Declaration  parameter() {
		Declaration  dec;
		dec = new Declaration(); 
		String ident = identity();
		dec.setName(ident); 
		if (la.kind == 29) {
			Get();
			Expression exp = expr();
			dec.setExpression(exp); 
		}
		return dec;
	}

	Expression  expr() {
		Expression  exp;
		exp = new Expression();
		Character sep = null;
		Term trm;
		
		trm = term();
		exp.getTerms().add(trm); 
		while (StartOf(8)) {
			if (la.kind == 24 || la.kind == 49 || la.kind == 61) {
				if (la.kind == 61) {
					Get();
					sep = '/'; 
				} else if (la.kind == 24) {
					Get();
					sep = ','; 
				} else {
					Get();
					sep = '='; 
				}
			}
			trm = term();
			if (sep != null) { trm.setSeperator(sep); }
			exp.getTerms().add(trm);
			sep = null;
			
		}
		return exp;
	}

	List<Declaration>  multideclaration() {
		List<Declaration>  decs;
		decs = new ArrayList<Declaration>();
		Declaration first = new Declaration();
		
		String ident = identity();
		first.setName(ident); decs.add(first); 
		while (la.kind == 24) {
			Get();
			String ident2 = identity();
			decs.add(new Declaration(ident2, new PropertyTerm(first.getName()).toExpression())); 
		}
		Expect(29);
		Expression exp = expr();
		first.setExpression(exp); 
		if (la.kind == 60) {
			Get();
			Expect(21);
			for (Declaration dec : decs) {
			   dec.setImportant(true);
			}
			
		}
		return decs;
	}

	FontFaceDirective  fontFaceDirective() {
		FontFaceDirective  dir;
		List<Declaration> declarations = new ArrayList<Declaration>();
		List<Declaration> mdecs;
		
		Expect(38);
		Expect(25);
		while (StartOf(6)) {
			mdecs = multideclaration();
			declarations.addAll(mdecs);
			if (endOfBlock()) {
			    break;
			}
			
			Expect(32);
		}
		Expect(26);
		dir = new FontFaceDirective(declarations); 
		return dir;
	}

	PageDirective  pageDirective() {
		PageDirective  dir;
		List<Declaration> declarations = new ArrayList<Declaration>();
		SimpleSelector ss = null;
		List<Declaration> mdecs;
		
		Expect(39);
		if (la.kind == 29) {
			String psd = pseudo();
			ss = new SimpleSelector();
			ss.setPseudo(psd);
			
		}
		Expect(25);
		while (StartOf(6)) {
			mdecs = multideclaration();
			declarations.addAll(mdecs);
			if (endOfBlock()) {
			    break;
			}
			
			Expect(32);
		}
		Expect(26);
		dir = new PageDirective(ss, declarations); 
		return dir;
	}

	String  pseudo() {
		String  pseudo;
		StringBuilder sb = new StringBuilder();
		boolean haveOpenParen = false;
		
		Expect(29);
		if (la.kind == 29) {
			Get();
			sb.append(":"); 
		}
		String ident = identity();
		sb.append(ident); 
		if (la.kind == 56) {
			Get();
			if (StartOf(9)) {
				if (la.kind == 35 || la.kind == 57) {
					if (la.kind == 35) {
						Get();
						sb.append("(").append(t.val);
						haveOpenParen = true; 
					} else {
						Get();
						sb.append("(").append(t.val);
						haveOpenParen = true; 
					}
				}
				if (la.kind == 3) {
					Get();
					if (!haveOpenParen) {
					   sb.append("(");
					   haveOpenParen = true;
					}
					sb.append(t.val); 
				}
				if (la.kind == 19 || la.kind == 58) {
					if (la.kind == 19) {
						Get();
					} else {
						Get();
					}
					if (!haveOpenParen) {
					   sb.append("(");
					   haveOpenParen = true;
					}
					sb.append(t.val); 
					if (la.kind == 35 || la.kind == 57) {
						if (la.kind == 35) {
							Get();
						} else {
							Get();
						}
						sb.append(t.val); 
						Expect(3);
						sb.append(t.val); 
					}
				}
				sb.append(")"); 
			} else if (StartOf(2)) {
				SimpleSelector ss = simpleselector();
				sb.append("(").append(ss).append(")"); 
			} else SynErr(78);
			Expect(59);
		}
		pseudo = sb.toString(); 
		return pseudo;
	}

	ImportDirective  importDirective() {
		ImportDirective  dir;
		Medium m = Medium.all;
		UrlTerm trm;
		
		Expect(40);
		String url = URI();
		trm = new UrlTerm(url); 
		if (StartOf(10)) {
			m = medium();
		}
		Expect(32);
		dir = new ImportDirective(trm, m); 
		return dir;
	}

	CharsetDirective  charsetDirective() {
		CharsetDirective  dir;
		Expect(42);
		Term trm = term();
		dir = new CharsetDirective(trm); 
		Expect(32);
		return dir;
	}

	Term  term() {
		Term  trm;
		String val = "";
		Expression exp;
		String ident;
		CalcExpression expression;
		trm = null;
		
		switch (la.kind) {
		case 5: {
			val = QuotedString();
			trm = new StringTerm(val); 
			break;
		}
		case 6: {
			val = URI();
			trm = new UrlTerm(val); 
			break;
		}
		case 66: {
			Get();
			Expect(56);
			ident = identity();
			Expect(59);
			trm = new ConstTerm(ident); 
			break;
		}
		case 67: {
			Get();
			Expect(56);
			ident = identity();
			Expect(59);
			trm = new ParamTerm(ident); 
			break;
		}
		case 68: {
			Get();
			Expect(56);
			ident = identity();
			Expect(59);
			trm = new PropertyTerm(ident); 
			break;
		}
		case 2: {
			ident = atReference();
			trm = new AtReferenceTerm(ident); 
			break;
		}
		case 69: {
			Get();
			Expect(56);
			Selector s = selector();
			Expect(59);
			trm = new RuleSetClassReferenceTerm(s); 
			break;
		}
		case 70: {
			Get();
			ident = identity();
			trm = new UnicodeTerm("U\\" + ident); 
			break;
		}
		case 46: {
			val = HexValue();
			trm = new HexTerm(val); 
			break;
		}
		case 48: case 65: {
			expression = calculation();
			trm = new CalculationTerm(expression); 
			break;
		}
		case 1: case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19: case 20: case 21: case 22: {
			ident = identity();
			trm = new StringTerm(ident); 
			while (la.kind == 29 || la.kind == 47) {
				if (la.kind == 29) {
					Get();
					((StringTerm) trm).setValue(trm.toString() + t.val); 
					if (la.kind == 29) {
						Get();
						((StringTerm) trm).setValue(trm.toString() + t.val); 
					}
					ident = identity();
					((StringTerm) trm).setValue(trm.toString() + ident); 
				} else {
					Get();
					((StringTerm) trm).setValue(trm.toString() + t.val); 
					ident = identity();
					((StringTerm) trm).setValue(trm.toString() + ident); 
				}
			}
			if (la.kind == 31 || la.kind == 56) {
				if (la.kind == 56) {
					Get();
					exp = expr();
					FunctionTerm func = new FunctionTerm();
					func.setName(trm.toString());
					func.setExpression(exp);
					trm = func;
					
					Expect(59);
				} else {
					Get();
					ClassReferenceTerm cls = new ClassReferenceTerm(trm.toString());
					Declaration dec;
					trm = cls;
					
					if (StartOf(11)) {
						if (isDeclaration()) {
							dec = declaration();
							cls.addArgument(dec); 
						} else {
							Expression arg = expr();
							cls.addArgument(new Declaration("", arg)); 
						}
						while (la.kind == 32) {
							Get();
							if (isDeclaration()) {
								dec = declaration();
								cls.addArgument(dec); 
							} else if (StartOf(11)) {
								Expression arg = expr();
								cls.addArgument(new Declaration("", arg)); 
							} else SynErr(79);
						}
					}
					Expect(33);
				}
			}
			break;
		}
		case 3: case 4: case 35: case 57: {
			if (la.kind == 35 || la.kind == 57) {
				if (la.kind == 57) {
					Get();
					val = "-"; 
				} else {
					Get();
					val = "+"; 
				}
			}
			if (la.kind == 3) {
				Get();
			} else if (la.kind == 4) {
				Get();
			} else SynErr(80);
			val += t.val; trm = new NumberTerm(Double.parseDouble(val)); 
			if (endOfBlock()) {
				((NumberTerm) trm).setValue(Double.parseDouble(val)); 
			} else if (StartOf(12)) {
				if (la.val.equalsIgnoreCase("n")) {
					Expect(19);
					val += t.val; 
					if (la.kind == 35 || la.kind == 57) {
						if (la.kind == 35) {
							Get();
							val += '+'; 
						} else {
							Get();
							val += '-'; 
						}
						Expect(3);
						val += t.val; 
					}
					trm = new StringTerm(val); val = ""; 
				} else if (la.kind == 71) {
					Get();
					((NumberTerm) trm).setUnit(Unit.Percent); 
				} else if (StartOf(13)) {
					if (isUnit()) {
						ident = identity();
						try {
						   // TODO: What if trm isn't a NumberTerm?
						   ((NumberTerm) trm).setUnit(Unit.parse(ident));
						} catch (IllegalArgumentException ex) {
						    errors.SemErr(t.line, t.col, "Unrecognized unit '" + ident + "'");
						}
						
					}
				} else SynErr(81);
				if (trm instanceof NumberTerm) {
				   ((NumberTerm) trm).setValue(Double.parseDouble(val));
				}
				else if (trm instanceof StringTerm) {
				    StringTerm strTrm = (StringTerm) trm;
				    strTrm.setValue(strTrm.getValue() + val);
				}
				
			} else SynErr(82);
			break;
		}
		default: SynErr(83); break;
		}
		return trm;
	}

	NamespaceDirective  namespaceDirective() {
		NamespaceDirective  dir;
		String ident = null;
		String url = null;
		
		Expect(43);
		if (StartOf(6)) {
			ident = identity();
		}
		if (la.kind == 6) {
			url = URI();
		} else if (la.kind == 5) {
			url = QuotedString();
		} else SynErr(84);
		Expect(32);
		dir = new NamespaceDirective(ident, new UrlTerm(url)); 
		return dir;
	}

	GenericDirective  genericDirective() {
		GenericDirective  dir;
		Expect(44);
		String ident = identity();
		dir = new GenericDirective();
		dir.setName("@" + ident);
		
		if (StartOf(11)) {
			if (StartOf(11)) {
				Expression exp = expr();
				dir.setExpression(exp); 
			} else {
				Medium m = medium();
				dir.addMedium(m); 
			}
		}
		if (la.kind == 25) {
			Get();
			while (StartOf(1)) {
				if (StartOf(6)) {
					Declaration dec = declaration();
					dir.addDeclaration(dec);
					if (endOfBlock()) {
					    break;
					}
					
					Expect(32);
				} else if (StartOf(2)) {
					RuleSet rset = ruleset();
					dir.addRule(rset); 
				} else {
					Rule dr = directive();
					dir.addRule(dr); 
				}
			}
			Expect(26);
		} else if (la.kind == 32) {
			Get();
		} else SynErr(85);
		return dir;
	}

	Declaration  declaration() {
		Declaration  dec;
		dec = new Declaration(); 
		String ident = identity();
		dec.setName(ident); 
		Expect(29);
		Expression exp = expr();
		dec.setExpression(exp); 
		if (la.kind == 60) {
			Get();
			Expect(21);
			dec.setImportant(true); 
		}
		return dec;
	}

	Selector  selector() {
		Selector  sel;
		sel = new Selector();
		SimpleSelector ss;
		Combinator cb = Combinator.Descendant;
		
		ss = simpleselector();
		sel.getSimpleSelectors().add(ss); 
		while (StartOf(14)) {
			if (la.kind == 33 || la.kind == 35 || la.kind == 36) {
				if (la.kind == 35) {
					Get();
					cb = Combinator.PrecededImmediatelyBy; 
				} else if (la.kind == 33) {
					Get();
					cb = Combinator.ChildOf; 
				} else {
					Get();
					cb = Combinator.PrecededBy; 
				}
			}
			ss = simpleselector();
			ss.setCombinator(cb);
			sel.getSimpleSelectors().add(ss);
			cb = Combinator.Descendant;
			
		}
		return sel;
	}

	SimpleSelector  simpleselector() {
		SimpleSelector  ss;
		ss = new SimpleSelector();
		SimpleSelector parent = ss;
		String ident;
		
		if (StartOf(6)) {
			ident = identity();
			ss.setElementName(ident); 
		} else if (la.kind == 45) {
			Get();
			ss.setElementName("*"); 
		} else if (StartOf(15)) {
			if (la.kind == 46) {
				Get();
				ident = identity();
				ss.setID(ident); 
			} else if (la.kind == 47) {
				Get();
				ident = identity();
				ss.setClassName(ident); 
			} else if (la.kind == 48) {
				Attribute atb = attrib();
				ss.setAttribute(atb); 
			} else {
				String psd = pseudo();
				ss.setPseudo(psd); 
			}
		} else SynErr(86);
		while (StartOf(15)) {
			if (t.pos + t.val.length() < la.pos) {
			   break;
			}
			SimpleSelector child = new SimpleSelector();
			
			if (la.kind == 46) {
				Get();
				ident = identity();
				child.setID(ident); 
			} else if (la.kind == 47) {
				Get();
				ident = identity();
				child.setClassName(ident); 
			} else if (la.kind == 48) {
				Attribute atb = attrib();
				child.setAttribute(atb); 
			} else {
				String psd = pseudo();
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
		String quote;
		String ident;
		
		Expect(48);
		ident = identity();
		atb.setOperand(ident); 
		if (StartOf(16)) {
			switch (la.kind) {
			case 49: {
				Get();
				atb.setOperator(AttributeOperator.Equals); 
				break;
			}
			case 50: {
				Get();
				atb.setOperator(AttributeOperator.InList); 
				break;
			}
			case 51: {
				Get();
				atb.setOperator(AttributeOperator.Hyphenated); 
				break;
			}
			case 52: {
				Get();
				atb.setOperator(AttributeOperator.EndsWith); 
				break;
			}
			case 53: {
				Get();
				atb.setOperator(AttributeOperator.BeginsWith); 
				break;
			}
			case 54: {
				Get();
				atb.setOperator(AttributeOperator.Contains); 
				break;
			}
			}
			if (StartOf(6)) {
				ident = identity();
				atb.setValue(ident); 
			} else if (la.kind == 5) {
				quote = QuotedString();
				atb.setValue(quote); 
			} else if (la.kind == 3 || la.kind == 4) {
				if (la.kind == 3) {
					Get();
				} else {
					Get();
				}
				atb.setValue(t.val); 
			} else SynErr(87);
		}
		Expect(55);
		return atb;
	}

	BooleanExpression  orExpression() {
		BooleanExpression  expr;
		BooleanExpression left, right; BooleanOperation op; 
		left = andExpression();
		expr = left; 
		while (la.kind == 62 || la.kind == 63) {
			op = orop();
			right = andExpression();
			expr = new BinaryBooleanExpression(op, expr, right); 
		}
		return expr;
	}

	BooleanOperation  orop() {
		BooleanOperation  op;
		op = null; 
		if (la.kind == 62) {
			Get();
			op = BooleanOperation.OR; 
		} else if (la.kind == 63) {
			Get();
			op = BooleanOperation.XOR; 
		} else SynErr(88);
		return op;
	}

	BooleanExpression  andExpression() {
		BooleanExpression  expr;
		BooleanExpression left, right;
		left = notExpression();
		expr = left; 
		while (la.kind == 64) {
			Get();
			right = notExpression();
			expr = new BinaryBooleanExpression(BooleanOperation.AND, expr, right); 
		}
		return expr;
	}

	BooleanExpression  notExpression() {
		BooleanExpression  expr;
		BooleanExpression exp; expr = null; 
		if (StartOf(17)) {
			exp = primaryBooleanExpression();
			expr = exp; 
		} else if (la.kind == 60) {
			Get();
			exp = notExpression();
			expr = new NotExpression(exp); 
		} else SynErr(89);
		return expr;
	}

	BooleanExpression  primaryBooleanExpression() {
		BooleanExpression  expr;
		expr = null; Term trm; BooleanExpression exp; 
		if (la.kind == 56) {
			Get();
			exp = booleanExpression();
			Expect(59);
			expr = exp; 
		} else if (StartOf(11)) {
			trm = term();
			expr = new TermBooleanExpression(trm); 
		} else SynErr(90);
		return expr;
	}

	CalcExpression  lengthExpression() {
		CalcExpression  expr;
		expr = additiveExpression();
		return expr;
	}

	CalcExpression  additiveExpression() {
		CalcExpression  expr;
		CalcExpression left, right; Operation op; 
		left = multiplicativeExpression();
		expr = left; 
		while (la.kind == 35 || la.kind == 57) {
			op = addop();
			right = multiplicativeExpression();
			expr = new BinaryExpression(op, expr, right); 
		}
		return expr;
	}

	Operation  addop() {
		Operation  op;
		op = null; 
		if (la.kind == 35) {
			Get();
			op = Operation.Add; 
		} else if (la.kind == 57) {
			Get();
			op = Operation.Subtract; 
		} else SynErr(91);
		return op;
	}

	Operation  mulop() {
		Operation  op;
		op = null; 
		if (la.kind == 45) {
			Get();
			op = Operation.Multiply; 
		} else if (la.kind == 61) {
			Get();
			op = Operation.Divide; 
		} else SynErr(92);
		return op;
	}

	CalcExpression  multiplicativeExpression() {
		CalcExpression  expr;
		CalcExpression left, right; Operation op; 
		left = termExpression();
		expr = left; 
		while (la.kind == 45 || la.kind == 61) {
			op = mulop();
			right = termExpression();
			expr = new BinaryExpression(op, expr, right); 
		}
		return expr;
	}

	CalcExpression  termExpression() {
		CalcExpression  expr;
		expr = null; Term trm; CalcExpression exp; 
		if (la.kind == 57) {
			Get();
			exp = termExpression();
			expr = new NegationExpression(exp); 
		} else if (la.kind == 56) {
			Get();
			exp = lengthExpression();
			Expect(59);
			expr = exp; 
		} else if (StartOf(11)) {
			trm = term();
			expr = new TermExpression(trm); 
		} else SynErr(93);
		return expr;
	}

	CalcExpression  calculation() {
		CalcExpression  expr;
		expr = null; 
		if (la.kind == 65) {
			Get();
			Expect(56);
			expr = lengthExpression();
			Expect(59);
		} else if (la.kind == 48) {
			Get();
			expr = lengthExpression();
			Expect(55);
		} else SynErr(94);
		return expr;
	}

	String  HexValue() {
		String  val;
		StringBuilder sb = new StringBuilder();
		boolean found = false;
		
		Expect(46);
		sb.append(t.val); 
		if (la.kind == 3) {
			Get();
			sb.append(t.val); 
		} else if (la.kind == 1) {
			Get();
			sb.append(t.val); found = true; 
		} else SynErr(95);
		if (!found && partOfHex(sb.toString())) {
			Expect(1);
			sb.append(t.val); 
		}
		val = sb.toString(); 
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
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x,x,T, x,T,T,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,T, x,x,T,x, x,x,x,x, x,T,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,T,T,x, x,T,T,T, T,T,x,x, x,x,x,x, x,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,T,T,x, x,x,x,x, x,T,x,x, x,T,x,x, x,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,T,x,x, x,T,T,T, T,x,x,x, x,x,x,x, x,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, T,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,T,x, T,T,x,x, x,x,x,x, x,T,x,x, x,T,x,x, x,T,T,T, T,T,T,x, x,x},
		{x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,T,x, T,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,x, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, T,T,x,x, x,x,x,x, T,T,x,T, x,x,x,x, x,x,x,x, x,T,T,x, T,T,x,x, x,x,x,T, x,T,x,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, T,T,x,x, x,x,x,x, T,T,x,T, x,x,x,x, x,x,x,x, x,T,T,x, T,T,x,x, x,x,x,T, x,T,x,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,T,x,x, x,T,x,T, T,x,x,x, x,x,x,x, x,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,T,x, T,x,x,x, x,x,x,x, T,T,x,x, x,x,x,x, x,T,T,T, T,T,T,x, x,x}

	};

	public static final String getErrorMessage(int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "ident expected"; break;
			case 2: s = "atref expected"; break;
			case 3: s = "integer expected"; break;
			case 4: s = "decimal expected"; break;
			case 5: s = "stringLit expected"; break;
			case 6: s = "url expected"; break;
			case 7: s = "\"<!--\" expected"; break;
			case 8: s = "\"-->\" expected"; break;
			case 9: s = "\"all\" expected"; break;
			case 10: s = "\"aural\" expected"; break;
			case 11: s = "\"braille\" expected"; break;
			case 12: s = "\"embossed\" expected"; break;
			case 13: s = "\"handheld\" expected"; break;
			case 14: s = "\"print\" expected"; break;
			case 15: s = "\"projection\" expected"; break;
			case 16: s = "\"screen\" expected"; break;
			case 17: s = "\"tty\" expected"; break;
			case 18: s = "\"tv\" expected"; break;
			case 19: s = "\"n\" expected"; break;
			case 20: s = "\"url\" expected"; break;
			case 21: s = "\"important\" expected"; break;
			case 22: s = "\"global\" expected"; break;
			case 23: s = "\"@media\" expected"; break;
			case 24: s = "\",\" expected"; break;
			case 25: s = "\"{\" expected"; break;
			case 26: s = "\"}\" expected"; break;
			case 27: s = "\"@if\" expected"; break;
			case 28: s = "\"@else\" expected"; break;
			case 29: s = "\":\" expected"; break;
			case 30: s = "\"@class\" expected"; break;
			case 31: s = "\"<\" expected"; break;
			case 32: s = "\";\" expected"; break;
			case 33: s = "\">\" expected"; break;
			case 34: s = "\"&\" expected"; break;
			case 35: s = "\"+\" expected"; break;
			case 36: s = "\"~\" expected"; break;
			case 37: s = "\"@define\" expected"; break;
			case 38: s = "\"@font-face\" expected"; break;
			case 39: s = "\"@page\" expected"; break;
			case 40: s = "\"@import\" expected"; break;
			case 41: s = "\"@include\" expected"; break;
			case 42: s = "\"@charset\" expected"; break;
			case 43: s = "\"@namespace\" expected"; break;
			case 44: s = "\"@\" expected"; break;
			case 45: s = "\"*\" expected"; break;
			case 46: s = "\"#\" expected"; break;
			case 47: s = "\".\" expected"; break;
			case 48: s = "\"[\" expected"; break;
			case 49: s = "\"=\" expected"; break;
			case 50: s = "\"~=\" expected"; break;
			case 51: s = "\"|=\" expected"; break;
			case 52: s = "\"$=\" expected"; break;
			case 53: s = "\"^=\" expected"; break;
			case 54: s = "\"*=\" expected"; break;
			case 55: s = "\"]\" expected"; break;
			case 56: s = "\"(\" expected"; break;
			case 57: s = "\"-\" expected"; break;
			case 58: s = "\"-n\" expected"; break;
			case 59: s = "\")\" expected"; break;
			case 60: s = "\"!\" expected"; break;
			case 61: s = "\"/\" expected"; break;
			case 62: s = "\"||\" expected"; break;
			case 63: s = "\"^\" expected"; break;
			case 64: s = "\"&&\" expected"; break;
			case 65: s = "\"calc\" expected"; break;
			case 66: s = "\"const\" expected"; break;
			case 67: s = "\"param\" expected"; break;
			case 68: s = "\"prop\" expected"; break;
			case 69: s = "\"ruleset\" expected"; break;
			case 70: s = "\"U\\\\\" expected"; break;
			case 71: s = "\"%\" expected"; break;
			case 72: s = "??? expected"; break;
			case 73: s = "invalid rule"; break;
			case 74: s = "invalid directive"; break;
			case 75: s = "invalid medium"; break;
			case 76: s = "invalid identity"; break;
			case 77: s = "invalid includeDirective"; break;
			case 78: s = "invalid pseudo"; break;
			case 79: s = "invalid term"; break;
			case 80: s = "invalid term"; break;
			case 81: s = "invalid term"; break;
			case 82: s = "invalid term"; break;
			case 83: s = "invalid term"; break;
			case 84: s = "invalid namespaceDirective"; break;
			case 85: s = "invalid genericDirective"; break;
			case 86: s = "invalid simpleselector"; break;
			case 87: s = "invalid attrib"; break;
			case 88: s = "invalid orop"; break;
			case 89: s = "invalid notExpression"; break;
			case 90: s = "invalid primaryBooleanExpression"; break;
			case 91: s = "invalid addop"; break;
			case 92: s = "invalid mulop"; break;
			case 93: s = "invalid termExpression"; break;
			case 94: s = "invalid calculation"; break;
			case 95: s = "invalid HexValue"; break;
			default: s = "error " + n; break;
		}
		return s;
	}
} // end Parser


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}

