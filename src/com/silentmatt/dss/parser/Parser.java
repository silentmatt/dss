package com.silentmatt.dss.parser;

import com.silentmatt.dss.*;
import com.silentmatt.dss.bool.*;
import com.silentmatt.dss.calc.*;
import com.silentmatt.dss.directive.*;
import com.silentmatt.dss.term.*;
import java.util.*;

class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _atref = 2;
	public static final int _integer = 3;
	public static final int _decimal = 4;
	public static final int _stringLit = 5;
	public static final int _url = 6;
	public static final int maxT = 80;

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
            List<String> units = Arrays.asList(new String[] { "em", "ex", "px", "gd", "rem", "vw", "vh", "vm", "ch", "mm", "cm", "in", "pt", "pc", "deg", "grad", "rad", "turn", "ms", "s", "hz", "khz", "dpi", "dpcm" });            return units.contains(la.val);
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
		} else SynErr(81);
		return rule;
	}

	RuleSet  ruleset() {
		RuleSet  rset;
		RuleSet.Builder rsetb = new RuleSet.Builder();
		Selector sel;
		List<Declaration> decs;
		Rule dir;
		Combinator cb;
		
		sel = selector();
		rsetb.addSelector(sel); 
		while (la.kind == 36) {
			Get();
			sel = selector();
			rsetb.addSelector(sel); 
		}
		DeclarationBlock block = declarationBlock();
		rsetb.setDeclarationBlock(block);
		rset = rsetb.build();
		
		return rset;
	}

	Rule  directive() {
		Rule  dir;
		dir = null; 
		switch (la.kind) {
		case 39: {
			dir = ifDirective();
			break;
		}
		case 35: {
			dir = mediaDirective();
			break;
		}
		case 46: {
			dir = classDirective();
			break;
		}
		case 48: {
			dir = defineDirective();
			break;
		}
		case 49: {
			dir = fontFaceDirective();
			break;
		}
		case 51: {
			dir = importDirective();
			break;
		}
		case 52: {
			dir = includeDirective();
			break;
		}
		case 53: {
			dir = charsetDirective();
			break;
		}
		case 50: {
			dir = pageDirective();
			break;
		}
		case 54: {
			dir = namespaceDirective();
			break;
		}
		case 2: {
			dir = genericDirective();
			break;
		}
		default: SynErr(82); break;
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
		default: SynErr(83); break;
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
		case 23: {
			Get();
			break;
		}
		case 24: {
			Get();
			break;
		}
		case 25: {
			Get();
			break;
		}
		case 26: {
			Get();
			break;
		}
		case 27: {
			Get();
			break;
		}
		case 28: {
			Get();
			break;
		}
		case 29: {
			Get();
			break;
		}
		case 30: {
			Get();
			break;
		}
		case 31: {
			Get();
			break;
		}
		default: SynErr(84); break;
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

	MediaQuery  mediaQuery() {
		MediaQuery  query;
		String expr, q = ""; 
		if (StartOf(4)) {
			if (la.kind == 24 || la.kind == 25) {
				if (la.kind == 24) {
					Get();
					q = "only "; 
				} else {
					Get();
					q = "not "; 
				}
			}
			String type = identity();
			q += type; 
			while (la.kind == 26) {
				Get();
				expr = mediaExpression();
				q += " and " + expr; 
			}
		} else if (la.kind == 32) {
			expr = mediaExpression();
			q += expr; 
			while (la.kind == 26) {
				Get();
				expr = mediaExpression();
				q += " and " + expr; 
			}
		} else SynErr(85);
		query = new MediaQuery(q); 
		return query;
	}

	String  mediaExpression() {
		String  expr;
		Expect(32);
		String e = identity();
		expr = e; 
		if (la.kind == 33) {
			Get();
			Expression exp = expr();
			expr += ":" + exp; 
		}
		Expect(34);
		expr = "(" + expr + ")"; 
		return expr;
	}

	Expression  expr() {
		Expression  exp;
		Expression.Builder expb = new Expression.Builder();
		Character sep = null;
		Term trm;
		
		trm = term();
		expb.addTerm(trm); 
		while (StartOf(5)) {
			if (la.kind == 36 || la.kind == 59 || la.kind == 69) {
				if (la.kind == 69) {
					Get();
					sep = '/'; 
				} else if (la.kind == 36) {
					Get();
					sep = ','; 
				} else {
					Get();
					sep = '='; 
				}
			}
			trm = term();
			if (sep != null) { trm = trm.withSeparator(sep); }
			expb.addTerm(trm);
			sep = null;
			
		}
		exp = expb.build(); 
		return exp;
	}

	MediaDirective  mediaDirective() {
		MediaDirective  mdir;
		List<MediaQuery> media = new ArrayList<MediaQuery>();
		List<Rule> rules = new ArrayList<Rule>();
		
		Expect(35);
		MediaQuery m = mediaQuery();
		media.add(m); 
		while (la.kind == 36) {
			Get();
			m = mediaQuery();
			media.add(m); 
		}
		Expect(37);
		while (StartOf(6)) {
			if (StartOf(2)) {
				RuleSet rset = ruleset();
				rules.add(rset); 
			} else if (la.kind == 46) {
				ClassDirective cdir = classDirective();
				rules.add(cdir); 
			} else if (la.kind == 48) {
				DefineDirective ddir = defineDirective();
				rules.add(ddir); 
			} else {
				IncludeDirective idir = includeDirective();
				rules.add(idir); 
			}
		}
		Expect(38);
		mdir = new MediaDirective(media, rules); 
		return mdir;
	}

	ClassDirective  classDirective() {
		ClassDirective  dir;
		String ident;
		List<Declaration> parameters = new ArrayList<Declaration>();
		Declaration param;
		boolean global = false;
		
		Expect(46);
		ident = identity();
		if (la.kind == 47) {
			Get();
			if (StartOf(4)) {
				param = parameter();
				parameters.add(param); 
				while (la.kind == 41) {
					Get();
					param = parameter();
					parameters.add(param); 
				}
			}
			Expect(44);
		}
		if (la.kind == 22) {
			Get();
			global = true; 
		}
		DeclarationBlock block = declarationBlock();
		dir = new ClassDirective(ident, new DeclarationList(parameters), global, block); 
		return dir;
	}

	DefineDirective  defineDirective() {
		DefineDirective  dir;
		List<Declaration> declarations = new ArrayList<Declaration>();
		boolean global = false;
		List<Declaration> mdecs;
		
		Expect(48);
		if (la.kind == 22) {
			Get();
			global = true; 
		}
		Expect(37);
		while (StartOf(4)) {
			mdecs = multideclaration();
			declarations.addAll(mdecs);
			if (endOfBlock()) {
			    break;
			}
			
			Expect(41);
		}
		Expect(38);
		dir = new DefineDirective(new DeclarationList(declarations), global, BooleanExpression.TRUE); 
		return dir;
	}

	IncludeDirective  includeDirective() {
		IncludeDirective  dir;
		boolean literal = false;
		List<Declaration> declarations = new ArrayList<Declaration>();
		UrlTerm includeUrl = null;
		List<Declaration> mdecs;
		
		Expect(52);
		if (la.kind == 27) {
			Get();
			literal = true; 
		}
		if (la.kind == 6) {
			String url = URI();
			includeUrl = new UrlTerm(url); 
		} else if (la.kind == 5) {
			String url = QuotedString();
			includeUrl = new UrlTerm(url.substring(1, url.length()-1)); 
		} else SynErr(86);
		if (la.kind == 41) {
			Get();
		} else if (la.kind == 37) {
			Get();
			while (StartOf(4)) {
				mdecs = multideclaration();
				declarations.addAll(mdecs);
				if (endOfBlock()) {
				    break;
				}
				
				Expect(41);
			}
			Expect(38);
		} else SynErr(87);
		dir = new IncludeDirective(includeUrl, literal, declarations); 
		return dir;
	}

	IfDirective  ifDirective() {
		IfDirective  idir;
		BooleanExpression expr;
		List<Rule> ifrules = new ArrayList<Rule>();
		List<Rule> elserules = null;
		
		Expect(39);
		expr = booleanExpression();
		Expect(37);
		while (StartOf(1)) {
			Rule rule = rule();
			ifrules.add(rule); 
		}
		Expect(38);
		if (la.kind == 40) {
			Get();
			elserules = new ArrayList<Rule>(); 
			Expect(37);
			while (StartOf(1)) {
				Rule rule = rule();
				elserules.add(rule); 
			}
			Expect(38);
		}
		idir = new IfDirective(expr, ifrules, elserules); 
		return idir;
	}

	BooleanExpression  booleanExpression() {
		BooleanExpression  expr;
		expr = orExpression();
		return expr;
	}

	DeclarationBlock  ifDeclarations() {
		DeclarationBlock  mdecs;
		BooleanExpression expr;
		BooleanExpression elseExpr;
		List<Declaration> decList = new ArrayList<Declaration>();
		List<Declaration> decs;
		DeclarationBlock ifBlock;
		DeclarationBlock elseBlock;
		List<NestedRuleSet> nestedRuleSets = new ArrayList<NestedRuleSet>();
		List<Rule> rules = new ArrayList<Rule>();
		
		Expect(39);
		expr = booleanExpression();
		elseExpr = new NotExpression(expr); 
		ifBlock = declarationBlock();
		for (Declaration dec : ifBlock.getDeclarations()) {
		   decList.add(dec.withCondition(expr));
		}
		for (NestedRuleSet rule : ifBlock.getNestedRuleSets()) {
		    nestedRuleSets.add(rule.withCondition(expr));
		}
		for (Rule rule : ifBlock.getRules()) {
		    if (rule instanceof DefineDirective) {
		        rules.add(((DefineDirective)rule).withCondition(expr));
		    }
		    else {
		        Expect(38);
		    }
		}
		
		if (la.kind == 40) {
			Get();
			elseBlock = declarationBlock();
			for (Declaration dec : elseBlock.getDeclarations()) {
			   decList.add(dec.withCondition(elseExpr));
			}
			for (NestedRuleSet rule : elseBlock.getNestedRuleSets()) {
			    nestedRuleSets.add(rule.withCondition(elseExpr));
			}
			for (Rule rule : elseBlock.getRules()) {
			    if (rule instanceof DefineDirective) {
			        rules.add(((DefineDirective)rule).withCondition(elseExpr));
			    }
			    else {
			        Expect(38);
			    }
			}
			
		}
		mdecs = new DeclarationBlock(new DeclarationList(decList), nestedRuleSets, rules); 
		return mdecs;
	}

	DeclarationBlock  declarationBlock() {
		DeclarationBlock  block;
		DeclarationBlock.Builder builder = new DeclarationBlock.Builder();
		List<Declaration> decs;
		DeclarationBlock ifdecs;
		
		Expect(37);
		while (StartOf(7)) {
			if (!isNestedSelector()) {
				decs = multideclaration();
				builder.addDeclarations(decs);
				if (endOfBlock()) {
				    break;
				}
				
				Expect(41);
			} else if (la.kind == 39) {
				ifdecs = ifDeclarations();
				builder.addDeclarations(ifdecs.getDeclarations().toList());
				builder.addNestedRuleSets(ifdecs.getNestedRuleSets());
				builder.addRules(ifdecs.getRules());
				
			} else if (la.kind == 46) {
				Rule dir = classDirective();
				builder.addRule(dir); 
			} else if (la.kind == 48) {
				Rule dir = defineDirective();
				builder.addRule(dir); 
			} else {
				Combinator cb = Combinator.Descendant; 
				if (la.kind == 42) {
					Get();
				}
				if (t.pos + t.val.length() == la.pos) {
				   cb = Combinator.None;
				}
				
				if (la.kind == 43 || la.kind == 44 || la.kind == 45) {
					if (la.kind == 43) {
						Get();
						cb = Combinator.PrecededImmediatelyBy; 
					} else if (la.kind == 44) {
						Get();
						cb = Combinator.ChildOf; 
					} else {
						Get();
						cb = Combinator.PrecededBy; 
					}
				}
				RuleSet nested = ruleset();
				builder.addNestedRuleSet(cb, nested, BooleanExpression.TRUE); 
			}
		}
		Expect(38);
		block = builder.build(); 
		return block;
	}

	Declaration  parameter() {
		Declaration  dec;
		Declaration.Builder decb = new Declaration.Builder(); 
		String ident = identity();
		decb.setName(ident); 
		if (la.kind == 33) {
			Get();
			Expression exp = expr();
			decb.setExpression(exp); 
		}
		dec = decb.build(); 
		return dec;
	}

	List<Declaration>  multideclaration() {
		List<Declaration>  decs;
		ArrayList<Declaration.Builder> decbs = new ArrayList<Declaration.Builder>();
		Declaration.Builder first = new Declaration.Builder();
		
		String ident = identity();
		first.setName(ident); decbs.add(first); 
		while (la.kind == 36) {
			Get();
			String ident2 = identity();
			decbs.add(new Declaration.Builder().setName(ident2).setExpression(new PropertyTerm(first.getName()).toExpression())); 
		}
		Expect(33);
		Expression exp = expr();
		first.setExpression(exp); 
		if (la.kind == 68) {
			Get();
			Expect(21);
			for (Declaration.Builder decb : decbs) {
			   decb.setImportant(true);
			}
			
		}
		decs = new ArrayList<Declaration>(decbs.size());
		for (Declaration.Builder decb : decbs) {
		    decs.add(decb.build());
		} 
		return decs;
	}

	FontFaceDirective  fontFaceDirective() {
		FontFaceDirective  dir;
		List<Declaration> declarations = new ArrayList<Declaration>();
		List<Declaration> mdecs;
		
		Expect(49);
		Expect(37);
		while (StartOf(4)) {
			mdecs = multideclaration();
			declarations.addAll(mdecs);
			if (endOfBlock()) {
			    break;
			}
			
			Expect(41);
		}
		Expect(38);
		dir = new FontFaceDirective(new DeclarationList(declarations)); 
		return dir;
	}

	PageDirective  pageDirective() {
		PageDirective  dir;
		List<Declaration> declarations = new ArrayList<Declaration>();
		SimpleSelector ss = null;
		List<Declaration> mdecs;
		
		Expect(50);
		if (la.kind == 33) {
			String psd = pseudo();
			ss = new SimpleSelector();
			ss = ss.withPseudo(psd);
			
		}
		Expect(37);
		while (StartOf(4)) {
			mdecs = multideclaration();
			declarations.addAll(mdecs);
			if (endOfBlock()) {
			    break;
			}
			
			Expect(41);
		}
		Expect(38);
		dir = new PageDirective(ss, new DeclarationList(declarations)); 
		return dir;
	}

	String  pseudo() {
		String  pseudo;
		StringBuilder sb = new StringBuilder();
		boolean haveOpenParen = false;
		
		Expect(33);
		if (la.kind == 33) {
			Get();
			sb.append(":"); 
		}
		if (StartOf(4)) {
			String ident = identity();
			sb.append(ident); 
		} else if (la.kind == 25) {
			Get();
			sb.append("not"); 
		} else SynErr(88);
		if (la.kind == 32) {
			Get();
			if (StartOf(8)) {
				if (la.kind == 43 || la.kind == 66) {
					if (la.kind == 43) {
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
				if (la.kind == 19 || la.kind == 67) {
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
					if (la.kind == 43 || la.kind == 66) {
						if (la.kind == 43) {
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
			} else SynErr(89);
			Expect(34);
		}
		pseudo = sb.toString(); 
		return pseudo;
	}

	ImportDirective  importDirective() {
		ImportDirective  dir;
		MediaQuery m = new MediaQuery("all");
		UrlTerm trm;
		
		Expect(51);
		String url = URI();
		trm = new UrlTerm(url); 
		if (StartOf(9)) {
			m = mediaQuery();
		}
		Expect(41);
		dir = new ImportDirective(trm, m); 
		return dir;
	}

	CharsetDirective  charsetDirective() {
		CharsetDirective  dir;
		Expect(53);
		Term trm = term();
		dir = new CharsetDirective(trm); 
		Expect(41);
		return dir;
	}

	Term  term() {
		Term  trm;
		String val = "";
		Expression exp;
		String ident;
		CalcExpression expression;
		trm = null;
		
		if (la.kind == 5) {
			val = QuotedString();
			trm = new StringTerm(val); 
		} else if (la.kind == 6) {
			val = URI();
			trm = new UrlTerm(val); 
		} else if (la.kind == 28) {
			Get();
			Expect(32);
			ident = identity();
			Expect(34);
			trm = new ConstTerm(ident); 
		} else if (la.kind == 29) {
			Get();
			Expect(32);
			ident = identity();
			Expect(34);
			trm = new ParamTerm(ident); 
		} else if (la.kind == 30) {
			Get();
			Expect(32);
			ident = identity();
			Expect(34);
			trm = new PropertyTerm(ident); 
		} else if (la.kind == 2) {
			ident = atReference();
			trm = new AtReferenceTerm(ident); 
		} else if (la.kind == 31) {
			Get();
			Expect(32);
			Selector s = selector();
			Expect(34);
			trm = new RuleSetClassReferenceTerm(s); 
		} else if (la.kind == 78) {
			Get();
			ident = identity();
			trm = new UnicodeTerm("U\\" + ident); 
		} else if (la.kind == 56) {
			val = HexValue();
			trm = new HexTerm(val); 
		} else if (la.kind == 58 || la.kind == 77) {
			expression = calculation();
			trm = new CalculationTerm(expression); 
		} else if (StartOf(10)) {
			trm = literalCalculation();
		} else if (StartOf(4)) {
			ident = identity();
			String trmValue = ident; 
			while (la.kind == 33 || la.kind == 57) {
				if (la.kind == 33) {
					Get();
					trmValue += t.val; 
					if (la.kind == 33) {
						Get();
						trmValue += t.val; 
					}
					ident = identity();
					trmValue += ident; 
				} else {
					Get();
					trmValue += t.val; 
					ident = identity();
					trmValue += ident; 
				}
			}
			trm = new StringTerm(trmValue); 
			if (la.kind == 32 || la.kind == 47) {
				if (la.kind == 32) {
					Get();
					exp = expr();
					trm = new FunctionTerm(trm.toString(), exp); 
					Expect(34);
				} else {
					Get();
					ClassReferenceTerm.Builder cls = new ClassReferenceTerm.Builder(trm.toString());
					Declaration dec;
					
					if (StartOf(11)) {
						if (isDeclaration()) {
							dec = declaration();
							cls.addArgument(dec); 
						} else {
							Expression arg = expr();
							cls.addArgument(new Declaration("", arg)); 
						}
						while (la.kind == 41) {
							Get();
							if (isDeclaration()) {
								dec = declaration();
								cls.addArgument(dec); 
							} else if (StartOf(11)) {
								Expression arg = expr();
								cls.addArgument(new Declaration("", arg)); 
							} else SynErr(90);
						}
					}
					Expect(44);
					trm = cls.build(); 
				}
			}
		} else if (StartOf(12)) {
			if (la.kind == 43 || la.kind == 66) {
				if (la.kind == 66) {
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
			} else SynErr(91);
			val += t.val; trm = new NumberTerm(Double.parseDouble(val)); 
			if (endOfBlock()) {
				trm = ((NumberTerm) trm).withValue(Double.parseDouble(val)); 
			} else if (StartOf(13)) {
				if (la.val.equalsIgnoreCase("n")) {
					Expect(19);
					val += t.val; 
					if (la.kind == 43 || la.kind == 66) {
						if (la.kind == 43) {
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
				} else if (la.kind == 79) {
					Get();
					trm = ((NumberTerm) trm).withUnit(Unit.Percent); 
				} else if (StartOf(14)) {
					if (isUnit()) {
						ident = identity();
						try {
						   // TODO: What if trm isn't a NumberTerm?
						   trm = ((NumberTerm) trm).withUnit(Unit.parse(ident));
						} catch (IllegalArgumentException ex) {
						    errors.SemErr(t.line, t.col, "Unrecognized unit '" + ident + "'");
						}
						
					}
				} else SynErr(92);
				if (trm instanceof NumberTerm) {
				   trm = ((NumberTerm) trm).withValue(Double.parseDouble(val));
				}
				else if (trm instanceof StringTerm) {
				    trm = new StringTerm(((StringTerm)trm).getValue() + val);
				}
				
			} else SynErr(93);
		} else SynErr(94);
		return trm;
	}

	NamespaceDirective  namespaceDirective() {
		NamespaceDirective  dir;
		String ident = null;
		String url = null;
		
		Expect(54);
		if (StartOf(4)) {
			ident = identity();
		}
		if (la.kind == 6) {
			url = URI();
		} else if (la.kind == 5) {
			url = QuotedString();
		} else SynErr(95);
		Expect(41);
		dir = new NamespaceDirective(ident, new UrlTerm(url)); 
		return dir;
	}

	GenericDirective  genericDirective() {
		GenericDirective  dir;
		String ident = atReference();
		GenericDirective.Builder dirb = new GenericDirective.Builder();
		dirb.setName("@" + ident);
		
		if (StartOf(11)) {
			if (StartOf(11)) {
				Expression exp = expr();
				dirb.setExpression(exp); 
			} else {
				Medium m = medium();
				dirb.addMedium(m); 
			}
		}
		if (la.kind == 37) {
			Get();
			while (StartOf(1)) {
				if (StartOf(2)) {
					RuleSet rset = ruleset();
					dirb.addRule(rset); 
				} else if (StartOf(4)) {
					Declaration dec = declaration();
					dirb.addDeclaration(dec);
					if (endOfBlock()) {
					    break;
					}
					
					Expect(41);
				} else {
					Rule dr = directive();
					dirb.addRule(dr); 
				}
			}
			Expect(38);
		} else if (la.kind == 41) {
			Get();
		} else SynErr(96);
		dir = dirb.build(); 
		return dir;
	}

	Declaration  declaration() {
		Declaration  dec;
		Declaration.Builder decb = new Declaration.Builder(); 
		String ident = identity();
		decb.setName(ident); 
		Expect(33);
		Expression exp = expr();
		decb.setExpression(exp); 
		if (la.kind == 68) {
			Get();
			Expect(21);
			decb.setImportant(true); 
		}
		dec = decb.build(); 
		return dec;
	}

	Selector  selector() {
		Selector  sel;
		Selector.Builder selb = new Selector.Builder();
		SimpleSelector ss;
		Combinator cb = Combinator.Descendant;
		
		ss = simpleselector();
		selb.addSimpleSelector(ss); 
		while (StartOf(15)) {
			if (la.kind == 43 || la.kind == 44 || la.kind == 45) {
				if (la.kind == 43) {
					Get();
					cb = Combinator.PrecededImmediatelyBy; 
				} else if (la.kind == 44) {
					Get();
					cb = Combinator.ChildOf; 
				} else {
					Get();
					cb = Combinator.PrecededBy; 
				}
			}
			ss = simpleselector();
			ss = ss.withCombinator(cb);
			selb.addSimpleSelector(ss);
			cb = Combinator.Descendant;
			
		}
		sel = selb.build(); 
		return sel;
	}

	SimpleSelector  simpleselector() {
		SimpleSelector  ss;
		SimpleSelector.Builder ssb = new SimpleSelector.Builder();
		SimpleSelector.Builder parent = ssb;
		String ident;
		
		if (StartOf(4)) {
			ident = identity();
			ssb.setElementName(ident); 
		} else if (la.kind == 55) {
			Get();
			ssb.setElementName("*"); 
		} else if (StartOf(16)) {
			if (la.kind == 56) {
				Get();
				ident = identity();
				ssb.setID(ident); 
			} else if (la.kind == 57) {
				Get();
				ident = identity();
				ssb.setClassName(ident); 
			} else if (la.kind == 58) {
				Attribute atb = attrib();
				ssb.setAttribute(atb); 
			} else {
				String psd = pseudo();
				ssb.setPseudo(psd); 
			}
		} else SynErr(97);
		while (StartOf(16)) {
			if (t.pos + t.val.length() < la.pos) {
			   break;
			}
			SimpleSelector.Builder child = new SimpleSelector.Builder();
			
			if (la.kind == 56) {
				Get();
				ident = identity();
				child.setID(ident); 
			} else if (la.kind == 57) {
				Get();
				ident = identity();
				child.setClassName(ident); 
			} else if (la.kind == 58) {
				Attribute atb = attrib();
				child.setAttribute(atb); 
			} else {
				String psd = pseudo();
				child.setPseudo(psd); 
			}
			parent.setChild(child);
			parent = child;
			
		}
		ss = ssb.build(); 
		return ss;
	}

	Attribute  attrib() {
		Attribute  atb;
		Attribute.Builder atbb = new Attribute.Builder();
		String quote;
		String ident;
		
		Expect(58);
		ident = identity();
		atbb.setOperand(ident); 
		if (StartOf(17)) {
			switch (la.kind) {
			case 59: {
				Get();
				atbb.setOperator(AttributeOperator.Equals); 
				break;
			}
			case 60: {
				Get();
				atbb.setOperator(AttributeOperator.InList); 
				break;
			}
			case 61: {
				Get();
				atbb.setOperator(AttributeOperator.Hyphenated); 
				break;
			}
			case 62: {
				Get();
				atbb.setOperator(AttributeOperator.EndsWith); 
				break;
			}
			case 63: {
				Get();
				atbb.setOperator(AttributeOperator.BeginsWith); 
				break;
			}
			case 64: {
				Get();
				atbb.setOperator(AttributeOperator.Contains); 
				break;
			}
			}
			if (StartOf(4)) {
				ident = identity();
				atbb.setValue(ident); 
			} else if (la.kind == 5) {
				quote = QuotedString();
				atbb.setValue(quote); 
			} else if (la.kind == 3 || la.kind == 4) {
				if (la.kind == 3) {
					Get();
				} else {
					Get();
				}
				atbb.setValue(t.val); 
			} else SynErr(98);
		}
		Expect(65);
		atb = atbb.build(); 
		return atb;
	}

	BooleanExpression  orExpression() {
		BooleanExpression  expr;
		BooleanExpression left, right; BooleanOperation op; 
		left = andExpression();
		expr = left; 
		while (la.kind == 70 || la.kind == 71) {
			op = orop();
			right = andExpression();
			expr = new BinaryBooleanExpression(op, expr, right); 
		}
		return expr;
	}

	BooleanOperation  orop() {
		BooleanOperation  op;
		op = null; 
		if (la.kind == 70) {
			Get();
			op = BooleanOperation.OR; 
		} else if (la.kind == 71) {
			Get();
			op = BooleanOperation.XOR; 
		} else SynErr(99);
		return op;
	}

	BooleanExpression  andExpression() {
		BooleanExpression  expr;
		BooleanExpression left, right;
		left = notExpression();
		expr = left; 
		while (la.kind == 72) {
			Get();
			right = notExpression();
			expr = new BinaryBooleanExpression(BooleanOperation.AND, expr, right); 
		}
		return expr;
	}

	BooleanExpression  notExpression() {
		BooleanExpression  expr;
		BooleanExpression exp; expr = null; 
		if (StartOf(18)) {
			exp = primaryBooleanExpression();
			expr = exp; 
		} else if (la.kind == 68) {
			Get();
			exp = notExpression();
			expr = new NotExpression(exp); 
		} else SynErr(100);
		return expr;
	}

	BooleanExpression  primaryBooleanExpression() {
		BooleanExpression  expr;
		expr = null; Term trm; BooleanExpression exp; 
		if (la.kind == 32) {
			Get();
			exp = booleanExpression();
			Expect(34);
			expr = exp; 
		} else if (StartOf(11)) {
			trm = term();
			expr = new TermBooleanExpression(trm); 
		} else SynErr(101);
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
		while (la.kind == 43 || la.kind == 66) {
			op = addop();
			right = multiplicativeExpression();
			expr = new BinaryExpression(op, expr, right); 
		}
		return expr;
	}

	Operation  addop() {
		Operation  op;
		op = null; 
		if (la.kind == 43) {
			Get();
			op = Operation.Add; 
		} else if (la.kind == 66) {
			Get();
			op = Operation.Subtract; 
		} else SynErr(102);
		return op;
	}

	Operation  mulop() {
		Operation  op;
		op = null; 
		if (la.kind == 55) {
			Get();
			op = Operation.Multiply; 
		} else if (la.kind == 69) {
			Get();
			op = Operation.Divide; 
		} else SynErr(103);
		return op;
	}

	CalcExpression  multiplicativeExpression() {
		CalcExpression  expr;
		CalcExpression left, right; Operation op; 
		left = termExpression();
		expr = left; 
		while (la.kind == 55 || la.kind == 69) {
			op = mulop();
			right = termExpression();
			expr = new BinaryExpression(op, expr, right); 
		}
		return expr;
	}

	CalcExpression  termExpression() {
		CalcExpression  expr;
		expr = null; Term trm; CalcExpression exp; 
		if (la.kind == 66) {
			Get();
			exp = termExpression();
			expr = new NegationExpression(exp); 
		} else if (la.kind == 32) {
			Get();
			exp = lengthExpression();
			Expect(34);
			expr = exp; 
		} else if (StartOf(11)) {
			trm = term();
			expr = new TermExpression(trm); 
		} else SynErr(104);
		return expr;
	}

	CalculationLiteralTerm  literalCalculation() {
		CalculationLiteralTerm  trm;
		CalcExpression expr = null;
		String prefix = ""; 
		if (StartOf(19)) {
			if (la.kind == 23) {
				Get();
			} else if (la.kind == 73) {
				Get();
			} else if (la.kind == 74) {
				Get();
			} else if (la.kind == 75) {
				Get();
			} else {
				Get();
			}
			prefix = t.val.replace("calc", ""); 
			Expect(32);
			expr = lengthExpression();
			Expect(34);
		} else if (la.kind == 58) {
			Get();
			expr = lengthExpression();
			Expect(65);
		} else SynErr(105);
		trm = new CalculationLiteralTerm(prefix, expr); 
		return trm;
	}

	CalcExpression  calculation() {
		CalcExpression  expr;
		expr = null; 
		if (la.kind == 77) {
			Get();
			Expect(32);
			expr = lengthExpression();
			Expect(34);
		} else if (la.kind == 58) {
			Get();
			expr = lengthExpression();
			Expect(65);
		} else SynErr(106);
		return expr;
	}

	String  HexValue() {
		String  val;
		StringBuilder sb = new StringBuilder();
		boolean found = false;
		
		Expect(56);
		sb.append(t.val); 
		if (la.kind == 3) {
			Get();
			sb.append(t.val); 
		} else if (la.kind == 1) {
			Get();
			sb.append(t.val); found = true; 
		} else SynErr(107);
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
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,x,T, x,x,x,T, x,x,x,x, x,x,T,x, T,T,T,T, T,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,T, x,x,x,x, x,x,T,x, T,T,T,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x,x,x, T,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,T, x,x,x,x, x,x,T,x, x,T,x,x, x,T,T,T, T,T,T,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,x,x, x,x,x,x, x,x,x,x, x,x,T,x, T,x,x,x, T,x,x,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,x,x, x,x,x,T, x,x,T,T, T,T,T,x, T,x,x,x, x,x,x,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,x,x,x, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,x,x,x, x,x,T,x, x,x,x,x, x,T,T,T, T,T,T,x, x,x},
		{x,x,x,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x,T,x, T,T,x,x, x,T,x,T, T,x,x,x, x,x,x,x, x,x,x,T, T,x,T,T, x,x,x,x, x,T,T,x, T,T,T,T, T,T,T,T, T,T,T,T, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,x,T,x, T,T,x,x, x,T,x,T, T,x,x,x, x,x,x,x, x,x,x,T, T,x,T,T, x,x,x,x, x,T,T,x, T,T,T,T, T,T,T,T, T,T,T,x, x,x},
		{x,T,x,x, x,x,x,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, x,T,x,x, x,x,x,x, x,x,x,T, T,T,x,x, x,x,x,x, x,x,x,T, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x},
		{x,T,T,T, T,T,T,x, x,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,T,T,T, T,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, T,x,T,x, x,x,x,x, x,x,T,x, x,x,x,x, x,T,T,T, T,T,T,x, x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,T,T,T, T,x,x,x, x,x}

	};

	public static String getErrorMessage(int n) {
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
			case 23: s = "\"calc\" expected"; break;
			case 24: s = "\"only\" expected"; break;
			case 25: s = "\"not\" expected"; break;
			case 26: s = "\"and\" expected"; break;
			case 27: s = "\"literal\" expected"; break;
			case 28: s = "\"const\" expected"; break;
			case 29: s = "\"param\" expected"; break;
			case 30: s = "\"prop\" expected"; break;
			case 31: s = "\"ruleset\" expected"; break;
			case 32: s = "\"(\" expected"; break;
			case 33: s = "\":\" expected"; break;
			case 34: s = "\")\" expected"; break;
			case 35: s = "\"@media\" expected"; break;
			case 36: s = "\",\" expected"; break;
			case 37: s = "\"{\" expected"; break;
			case 38: s = "\"}\" expected"; break;
			case 39: s = "\"@if\" expected"; break;
			case 40: s = "\"@else\" expected"; break;
			case 41: s = "\";\" expected"; break;
			case 42: s = "\"&\" expected"; break;
			case 43: s = "\"+\" expected"; break;
			case 44: s = "\">\" expected"; break;
			case 45: s = "\"~\" expected"; break;
			case 46: s = "\"@class\" expected"; break;
			case 47: s = "\"<\" expected"; break;
			case 48: s = "\"@define\" expected"; break;
			case 49: s = "\"@font-face\" expected"; break;
			case 50: s = "\"@page\" expected"; break;
			case 51: s = "\"@import\" expected"; break;
			case 52: s = "\"@include\" expected"; break;
			case 53: s = "\"@charset\" expected"; break;
			case 54: s = "\"@namespace\" expected"; break;
			case 55: s = "\"*\" expected"; break;
			case 56: s = "\"#\" expected"; break;
			case 57: s = "\".\" expected"; break;
			case 58: s = "\"[\" expected"; break;
			case 59: s = "\"=\" expected"; break;
			case 60: s = "\"~=\" expected"; break;
			case 61: s = "\"|=\" expected"; break;
			case 62: s = "\"$=\" expected"; break;
			case 63: s = "\"^=\" expected"; break;
			case 64: s = "\"*=\" expected"; break;
			case 65: s = "\"]\" expected"; break;
			case 66: s = "\"-\" expected"; break;
			case 67: s = "\"-n\" expected"; break;
			case 68: s = "\"!\" expected"; break;
			case 69: s = "\"/\" expected"; break;
			case 70: s = "\"||\" expected"; break;
			case 71: s = "\"^\" expected"; break;
			case 72: s = "\"&&\" expected"; break;
			case 73: s = "\"-webkit-calc\" expected"; break;
			case 74: s = "\"-o-calc\" expected"; break;
			case 75: s = "\"-ms-calc\" expected"; break;
			case 76: s = "\"-moz-calc\" expected"; break;
			case 77: s = "\"@calc\" expected"; break;
			case 78: s = "\"U\\\\\" expected"; break;
			case 79: s = "\"%\" expected"; break;
			case 80: s = "??? expected"; break;
			case 81: s = "invalid rule"; break;
			case 82: s = "invalid directive"; break;
			case 83: s = "invalid medium"; break;
			case 84: s = "invalid identity"; break;
			case 85: s = "invalid mediaQuery"; break;
			case 86: s = "invalid includeDirective"; break;
			case 87: s = "invalid includeDirective"; break;
			case 88: s = "invalid pseudo"; break;
			case 89: s = "invalid pseudo"; break;
			case 90: s = "invalid term"; break;
			case 91: s = "invalid term"; break;
			case 92: s = "invalid term"; break;
			case 93: s = "invalid term"; break;
			case 94: s = "invalid term"; break;
			case 95: s = "invalid namespaceDirective"; break;
			case 96: s = "invalid genericDirective"; break;
			case 97: s = "invalid simpleselector"; break;
			case 98: s = "invalid attrib"; break;
			case 99: s = "invalid orop"; break;
			case 100: s = "invalid notExpression"; break;
			case 101: s = "invalid primaryBooleanExpression"; break;
			case 102: s = "invalid addop"; break;
			case 103: s = "invalid mulop"; break;
			case 104: s = "invalid termExpression"; break;
			case 105: s = "invalid literalCalculation"; break;
			case 106: s = "invalid calculation"; break;
			case 107: s = "invalid HexValue"; break;
			default: s = "error " + n; break;
		}
		return s;
	}
} // end Parser


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}

