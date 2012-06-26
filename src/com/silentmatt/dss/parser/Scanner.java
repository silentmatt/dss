package com.silentmatt.dss.parser;

import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.HashMap;

class Token {
	int kind;    // token kind
	int pos;     // token position in the source text (starting at 0)
	int col;     // token column (starting at 1)
	int line;    // token line (starting at 1)
	String val;  // token value
	Token next;  // ML 2005-03-11 Peek tokens are kept in linked list
}

//-----------------------------------------------------------------------------------
// Buffer
//-----------------------------------------------------------------------------------
class Buffer {
	// This Buffer supports the following cases:
	// 1) seekable stream (file)
	//    a) whole stream in buffer
	//    b) part of stream in buffer
	// 2) non seekable stream (network, console)

	public static final int EOF = Character.MAX_VALUE + 1;
	private static final int MIN_BUFFER_LENGTH = 1024; // 1KB
	private static final int MAX_BUFFER_LENGTH = MIN_BUFFER_LENGTH * 64; // 64KB
	private byte[] buf;   // input buffer
	private int bufStart; // position of first byte in buffer relative to input stream
	private int bufLen;   // length of buffer
	private int fileLen;  // length of input stream (may change if stream is no file)
	private int bufPos;      // current position in buffer
	private RandomAccessFile file; // input stream (seekable)
	private InputStream stream; // growing input stream (e.g.: console, network)

	public Buffer(InputStream s) {
		stream = s;
		fileLen = bufLen = bufStart = bufPos = 0;
		buf = new byte[MIN_BUFFER_LENGTH];
	}

	public Buffer(String fileName) {
		try {
			file = new RandomAccessFile(fileName, "r");
			fileLen = (int) file.length();
			bufLen = Math.min(fileLen, MAX_BUFFER_LENGTH);
			buf = new byte[bufLen];
			bufStart = Integer.MAX_VALUE; // nothing in buffer so far
			if (fileLen > 0) setPos(0); // setup buffer to position 0 (start)
			else bufPos = 0; // index 0 is already after the file, thus setPos(0) is invalid
			if (bufLen == fileLen) Close();
		} catch (IOException e) {
			throw new FatalError("Could not open file " + fileName);
		}
	}

	// don't use b after this call anymore
	// called in UTF8Buffer constructor
	protected Buffer(Buffer b) {
		buf = b.buf;
		bufStart = b.bufStart;
		bufLen = b.bufLen;
		fileLen = b.fileLen;
		bufPos = b.bufPos;
		file = b.file;
		stream = b.stream;
		// keep finalize from closing the file
		b.file = null;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		Close();
	}

	protected void Close() {
		if (file != null) {
			try {
				file.close();
				file = null;
			} catch (IOException e) {
				throw new FatalError(e.getMessage());
			}
		}
	}

	public int Read() {
		if (bufPos < bufLen) {
			return buf[bufPos++] & 0xff;  // mask out sign bits
		} else if (getPos() < fileLen) {
			setPos(getPos());         // shift buffer start to pos
			return buf[bufPos++] & 0xff; // mask out sign bits
		} else if (stream != null && ReadNextStreamChunk() > 0) {
			return buf[bufPos++] & 0xff;  // mask out sign bits
		} else {
			return EOF;
		}
	}

	public int Peek() {
		int curPos = getPos();
		int ch = Read();
		setPos(curPos);
		return ch;
	}

	public String GetString(int beg, int end) {
	    int len = end - beg;
	    char[] buf = new char[len];
	    int oldPos = getPos();
	    setPos(beg);
	    for (int i = 0; i < len; ++i) buf[i] = (char) Read();
	    setPos(oldPos);
	    return new String(buf);
	}

	public int getPos() {
		return bufPos + bufStart;
	}

	public void setPos(int value) {
		if (value >= fileLen && stream != null) {
			// Wanted position is after buffer and the stream
			// is not seek-able e.g. network or console,
			// thus we have to read the stream manually till
			// the wanted position is in sight.
			while (value >= fileLen && ReadNextStreamChunk() > 0);
		}

		if (value < 0 || value > fileLen) {
			throw new FatalError("buffer out of bounds access, position: " + value);
		}

		if (value >= bufStart && value < bufStart + bufLen) { // already in buffer
			bufPos = value - bufStart;
		} else if (file != null) { // must be swapped in
			try {
				file.seek(value);
				bufLen = file.read(buf);
				bufStart = value; bufPos = 0;
			} catch(IOException e) {
				throw new FatalError(e.getMessage());
			}
		} else {
			// set the position to the end of the file, Pos will return fileLen.
			bufPos = fileLen - bufStart;
		}
	}
	
	// Read the next chunk of bytes from the stream, increases the buffer
	// if needed and updates the fields fileLen and bufLen.
	// Returns the number of bytes read.
	private int ReadNextStreamChunk() {
		int free = buf.length - bufLen;
		if (free == 0) {
			// in the case of a growing input stream
			// we can neither seek in the stream, nor can we
			// foresee the maximum length, thus we must adapt
			// the buffer size on demand.
			byte[] newBuf = new byte[bufLen * 2];
			System.arraycopy(buf, 0, newBuf, 0, bufLen);
			buf = newBuf;
			free = bufLen;
		}
		
		int read;
		try { read = stream.read(buf, bufLen, free); }
		catch (IOException ioex) { throw new FatalError(ioex.getMessage()); }
		
		if (read > 0) {
			fileLen = bufLen = (bufLen + read);
			return read;
		}
		// end of stream reached
		return 0;
	}
}

//-----------------------------------------------------------------------------------
// UTF8Buffer
//-----------------------------------------------------------------------------------
class UTF8Buffer extends Buffer {
	UTF8Buffer(Buffer b) { super(b); }

	public int Read() {
		int ch;
		do {
			ch = super.Read();
			// until we find a utf8 start (0xxxxxxx or 11xxxxxx)
		} while ((ch >= 128) && ((ch & 0xC0) != 0xC0) && (ch != EOF));
		if (ch < 128 || ch == EOF) {
			// nothing to do, first 127 chars are the same in ascii and utf8
			// 0xxxxxxx or end of file character
		} else if ((ch & 0xF0) == 0xF0) {
			// 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
			int c1 = ch & 0x07; ch = super.Read();
			int c2 = ch & 0x3F; ch = super.Read();
			int c3 = ch & 0x3F; ch = super.Read();
			int c4 = ch & 0x3F;
			ch = (((((c1 << 6) | c2) << 6) | c3) << 6) | c4;
		} else if ((ch & 0xE0) == 0xE0) {
			// 1110xxxx 10xxxxxx 10xxxxxx
			int c1 = ch & 0x0F; ch = super.Read();
			int c2 = ch & 0x3F; ch = super.Read();
			int c3 = ch & 0x3F;
			ch = (((c1 << 6) | c2) << 6) | c3;
		} else if ((ch & 0xC0) == 0xC0) {
			// 110xxxxx 10xxxxxx
			int c1 = ch & 0x1F; ch = super.Read();
			int c2 = ch & 0x3F;
			ch = (c1 << 6) | c2;
		}
		return ch;
	}
}

//-----------------------------------------------------------------------------------
// StartStates  -- maps characters to start states of tokens
//-----------------------------------------------------------------------------------
class StartStates {
	private static class Elem {
		public int key, val;
		public Elem next;
		public Elem(int key, int val) { this.key = key; this.val = val; }
	}

	private Elem[] tab = new Elem[128];

	public void set(int key, int val) {
		Elem e = new Elem(key, val);
		int k = key % 128;
		e.next = tab[k]; tab[k] = e;
	}

	public int state(int key) {
		Elem e = tab[key % 128];
		while (e != null && e.key != key) e = e.next;
		return e == null ? 0: e.val;
	}
}

//-----------------------------------------------------------------------------------
// Scanner
//-----------------------------------------------------------------------------------
public class Scanner {
	static final char EOL = '\n';
	static final int  eofSym = 0;
	static final int maxT = 76;
	static final int noSym = 76;


	public Buffer buffer; // scanner buffer

	Token t;           // current token
	int ch;            // current input character
	int pos;           // byte position of current character
	int col;           // column number of current character
	int line;          // line number of current character
	int oldEols;       // EOLs that appeared in a comment;
	static final StartStates start; // maps initial token character to start state
	static final Map<String, Integer> literals;      // maps literal strings to literal kinds

	Token tokens;      // list of tokens already peeked (first token is a dummy)
	Token pt;          // current peek token
	
	char[] tval = new char[16]; // token text used in NextToken(), dynamically enlarged
	int tlen;          // length of current token


	static {
		start = new StartStates();
		literals = new HashMap<String, Integer>();
		for (int i = 65; i <= 84; ++i) start.set(i, 1);
		for (int i = 86; i <= 90; ++i) start.set(i, 1);
		for (int i = 95; i <= 95; ++i) start.set(i, 1);
		for (int i = 97; i <= 116; ++i) start.set(i, 1);
		for (int i = 118; i <= 122; ++i) start.set(i, 1);
		for (int i = 128; i <= 55295; ++i) start.set(i, 1);
		for (int i = 57344; i <= 65533; ++i) start.set(i, 1);
		for (int i = 48; i <= 57; ++i) start.set(i, 38);
		start.set(92, 39); 
		start.set(45, 128); 
		start.set(64, 129); 
		start.set(46, 4); 
		start.set(34, 5); 
		start.set(39, 12); 
		start.set(117, 40); 
		start.set(60, 130); 
		start.set(40, 104); 
		start.set(58, 105); 
		start.set(41, 106); 
		start.set(44, 107); 
		start.set(123, 108); 
		start.set(125, 109); 
		start.set(59, 110); 
		start.set(62, 111); 
		start.set(38, 131); 
		start.set(43, 112); 
		start.set(126, 132); 
		start.set(42, 133); 
		start.set(35, 113); 
		start.set(91, 114); 
		start.set(61, 115); 
		start.set(124, 134); 
		start.set(36, 118); 
		start.set(94, 135); 
		start.set(93, 122); 
		start.set(33, 123); 
		start.set(47, 124); 
		start.set(85, 136); 
		start.set(37, 127); 
		start.set(Buffer.EOF, -1);
		literals.put("all", new Integer(9));
		literals.put("aural", new Integer(10));
		literals.put("braille", new Integer(11));
		literals.put("embossed", new Integer(12));
		literals.put("handheld", new Integer(13));
		literals.put("print", new Integer(14));
		literals.put("projection", new Integer(15));
		literals.put("screen", new Integer(16));
		literals.put("tty", new Integer(17));
		literals.put("tv", new Integer(18));
		literals.put("n", new Integer(19));
		literals.put("url", new Integer(20));
		literals.put("important", new Integer(21));
		literals.put("global", new Integer(22));
		literals.put("calc", new Integer(23));
		literals.put("only", new Integer(24));
		literals.put("not", new Integer(25));
		literals.put("and", new Integer(26));
		literals.put("literal", new Integer(27));
		literals.put("const", new Integer(28));
		literals.put("param", new Integer(29));
		literals.put("ruleset", new Integer(30));
		literals.put("@media", new Integer(34));
		literals.put("@if", new Integer(38));
		literals.put("@else", new Integer(39));
		literals.put("@class", new Integer(40));
		literals.put("@define", new Integer(47));
		literals.put("@font-face", new Integer(48));
		literals.put("@page", new Integer(49));
		literals.put("@import", new Integer(50));
		literals.put("@include", new Integer(51));
		literals.put("@charset", new Integer(52));
		literals.put("@namespace", new Integer(53));
		literals.put(".", new Integer(57));
		literals.put("-n", new Integer(67));
		literals.put("prop", new Integer(73));

	}
	
	public Scanner (String fileName) {
		buffer = new Buffer(fileName);
		Init();
	}
	
	public Scanner(InputStream s) {
		buffer = new Buffer(s);
		Init();
	}
	
	void Init () {
		pos = -1; line = 1; col = 0;
		oldEols = 0;
		NextCh();
		if (ch == 0xEF) { // check optional byte order mark for UTF-8
			NextCh(); int ch1 = ch;
			NextCh(); int ch2 = ch;
			if (ch1 != 0xBB || ch2 != 0xBF) {
				throw new FatalError("Illegal byte order mark at start of file");
			}
			col = 0;
			NextCh();
		}
                buffer = new UTF8Buffer(buffer);
		pt = tokens = new Token();  // first token is a dummy
	}
	
	void NextCh() {
		if (oldEols > 0) { ch = EOL; oldEols--; }
		else {
			pos = buffer.getPos();
			ch = buffer.Read(); col++;
			// replace isolated '\r' by '\n' in order to make
			// eol handling uniform across Windows, Unix and Mac
			if (ch == '\r' && buffer.Peek() != '\n') ch = EOL;
			if (ch == EOL) { line++; col = 0; }
		}

	}
	
	void AddCh() {
		if (tlen >= tval.length) {
			char[] newBuf = new char[2 * tval.length];
			System.arraycopy(tval, 0, newBuf, 0, tval.length);
			tval = newBuf;
		}
		if (ch != Buffer.EOF) {
			tval[tlen++] = (char)ch; 

			NextCh();
		}

	}
	

	boolean Comment0() {
		int level = 1, pos0 = pos, line0 = line, col0 = col;
		NextCh();
		if (ch == '/') {
			NextCh();
			for(;;) {
				if (ch == 10) {
					level--;
					if (level == 0) { oldEols = line - line0; NextCh(); return true; }
					NextCh();
				} else if (ch == Buffer.EOF) return false;
				else NextCh();
			}
		} else {
			buffer.setPos(pos0); NextCh(); line = line0; col = col0;
		}
		return false;
	}

	boolean Comment1() {
		int level = 1, pos0 = pos, line0 = line, col0 = col;
		NextCh();
		if (ch == '*') {
			NextCh();
			for(;;) {
				if (ch == '*') {
					NextCh();
					if (ch == '/') {
						level--;
						if (level == 0) { oldEols = line - line0; NextCh(); return true; }
						NextCh();
					}
				} else if (ch == Buffer.EOF) return false;
				else NextCh();
			}
		} else {
			buffer.setPos(pos0); NextCh(); line = line0; col = col0;
		}
		return false;
	}


	void CheckLiteral() {
		String val = t.val;

		Object kind = literals.get(val);
		if (kind != null) {
			t.kind = ((Integer) kind).intValue();
		}
	}

	Token NextToken() {
		while (ch == ' ' ||
			ch >= 9 && ch <= 10 || ch == 13 || ch == ' '
		) NextCh();
		if (ch == '/' && Comment0() ||ch == '/' && Comment1()) return NextToken();
		t = new Token();
		t.pos = pos; t.col = col; t.line = line; 
		int state = start.state(ch);
		tlen = 0; AddCh();

		loop: for (;;) {
			switch (state) {
				case -1: { t.kind = eofSym; break loop; } // NextCh already done 
				case 0: { t.kind = noSym; break loop; }   // NextCh already done
				case 1:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 2:
					if (ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch == 92) {AddCh(); state = 44; break;}
					else {t.kind = noSym; break loop;}
				case 3:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 4:
					if (ch >= '0' && ch <= '9') {AddCh(); state = 4; break;}
					else {t.kind = 4; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 5:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 5; break;}
					else if (ch == '"') {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 6; break;}
					else {t.kind = noSym; break loop;}
				case 6:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 48; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 11; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 5; break;}
					else if (ch == 'u') {AddCh(); state = 7; break;}
					else {t.kind = noSym; break loop;}
				case 7:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 8; break;}
					else if (ch == 'u') {AddCh(); state = 7; break;}
					else {t.kind = noSym; break loop;}
				case 8:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 9; break;}
					else {t.kind = noSym; break loop;}
				case 9:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 10; break;}
					else {t.kind = noSym; break loop;}
				case 10:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 5; break;}
					else {t.kind = noSym; break loop;}
				case 11:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 5; break;}
					else if (ch == '"') {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 6; break;}
					else {t.kind = noSym; break loop;}
				case 12:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 12; break;}
					else if (ch == 39) {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 13; break;}
					else {t.kind = noSym; break loop;}
				case 13:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 50; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 18; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 12; break;}
					else if (ch == 'u') {AddCh(); state = 14; break;}
					else {t.kind = noSym; break loop;}
				case 14:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 15; break;}
					else if (ch == 'u') {AddCh(); state = 14; break;}
					else {t.kind = noSym; break loop;}
				case 15:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 16; break;}
					else {t.kind = noSym; break loop;}
				case 16:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 17; break;}
					else {t.kind = noSym; break loop;}
				case 17:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 12; break;}
					else {t.kind = noSym; break loop;}
				case 18:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 12; break;}
					else if (ch == 39) {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 13; break;}
					else {t.kind = noSym; break loop;}
				case 19:
					{t.kind = 5; break loop;}
				case 20:
					if (ch <= '!' || ch >= '#' && ch <= '&' || ch == '(' || ch >= '*' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == '"') {AddCh(); state = 52; break;}
					else if (ch == 39) {AddCh(); state = 53; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 21:
					if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 22:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 22; break;}
					else if (ch == '"') {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 23; break;}
					else {t.kind = noSym; break loop;}
				case 23:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 54; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 28; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 22; break;}
					else if (ch == 'u') {AddCh(); state = 24; break;}
					else {t.kind = noSym; break loop;}
				case 24:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 25; break;}
					else if (ch == 'u') {AddCh(); state = 24; break;}
					else {t.kind = noSym; break loop;}
				case 25:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 26; break;}
					else {t.kind = noSym; break loop;}
				case 26:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 27; break;}
					else {t.kind = noSym; break loop;}
				case 27:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 22; break;}
					else {t.kind = noSym; break loop;}
				case 28:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 22; break;}
					else if (ch == '"') {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 23; break;}
					else {t.kind = noSym; break loop;}
				case 29:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 29; break;}
					else if (ch == 39) {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 30; break;}
					else {t.kind = noSym; break loop;}
				case 30:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 56; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 35; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 29; break;}
					else if (ch == 'u') {AddCh(); state = 31; break;}
					else {t.kind = noSym; break loop;}
				case 31:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 32; break;}
					else if (ch == 'u') {AddCh(); state = 31; break;}
					else {t.kind = noSym; break loop;}
				case 32:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 33; break;}
					else {t.kind = noSym; break loop;}
				case 33:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 34; break;}
					else {t.kind = noSym; break loop;}
				case 34:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 29; break;}
					else {t.kind = noSym; break loop;}
				case 35:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 29; break;}
					else if (ch == 39) {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 30; break;}
					else {t.kind = noSym; break loop;}
				case 36:
					if (ch <= '(' || ch >= '*' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 37:
					{t.kind = 6; break loop;}
				case 38:
					if (ch >= '0' && ch <= '9') {AddCh(); state = 38; break;}
					else if (ch == '.') {AddCh(); state = 4; break;}
					else {t.kind = 3; break loop;}
				case 39:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 58; break;}
					else if (ch >= ' ' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= '~' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else {t.kind = noSym; break loop;}
				case 40:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'q' || ch >= 's' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else if (ch == 'r') {AddCh(); state = 59; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 41:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 60; break;}
					else if (ch >= ' ' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= '~' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else {t.kind = noSym; break loop;}
				case 42:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 61; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 43:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 62; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 44:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 63; break;}
					else if (ch >= ' ' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= '~' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else {t.kind = noSym; break loop;}
				case 45:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 64; break;}
					else if (ch >= ' ' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= '~' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else {t.kind = noSym; break loop;}
				case 46:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 65; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 47:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 66; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 48:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 5; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 49; break;}
					else if (ch == '"') {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 6; break;}
					else {t.kind = noSym; break loop;}
				case 49:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 5; break;}
					else if (ch == '"') {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 6; break;}
					else {t.kind = noSym; break loop;}
				case 50:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 12; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 51; break;}
					else if (ch == 39) {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 13; break;}
					else {t.kind = noSym; break loop;}
				case 51:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 12; break;}
					else if (ch == 39) {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 13; break;}
					else {t.kind = noSym; break loop;}
				case 52:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 52; break;}
					else if (ch == ')') {AddCh(); state = 67; break;}
					else if (ch == 10 || ch == 13) {AddCh(); state = 36; break;}
					else if (ch == '"') {AddCh(); state = 68; break;}
					else if (ch == 92) {AddCh(); state = 69; break;}
					else {t.kind = noSym; break loop;}
				case 53:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch == '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 53; break;}
					else if (ch == ')') {AddCh(); state = 70; break;}
					else if (ch == 10 || ch == 13) {AddCh(); state = 36; break;}
					else if (ch == 39) {AddCh(); state = 68; break;}
					else if (ch == 92) {AddCh(); state = 71; break;}
					else {t.kind = noSym; break loop;}
				case 54:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 22; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 55; break;}
					else if (ch == '"') {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 23; break;}
					else {t.kind = noSym; break loop;}
				case 55:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 22; break;}
					else if (ch == '"') {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 23; break;}
					else {t.kind = noSym; break loop;}
				case 56:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 29; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 57; break;}
					else if (ch == 39) {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 30; break;}
					else {t.kind = noSym; break loop;}
				case 57:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 29; break;}
					else if (ch == 39) {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 30; break;}
					else {t.kind = noSym; break loop;}
				case 58:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 43; break;}
					else if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 59:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'k' || ch >= 'm' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else if (ch == 'l') {AddCh(); state = 72; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 60:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 42; break;}
					else if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 61:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 73; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 62:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 74; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 63:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 47; break;}
					else if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 64:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 46; break;}
					else if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 65:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 75; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 66:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 76; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 67:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 22; break;}
					else if (ch == '"') {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 23; break;}
					else {t.kind = 6; break loop;}
				case 68:
					if (ch <= '(' || ch >= '*' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 69:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 77; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 78; break;}
					else if (ch <= 9 || ch >= 11 && ch <= '!' || ch >= '#' && ch <= '&' || ch == '(' || ch >= '*' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 'a' || ch >= 'c' && ch <= 'e' || ch >= 'g' && ch <= 'm' || ch >= 'o' && ch <= 'q' || ch == 's' || ch >= 'v' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 52; break;}
					else if (ch == 'u') {AddCh(); state = 79; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 70:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 29; break;}
					else if (ch == 39) {AddCh(); state = 21; break;}
					else if (ch == 92) {AddCh(); state = 30; break;}
					else {t.kind = 6; break loop;}
				case 71:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 80; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 81; break;}
					else if (ch <= 9 || ch >= 11 && ch <= '!' || ch >= '#' && ch <= '&' || ch == '(' || ch >= '*' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 'a' || ch >= 'c' && ch <= 'e' || ch >= 'g' && ch <= 'm' || ch >= 'o' && ch <= 'q' || ch == 's' || ch >= 'v' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 53; break;}
					else if (ch == 'u') {AddCh(); state = 82; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 72:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else if (ch == '(') {AddCh(); state = 20; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 73:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 83; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 74:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 84; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 75:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 85; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 76:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 86; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 77:
					if (ch == 10 || ch == 13) {AddCh(); state = 36; break;}
					else if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '(' || ch >= '*' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 52; break;}
					else if (ch == ')') {AddCh(); state = 67; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 87; break;}
					else if (ch == '"') {AddCh(); state = 68; break;}
					else if (ch == 92) {AddCh(); state = 69; break;}
					else {t.kind = noSym; break loop;}
				case 78:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 52; break;}
					else if (ch == ')') {AddCh(); state = 67; break;}
					else if (ch == 10 || ch == 13) {AddCh(); state = 36; break;}
					else if (ch == '"') {AddCh(); state = 68; break;}
					else if (ch == 92) {AddCh(); state = 69; break;}
					else {t.kind = noSym; break loop;}
				case 79:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 88; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 't' || ch >= 'v' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == 'u') {AddCh(); state = 79; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 80:
					if (ch == 10 || ch == 13) {AddCh(); state = 36; break;}
					else if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch == '(' || ch >= '*' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 53; break;}
					else if (ch == ')') {AddCh(); state = 70; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 89; break;}
					else if (ch == 39) {AddCh(); state = 68; break;}
					else if (ch == 92) {AddCh(); state = 71; break;}
					else {t.kind = noSym; break loop;}
				case 81:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch == '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 53; break;}
					else if (ch == ')') {AddCh(); state = 70; break;}
					else if (ch == 10 || ch == 13) {AddCh(); state = 36; break;}
					else if (ch == 39) {AddCh(); state = 68; break;}
					else if (ch == 92) {AddCh(); state = 71; break;}
					else {t.kind = noSym; break loop;}
				case 82:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 90; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 't' || ch >= 'v' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == 'u') {AddCh(); state = 82; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 83:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 91; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 84:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 92; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 85:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 93; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 86:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 94; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 87:
					if (ch == 10 || ch == 13) {AddCh(); state = 36; break;}
					else if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 52; break;}
					else if (ch == ')') {AddCh(); state = 67; break;}
					else if (ch == '"') {AddCh(); state = 68; break;}
					else if (ch == 92) {AddCh(); state = 69; break;}
					else {t.kind = noSym; break loop;}
				case 88:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 95; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 89:
					if (ch == 10 || ch == 13) {AddCh(); state = 36; break;}
					else if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch == '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 53; break;}
					else if (ch == ')') {AddCh(); state = 70; break;}
					else if (ch == 39) {AddCh(); state = 68; break;}
					else if (ch == 92) {AddCh(); state = 71; break;}
					else {t.kind = noSym; break loop;}
				case 90:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 96; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 91:
					if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 92:
					if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 41; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 93:
					if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 94:
					if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch == 92) {AddCh(); state = 45; break;}
					else {t.kind = 2; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 95:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 97; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 96:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 98; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 97:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 52; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 98:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 53; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 36; break;}
					else if (ch == ')') {AddCh(); state = 37; break;}
					else {t.kind = noSym; break loop;}
				case 99:
					if (ch == '-') {AddCh(); state = 100; break;}
					else {t.kind = noSym; break loop;}
				case 100:
					if (ch == '-') {AddCh(); state = 101; break;}
					else {t.kind = noSym; break loop;}
				case 101:
					{t.kind = 7; break loop;}
				case 102:
					if (ch == '>') {AddCh(); state = 103; break;}
					else {t.kind = noSym; break loop;}
				case 103:
					{t.kind = 8; break loop;}
				case 104:
					{t.kind = 31; break loop;}
				case 105:
					{t.kind = 32; break loop;}
				case 106:
					{t.kind = 33; break loop;}
				case 107:
					{t.kind = 35; break loop;}
				case 108:
					{t.kind = 36; break loop;}
				case 109:
					{t.kind = 37; break loop;}
				case 110:
					{t.kind = 42; break loop;}
				case 111:
					{t.kind = 43; break loop;}
				case 112:
					{t.kind = 45; break loop;}
				case 113:
					{t.kind = 56; break loop;}
				case 114:
					{t.kind = 58; break loop;}
				case 115:
					{t.kind = 59; break loop;}
				case 116:
					{t.kind = 60; break loop;}
				case 117:
					{t.kind = 61; break loop;}
				case 118:
					if (ch == '=') {AddCh(); state = 119; break;}
					else {t.kind = noSym; break loop;}
				case 119:
					{t.kind = 62; break loop;}
				case 120:
					{t.kind = 63; break loop;}
				case 121:
					{t.kind = 64; break loop;}
				case 122:
					{t.kind = 65; break loop;}
				case 123:
					{t.kind = 68; break loop;}
				case 124:
					{t.kind = 69; break loop;}
				case 125:
					{t.kind = 70; break loop;}
				case 126:
					{t.kind = 72; break loop;}
				case 127:
					{t.kind = 75; break loop;}
				case 128:
					if (ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else if (ch == '-') {AddCh(); state = 102; break;}
					else {t.kind = 66; break loop;}
				case 129:
					if (ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 3; break;}
					else if (ch == 92) {AddCh(); state = 44; break;}
					else if (ch == '-') {AddCh(); state = 2; break;}
					else {t.kind = 54; break loop;}
				case 130:
					if (ch == '!') {AddCh(); state = 99; break;}
					else {t.kind = 41; break loop;}
				case 131:
					if (ch == '&') {AddCh(); state = 126; break;}
					else {t.kind = 44; break loop;}
				case 132:
					if (ch == '=') {AddCh(); state = 116; break;}
					else {t.kind = 46; break loop;}
				case 133:
					if (ch == '=') {AddCh(); state = 121; break;}
					else {t.kind = 55; break loop;}
				case 134:
					if (ch == '=') {AddCh(); state = 117; break;}
					else if (ch == '|') {AddCh(); state = 125; break;}
					else {t.kind = noSym; break loop;}
				case 135:
					if (ch == '=') {AddCh(); state = 120; break;}
					else {t.kind = 71; break loop;}
				case 136:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 137; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 137:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 60; break;}
					else if (ch >= ' ' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= '~' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else {t.kind = 74; break loop;}

			}
		}
		t.val = new String(tval, 0, tlen);
		return t;
	}
	
	// get the next token (possibly a token already seen during peeking)
	public Token Scan () {
		if (tokens.next == null) {
			return NextToken();
		} else {
			pt = tokens = tokens.next;
			return tokens;
		}
	}

	// get the next token, ignore pragmas
	public Token Peek () {
		do {
			if (pt.next == null) {
				pt.next = NextToken();
			}
			pt = pt.next;
		} while (pt.kind > maxT); // skip pragmas

		return pt;
	}

	// make sure that peeking starts at current scan position
	public void ResetPeek () { pt = tokens; }

} // end Scanner

