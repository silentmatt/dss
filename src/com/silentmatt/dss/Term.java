package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public class Term {
    private Character seperator;
    private Character sign;
    private TermType type;
    private String value;
    private Unit unit;
    private Function function;

    public Character getSeperator() {
        return seperator;
    }

    public void setSeperator(Character Seperator) {
        this.seperator = Seperator;
    }

    public Character getSign() {
        return sign;
    }

    public void setSign(Character Sign) {
        this.sign = Sign;
    }

    public TermType getType() {
        return type;
    }

    public void setType(TermType Type) {
        this.type = Type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String Value) {
        this.value = Value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit Unit) {
        this.unit = Unit;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function Function) {
        this.function = Function;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toCompactString() {
        return toString(true);
    }

    private String toString(boolean compact) {
        StringBuilder txt = new StringBuilder();
        if (seperator != null) {
            txt.append(seperator);
            if (!compact) {
                txt.append(" ");
            }
        }

        if (type == TermType.Function) {
            txt.append(compact ? function.toCompactString() : function.toString());
        } else if (type == TermType.Url) {
            txt.append("url(").append(value).append(")");
        } else if (type == TermType.Unicode) {
            txt.append("U\\").append(value.toUpperCase());
        } else if (type == TermType.Hex) {
            txt.append(value.toUpperCase());
        } else {
            if (sign != null) { txt.append(sign); }
            txt.append(value);
            if (unit != null) {
                if (unit == unit.Percent) {
                    txt.append("%");
                } else {
                    txt.append(unit.toString());
                }
            }
        }

        return txt.toString();
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
