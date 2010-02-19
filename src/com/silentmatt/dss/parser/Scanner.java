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
	static final int maxT = 69;
	static final int noSym = 69;


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
		for (int i = 48; i <= 57; ++i) start.set(i, 36);
		start.set(92, 37); 
		start.set(45, 172); 
		start.set(34, 3); 
		start.set(39, 10); 
		start.set(117, 38); 
		start.set(60, 173); 
		start.set(64, 174); 
		start.set(123, 93); 
		start.set(125, 94); 
		start.set(58, 100); 
		start.set(59, 105); 
		start.set(62, 106); 
		start.set(38, 175); 
		start.set(43, 107); 
		start.set(126, 176); 
		start.set(44, 153); 
		start.set(42, 177); 
		start.set(35, 154); 
		start.set(46, 155); 
		start.set(91, 156); 
		start.set(61, 157); 
		start.set(124, 178); 
		start.set(36, 160); 
		start.set(94, 179); 
		start.set(93, 164); 
		start.set(40, 165); 
		start.set(41, 166); 
		start.set(33, 167); 
		start.set(47, 168); 
		start.set(85, 180); 
		start.set(37, 171); 
		start.set(Buffer.EOF, -1);
		literals.put("all", new Integer(8));
		literals.put("aural", new Integer(9));
		literals.put("braille", new Integer(10));
		literals.put("embossed", new Integer(11));
		literals.put("handheld", new Integer(12));
		literals.put("print", new Integer(13));
		literals.put("projection", new Integer(14));
		literals.put("screen", new Integer(15));
		literals.put("tty", new Integer(16));
		literals.put("tv", new Integer(17));
		literals.put("n", new Integer(18));
		literals.put("url", new Integer(19));
		literals.put("global", new Integer(34));
		literals.put("important", new Integer(57));
		literals.put("calc", new Integer(63));
		literals.put("const", new Integer(64));
		literals.put("param", new Integer(65));
		literals.put("prop", new Integer(66));

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
			buffer = new UTF8Buffer(buffer); col = 0;
			NextCh();
		}
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
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 2:
					if (ch >= '0' && ch <= '9') {AddCh(); state = 2; break;}
					else {t.kind = 3; break loop;}
				case 3:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 3; break;}
					else if (ch == '"') {AddCh(); state = 17; break;}
					else if (ch == 92) {AddCh(); state = 4; break;}
					else {t.kind = noSym; break loop;}
				case 4:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 42; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 9; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 3; break;}
					else if (ch == 'u') {AddCh(); state = 5; break;}
					else {t.kind = noSym; break loop;}
				case 5:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 6; break;}
					else if (ch == 'u') {AddCh(); state = 5; break;}
					else {t.kind = noSym; break loop;}
				case 6:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 7; break;}
					else {t.kind = noSym; break loop;}
				case 7:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 8; break;}
					else {t.kind = noSym; break loop;}
				case 8:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 3; break;}
					else {t.kind = noSym; break loop;}
				case 9:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 3; break;}
					else if (ch == '"') {AddCh(); state = 17; break;}
					else if (ch == 92) {AddCh(); state = 4; break;}
					else {t.kind = noSym; break loop;}
				case 10:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 10; break;}
					else if (ch == 39) {AddCh(); state = 17; break;}
					else if (ch == 92) {AddCh(); state = 11; break;}
					else {t.kind = noSym; break loop;}
				case 11:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 44; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 16; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 10; break;}
					else if (ch == 'u') {AddCh(); state = 12; break;}
					else {t.kind = noSym; break loop;}
				case 12:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 13; break;}
					else if (ch == 'u') {AddCh(); state = 12; break;}
					else {t.kind = noSym; break loop;}
				case 13:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 14; break;}
					else {t.kind = noSym; break loop;}
				case 14:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 15; break;}
					else {t.kind = noSym; break loop;}
				case 15:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 10; break;}
					else {t.kind = noSym; break loop;}
				case 16:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 10; break;}
					else if (ch == 39) {AddCh(); state = 17; break;}
					else if (ch == 92) {AddCh(); state = 11; break;}
					else {t.kind = noSym; break loop;}
				case 17:
					{t.kind = 4; break loop;}
				case 18:
					if (ch <= '!' || ch >= '#' && ch <= '&' || ch == '(' || ch >= '*' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == '"') {AddCh(); state = 46; break;}
					else if (ch == 39) {AddCh(); state = 47; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 19:
					if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 20:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 20; break;}
					else if (ch == '"') {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 21; break;}
					else {t.kind = noSym; break loop;}
				case 21:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 48; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 26; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 20; break;}
					else if (ch == 'u') {AddCh(); state = 22; break;}
					else {t.kind = noSym; break loop;}
				case 22:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 23; break;}
					else if (ch == 'u') {AddCh(); state = 22; break;}
					else {t.kind = noSym; break loop;}
				case 23:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 24; break;}
					else {t.kind = noSym; break loop;}
				case 24:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 25; break;}
					else {t.kind = noSym; break loop;}
				case 25:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 20; break;}
					else {t.kind = noSym; break loop;}
				case 26:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 20; break;}
					else if (ch == '"') {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 21; break;}
					else {t.kind = noSym; break loop;}
				case 27:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 27; break;}
					else if (ch == 39) {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 28; break;}
					else {t.kind = noSym; break loop;}
				case 28:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 50; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 33; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 27; break;}
					else if (ch == 'u') {AddCh(); state = 29; break;}
					else {t.kind = noSym; break loop;}
				case 29:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 30; break;}
					else if (ch == 'u') {AddCh(); state = 29; break;}
					else {t.kind = noSym; break loop;}
				case 30:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 31; break;}
					else {t.kind = noSym; break loop;}
				case 31:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 32; break;}
					else {t.kind = noSym; break loop;}
				case 32:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 27; break;}
					else {t.kind = noSym; break loop;}
				case 33:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 27; break;}
					else if (ch == 39) {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 28; break;}
					else {t.kind = noSym; break loop;}
				case 34:
					if (ch <= '(' || ch >= '*' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 35:
					{t.kind = 5; break loop;}
				case 36:
					if (ch >= '0' && ch <= '9') {AddCh(); state = 36; break;}
					else if (ch == '.') {AddCh(); state = 2; break;}
					else {t.kind = 2; break loop;}
				case 37:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 52; break;}
					else if (ch >= ' ' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= '~' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else {t.kind = noSym; break loop;}
				case 38:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'q' || ch >= 's' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else if (ch == 'r') {AddCh(); state = 53; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 39:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 54; break;}
					else if (ch >= ' ' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= '~' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else {t.kind = noSym; break loop;}
				case 40:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 55; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 41:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 56; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 42:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 3; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 43; break;}
					else if (ch == '"') {AddCh(); state = 17; break;}
					else if (ch == 92) {AddCh(); state = 4; break;}
					else {t.kind = noSym; break loop;}
				case 43:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 3; break;}
					else if (ch == '"') {AddCh(); state = 17; break;}
					else if (ch == 92) {AddCh(); state = 4; break;}
					else {t.kind = noSym; break loop;}
				case 44:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 10; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 45; break;}
					else if (ch == 39) {AddCh(); state = 17; break;}
					else if (ch == 92) {AddCh(); state = 11; break;}
					else {t.kind = noSym; break loop;}
				case 45:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 10; break;}
					else if (ch == 39) {AddCh(); state = 17; break;}
					else if (ch == 92) {AddCh(); state = 11; break;}
					else {t.kind = noSym; break loop;}
				case 46:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 46; break;}
					else if (ch == ')') {AddCh(); state = 57; break;}
					else if (ch == 10 || ch == 13) {AddCh(); state = 34; break;}
					else if (ch == '"') {AddCh(); state = 58; break;}
					else if (ch == 92) {AddCh(); state = 59; break;}
					else {t.kind = noSym; break loop;}
				case 47:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch == '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 47; break;}
					else if (ch == ')') {AddCh(); state = 60; break;}
					else if (ch == 10 || ch == 13) {AddCh(); state = 34; break;}
					else if (ch == 39) {AddCh(); state = 58; break;}
					else if (ch == 92) {AddCh(); state = 61; break;}
					else {t.kind = noSym; break loop;}
				case 48:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 20; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 49; break;}
					else if (ch == '"') {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 21; break;}
					else {t.kind = noSym; break loop;}
				case 49:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 20; break;}
					else if (ch == '"') {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 21; break;}
					else {t.kind = noSym; break loop;}
				case 50:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 27; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 51; break;}
					else if (ch == 39) {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 28; break;}
					else {t.kind = noSym; break loop;}
				case 51:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 27; break;}
					else if (ch == 39) {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 28; break;}
					else {t.kind = noSym; break loop;}
				case 52:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 41; break;}
					else if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 53:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'k' || ch >= 'm' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else if (ch == 'l') {AddCh(); state = 62; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 54:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 40; break;}
					else if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 55:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 63; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 56:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 64; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 57:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 20; break;}
					else if (ch == '"') {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 21; break;}
					else {t.kind = 5; break loop;}
				case 58:
					if (ch <= '(' || ch >= '*' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 59:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 65; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 66; break;}
					else if (ch <= 9 || ch >= 11 && ch <= '!' || ch >= '#' && ch <= '&' || ch == '(' || ch >= '*' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 'a' || ch >= 'c' && ch <= 'e' || ch >= 'g' && ch <= 'm' || ch >= 'o' && ch <= 'q' || ch == 's' || ch >= 'v' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 46; break;}
					else if (ch == 'u') {AddCh(); state = 67; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 60:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch >= '(' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 27; break;}
					else if (ch == 39) {AddCh(); state = 19; break;}
					else if (ch == 92) {AddCh(); state = 28; break;}
					else {t.kind = 5; break loop;}
				case 61:
					if (ch >= '0' && ch <= '3') {AddCh(); state = 68; break;}
					else if (ch >= '4' && ch <= '7') {AddCh(); state = 69; break;}
					else if (ch <= 9 || ch >= 11 && ch <= '!' || ch >= '#' && ch <= '&' || ch == '(' || ch >= '*' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 'a' || ch >= 'c' && ch <= 'e' || ch >= 'g' && ch <= 'm' || ch >= 'o' && ch <= 'q' || ch == 's' || ch >= 'v' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == 10 || ch == '"' || ch == 39 || ch == 92 || ch == 'b' || ch == 'f' || ch == 'n' || ch == 'r' || ch == 't') {AddCh(); state = 47; break;}
					else if (ch == 'u') {AddCh(); state = 70; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 62:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else if (ch == '(') {AddCh(); state = 18; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 63:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 71; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 64:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 72; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 65:
					if (ch == 10 || ch == 13) {AddCh(); state = 34; break;}
					else if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '(' || ch >= '*' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 46; break;}
					else if (ch == ')') {AddCh(); state = 57; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 73; break;}
					else if (ch == '"') {AddCh(); state = 58; break;}
					else if (ch == 92) {AddCh(); state = 59; break;}
					else {t.kind = noSym; break loop;}
				case 66:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 46; break;}
					else if (ch == ')') {AddCh(); state = 57; break;}
					else if (ch == 10 || ch == 13) {AddCh(); state = 34; break;}
					else if (ch == '"') {AddCh(); state = 58; break;}
					else if (ch == 92) {AddCh(); state = 59; break;}
					else {t.kind = noSym; break loop;}
				case 67:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 74; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 't' || ch >= 'v' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == 'u') {AddCh(); state = 67; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 68:
					if (ch == 10 || ch == 13) {AddCh(); state = 34; break;}
					else if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch == '(' || ch >= '*' && ch <= '/' || ch >= '8' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 47; break;}
					else if (ch == ')') {AddCh(); state = 60; break;}
					else if (ch >= '0' && ch <= '7') {AddCh(); state = 75; break;}
					else if (ch == 39) {AddCh(); state = 58; break;}
					else if (ch == 92) {AddCh(); state = 61; break;}
					else {t.kind = noSym; break loop;}
				case 69:
					if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch == '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 47; break;}
					else if (ch == ')') {AddCh(); state = 60; break;}
					else if (ch == 10 || ch == 13) {AddCh(); state = 34; break;}
					else if (ch == 39) {AddCh(); state = 58; break;}
					else if (ch == 92) {AddCh(); state = 61; break;}
					else {t.kind = noSym; break loop;}
				case 70:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 76; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 't' || ch >= 'v' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == 'u') {AddCh(); state = 70; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 71:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 77; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 72:
					if (ch == '-' || ch >= 'G' && ch <= 'Z' || ch == '_' || ch >= 'g' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 78; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 73:
					if (ch == 10 || ch == 13) {AddCh(); state = 34; break;}
					else if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '!' || ch >= '#' && ch <= '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 46; break;}
					else if (ch == ')') {AddCh(); state = 57; break;}
					else if (ch == '"') {AddCh(); state = 58; break;}
					else if (ch == 92) {AddCh(); state = 59; break;}
					else {t.kind = noSym; break loop;}
				case 74:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 79; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 75:
					if (ch == 10 || ch == 13) {AddCh(); state = 34; break;}
					else if (ch <= 9 || ch >= 11 && ch <= 12 || ch >= 14 && ch <= '&' || ch == '(' || ch >= '*' && ch <= '[' || ch >= ']' && ch <= 65535) {AddCh(); state = 47; break;}
					else if (ch == ')') {AddCh(); state = 60; break;}
					else if (ch == 39) {AddCh(); state = 58; break;}
					else if (ch == 92) {AddCh(); state = 61; break;}
					else {t.kind = noSym; break loop;}
				case 76:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 80; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 77:
					if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 78:
					if (ch >= 9 && ch <= 10 || ch >= 12 && ch <= 13 || ch == ' ' || ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 39; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 79:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 81; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 80:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 82; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 81:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 46; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 82:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 47; break;}
					else if (ch <= '(' || ch >= '*' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= 65535) {AddCh(); state = 34; break;}
					else if (ch == ')') {AddCh(); state = 35; break;}
					else {t.kind = noSym; break loop;}
				case 83:
					if (ch == '-') {AddCh(); state = 84; break;}
					else {t.kind = noSym; break loop;}
				case 84:
					if (ch == '-') {AddCh(); state = 85; break;}
					else {t.kind = noSym; break loop;}
				case 85:
					{t.kind = 6; break loop;}
				case 86:
					if (ch == '>') {AddCh(); state = 87; break;}
					else {t.kind = noSym; break loop;}
				case 87:
					{t.kind = 7; break loop;}
				case 88:
					if (ch == 'e') {AddCh(); state = 89; break;}
					else {t.kind = noSym; break loop;}
				case 89:
					if (ch == 'd') {AddCh(); state = 90; break;}
					else {t.kind = noSym; break loop;}
				case 90:
					if (ch == 'i') {AddCh(); state = 91; break;}
					else {t.kind = noSym; break loop;}
				case 91:
					if (ch == 'a') {AddCh(); state = 92; break;}
					else {t.kind = noSym; break loop;}
				case 92:
					{t.kind = 20; break loop;}
				case 93:
					{t.kind = 21; break loop;}
				case 94:
					{t.kind = 22; break loop;}
				case 95:
					{t.kind = 23; break loop;}
				case 96:
					if (ch == 'l') {AddCh(); state = 97; break;}
					else {t.kind = noSym; break loop;}
				case 97:
					if (ch == 's') {AddCh(); state = 98; break;}
					else {t.kind = noSym; break loop;}
				case 98:
					if (ch == 'e') {AddCh(); state = 99; break;}
					else {t.kind = noSym; break loop;}
				case 99:
					{t.kind = 24; break loop;}
				case 100:
					{t.kind = 25; break loop;}
				case 101:
					if (ch == 'a') {AddCh(); state = 102; break;}
					else {t.kind = noSym; break loop;}
				case 102:
					if (ch == 's') {AddCh(); state = 103; break;}
					else {t.kind = noSym; break loop;}
				case 103:
					if (ch == 's') {AddCh(); state = 104; break;}
					else {t.kind = noSym; break loop;}
				case 104:
					{t.kind = 26; break loop;}
				case 105:
					{t.kind = 28; break loop;}
				case 106:
					{t.kind = 29; break loop;}
				case 107:
					{t.kind = 31; break loop;}
				case 108:
					if (ch == 'e') {AddCh(); state = 109; break;}
					else {t.kind = noSym; break loop;}
				case 109:
					if (ch == 'f') {AddCh(); state = 110; break;}
					else {t.kind = noSym; break loop;}
				case 110:
					if (ch == 'i') {AddCh(); state = 111; break;}
					else {t.kind = noSym; break loop;}
				case 111:
					if (ch == 'n') {AddCh(); state = 112; break;}
					else {t.kind = noSym; break loop;}
				case 112:
					if (ch == 'e') {AddCh(); state = 113; break;}
					else {t.kind = noSym; break loop;}
				case 113:
					{t.kind = 33; break loop;}
				case 114:
					if (ch == 'o') {AddCh(); state = 115; break;}
					else {t.kind = noSym; break loop;}
				case 115:
					if (ch == 'n') {AddCh(); state = 116; break;}
					else {t.kind = noSym; break loop;}
				case 116:
					if (ch == 't') {AddCh(); state = 117; break;}
					else {t.kind = noSym; break loop;}
				case 117:
					if (ch == '-') {AddCh(); state = 118; break;}
					else {t.kind = noSym; break loop;}
				case 118:
					if (ch == 'f') {AddCh(); state = 119; break;}
					else {t.kind = noSym; break loop;}
				case 119:
					if (ch == 'a') {AddCh(); state = 120; break;}
					else {t.kind = noSym; break loop;}
				case 120:
					if (ch == 'c') {AddCh(); state = 121; break;}
					else {t.kind = noSym; break loop;}
				case 121:
					if (ch == 'e') {AddCh(); state = 122; break;}
					else {t.kind = noSym; break loop;}
				case 122:
					{t.kind = 35; break loop;}
				case 123:
					if (ch == 'a') {AddCh(); state = 124; break;}
					else {t.kind = noSym; break loop;}
				case 124:
					if (ch == 'g') {AddCh(); state = 125; break;}
					else {t.kind = noSym; break loop;}
				case 125:
					if (ch == 'e') {AddCh(); state = 126; break;}
					else {t.kind = noSym; break loop;}
				case 126:
					{t.kind = 36; break loop;}
				case 127:
					if (ch == 'p') {AddCh(); state = 128; break;}
					else {t.kind = noSym; break loop;}
				case 128:
					if (ch == 'o') {AddCh(); state = 129; break;}
					else {t.kind = noSym; break loop;}
				case 129:
					if (ch == 'r') {AddCh(); state = 130; break;}
					else {t.kind = noSym; break loop;}
				case 130:
					if (ch == 't') {AddCh(); state = 131; break;}
					else {t.kind = noSym; break loop;}
				case 131:
					{t.kind = 37; break loop;}
				case 132:
					if (ch == 'c') {AddCh(); state = 133; break;}
					else {t.kind = noSym; break loop;}
				case 133:
					if (ch == 'l') {AddCh(); state = 134; break;}
					else {t.kind = noSym; break loop;}
				case 134:
					if (ch == 'u') {AddCh(); state = 135; break;}
					else {t.kind = noSym; break loop;}
				case 135:
					if (ch == 'd') {AddCh(); state = 136; break;}
					else {t.kind = noSym; break loop;}
				case 136:
					if (ch == 'e') {AddCh(); state = 137; break;}
					else {t.kind = noSym; break loop;}
				case 137:
					{t.kind = 38; break loop;}
				case 138:
					if (ch == 'a') {AddCh(); state = 139; break;}
					else {t.kind = noSym; break loop;}
				case 139:
					if (ch == 'r') {AddCh(); state = 140; break;}
					else {t.kind = noSym; break loop;}
				case 140:
					if (ch == 's') {AddCh(); state = 141; break;}
					else {t.kind = noSym; break loop;}
				case 141:
					if (ch == 'e') {AddCh(); state = 142; break;}
					else {t.kind = noSym; break loop;}
				case 142:
					if (ch == 't') {AddCh(); state = 143; break;}
					else {t.kind = noSym; break loop;}
				case 143:
					{t.kind = 39; break loop;}
				case 144:
					if (ch == 'a') {AddCh(); state = 145; break;}
					else {t.kind = noSym; break loop;}
				case 145:
					if (ch == 'm') {AddCh(); state = 146; break;}
					else {t.kind = noSym; break loop;}
				case 146:
					if (ch == 'e') {AddCh(); state = 147; break;}
					else {t.kind = noSym; break loop;}
				case 147:
					if (ch == 's') {AddCh(); state = 148; break;}
					else {t.kind = noSym; break loop;}
				case 148:
					if (ch == 'p') {AddCh(); state = 149; break;}
					else {t.kind = noSym; break loop;}
				case 149:
					if (ch == 'a') {AddCh(); state = 150; break;}
					else {t.kind = noSym; break loop;}
				case 150:
					if (ch == 'c') {AddCh(); state = 151; break;}
					else {t.kind = noSym; break loop;}
				case 151:
					if (ch == 'e') {AddCh(); state = 152; break;}
					else {t.kind = noSym; break loop;}
				case 152:
					{t.kind = 40; break loop;}
				case 153:
					{t.kind = 42; break loop;}
				case 154:
					{t.kind = 44; break loop;}
				case 155:
					{t.kind = 45; break loop;}
				case 156:
					{t.kind = 46; break loop;}
				case 157:
					{t.kind = 47; break loop;}
				case 158:
					{t.kind = 48; break loop;}
				case 159:
					{t.kind = 49; break loop;}
				case 160:
					if (ch == '=') {AddCh(); state = 161; break;}
					else {t.kind = noSym; break loop;}
				case 161:
					{t.kind = 50; break loop;}
				case 162:
					{t.kind = 51; break loop;}
				case 163:
					{t.kind = 52; break loop;}
				case 164:
					{t.kind = 53; break loop;}
				case 165:
					{t.kind = 54; break loop;}
				case 166:
					{t.kind = 55; break loop;}
				case 167:
					{t.kind = 56; break loop;}
				case 168:
					{t.kind = 58; break loop;}
				case 169:
					{t.kind = 59; break loop;}
				case 170:
					{t.kind = 61; break loop;}
				case 171:
					{t.kind = 68; break loop;}
				case 172:
					if (ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 37; break;}
					else if (ch == '-') {AddCh(); state = 86; break;}
					else {t.kind = 62; break loop;}
				case 173:
					if (ch == '!') {AddCh(); state = 83; break;}
					else {t.kind = 27; break loop;}
				case 174:
					if (ch == 'm') {AddCh(); state = 88; break;}
					else if (ch == 'i') {AddCh(); state = 181; break;}
					else if (ch == 'e') {AddCh(); state = 96; break;}
					else if (ch == 'c') {AddCh(); state = 182; break;}
					else if (ch == 'd') {AddCh(); state = 108; break;}
					else if (ch == 'f') {AddCh(); state = 114; break;}
					else if (ch == 'p') {AddCh(); state = 123; break;}
					else if (ch == 'n') {AddCh(); state = 144; break;}
					else {t.kind = 41; break loop;}
				case 175:
					if (ch == '&') {AddCh(); state = 170; break;}
					else {t.kind = 30; break loop;}
				case 176:
					if (ch == '=') {AddCh(); state = 158; break;}
					else {t.kind = 32; break loop;}
				case 177:
					if (ch == '=') {AddCh(); state = 163; break;}
					else {t.kind = 43; break loop;}
				case 178:
					if (ch == '=') {AddCh(); state = 159; break;}
					else if (ch == '|') {AddCh(); state = 169; break;}
					else {t.kind = noSym; break loop;}
				case 179:
					if (ch == '=') {AddCh(); state = 162; break;}
					else {t.kind = 60; break loop;}
				case 180:
					if (ch == '-' || ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch >= 'a' && ch <= 'z' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else if (ch == 92) {AddCh(); state = 183; break;}
					else {t.kind = 1; t.val = new String(tval, 0, tlen); CheckLiteral(); return t;}
				case 181:
					if (ch == 'f') {AddCh(); state = 95; break;}
					else if (ch == 'm') {AddCh(); state = 127; break;}
					else if (ch == 'n') {AddCh(); state = 132; break;}
					else {t.kind = noSym; break loop;}
				case 182:
					if (ch == 'l') {AddCh(); state = 101; break;}
					else if (ch == 'h') {AddCh(); state = 138; break;}
					else {t.kind = noSym; break loop;}
				case 183:
					if (ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f') {AddCh(); state = 54; break;}
					else if (ch >= ' ' && ch <= '/' || ch >= ':' && ch <= '@' || ch >= 'G' && ch <= '`' || ch >= 'g' && ch <= '~' || ch >= 128 && ch <= 55295 || ch >= 57344 && ch <= 65533) {AddCh(); state = 1; break;}
					else {t.kind = 67; break loop;}

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

