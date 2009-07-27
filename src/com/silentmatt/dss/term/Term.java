package com.silentmatt.dss.term;

/**
 * @todo Separate different {@link TermType}s into subclasses of Term.
 *
 * @author Matthew Crumley
 */
public abstract class Term {
    private Character seperator;

    public Character getSeperator() {
        return seperator;
    }

    public void setSeperator(Character Seperator) {
        this.seperator = Seperator;
    }

// TODO: Implement these methods
/*		public bool IsColor {
			get {
				if (((type == TermType.Hex) || (type == TermType.String && val.StartsWith("#")))
					&& (val.Length == 6 || val.Length == 3 || ((val.Length == 7 || val.Length == 4)
					&& val.StartsWith("#")))) {
					bool hex = true;
					foreach (char c in val) {
						if (!char.IsDigit(c) && c != '#'
							&& c != 'a' && c != 'A'
							&& c != 'b' && c != 'B'
							&& c != 'c' && c != 'C'
							&& c != 'd' && c != 'D'
							&& c != 'e' && c != 'E'
							&& c != 'f' && c != 'F'
						) {
							return false;
						}
					}
					return hex;
				} else if (type == TermType.String) {
					bool number = true;
					foreach (char c in val) {
						if (!char.IsDigit(c)) {
							number = false;
							break;
						}
					}
					if (number) { return false; }

					try {
						KnownColor kc = (KnownColor)Enum.Parse(typeof(KnownColor), val, true);
						return true;
					} catch { }
				} else if (type == TermType.Function) {
					if ((function.Name.ToLower().Equals("rgb") && function.Expression.Terms.Count == 3)
						|| (function.Name.ToLower().Equals("rgba") && function.Expression.Terms.Count == 4)
						) {
						for (int i = 0; i < function.Expression.Terms.Count; i++) {
							if (function.Expression.Terms[i].Type != TermType.Number) { return false; }
						}
						return true;
					} else if ((function.Name.ToLower().Equals("hsl") && function.Expression.Terms.Count == 3)
						|| (function.Name.ToLower().Equals("hsla") && function.Expression.Terms.Count == 4)
						) {
						for (int i = 0; i < function.Expression.Terms.Count; i++) {
							if (function.Expression.Terms[i].Type != TermType.Number) { return false; }
						}
						return true;
					}
				}
				return false;
			}
		}
		private int GetRGBValue(Term t) {
			try {
				if (t.Unit.HasValue && t.Unit.Value == BoneSoft.CSS.Unit.Percent) {
					return (int)(255f * float.Parse(t.Value) / 100f);
				}
				return int.Parse(t.Value);
			} catch {}
			return 0;
		}
		private int GetHueValue(Term t) {
			// 0 - 360
			try {
				return (int)(float.Parse(t.Value) * 255f / 360f);
			} catch {}
			return 0;
		}

		public Color ToColor() {
			string hex = "000000";
			if (type == TermType.Hex) {
				if ((val.Length == 7 || val.Length == 4) && val.StartsWith("#")) {
					hex = val.Substring(1);
				} else if (val.Length == 6 || val.Length == 3) {
					hex = val;
				}
			} else if (type == TermType.Function) {
				if ((function.Name.ToLower().Equals("rgb") && function.Expression.Terms.Count == 3)
					|| (function.Name.ToLower().Equals("rgba") && function.Expression.Terms.Count == 4)
					) {
					int fr = 0, fg = 0, fb = 0;
					for (int i = 0; i < function.Expression.Terms.Count; i++) {
						if (function.Expression.Terms[i].Type != TermType.Number) { return Color.Black; }
						switch (i) {
							case 0: fr = GetRGBValue(function.Expression.Terms[i]); break;
							case 1: fg = GetRGBValue(function.Expression.Terms[i]); break;
							case 2: fb = GetRGBValue(function.Expression.Terms[i]); break;
						}
					}
					return Color.FromArgb(fr, fg, fb);
				} else if ((function.Name.ToLower().Equals("hsl") && function.Expression.Terms.Count == 3)
					|| (function.Name.Equals("hsla") && function.Expression.Terms.Count == 4)
					) {
					int h = 0, s = 0, v = 0;
					for (int i = 0; i < function.Expression.Terms.Count; i++) {
						if (function.Expression.Terms[i].Type != TermType.Number) { return Color.Black; }
						switch (i) {
							case 0: h = GetHueValue(function.Expression.Terms[i]); break;
							case 1: s = GetRGBValue(function.Expression.Terms[i]); break;
							case 2: v = GetRGBValue(function.Expression.Terms[i]); break;
						}
					}
					HSV hsv = new HSV(h, s, v);
					return hsv.Color;
				}
			} else {
				try {
					KnownColor kc = (KnownColor)Enum.Parse(typeof(KnownColor), val, true);
					Color c = Color.FromKnownColor(kc);
					return c;
				} catch { }
			}
			if (hex.Length == 3) {
				string temp = "";
				foreach (char c in hex) {
					temp += c.ToString() + c.ToString();
				}
				hex = temp;
			}
			int r = DeHex(hex.Substring(0, 2));
			int g = DeHex(hex.Substring(2, 2));
			int b = DeHex(hex.Substring(4));
			return Color.FromArgb(r, g, b);
		}
		private int DeHex(string input) {
			int val;
			int result = 0;
			for (int i = 0; i < input.Length; i++) {
				string chunk = input.Substring(i, 1).ToUpper();
				switch (chunk) {
					case "A":
						val = 10; break;
					case "B":
						val = 11; break;
					case "C":
						val = 12; break;
					case "D":
						val = 13; break;
					case "E":
						val = 14; break;
					case "F":
						val = 15; break;
					default:
						val = int.Parse(chunk); break;
				}
				if (i == 0) {
					result += val * 16;
				} else {
					result += val;
				}
			}
			return result;
		}
*/
}
