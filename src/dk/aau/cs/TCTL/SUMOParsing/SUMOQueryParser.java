/* Generated By:JavaCC: Do not edit this line. SUMOQueryParser.java */
package dk.aau.cs.TCTL.SUMOParsing;

import java.io.StringReader;
import java.util.ArrayList;

import dk.aau.cs.TCTL.AritmeticOperator;
import dk.aau.cs.TCTL.TCTLPlusListNode;
import dk.aau.cs.TCTL.TCTLTermListNode;
import dk.aau.cs.TCTL.TCTLPlaceNode;
import dk.aau.cs.TCTL.TCTLConstNode;
import dk.aau.cs.TCTL.TCTLAFNode;
import dk.aau.cs.TCTL.TCTLAGNode;
import dk.aau.cs.TCTL.TCTLAbstractProperty;
import dk.aau.cs.TCTL.TCTLAbstractStateProperty;
import dk.aau.cs.TCTL.TCTLAndListNode;
import dk.aau.cs.TCTL.TCTLAtomicPropositionNode;
import dk.aau.cs.TCTL.TCTLEFNode;
import dk.aau.cs.TCTL.TCTLEGNode;
import dk.aau.cs.TCTL.TCTLFalseNode;
import dk.aau.cs.TCTL.TCTLNotNode;
import dk.aau.cs.TCTL.TCTLOrListNode;
import dk.aau.cs.TCTL.TCTLTrueNode;
import dk.aau.cs.TCTL.TCTLDeadlockNode;


public class SUMOQueryParser implements SUMOQueryParserConstants {

        private static final String ERROR_PARSING_QUERY_MESSAGE = "TAPAAL countered an error trying to parse the query";

        public static TCTLAbstractProperty parse(String query) throws ParseException {
                SUMOQueryParser parser = new SUMOQueryParser(new StringReader(query));
                return parser.AbstractProperty();
        }

/** Root production. */
  final public TCTLAbstractProperty AbstractProperty() throws ParseException {
        TCTLAbstractStateProperty child = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EF:
      jj_consume_token(EF);
      child = OrExpr();
                                       {if (true) return new TCTLEFNode(child);}
      break;
    case EG:
      jj_consume_token(EG);
      child = OrExpr();
                                         {if (true) return new TCTLEGNode(child);}
      break;
    case AF:
      jj_consume_token(AF);
      child = OrExpr();
                                         {if (true) return new TCTLAFNode(child);}
      break;
    case AG:
      jj_consume_token(AG);
      child = OrExpr();
                                         {if (true) return new TCTLAGNode(child);}
      break;
    case I:
      jj_consume_token(I);
      child = OrExpr();
                                        {if (true) return new TCTLAGNode(child);}
      break;
    case N:
      jj_consume_token(N);
      child = OrExpr();
                                        {if (true) return new TCTLAGNode(new TCTLNotNode(child));}
      break;
    case P:
      jj_consume_token(P);
      child = OrExpr();
                                        {if (true) return new TCTLEGNode(child);}
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public TCTLAbstractStateProperty OrExpr() throws ParseException {
        TCTLAbstractStateProperty currentChild;
        ArrayList<TCTLAbstractStateProperty> disjunctions = new ArrayList<TCTLAbstractStateProperty>();
    currentChild = AndExpr();
          disjunctions.add(currentChild);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
      jj_consume_token(OR);
      currentChild = AndExpr();
                  disjunctions.add(currentChild);
    }
          {if (true) return disjunctions.size() == 1 ? currentChild : new TCTLOrListNode(disjunctions);}
    throw new Error("Missing return statement in function");
  }

  final public TCTLAbstractStateProperty AndExpr() throws ParseException {
        TCTLAbstractStateProperty currentChild;
        ArrayList<TCTLAbstractStateProperty> conjunctions = new ArrayList<TCTLAbstractStateProperty>();
    currentChild = XorOrImpl();
          conjunctions.add(currentChild);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      jj_consume_token(AND);
      currentChild = XorOrImpl();
                  conjunctions.add(currentChild);
    }
          {if (true) return conjunctions.size() == 1 ? currentChild : new TCTLAndListNode(conjunctions);}
    throw new Error("Missing return statement in function");
  }

  final public TCTLAbstractStateProperty XorOrImpl() throws ParseException {
        TCTLAbstractStateProperty currentChild;
        TCTLAbstractStateProperty next;
        TCTLAbstractStateProperty first;
        TCTLAbstractStateProperty second;
        TCTLAbstractStateProperty firstOfSecond;
        TCTLAbstractStateProperty secondOfSecond;
    currentChild = NotExpr();
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case XOR:
      case IMPL:
      case BIIMPL:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_3;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case XOR:
        jj_consume_token(XOR);
        next = NotExpr();
                        //Convert to & and | 
                        first = new TCTLOrListNode(currentChild, next);
                        second = new TCTLAndListNode(currentChild, next);
                        second = new TCTLNotNode(second);
                        currentChild = new TCTLAndListNode(first, second);
        break;
      case IMPL:
        jj_consume_token(IMPL);
        next = NotExpr();
                        //Convert to & and |
                        first = new TCTLNotNode(currentChild);
                        currentChild = new TCTLOrListNode(first, next);
        break;
      case BIIMPL:
        jj_consume_token(BIIMPL);
        next = NotExpr();
                        //Convert to & and |
                        first = new TCTLAndListNode(currentChild, next);
                        firstOfSecond = new TCTLNotNode(currentChild);
                        secondOfSecond = new TCTLNotNode(next);
                        second = new TCTLAndListNode(firstOfSecond, secondOfSecond);
                        currentChild = new TCTLOrListNode(first, second);
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
          {if (true) return currentChild;}
    throw new Error("Missing return statement in function");
  }

  final public TCTLAbstractStateProperty NotExpr() throws ParseException {
        TCTLAbstractStateProperty child;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      jj_consume_token(NOT);
      jj_consume_token(27);
      child = OrExpr();
      jj_consume_token(28);
                                         {if (true) return new TCTLNotNode(child);}
      break;
    case TRUE:
    case FALSE:
    case DEADLOCK:
    case NUM:
    case 27:
    case 29:
      child = Factor();
                             {if (true) return child;}
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public TCTLAbstractStateProperty Factor() throws ParseException {
        TCTLAbstractStateProperty thisProp;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TRUE:
      jj_consume_token(TRUE);
                        thisProp = new TCTLTrueNode();
      break;
    case FALSE:
      jj_consume_token(FALSE);
                           thisProp = new TCTLFalseNode();
      break;
    case DEADLOCK:
      jj_consume_token(DEADLOCK);
                              thisProp = new TCTLDeadlockNode();
      break;
    default:
      jj_la1[6] = jj_gen;
      if (jj_2_1(2147483647)) {
        thisProp = AtomicProposition();
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 27:
          jj_consume_token(27);
          thisProp = OrExpr();
          jj_consume_token(28);
          break;
        default:
          jj_la1[7] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
          {if (true) return thisProp;}
    throw new Error("Missing return statement in function");
  }

  final public TCTLAbstractStateProperty AtomicProposition() throws ParseException {
        TCTLAbstractStateProperty left;
        TCTLAbstractStateProperty right;
        Token op;
    left = AtomicFactor();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OP:
      op = jj_consume_token(OP);
      right = AtomicFactor();
                                                          {if (true) return new TCTLAtomicPropositionNode(left, op.image, right);}
      break;
    case NOP:
      jj_consume_token(NOP);
      right = AtomicFactor();
                                               {if (true) return new TCTLNotNode(new TCTLAtomicPropositionNode(left, "=", right));}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public TCTLAbstractStateProperty AtomicFactor() throws ParseException {
        TCTLAbstractStateProperty thisProp;
        Token num;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 29:
      thisProp = PlaceList();
      break;
    case NUM:
      num = jj_consume_token(NUM);
                              thisProp = new TCTLConstNode(Integer.parseInt(num.image));
      break;
    case 27:
      jj_consume_token(27);
      thisProp = AtomicFactor();
      jj_consume_token(28);
                                                    {if (true) return thisProp;}
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
         {if (true) return thisProp;}
    throw new Error("Missing return statement in function");
  }

  final public TCTLAbstractStateProperty PlaceList() throws ParseException {
        TCTLAbstractStateProperty currentChild;
        ArrayList<TCTLAbstractStateProperty> places = new ArrayList<TCTLAbstractStateProperty>();
        Token place;
    jj_consume_token(29);
    jj_consume_token(30);
    place = jj_consume_token(IDENT);
    jj_consume_token(30);
                                               currentChild = new TCTLPlaceNode(place.image); places.add(currentChild);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 31:
        ;
        break;
      default:
        jj_la1[10] = jj_gen;
        break label_4;
      }
      jj_consume_token(31);
      jj_consume_token(30);
      place = jj_consume_token(IDENT);
      jj_consume_token(30);
                        places.add(new AritmeticOperator("+"));
                        currentChild = new TCTLPlaceNode(place.image);
                        places.add(currentChild);
    }
    jj_consume_token(28);
          {if (true) return places.size() == 1 ? currentChild : new TCTLPlusListNode(places);}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_3R_6() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_9()) {
    jj_scanpos = xsp;
    if (jj_3R_10()) {
    jj_scanpos = xsp;
    if (jj_3R_11()) return true;
    }
    }
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_scan_token(NOP)) return true;
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_scan_token(OP)) return true;
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3R_12() {
    if (jj_scan_token(29)) return true;
    if (jj_scan_token(30)) return true;
    if (jj_scan_token(IDENT)) return true;
    if (jj_scan_token(30)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_13()) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(28)) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_5()) return true;
    return false;
  }

  private boolean jj_3R_11() {
    if (jj_scan_token(27)) return true;
    if (jj_3R_6()) return true;
    if (jj_scan_token(28)) return true;
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(NUM)) return true;
    return false;
  }

  private boolean jj_3R_9() {
    if (jj_3R_12()) return true;
    return false;
  }

  private boolean jj_3R_5() {
    if (jj_3R_6()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_7()) {
    jj_scanpos = xsp;
    if (jj_3R_8()) return true;
    }
    return false;
  }

  private boolean jj_3R_13() {
    if (jj_scan_token(31)) return true;
    if (jj_scan_token(30)) return true;
    if (jj_scan_token(IDENT)) return true;
    if (jj_scan_token(30)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public SUMOQueryParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[11];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x7f0,0x800,0x1000,0x1c000,0x1c000,0x2808200e,0xe,0x8000000,0x600000,0x28080000,0x80000000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public SUMOQueryParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public SUMOQueryParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SUMOQueryParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public SUMOQueryParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SUMOQueryParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public SUMOQueryParser(SUMOQueryParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(SUMOQueryParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[32];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 11; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 32; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
